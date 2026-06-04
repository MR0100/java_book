# Java & Backend — The 0-to-Hero Master Book

One book that takes you from *"I've never written code"* all the way to
**senior and tech-lead level** Java backend engineering — including the
interview prep to land the roles along the way.

This is **Java-first and backend-focused**. Beyond the language itself we
cover everything a Java backend engineer or lead is expected to know:
Spring & Spring Boot, databases, APIs (REST, GraphQL, gRPC), system design,
distributed-systems concepts (scaling, load balancing, DNS, caching), and
the leadership craft that separates seniors from leads.

---

## The goal

> Take a complete beginner to **lead/staff-level** competence in Java
> backend engineering, with a single, coherent, hands-on resource — and
> prepare them for interviews at MNCs and FAANGM companies.

We are not writing six disconnected tutorials. We're building **one
staircase** where each step earns the next.

---

## Who it's for

- **Absolute beginners** — start at L0, no prior experience assumed.
- **Working developers** — jump to your level and go deeper.
- **Interview candidates** — every module has interview Q&A, plus a whole
  module (L6) dedicated to FAANGM/MNC interview preparation.

---

## How it's organized

Content is split into **seven modules (L0 → L6)** by experience tier. Each
module is internally divided into recurring **section types** — Concepts,
Tools & Environment, Hands-On, Best Practices & Pitfalls, Interview Prep,
Q&A/FAQ, Cheatsheets, and Resources.

| Module | Tier | Focus |
|--------|------|-------|
| **L0 — Foundations** | Absolute beginner | How programs run, syntax, control flow, methods, arrays |
| **L1 — Core Java & OOP** | Beginner → Junior | OOP, collections, exceptions, generics basics, JUnit intro |
| **L2 — Intermediate & Backend Foundations** | Junior → Mid | Streams/functional, I/O, HTTP, DNS, SQL/JDBC |
| **L3 — Advanced Java & the JVM** | Mid → Senior | Concurrency, memory model, GC, performance, design patterns |
| **L4 — Backend Engineering** | Senior | Spring/Boot, REST/GraphQL, JPA, testing, security, observability, Docker |
| **L5 — Architecture & Leadership** | Lead / Staff | System design, distributed systems, ADRs, mentoring, strategy |
| **L6 — Interview Mastery** | All levels | DSA, LLD/HLD, behavioral + FAANGM company tracks |

> **Interview prep is everywhere.** Each module carries interview questions
> asked at MNCs for that experience level. **L6** is the dedicated, deep
> interview module focused on FAANGM — Flipkart, Apple, Amazon, Netflix,
> Google, Meta.

**The full topic-by-topic plan lives in [CURRICULUM.md](CURRICULUM.md)** —
that's the master index ("phonebook") for the whole book.

---

## How to use it

- **Learning cover-to-cover?** Start at [L0](content/L0-foundations/) and
  walk the staircase.
- **Targeting a level?** Open that module's folder; each has its own index.
- **Looking for one topic?** Use [CURRICULUM.md](CURRICULUM.md) to find
  exactly where it lives.
- **Doing the work?** Every level ends with a **project**. Reading alone
  won't get you there.

---

## Repository layout

```
java-learning/
├── README.md            ← you are here
├── CURRICULUM.md        ← master index / phonebook (generated — every topic + location)
├── TOPIC-CATALOG.md     ← the superset menu of all candidate topics (checklist)
├── CONVENTIONS.md       ← authoring rules + web-ready Markdown format
├── templates/           ← copy-paste templates for new content
├── scripts/             ← skeleton generator (source of truth for structure)
│   └── generate_skeleton.py
├── content/             ← all the modules (each module + section has an index README)
│   ├── L0-foundations/
│   ├── L1-core-java/
│   ├── L2-intermediate-backend/
│   ├── L3-advanced-jvm/
│   ├── L4-backend-engineering/
│   ├── L5-architecture-leadership/
│   └── L6-interview-mastery/
└── assets/              ← images & diagrams
```

> [!NOTE]
> The structure (module/section folders, their index READMEs, and
> `CURRICULUM.md`) is **generated** from `scripts/generate_skeleton.py`.
> To reshape the plan — move a topic, rename a section, change ordering —
> edit that script's data and re-run `python3 scripts/generate_skeleton.py`.
> Per-topic `.md` files are written by hand as content is authored.

---

## Output & roadmap

- **Phase 1 (now):** Author all content in **Markdown**. Structure-first,
  then fill in detailed content module by module.
- **Phase 2 (later):** Render the Markdown into a dedicated **website**
  (HTML or React). All Markdown is written to a strict, script-friendly
  format (see [CONVENTIONS.md](CONVENTIONS.md)) so this conversion is
  largely automated.

> [!IMPORTANT]
> Because the web build comes later, **every** Markdown file must follow
> the frontmatter + formatting rules in [CONVENTIONS.md](CONVENTIONS.md).
> Consistency now is what makes the website cheap to generate later.

---

## Status

Full skeleton established: **7 modules, 74 sections, 371 topics** mapped and
indexed. Content authoring has not started — every topic is `planned`. See
[CURRICULUM.md](CURRICULUM.md) for the complete plan and where each topic
will live.
