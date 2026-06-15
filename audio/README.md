# Audio Course

MP3 narration of **every topic in `content/`**, generated from the source markdown.
The audio is **not stored in the repo** — it is *generated on demand* and is
fully reproducible on **Windows, macOS, or Linux** with no machine-specific changes.

> [!IMPORTANT]
> **Locked configuration (do not change to match the published course).**
> The official narration uses the **`edge`** engine (Microsoft Edge Neural TTS,
> via the `edge-tts` Python package) and the voice **`en-GB-RyanNeural`** at the
> default rate. These are the built-in defaults in `tools/build.py`, so running
> the builder with **no arguments** reproduces the course identically on any OS.

## Prerequisites (all platforms)

| Tool | Why | Install |
|---|---|---|
| **Python 3.9+** | runs the builder | [python.org](https://www.python.org/downloads/) (on Windows tick *"Add Python to PATH"*) |
| **ffmpeg + ffprobe** | probe/encode audio | macOS: `brew install ffmpeg` · Windows: `winget install Gyan.FFmpeg` · Linux: `sudo apt install ffmpeg` |
| **edge-tts** | the locked TTS engine | `pip install edge-tts` (any OS) |
| **Internet** | `edge` is a cloud voice | required while generating |

> [!NOTE]
> `edge-tts` is invoked as a Python module (`python -m edge_tts`), so it works
> even when pip's script folder isn't on your `PATH` — the usual Windows/macOS
> footgun. You only need `pip install edge-tts` for the *same* interpreter you
> run the build with.

## Quick start

**macOS / Linux:**
```bash
./audio/build.sh                 # build / refresh the WHOLE course
./audio/build.sh L0/C01          # just one chapter
./audio/build.sh L4              # one module
./audio/build.sh --diff L0       # dry-run: list what would build
./audio/build.sh --force L0/C01  # force-regenerate
```

**Windows (PowerShell):**
```powershell
.\audio\build.ps1                 # whole course
.\audio\build.ps1 L0/C01          # one chapter
.\audio\build.ps1 --diff L0       # dry-run
.\audio\build.ps1 --force L0/C01  # force-regenerate
```

**Any OS (direct — no wrapper):**
```bash
python audio/tools/build.py                # whole course (edge / en-GB-RyanNeural)
python audio/tools/build.py L0/C01         # one chapter
python audio/tools/build.py --diff L4      # dry-run
```

Scope shortcuts (`L0`, `L0/C01`, full folder names, or a single `.md` path) all work.

## How it works

```
content/<module>/<chapter>/<topic>.md
    │
    ▼  tools/preprocess.py   strip markdown → speech-friendly text
    │                        (code/tables/diagrams → "see the written notes")
    ▼  python -m edge_tts    text → MP3 (voice en-GB-RyanNeural)   [cross-platform]
    │
    ▼  ffmpeg / ffprobe      validate + measure duration
    │
    ▼
audio/out/<module>/<chapter>/<topic>.mp3
```

**Incremental.** Every source `.md` is hashed (SHA-256) and recorded in
`manifest.json` with its engine, voice, rate, and duration. A re-run skips any
file whose hash + engine + voice + rate match and whose MP3 already exists. Edit
one topic, rebuild — only that topic regenerates. Use `--force` to rebuild anyway.

## Regenerating from scratch (e.g. a fresh PC)

The MP3s, voice models, samples, and `manifest.json` are **gitignored and not
shipped**. On a new machine:

```bash
pip install edge-tts          # + ensure ffmpeg/ffprobe are installed
./audio/build.sh              # macOS/Linux   (or  .\audio\build.ps1  on Windows)
```

The whole course (~500 topics, ~170 hours of audio) takes roughly 1–2 hours of
wall-clock and lands in `audio/out/`. Storage is a few GB; it stays out of git.

## Offline alternative: the `piper` engine

`edge` needs internet. For fully **offline** generation, use [Piper](https://github.com/rhasspy/piper)
(local neural TTS). It needs the `piper` binary on `PATH` and a voice model:

```bash
# 1. install piper (see its releases), then download a voice model into audio/voices/piper/
#    e.g. en_US-lessac-medium.onnx (+ .onnx.json) from the Piper voices repo
# 2. build with the piper engine
ENGINE=piper ./audio/build.sh L0/C01          # macOS/Linux
$env:ENGINE="piper"; .\audio\build.ps1 L0/C01 # Windows
```

> Piper output won't match the published `edge/en-GB-RyanNeural` narration — it's
> a different voice. Use it only when you need offline generation.

## Ad-hoc one-off narration (cross-platform)

To narrate a snippet without the build pipeline, call edge-tts directly — works
on any OS:

```bash
python -m edge_tts --voice en-GB-RyanNeural --text "Some text to narrate." --write-media out.mp3
python -m edge_tts --list-voices            # browse all available voices
```

## Markdown handling rules

The preprocessor turns markdown into clean speech:

- **Frontmatter** — skipped (only `title:` is used for the spoken intro).
- **Code fences** — replaced with "Code example. See the written notes." (reading 50 lines of Java aloud helps no one).
- **Tables** — "Reference table. See the written notes."
- **ASCII / Mermaid diagrams** — "Diagram. See the written notes."
- **Headings** — spoken with a pause sized to the level.
- **Callouts** (`> [!WARNING]`) — read as "Warning. …".
- **Links** `[text](url)` — reduced to "text". **Inline code / bold / italic** — markup stripped, text kept. **Emojis** — stripped.

## Files

| Path | Purpose | In git? |
|---|---|---|
| `build.sh` | macOS/Linux entry point | ✅ |
| `build.ps1` | Windows (PowerShell) entry point | ✅ |
| `tools/build.py` | orchestrator: walk content, hash, generate, update manifest | ✅ |
| `tools/preprocess.py` | markdown → speech-friendly text | ✅ |
| `.gitignore` | keeps generated audio out of git | ✅ |
| `manifest.json` | incremental cache (hash → mp3 metadata) | ❌ generated |
| `out/` | generated MP3 tree (mirrors `content/`) | ❌ generated |
| `voices/` | downloaded Piper models (offline engine only) | ❌ downloaded |
| `samples/` | voice-comparison clips | ❌ optional |

`manifest.json`, `out/`, `voices/`, and `samples/` are generated/downloaded
artifacts — safe to delete; a build reconstructs what it needs.

## Changing the voice (optional)

To preview the course in a different voice without editing the lock, override at
run time (this won't match the published narration):

```bash
VOICE=en-US-GuyNeural ./audio/build.sh L0/C01      # macOS/Linux
$env:VOICE="en-US-GuyNeural"; .\audio\build.ps1 L0/C01   # Windows
```

Run `python -m edge_tts --list-voices` for the full catalogue. To change the
*locked* default for everyone, edit `ENGINE_DEFAULT_VOICE["edge"]` and
`DEFAULT_ENGINE` in `tools/build.py`.
