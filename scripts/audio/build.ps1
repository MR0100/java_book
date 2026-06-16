<#
.SYNOPSIS
  audio/build.ps1 — Incremental audio-course builder for Windows (PowerShell).
  Mirrors build.sh. macOS / Linux users: use build.sh.

.DESCRIPTION
  Defaults reproduce the PUBLISHED course exactly: engine=edge, voice=en-GB-RyanNeural.

  Prerequisites (see audio/README.md):
    - Python 3.9+            (https://www.python.org/  — tick "Add to PATH")
    - ffmpeg + ffprobe       (https://ffmpeg.org/ or `winget install Gyan.FFmpeg`)
    - pip install edge-tts   (the locked TTS engine; needs internet)

.EXAMPLE
  .\audio\build.ps1                 # build / refresh the whole course
  .\audio\build.ps1 L0/C01          # scope to one chapter
  .\audio\build.ps1 L4              # one module
  .\audio\build.ps1 --force L0/C01  # regenerate even if unchanged
  .\audio\build.ps1 --diff L0       # dry-run; show what would build

.NOTES
  Env overrides: $env:ENGINE (default edge), $env:VOICE, $env:RATE, $env:PYTHON (default "python").
#>

$ErrorActionPreference = "Stop"
$here   = Split-Path -Parent $MyInvocation.MyCommand.Path
$py     = if ($env:PYTHON) { $env:PYTHON } else { "python" }
$engine = if ($env:ENGINE) { $env:ENGINE } else { "edge" }

$buildArgs = @("--engine", $engine)
if ($env:VOICE) { $buildArgs += @("--voice", $env:VOICE) }
if ($env:RATE)  { $buildArgs += @("--rate",  $env:RATE)  }

# All remaining CLI arguments (scope, --force, --diff, …) are forwarded via $args.
& $py (Join-Path $here "build.py") @buildArgs @args
exit $LASTEXITCODE
