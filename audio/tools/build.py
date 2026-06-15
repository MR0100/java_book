#!/usr/bin/env python3
"""Incremental audio course builder.

Walks the `content/` tree, finds topic markdown files, and for each:

1. Computes SHA-256 of the source file.
2. Compares against `audio/manifest.json` (or regenerates if hash unknown / mp3 missing).
3. Preprocesses markdown → speech-friendly text (preprocess.py).
4. Runs macOS `say` to produce an AIFF.
5. Converts AIFF → MP3 with ffmpeg.
6. Updates the manifest with the new hash, output path, voice, rate, duration.

Default behavior is incremental — unchanged topics are skipped instantly.
Use `--force` to regenerate everything.

Run via the shell wrapper (`audio/build.sh`) which sets sensible defaults and
locates this script. Direct invocation works too:

    python3 audio/tools/build.py L0/C01            # scope to one chapter
    python3 audio/tools/build.py L0                # one module
    python3 audio/tools/build.py                   # whole course
    python3 audio/tools/build.py --force L0/C01    # regenerate
    python3 audio/tools/build.py --diff L0/C01     # dry-run, list what would change
"""
from __future__ import annotations

import argparse
import hashlib
import importlib.util
import json
import re
import shutil
import subprocess
import sys
import tempfile
import time
from pathlib import Path

THIS = Path(__file__).resolve()
AUDIO_DIR = THIS.parent.parent
PROJECT_ROOT = AUDIO_DIR.parent
CONTENT_DIR = PROJECT_ROOT / "content"
OUT_DIR = AUDIO_DIR / "out"
MANIFEST = AUDIO_DIR / "manifest.json"
PREPROCESS = THIS.parent / "preprocess.py"
PIPER_VOICES_DIR = AUDIO_DIR / "voices" / "piper"

# ── LOCKED COURSE AUDIO CONFIG ───────────────────────────────────────────────
# The official course narration is generated with the `edge` engine and the
# `en-GB-RyanNeural` voice. These are the defaults below, so running the build
# with NO arguments reproduces the course identically on any machine
# (Windows / macOS / Linux). Do not change these if you want regenerated audio
# to match the published narration. See audio/README.md.
DEFAULT_ENGINE = "edge"

# Default voice per engine.
ENGINE_DEFAULT_VOICE = {
    "say": "Samantha",
    "piper": "en_US-lessac-medium",
    "edge": "en-GB-RyanNeural",  # ← locked course voice
}

SLNC_RE = re.compile(r"\[\[slnc\s+\d+\]\]")


def sha256_of(path: Path) -> str:
    h = hashlib.sha256()
    with path.open("rb") as f:
        for chunk in iter(lambda: f.read(65536), b""):
            h.update(chunk)
    return h.hexdigest()


def load_manifest() -> dict:
    if MANIFEST.exists():
        try:
            return json.loads(MANIFEST.read_text())
        except json.JSONDecodeError:
            return {}
    return {}


def save_manifest(manifest: dict) -> None:
    MANIFEST.parent.mkdir(parents=True, exist_ok=True)
    MANIFEST.write_text(json.dumps(manifest, indent=2, sort_keys=True) + "\n")


def resolve_scope(scope: str) -> Path:
    """Resolve user-friendly scopes like 'L0', 'L0/C01' to actual folders.

    Real folder names are 'L0-foundations', 'C01-cs-foundations', etc.
    We match by prefix so 'L0' picks 'L0-foundations', 'L0/C01' picks
    'L0-foundations/C01-cs-foundations'. Exact paths still work.
    """
    if not scope or scope == ".":
        return CONTENT_DIR
    parts = [p for p in scope.split("/") if p]
    current = CONTENT_DIR
    for part in parts:
        # Exact match first
        candidate = current / part
        if candidate.exists():
            current = candidate
            continue
        # Prefix match (e.g. 'L0' → 'L0-foundations')
        matches = sorted(p for p in current.iterdir() if p.is_dir() and p.name.startswith(part))
        if len(matches) == 1:
            current = matches[0]
        elif len(matches) == 0:
            sys.exit(f"Scope not found: {scope} (no folder starting with '{part}' under {current})")
        else:
            names = ", ".join(p.name for p in matches)
            sys.exit(f"Ambiguous scope '{part}' under {current}: matches {names}")
    return current


def find_target_files(scope: str) -> list[Path]:
    """Return ordered list of markdown files matching the scope.

    Scope can be a folder (under content/) or a specific .md file path.
    """
    # Allow a direct .md path (relative to project root, content/, or absolute)
    candidates = [Path(scope), CONTENT_DIR / scope, PROJECT_ROOT / scope]
    for c in candidates:
        if c.is_file() and c.suffix == ".md":
            return [c.resolve()]
    base = resolve_scope(scope)
    files = sorted(p for p in base.rglob("*.md"))
    # Filter to topic + README files; ignore generated indices at module root.
    return [p for p in files if p.name == "README.md" or p.name.startswith("T")]


def relative_key(md_path: Path) -> str:
    return str(md_path.relative_to(CONTENT_DIR))


def output_path_for(md_path: Path) -> Path:
    rel = md_path.relative_to(CONTENT_DIR)
    return OUT_DIR / rel.with_suffix(".mp3")


def probe_duration(mp3: Path) -> float | None:
    try:
        result = subprocess.run(
            [
                "ffprobe",
                "-v",
                "error",
                "-show_entries",
                "format=duration",
                "-of",
                "default=noprint_wrappers=1:nokey=1",
                str(mp3),
            ],
            check=True,
            capture_output=True,
            text=True,
        )
        return float(result.stdout.strip())
    except (subprocess.CalledProcessError, ValueError):
        return None


def humanize(seconds: float) -> str:
    if seconds < 60:
        return f"{seconds:.0f}s"
    if seconds < 3600:
        m, s = divmod(seconds, 60)
        return f"{int(m)}m{int(s):02d}s"
    h, rem = divmod(seconds, 3600)
    m, _ = divmod(rem, 60)
    return f"{int(h)}h{int(m):02d}m"


def aiff_to_mp3(aiff_path: Path, out_mp3: Path) -> tuple[bool, str | None]:
    ff = subprocess.run(
        ["ffmpeg", "-y", "-hide_banner", "-loglevel", "error",
         "-i", str(aiff_path), "-codec:a", "libmp3lame", "-qscale:a", "2",
         str(out_mp3)],
        check=False, capture_output=True, text=True)
    return (ff.returncode == 0, None if ff.returncode == 0 else ff.stderr.strip())


def wav_to_mp3(wav_path: Path, out_mp3: Path) -> tuple[bool, str | None]:
    ff = subprocess.run(
        ["ffmpeg", "-y", "-hide_banner", "-loglevel", "error",
         "-i", str(wav_path), "-codec:a", "libmp3lame", "-qscale:a", "2",
         str(out_mp3)],
        check=False, capture_output=True, text=True)
    return (ff.returncode == 0, None if ff.returncode == 0 else ff.stderr.strip())


def synth_say(text_path: Path, out_mp3: Path, voice: str, rate: int) -> tuple[bool, str | None]:
    aiff = Path(tempfile.mkstemp(suffix=".aiff")[1])
    try:
        r = subprocess.run(
            ["say", "-v", voice, "-r", str(rate), "-o", str(aiff), "-f", str(text_path)],
            check=False, capture_output=True, text=True)
        if r.returncode != 0:
            return False, f"say failed: {r.stderr.strip() or 'unknown error'}"
        return aiff_to_mp3(aiff, out_mp3)
    finally:
        aiff.unlink(missing_ok=True)


def synth_piper(text_path: Path, out_mp3: Path, voice: str, rate: int) -> tuple[bool, str | None]:
    model = PIPER_VOICES_DIR / f"{voice}.onnx"
    if not model.exists():
        return False, f"piper voice model not found: {model} (expected at {PIPER_VOICES_DIR}/)"
    wav = Path(tempfile.mkstemp(suffix=".wav")[1])
    try:
        with text_path.open("r") as fin:
            r = subprocess.run(
                ["piper", "--model", str(model), "--output_file", str(wav)],
                stdin=fin, check=False, capture_output=True, text=True)
        if r.returncode != 0:
            return False, f"piper failed: {r.stderr.strip() or 'unknown error'}"
        return wav_to_mp3(wav, out_mp3)
    finally:
        wav.unlink(missing_ok=True)


def synth_edge(text_path: Path, out_mp3: Path, voice: str, rate: int) -> tuple[bool, str | None]:
    # Edge TTS writes MP3 directly. Rate is a percent adjustment from voice default.
    # Invoke as a Python module (`python -m edge_tts`) rather than the `edge-tts`
    # console script: this works identically on Windows/macOS/Linux without the
    # pip script-bin needing to be on PATH (the #1 cross-platform footgun).
    args = [sys.executable, "-m", "edge_tts",
            "--voice", voice, "--file", str(text_path), "--write-media", str(out_mp3)]
    if rate != 0:
        args.extend(["--rate", f"{rate:+d}%"])
    r = subprocess.run(args, check=False, capture_output=True, text=True)
    if r.returncode != 0:
        return False, f"edge-tts failed: {r.stderr.strip() or 'unknown error'}"
    if not out_mp3.exists() or out_mp3.stat().st_size == 0:
        return False, "edge-tts produced empty output"
    return True, None


SYNTHESIZERS = {"say": synth_say, "piper": synth_piper, "edge": synth_edge}


def adapt_text_for_engine(raw_text: str, engine: str) -> str:
    """Engine-specific transforms on preprocessed text.

    `say` keeps the [[slnc N]] pause markers (it understands them).
    `piper` / `edge` strip them and let the neural model's natural pacing carry
    pauses via the surrounding punctuation.
    """
    if engine == "say":
        return raw_text
    # Replace [[slnc N]] with ". " so the engine inserts a natural pause from punctuation.
    return SLNC_RE.sub(". ", raw_text)


def generate_one(
    md_path: Path,
    out_mp3: Path,
    engine: str,
    voice: str,
    rate: int,
    quiet: bool,
) -> tuple[bool, float | None, str | None]:
    """Generate a single MP3. Return (ok, duration_seconds, error)."""
    out_mp3.parent.mkdir(parents=True, exist_ok=True)
    text_path = Path(tempfile.mkstemp(suffix=".txt")[1])
    try:
        # 1. preprocess (engine-agnostic markdown → speech-friendly text)
        pre = subprocess.run(
            [sys.executable, str(PREPROCESS), str(md_path), "-o", str(text_path)],
            check=False, capture_output=True, text=True)
        if pre.returncode != 0:
            return False, None, f"preprocess failed: {pre.stderr.strip()}"

        # 2. adapt for engine (strip slnc markers if engine doesn't use them)
        adapted = adapt_text_for_engine(text_path.read_text(encoding="utf-8"), engine)
        text_path.write_text(adapted, encoding="utf-8")

        # 3. synthesize
        synth = SYNTHESIZERS.get(engine)
        if synth is None:
            return False, None, f"unknown engine: {engine}"
        ok, err = synth(text_path, out_mp3, voice, rate)
        if not ok:
            return False, None, err

        return True, probe_duration(out_mp3), None
    finally:
        text_path.unlink(missing_ok=True)


def main() -> int:
    parser = argparse.ArgumentParser(description="Incremental audio course builder.")
    parser.add_argument("scope", nargs="?", default=".", help="Scope under content/ (e.g. L0/C01, L0, .)")
    parser.add_argument("--engine", default=DEFAULT_ENGINE, choices=sorted(SYNTHESIZERS.keys()),
                        help="TTS engine (say|piper|edge). Default: edge (cross-platform; locked course voice).")
    parser.add_argument("--voice", default=None,
                        help="Voice name (engine-specific). Defaults vary per engine.")
    parser.add_argument("--rate", type=int, default=None,
                        help="Rate. For say: wpm (default 190). For edge: percent offset (default 0). Ignored by piper.")
    parser.add_argument("--force", action="store_true", help="Regenerate even if hash matches")
    parser.add_argument("--diff", action="store_true", help="List what would regenerate; do not generate")
    parser.add_argument("--quiet", action="store_true", help="Less progress output")
    args = parser.parse_args()

    engine = args.engine
    voice = args.voice or ENGINE_DEFAULT_VOICE[engine]
    if args.rate is None:
        rate = 190 if engine == "say" else 0
    else:
        rate = args.rate

    # Per-engine required tools (ffmpeg/ffprobe always; the engine tool varies).
    # edge is checked as an importable Python module (not a PATH binary) so the
    # check matches how synth_edge invokes it — portable across OSes.
    missing = [t for t in ("ffmpeg", "ffprobe") if shutil.which(t) is None]
    if engine == "say" and shutil.which("say") is None:
        missing.append("say (macOS only — use --engine edge on Windows/Linux)")
    elif engine == "piper" and shutil.which("piper") is None:
        missing.append("piper")
    elif engine == "edge" and importlib.util.find_spec("edge_tts") is None:
        missing.append("edge-tts (install with: pip install edge-tts)")
    if missing:
        sys.exit(f"Required tool(s) not available: {', '.join(missing)}")

    manifest = load_manifest()
    targets = find_target_files(args.scope)
    if not targets:
        print(f"No markdown files found under scope: {args.scope}")
        return 0

    plan: list[tuple[Path, Path, str]] = []
    skipped = 0
    for md in targets:
        key = relative_key(md)
        out_mp3 = output_path_for(md)
        new_hash = sha256_of(md)
        entry = manifest.get(key, {})
        cached_hash = entry.get("hash")
        if (
            not args.force
            and cached_hash == new_hash
            and out_mp3.exists()
            and entry.get("engine") == engine
            and entry.get("voice") == voice
            and entry.get("rate") == rate
        ):
            skipped += 1
            continue
        plan.append((md, out_mp3, new_hash))

    print(f"Scope: {args.scope}   Engine: {engine}/{voice} @ rate={rate}")
    print(f"Targets: {len(targets)}   Up-to-date: {skipped}   To build: {len(plan)}")
    if args.diff or not plan:
        for md, _, _ in plan:
            print(f"  build: {relative_key(md)}")
        return 0

    total_seconds = 0.0
    failures: list[tuple[str, str]] = []
    started = time.time()

    for idx, (md, out_mp3, new_hash) in enumerate(plan, start=1):
        rel = relative_key(md)
        t0 = time.time()
        print(f"[{idx}/{len(plan)}] {rel}", flush=True)
        ok, duration, err = generate_one(md, out_mp3, engine, voice, rate, args.quiet)
        elapsed = time.time() - t0
        if not ok:
            failures.append((rel, err or "unknown"))
            print(f"        FAILED in {humanize(elapsed)}: {err}", flush=True)
            continue
        total_seconds += duration or 0
        manifest[rel] = {
            "hash": new_hash,
            "mp3": str(out_mp3.relative_to(PROJECT_ROOT)),
            "engine": engine,
            "voice": voice,
            "rate": rate,
            "duration_seconds": round(duration or 0, 2),
            "generated_at": time.strftime("%Y-%m-%dT%H:%M:%SZ", time.gmtime()),
        }
        save_manifest(manifest)
        print(f"        {humanize(duration or 0)} audio in {humanize(elapsed)}", flush=True)

    wall = time.time() - started
    print()
    print(f"Done. Generated {len(plan) - len(failures)} files ({humanize(total_seconds)} audio) in {humanize(wall)}.")
    if failures:
        print(f"Failures: {len(failures)}")
        for rel, err in failures:
            print(f"  {rel}: {err}")
        return 1
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
