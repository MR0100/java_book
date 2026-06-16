#!/usr/bin/env bash
# audio/build.sh — Incremental audio-course builder (macOS / Linux).
# Windows users: use build.ps1, or run `python audio\tools\build.py` directly.
#
# Defaults reproduce the PUBLISHED course exactly: engine=edge, voice=en-GB-RyanNeural.
# No machine-specific setup required — see audio/README.md for prerequisites.
#
# Usage:
#   ./audio/build.sh                 # build / refresh the whole course
#   ./audio/build.sh L0/C01          # scope to one chapter
#   ./audio/build.sh L4              # one module
#   ./audio/build.sh --force L0/C01  # regenerate even if unchanged
#   ./audio/build.sh --diff L0       # dry-run; show what would build
#
# Env overrides (rarely needed — the defaults are the locked course config):
#   ENGINE=piper ./audio/build.sh        # offline engine (needs `piper` + models)
#   VOICE=en-GB-SoniaNeural ./audio/build.sh
#   RATE=-5 ./audio/build.sh             # edge: percent speed offset
#   PYTHON=python3.12 ./audio/build.sh   # pick a specific interpreter
#
# Incremental by default: skips files whose SHA-256 hash + engine + voice + rate
# match the manifest and whose MP3 exists. Edit one .md, re-run, only it rebuilds.

set -euo pipefail

HERE="$(cd "$(dirname "$0")" && pwd)"
PY="${PYTHON:-python3}"
ENGINE="${ENGINE:-edge}"

ARGS=(--engine "$ENGINE")
[ -n "${VOICE:-}" ] && ARGS+=(--voice "$VOICE")
[ -n "${RATE:-}" ]  && ARGS+=(--rate "$RATE")

exec "$PY" "$HERE/build.py" "${ARGS[@]}" "$@"
