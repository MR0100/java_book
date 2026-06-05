#!/usr/bin/env python3
"""
Build the Java 0-to-Hero dochub — a beautiful static HTML reader for every
Markdown file in the repo.

Mirrors the pattern of `tool/build_dochub.dart` from the KGK Flutter project:
one generator script + a separate CSS/JS asset bundle, producing a self-
contained `dochub/` tree that opens directly in a browser. No node_modules,
no build step beyond `python3 scripts/build_dochub.py`.

Reads (READ-ONLY — never modifies):
    README.md, CURRICULUM.md, CONVENTIONS.md, DEPTH-CHECKLIST.md,
    TOPIC-CATALOG.md, PROGRESS.md, PROGRESS-L1.md
    content/L*/README.md          (level overviews)
    content/L*/C*/README.md       (chapter overviews)
    content/L*/C*/T*.md           (topic concept files)
    scripts/dochub_assets/style.css
    scripts/dochub_assets/app.js

Writes:
    dochub/index.html             (hero + chapter cards + search)
    dochub/{chapter}/index.html   (category page for each chapter)
    dochub/{chapter}/{slug}.html  (per-doc reader page)
    dochub/assets/style.css       (copied)
    dochub/assets/app.js          (copied)
    dochub/assets/search-index.json

Eight chapters:
    1. reference  — root MDs (README, CURRICULUM, …)
    2. l0         — Foundations
    3. l1         — Core Java & OOP
    4. l2         — Intermediate & Backend
    5. l3         — Advanced Java & the JVM
    6. l4         — Backend Engineering
    7. l5         — Architecture & Leadership
    8. l6         — Interview Mastery

Optional env:
    DOCHUB_ONLY_COMPLETE=1   Hide topics whose frontmatter status != 'complete'

Requires (already installed on macOS Python 3.9):
    pip3 install markdown
"""

from __future__ import annotations
import os
import re
import sys
import json
import html
import shutil
from pathlib import Path
from dataclasses import dataclass, field
from typing import Any, Dict, List, Optional, Tuple

try:
    import markdown as md_lib
    from markdown.extensions.toc import TocExtension
except ImportError:
    sys.stderr.write(
        "ERROR: Python 'markdown' package not installed.\n"
        "Install with:  pip3 install markdown\n"
    )
    sys.exit(1)


# =============================================================================
# Paths
# =============================================================================

SCRIPT_DIR = Path(__file__).resolve().parent
ROOT = SCRIPT_DIR.parent
OUT_DIR = ROOT / "dochub"
ASSETS_SRC = SCRIPT_DIR / "dochub_assets"

ONLY_COMPLETE = os.environ.get("DOCHUB_ONLY_COMPLETE") == "1"

EXCLUDE_DIRS = {
    ".git", "node_modules", "build", ".docusaurus", ".cache-loader",
    "dochub", "web", ".idea", ".vscode",
}


# =============================================================================
# Chapter definitions
# =============================================================================
# slug, number, title, path_label, subtitle, icon, color (CSS var name)

CHAPTERS: Dict[str, Dict[str, Any]] = {
    "reference": {
        "slug": "reference", "number": 1,
        "nav_label": "Ref",
        "title": "Reference", "path_label": "/",
        "subtitle": (
            "The phonebook and the rulebook. The master curriculum, the "
            "authoring conventions, the depth bar every topic must clear, the "
            "candidate-topic catalogue, and the living progress trackers."
        ),
        "icon": "📚", "color": "indigo",
    },
    "l0": {
        "slug": "l0", "number": 2,
        "title": "L0 — Foundations", "path_label": "content/L0-foundations/",
        "subtitle": (
            "The starting line. Zero programming experience assumed. How "
            "programs run, the JDK/JRE/JVM split, syntax, control flow, "
            "methods, arrays — the building blocks every later module relies "
            "on. Complete and authored to the deep-bar standard."
        ),
        "icon": "🌱", "color": "teal",
    },
    "l1": {
        "slug": "l1", "number": 3,
        "title": "L1 — Core Java & OOP", "path_label": "content/L1-core-java/",
        "subtitle": (
            "Where Java starts to feel like Java. Think in objects and use "
            "the core language fluently — classes, inheritance, generics, "
            "exceptions, collections, the standard library — to the same "
            "language + memory + architecture depth as L0."
        ),
        "icon": "🧬", "color": "violet",
    },
    "l2": {
        "slug": "l2", "number": 4,
        "title": "L2 — Intermediate & Backend",
        "path_label": "content/L2-intermediate-backend/",
        "subtitle": (
            "Modern idiomatic Java plus the backend vocabulary every server-"
            "side developer needs before touching a framework. Streams, "
            "build tools, networking, REST, SQL/JDBC."
        ),
        "icon": "🛰", "color": "pink",
    },
    "l3": {
        "slug": "l3", "number": 5,
        "title": "L3 — Advanced JVM", "path_label": "content/L3-advanced-jvm/",
        "subtitle": (
            "The jump from writing working code to understanding what the "
            "machine is doing. Concurrency, the JVM internals, the memory "
            "model, garbage collection, performance work, design patterns."
        ),
        "icon": "⚙", "color": "amber",
    },
    "l4": {
        "slug": "l4", "number": 6,
        "title": "L4 — Backend Engineering",
        "path_label": "content/L4-backend-engineering/",
        "subtitle": (
            "Build, test, secure, and operate a production-grade backend "
            "service. Spring, JPA/Hibernate, REST, messaging, security, "
            "observability, Docker, Kubernetes — the senior skill set."
        ),
        "icon": "🏗", "color": "emerald",
    },
    "l5": {
        "slug": "l5", "number": 7,
        "title": "L5 — Architecture & Leadership",
        "path_label": "content/L5-architecture-leadership/",
        "subtitle": (
            "Design systems at scale and lead the people who build them. "
            "Software architecture, distributed-systems theory, the worked "
            "system designs, ADRs, mentoring, technical strategy."
        ),
        "icon": "🧭", "color": "sky",
    },
    "l6": {
        "slug": "l6", "number": 8,
        "title": "L6 — Interview Mastery",
        "path_label": "content/L6-interview-mastery/",
        "subtitle": (
            "The dedicated interview module. Turns L0–L5 into offers. DSA in "
            "Java, low-level and high-level design interviews, behavioural "
            "prep, FAANGM company tracks (Flipkart/Apple/Amazon/Netflix/"
            "Google/Meta)."
        ),
        "icon": "🎯", "color": "rose",
    },
}

NUM_WORDS = {1: "One", 2: "Two", 3: "Three", 4: "Four", 5: "Five",
             6: "Six", 7: "Seven", 8: "Eight", 9: "Nine", 10: "Ten"}

ROOT_FILE_ORDER = [
    "README.md",
    "CURRICULUM.md",
    "CONVENTIONS.md",
    "DEPTH-CHECKLIST.md",
    "TOPIC-CATALOG.md",
    "PROGRESS.md",
    "PROGRESS-L1.md",
]


# =============================================================================
# Data models
# =============================================================================

@dataclass
class Doc:
    src: Path
    rel: str                 # path relative to repo root
    chapter: str             # chapter slug
    module: str              # module key (unique per chapter)
    module_label: str
    module_order: List[Any]
    kind: str                # 'overview' | 'page'
    title: str
    out_rel: str             # path relative to OUT_DIR, e.g. 'l1/T01-foo.html'
    raw: str
    frontmatter: Dict[str, Any] = field(default_factory=dict)
    body_html: str = ""

    @property
    def url(self) -> str:
        return self.out_rel


@dataclass
class Module:
    key: str
    label: str
    chapter: str
    order: List[Any]
    docs: List[Doc] = field(default_factory=list)

    @property
    def primary(self) -> Doc:
        for d in self.docs:
            if d.kind == "overview":
                return d
        return self.docs[0]


@dataclass
class Chapter:
    slug: str
    number: int
    title: str
    path_label: str
    subtitle: str
    icon: str
    color: str
    nav_label: str = ""          # short label for the topnav strip
    modules: List[Module] = field(default_factory=list)
    docs: List[Doc] = field(default_factory=list)


# =============================================================================
# Frontmatter — flat YAML parser tailored for our CONVENTIONS.md spec
# =============================================================================

_FM_RE = re.compile(r"^---\s*\n(.*?)\n---\s*\n", re.DOTALL)


def _parse_scalar(val: str) -> Any:
    val = val.strip()
    if not val or val.lower() in ("null", "~"):
        return None
    if (val[0] == val[-1]) and val[0] in ("'", '"'):
        return val[1:-1]
    if val.lower() == "true":
        return True
    if val.lower() == "false":
        return False
    # ISO date — leave as string
    if re.match(r"^\d{4}-\d{2}-\d{2}$", val):
        return val
    if re.match(r"^-?\d+$", val):
        try:
            return int(val)
        except ValueError:
            return val
    if re.match(r"^-?\d+\.\d+$", val):
        try:
            return float(val)
        except ValueError:
            return val
    return val


def _parse_inline_list(val: str) -> List[Any]:
    inner = val.strip()[1:-1].strip()
    if not inner:
        return []
    out: List[Any] = []
    for raw in inner.split(","):
        x = _parse_scalar(raw)
        if x is None or (isinstance(x, str) and not x.strip()):
            continue
        out.append(x)
    return out


def parse_frontmatter(raw: str) -> Tuple[Dict[str, Any], str]:
    m = _FM_RE.match(raw)
    if not m:
        return {}, raw
    fm_text = m.group(1)
    body = raw[m.end():]
    fm: Dict[str, Any] = {}
    for line in fm_text.split("\n"):
        line = line.rstrip()
        if not line or line.lstrip().startswith("#"):
            continue
        if ":" not in line:
            continue
        key, _, val = line.partition(":")
        key = key.strip()
        val = val.strip()
        if not key:
            continue
        if not val:
            fm[key] = ""
            continue
        if val.startswith("[") and val.endswith("]"):
            fm[key] = _parse_inline_list(val)
        else:
            fm[key] = _parse_scalar(val)
    return fm, body


# =============================================================================
# Discovery & classification
# =============================================================================

def slugify(s: str) -> str:
    r = s.lower()
    r = re.sub(r"[^a-z0-9]+", "-", r)
    r = re.sub(r"^-+|-+$", "", r)
    return r or "doc"


# Common acronyms used in the Java/backend domain — humanize() upper-cases
# these so chapter labels read "C01 · OOP" instead of "C01 · Oop".
ACRONYMS = {
    "oop", "jvm", "jdk", "jre", "jpms", "rest", "jdbc", "orm", "jpa", "sql",
    "nio", "api", "dsa", "lld", "hld", "mnc", "os", "cli", "bff",
    "cdn", "dns", "tcp", "udp", "http", "https", "tls", "ssl", "jwt",
    "owasp", "acid", "cap", "cdc", "ide", "jfr", "jmh", "gc", "aot", "jit",
    "crud", "mvc", "rfc", "adr", "faangm", "qa", "faq", "io", "ui", "ux",
    "json", "xml", "yaml", "csv", "jpa", "amqp", "jms", "sse", "graphql",
    "grpc", "cqrs", "ddd", "oauth", "openid", "saml", "kpi", "sla", "slo",
    "sli", "k8s",
}


def humanize(s: str) -> str:
    r = s.replace("_", " ").replace("-", " ").strip()
    out: List[str] = []
    for w in r.split(" "):
        if not w:
            continue
        lw = w.lower()
        if lw in ACRONYMS:
            out.append(w.upper())
        elif lw.endswith("s") and lw[:-1] in ACRONYMS:
            # Plural acronym — "apis" → "APIs", not "APIS"
            out.append(lw[:-1].upper() + "s")
        else:
            out.append(w[:1].upper() + w[1:])
    return " ".join(out)


def filename_label(rel: str) -> str:
    stem = Path(rel).stem
    parent = Path(rel).parent.name
    if stem.upper() in ("README", "TESTCASES") and parent and parent != ".":
        return f"{parent}/{stem}"
    return stem


def title_for(rel: str, raw: str, fm: Dict[str, Any]) -> str:
    if isinstance(fm.get("title"), str) and fm["title"].strip():
        return fm["title"].strip()
    m = re.search(r"^#\s+(.+?)\s*$", raw, re.MULTILINE)
    if m:
        return re.sub(r"`([^`]+)`", r"\1", m.group(1)).strip()
    if rel == "README.md":
        return "Project README"
    parts = Path(rel).parts
    if len(parts) >= 2 and parts[-1].lower() == "readme.md":
        return humanize(parts[-2])
    return humanize(Path(rel).stem)


def find_md_files() -> List[Path]:
    """Walk the repo, returning every .md we want to include."""
    out: List[Path] = []
    # Root files
    for name in ROOT_FILE_ORDER:
        p = ROOT / name
        if p.is_file():
            out.append(p)
    # content/ tree
    content = ROOT / "content"
    if content.is_dir():
        for p in sorted(content.rglob("*.md")):
            parts = p.relative_to(ROOT).parts
            if any(part in EXCLUDE_DIRS for part in parts):
                continue
            out.append(p)
    return out


_LEVEL_RE = re.compile(r"^L(\d)-")
_CHAPTER_RE = re.compile(r"^C(\d{2})-")
_TOPIC_RE = re.compile(r"^T(\d{2})-")


def classify(rel: str) -> Optional[Dict[str, Any]]:
    """Map a repo-relative .md path to (chapter, module, kind, out_rel, ...)."""
    parts = rel.split("/")

    # 1) Root markdown — Reference chapter
    if len(parts) == 1:
        fname = parts[0]
        if fname not in ROOT_FILE_ORDER:
            return None
        order = ROOT_FILE_ORDER.index(fname)
        stem = fname[:-3]  # strip .md
        return {
            "chapter": "reference",
            "module": f"reference:{slugify(stem)}",
            "module_label": stem,
            "module_order": [order],
            "kind": "page",
            "out_rel": f"reference/{slugify(stem)}.html",
        }

    # 2) content/L*/...
    if parts[0] != "content" or len(parts) < 2:
        return None

    level_dir = parts[1]
    lm = _LEVEL_RE.match(level_dir)
    if not lm:
        return None
    level_num = int(lm.group(1))
    chapter_slug = f"l{level_num}"
    if chapter_slug not in CHAPTERS:
        return None

    # 2a) Level README → Overview module
    if len(parts) == 3 and parts[-1].lower() == "readme.md":
        return {
            "chapter": chapter_slug,
            "module": f"{chapter_slug}:overview",
            "module_label": "Overview",
            "module_order": [0],
            "kind": "overview",
            "out_rel": f"{chapter_slug}/overview.html",
        }

    if len(parts) < 4:
        return None
    chap_dir = parts[2]
    cm = _CHAPTER_RE.match(chap_dir)
    if not cm:
        return None
    chap_num = int(cm.group(1))
    chap_rest = chap_dir[len(cm.group(0)):]  # strip "C##-"
    chap_label_short = humanize(chap_rest)
    module_key = f"{chapter_slug}:c{chap_num:02d}"
    module_label = f"C{chap_num:02d} · {chap_label_short}"

    # 2b) Chapter README
    if parts[-1].lower() == "readme.md" and len(parts) == 4:
        return {
            "chapter": chapter_slug,
            "module": module_key,
            "module_label": module_label,
            "module_order": [chap_num],
            "kind": "overview",
            "out_rel": f"{chapter_slug}/c{chap_num:02d}-readme.html",
        }

    # 2c) Topic file
    fname = parts[-1]
    tm = _TOPIC_RE.match(fname)
    if not tm or len(parts) != 4:
        return None
    topic_num = int(tm.group(1))
    topic_stem = fname[:-3]  # strip .md
    return {
        "chapter": chapter_slug,
        "module": module_key,
        "module_label": module_label,
        "module_order": [chap_num],
        "kind": "page",
        "out_rel": f"{chapter_slug}/c{chap_num:02d}-{slugify(topic_stem)}.html",
        "topic_num": topic_num,
    }


# =============================================================================
# Markdown rendering
# =============================================================================

# Known HTML5 element names — `<Foo>` outside this set in prose is treated
# as a placeholder and escaped so it renders as literal text. Otherwise the
# browser silently swallows things like `<T extends Number>`.
HTML_ELEMENTS = {
    "a","abbr","address","area","article","aside","audio","b","base","bdi",
    "bdo","blockquote","body","br","button","canvas","caption","cite","code",
    "col","colgroup","data","datalist","dd","del","details","dfn","dialog",
    "div","dl","dt","em","embed","fieldset","figcaption","figure","footer",
    "form","h1","h2","h3","h4","h5","h6","head","header","hgroup","hr","html",
    "i","iframe","img","input","ins","kbd","label","legend","li","link","main",
    "map","mark","menu","meta","meter","nav","noscript","object","ol","optgroup",
    "option","output","p","param","picture","pre","progress","q","rb","rp","rt",
    "rtc","ruby","s","samp","script","section","select","slot","small","source",
    "span","strong","style","sub","summary","sup","table","tbody","td","template",
    "textarea","tfoot","th","thead","time","title","tr","track","u","ul","var",
    "video","wbr",
    "svg","path","circle","rect","line","polyline","polygon","g","defs","use",
    "symbol","text","mask","clippath","lineargradient","radialgradient","stop",
    "filter",
}

_PLACEHOLDER_RE = re.compile(r"<([^<>\n]+?)>")
_AUTOLINK_SCHEME = re.compile(r"^[a-z][a-z0-9+.-]*://", re.IGNORECASE)
_AUTOLINK_EMAIL = re.compile(r"^[^\s@<>]+@[^\s@<>]+\.[^\s@<>]+$")
_TAG_NAME_RE = re.compile(r"^([a-zA-Z][a-zA-Z0-9-]*)")
BOOLEAN_ATTRS = {
    "allowfullscreen","async","autofocus","autoplay","checked","controls",
    "default","defer","disabled","formnovalidate","hidden","inert","ismap",
    "itemscope","loop","multiple","muted","nomodule","novalidate","open",
    "playsinline","readonly","required","reversed","selected",
}


def _escape_segment(s: str) -> str:
    def repl(m: re.Match) -> str:
        content = m.group(1)
        if not content:
            return m.group(0)
        if _AUTOLINK_SCHEME.search(content):
            return m.group(0)
        if content.startswith("mailto:") or _AUTOLINK_EMAIL.match(content):
            return m.group(0)
        if content.startswith("!--") or content.startswith("!DOCTYPE"):
            return m.group(0)
        c = content
        if c.startswith("/"):
            c = c[1:]
        c = c.lstrip()
        if c.endswith("/"):
            c = c[:-1].rstrip()
        tag_m = _TAG_NAME_RE.match(c)
        def esc() -> str:
            return "&lt;" + content.replace("&", "&amp;") + "&gt;"
        if not tag_m:
            return esc()
        tag = tag_m.group(1)
        if tag != tag.lower():
            return esc()
        if tag not in HTML_ELEMENTS:
            return esc()
        rest = c[len(tag):].strip()
        if not rest:
            return m.group(0)
        if "=" in rest or '"' in rest or "'" in rest:
            return m.group(0)
        words = rest.split()
        if all(w in BOOLEAN_ATTRS for w in words):
            return m.group(0)
        return esc()
    return _PLACEHOLDER_RE.sub(repl, s)


def escape_placeholder_tags(src: str) -> str:
    """Walk the source line-by-line; escape `<placeholder>` tokens outside
    fenced code, indented code, and inline-code spans."""
    out: List[str] = []
    in_fence = False
    fence_marker: Optional[str] = None
    for ln in src.split("\n"):
        t = ln.lstrip()
        if not in_fence and (t.startswith("```") or t.startswith("~~~")):
            in_fence = True
            fence_marker = "```" if t.startswith("```") else "~~~"
            out.append(ln)
            continue
        if in_fence:
            out.append(ln)
            assert fence_marker is not None
            if t.startswith(fence_marker):
                in_fence = False
                fence_marker = None
            continue
        if ln.startswith("    ") or ln.startswith("\t"):
            out.append(ln)
            continue
        buf: List[str] = []
        i = 0
        while i < len(ln):
            c = ln[i]
            if c == "`":
                end = ln.find("`", i + 1)
                if end == -1:
                    buf.append(ln[i:])
                    i = len(ln)
                else:
                    buf.append(ln[i:end + 1])
                    i = end + 1
            else:
                nxt = ln.find("`", i)
                end = len(ln) if nxt == -1 else nxt
                buf.append(_escape_segment(ln[i:end]))
                i = end
        out.append("".join(buf))
    return "\n".join(out)


def make_md_engine() -> md_lib.Markdown:
    return md_lib.Markdown(extensions=[
        "fenced_code",
        "tables",
        "sane_lists",
        "attr_list",
        "def_list",
        "footnotes",
        TocExtension(permalink=False, baselevel=1, toc_depth="2-4"),
    ], output_format="html5")


_ALERT_RE = re.compile(
    r"<blockquote>\s*<p>\[!(NOTE|TIP|WARNING|IMPORTANT|CAUTION|INTERVIEW)\]\s*"
    r"(?:<br\s*/?>)?\s*(.*?)</p>(.*?)</blockquote>",
    re.DOTALL | re.IGNORECASE,
)

_ALERT_ICONS = {
    "NOTE": "ⓘ", "TIP": "💡", "WARNING": "⚠", "IMPORTANT": "‼",
    "CAUTION": "⚠", "INTERVIEW": "🎤",
}


def _h_unescape(s: str) -> str:
    return html.unescape(s)


def _h_escape(s: str) -> str:
    return html.escape(s, quote=True)


def post_process_html(body: str) -> str:
    # 1) Mermaid swap
    body = re.sub(
        r'<pre(?:\s+class="codehilite")?>\s*<code class="language-mermaid">(.*?)</code>\s*</pre>',
        lambda m: f'<div class="mermaid">{_h_unescape(m.group(1))}</div>',
        body, flags=re.DOTALL,
    )

    # 2) Callouts (GH-style + custom INTERVIEW)
    def alert_repl(m: re.Match) -> str:
        kind = m.group(1).upper()
        first = m.group(2).strip()
        rest = m.group(3).strip()
        cls = kind.lower()
        body_html = f"<p>{first}</p>{rest}" if rest else f"<p>{first}</p>"
        ic = _ALERT_ICONS.get(kind, "ⓘ")
        kind_label = kind[0] + kind[1:].lower()
        return (
            f'<div class="callout callout-{cls}">'
            f'<div class="callout-head"><span class="callout-ic">{ic}</span>'
            f'<span class="callout-kind">{kind_label}</span></div>'
            f'<div class="callout-body">{body_html}</div>'
            f'</div>'
        )
    body = _ALERT_RE.sub(alert_repl, body)

    # 3a) Fenced code WITH language → decorated wrapper
    def code_lang_repl(m: re.Match) -> str:
        lang = m.group(1)
        inner = m.group(2)
        return (
            f'<div class="code-block">'
            f'<div class="code-head">'
            f'<span class="code-lang">{_h_escape(lang)}</span>'
            f'<button class="code-copy" type="button" title="Copy">Copy</button>'
            f'</div>'
            f'<pre class="codehilite"><code class="language-{_h_escape(lang)}">{inner}</code></pre>'
            f'</div>'
        )
    body = re.sub(
        r'<pre(?:\s+class="codehilite")?>\s*<code class="language-([^"]+)">(.*?)</code>\s*</pre>',
        code_lang_repl, body, flags=re.DOTALL,
    )

    # 3b) Fenced code WITHOUT language
    def code_plain_repl(m: re.Match) -> str:
        inner = m.group(1)
        return (
            f'<div class="code-block">'
            f'<div class="code-head">'
            f'<button class="code-copy" type="button" title="Copy">Copy</button>'
            f'</div>'
            f'<pre class="codehilite"><code>{inner}</code></pre>'
            f'</div>'
        )
    body = re.sub(
        r'<pre(?:\s+class="codehilite")?>\s*<code>(.*?)</code>\s*</pre>',
        code_plain_repl, body, flags=re.DOTALL,
    )

    # 4) Anchor links on h2/h3/h4
    def anchor_repl(m: re.Match) -> str:
        tag = m.group(1)
        attrs = m.group(2)
        inner = m.group(3)
        id_m = re.search(r'\sid="([^"]+)"', attrs)
        if not id_m:
            return m.group(0)
        hid = id_m.group(1)
        return f'<{tag}{attrs}>{inner}<a class="anchor" href="#{hid}" aria-hidden="true">#</a></{tag}>'
    body = re.sub(
        r'<(h[234])((?:\s+[^>]*)?)>(.*?)</\1>',
        anchor_repl, body, flags=re.DOTALL | re.IGNORECASE,
    )

    # 5) Drop the first H1 (article header already shows it)
    body = re.sub(r'^\s*<h1(?:\s+[^>]*)?>.*?</h1>\s*', "", body, count=1, flags=re.DOTALL)

    # 6) Task lists (GFM-style `- [ ] foo`) — python-markdown doesn't render them
    #    natively without an extension; we'll do a quick fix-up.
    def task_repl(m: re.Match) -> str:
        marker = m.group(1).lower()
        rest = m.group(2)
        checked = " checked" if marker == "x" else ""
        done = " task-done" if marker == "x" else ""
        return (
            f'<li class="task-item{done}">'
            f'<input type="checkbox" disabled{checked} />'
            f'<span>{rest}</span></li>'
        )
    body = re.sub(r'<li>\[([ xX])\]\s*(.*?)</li>', task_repl, body, flags=re.DOTALL)

    # 7) Wrap tables for horizontal scroll
    body = re.sub(r'(<table[\s>])', r'<div class="table-wrap">\1', body)
    body = body.replace("</table>", "</table></div>")

    return body


def render_markdown(raw: str) -> str:
    escaped = escape_placeholder_tags(raw)
    engine = make_md_engine()
    return engine.convert(escaped)


def rewrite_links(html_text: str, src_file: Path, doc_map: Dict[str, Doc], current: Doc) -> str:
    """Rewrite relative .md links so they point at the generated HTML."""
    src_dir = src_file.parent
    cur_dir = (OUT_DIR / current.out_rel).parent

    def repl(m: re.Match) -> str:
        href = m.group(1)
        if href.startswith(("http://", "https://", "mailto:", "#")):
            return m.group(0)
        anchor = ""
        if "#" in href:
            href, _, anchor = href.partition("#")
            anchor = "#" + anchor
        try:
            target_path = (src_dir / href).resolve()
        except (OSError, RuntimeError):
            return m.group(0)
        key = str(target_path)
        target = doc_map.get(key)
        if not target:
            return m.group(0)
        try:
            rel = os.path.relpath(OUT_DIR / target.out_rel, cur_dir).replace(os.sep, "/")
        except ValueError:
            return m.group(0)
        return f'href="{_h_escape(rel + anchor)}"'

    return re.sub(r'href="([^"#][^"]*)"', repl, html_text)


# =============================================================================
# Helpers — search index + lede + TOC rail
# =============================================================================

def compare_orders(a: List[Any], b: List[Any]) -> int:
    def norm(x: Any) -> str:
        if isinstance(x, int):
            return str(x).rjust(12, "0")
        return str(x)
    for i in range(min(len(a), len(b))):
        ca = norm(a[i])
        cb = norm(b[i])
        if ca < cb:
            return -1
        if ca > cb:
            return 1
    return (len(a) > len(b)) - (len(a) < len(b))


def strip_to_text(raw_md: str, max_chars: int = 5000) -> str:
    s = raw_md
    s = re.sub(r"```[\s\S]*?```", " ", s)
    s = re.sub(r"`[^`]+`", " ", s)
    s = re.sub(r"<[^>]+>", " ", s)
    s = re.sub(r"!\[[^\]]*\]\([^)]+\)", " ", s)
    s = re.sub(r"\[([^\]]+)\]\([^)]+\)", r"\1", s)
    s = re.sub(r"^[#>|=\-+*]+\s*", " ", s, flags=re.MULTILINE)
    s = re.sub(r"[*_~]+", " ", s)
    s = re.sub(r"\s+", " ", s).strip()
    if len(s) > max_chars:
        s = s[:max_chars]
    return s


def extract_lede(raw: str) -> str:
    lines = raw.split("\n")
    started = False
    buf: List[str] = []
    for ln in lines:
        s = ln.strip()
        if not started:
            if s.startswith("#"):
                started = True
            continue
        if not s:
            if buf:
                break
            continue
        if s.startswith(("#", ">", "|", "```")):
            if buf:
                break
            continue
        if s.startswith(("-", "*")):
            if buf:
                break
            continue
        buf.append(s)
        if len(" ".join(buf)) > 160:
            break
    text = " ".join(buf).strip()
    text = re.sub(r"`([^`]+)`", r"\1", text)
    text = re.sub(r"\[([^\]]+)\]\([^)]+\)", r"\1", text)
    text = re.sub(r"\*\*([^*]+)\*\*", r"\1", text)
    text = re.sub(r"\*([^*]+)\*", r"\1", text)
    if len(text) > 240:
        text = text[:237].rstrip() + "…"
    return text


def build_toc_rail(body_html: str) -> str:
    matches = list(re.finditer(
        r'<h([23])(?:[^>]*)\sid="([^"]+)"[^>]*>(.*?)</h\1>',
        body_html, flags=re.DOTALL | re.IGNORECASE,
    ))
    if not matches:
        return ""
    lis: List[str] = []
    for m in matches:
        level = m.group(1)
        anchor = m.group(2)
        text = m.group(3)
        text = re.sub(r'<a class="anchor"[^>]*>.*?</a>', "", text, flags=re.DOTALL)
        text = re.sub(r"<[^>]+>", "", text).strip()
        lis.append(
            f'<li class="lvl-{level}"><a href="#{_h_escape(anchor)}" '
            f'data-toc-link>{_h_escape(text)}</a></li>'
        )
    return (
        '<aside class="toc-rail" aria-label="On this page">'
        '<h4>On this page</h4>'
        f'<ul>{"".join(lis)}</ul>'
        '</aside>'
    )


# =============================================================================
# Page templates
# =============================================================================

def theme_toggle_html() -> str:
    return (
        '<button id="theme-toggle" class="theme-toggle" type="button" '
        'aria-label="Toggle light/dark theme" title="Theme: auto (click to cycle)">'
        '<span class="tt-icon" aria-hidden="true">◐</span>'
        '<span class="tt-label">Auto</span>'
        '</button>'
    )


def nav_html(up: str, current_chapter: Optional[str]) -> str:
    parts: List[str] = []
    for ch in CHAPTER_LIST:
        active = " active" if current_chapter == ch.slug else ""
        parts.append(
            f'<a class="nav-link{active}" href="{up}{ch.slug}/index.html" '
            f'title="{_h_escape(ch.title)}">'
            f'<span class="name">{_h_escape(ch.nav_label)}</span>'
            f'<span class="count">{len(ch.docs)}</span>'
            f'</a>'
        )
    return "".join(parts)


def page_shell(title: str, body: str, *, base_depth: int,
               current_chapter: Optional[str] = None,
               with_progress: bool = False) -> str:
    up = "../" * base_depth
    progress = '<div class="progress-bar"><div class="fill"></div></div>' if with_progress else ""
    return f"""<!doctype html>
<html lang="en">
<head>
<meta charset="utf-8" />
<meta name="viewport" content="width=device-width, initial-scale=1" />
<title>{_h_escape(title)} — Java 0-to-Hero</title>
<link rel="stylesheet" href="{up}assets/style.css" />
<script>
  try {{
    var t = localStorage.getItem('dochub-theme');
    if (t && t !== 'auto') document.documentElement.dataset.theme = t;
  }} catch (e) {{}}
</script>
</head>
<body>
{progress}
<header class="topnav">
  <div class="topnav-inner">
    <a href="{up}index.html" class="brand">
      <div class="brand-logo">J</div>
      <div>
        <div class="brand-name">Java 0-to-Hero</div>
        <div class="brand-sub">Documentation hub</div>
      </div>
    </a>
    <nav>{nav_html(up, current_chapter)}</nav>
    <div class="spacer"></div>
    {theme_toggle_html()}
  </div>
</header>
{body}
<footer>
  <div class="container">
    <div>&copy; Java 0-to-Hero · Static site generated from the repo Markdown</div>
    <div>Read-only mirror of <code>content/</code> and root reference docs</div>
  </div>
</footer>
<script src="{up}assets/app.js"></script>
</body>
</html>
"""


_KIND_ORDER = {"overview": 0, "page": 1}


def _sort_docs(docs: List[Doc]) -> List[Doc]:
    def key(d: Doc) -> Tuple[int, str]:
        return (_KIND_ORDER.get(d.kind, 9), d.rel)
    return sorted(docs, key=key)


def sidebar_html(chapter: Chapter, current: Doc, base_depth: int) -> str:
    up = "../" * base_depth
    parts: List[str] = [
        f'<div class="chapter-pill">'
        f'<span class="ic">{chapter.icon}</span>'
        f'<span>Ch. {chapter.number} · {_h_escape(chapter.title)}</span>'
        f'</div>',
        f'<div class="chapter-path">{_h_escape(chapter.path_label)}</div>',
        '<div class="side-search">'
        '<input id="side-search" type="search" placeholder="Filter this chapter…" />'
        '</div>',
        '<div class="nav-section">',
    ]

    for mod in chapter.modules:
        if len(mod.docs) == 1:
            d = mod.docs[0]
            active = " mod-flat-active" if d is current else ""
            href = up + d.url
            hay = f"{d.title} {d.rel} {mod.label}".lower()
            parts.append(
                f'<a class="mod-flat{active}" href="{_h_escape(href)}" '
                f'data-q="{_h_escape(hay)}">'
                f'<span>{_h_escape(mod.label)}</span></a>'
            )
            continue

        current_in_mod = any(d is current for d in mod.docs)
        attr_open = " open" if current_in_mod else ""
        hay_mod = (mod.label + " " + " ".join(d.rel for d in mod.docs)).lower()
        parts.append(
            f'<details class="mod"{attr_open} data-q="{_h_escape(hay_mod)}">'
            f'<summary><span class="caret">▶</span>'
            f'<span>{_h_escape(mod.label)}</span></summary>'
            f'<ul class="pages">'
        )
        for d in _sort_docs(mod.docs):
            active = " active" if d is current else ""
            href = up + d.url
            hay = f"{d.title} {d.rel}".lower()
            if d.kind == "overview":
                kind_label, kind_cls, pill = "Overview", "overview", "intro"
            else:
                kind_label, kind_cls, pill = d.title, "", ""
            parts.append(
                f'<li data-q="{_h_escape(hay)}">'
                f'<a class="{active.strip()}" href="{_h_escape(href)}">'
                f'<span>{_h_escape(kind_label)}</span>'
                f'<span class="kind-pill {kind_cls}">{_h_escape(pill)}</span>'
                f'</a></li>'
            )
        parts.append("</ul></details>")

    parts.append("</div>")
    return f'<aside class="sidebar">{"".join(parts)}</aside>'


def build_doc_page(doc: Doc, chapter: Chapter) -> str:
    base_depth = len(Path(doc.out_rel).parts) - 1
    up = "../" * base_depth

    # Flat order across the chapter for prev/next.
    flat: List[Doc] = []
    for mod in chapter.modules:
        flat.extend(_sort_docs(mod.docs))
    idx = flat.index(doc) if doc in flat else -1
    prev_doc = flat[idx - 1] if idx > 0 else None
    next_doc = flat[idx + 1] if 0 <= idx < len(flat) - 1 else None

    toc = build_toc_rail(doc.body_html)

    breadcrumb = (
        '<div class="breadcrumb">'
        f'<a href="{up}index.html">Docs hub</a><span class="sep">/</span>'
        f'<a href="{up}{chapter.slug}/index.html">{_h_escape(chapter.title)}</a>'
        f'<span class="sep">/</span><span>{_h_escape(doc.title)}</span>'
        '</div>'
    )

    pager_parts: List[str] = ['<div class="pager">']
    if prev_doc:
        pager_parts.append(
            f'<a class="prev" href="{up}{prev_doc.url}">'
            f'<div class="lbl">← Previous</div>'
            f'<div class="ttl">{_h_escape(filename_label(prev_doc.rel))}</div>'
            f'<div class="sub">{_h_escape(prev_doc.title)}</div></a>'
        )
    else:
        pager_parts.append("<span></span>")
    if next_doc:
        pager_parts.append(
            f'<a class="next" href="{up}{next_doc.url}">'
            f'<div class="lbl">Next →</div>'
            f'<div class="ttl">{_h_escape(filename_label(next_doc.rel))}</div>'
            f'<div class="sub">{_h_escape(next_doc.title)}</div></a>'
        )
    else:
        pager_parts.append("<span></span>")
    pager_parts.append("</div>")
    pager = "".join(pager_parts)

    raw_pane = f'<div class="raw-pane"><pre><code>{_h_escape(doc.raw)}</code></pre></div>'

    status = doc.frontmatter.get("status")
    difficulty = doc.frontmatter.get("difficulty")
    minutes = doc.frontmatter.get("estimated_minutes")
    last_updated = doc.frontmatter.get("last_updated")
    meta_bits: List[str] = []
    if isinstance(status, str) and status:
        meta_bits.append(f'<span class="meta-pill status-{_h_escape(status)}">{_h_escape(status)}</span>')
    if isinstance(difficulty, str) and difficulty:
        meta_bits.append(f'<span class="meta-pill difficulty">{_h_escape(difficulty)}</span>')
    if isinstance(minutes, (int, float)) and minutes:
        meta_bits.append(f'<span class="meta-pill mins">~{int(minutes)} min</span>')
    if isinstance(last_updated, str) and last_updated:
        meta_bits.append(f'<span class="meta-pill date">updated {_h_escape(last_updated)}</span>')
    meta_html = f'<div class="meta-row">{"".join(meta_bits)}</div>' if meta_bits else ""

    body = f"""
<div class="container">
  <div class="layout">
    {sidebar_html(chapter, doc, base_depth)}
    <main>
      <div class="article-head">
        {breadcrumb}
        <h1>{_h_escape(doc.title)}</h1>
        <div><span class="source-path">{_h_escape(doc.rel)}</span></div>
        {meta_html}
        <div class="view-toggle" role="tablist">
          <button data-view="rendered" class="active">Rendered</button>
          <button data-view="raw">Raw Markdown</button>
        </div>
      </div>
      <article class="markdown">{doc.body_html}</article>
      {raw_pane}
      {pager}
    </main>
    {toc}
  </div>
</div>
"""
    return page_shell(doc.title, body, base_depth=base_depth,
                      current_chapter=chapter.slug, with_progress=True)


def build_category_index(chapter: Chapter) -> str:
    base_depth = 1
    up = "../"

    cards: List[str] = []
    for mod in chapter.modules:
        primary = mod.primary
        lede = _h_escape(extract_lede(primary.raw))
        initial = (mod.label[0] if mod.label else "·").upper()
        chip_parts: List[str] = []
        for d in _sort_docs(mod.docs):
            if d.kind == "overview":
                pill_text, cls = "Overview", "page-chip overview"
            else:
                pill_text, cls = d.title, "page-chip"
            chip_parts.append(
                f'<a class="{cls}" href="{up}{d.url}">{_h_escape(pill_text)}</a>'
            )
        chips = "".join(chip_parts)
        parent_path = str(Path(primary.rel).parent)
        path_display = parent_path if parent_path != "." else primary.rel
        cards.append(f"""
<div class="module-card">
  <div class="top">
    <div class="icon-mod">{_h_escape(initial)}</div>
    <div>
      <a href="{up}{primary.url}" class="module-title">
        <div class="ttl">{_h_escape(mod.label)}</div>
      </a>
      <div class="pth">{_h_escape(path_display)}</div>
    </div>
  </div>
  <div class="lede">{lede}</div>
  <div class="pages-row">{chips}</div>
</div>""")

    grid = "".join(cards)

    body = f"""
<section class="category-hero">
  <div class="container">
    <span class="eyebrow"><span>{chapter.icon}</span>
      <span>Chapter {chapter.number} · {_h_escape(chapter.title)}</span>
    </span>
    <h1>{_h_escape(chapter.title)}
      <span class="path-chip">{_h_escape(chapter.path_label)}</span>
    </h1>
    <p>{_h_escape(chapter.subtitle)}</p>
  </div>
</section>
<section>
  <div class="container">
    <div class="module-grid">{grid}</div>
  </div>
</section>
"""
    return page_shell(chapter.title, body, base_depth=base_depth,
                      current_chapter=chapter.slug)


def build_landing(total_docs: int, total_modules: int) -> str:
    total_chapters = len(CHAPTER_LIST)
    chapters_word = NUM_WORDS.get(total_chapters, str(total_chapters))

    cards: List[str] = []
    for ch in CHAPTER_LIST:
        mod_word = "module" if len(ch.modules) == 1 else "modules"
        doc_word = "doc" if len(ch.docs) == 1 else "docs"
        cards.append(f"""
<a class="chapter-card tone-{ch.color}" href="{ch.slug}/index.html">
  <div class="top">
    <div class="icon">{ch.icon}</div>
    <div>
      <div class="num">Chapter {ch.number}</div>
      <h2>{_h_escape(ch.title)}</h2>
      <div class="path-chip">{_h_escape(ch.path_label)}</div>
    </div>
  </div>
  <p>{_h_escape(ch.subtitle)}</p>
  <div class="count">
    <span>{len(ch.modules)} {mod_word} · {len(ch.docs)} {doc_word}</span>
    <span class="arrow">→</span>
  </div>
</a>""")
    cards_html = "".join(cards)

    body = f"""
<section class="hero">
  <div class="hero-inner">
    <span class="hero-eyebrow">Documentation hub</span>
    <h1>Welcome to <span class="grad">Java 0-to-Hero</span>.<br/>
        From zero to lead, one staircase.</h1>
    <p>
      A storybook tour of the Java backend curriculum — beginner through
      lead/staff, plus dedicated interview prep. {chapters_word} chapters,
      {total_modules} modules, {total_docs} documents. Start at
      <strong>Chapter 2 (L0)</strong> if it's your first day with the
      language, or jump to your tier.
    </p>

    <div class="hero-search" id="hero-search">
      <div class="hero-search-box">
        <span class="hero-search-icon">⌕</span>
        <input id="docs-search" type="search" autocomplete="off" spellcheck="false"
               placeholder="Search every doc — try &ldquo;hashmap&rdquo;, &ldquo;G1 GC&rdquo;, &ldquo;volatile&rdquo;&hellip;"
               aria-label="Search documentation" />
        <kbd class="hero-search-kbd">/</kbd>
      </div>
      <div class="hero-search-panel" id="docs-search-panel" hidden>
        <div class="hero-search-status" id="docs-search-status">Loading index…</div>
        <ul class="hero-search-results" id="docs-search-results" role="listbox"></ul>
        <div class="hero-search-foot">
          <span><kbd>↑</kbd><kbd>↓</kbd> navigate</span>
          <span><kbd>↵</kbd> open</span>
          <span><kbd>esc</kbd> close</span>
        </div>
      </div>
    </div>

    <div class="hero-meta">
      <span><span class="dot"></span>{total_docs} documents</span>
      <span><span class="dot" style="background:var(--violet)"></span>{total_modules} modules</span>
      <span><span class="dot" style="background:var(--pink)"></span>{total_chapters} chapters</span>
    </div>
  </div>
</section>
<section class="chapters">
  <div class="container">
    <div class="chapter-grid">{cards_html}</div>
  </div>
</section>
"""
    return page_shell("Welcome", body, base_depth=0)


def build_search_index(docs: List[Doc]) -> List[Dict[str, Any]]:
    out: List[Dict[str, Any]] = []
    for d in docs:
        ch = CHAPTERS[d.chapter]
        out.append({
            "t": d.title,
            "l": filename_label(d.rel),
            "p": d.rel,
            "u": d.url,
            "c": ch["title"],
            "cs": ch["slug"],
            "ci": ch["icon"],
            "co": ch["color"],
            "k": d.kind,
            "b": strip_to_text(d.raw),
        })
    return out


# =============================================================================
# Main
# =============================================================================

CHAPTER_LIST: List[Chapter] = []   # populated in main()


def main() -> int:
    # 1. Validate asset bundle
    style_src = ASSETS_SRC / "style.css"
    app_src = ASSETS_SRC / "app.js"
    if not style_src.is_file() or not app_src.is_file():
        sys.stderr.write(
            f"Missing assets — expected:\n  {style_src}\n  {app_src}\n"
        )
        return 1

    # 2. Build Chapter objects
    global CHAPTER_LIST
    CHAPTER_LIST = [Chapter(
        slug=v["slug"], number=v["number"], title=v["title"],
        path_label=v["path_label"], subtitle=v["subtitle"],
        icon=v["icon"], color=v["color"],
        nav_label=v.get("nav_label") or v["slug"].upper(),
    ) for v in CHAPTERS.values()]
    chapter_by_slug = {c.slug: c for c in CHAPTER_LIST}

    # 3. Discover & classify
    files = find_md_files()
    docs: List[Doc] = []
    doc_by_src: Dict[str, Doc] = {}
    skipped: List[str] = []
    filtered_count = 0

    for src in files:
        rel = str(src.relative_to(ROOT))
        info = classify(rel)
        if not info:
            skipped.append(rel)
            continue
        raw = src.read_text(encoding="utf-8", errors="replace")
        fm, _ = parse_frontmatter(raw)
        # Optional status filter
        if ONLY_COMPLETE and info["kind"] == "page":
            status = fm.get("status")
            if isinstance(status, str) and status != "complete":
                filtered_count += 1
                continue
        title = title_for(rel, raw, fm)
        doc = Doc(
            src=src, rel=rel,
            chapter=info["chapter"],
            module=info["module"],
            module_label=info["module_label"],
            module_order=list(info["module_order"]),
            kind=info["kind"],
            title=title,
            out_rel=info["out_rel"],
            raw=raw,
            frontmatter=fm,
        )
        docs.append(doc)
        doc_by_src[str(src.resolve())] = doc

    # 4. Group into modules and attach to chapters
    modules: Dict[str, Module] = {}
    for d in docs:
        mod = modules.get(d.module)
        if not mod:
            mod = Module(key=d.module, label=d.module_label,
                         chapter=d.chapter, order=list(d.module_order))
            modules[d.module] = mod
        mod.docs.append(d)

    for mod in modules.values():
        chapter_by_slug[mod.chapter].modules.append(mod)

    import functools
    for ch in CHAPTER_LIST:
        ch.modules.sort(key=functools.cmp_to_key(lambda a, b: compare_orders(a.order, b.order)))
        for m in ch.modules:
            ch.docs.extend(m.docs)

    # 5. Render markdown bodies
    print(f"Rendering {len(docs)} docs…", flush=True)
    for i, d in enumerate(docs, 1):
        html_body = render_markdown(d.raw)
        html_body = rewrite_links(html_body, d.src, doc_by_src, d)
        html_body = post_process_html(html_body)
        d.body_html = html_body
        if i % 25 == 0:
            print(f"  {i}/{len(docs)}", flush=True)

    # 6. Wipe and write the output tree
    if OUT_DIR.exists():
        shutil.rmtree(OUT_DIR)
    (OUT_DIR / "assets").mkdir(parents=True)
    shutil.copy2(style_src, OUT_DIR / "assets" / "style.css")
    shutil.copy2(app_src, OUT_DIR / "assets" / "app.js")

    # Search index
    search_index = build_search_index(docs)
    (OUT_DIR / "assets" / "search-index.json").write_text(
        json.dumps(search_index, ensure_ascii=False), encoding="utf-8"
    )

    # Per-doc pages
    for d in docs:
        ch = chapter_by_slug[d.chapter]
        page = build_doc_page(d, ch)
        out_path = OUT_DIR / d.out_rel
        out_path.parent.mkdir(parents=True, exist_ok=True)
        out_path.write_text(page, encoding="utf-8")

    # Category pages
    for ch in CHAPTER_LIST:
        page = build_category_index(ch)
        out_path = OUT_DIR / ch.slug / "index.html"
        out_path.parent.mkdir(parents=True, exist_ok=True)
        out_path.write_text(page, encoding="utf-8")

    # Landing
    total_modules = sum(len(c.modules) for c in CHAPTER_LIST)
    (OUT_DIR / "index.html").write_text(
        build_landing(len(docs), total_modules), encoding="utf-8"
    )

    # Summary
    total_bytes = 0
    for p in OUT_DIR.rglob("*"):
        if p.is_file():
            total_bytes += p.stat().st_size
    print(
        f"\nBuilt dochub/ — {len(docs)} docs, {total_modules} modules "
        f"across {len(CHAPTER_LIST)} chapters ({total_bytes // 1024} KB total)"
    )
    for ch in CHAPTER_LIST:
        print(
            f"  · {ch.slug.ljust(10)} {str(len(ch.modules)).rjust(3)} modules "
            f"/ {str(len(ch.docs)).rjust(3)} docs"
        )
    if filtered_count:
        print(f"Filtered {filtered_count} non-complete topics (DOCHUB_ONLY_COMPLETE=1)")
    if skipped:
        preview = ", ".join(skipped[:8])
        more = " …" if len(skipped) > 8 else ""
        print(f"Skipped {len(skipped)} file(s): {preview}{more}")
    print("\nOpen: dochub/index.html")
    print("Or serve:  python3 -m http.server -d dochub 8000")
    return 0


if __name__ == "__main__":
    sys.exit(main())
