# Makefile — documentation generation for the Java 0-to-Hero course.
#
# Prerequisites (see GUIDE.md §4–5):
#   web   : Python 3.9+
#   audio : Python 3.9+, ffmpeg/ffprobe, and `pip install edge-tts`
#
# Usage:
#   make gen-doc TYPE=mixed    # audio first, THEN web (so web embeds audio players)
#   make gen-doc TYPE=web      # web (dochub) only
#   make gen-doc TYPE=audio    # audio narration only
#
#   Shortcuts:  make mixed  |  make web  |  make audio
#
# NOTE: make uses `TYPE=mixed`, NOT `--type=mixed`. A leading `--` is reserved
#       for make's own options, so `make gen-doc --type=mixed` errors. Use TYPE=.

PYTHON      ?= python3
AUDIO_BUILD := $(PYTHON) scripts/audio/build.py
WEB_BUILD   := $(PYTHON) scripts/web/build_dochub.py
TYPE        ?= mixed

.PHONY: gen-doc mixed web audio help
.DEFAULT_GOAL := help

# Single entry point. Dispatches on TYPE (mixed | web | audio); defaults to mixed.
gen-doc:
ifeq ($(TYPE),audio)
	@$(MAKE) --no-print-directory audio
else ifeq ($(TYPE),web)
	@$(MAKE) --no-print-directory web
else ifeq ($(TYPE),mixed)
	@$(MAKE) --no-print-directory mixed
else
	@echo "Unknown TYPE='$(TYPE)'. Use one of: mixed | web | audio"; exit 2
endif

# mixed: audio FIRST, then web — so the web build can embed the audio players.
mixed:
	@echo "==> [1/2] Generating audio narration (this can take a while)..."
	$(AUDIO_BUILD)
	@echo "==> [2/2] Generating web book (dochub)..."
	$(WEB_BUILD)
	@echo "==> Done. Open dochub/index.html"

# web only.
web:
	@echo "==> Generating web book (dochub)..."
	$(WEB_BUILD)
	@echo "==> Done. Open dochub/index.html"

# audio only.
audio:
	@echo "==> Generating audio narration into audio/ (locked voice en-GB-RyanNeural)..."
	$(AUDIO_BUILD)
	@echo "==> Done. Audio in audio/ ; re-run 'make web' to embed players."

help:
	@echo "Documentation generation:"
	@echo "  make gen-doc TYPE=mixed   # audio first, then web  (default)"
	@echo "  make gen-doc TYPE=web     # web (dochub) only"
	@echo "  make gen-doc TYPE=audio   # audio narration only"
	@echo ""
	@echo "  Shortcuts:  make mixed | make web | make audio"
	@echo ""
	@echo "  Tip: use TYPE=mixed (not --type=mixed); '--' is reserved by make."
	@echo "  Prereqs: web needs Python; audio also needs ffmpeg + 'pip install edge-tts'."
