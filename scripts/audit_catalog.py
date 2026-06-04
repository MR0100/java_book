#!/usr/bin/env python3
"""
Audit: every topic in TOPIC-CATALOG.md should be represented in CURRICULUM.md
(i.e. somewhere in the content structure). Reports catalog topics with no good
match, and content topics that don't map back to the catalog (extras).

Usage: python3 scripts/audit_catalog.py
"""
import os
import re
from difflib import SequenceMatcher

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
CATALOG = os.path.join(ROOT, "TOPIC-CATALOG.md")
CURRIC = os.path.join(ROOT, "CURRICULUM.md")
THRESHOLD = 0.72


def norm(s):
    s = s.lower().replace("`", "")
    s = s.replace("→", " to ").replace("/", " ")
    s = s.replace("&", " and ").replace("+", " plus ")
    s = re.sub(r"\bdsa:\s*", "", s)
    s = re.sub(r"\bcompany track:\s*", "", s)
    s = re.sub(r"[^a-z0-9]+", " ", s)
    return re.sub(r"\s+", " ", s).strip()


def catalog_topics():
    out = []
    for line in open(CATALOG):
        m = re.match(r"\s*- \[[ xX]\]\s+(.*)", line)
        if m:
            out.append(m.group(1).strip())
    return out


def content_topics():
    out = []
    for line in open(CURRIC):
        m = re.match(r"- (?!\[)(.*)", line.rstrip())
        if m:
            out.append(m.group(1).strip())
    return out


def best(target, pool_norm):
    tn = norm(target)
    bestr, bestm = 0.0, None
    for original, n in pool_norm:
        r = SequenceMatcher(None, tn, n).ratio()
        # boost containment
        if tn in n or n in tn:
            r = max(r, 0.9)
        if r > bestr:
            bestr, bestm = r, original
    return bestr, bestm


def main():
    cat = catalog_topics()
    con = content_topics()
    cat_norm = [(t, norm(t)) for t in cat]
    con_norm = [(t, norm(t)) for t in con]

    print("Catalog topics : {}".format(len(cat)))
    print("Content topics : {}".format(len(con)))
    print()

    print("=== CATALOG TOPICS WITH NO GOOD MATCH IN CONTENT ===")
    missing = 0
    for t in cat:
        r, m = best(t, con_norm)
        if r < THRESHOLD:
            missing += 1
            print("  MISSING: {!r}".format(t))
            print("           best={:.2f} -> {!r}".format(r, m))
    if not missing:
        print("  (none)")
    print()

    print("=== CONTENT TOPICS WITH NO GOOD MATCH IN CATALOG (extras) ===")
    extra = 0
    for t in con:
        r, m = best(t, cat_norm)
        if r < THRESHOLD:
            extra += 1
            print("  EXTRA:   {!r}".format(t))
            print("           best={:.2f} -> {!r}".format(r, m))
    if not extra:
        print("  (none)")
    print()
    print("Summary: {} possibly-missing, {} extras".format(missing, extra))


if __name__ == "__main__":
    main()
