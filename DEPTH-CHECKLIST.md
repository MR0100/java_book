# Topic Depth Checklist

The quality bar **every** concept topic must clear before its `status` becomes
`complete`. It exists so the book stays *consistent* and *genuinely useful*:
each topic must teach its subject **completely and deeply** — not just *what*
something is and *how to use it*, but **how it actually works under the hood**
(where data lives and moves in memory and the processor, down to gates and
electricity where that makes it click), with **a diagram for every concept and
process.**

Distilled from — and now deliberately *exceeding* — the reference topic
[L0/C01/T01 · How Computers Run Programs](content/L0-foundations/C01-cs-foundations/T01-how-computers-run-programs-cpu-memory-binary.md).
T01's depth is the **floor**, not the ceiling.

Pair this with [CONVENTIONS.md](CONVENTIONS.md): **CONVENTIONS = how to format**;
**this file = how much substance, mechanism, and visuals.**

> [!IMPORTANT]
> **Depth = coverage + mechanism + visuals. Never word count.** Three things
> together decide "deep enough":
> 1. **Mechanism — "under the hood."** Don't stop at *what* and *how to use*.
>    Explain *how it actually works*: where the data physically lives and how it
>    moves (registers ↔ cache ↔ RAM ↔ disk; source → bytecode → native), what
>    the hardware or runtime does step by step, down to **logic gates and
>    voltage** when that illuminates it.
> 2. **A diagram for everything.** Every significant concept or process gets its
>    **own** accompanying diagram, right where it's explained. Prefer **Mermaid**;
>    use annotated tables or ASCII bit-layouts where they're clearer. *If you
>    explained a process and there is no picture of it, the topic is not done.*
> 3. **Full coverage.** No sub-concept named without being taught.
>
> Length follows scope — a big subject runs long, a small one short — but the
> **mechanism** and **diagram-per-concept** rules apply equally to both. A short
> topic is still deep; it just has fewer concepts (each still gets its diagram +
> mechanism).

---

## How To Use This Checklist

1. **Before writing** — read the chapter's `README.md` (planned topic list) and
   each `prerequisites` topic, so you know what the reader already has.
2. **While writing** — for *each* concept ask: *What is it? Why does it matter?
   How does it work under the hood? Where's the diagram?* Don't move on until all
   four are answered.
3. **Before flipping to `complete`** — run section 8 (Final Review) top to bottom.

---

## What "Fully Detailed" Looks Like

The reference topic is the floor. It opens with *why*, builds bit → gate → CPU →
memory → JVM with **multiple diagrams**, makes the abstract concrete with traces
and analogies, and serves beginner and expert at once via skippable "Going
deeper" sidebars. **Match that, then add more: a diagram beside every concept,
and an explicit "how it works at the machine level" for every mechanism.**

Concretely, a topic at this bar will:

- Trace **data flow** — name where each value lives and how it travels (e.g. a
  literal in source → constant pool → register → ALU → result back to memory).
- Show the **machine-level mechanism** with a picture (e.g. addition → a
  half/full-adder gate diagram; a shift → a barrel-shifter diagram; interpreting
  → the interpreter loop mirroring fetch-decode-execute).
- Have **many diagrams**, not one — typically one per H2/H3 that explains a
  process, often more.

---

## 1. Frontmatter & Identity

- [ ] All YAML fields present and correct (CONVENTIONS §2): `title`, `slug`,
      `level`, `module`, `section`, `type`, `difficulty`, `order`, `tags`,
      `prerequisites`, `status`, `estimated_minutes`, `last_updated`.
- [ ] `slug` is unique book-wide and matches the filename minus its `T##-` prefix.
- [ ] `prerequisites` list the slugs a reader genuinely needs first.
- [ ] `tags` are lowercase and cover the searchable concepts taught.
- [ ] `estimated_minutes` honestly reflects read + practice time.

## 2. Required Structure (CONVENTIONS §10)

- [ ] Exactly one H1, matching the frontmatter `title`.
- [ ] Opening paragraph: **what** the topic is **and why it matters**.
- [ ] Prerequisites callout if any.
- [ ] Body under `##` sections (`###` for sub-points); never skip levels.
- [ ] **Practice**, **Recap**, and **Next** sections.

## 3. Coverage — The "Fully Detailed" Test

- [ ] Every sub-concept implied by the title is **taught**, not name-dropped.
- [ ] Each concept has a **definition**, its **mechanics**, and **why/when** used.
- [ ] At least one **concrete example** per major concept (runnable Java where it
      applies).
- [ ] **Edge cases** and boundary behaviour addressed (limits, overflow,
      empty/null, failure modes).
- [ ] **Common mistakes/misconceptions** called out (`> [!WARNING]`).
- [ ] The reader's **"but what about…?"** follow-ups are anticipated.
- [ ] Connects **backward** (prerequisites) and **forward** (where it's used later).
- [ ] A reader finishing this topic needs **no other source** at their level.

## 4. Under-the-Hood Depth (Mechanism & Data Flow) — REQUIRED

This is the section that separates this book from a tutorial. For the core ideas
of the topic:

- [ ] **Mechanism explained**, not just behaviour — *how* the result is actually
      produced (the hardware circuit, the runtime loop, the algorithm the CPU/JVM
      runs), to the gate/voltage level when it helps.
- [ ] **Data flow named** — where each value lives (register, cache, stack, heap,
      RAM, disk, constant pool, operand stack…) and how it moves between them.
- [ ] **A diagram of the mechanism/flow** sits right beside the explanation.
- [ ] **Layered**: the everyday "what" on top, the "how it really works"
      underneath — so a beginner gets the idea and an expert gets the machinery.
- [ ] Ties back to the **physical model** from T01 (bits, gates, CPU, memory,
      OS) wherever the chain reaches the hardware.

### 4a. Must-Cover for Any Data-Touching Topic

If the topic mentions variables, fields, values, parameters, return values, or
anything that lives in memory or runs on a CPU, **all of these get an explicit
explanation with a diagram** before `complete`:

- [ ] **Byte-level memory layout** — slot index, byte offset within a stack
      frame, object-header bytes, field offset, padding to alignment. Not just
      "lives on the stack"; *where*.
- [ ] **Variable–memory interaction during method calls** — frame setup at call,
      parameter copy (pass-by-value mechanism, *including reference-by-value*),
      return-value slot, frame teardown.
- [ ] **Lifetime** — when each kind of variable is allocated, where, and when
      reclaimed (frame pop for locals, GC for heap objects, class-unload for
      statics).
- [ ] **Architecture dependence** — Java's *fixed* primitive sizes (write-once-
      run-anywhere) vs the underlying CPU's word/register sizes. Show how a JVM
      slot maps to an x86-64 register (`eax`/`rax`) and an ARM64 register
      (`w0`/`x0`). 32-bit JVM vs 64-bit JVM. **Compressed OOPs**, object
      alignment, endianness (bytecode is big-endian; native execution follows
      the host CPU).
- [ ] **Memory efficiency** — actual byte counts. Primitives vs wrappers, arrays
      vs object-per-element where the topic warrants it.
- [ ] **CPU register / cache interaction** where applicable — L1/L2/L3 lines,
      locality of reference, false-sharing pointer (defer detail to concurrency).

> [!IMPORTANT]
> A topic that explains *what* a feature does and *how to use it* — but skips
> these mechanics — fails this checklist. The bar is **language layer + memory
> layer + architecture layer**.

## 5. Visuals — A Diagram for Every Concept (REQUIRED)

- [ ] **Every significant concept or process has its own diagram** placed where
      it's explained (not one diagram for the whole page).
- [ ] **Mermaid** for flows, structures, circuits, pipelines, state, memory
      layouts; **tables** for comparisons/reference; **ASCII bit-layouts** for
      showing bits/bytes in memory.
- [ ] Diagrams are **labelled and accurate** — a reader could follow the process
      from the picture alone.
- [ ] Every code block is **language-tagged**, **correct**, and compiles (mark
      fragments `// ...`). Bigger runnable examples go in `examples/` and are
      linked (CONVENTIONS §4).

## 6. Interview & Practice Value

- [ ] At least one `> [!INTERVIEW]` callout.
- [ ] **Practice**: several exercises, easy → harder, tied to the content —
      including at least one "trace it / predict the output" **or** "trace the
      flow through memory/hardware", and one "explain the mechanism in your own
      words".
- [ ] **Recap** written as concrete learning objectives, **including the
      under-the-hood points** ("explain how … works at the gate/memory level").

## 7. Formatting & Links (CONVENTIONS §3–8)

- [ ] Callouts use GitHub alert syntax: NOTE / TIP / IMPORTANT / WARNING + custom
      INTERVIEW.
- [ ] Internal links are **relative and include the `.md`** extension, using the
      `T##-…` filenames.
- [ ] Headings are Title Case; tables have consistent headers/alignment.

## 8. Final Review (Before `status: complete`)

- [ ] Re-read as a first-time reader at the target level — any gap, leap, or
      undefined term gets fixed.
- [ ] **Every explained process has a diagram**, and every diagram is correct.
- [ ] Mechanism + data-flow covered for the core ideas (section 4 satisfied).
- [ ] Links resolve (or are clearly planned, not-yet-written topics).
- [ ] Set `status: complete` and update `last_updated` in the **topic file's**
      frontmatter (the per-topic source of truth).
- [ ] Refresh indexes: `python3 scripts/generate_skeleton.py`.

> [!NOTE]
> The generator prints **every** topic as `planned` in the index tables
> regardless of real status — it doesn't read authored files. Until that's
> fixed, the indexes understate progress; the topic file's frontmatter is the
> truth. Don't hand-edit the generated tables.

---

## Type Variations

Most topics are `type: concept` and use this whole checklist. Others adapt:

- **exercise / project** — lighter exposition; heavy on section 3 (clear task,
  starter/solution, acceptance criteria) and section 6 (practice).
- **interview-qa** — follows the fixed Q&A structure in CONVENTIONS §9; still
  demands real depth and mechanism in answers.
- **cheatsheet / reference** — terse by design; complete, correct, well-organised
  tables over prose. Sections 4–5 are minimal (but a summary diagram still helps).

---

## Definition Of Done (the one-line bar)

> A topic is `complete` when a reader at its level could learn the subject
> **fully and deeply** from this page alone — *why* it matters, *how to use it*,
> and **how it actually works under the hood** (data flow through memory/CPU,
> down to gates where it helps) — with **a diagram beside every concept**, an
> interview angle, practice, and a mechanism-aware recap, and every box above
> that applies is ticked.
