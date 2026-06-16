# scripts/audio — Audio narration generator

Generates MP3 narration of every topic in `content/` into the repo-root `audio/`
folder (auto-created, git-ignored). Locked, cross-platform config: engine `edge`,
voice `en-GB-RyanNeural`.

**Full instructions — prerequisites, commands, Windows/macOS/Linux, the offline
option — are in [../../GUIDE.md](../../GUIDE.md) §5.**

Quick start:

```bash
pip install edge-tts          # + ensure ffmpeg/ffprobe are installed
./build.sh                    # macOS/Linux   (Windows:  .\build.ps1)
python build.py --diff        # dry-run
```

- `build.py` — the orchestrator (walk content, hash, synthesize, update manifest).
- `preprocess.py` — markdown → speech-friendly text.
- `build.sh` / `build.ps1` — thin wrappers (set the locked defaults, call `build.py`).
