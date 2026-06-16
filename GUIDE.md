# GUIDE — Using the Java 0-to-Hero Course

Everything you need to **read, build, and maintain** this course. For a one-line
intro see [README.md](README.md); for the rules on editing content see
[CLAUDE.md](CLAUDE.md).

---

## 1. What this project is

A complete, self-paced book that takes you from absolute beginner to
senior/lead Java backend engineer, plus interview mastery — **seven modules
(L0 → L6)**, every topic authored and depth-checked. It ships in three forms,
all generated from the same Markdown source:

- **The Markdown book** — `content/` (the source of truth; read it in any editor or on GitHub).
- **The web reader ("dochub")** — a self-contained static HTML site you generate locally.
- **The audio course** — MP3 narration of every topic, generated on demand.

Plus **`examples/`** — 21 runnable companion projects.

---

## 2. Folder structure

```
java-learning/
├── README.md            short intro + entry points
├── GUIDE.md             this file
├── CLAUDE.md            rules for adding/editing content
│
├── content/             THE BOOK — hand-written Markdown (the source of truth)
│   ├── L0-foundations/ … L6-interview-mastery/
│   │   └── C##-chapter/                  ← chapters
│   │       ├── README.md                 ← chapter index
│   │       └── T##-topic.md              ← one file per topic
│   └── (every chapter also has cross-cutting sections: cheatsheets, qa-faq, …)
│
├── examples/            runnable companion code (Maven projects + k8s manifests)
│   ├── starter-templates/  system-designs/  labs/  k8s-manifests/
│   └── README.md           maps each project → the topic it "Backs:"
│
├── docs/                reference material
│   ├── CONTENTS.md         master index of every module/chapter/topic
│   ├── GLOSSARY.md         term definitions
│   ├── ACRONYMS.md         acronym lookups
│   └── LEARNING-PATHS.md   suggested study tracks (by level / goal)
│
├── templates/           authoring guidelines + copy-paste templates
│   ├── CONVENTIONS.md      formatting rules (mandatory)
│   ├── DEPTH-CHECKLIST.md  depth/quality bar (mandatory)
│   └── *-template.md       topic / interview-qa / module-readme starters
│
├── scripts/
│   ├── audio/              audio generator (build.py, preprocess.py, build.sh, build.ps1)
│   └── web/                dochub generator (build_dochub.py, dochub_assets/)
│
├── audio/   (generated, git-ignored)   MP3 narration — mirrors content/
└── dochub/  (generated, git-ignored)   static web book
```

`audio/` and `dochub/` are **created by the build scripts** and are not committed.
Delete them anytime; rebuild to recreate.

---

## 3. How to read the book

- **In Markdown:** open `content/` in your editor or on GitHub. Start at
  [content/L0-foundations/](content/L0-foundations/) and follow each chapter's
  `README.md`, or jump anywhere via [docs/CONTENTS.md](docs/CONTENTS.md).
- **In the browser (recommended):** generate the dochub web reader (§4) — it has
  search, navigation, the reference docs, the examples, and **optional audio
  players** on each topic when narration has been generated.
- **By ear:** generate the audio (§5) and listen; or open dochub after generating
  audio to get inline players.

---

## 4. Generating the web reader (dochub)

**Prerequisite:** Python 3.9+ (no other dependencies).

```bash
python scripts/web/build_dochub.py
```

This regenerates **`dochub/`**. Open **`dochub/index.html`** in any browser
(double-click, or `open dochub/index.html` / `start dochub\index.html`).

What it includes:

- Every module → chapter → topic, rendered with syntax highlighting, search, and navigation.
- A **Reference** section (this guide, README, the master index, glossary, acronyms, learning paths, and the authoring guidelines).
- An **Examples** section — one page per `examples/` project rendering its README + source files, grouped by category.
- **Cross-links:** topics that have a backing example show a "▶ Runnable example" link (driven automatically by each project's `Backs:` line).
- **Optional audio:** if a topic's MP3 exists at the mirrored path under `audio/`, the page embeds an audio player; if not, no player is shown. So dochub works with or without audio — generate audio first if you want inline players.

Rebuild whenever content, examples, or the reference docs change.

---

## 5. Generating the audio narration

The audio is **locked and cross-platform**: it reproduces identically on Windows,
macOS, or Linux with no configuration changes.

> **Locked config:** engine **`edge`** (Microsoft Edge Neural TTS via the
> `edge-tts` package), voice **`en-GB-RyanNeural`**. These are the built-in
> defaults — running the build with no arguments reproduces the published
> narration. Don't change them unless you intend to.

### Prerequisites (all platforms)

| Tool | Install |
|---|---|
| **Python 3.9+** | [python.org](https://www.python.org/downloads/) (Windows: tick *"Add Python to PATH"*) |
| **ffmpeg + ffprobe** | macOS `brew install ffmpeg` · Windows `winget install Gyan.FFmpeg` · Linux `sudo apt install ffmpeg` |
| **edge-tts** | `pip install edge-tts` |
| **Internet** | required (the `edge` voice is cloud-based) |

### Build

```bash
# macOS / Linux
scripts/audio/build.sh                 # whole course (refresh)
scripts/audio/build.sh L0/C01          # one chapter
scripts/audio/build.sh --diff L0       # dry-run: list what would build
scripts/audio/build.sh --force L0/C01  # regenerate even if unchanged

# Windows (PowerShell)
scripts\audio\build.ps1
scripts\audio\build.ps1 L0/C01

# Any OS, no wrapper
python scripts/audio/build.py [scope] [--diff|--force]
```

Output lands in **`audio/`**, mirroring `content/` exactly
(`audio/L0-foundations/C01-…/T01-….mp3`). That mirroring is what lets the web
build find a topic's MP3 and show the optional player.

- **Incremental:** each source `.md` is hashed; only changed topics regenerate. The whole course (~500+ topics, ~170 h of audio) takes ~1–2 hours the first time.
- **Offline alternative:** `ENGINE=piper scripts/audio/build.sh …` uses local [Piper](https://github.com/rhasspy/piper) TTS (needs the `piper` binary + a voice model in `audio/voices/piper/`). Different voice; use only when offline.
- **Ad-hoc one-off (any OS):** `python -m edge_tts --voice en-GB-RyanNeural --text "Hello." --write-media out.mp3`.

The two builds are **independent**: generate audio and the web build will pick it
up; skip audio and the web build simply hides the players.

---

## 6. Using the examples

`examples/` holds 21 self-contained projects (Java 21 + Maven, plus k8s manifests).
Each one's `README.md` opens with `Backs: L#/C##/T## — <topic>`, tying it to the
topic it supports. See [examples/README.md](examples/README.md) for the full map.

```bash
cd examples/system-designs/url-shortener
mvn test            # run it (most need nothing but Java 21 + Maven)
```

A couple of projects use Docker (Testcontainers) for their integration tests;
their pure-logic tests pass without it. The dochub build renders every project's
code and links it from the backing topics (§4).

---

## 7. Adding or editing content

Read **[CLAUDE.md](CLAUDE.md)** — it's the rulebook. In short:

1. Start from a file in `templates/`. Meet **[templates/CONVENTIONS.md](templates/CONVENTIONS.md)** (format) and **[templates/DEPTH-CHECKLIST.md](templates/DEPTH-CHECKLIST.md)** (depth).
2. Update the chapter `README.md` (hand-maintained), the master index [docs/CONTENTS.md](docs/CONTENTS.md), and the glossary/acronyms/learning-paths if affected.
3. Add a backing `examples/` project when useful (with a `Backs:` line).
4. Keep links relative and valid. Regenerate dochub (and audio if you maintain it).

`content/` is the source of truth — treat it carefully and additively.

---

## 8. Command cheat-sheet

```bash
# Generate docs — shortest, via the Makefile
make gen-doc TYPE=mixed     # audio first, then web   (or: make mixed)
make gen-doc TYPE=web       # web only                (or: make web)
make gen-doc TYPE=audio     # audio only              (or: make audio)
# (use TYPE=…, not --type=… — a leading -- is reserved by make)

# Read
open content/L0-foundations/                 # the Markdown book
python scripts/web/build_dochub.py && open dochub/index.html   # the web reader

# Audio (needs ffmpeg + `pip install edge-tts`)
scripts/audio/build.sh                       # build/refresh all narration  (Windows: build.ps1)
scripts/audio/build.sh --diff                # preview what would build

# Examples
cd examples/<category>/<project> && mvn test

# Master index of everything
open docs/CONTENTS.md
```
