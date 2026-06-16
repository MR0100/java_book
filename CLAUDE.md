# CLAUDE.md — Rules for Maintaining This Course

This file governs how anyone (human or AI) **adds to or edits** the Java 0-to-Hero
master book. Follow it **strictly** — it is what keeps the whole course consistent
and high-quality. If a request conflicts with these rules, surface the conflict
instead of silently breaking them.

## 0. The two non-negotiable guidelines

Every piece of content must satisfy **both**:

1. **[templates/CONVENTIONS.md](templates/CONVENTIONS.md)** — *how to format* (frontmatter, headings, callouts, code blocks, links, tables). The web build parses these mechanically; breaking them breaks the site.
2. **[templates/DEPTH-CHECKLIST.md](templates/DEPTH-CHECKLIST.md)** — *how much substance* (mechanism + a diagram per concept + full coverage). A topic is not done until it clears this bar.

Start every new topic from **[templates/topic-template.md](templates/topic-template.md)** (or `interview-qa-template.md` / `module-readme-template.md`).

## 1. What is sacred

- **`content/` is the source of truth and is hand-written. Never delete, overwrite, or lose a topic `.md` file carelessly.** Treat edits to existing content as additive unless explicitly told to remove something. When in doubt, ask.
- `audio/` and `dochub/` are **generated and git-ignored** — never hand-edit them, never commit them. Regenerate instead.

## 2. Folder structure (keep it this way)

```
content/        THE BOOK — hand-written .md (L0–L6 / C## chapters / T## topics)   ← sacred
examples/       runnable companion projects (each README starts with "Backs: L#/C##/T##")
docs/           CONTENTS.md (master index) · GLOSSARY.md · ACRONYMS.md · LEARNING-PATHS.md
templates/      CONVENTIONS.md · DEPTH-CHECKLIST.md · *-template.md   ← the guidelines
scripts/audio/  audio generator (build.py, preprocess.py, build.sh, build.ps1)
scripts/web/    dochub generator (build_dochub.py, dochub_assets/)
audio/          generated MP3 narration            (git-ignored, auto-created)
dochub/         generated static web book          (git-ignored, auto-created)
README.md · GUIDE.md · CLAUDE.md   (root)
```

Any **new script** goes under `scripts/` in a task subfolder (`scripts/audio`, `scripts/web`, …) — **never** inside `audio/` or `dochub/`.

## 3. Workflow — when you ADD or UPDATE a topic

Do all of the following, in order:

1. **Write/edit the topic** from the template, meeting CONVENTIONS + DEPTH-CHECKLIST. Keep `status: complete` and bump `last_updated`.
2. **Update its chapter `README.md`** — chapter READMEs are **hand-maintained** (there is no skeleton generator anymore). Add/adjust the topic's row and keep links correct.
3. **Update [docs/CONTENTS.md](docs/CONTENTS.md)** — the single master index. Add the topic so it's discoverable.
4. **Update the reference docs if affected:** new terms → [docs/GLOSSARY.md](docs/GLOSSARY.md); new acronyms → [docs/ACRONYMS.md](docs/ACRONYMS.md); changed study order → [docs/LEARNING-PATHS.md](docs/LEARNING-PATHS.md).
5. **Add/update a backing example** in `examples/` when the topic warrants runnable code, and start that project's `README.md` with `Backs: L#/C##/T##` so the web build auto-links it.
6. **Regenerate** the web book (and audio if you maintain it) — see GUIDE.md / §5.
7. **Verify links.** Internal links are relative and include `.md`. Don't leave dangling links.

## 4. Links & consistency rules

- Internal links are **relative** and include the `.md` extension. Topics are addressed by code path `L#/C##/T##`.
- From a `content/L#/C##/` topic, the guidelines are at `../../../templates/CONVENTIONS.md`, the index at `../../../docs/CONTENTS.md`.
- One `# H1` per file matching the frontmatter `title`. Never skip heading levels. Every code fence is language-tagged. Diagrams prefer Mermaid.
- Don't introduce new top-level files at the repo root; put docs in `docs/`, guidelines/templates in `templates/`.

## 5. Regeneration commands

```bash
# Quickest — via the Makefile (TYPE = mixed | web | audio):
make gen-doc TYPE=mixed                      # audio first, then web

# Or directly:
# Web book (dochub) — needs Python 3.9+
python scripts/web/build_dochub.py          # → dochub/index.html (open in a browser)

# Audio narration — needs Python 3.9+, ffmpeg, and `pip install edge-tts`
scripts/audio/build.sh                       # macOS/Linux  (Windows: scripts\audio\build.ps1)
python scripts/audio/build.py --diff         # dry-run: list what would build
```

The audio config is **locked** (engine `edge`, voice `en-GB-RyanNeural`) so regeneration is identical on any machine — do not change the defaults if you want output to match the published narration. Full details in **[GUIDE.md](GUIDE.md)**.
