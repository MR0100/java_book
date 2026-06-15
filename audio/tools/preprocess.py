#!/usr/bin/env python3
"""Convert a markdown file into speech-friendly text for macOS `say`.

Strategy
--------
- Frontmatter:    Skipped (used only for the spoken title intro).
- Code blocks:    Skipped, replaced with a brief verbal pointer.
- Tables:         Skipped, replaced with a verbal pointer.
- ASCII diagrams: Skipped, replaced with a verbal pointer.
- Headings:       Spoken with [[slnc N]] pause markers (larger pause for higher-level headings).
- Callouts:       `> [!WARNING]` → "Warning." etc.
- Links:          `[text](url)` → "text".
- Inline code:    Read plainly with surrounding markup stripped.
- Bold / italic:  Markup stripped, text kept.
- Emojis:         Stripped.

Output is plain text with embedded `[[slnc <ms>]]` markers that macOS `say`
interprets as a pause of that many milliseconds.

Usage:
    python3 preprocess.py path/to/topic.md            # to stdout
    python3 preprocess.py path/to/topic.md -o out.txt  # to file
"""
from __future__ import annotations

import argparse
import re
import sys
from pathlib import Path


FENCE_RE = re.compile(r"^\s*```")
HEADING_RE = re.compile(r"^(#{1,6})\s+(.+?)\s*#*\s*$")
CALLOUT_RE = re.compile(r"^>\s*\[!(NOTE|TIP|IMPORTANT|WARNING|CAUTION)\]\s*(.*)$", re.IGNORECASE)
HR_RE = re.compile(r"^\s*(?:-{3,}|={3,}|\*{3,})\s*$")
TABLE_RE = re.compile(r"^\s*\|")
BULLET_RE = re.compile(r"^\s*[-*+]\s+")
NUMLIST_RE = re.compile(r"^\s*\d+\.\s+")
LINK_RE = re.compile(r"\[([^\]]+)\]\([^)]+\)")
IMG_RE = re.compile(r"!\[([^\]]*)\]\([^)]+\)")
INLINE_CODE_RE = re.compile(r"`([^`]+)`")
BOLD_RE = re.compile(r"\*\*([^*]+)\*\*|__([^_]+)__")
ITALIC_RE = re.compile(r"(?<!\*)\*([^*]+)\*(?!\*)|(?<!_)_([^_]+)_(?!_)")
STRIKE_RE = re.compile(r"~~([^~]+)~~")
# A reasonable emoji range (covers most pictographs without being exhaustive).
EMOJI_RE = re.compile(
    "["
    "\U0001F300-\U0001F9FF"
    "\U0001FA00-\U0001FAFF"
    "\U0001F600-\U0001F64F"
    "\U0001F680-\U0001F6FF"
    "\U00002600-\U000027BF"
    "]+",
    flags=re.UNICODE,
)
ASCII_DIAGRAM_HINTS = ("┌", "┐", "└", "┘", "│", "─", "├", "┤", "┬", "┴", "┼", "▶", "▲", "▼", "◀")


def parse_frontmatter(text: str) -> tuple[str, dict[str, str]]:
    """Strip YAML frontmatter; return (body, parsed_simple_fields)."""
    if not text.startswith("---"):
        return text, {}
    end = text.find("\n---", 3)
    if end == -1:
        return text, {}
    fm_block = text[3:end].strip()
    body = text[end + 4 :].lstrip("\n")
    fields: dict[str, str] = {}
    for line in fm_block.splitlines():
        if ":" in line and not line.startswith(" "):
            key, _, value = line.partition(":")
            value = value.strip().strip('"').strip("'")
            if value:
                fields[key.strip()] = value
    return body, fields


def is_ascii_diagram_line(line: str) -> bool:
    return any(ch in line for ch in ASCII_DIAGRAM_HINTS)


def clean_inline(text: str) -> str:
    """Strip markdown markup from a single line of text."""
    text = IMG_RE.sub(r"\1", text)
    text = LINK_RE.sub(r"\1", text)
    text = INLINE_CODE_RE.sub(r"\1", text)
    text = BOLD_RE.sub(lambda m: m.group(1) or m.group(2), text)
    text = ITALIC_RE.sub(lambda m: m.group(1) or m.group(2), text)
    text = STRIKE_RE.sub(r"\1", text)
    text = EMOJI_RE.sub("", text)
    # Drop residual markup characters that don't carry meaning aloud.
    text = text.replace("`", "")
    # Strip control characters (except whitespace) and zero-width chars —
    # macOS `say` hangs or crashes on some of these.
    text = "".join(
        ch for ch in text
        if (ord(ch) >= 32 or ch in "\t")
        and ord(ch) not in (0x200B, 0x200C, 0x200D, 0x200E, 0x200F, 0xFEFF)
    )
    return text.strip()


def heading_pause_ms(level: int) -> int:
    """Pause before a heading depending on its level (h1 longest)."""
    return {1: 1400, 2: 1100, 3: 900, 4: 700, 5: 600, 6: 500}.get(level, 500)


def preprocess(markdown: str) -> str:
    body, fm = parse_frontmatter(markdown)
    title = fm.get("title") or "Untitled topic"
    title_norm = re.sub(r"[^a-z0-9]+", "", title.lower())

    out: list[str] = []
    intro = f"{title}. [[slnc 1200]]"
    out.append(intro)

    in_code = False
    in_table = False
    in_diagram = False
    skipped_code_announced = False
    suppressed_first_h1 = False

    lines = body.splitlines()
    i = 0
    while i < len(lines):
        line = lines[i]
        stripped = line.strip()

        # Code fences
        if FENCE_RE.match(line):
            if not in_code:
                in_code = True
                if not skipped_code_announced:
                    out.append("[[slnc 400]] Code example. See the written notes. [[slnc 600]]")
                    skipped_code_announced = True
            else:
                in_code = False
                skipped_code_announced = False
            i += 1
            continue
        if in_code:
            i += 1
            continue

        # Tables
        if TABLE_RE.match(line):
            if not in_table:
                in_table = True
                out.append("[[slnc 400]] Reference table. See the written notes. [[slnc 600]]")
            i += 1
            continue
        in_table = False

        # ASCII diagrams (typically multiple consecutive lines with box-drawing chars)
        if is_ascii_diagram_line(line):
            if not in_diagram:
                in_diagram = True
                out.append("[[slnc 400]] Diagram. See the written notes. [[slnc 600]]")
            i += 1
            continue
        in_diagram = False

        # Horizontal rules → small pause
        if HR_RE.match(line):
            out.append("[[slnc 800]]")
            i += 1
            continue

        # Headings
        if m := HEADING_RE.match(line):
            level = len(m.group(1))
            heading_text = clean_inline(m.group(2))
            # Suppress the first h1 if it duplicates the frontmatter title (we already said it)
            if level == 1 and not suppressed_first_h1:
                suppressed_first_h1 = True
                heading_norm = re.sub(r"[^a-z0-9]+", "", heading_text.lower())
                if heading_norm == title_norm:
                    i += 1
                    continue
            pause = heading_pause_ms(level)
            out.append(f"[[slnc {pause}]] {heading_text}. [[slnc {pause // 2}]]")
            i += 1
            continue

        # Callouts (GitHub flavored)
        if m := CALLOUT_RE.match(line):
            kind = m.group(1).capitalize()
            remainder = m.group(2).strip()
            out.append(f"[[slnc 500]] {kind}. {clean_inline(remainder)}")
            i += 1
            continue

        # Blockquote lines (after callout matching)
        if stripped.startswith(">"):
            quote_text = clean_inline(stripped.lstrip("> ").lstrip(">"))
            if quote_text:
                out.append(quote_text)
            i += 1
            continue

        # Bullet / numbered lists → plain sentence
        if BULLET_RE.match(line) or NUMLIST_RE.match(line):
            item = BULLET_RE.sub("", line)
            item = NUMLIST_RE.sub("", item)
            cleaned = clean_inline(item)
            if cleaned:
                if not cleaned.endswith((".", "!", "?", ":", ";", ",")):
                    cleaned = f"{cleaned}."
                out.append(cleaned)
            i += 1
            continue

        # Blank lines → small pause between paragraphs
        if not stripped:
            if out and not out[-1].startswith("[[slnc"):
                out.append("[[slnc 350]]")
            i += 1
            continue

        # Plain paragraph line
        cleaned = clean_inline(stripped)
        if cleaned:
            out.append(cleaned)
        i += 1

    # Collapse runs of pause-only markers into one (avoid huge silences)
    collapsed: list[str] = []
    for chunk in out:
        if chunk.startswith("[[slnc") and collapsed and collapsed[-1].startswith("[[slnc"):
            continue
        collapsed.append(chunk)

    return "\n".join(collapsed) + "\n"


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__.splitlines()[0] if __doc__ else None)
    parser.add_argument("input", type=Path, help="Markdown file to preprocess")
    parser.add_argument("-o", "--output", type=Path, help="Output file (stdout if omitted)")
    args = parser.parse_args()

    markdown = args.input.read_text(encoding="utf-8")
    result = preprocess(markdown)

    if args.output:
        args.output.write_text(result, encoding="utf-8")
    else:
        sys.stdout.write(result)
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
