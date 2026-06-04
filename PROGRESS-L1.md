# L1 Progress (Parallel Session)

> [!IMPORTANT]
> **Purpose.** This file tracks the **L1 — Core Java & OOP** authoring session
> that runs in parallel with the main L0 session. It exists so the two
> sessions don't fight over [PROGRESS.md](PROGRESS.md). When both sessions
> finish, merge the entries here into the canonical `PROGRESS.md` §3 / §5 /
> §6 / §8 and delete this file.

> [!NOTE]
> **Coordination rules with the L0 session.**
> - **Do not touch** anything inside `content/L0-foundations/`.
> - **Do not touch** `PROGRESS.md` (the L0 session owns it).
> - **Do not run** `python3 scripts/generate_skeleton.py` — it overwrites
>   READMEs and indexes; the L0 session will run it once both sessions stop.
> - Everything authored here lives under `content/L1-core-java/`.

---

## 1. The Mission (L1 Slice)

Author the **L1 — Core Java & OOP** concept topics (49 total across three
chapters) to the same depth bar as the L0 chapters — see
[DEPTH-CHECKLIST.md](DEPTH-CHECKLIST.md) §4 + §4a. Match the reference
quality of the most recent L0 topics
([L0/C02/T13](content/L0-foundations/C02-java-core/T13-method-overloading.md)–[T16](content/L0-foundations/C02-java-core/T16-varargs.md)):
language layer + memory layer + architecture layer, ~700–1100 lines each,
15–25+ Mermaid diagrams, INTERVIEW callouts, Practice (15+ exercises),
mechanism-aware Recap.

## 2. L1 At A Glance

| Chapter | Title | Topics | Complete | % |
|---------|-------|:---:|:---:|:---:|
| C01 | Object-Oriented Programming | 19 | **19** | **100% ✅** |
| C02 | Collections & Core APIs | 23 | **14** | **61%** |
| C03 | Testing Fundamentals | 7 | 0 | 0% |
| **L1 total** | | **49** | **33** | **67%** |

## 3. Per-Chapter Tracker

### L1/C01 — Object-Oriented Programming

| # | Topic | Status |
|---|-------|--------|
| T01 | Classes & objects | **complete** |
| T02 | Fields, methods, constructors, this | **complete** |
| T03 | Encapsulation & access modifiers | **complete** |
| T04 | Inheritance & super | **complete** |
| T05 | Method overriding | **complete** |
| T06 | Polymorphism (compile-time vs runtime) | **complete** |
| T07 | Abstraction & abstract classes | **complete** |
| T08 | Interfaces (default, static, private methods) | **complete** |
| T09 | Object class & its methods | **complete** |
| T10 | equals, hashCode, toString contracts | **complete** |
| T11 | static members, blocks & nested classes | **complete** |
| T12 | Inner, local & anonymous classes | **complete** |
| T13 | enum types (with fields/methods) | **complete** |
| T14 | record types | **complete** |
| T15 | Sealed classes & interfaces | **complete** |
| T16 | Packages & imports | **complete** |
| T17 | Java Module System (JPMS) | **complete** |
| T18 | Object cloning & Cloneable | **complete** |
| T19 | Immutability & immutable class design | **complete** |

### L1/C02 — Collections & Core APIs

| # | Topic | Status |
|---|-------|--------|
| T01 | Collections framework overview | **complete** |
| T02 | List (ArrayList, LinkedList) | **complete** |
| T03 | Set (HashSet, LinkedHashSet, TreeSet) | **complete** |
| T04 | Map (HashMap, LinkedHashMap, TreeMap) | **complete** |
| T05 | Queue, Deque, PriorityQueue, Stack | **complete** |
| T06 | Iterators & Iterable | **complete** |
| T07 | Comparable vs Comparator | **complete** |
| T08 | Collection performance characteristics (Big-O) | **complete** |
| T09 | Exceptions: try/catch/finally, checked vs unchecked | **complete** |
| T10 | Custom exceptions & try-with-resources | **complete** |
| T11 | Generics — basics | **complete** |
| T12 | Generics — bounded types, wildcards, type erasure | **complete** |
| T13 | I/O streams (byte & character) | **complete** |
| T14 | NIO.2 (Path, Files, channels) | **complete** |
| T15–T23 | (see [L1/C02 README](content/L1-core-java/C02-collections-and-core-apis/README.md)) | not-started |

### L1/C03 — Testing Fundamentals

| # | Topic | Status |
|---|-------|--------|
| T01–T07 | (see [L1/C03 README](content/L1-core-java/C03-testing-fundamentals/README.md)) | not-started |

## 4. Current Position — Resume Here

- **Chapter:** `L1/C02` — Collections & Core APIs (9 / 23 topics done).
  **C01 OOP is COMPLETE (19/19 ✅).** **Core-structures arc (T02–T08) is
  COMPLETE** — the chapter is in the core-language/API run (T09–T23).
- **Last finished:** `L1/C02/T09` — Exceptions (try/catch/finally, checked vs unchecked) (2026-06-04).
- **WORKFLOW (user instruction):** author **exactly ONE topic per session**
  for maximum research depth — never batch. Each topic must hit the
  full standard: language + memory (byte counts, struct layouts) +
  architecture (CPU cycle traces, cache/branch behavior, cross-language
  design trade-offs and the "why"). ~10 Mermaid diagrams, INTERVIEW
  callout, 15+ practice exercises, mechanism-aware recap. See memory
  `feedback_one-topic-per-session`.
  **Accuracy note (T11):** static fields live in the `java.lang.Class`
  MIRROR object on the HEAP since JDK 8 — NOT Metaspace, NOT PermGen.
  **README ordering note:** C02 T09–T23 per the C02 README are: T09
  Exceptions, T10 Custom exceptions & try-with-resources, T11 Generics
  basics, T12 Generics (bounded/wildcards/erasure), T13 I/O streams, T14
  NIO.2, T15 java.time, T16 regex, T17 reflection, T18 annotations, T19
  Optional, T20 Math/BigDecimal/BigInteger/Random, T21 serialization, T22
  networking, T23 i18n. (NOT I/O at T09 — that's T13.)
- **Next topic to write:** `L1/C02/T15` →
  `content/L1-core-java/C02-collections-and-core-apis/T15-date-time-api-java-time.md`
  - The **modern temporal library** — one of the best-designed JDK parts;
    a shift from "where data lives" to "what data is." **Language layer**:
    **why the old API was broken** — `java.util.Date` (mutable, most methods
    deprecated, year-1900/month-0 conventions); `Calendar` (mutable,
    **0-based months**, clunky, not thread-safe); **`SimpleDateFormat` is
    NOT thread-safe** (a classic production bug). **`java.time`** (Java 8,
    JSR 310, by Stephen Colebourne, based on **Joda-Time**). The core types:
    **`Instant`** (a point on the timeline — nanos from the 1970 epoch,
    machine/UTC time); **`LocalDate`** (date, no time/zone — a birthday);
    **`LocalTime`**; **`LocalDateTime`** (date+time, NO zone);
    **`ZonedDateTime`** (date+time+zone — a specific instant in a place);
    `OffsetDateTime`; `ZoneId`/`ZoneOffset`; **`Duration`** (machine amount —
    seconds/nanos, between `Instant`s) vs **`Period`** (human amount —
    years/months/days, between `LocalDate`s); **`DateTimeFormatter`**
    (immutable, **thread-safe** — fixes `SimpleDateFormat`). Design
    principles: **IMMUTABLE** (every type → thread-safe, the T19 lesson
    applied; `plusDays`/`withYear` return NEW instances), clear separation
    (machine `Instant` vs human `LocalDateTime` vs zoned), fluent, ISO-8601
    default, 1-based months (`Month` enum — finally fixes Calendar). `Clock`
    (injectable time source for testing). Conversions
    (`atZone`/`toInstant`). **Memory layer**: the byte representation —
    **`Instant` = a `long` epochSecond (8 B) + an `int` nanoOfSecond (4 B)**
    → ~24 B with header; `LocalDate` = `int` year + `short` month + `short`
    day, packed; `LocalDateTime` = `LocalDate` + `LocalTime`;
    `ZonedDateTime` = `LocalDateTime` + `ZoneOffset` + a SHARED interned
    `ZoneId`. All **final fields → JMM safe publication** (T19/T02 freeze).
    Contrast old `Date` = one mutable `long`. **Architecture layer**:
    **immutability → free thread-safety** (no locks; read-only data is
    MESI-Shared across cores with zero invalidation — the T19 multicore
    win); **`DateTimeFormatter` thread-safe** vs `SimpleDateFormat` needing
    a `ThreadLocal`/new instance per call (a real correctness+perf win);
    `Instant` arithmetic = cheap `long` math; the epoch-second+nano split
    packs nanosecond precision in 12 B; **time-zone rules from the IANA tz
    database** (`ZoneRulesProvider` — DST transitions, historical offsets; a
    zone conversion is a rules lookup); java.time ignores leap seconds
    (smoothed UTC). **Cross-language**: the universal **"broken first
    attempt → immutable redesign"** story — **Joda-Time** (the library
    java.time is based on; Colebourne wrote both); **Python `datetime`**
    (naive vs aware = `LocalDateTime` vs `ZonedDateTime`; `zoneinfo`); **C#
    `DateTime`/`DateTimeOffset` + NodaTime** (Jon Skeet's Joda-Time port!) +
    .NET 6 `DateOnly`/`TimeOnly` (catching up to LocalDate/LocalTime);
    **JavaScript `Date`** (notoriously broken → being replaced by **`Temporal`**,
    same immutable principles); Rust `chrono`. **java.time is widely
    considered the GOLD STANDARD** others now emulate. **Common mistakes**:
    using `Date`/`Calendar`/`SimpleDateFormat` in new code; `SimpleDateFormat`
    in a shared field (concurrency corruption → use `DateTimeFormatter`);
    `LocalDateTime` for a timestamp (use `Instant` for machine time);
    `Period` vs `Duration` confusion; forgetting immutability (`plusDays`
    returns a NEW value — must assign); DST arithmetic (add 1 day vs 24
    hours differ across a DST boundary). **Practice**: `Instant` vs
    `LocalDateTime` vs `ZonedDateTime` (when each); immutability (`plusDays`
    returns new); `Duration` between `Instant`s; `Period` between
    `LocalDate`s; `DateTimeFormatter` format+parse (thread-safe); zone
    conversion; DST boundary (1 day vs 24 h); **reproduce the
    `SimpleDateFormat` thread-safety bug** vs `DateTimeFormatter`; `Clock`
    for testable time; `Date`→`Instant`; `Month` enum (1-based); `Instant`
    byte layout via reflection; end-to-end explain-it-back of why java.time
    is thread-safe + the `Instant` byte representation.
    **NOTE:** after T15: T16 regex, T17 reflection, T18 annotations, T19
    Optional, T20 Math/BigDecimal, T21 serialization, T22 networking, T23
    i18n.
- **Immediate next action:** author `L1/C02/T15` (ONE topic this session).
  Then update §2, §3, §4, §6.

## 5. Completed Topics

| Topic file | Title | Completed |
|------------|-------|-----------|
| `L1/C01/T01` · [`T01-classes-and-objects.md`](content/L1-core-java/C01-oop/T01-classes-and-objects.md) | Classes & Objects | 2026-06-04 |
| `L1/C01/T02` · [`T02-fields-methods-constructors-this.md`](content/L1-core-java/C01-oop/T02-fields-methods-constructors-this.md) | Fields, methods, constructors, this | 2026-06-04 |
| `L1/C01/T03` · [`T03-encapsulation-and-access-modifiers.md`](content/L1-core-java/C01-oop/T03-encapsulation-and-access-modifiers.md) | Encapsulation & access modifiers | 2026-06-04 |
| `L1/C01/T04` · [`T04-inheritance-and-super.md`](content/L1-core-java/C01-oop/T04-inheritance-and-super.md) | Inheritance & super | 2026-06-04 |
| `L1/C01/T05` · [`T05-method-overriding.md`](content/L1-core-java/C01-oop/T05-method-overriding.md) | Method overriding | 2026-06-04 |
| `L1/C01/T06` · [`T06-polymorphism-compile-time-vs-runtime.md`](content/L1-core-java/C01-oop/T06-polymorphism-compile-time-vs-runtime.md) | Polymorphism (compile-time vs runtime) | 2026-06-04 |
| `L1/C01/T07` · [`T07-abstraction-and-abstract-classes.md`](content/L1-core-java/C01-oop/T07-abstraction-and-abstract-classes.md) | Abstraction & abstract classes | 2026-06-04 |
| `L1/C01/T08` · [`T08-interfaces-default-static-private-methods.md`](content/L1-core-java/C01-oop/T08-interfaces-default-static-private-methods.md) | Interfaces (default, static, private methods) | 2026-06-04 |
| `L1/C01/T09` · [`T09-object-class-and-its-methods.md`](content/L1-core-java/C01-oop/T09-object-class-and-its-methods.md) | Object class & its methods | 2026-06-04 |
| `L1/C01/T10` · [`T10-equals-hashcode-tostring-contracts.md`](content/L1-core-java/C01-oop/T10-equals-hashcode-tostring-contracts.md) | equals, hashCode, toString contracts | 2026-06-04 |
| `L1/C01/T11` · [`T11-static-members-blocks-and-nested-classes.md`](content/L1-core-java/C01-oop/T11-static-members-blocks-and-nested-classes.md) | static members, blocks & nested classes | 2026-06-04 |
| `L1/C01/T12` · [`T12-inner-local-and-anonymous-classes.md`](content/L1-core-java/C01-oop/T12-inner-local-and-anonymous-classes.md) | Inner, local & anonymous classes | 2026-06-04 |
| `L1/C01/T13` · [`T13-enum-types-with-fields-methods.md`](content/L1-core-java/C01-oop/T13-enum-types-with-fields-methods.md) | enum types (with fields/methods) | 2026-06-04 |
| `L1/C01/T14` · [`T14-record-types.md`](content/L1-core-java/C01-oop/T14-record-types.md) | record types | 2026-06-04 |
| `L1/C01/T15` · [`T15-sealed-classes-and-interfaces.md`](content/L1-core-java/C01-oop/T15-sealed-classes-and-interfaces.md) | Sealed classes & interfaces | 2026-06-04 |
| `L1/C01/T16` · [`T16-packages-and-imports.md`](content/L1-core-java/C01-oop/T16-packages-and-imports.md) | Packages & imports | 2026-06-04 |
| `L1/C01/T17` · [`T17-java-module-system-jpms.md`](content/L1-core-java/C01-oop/T17-java-module-system-jpms.md) | Java Module System (JPMS) | 2026-06-04 |
| `L1/C01/T18` · [`T18-object-cloning-and-cloneable.md`](content/L1-core-java/C01-oop/T18-object-cloning-and-cloneable.md) | Object cloning & Cloneable | 2026-06-04 |
| `L1/C01/T19` · [`T19-immutability-and-immutable-class-design.md`](content/L1-core-java/C01-oop/T19-immutability-and-immutable-class-design.md) | Immutability & immutable class design | 2026-06-04 |
| `L1/C02/T01` · [`T01-collections-framework-overview.md`](content/L1-core-java/C02-collections-and-core-apis/T01-collections-framework-overview.md) | Collections framework overview | 2026-06-04 |
| `L1/C02/T02` · [`T02-list-arraylist-linkedlist.md`](content/L1-core-java/C02-collections-and-core-apis/T02-list-arraylist-linkedlist.md) | List (ArrayList, LinkedList) | 2026-06-04 |
| `L1/C02/T03` · [`T03-set-hashset-linkedhashset-treeset.md`](content/L1-core-java/C02-collections-and-core-apis/T03-set-hashset-linkedhashset-treeset.md) | Set (HashSet, LinkedHashSet, TreeSet) | 2026-06-04 |
| `L1/C02/T04` · [`T04-map-hashmap-linkedhashmap-treemap.md`](content/L1-core-java/C02-collections-and-core-apis/T04-map-hashmap-linkedhashmap-treemap.md) | Map (HashMap, LinkedHashMap, TreeMap) | 2026-06-04 |
| `L1/C02/T05` · [`T05-queue-deque-priorityqueue-stack.md`](content/L1-core-java/C02-collections-and-core-apis/T05-queue-deque-priorityqueue-stack.md) | Queue, Deque, PriorityQueue, Stack | 2026-06-04 |
| `L1/C02/T06` · [`T06-iterators-and-iterable.md`](content/L1-core-java/C02-collections-and-core-apis/T06-iterators-and-iterable.md) | Iterators & Iterable | 2026-06-04 |
| `L1/C02/T07` · [`T07-comparable-vs-comparator.md`](content/L1-core-java/C02-collections-and-core-apis/T07-comparable-vs-comparator.md) | Comparable vs Comparator | 2026-06-04 |
| `L1/C02/T08` · [`T08-collection-performance-characteristics-big-o.md`](content/L1-core-java/C02-collections-and-core-apis/T08-collection-performance-characteristics-big-o.md) | Collection performance (Big-O) | 2026-06-04 |
| `L1/C02/T09` · [`T09-exceptions-try-catch-finally-checked-vs-unchecked.md`](content/L1-core-java/C02-collections-and-core-apis/T09-exceptions-try-catch-finally-checked-vs-unchecked.md) | Exceptions: try/catch/finally, checked vs unchecked | 2026-06-04 |
| `L1/C02/T10` · [`T10-custom-exceptions-and-try-with-resources.md`](content/L1-core-java/C02-collections-and-core-apis/T10-custom-exceptions-and-try-with-resources.md) | Custom exceptions & try-with-resources | 2026-06-04 |
| `L1/C02/T11` · [`T11-generics-basics.md`](content/L1-core-java/C02-collections-and-core-apis/T11-generics-basics.md) | Generics — basics | 2026-06-04 |
| `L1/C02/T12` · [`T12-generics-bounded-types-wildcards-type-erasure.md`](content/L1-core-java/C02-collections-and-core-apis/T12-generics-bounded-types-wildcards-type-erasure.md) | Generics — bounded types, wildcards, type erasure | 2026-06-04 |
| `L1/C02/T13` · [`T13-i-o-streams-byte-and-character.md`](content/L1-core-java/C02-collections-and-core-apis/T13-i-o-streams-byte-and-character.md) | I/O streams (byte & character) | 2026-06-04 |
| `L1/C02/T14` · [`T14-nio-2-path-files-channels.md`](content/L1-core-java/C02-collections-and-core-apis/T14-nio-2-path-files-channels.md) | NIO.2 (Path, Files, channels) | 2026-06-04 |

## 6. Session Log (newest first)

### 2026-06-04 (L1/C02/T14 — NIO.2; Java's complete file story; one topic)

- Authored `L1/C02/T14` NIO.2 (Path, Files, channels) — **297 lines, 10
  Mermaid diagrams, 12 H2, 13 interview questions, 7 warning callouts, 15
  practice exercises.** The modern filesystem API above the T13 streams;
  **T13+T14 = Java's complete file story** (streams for the bytes, NIO.2
  for the filesystem). **Language layer**: why **`java.io.File` was poor**
  (boolean returns lose the failure reason; no symlinks; thin metadata;
  no lazy/recursive walk; no atomic ops) → **NIO.2** (Java 7, JSR 203).
  **`Path`** (immutable, abstract, touches no disk until used) —
  `of`/`resolve`/`relativize`/`normalize`. **`Files`** static utility —
  the conveniences wrapping T13 (`readString`/`lines`/`readAllBytes`/
  `writeString`/`newBufferedReader` — **UTF-8 by DEFAULT, fixing the
  FileReader trap**); `copy`/`move`/`delete` (+ options, throw with
  detail); `createDirectories`; query/attributes. **Directory traversal**
  — `list` (one level), **`walk`** (lazy recursive Stream), `find`,
  **`walkFileTree`** (`FileVisitor` — recursive delete/copy);
  `WatchService` (change notify); pluggable `FileSystem`s (ZIP). **Channels
  & ByteBuffer** — `FileChannel`, the **fill→flip→drain→clear** cycle,
  `ByteOrder` (endianness). **Memory layer**: `Path` = small immutable
  object (`UnixPath` a `byte[]`); **`ByteBuffer` HEAP** (`byte[]` on GC
  heap) **vs DIRECT** (`allocateDirect` — off-heap, Cleaner-freed, leak
  risk) **vs MAPPED** (the file's pages). **Architecture layer (THE
  story)**: a **heap-buffer read forces an extra COPY** (a moving GC can't
  be DMA'd into → stage off-heap → copy to heap); a **direct buffer is
  off-heap so the OS DMAs straight in — no copy** (why Netty uses them);
  **zero-copy `transferTo`/`sendfile`** (file→socket in the kernel,
  skipping user space — diagrammed, the T13 zero-copy); **memory-mapped
  files** (`FileChannel.map` → file pages in virtual memory, lazy page
  faults via the page cache — databases/Lucene). **Cross-language**: the
  universal **string-paths → OO-`Path`-object** evolution — **Python
  `pathlib`** (the direct twin: `/` operator, `read_text`) + `mmap`; C#
  `Path`/`FileInfo` + `MemoryMappedFile`; Rust `Path`/`PathBuf`; Go
  `path/filepath`; `mmap` universal; NIO.2 (2011) = Java catching up.
  **Common mistakes** (6): still using `File`; **not closing the
  `Files.lines`/`walk` Stream** (FD leak — try-with-resources);
  `resolve`/`resolveSibling`; `relativize` same-type rule; `readAllBytes`
  on a huge file (OOM); direct-buffer leak. 15th practice = end-to-end
  heap-vs-direct-vs-mapped copy-avoidance trace. Recap ties to T08/T13
  copy-avoidance. L1 progress now **33/49** (67%); C02 chapter **14/23**
  (61%).
- **Next action:** author `L1/C02/T15` Date/Time API (java.time) — the
  modern temporal library: why Date/Calendar/SimpleDateFormat were broken,
  the Instant/LocalDate/LocalDateTime/ZonedDateTime/Duration/Period model,
  immutability → free thread-safety (T19 applied), the Instant byte
  representation, IANA tz rules, and the cross-language broken→immutable
  story (Joda-Time, Python datetime, NodaTime, JS Temporal); see §4 brief.
  ONE topic next session.

### 2026-06-04 (L1/C02/T13 — FIRST core-API topic; I/O streams; one topic)

- Authored `L1/C02/T13` I/O streams (byte & character) — **285 lines, 10
  Mermaid diagrams, 11 H2, 13 interview questions, 7 warning callouts, 15
  practice exercises.** The **first core-API topic** — shift from language
  facilities to LIBRARIES; moving bytes/chars across boundaries.
  **Language layer**: the **two parallel hierarchies** —
  `InputStream`/`OutputStream` (raw **bytes**, binary) vs `Reader`/`Writer`
  (**characters**, charset-aware); **why the split** (a char isn't a byte —
  needs a charset to decode; UTF-8 ASCII 1 B / others 2–4); the **bridge
  classes** `InputStreamReader`/`OutputStreamWriter` (the byte↔char
  boundary); the **`FileReader`/`FileWriter` default-charset TRAP**
  (platform-dependent garbling — always specify UTF-8); the **DECORATOR
  pattern** (`BufferedReader`→`InputStreamReader`→`FileInputStream` — each
  layer wraps + adds one capability; java.io = the textbook GoF example);
  buffering; **`read()` returns `int`** (−1 EOF distinct from byte 0xFF);
  `System.in/out/err`; try-with-resources (T10); modern convenience
  (`Files.readString`/`transferTo`/`readAllBytes` — T14 forward).
  **Memory layer**: a bare stream is tiny (a file descriptor, no content);
  **`BufferedInputStream` holds an 8 KB `byte[]` buffer** (`BufferedReader`
  a 16 KB `char[]`) + pos/count; the decorator chain = small wrapper
  objects; the String-chars-vs-disk-bytes charset asymmetry. **Architecture
  layer (THE story)**: **buffering = syscall amortization** — an unbuffered
  `read()` is a **syscall** (user→kernel trap, ~hundreds–thousands of
  cycles before any device access), so 1 MB byte-by-byte = **~1,000,000
  syscalls** = catastrophic; an 8 KB `BufferedInputStream` → **~128
  syscalls** (~8000× fewer). **The SAME batch-to-amortize pattern as cache
  lines + ArrayList geometric growth** (T08), now at the OS boundary;
  the latency hierarchy extended (RAM ~100 ns / SSD ~100 µs / HDD ~10 ms);
  the OS page cache (avoids the disk, not the syscall); `transferTo`
  zero-copy (`sendfile`). **Cross-language**: the **byte/text split is
  universal** — Python `open()` = `TextIOWrapper(BufferedReader(FileIO))`
  = EXACTLY Java's 3-layer stack; C# `Stream`+`StreamReader`; **C/Python
  buffer by DEFAULT** (`setvbuf`), **Java makes you opt in** (the footgun);
  java.io is the canonical Decorator example. **Common mistakes** (6): not
  buffering; default charset; not closing (FD leak); `read()` in a `byte`
  (loses EOF); forgetting to flush; bytes-as-text (mojibake). 15th practice
  = end-to-end why-1M-syscalls-becomes-128. Recap ties buffering to T08. L1
  progress now **32/49** (65%); C02 chapter **13/23** (57%).
- **Next action:** author `L1/C02/T14` NIO.2 (Path, Files, channels) — the
  modern filesystem API above these streams: `Path` (replaces `File`),
  `Files` conveniences (readString/lines/walk/copy — UTF-8 by default),
  directory traversal + `WatchService`, and channels/`ByteBuffer`
  (heap vs direct, memory-mapped files, zero-copy DMA); see §4 brief. ONE
  topic next session. T13+T14 = Java's complete file story.

### 2026-06-04 (L1/C02/T12 — advanced generics; generics pair + language-facilities run COMPLETE; one topic)

- Authored `L1/C02/T12` Generics — bounded types, wildcards, type erasure
  — **313 lines, 10 Mermaid diagrams, 11 H2, 13 interview questions, 7
  warning callouts, 15 practice exercises.** The advanced generics half —
  turns `<T>` into a tool for flexible, type-safe APIs; **completes the
  generics pair (T11+T12)**. **Language layer**: **bounded type parameters**
  (`<T extends Number>` → call Number methods; **multiple bounds** class
  first; recursive `<T extends Comparable<T>>`). **The invariance problem**
  — `List<String>` is NOT a `List<Object>` (allowing it → `add` an Integer
  through the alias → corruption), so generics are INVARIANT + compile-time
  checked; **the array-covariance hole** — arrays ARE covariant
  (`Object[] = String[]`) and pay with a RUNTIME `ArrayStoreException` +
  per-store check (the Java 1.0 mistake generics fixed). **Wildcards**
  (`?` / `? extends T` covariant producer / `? super T` contravariant
  consumer) + **PECS** (Producer Extends, Consumer Super) with
  `Collections.copy(dest ? super T, src ? extends T)` + the get/put matrix.
  **Memory layer**: a **bounded** param erases to its **BOUND not Object**
  (`<T extends Number>` → Number → call `doubleValue()` with no cast,
  leaner bytecode; multiple bounds → leftmost). **Bridge methods (THE
  erasure subtlety)** — `Comparable<MyInt>.compareTo(MyInt)` vs the erased
  interface `compareTo(Object)` mismatch → compiler synthesizes a
  `compareTo(Object)` **bridge** (`ACC_BRIDGE | ACC_SYNTHETIC`) that
  casts + forwards, preserving polymorphism through erasure (T06/T07;
  visible in `javap`). **Architecture layer**: bounds → **fewer casts**;
  **bridge → one indirection, JIT-inlined → ~free**; **can't catch a
  generic exception / extend Throwable generically** (needs a reifiable
  type; erasure can't tell `MyEx<A>` from `MyEx<B>`); **wildcard capture**
  (CAP#1 → private `<T>` helper); **heap pollution + `@SafeVarargs`**
  (generic varargs → `Object[]`). **Cross-language (the big axis):
  use-site vs declaration-site variance** — **Java = USE-SITE wildcards**
  (`? extends`/`? super` per use; flexible, verbose, retrofit-friendly) vs
  **C# `out`/`in`, Kotlin `out`/`in`, Scala `+T`/`-T` = DECLARATION-SITE**
  (once on the type; concise, fixed; Kotlin has both); C++ invariant +
  duck-typed (concepts C++20); Rust trait bounds + inferred variance.
  **Common mistakes** (6): assuming `List<String>` is a `List<Object>`;
  PECS backwards; `add` to `? extends`; raw types to silence wildcard
  errors; multiple-bound order; catching a generic exception / unchecked
  generic varargs. 15th practice = end-to-end PECS + bridge-method trace.
  Recap completes the generics pair + the language-facilities run. L1
  progress now **31/49** (63%); C02 chapter **12/23** (52%).
- **🎯 GENERICS PAIR (T11+T12) + LANGUAGE-FACILITIES RUN (exceptions T09–10,
  generics T11–12) COMPLETE.** C02 now turns to the **core-API libraries**
  (T13–T23), starting with I/O.
- **Next action:** author `L1/C02/T13` I/O streams (byte & character) —
  the first core-API topic: the InputStream/OutputStream vs Reader/Writer
  split + the charset boundary, the DECORATOR pattern (BufferedReader→
  InputStreamReader→FileInputStream), and **buffering as the
  memory-hierarchy lesson at the syscall/disk level** (1M syscalls → 128);
  see §4 brief. ONE topic next session.

### 2026-06-04 (L1/C02/T11 — generics fundamentals; type erasure; one topic)

- Authored `L1/C02/T11` Generics — basics — **324 lines, 10 Mermaid
  diagrams, 11 H2, 13 interview questions, 6 warning callouts, 15 practice
  exercises.** Opens the type-parameter system under every `List<String>`/
  `Map<K,V>`/`Comparator<T>` used since T01. **Language layer**: the
  **problem generics solve** (pre-1.5 raw collections → cast on every
  `get` + wrong-type `add` → runtime `ClassCastException`; generics move
  the error to COMPILE time + eliminate casts); **generic classes**
  (`Box<T>`), **type parameter vs type argument**, the **diamond** (Java
  7), **generic methods** (`<T> T m()` + type witness), multiple params,
  `var` + inference; **raw types** as a backward-compat trap (use `List<?>`
  not raw `List`); naming conventions. **Memory layer (THE key fact): TYPE
  ERASURE** — generics are compile-time only; the compiler erases each
  parameter to its bound (`Object` unbounded) + inserts casts, so
  `List<String>`/`List<Integer>` are the **SAME runtime class**
  (`getClass()` identical — proven) and an instance carries ZERO extra
  memory for its type arg; the `.class` keeps generics in a **`Signature`
  attribute** (reflection/separate-compilation) while the bytecode runs
  erased. **Architecture layer**: erasure → **zero runtime cost** (a
  `get()` is raw `get()` + one **synthetic `checkcast`** — the cast you no
  longer write, provably correct); **one class = no code bloat**; the
  **price of no reification** — can't `new T[]`, `instanceof List<String>`,
  `T.class`, or overload on type args; **Java chose erasure for MIGRATION
  COMPATIBILITY** (2004 — generic + raw code had to interoperate; a
  diagram traces the decision). **Cross-language**: the **erasure /
  reification / monomorphization spectrum** — C++/Rust **monomorphize** (a
  class per type — fast, specializable, code bloat); **C# reifies** (CLR
  knows `List<int>` → `typeof(T)`, `new T[]`, **unboxed value types** — a
  ~5× memory win over Java's boxed `List<Integer>`, diagrammed); Java/Scala
  **erase** (one class, migration-friendly, no runtime type info);
  **Kotlin** softens it with `reified` type params in `inline` funs (the
  escape hatch plain Java lacks). **Common mistakes** (6): raw types;
  generic array creation `new T[]`; `instanceof List<String>`; expecting
  `T.class`; assuming parameterizations are distinct classes; ignoring
  unchecked warnings (heap pollution). 15th practice = end-to-end
  `List<String>.get()` erasure + synthetic-checkcast trace. Recap sets up
  T12. L1 progress now **30/49** (61%); C02 chapter **11/23** (48%).
- **Next action:** author `L1/C02/T12` Generics — bounded types, wildcards,
  type erasure (the advanced half): bounded params + multiple bounds, the
  invariance problem (`List<String>` ≠ `List<Object>`) + the array
  covariance hole, wildcards (`? extends`/`? super`) + **PECS**, bounded
  erasure + **bridge methods**, can't-catch-generic, `@SafeVarargs`, and
  use-site (Java) vs declaration-site (C#/Kotlin/Scala) variance; see §4
  brief. ONE topic next session — it completes the generics pair + the
  language-facilities run; T13 (I/O streams) begins the core-API topics.

### 2026-06-04 (L1/C02/T10 — error-handling pair complete; one topic)

- Authored `L1/C02/T10` Custom exceptions & try-with-resources — **343
  lines, 10 Mermaid diagrams, 12 H2, 13 interview questions, 6 warning
  callouts, 15 practice exercises.** The practical other half of error
  handling; **completes the T09+T10 pair**. **Language layer**: **reuse a
  standard exception vs define a custom one** (EJ Item 72 — reuse
  IllegalArgument/IllegalState/NPE/UnsupportedOperation where they fit;
  go custom only for data/a catchable type/a library family);
  **designing a custom exception** (extend RuntimeException-unchecked-
  default or Exception-checked; the standard constructors esp. the
  chaining `(String, Throwable)` — T09; `serialVersionUID` since Throwable
  is Serializable — T21; context fields + getters). **`AutoCloseable`**
  (Java 7, `close() throws Exception`) vs **`Closeable`** (Java 5, `throws
  IOException`, idempotent). **`try`-with-resources** — automatic close at
  block exit (normal OR exceptional), **reverse order** (LIFO — later
  resources wrap earlier ones, BufferedWriter→FileWriter must flush
  first), multiple + Java 9 effectively-final resources. **The
  suppressed-exceptions mechanism (THE payoff)** — body exception A stays
  PRIMARY and propagates, `close()`'s exception B is **suppressed** via
  `addSuppressed`/`getSuppressed` (the exact fix for the old `try`/
  `finally` MASKING bug T09 warned about, where B overrode A). **Memory
  layer**: custom exception = Throwable layout (T09) + context fields;
  `serialVersionUID` is static (zero per-instance); the
  `suppressedExceptions` list is **lazily allocated** (shared empty
  sentinel → ArrayList only on first `addSuppressed`), so no-suppression
  costs nothing. **Architecture layer**: **`try`-with-resources DESUGARS**
  to a `try`/`finally` with a primary-exception local + conditional
  `close()`+`addSuppressed` — **the compiler emits the correct
  suppress-not-mask idiom humans wrote wrong**; zero-cost exception table
  on the happy path (T09); multiple resources **nest** → reverse-order
  close falls out; `close()` via `invokeinterface`; the `!= null` guard
  skips close if acquisition threw. **Cross-language**: the **RAII vs
  explicit-cleanup split follows from GC** — **C++ destructors / Rust
  `Drop`** run automatically on scope exit (no GC → deterministic
  lifetimes → the gold standard, no `close` needed); **Java
  try-with-resources / C# `using` / Python `with`** are the **tracing-GC
  approximation** (GC finalizes non-deterministically → can't ride on
  destruction → explicit scope-bound construct; Java's deprecated
  `finalize()` = the failed RAII-via-GC attempt); Go `defer` (LIFO at
  function return). **Common mistakes** (6): manual `finally`-close (mask
  bug + leaks); reinventing standard exceptions; missing `(String,
  Throwable)` ctor; expecting `getSuppressed` to be primary;
  non-idempotent `close`; over-deep custom hierarchies. 15th practice =
  end-to-end try-with-resources desugaring + primary/suppressed + reverse
  order. Recap completes the error-handling foundation. L1 progress now
  **29/49** (59%); C02 chapter **10/23** (43%).
- **Next action:** author `L1/C02/T11` Generics basics — the type-parameter
  system under every `List<String>`/`Map<K,V>`; generic classes/methods,
  the diamond, compile-time safety, raw types, and **type erasure** (the
  compile-time-only nature → one runtime class, synthetic checkcasts,
  migration-compatibility reason) + cross-language erasure-vs-reification;
  see §4 brief. ONE topic next session. T12 is the advanced generics half.

### 2026-06-04 (L1/C02/T09 — error handling; opens the core-language/API run; one topic)

- Authored `L1/C02/T09` Exceptions: try/catch/finally, checked vs
  unchecked — **342 lines, 10 Mermaid diagrams, 13 H2, 13 interview
  questions, 7 warning callouts, 15 practice exercises.** Opens the
  T09–T23 core-language/API run; the error-handling model under every API
  ahead (callbacks to the collections' CME T06 / NoSuchElement T05 /
  ClassCast T07). **Language layer**: the **`Throwable` hierarchy**
  (`Error` unrecoverable / `RuntimeException` unchecked-bugs / other
  `Exception` checked-recoverable); `try`/`catch`/`finally` (most-specific
  catch first) + **multi-catch** (`A | B`, effectively-final common
  supertype); the **checked-vs-unchecked "catch or declare"** compiler
  rule + Bloch's recoverable-vs-programming-error guideline; **the great
  checked-exception debate** (for: failure in the signature; against:
  don't scale / `throws Exception` / swallowing / break lambdas+streams —
  C#/Kotlin dropped them; honest both-sides); **exception chaining**
  (cause constructor / `getCause` / "Caused by:" — translate the
  abstraction without losing the trace); **`finally` semantics** + the
  **return/throw-in-`finally` trap** (silently swallows the pending
  exception). **Memory layer**: a `Throwable` IS an object (message +
  cause + `stackTrace` + suppressed list); the **`StackTraceElement[]` is
  captured at CONSTRUCTION by native `fillInStackTrace()`** (~40 B/frame)
  — paid at `new`, not `throw`. **Architecture layer (THE mechanism)**:
  **`try`/`catch` compiles to an EXCEPTION TABLE** consulted only on a
  throw → **zero-cost try** on the happy path; **throwing is cheap
  (goto-like jump + unwind), CONSTRUCTING is expensive** (the
  `fillInStackTrace` walk) → exceptions-as-control-flow is the
  anti-pattern; HotSpot **"fast throw"** strips traces from hot implicit
  exceptions (`-XX:-OmitStackTraceInFastThrow` restores them); **stack
  unwinding** pops frames running `finally`/releasing monitors until a
  matching handler. **Cross-language**: C++ (no checked; **RAII instead of
  `finally`**; specs removed in C++17 — the lesson critics cite); Python
  (idiomatic exceptions-as-control-flow — **EAFP**, `StopIteration`); C#
  (rejected checked; `using`); **Rust (`Result<T,E>` + `?` — checked-errors
  in the type system, composable, "checked exceptions done right")**; Go
  (`error` returns + panic/recover) — the exceptions-vs-errors-as-values
  divide, with Rust vindicating Java's underlying idea. **Common mistakes**
  (6): swallowing (empty catch); catching `Throwable`/`Exception` too
  broadly (catches `Error`); return/throw in `finally`; losing the cause;
  exceptions for control flow; catch-log-rethrow double logging. 15th
  practice = end-to-end throw→handler trace (construct/fillInStackTrace →
  athrow → exception-table search per frame → finally → handler). Recap
  ties to T10 (custom exceptions + try-with-resources). L1 progress now
  **28/49** (57%); C02 chapter **9/23** (39%).
- **Next action:** author `L1/C02/T10` Custom exceptions & try-with-
  resources — when to define vs reuse a standard exception, `AutoCloseable`/
  `Closeable`, try-with-resources + reverse-order close + the
  **suppressed-exceptions** mechanism (the fix for the `finally`-masks-the-
  real-exception bug T09 warned about), the desugaring, cross-language
  RAII/`with`/`using`/`Drop`/`defer`; see §4 brief. ONE topic next session.

### 2026-06-04 (L1/C02/T08 — PERFORMANCE CAPSTONE; core-structures arc T02–T08 COMPLETE; one topic)

- Authored `L1/C02/T08` Collection Performance Characteristics (Big-O) —
  **301 lines, 10 Mermaid diagrams + 2 major cost tables (master + space),
  11 H2, 13 interview questions, 7 warning callouts, 15 practice
  exercises.** The chapter's **performance capstone** — synthesizes
  T02–T05 into a decision tool. **Language layer**: what Big-O measures
  (growth of op *count* as n→∞, constants dropped) and what it HIDES
  (constant factors + the finite n you run at); the **master cost table**
  (add/get/contains/remove/iterate × ArrayList/LinkedList/ArrayDeque/
  HashMap/LinkedHashMap/TreeMap/PriorityQueue) with every entry tied to
  its T02–T05 mechanics; **amortized vs worst-case** (ArrayList.add
  amortized O(1)/O(n)-resize; HashMap O(1)-avg/O(log n)-treeified-worst —
  T04); the **decision framework** (key→value? sorted? membership? index/
  ends/smallest-first? → the right collection). **Memory layer**: space
  Big-O + per-element overhead (ArrayList ~4–8 B, LinkedList 24-B Node,
  HashMap 32-B Node, TreeMap ~40-B node) + the ~5–10× boxing tax;
  amortized analysis of geometric resizing (geometric series ~3n → O(1)
  amortized; +1 growth would be O(n²)). **Architecture layer (THE
  thesis)**: **Big-O counts operations, the memory hierarchy counts time**
  — a cache miss ≈ 300 instructions, so the constant factor (layout)
  dominates wall-clock until n is large; same-O(n)-50–100×-apart
  (ArrayList prefetched stride-1 vs LinkedList pointer-chase); **ArrayList
  beats LinkedList at mid-list insert despite Big-O** (Stroustrup — memcpy
  at bandwidth vs O(n) cache-missing walk); **memory-latency-bound "O(1)"**
  (HashMap.get cold = 3 cache misses ~150–300 cyc — T04). **Cross-
  language**: asymptotics are universal (same structures → same Big-O
  everywhere) but constants are local — Rust/C++/Go open-addressing
  SwissTable maps beat Java's separate chaining, B-tree vs red-black, at
  the same O — so memorize the table but measure the constant in your
  language. **Common mistakes** (6): choosing by Big-O without measuring;
  treating all O(n) equal; ignoring amortized-vs-worst tail latency
  (pre-size); ArrayList.contains-in-a-loop O(n²) (use HashSet); assuming
  HashMap always O(1); ignoring space. 15th practice = end-to-end
  explain-it-back of ArrayList-beats-LinkedList-despite-Big-O. Recap
  closes the core-structures arc. **Also corrected T08's forward refs:
  T09 is EXCEPTIONS, not I/O (I/O is T13) — verified against the C02
  README.** L1 progress now **27/49** (55%); C02 chapter **8/23** (35%).
- **🎯 CORE-STRUCTURES ARC (C02 T02–T08) COMPLETE** — List, Set, Map,
  Queue/Deque/heap, iteration, ordering, and the Big-O synthesis, all at
  language+memory+architecture depth. C02 now pivots to core-language/API
  topics (T09–T23).
- **Next action:** author `L1/C02/T09` Exceptions (try/catch/finally,
  checked vs unchecked) — the Throwable hierarchy, the checked-exception
  debate, exception chaining, the fillInStackTrace cost + zero-cost-try
  mechanism, Result-vs-exception cross-language; see §4 brief. ONE topic
  next session.

### 2026-06-04 (L1/C02/T07 — the ordering protocol; one topic)

- Authored `L1/C02/T07` Comparable vs Comparator — **348 lines, 10
  Mermaid diagrams, 13 H2, 13 interview questions, 8 warning callouts, 15
  practice exercises.** The ordering protocol that `TreeSet`/`TreeMap`
  (T03/T04), `PriorityQueue` (T05), and every `sort` consume.
  **Language layer**: **`Comparable<T>`** (one `compareTo` — a type's ONE
  natural order; `String`/`Integer`/`LocalDate`/`BigDecimal`/enums) vs
  **`Comparator<T>`** (`compare`, a `@FunctionalInterface` — any number of
  external orders). **The total-order contract** (sign convention,
  antisymmetry, transitivity, consistency). **The `a - b` subtraction
  pitfall** (int overflow inverts the order → use `Integer.compare`).
  **Consistency-with-`equals`** (recommended, NOT required) and the
  consequence that `TreeSet`/`TreeMap` decide duplicates by the *order*,
  not `equals` — the canonical `BigDecimal("1.0")`/`("1.00")` (equal by
  `compareTo`, unequal by `equals` → HashSet keeps 2, TreeSet keeps 1),
  plus the `Double`/`NaN`/`-0.0` total-order-inconsistent-with-`==`
  parallel. **The Java 8 combinators** (`comparing`/`thenComparing`/
  `reversed`/`naturalOrder`/`nullsFirst`/boxing-free `comparingInt`). Who
  consumes ordering (sort/Tree*/PriorityQueue/min-max). **Memory layer**:
  `Comparable` adds **no instance state** (one vtable method); a
  `Comparator` IS an object — a lambda (`invokedynamic`, often cached — T12)
  or a `thenComparing` **linked chain of ~16–32-B wrapper objects** (built
  once, reused for all comparisons). The sort footprint: `Arrays.sort`
  primitives = **dual-pivot quicksort** (in place, no comparator, no
  boxing); objects/`List.sort` = **TimSort** (stable, adaptive, temp
  buffer ≈ n/2 refs) — split because primitives have no identity
  (stability moot) while objects are equal-but-distinct + need stability +
  O(n log n) worst case. **Architecture layer**: the comparator is the
  **hot path** (O(n log n) comparisons) — monomorphic site → JIT inlines
  `compare` to field-loads + int-compare; megamorphic → real dispatch;
  **`comparingInt` avoids a boxed `Integer` per comparison**; TimSort run
  detection + galloping → near-O(n) on partially-sorted, cache-friendly
  merges. **Cross-language**: C++ strict-weak-ordering (UB if violated —
  worse than Java's exception) + introsort/stable_sort; Python `__lt__` +
  `key=` (the decorate-sort-undecorate model `comparing` mirrors; CPython
  *originated* TimSort); C# `IComparable`/`IComparer`/`Comparison`; **Rust
  `Ord` vs `PartialOrd` — `f64` is only `PartialOrd` because `NaN` is
  unordered, so `vec_of_f64.sort()` won't compile** (the type system
  forcing the NaN question `Double.compare` resolves by fiat);
  `#[derive(Ord)]` = a `thenComparing` chain at the language level.
  **Common mistakes** (6): `a-b` overflow; inconsistent-with-`equals` in a
  Tree* (BigDecimal / by-one-field drop); non-total-order → TimSort's
  *"Comparison method violates its general contract!"*; mutating a sort
  key while in a TreeSet (lost, like mutable `hashCode` — T03);
  `comparing` boxing where `comparingInt` fits; `reversed()` placement.
  15th practice = end-to-end `thenComparing` chain evaluation + hot-path
  inlining trace. Recap ties ordering to T03/T04/T05 + T10 equals. L1
  progress now **26/49** (53%); C02 chapter **7/23** (30%).
- **Next action:** author `L1/C02/T08` Collection Performance
  Characteristics (Big-O) — the chapter's performance capstone: the
  comparative cost table across all structures, amortized-vs-worst-case,
  the constant-factor/cache thesis, the decision framework; see §4 brief.
  ONE topic next session — it CLOSES the core-structures arc; T09+ begins
  the core APIs (check the C02 README for T09–T23 order).

### 2026-06-04 (L1/C02/T06 — the traversal protocol; pivot from structures to cross-cutting; one topic)

- Authored `L1/C02/T06` Iterators & Iterable — **421 lines, 10 Mermaid
  diagrams, 15 H2, 13 interview questions, 7 warning callouts, 15
  practice exercises.** The uniform traversal protocol under every
  `for-each` — pivots the chapter from "the structures" (T02–T05) to the
  cross-cutting concerns. **Language layer**: the **`Iterable<T>`**
  (source, `iterator()` factory) / **`Iterator<T>`** (cursor:
  `hasNext`/`next`/`default remove`) split; every `Collection` is
  `Iterable`, a `Map` is not (its views are — T04). **How `for-each`
  desugars** — over a collection → `Iterator it = c.iterator(); while
  (it.hasNext()) { e = it.next(); … }`; over an **array → a plain indexed
  loop, no iterator object** (the compiler special-cases arrays, JLS
  §14.14.2). **`Iterator.remove()`** (the only safe mid-loop deletion —
  it re-syncs `expectedModCount`) + **`removeIf`** (Java 8 idiom,
  `ArrayList` batch-compacts). **`ListIterator`** (bidirectional
  `previous`/`hasPrevious`, positional `nextIndex`/`previousIndex`,
  in-place `set`/`add` — the LinkedList sweet spot, T02). **`Spliterator`**
  (`tryAdvance`/`trySplit`/`characteristics`/`estimateSize` — the
  splittable foundation under Streams). Custom `Iterable` (a `Range`).
  **The fail-fast `modCount` mechanism (THE deep mechanism)**: a
  `protected transient int modCount` bumped on every structural change;
  the iterator snapshots `expectedModCount` at creation and runs
  `checkForComodification()` on each `next()` → **`ConcurrentModification
  Exception`** on mismatch (best-effort, NOT guaranteed; fires in
  single-threaded code — "concurrent with the iteration"). Fail-fast vs
  **weakly-consistent** (`ConcurrentHashMap` never throws) vs **snapshot**
  (`CopyOnWriteArrayList`) iterators. **Memory layer**: an iterator is a
  **separate heap object** — `ArrayList$Itr` = header 12 B + `this$0`
  back-ref 4 B (inner class, T12) + `cursor`/`lastRet`/`expectedModCount`
  3×4 B = **~32 bytes**, allocated fresh per `iterator()` call
  (independent cursors → nested iteration); the array `for-each`
  allocates **nothing** (an int index in a register). `modCount` = one
  `int` on the collection. **Architecture layer**: cold/interpreted =
  iterator allocation + virtual `hasNext`/`next` per element; **hot = the
  JIT erases it** — escape analysis scalar-replaces the non-escaping
  `Itr` (cursor → register, zero allocation) + inlining/devirtualization
  (monomorphic) + range-check elimination → **same machine code as an
  indexed loop** (the concrete justification for "prefer `for-each`"). The
  `modCount` check = one predicted-branch `int` compare (~free).
  `Spliterator.trySplit` halves the index range O(1) for `ArrayList` (the
  parallel-stream work-split). **Cross-language**: C++ iterators
  (pointer-like, invalidation = **UB**, no safety net); Python
  `__iter__`/`__next__`+`StopIteration`/generators; C#
  `IEnumerable`/`MoveNext` (version-field fail-fast = `modCount` twin);
  **Rust `Iterator::next()->Option` — the borrow checker makes
  iterator-invalidation a COMPILE error** (Java catches at run time via
  `modCount`, Rust proves it can't happen) + zero-cost lazy adapters.
  **External (pull) vs internal (push) iteration** — `Spliterator` is the
  bridge to L2 Streams. **Common mistakes** (6): structural mod inside
  `for-each` (CME → use `Iterator.remove`/`removeIf`); `next()` without
  `hasNext()`; `remove()` before `next()` or twice; relying on
  `HashSet`/`HashMap` order; reusing a spent iterator; treating CME as a
  feature. 15th practice = end-to-end `for-each` desugaring + `Itr`
  layout + CME + escape-analysis trace. Recap ties to T01 escape analysis
  + T12 inner classes. L1 progress now **25/49** (51%); C02 chapter
  **6/23** (26%).
- **Next action:** author `L1/C02/T07` Comparable vs Comparator — the
  ordering protocol `TreeSet`/`TreeMap`/`PriorityQueue`/`sort` consume
  (natural vs external order, the total-order contract, consistency-with-
  equals, the combinators, TimSort); see §4 brief. ONE topic next session.

### 2026-06-04 (L1/C02/T05 — ends-oriented collections; core-structure tour complete; one topic)

- Authored `L1/C02/T05` Queue, Deque, PriorityQueue, Stack — **421
  lines, 9 Mermaid diagrams, 12 H2, 12 interview questions, 8 warning
  callouts, 15 practice exercises.** Completes the core-data-structure
  tour (List T02, Set T03, Map T04, now the ends-oriented structures).
  **Language layer**: the **Queue contract** — the two method families
  (throwing `add`/`remove`/`element` vs returning `offer`/`poll`/`peek`;
  they diverge only at capacity/empty boundaries); the **Deque contract**
  (double-ended `addFirst`/`addLast`/`offerFirst`/`pollLast`/`peekFirst`…
  — serves as BOTH a FIFO queue AND a LIFO stack). **ArrayDeque** as the
  recommended stack AND queue (beats legacy `Stack` and `LinkedList`).
  **PriorityQueue** as a binary MIN-heap (smallest first; O(log n)
  offer/poll, O(1) peek; **iteration is NOT sorted** — only the head is
  ordered, poll repeatedly = heap sort). **The legacy `Stack`** — the
  BROKEN-INHERITANCE example (`extends Vector` → inherits insert-anywhere
  methods violating LIFO; synchronized = slow) — NEVER use; use
  `ArrayDeque`. **Memory layer**: **ArrayDeque circular-array layout** —
  `Object[] elements` + `head`/`tail` indices wrapping via a power-of-2
  `& (length-1)` mask (the same trick as HashMap bucketing — T04
  callback); doubles when full (`System.arraycopy` — T02); contiguous
  ~4 B/elem vs LinkedList's scattered 24-byte Nodes; **forbids null**
  (null is the `poll`/`peek` empty signal). **PriorityQueue binary-heap
  layout** — `Object[] queue` as an IMPLICIT complete binary tree (parent
  `(i-1)/2`, children `2i+1`/`2i+2`; **no node objects, no pointers — the
  array indices ENCODE the tree**); sift-up on offer, sift-down on poll,
  each one O(log n) path. **Architecture layer**: ArrayDeque push ~3-4
  cyc (mask + array write, cache-friendly) vs `Stack.push` synchronized
  (`lock cmpxchg` ~20-50 cyc — T11 callback, ~10× slower) vs
  `LinkedList.push` node-allocation (~20 ns, cache-hostile); the heap's
  "fast O(log n)" — contiguous array + hot upper levels stay cached vs
  `TreeMap`'s pointer-chased red-black tree of the same height (T04
  contrast); the binary-heap-in-an-array as the cleanest demonstration
  that *layout* matters as much as asymptotic complexity. **Cross-
  language**: the universal array-backed consensus — ring-buffer deque
  (Java ArrayDeque, Rust VecDeque, C++/Python deque) + array binary heap
  (Java PriorityQueue, C++ priority_queue-over-vector, Python heapq-over-
  list, Rust BinaryHeap-over-Vec), nobody uses pointer-linked (the T02
  contiguity lesson); **Java/Python default min-heap vs C++/Rust default
  max-heap** (a frequent porting bug — reverse the comparator to flip).
  **Common mistakes** (8): using legacy Stack; expecting PriorityQueue
  iteration sorted; null in ArrayDeque/PriorityQueue (NPE); add-throws vs
  offer-returns-false on bounded queues; PriorityQueue on non-Comparable
  without a Comparator (ClassCastException); LinkedList-as-queue (works
  but slower); peek/poll-null-confusion; treating the heap as a sorted
  list. 15th practice = end-to-end explain-it-back of a `poll()`
  sift-down. Recap reinforces "contiguity beats pointer-chasing" across
  T02–T05. L1 progress now **24/49** (49%); C02 chapter **5/23** (22%).
- **Next action:** author `L1/C02/T06` Iterators & Iterable — the uniform
  traversal protocol under every `for-each` (Iterable/Iterator, the
  for-each desugaring, modCount fail-fast/CME, Iterator.remove,
  ListIterator, Spliterator); see §4 brief. ONE topic next session.

### 2026-06-04 (L1/C02/T04 — THE structural heart; biggest C02 topic; one topic)

- Authored `L1/C02/T04` Map (HashMap, LinkedHashMap, TreeMap) — **513
  lines, 11 Mermaid diagrams, 14 H2, 14 interview questions, 7 warning
  callouts, 15 practice exercises.** THE structural heart — the full
  HashMap internals that T10 (equals/hashCode) and T03 (Set) referenced
  and deferred. **Language layer**: the Map contract (unique keys,
  key→value, entrySet/keySet/values views T01/T12); the Java 8
  functional methods + the two essential idioms (**computeIfAbsent =
  multimap** get-or-create, **merge = counting** accumulate — replaced
  get-check-put boilerplate); HashMap (default, O(1), one null key);
  LinkedHashMap (insertion OR access order → the **5-line LRU cache**
  via accessOrder + removeEldestEntry); TreeMap (red-black, NavigableMap,
  floorKey/ceilingKey/lowerKey/higherKey/firstEntry/headMap/tailMap/
  subMap/descendingMap, O(log n)); Hashtable legacy + ConcurrentHashMap
  forward; EnumMap (ordinal array T13); Map.of/copyOf immutable T19; the
  decision tree. **Memory layer (THE core)**: full HashMap layout —
  Node[] table (power-of-2), **Node 32 bytes** (header+hash+key+value+
  next), size/threshold/loadFactor=0.75/modCount; **spread h^(h>>>16)**
  + **bucket (n-1)&hash** (power-of-2 → 1-cycle AND not modulo); put/get
  algorithms; **collision chains → treeification** at 8 + table≥64 →
  red-black **TreeNode ~56 bytes** (untreeify at 6 = hysteresis);
  **resize** = double + the **lo/hi single-bit split** (no rehash,
  order-preserving — fixed the pre-8 concurrent-cycle bug); load-factor
  trade-off; pre-size to avoid resizes. **Architecture layer (THE cache
  story)**: HashMap.get = spread+mask+table-load+Node+equals → ~20 cyc
  all-L1-hot vs **~150-300 cyc / 3 cache misses on a large cold map**
  (table slot + Node + key each a miss) — "O(1)" is memory-latency-bound
  at scale; **hash flooding DoS** (CVE-2011-4858 colliding keys → O(n²);
  treeification = the Java 8 security mitigation). **Cross-language**:
  C++ unordered_map/map, **Python dict (open-addressing + INSERTION-
  ORDERED since 3.7 — the compact-dict redesign; Java HashMap is NOT
  ordered, must use LinkedHashMap)**, **Rust HashMap = SwissTable/
  hashbrown (open addressing + SIMD 16-control-byte probing — the modern
  cache-optimal design)** + BTreeMap; **Java's separate chaining (scattered
  Node allocations, cache-hostile) is the classic 1990s design; the modern
  world moved to open-addressing SwissTable (contiguous, cache-friendly)
  — the same T02/T03 contiguity-beats-pointer-chase lesson.** **Common
  mistakes** (7): mutable-key-lost, HashMap-order-reliance, missing
  equals/hashCode keys, no-pre-size, HashMap<Enum>-not-EnumMap,
  Hashtable, computeIfAbsent-recursive-CME. 15th practice = end-to-end
  HashMap.put triggering treeification. Recap notes Map backs every Set.
  L1 progress now **23/49** (47%); C02 chapter **4/23** (17%).
- **Next action:** author `L1/C02/T05` Queue/Deque/PriorityQueue/Stack —
  the ends-oriented collections (ArrayDeque circular array, PriorityQueue
  binary heap, the broken legacy Stack); completes the core-structure
  tour (see §4 brief). ONE topic next session.

### 2026-06-04 (L1/C02/T03 — one topic this session)

- Authored `L1/C02/T03` Set (HashSet, LinkedHashSet, TreeSet) — **388
  lines, 10 Mermaid diagrams, 14 H2, 12 interview questions, 8 warning
  callouts, 15 practice exercises.** Disciplined as a HashMap/TreeMap
  CONSUMER — referenced T10's hash-table mechanics, deferred the full
  HashMap byte layout + treeification + red-black tree to T04 (Map).
  **Language layer**: the Set contract (no dups by equals, ≤1 null in
  hash sets, none in TreeSet); set-algebra bulk ops (addAll=union,
  retainAll=intersection, removeAll=difference, containsAll=subset —
  mutate in place); **HashSet is a HashMap in disguise** (element=KEY →
  shared PRESENT dummy value; add returns put==null; the elegant reuse);
  **equals/hashCode load-bearing** (T10 — the two failure modes: equal-
  but-different-hashCode → duplicates coexist; mutable element hashCode-
  changes → lost); LinkedHashSet (LinkedHashMap-backed, before/after →
  insertion-order iteration, O(1) membership); TreeSet (TreeMap/red-
  black tree, NavigableSet, sorted via Comparable/Comparator, O(log n),
  navigation floor/ceiling/lower/higher/headSet/tailSet/subSet/pollFirst;
  uses compareTo NOT equals for dups); EnumSet (bitmask, T13); Set.of
  immutable (T19); the decision tree. **Memory layer**: HashSet memory
  IS HashMap's (32-byte Node/element, value slot = shared PRESENT);
  LinkedHashSet +8 B/element (before/after); TreeSet ~40 B red-black
  node (no table); per-element ranking ~36/44/40 B — 8-10× an ArrayList;
  EnumSet ~0 (one long). Deferred full Node/TreeNode layout to T04.
  **Architecture layer**: HashSet.contains = O(1) hash lookup (T10 cache
  story ~20 cyc hot / ~150-300 cold) vs TreeSet.contains = O(log n) tree
  descent (~log n cache-missing pointer-chases); HashSet for membership,
  TreeSet for order/range. **Cross-language**: C++ unordered_set/set,
  Python set (no sorted set), C# HashSet/SortedSet, **Rust BTreeSet
  (B-tree NOT red-black — cache-friendlier, wide nodes/cache-line, the
  T02 lesson; Rust 2015 picked the modern structure, Java's TreeSet is
  classic 1990s red-black)**. **Common mistakes** (6): HashSet-order
  reliance, mutable-element-lost, missing equals/hashCode → duplicates,
  TreeSet non-Comparable ClassCastException + compareTo-not-equals,
  HashSet<Enum>-not-EnumSet, set-algebra-mutates-in-place. 15th
  practice = end-to-end HashSet.add via the backing HashMap. Recap
  notes the framework builds on itself (HashSet IS HashMap, etc.). L1
  progress now **22/49** (45%); C02 chapter **3/23** (13%).
- **Next action:** author `L1/C02/T04` Map (HashMap/LinkedHashMap/
  TreeMap) — **THE structural heart, the biggest C02 topic**; the full
  HashMap internals T03 + T10 referenced (see §4 brief, flagged for
  extra depth). ONE topic next session.

### 2026-06-04 (L1/C02/T02 — first structural deep dive; one topic)

- Authored `L1/C02/T02` List (ArrayList, LinkedList) — **441 lines, 10
  Mermaid diagrams, 14 H2, 12 interview questions, 7 warning callouts,
  15 practice exercises.** The first STRUCTURAL deep dive (full byte
  layout + resize mechanics + the cache-vs-Big-O story). **Language
  layer**: the List contract + index API; ArrayList (resizable array,
  the default, O(1) random access); LinkedList (doubly-linked nodes,
  also a Deque, O(n) random access); ListIterator (the one LinkedList
  sweet spot — O(1) edits at the cursor); subList VIEW (T12, write-
  through + invalidation); Arrays.asList (fixed-size view) vs List.of
  (immutable T19) vs new ArrayList<>() (mutable copy) ladder + the
  Arrays.asList(int[]) size-1 trap. **Memory layer**: ArrayList layout
  (Object[] elementData 24-byte object + lazy empty array; size vs
  capacity); the **1.5× growth** (oldCap+(oldCap>>1) NOT 2× — the
  interview gotcha; 10→15→22→33; Arrays.copyOf→System.arraycopy
  intrinsic memcpy, L0/C02/T07/T11); **amortized O(1) append** (the
  geometric-series ~3N-total-copies analysis); null-out-on-remove
  (loitering/GC, EJ Item 7); LinkedList layout (24-byte Node =
  header+item+next+prev, scattered, N allocations). The **headline
  comparison** (ArrayList contiguous ~4-6 B/elem vs LinkedList scattered
  24 B/elem). **Architecture layer**: ArrayList.get = bounds-check +
  scaled-index load (~4 cyc, RCE in loops L0/C02/T09) vs LinkedList.get
  = O(n) node walk (~100 cyc/node cache miss → indexed loop is O(n²));
  iteration stride-1-prefetcher-friendly vs pointer-chase-prefetcher-
  BLIND (50-100× at same O(n)); **why ArrayList beats LinkedList even
  on mid-insert** (memcpy shift at memory bandwidth vs O(n) cache-
  missing walk-to-position — Stroustrup's "Are lists evil?" benchmark).
  **Cross-language**: the UNIVERSAL array-backed-by-default consensus
  (C++ vector/Stroustrup, Python list, C# List, Rust Vec — docs warn
  off LinkedList) — cache locality > asymptotic complexity. **Common
  mistakes** (6): LinkedList indexed loop O(n²), choosing LinkedList by
  Big-O without measuring, not pre-sizing, subList-as-copy, modify-
  during-iteration CME, Arrays.asList-not-growable + primitive trap.
  15th practice = end-to-end ArrayList.add triggering a 1.5× resize.
  Recap establishes the deep-dive template (open backing structure →
  memory/resize mechanics → cache+CPU reasons behind Big-O). L1
  progress now **21/49** (43%); C02 chapter **2/23** (9%).
- **Next action:** author `L1/C02/T03` Set (HashSet/LinkedHashSet/
  TreeSet) — largely a HashMap/TreeMap consumer; reference T10's
  hash-table mechanics, defer full HashMap byte layout to T04 (see §4
  brief). ONE topic next session.

### 2026-06-04 (L1/C02/T01 — new chapter begins; one topic this session)

- Authored `L1/C02/T01` Collections framework overview — **453 lines,
  10 Mermaid diagrams, 20 H2, 12 interview questions, 8 warning
  callouts, 15 practice exercises.** The MAP of the framework (deep
  dives deferred to T02-T05/T08 by design). Applies C01: interfaces
  (T08), polymorphism (T06), equals/hashCode (T10), Template Method
  (T07), inner-class views (T12), immutability (T19). **Language
  layer**: why a framework (pre-1.2 chaos → Bloch 1998); the TWO root
  hierarchies — Collection (elements) + Map (key→value, SEPARATE);
  Iterable→Collection→List/Set/Queue/Deque with each contract (List
  ordered/indexed/dups; Set no-dups via equals/hashCode T10; Queue/
  Deque ends-oriented); **why Map is NOT a Collection** (it's
  associations, not a bag — connects via keySet/values/entrySet VIEWS,
  inner-class objects backed by the map, T12); skeletal implementations
  (AbstractList/Set/Map = Template Method T07); the concrete-impl tour
  + defaults (ArrayList/HashMap/ArrayDeque) with deferral note;
  generics usage (type-safe, raw-types-bad); the Collections utility
  class; immutable (List.of/copyOf) vs unmodifiable VIEW (Collections.
  unmodifiableList — backing can still change, T19); fail-fast
  iterators + modCount (CME, L0/C02/T09); arrays vs collections
  (L0/C02/T11); legacy Vector/Hashtable/Stack/Enumeration discouraged
  (use ArrayList/HashMap/ArrayDeque, or j.u.concurrent). **Memory
  layer**: the **three-layer shape** (interface reference → impl
  object → backing structure: ArrayList Object[], LinkedList Node
  chain, HashMap Node[] — preview, deep dives T02-T05); the **boxing
  cost** — List<Integer> ~20 MB vs int[] ~4 MB (5×, cache-hostile,
  L0/C02/T05). **Architecture layer**: program-to-the-interface
  enables impl-swap (polymorphism T06); invokeinterface dispatch
  devirtualized at monomorphic sites (T05) → flexibility ~free;
  algorithmic complexity is what matters (Big-O preview, T08).
  **Cross-language**: C++ STL (containers+iterators+algorithms, the
  inspiration; template-based vs Java interface-based); Python (no
  hierarchy, protocols); C# (parallel design); JCF as a model API.
  **Common mistakes** (8): program-to-impl in signatures, Map-as-
  Collection, modify-during-iteration CME, Vector/Hashtable/Stack in
  new code, raw types, unmodifiable-vs-immutable confusion, millions-
  of-primitives-in-generic-collection. 15th practice = end-to-end
  List reference→ArrayList object→Object[] backing trace. Recap ties
  back to C01 throughout. **Disciplined as the OVERVIEW** — set up the
  architecture, deferred per-structure mechanics to the detail topics.
  L1 progress now **20/49** (41%); C02 chapter **1/23** (4%); C01 done.
- **Next action:** author `L1/C02/T02` List (ArrayList, LinkedList) —
  the first deep dive with full byte layout + resize + cache-vs-Big-O
  (see §4 brief). ONE topic next session.

### 2026-06-04 (T19 — one topic; 🎉 L1/C01 OOP CHAPTER COMPLETE 19/19)

- Authored `L1/C01/T19` Immutability & immutable class design — **478
  lines, 10 Mermaid diagrams, 19 H2, 14 interview questions, 7 warning
  callouts, 15 practice exercises.** **The C01 CAPSTONE** — ties together
  the whole chapter. **Language layer**: the recurring root cause
  (shared mutable state → aliasing T12, defensive-copy overhead T03/T18,
  unstable keys T10, thread hazards T11) that immutability eliminates at
  the source. The **5 rules** (EJ Item 17: no mutators; final class or
  private-ctor+factories; all fields final; all fields private; defensive
  copies of mutable components in+out) with the Period/Date example +
  the **copy-then-validate TOCTOU** ordering. The defensive-copy /
  use-immutable-component-types continuation of T18/T10. The **5
  benefits** (simplicity, free thread-safety, safe hash keys T10, free
  sharing/caching kills T12 aliasing, failure-atomicity). **The JMM
  final-field safe-publication guarantee** — THE deep reason immutables
  are thread-safe: the constructor-exit freeze (memory barrier) makes
  final fields visible to any thread reading the object after
  construction WITHOUT sync (T02 callback; this-must-not-escape caveat;
  only final fields). Functional updates / withers (String/BigInteger/
  LocalDate; record withX). Records as immutability-by-default (4/5
  rules free) + the shallow caveat (T14). The mutability spectrum
  (immutable / effectively-immutable-needs-safe-publication / defensively-
  copied / mutable). The cost (new object per change) + 4 mitigations
  (EA T01, share-don't-copy, structural sharing, mutable companion like
  StringBuilder). **Memory layer**: immutable layout = normal object
  (header + final fields, ACC_FINAL + freeze — T01/T02/T03); String's
  mechanics (final private never-exposed byte[] value + the benign hash
  data race — L0/C02/T06); **structural sharing** (persistent data
  structures copy only the O(log n) changed path, share the rest — a
  million-element persistent vector update copies ~4 nodes not a million);
  interning/caching (String pool, Integer cache — safe BECAUSE immutable;
  L0/C02/T05-T06). **Architecture layer**: the freeze barrier per-arch
  (ARM dmb ishst, x86 no-op/TSO — T02 deeper callback); immutables need
  NO locks/read-barriers → faster concurrent reads; **read-only shared
  data is MESI-Shared across all cores with ZERO invalidation traffic**
  while mutable shared data ping-pongs (T01 cache callback) — immutability
  is fast on multicore, not just safe. **Cross-language**: Rust
  (immutable-by-default `let`/`let mut` + borrow-checker = provably-safe
  shared-immutable, the compile-time resolution of T12 aliasing); Clojure/
  Haskell (everything immutable, persistence/purity for concurrency);
  Scala/Kotlin (val); the immutable-by-default INDUSTRY TREND driven by
  multicore, with Java retrofitting (records, final, List.of/copyOf,
  java.time, coming value classes). **Common mistakes** (7): missing
  defensive copy, non-final class, final-is-shallow, leaking internal
  collection, validate-before-copy TOCTOU, over-copying immutables,
  effectively-immutable-without-safe-publication. 15th practice =
  capstone end-to-end (why cross-thread String read is lock-free safe).
  Recap explicitly ties the whole chapter together. L1 progress now
  **19/49** (39%); **C01 chapter 19/19 (100%) ✅**.
- **🎉 L1/C01 OBJECT-ORIENTED PROGRAMMING COMPLETE** — 19 topics, ~13,800
  lines, ~185 diagrams, all at language+memory+architecture depth.
- **Next action:** begin **L1/C02 — Collections & Core APIs** with
  `L1/C02/T01` Collections framework overview (see §4 brief — it's the
  OVERVIEW/map; keep structure deep-dives for T02-T05). ONE topic next
  session.

### 2026-06-04 (T18 — one topic this session)

- Authored `L1/C01/T18` Object cloning & Cloneable — **481 lines, 10
  Mermaid diagrams, 19 H2, 14 interview questions, 7 warning callouts,
  15 practice exercises.** Why Java's clone/Cloneable is broken + the
  modern alternatives. **Language layer**: shallow vs deep copy (shallow
  shares nested objects — the T12 aliasing bug); `Object.clone()`
  (protected native, shallow field-by-field, gated by the **`Cloneable`
  empty marker** — T08 — which declares NO clone method, the "backwards"
  design); the clone recipe (super.clone, covariant cast T05, deep-copy
  mutable fields); **the final-field problem** — can't reassign a `final`
  field after super.clone, so clone is INCOMPATIBLE with final mutable
  fields (because it skips the constructor — T02); the by-convention
  unenforced contract (!=x, same class, equals). **Why Cloneable is
  broken** (EJ Item 13, 6 reasons: backwards marker, skips constructor,
  vague contract, manual deep copy, fragile inheritance, useless checked
  exception). **Array clone() — the ONE good use** (int[] independent,
  Object[] shares elements, int[][] shares inner arrays — T01 array
  callback). **Modern alternatives**: copy constructor (the default —
  runs constructor, final-safe, type-safe, no checked exception — T02;
  the JDK collections all use it); static copy factory (List.copyOf);
  records (immutable → share freely + wither for changes — T14);
  serialization deep copy (deep+automatic but slow). **Memory layer**:
  `Object.clone()` is a **JVM intrinsic** — read size from Klass,
  allocate same-size, install FRESH header (new identity/lock, T09),
  bulk byte-copy the field region (memcpy-like, T01) — NO constructor,
  NO field-by-field bytecode; ~alloc+memcpy cost. **Why the byte-copy
  is shallow** (the precise physical reason): reference fields are
  copied as their POINTER BITS → clone's field holds the same pointer
  → aliases the same nested object (T01 reference-vs-object + T12);
  primitives copy correctly (bytes ARE the value). Array clone
  mechanics. **Architecture layer**: intrinsic byte-copy vs copy-ctor
  field-stores — both fast, JIT-inlinable, EA-eliminable (T01); the
  intrinsic's single memcpy can edge out N stores for huge flat objects
  but difference negligible → clarity/correctness favor copy
  constructor. **Cross-language**: C++ copy constructor + operator=
  (value semantics, deep-by-default — the model Java SHOULD have
  followed; Bloch says the Java analog IS a copy constructor); Rust
  Clone (explicit, cost-visible) + Copy (bitwise); Python copy/deepcopy
  (depth-explicit); C# ICloneable (also broken); Kotlin data copy()
  (shallow). The universal lesson: constructor/function-based copying
  good, marker-interface-bolt-on copying regretted. **Common mistakes**
  (6): clone without Cloneable (CloneNotSupportedException), shallow-
  shares-nested (aliasing T12), final-field deep-copy impossibility
  (T02), forgot covariant/super.clone, clone-instead-of-copy-ctor,
  array-clone-is-deep myth. 15th practice = end-to-end Object.clone
  intrinsic byte-copy trace. Recap ~18 objectives across all three
  layers. L1 progress now **18/49** (37%); C01 chapter **18/19** (95%).
- **Next action:** author `L1/C01/T19` Immutability (the C01 CAPSTONE —
  see §4 brief). ONE topic next session → then C01 COMPLETE, C02 begins.

### 2026-06-04 (T17 — one topic this session; dense topic, full depth)

- Authored `L1/C01/T17` Java Module System (JPMS) — **465 lines, 10
  Mermaid diagrams, 18 H2, 14 interview questions, 7 warning callouts,
  15 practice exercises.** The module layer above packages (Java 9+,
  Project Jigsaw / JEP 261). **Language layer**: the THREE problems
  JPMS solves (classpath hell/reliable config, weak encapsulation,
  monolithic platform). `module-info.java` directives: **`requires`**
  (+ `transitive` implied-readability when your API exposes the dep,
  + `static` compile-time-only); **`exports`** (+ qualified `to`);
  **`opens`** (deep reflection — distinct from exports; + qualified,
  + `open module`); **`uses`/`provides...with`** (ServiceLoader SPI,
  T08). **The two-part access rule** — M reads N (`requires`) AND N
  exports P (`exports`), BOTH required. **Strong encapsulation** —
  THE key idea: a `public` type in an unexported package is invisible
  to other modules (the module outranks `public`; this is what let
  the JDK encapsulate sun.misc.Unsafe). Module path vs classpath.
  **Named / automatic / unnamed modules** (named = module-info, strong
  encap; automatic = plain JAR on module path, reads-all/exports-all
  migration bridge, Automatic-Module-Name vs filename instability;
  unnamed = classpath, reads-all/exports-nothing-to-named backward
  compat). Split packages forbidden (T16). Modular JDK (java.base
  always implicit). jlink + jdeps. **Memory layer**: `module-info
  .class` (ACC_MODULE 0x8000 + Module attribute with requires/exports/
  opens/uses/provides tables + per-entry ACC_TRANSITIVE/ACC_MANDATED;
  T03 deeper callback); the module graph RESOLVED + VALIDATED at
  startup into the boot ModuleLayer (reliable config = fail-fast
  "module not found" vs classpath lazy NoClassDefFoundError); each
  class belongs to a Module (getModule()); boot/platform/app
  classloaders host modules. **Architecture layer**: access checks
  resolved ONCE at link time then constant-pool-patched → ZERO
  per-call cost (T16/T03); jlink slim images + CDS faster cold start;
  the closed module graph (sealed-set-style closed world — T15) enables
  AOT/Leyden; `--add-opens`/`--add-exports` escape hatches (T03) for
  legacy reflective frameworks. **Cross-language**: .NET assemblies
  (the closest analog, 15 yrs earlier — internal ≈ unexported, Internals
  VisibleTo ≈ qualified export), ML/OCaml functors (richest, different
  goal), Rust crates + pub(crate), Node/ES (no strong-encap tier).
  **HONEST adoption account**: JPMS won INSIDE the JDK (modular
  platform, jlink, encapsulated internals — you get it automatically)
  but most APP code still runs on the classpath/unnamed module
  (migration cost + insufficient incentive + JDK already captured the
  main benefit). **Common mistakes** (7): forgot exports (public-but-
  invisible), reflection without opens (InaccessibleObjectException),
  split packages, filename-derived automatic-module-name instability,
  module-path/classpath mixing, public-still-universal myth, --add-opens
  overuse. 15th practice = end-to-end module access trace. Recap ~22
  objectives across all three layers. L1 progress now **17/49** (35%);
  C01 chapter **17/19** (89%).
- **Next action:** author `L1/C01/T18` Object cloning & Cloneable (see
  §4 brief). ONE topic next session. 2 left in C01 (T18, T19) → then C02.

### 2026-06-04 (T16 — one topic this session)

- Authored `L1/C01/T16` Packages & imports — **442 lines, 10 Mermaid
  diagrams, 19 H2, 14 interview questions, 8 warning callouts, 15
  practice exercises.** The on-disk/on-classpath reality beneath every
  type. **Language layer**: packages serve TWO roles — namespace
  (globally-unique FQNs: java.util.List vs java.awt.List) + access
  boundary (package-private scope, T03). The `package` declaration
  (reverse-domain, first non-comment line). FQN vs simple names; when
  FQN is mandatory (java.util.Date + java.sql.Date — import one, FQN
  the other). The 3 import forms (single-type; on-demand wildcard —
  imports types NOT subpackages, compile-time only; static — T11).
  java.lang auto-imported. Import resolution precedence (same file >
  single-type > same package > wildcard; two wildcards same name =
  ambiguous). The default/unnamed package (can't be imported FROM —
  unreachable from real code). Naming conventions + package-info.java.
  Packages as access boundaries (the internal-API tier, T03). Split
  packages (classpath-legal-but-fragile, JPMS-forbidden — T17 forward).
  **Memory layer**: **directory-mirrors-package as a HARD requirement**
  — package com.example.app FORCES the file to com/example/app/App.class
  (compiler enforces output, classloader relies on it). **Class-name →
  .class-file resolution**: classpath = ordered roots (dirs + JARs=ZIP+
  MANIFEST); classloader translates dots→slashes, walks roots in order,
  FIRST-MATCH-WINS (T01); classpath-order shadowing = "JAR hell"; lookup
  cost = per-first-load linear scan, then cached. **Three name forms**:
  canonical (com.example.Map.Entry, dots), binary (com.example.Map$Entry,
  $ for nested — getName), internal (com/example/Map$Entry, slashes —
  constant pool/bytecode, T01/T04). **Runtime package identity =
  (package name, classloader)** — two com.foo.Bar from different
  classloaders are DIFFERENT runtime packages + types (ClassCastException
  across them); foundation of classloader isolation (app servers) +
  why package-private is classloader-scoped (T03 deeper callback). No
  per-instance cost; the Package object. **Architecture layer**:
  **imports are COMPILE-TIME ONLY** — they vanish from bytecode; the
  constant pool stores only FQN internal-form; wildcard ≡ single-type
  produce IDENTICAL bytecode; zero runtime cost. The real cost is class
  LOOKUP (classpath scan), independent of import style. JIT/runtime
  never see packages — only resolved klass pointers (T01/T04).
  **Cross-language**: namespace-tied-to-directory (Java/Python/Go,
  mechanical lookup) vs decoupled (C++/C#, flexible); **Python import
  RUNS code at runtime** (vs Java's compile-time, no-execution import);
  C#/C++ decouple namespace from files. Java's directory-mirrors-package
  convention-turned-requirement enables the deterministic dots→slashes
  classloader (no index, no search). **Common mistakes** (8): default-
  package real code, wildcard ambiguity, wildcard-imports-subpackages
  myth, wrong directory, import-vs-Python-runtime-import confusion,
  split packages, classpath-order-for-correctness, (and the FQN-both-
  Dates impossibility). 15th practice = end-to-end class-name →
  .class-file resolution trace. Recap ~22 objectives across all three
  layers. L1 progress now **16/49** (33%); C01 chapter **16/19** (84%).
- **Next action:** author `L1/C01/T17` Java Module System (JPMS) (see
  §4 brief — flagged as a big/dense topic). ONE topic next session.

### 2026-06-04 (T15 — one topic this session; modern-data-modeling trio complete)

- Authored `L1/C01/T15` Sealed classes & interfaces — **507 lines, 10
  Mermaid diagrams, 18 H2, 14 interview questions, 7 warning callouts,
  15 practice exercises.** Completes the T13/T14/T15 trio. **Language
  layer**: sealing as the **missing third option** between `final`
  (no subtypes) and open (any subtype) — `sealed`+`permits` = a
  specific named set. The **3 subtype rules** (every direct subtype
  must be `final` leaf / `sealed` continue-closed / `non-sealed`
  re-open — records & enums implicitly final). Locality rules (same
  module, or package on classpath; direct subtypes only) + `permits`
  omission (same-file inference). Sealed classes (abstract/concrete)
  vs sealed interfaces; sealing orthogonal to access modifiers (T03).
  **Exhaustive pattern matching — THE payoff**: sealed switch needs NO
  default; adding a variant breaks every non-exhaustive switch at
  compile (the refactoring-checklist guarantee); the synthetic-default
  MatchException for separate-compilation safety. **Sealed + records =
  ADT** (sum of products = Rust enum) — full JSON-tree example with
  deconstruction (T14 callback). **Sealed vs enum** — closed set of
  TYPES (different shapes, own data) vs VALUES (same-shaped constants).
  Closed-domain modeling (ASTs, state machines, commands, Result<T>).
  **Memory layer**: the **`PermittedSubclasses` class-file attribute**
  (NO ACC_SEALED flag — the attribute's presence IS the sealing;
  alongside Record/NestMembers/BootstrapMethods); **load-time verifier
  enforcement** (unpermitted subtype → `IncompatibleClassChangeError`,
  dual compile+load enforcement like T03 access / T07 abstract-new);
  `isSealed()`/`getPermittedSubclasses()` reflection; ZERO instance-
  level cost (pure type metadata). **Architecture layer**: exact CHA /
  bounded devirtualization — closed load-time-locked implementor set
  means the JIT knows ALL implementors → no deopt guard needed for
  "new subclass" (T05 callback); single-implementor sealed = provably
  monomorphic; pattern switch = dense typeSwitch dispatch (T06). HONEST
  note that the compile/load guarantees are fully real today while the
  degree of JIT exploitation of `sealed` specifically is version-
  dependent + forward-looking (Valhalla). **Cross-language**: the
  ML-family lineage (Haskell/OCaml `data` sum types 1970s → Scala
  `sealed trait` 2000s, the DIRECT inspiration → Java 17) vs Rust/Swift
  `enum` (closed by default, sum+product unified in one feature). Java
  finally has the typed-functional data modeling ML had in 1980.
  **Common mistakes** (7): missing final/sealed/non-sealed modifier,
  non-sealed defeating exhaustiveness, cross-module permitted subtype,
  sealed-where-enum/interface-fits, expecting compile-time-only
  enforcement, habitual default-in-sealed-switch. 15th practice =
  end-to-end sealed lifecycle (PermittedSubclasses attr → compile
  exhaustiveness → load-time verifier → JIT devirt → reflection).
  Recap ~22 objectives across all three layers. **MILESTONE: enum
  (T13) + record (T14) + sealed (T15) modern-data-modeling trio
  COMPLETE.** L1 progress now **15/49** (31%); C01 chapter **15/19**
  (79%).
- **Next action:** author `L1/C01/T16` Packages & imports (see §4
  brief). ONE topic next session.

### 2026-06-04 (T14 — one topic this session)

- Authored `L1/C01/T14` record types — **556 lines, 9 Mermaid diagrams,
  18 H2, 14 interview questions, 8 warning callouts, 15 practice
  exercises.** **Language layer**: the ~50-line value-class boilerplate
  problem → Lombok/AutoValue workarounds → `record Point(int x, int y)`
  as the language feature. What the record header generates (private
  final field per component, canonical constructor, accessor `x()` not
  `getX()`, equals/hashCode/toString — T10 trio for free). **Compact
  constructor** (no param list, no field assignment — validate/normalize,
  fields auto-appended; param reassignment flows to fields); explicit
  canonical + additional (this(...)-delegating) constructors. **Shallow
  immutability + the defensive-copy trap** (mutable array/List/Date
  component — copy in compact ctor AND override accessor, or use
  immutable types; same as T10 array-component caveat). **Records extend
  `Record`** (T13 enum parallel — implicitly final, can't extend a class,
  CAN implement interfaces; ALL instance state = the components =
  transparency). Additional members (static, instance methods, generics,
  nested=implicitly-static, local records in stream pipelines). **Records
  + sealed = algebraic data types** (T13/T15 bridge — sum of products =
  Rust enum). **Pattern matching + record deconstruction** (Java 21:
  `case Point(int x, int y)`, nested patterns — sound BECAUSE of
  transparency). When records don't fit (mutability, inheritance, hidden
  state, JPA entities). **Memory layer**: record instance layout
  IDENTICAL to a hand-written final class (header + field per component,
  reorder-by-size — T01) — ZERO overhead; record-ness lives in metadata,
  not instances. The **`Record` marker superclass**. **THE deep
  mechanism**: equals/hashCode/toString are each a SINGLE `invokedynamic`
  → `java.lang.runtime.ObjectMethods.bootstrap` (T06 callback) which
  weaves the component accessor MethodHandles into ONE combined handle
  on first call, returns a ConstantCallSite, caches it; compact bytecode
  (one opcode vs N field compares), centralized correctness in the JDK
  runtime, auto-handles the Double.compare float trap (T10). The
  **`Record` class-file attribute** + `isRecord()`/`getRecordComponents()`
  reflection (declaration order — drives deconstruction + framework
  serialization). **Architecture layer**: not slower — bootstrap once,
  then JIT inlines through the ConstantCallSite + combined handle to the
  SAME machine code as hand-written; accessors inline to a single field
  load (T04); EA can scalar-replace short-lived records; deconstruction
  = accessor calls = field loads. **Records use VALUE equality (.equals,
  component-by-component) — NOT identity (== ) unlike singleton enums
  (T13 contrast)** → safe HashMap keys (T10). **Cross-language**: Kotlin
  data class (+copy/componentN), Scala case class (+apply/unapply),
  C# record (+with/Deconstruct), Rust struct + à-la-carte #[derive],
  Lombok @Value (the pre-record workaround) — Java deliberately minimal
  (pattern matching over copy/componentN). **Common mistakes** (8):
  expecting getX(), mutable component not copied, adding an instance
  field, extending a class, accessor returning a transformed value,
  using a record where mutability needed, == instead of .equals. 15th
  practice = end-to-end equals via the ObjectMethods bootstrap trace.
  Recap ~24 objectives across all three layers. **Also cleaned up
  PROGRESS-L1 §4: removed 264 lines of orphaned prior-session briefs
  (T14/T08/T05/T04) that had accumulated; §4 now holds exactly the
  current T15 brief.** L1 progress now **14/49** (29%); C01 chapter
  **14/19** (74%).
- **Next action:** author `L1/C01/T15` sealed classes & interfaces
  (see §4 brief). ONE topic next session.

### 2026-06-04 (T13 — one topic this session)

- Authored `L1/C01/T13` enum types (with fields/methods) — **557 lines,
  10 Mermaid diagrams, 17 H2, 14 interview questions, 7 warning
  callouts, 15 practice exercises.** **Language layer**: the int-constant
  problem (no type safety/namespace/behavior/stable-values) → Bloch's
  pre-1.5 typesafe-enum pattern → the `enum` keyword as that pattern as
  a language feature. Each constant is a **`public static final`
  singleton instance** built once in `<clinit>` (T11 callback) → compare
  with `==`. **Enums with fields/constructor(implicitly private)/
  methods** (Planet mass/radius/surfaceGravity). **Constant-specific
  method bodies = anonymous subclasses** (T12 callback — PLUS is an
  instance of synthetic `Operation$1`); **abstract enum methods** force
  every constant to implement (compiler completeness check). Compiler-
  generated `values()` (clones $VALUES each call), `valueOf()` (throws
  IAE), `ordinal()` (fragile — don't persist), `name()` (stable, final).
  **`enum extends Enum<E>`** → can't extend a class, CAN implement
  interfaces; `equals`/`hashCode`/`compareTo`/`name`/`ordinal` all
  **final** (identity equals, ordinal compareTo). **The enum singleton**
  (EJ Item 3 — reflection-safe: JVM forbids reflective enum instantiation;
  serialization-safe: name-only + valueOf on read; thread-safe: <clinit>
  + class-init lock — beats the holder idiom T11). **EnumSet = a `long`
  bitmask** (RegularEnumSet ≤64 constants; JumboEnumSet long[]; set ops
  = single bitwise instructions — L0/C02/T04 callback; ~20× smaller/
  faster than HashSet). **EnumMap = an ordinal-indexed array** (no
  hashing, no Node objects — T10 contrast). **switch on enums** via the
  synthetic `$SwitchMap` int[] + tableswitch (L0/C02/T08 callback) +
  Java 21 pattern switch. **Memory layer**: constants as static final
  fields in the enum's Class mirror (T11 callback); enum instance byte
  layout (header 12 + name ref 4 + ordinal int 4 = 24 B bare; Planet
  with 2 doubles = 40 B); the synthetic `$VALUES` array; <clinit>
  builds all constants in declaration order; EnumSet's single long;
  EnumMap's array. **Architecture layer**: `==` identity (1 cycle,
  final Enum.equals); EnumSet bit test = 1 AND, EnumMap = direct array
  index (~4 cycles) — entire HashMap machinery bypassed (T10 contrast);
  switch = tableswitch O(1) jump (L0/C02/T08); JIT constant-folds the
  static-final constants. **Cross-language**: C enum (named ints), C++
  enum class (scoped typed ints, no methods), Java (singleton objects),
  Rust/Swift (algebraic data types — variants with different payloads)
  → Java models ADTs with sealed + records (T15). **Common mistakes**
  (7): persisting ordinal(), HashSet/HashMap instead of EnumSet/EnumMap,
  values() in hot loop, non-exhaustive switch + new constant, mutable
  enum state, extending a class from enum, .equals instead of ==.
  15th practice = end-to-end EnumSet.contains (bit AND) + Operation.
  apply (vtable to anonymous subclass). Recap ~22 objectives across
  all three layers. L1 progress now **13/49** (27%); C01 chapter
  **13/19** (68%).
- **Next action:** author `L1/C01/T14` record types (see §4 brief).
  ONE topic next session.

### 2026-06-04 (T12 — one topic this session)

- Authored `L1/C01/T12` Inner, local & anonymous classes — **640 lines,
  10 Mermaid diagrams, 14 H2 / 12 H3, 12 interview questions, 7 warning
  callouts, 15 practice exercises.** Theme: **capture** — the three
  non-static nested classes reach into their surroundings and the
  compiler implements that with synthetic fields. **Language layer**:
  the four-kinds table (capture column); **inner classes** — `this$0`,
  access enclosing privates, the qualified-`new` syntax
  `outer.new Inner()`, `Outer.this` disambiguation, iterator/collection-
  view use cases (`ArrayList$Itr`, `Map.keySet()` backed views).
  **Local classes** — method-scoped, rare now. **Anonymous classes** —
  unnamed class+instance, extend-one-class-OR-implement-one-interface,
  no constructor (superclass args + instance initializer block — T02),
  the pre-lambda workhorse. **Variable capture + effectively-final** —
  the core mechanism: locals live on the stack frame (L0/C02/T15) which
  dies at method return, but the instance can outlive it, so the
  compiler COPIES the value into a synthetic `val$x` field at
  construction → capture is BY VALUE → captured locals must be
  effectively final (the copy mustn't diverge from the source). The
  mutable-holder workaround (one-element array / AtomicInteger).
  **Lambdas vs anonymous classes** — NOT the same mechanism: anonymous
  = real `Outer$1.class`, allocated each eval, `this` = the instance;
  lambda = invokedynamic + LambdaMetafactory (T06), no .class file,
  non-capturing = cached singleton, `this` = enclosing instance. The
  `this`-semantics difference demo (anonymous prints `Widget$1`, lambda
  prints `Widget`). When to use each. **Cross-language closures table**
  + deep dives: **JS/Python/Kotlin capture by REFERENCE** (closures
  share + can mutate the variable → must heap-box it → the JS loop-var
  bug; Kotlin auto-wraps captured vars in a Ref object — the holder
  trick done automatically); **C++ explicit capture lists** `[=]`/`[&]`
  (the dangling-reference footgun Java removed); Java's capture-by-
  value-of-effectively-final = safest point in the design space.
  **Memory layer**: the synthetic **`this$0` field** in full — 4 bytes,
  how the compiler generates it + the hidden constructor parameter +
  the rewrite of `new Inner()`/`outer.new Inner()`; `javap -p` output;
  inner instance 24 B vs static-nested 16 B (the 8-byte this$0+padding
  cost). **`val$` capture fields** — each captured local = 4-8 bytes;
  anonymous instance byte layout (header + this$0 + val$message +
  val$count). The `.class` files (`Outer$1`/`Outer$2` numbered
  anonymous, `Outer$Inner` named, `Outer$1Local`) — ~500-700 B
  Metaspace each; lambdas produce NONE. **The enclosing-instance leak**
  — full mechanism: a `HugeBuffer` (100 MB) pinned alive by an
  iterator's `this$0` even when otherwise unreferenced; the
  static-nested / capture-minimally fix (EJ Item 24). **Architecture
  layer**: construction cost (allocation + one field store per capture,
  ~15-30 ns); **why lambdas are cheaper** — non-capturing anonymous
  allocates a NEW instance every eval (1M allocations in a loop) vs the
  non-capturing lambda's ONE cached instance (0 per-iter allocation);
  enclosing-private access — pre-Java-11 synthetic `access$NNN` bridges
  vs Java-11+ nest-based direct access (T03 deeper callback); JIT
  inlining of monomorphic anonymous/lambda callbacks (dispatch vanishes
  after warm-up). **Common mistakes** (7): enclosing-instance leak,
  mutating a captured local, this-confusion anon-vs-lambda, anonymous
  can't implement 2 interfaces, no anonymous constructor, hot-loop
  anonymous allocation, the pre-Java-8 loop-capture bug. 15th practice
  = end-to-end `makeTask("hi")` capture trace (val$message copy,
  frame death, heap survival). Recap ~20 objectives across all three
  layers. L1 progress now **12/49** (24%); C01 chapter **12/19** (63%).
- **Next action:** author `L1/C01/T13` enum types (with fields/methods)
  (see §4 brief). ONE topic next session.

### 2026-06-04 (T11 — one topic this session)

- Authored `L1/C01/T11` static members, blocks & nested classes —
  **604 lines, 10 Mermaid diagrams, 16 H2 / 15 H3, 12 interview
  questions, 8 warning callouts, 15 practice exercises.** **Language
  layer**: static vs instance split (per-class vs per-value); why
  static exists (constants, counters/registries, factories, `main`);
  cross-language design table + deep dives (**Kotlin dropped `static`
  for `companion object`** because statics aren't real objects;
  **Python class variables silently shadow through instances** — a bug
  Java avoids). **Static fields** — `static final` compile-time
  constants (inlined) vs mutable static state (global problem); the
  **cross-JAR recompile gotcha** (inlined constant goes stale).
  **Static methods** — no `this`, `invokestatic`, utility classes +
  static factories (EJ Item 1); can't be overridden (hiding, static
  dispatch). **`static {}` blocks + `<clinit>`** — source-order
  splicing, `javap -c` walkthrough, the per-class init LOCK making it
  thread-safe, `ExceptionInInitializerError` → `NoClassDefFoundError`
  forever (read the FIRST error), static-init deadlock. **Static
  import**. **Nested classes — the four kinds** overview; **static
  nested classes** (no enclosing ref; `HashMap.Node` real example —
  T10 callback; EJ Item 24: prefer static nested over inner). **Memory
  layer (the accurate, often-wrong-in-other-resources part)**: static
  fields live in the **`java.lang.Class` MIRROR object on the HEAP**
  since JDK 8 — appended after the mirror's fields; NOT in Metaspace
  (which holds the InstanceKlass metadata/offsets), NOT in PermGen
  (deleted, JEP 122). History PermGen→Metaspace+mirror. Consequence:
  static fields are **GC roots tied to the classloader** → static
  collections = the classic leak; static fields collectible only on
  class unload (classloader death) → the basis of hot-reload + the
  classloader-leak trap. Byte math: `static int` = 4 bytes TOTAL vs
  4 bytes × instances. **Nested `.class` files** (`Outer$Nested.class`);
  the synthetic **`this$0`** field on inner instances (4 bytes + pins
  enclosing alive) that static nested lacks — JOL/`javap -p` byte
  comparison (StaticNested 16 B vs Inner 24 B). **Architecture layer**:
  `getstatic`/`putstatic`/`invokestatic` vs instance opcodes (no
  receiver); `getstatic` resolves to mirror-address+offset → single
  `mov`; **`static final` folds to an IMMEDIATE operand** (no memory
  access — faster than any field read, enables loop unroll/vectorize);
  `invokestatic` = most inline-friendly (one target, no vtable, no
  guard); JMM ordering for mutable statics (needs volatile/sync).
  **The initialization-on-demand holder idiom** as the payoff —
  static nested holder + lazy class init + per-class init lock =
  thread-safe lazy singleton with ZERO synchronization; full
  mechanism walk; the recommended lazy singleton (enum singleton in
  T13). **Common mistakes** (8): mutable static global, static
  collection leak, inner-where-static-nested-would-do, static-final
  treated as runtime-config, reading-the-second-error, static-via-
  instance, circular static init deadlock, non-thread-safe counter.
  16th practice = end-to-end `Config.MAX + Counter.total` trace
  (immediate fold vs getstatic load). Recap ~22 objectives across all
  three layers. L1 progress now **11/49** (22%); C01 chapter **11/19**
  (58%).
- **Next action:** author `L1/C01/T12` Inner, local & anonymous classes
  (see §4 brief). ONE topic next session.

### 2026-06-04 (T10 — one-topic-per-session standard begins)

- **New workflow per user:** ONE topic per session for depth (saved as
  memory `feedback_one-topic-per-session`). T10 authored to the full
  T01–T10 standard.
- Authored `L1/C01/T10` equals, hashCode, toString contracts — **790
  lines, 10 Mermaid diagrams, 14 H2 / 24 H3 sections, 14 interview
  questions, 11 warning callouts.** **Language layer**: why value
  equality exists (entities vs value types); cross-language design
  table + deep dives (Python disables hashing when you override __eq__
  without __hash__; Rust's PartialEq-vs-Eq encoding reflexivity in the
  type system — floats are PartialEq not Eq because NaN≠NaN; C++ keeps
  == and std::hash unrelated). The **five `equals` properties** each
  with a concrete violation + the collection that depends on it:
  reflexive (List.contains), symmetric (CaseInsensitiveString↔String
  trap), transitive (Point/ColorPoint — Effective Java Item 10, "no
  way to extend an instantiable class adding a value field while
  preserving the contract"), consistent (URL.equals does DNS!), non-null
  (instanceof null = false). The **5-step canonical `equals` recipe**;
  **getClass vs instanceof** trade-off table (substitutability vs
  extensibility); **float trap** (Double.compare for NaN + signed zero);
  **array trap** (Arrays.equals/hashCode). The **3-clause `hashCode`
  contract**; why-forgetting-hashCode-breaks-HashMap with the physical
  bucket-mismatch mechanism. The **`hashCode` recipe** (Objects.hash vs
  hand-rolled 31*h+field, allocation cost); **why 31** (odd = no bit
  loss; prime = distribution; strength-reduces to (i<<5)-i); hash
  caching for immutable objects. **Memory layer**: identity hash in
  the mark word (0 extra heap bytes) vs overridden hash (recomputed
  each call); **String's dedicated `hash` (4 bytes) + `hashIsZero`
  (1 byte) fields** with full 24-byte String object layout and the
  hashCode source; **HashMap.Node 32-byte layout** (header+hash+key+
  value+next) + per-entry/per-million-entries memory math; **table
  power-of-2** → `& (length-1)` masking (1 cycle) vs modulo (20-40);
  **hash spreading** `h ^ (h>>>16)` and why (bucket uses only low
  bits); **treeification** Node(32B) → red-black TreeNode(~56B) at
  bucket 8 + table≥64, O(n)→O(log n), with the Comparable requirement.
  **Architecture layer**: why-31 strength reduction with the x86
  `shl 5 + sub` (no imul) and the historical 2-5× String.hashCode win;
  HashMap.get as a **cache story** (~20 cycles all-L1 vs ~150-300
  cycles / 3 cache misses on a large cold map — "O(1)" dominated by
  memory latency); collision walk = O(n) cache-miss chain; branch
  prediction in equals `&&` chains (discriminating-field-first);
  **hash flooding DoS** (CVE-2011-4858 / 28C3) — attacker-controlled
  colliding keys → O(n²) inserts → server hang; treeification as the
  Java-8 security mitigation (O(n log n)). **toString** conventions
  (class+fields, no secrets/log-leak, not a parseable API). **Records**
  auto-generate the contract-correct trio (getClass-based, final =
  no inheritance trap; array-component caveat). **Lombok/AutoValue**
  for non-record classes. 16 practice exercises incl. the vanishing
  key, symmetry/transitivity breaks, the assembly of 31*x, String
  hash-field reflection, JOL Node layout, forced treeification, hash-
  flooding microbench, Objects.hash allocation cost, toString secret
  leak, and an end-to-end map.get() source→CPU trace with cycle
  estimates. Recap as ~24 learning objectives across all three layers.
  L1 progress now **10/49** (20%); C01 chapter **10/19** (53%).
- **Next action:** author `L1/C01/T11` static members, blocks & nested
  classes (see §4 brief). ONE topic next session.

### 2026-06-04 (later — depth pass T01-T09)

- **User feedback**: the depth across the L1/C01 topics was lighter than
  it should be, especially on JVM internals and memory interactions. Did
  a systematic depth pass adding a substantial **"Deeper JVM Internals"**
  H2 section to each of T01-T09. Final line counts:

  | Topic | Before | After | Delta | Diagrams |
  |-------|------:|-----:|-----:|---------:|
  | T01 Classes & Objects | 943 | **1,159** | +216 | 32 |
  | T02 Fields/methods/ctors/this | 883 | **1,029** | +146 | 23 |
  | T03 Encapsulation | 709 | **913** | +204 | 16 |
  | T04 Inheritance & super | 685 | **875** | +190 | 18 |
  | T05 Method overriding | 482 | **690** | +208 | 15 |
  | T06 Polymorphism | 467 | **661** | +194 | 11 |
  | T07 Abstract classes | 448 | **587** | +139 | 12 |
  | T08 Interfaces | 519 | **679** | +160 | 11 |
  | T09 Object class | 455 | **689** | +234 | 13 |
  | **Total** | 5,591 | **7,282** | **+1,691** | **151** |

- **What each Deeper section covers**:
  - **T01**: HotSpot Klass struct (with offsets table); compressed
    class pointers vs compressed oops as separate features; mark word
    bit transitions across all 5 states (unlocked, biased history,
    thin-locked, inflated, marked-for-GC); TLAB fast-path pseudocode +
    refill mechanism; GC barriers (card table, write barrier); tiered
    compilation states (interpreter → C1 → C2 + deopt); NUMA-aware
    allocation; class mirror vs ClassLoaderData.
  - **T02**: Field resolution from symbolic Fieldref to direct offset;
    JMM final-freeze memory barriers per architecture (x86 mfence vs
    ARM dmb ishst); allocation prefetching (`prefetchnta`); on-stack
    replacement for hot constructors; chain inlining across `<init>`
    levels; full bytecode-level field initializer splicing; constructor
    + escape analysis full mechanism; verifier's uninitialized-this
    type tracking.
  - **T03**: Nest-based access (Java 11 JEP 181) replacing pre-11
    synthetic `access$NNN` bridges; NestHost/NestMembers attributes;
    module access pipeline (export check + member check); module-info
    .class structure with ACC_MODULE flag; binary compatibility rules
    table; full access-check bytecode pipeline (resolve → access check
    → patch CP); VarHandle access modes; invokespecial sidesteps;
    IllegalAccessError vs IllegalAccessException vs InaccessibleObject
    Exception distinctions.
  - **T04**: Subclass linking algorithm (8 steps); subtype check via
    `_primary_supers` display (O(1) for depth ≤ 7); `_secondary_supers`
    fallback + `_secondary_super_cache`; vtable construction with
    overriding (index stability); subclass field-offset append rule;
    super.method() walked-up resolution; CHA implementation via
    `_subklass`/`_next_sibling` linked list; concrete numbers for deep
    hierarchies; class initialization locks (per-Klass mutex).
  - **T05**: Method struct in detail (entry points, MDO pointer,
    intrinsic id, vtable index); MDO (Method Data Object) profile
    contents; CompiledIC inline cache structure with self-patching;
    polymorphic inline cache (PIC) chain; deoptimization mechanism
    (scope info + uncommon trap + scope reconstruction); tiered
    compilation state machine; bridge method generation algorithm in
    detail; "free polymorphism" cost breakdown.
  - **T06**: invokedynamic opcode mechanics with bootstrap +
    CallSite types (Constant/Mutable/Volatile); LambdaMetafactory
    spinning hidden classes (ASM internally); non-capturing lambda
    instance caching; lambda body as private static synthetic method;
    StringConcatFactory recipe-string mechanism; SwitchBootstraps.
    typeSwitch perfect-hash table; generic erasure bridge methods
    with checkcast delegation; full lambda dispatch cost in pseudo-
    x86 (~6 cycles).
  - **T07**: AbstractMethodError stub in HotSpot
    (SharedRuntime::throw_AbstractMethodError); `new` opcode
    verification rejecting ACC_ABSTRACT (InstantiationError); three
    "cannot instantiate" errors distinguished; abstract `<init>`
    still callable via `super(...)`; Miranda methods — JVM-synthesized
    abstract stubs filling missing itable slots; verifier tracking
    concrete-class completeness; per-method ACC_ABSTRACT verifier
    rules.
  - **T08**: klassItable struct layout (itableOffsetEntry pairs +
    method pointer arrays); itable construction algorithm at class
    load; why invokeinterface is slower than invokevirtual (two
    indirections + linear scan); inline cache mitigation; default
    method selection 3-step resolution (class wins, most-specific
    interface wins, otherwise override); invokeinterface 5-byte
    instruction format quirk (extra args hint); private interface
    methods bytecode; sealed interface PermittedSubclasses attribute.
  - **T09**: Full mark word bit layouts per state; mark word
    transitions table; ObjectMonitor struct in C++ heap with cost
    table per operation; monitor deflation mechanism; identity hash
    code generation algorithms (-XX:hashCode=N options); java.lang
    .ref family (Weak/Soft/Phantom); Cleaner internals
    (PhantomCleanable + queue + thread); Cleaner vs try-with-
    resources guidance; native intrinsics for Object methods (PrintIntrinsics);
    mark word repurposing as forwarding pointer during GC; concurrent-
    GC load barriers (ZGC, Shenandoah).

- Coverage now includes the byte-level memory layouts, JVM data structures
  (Klass, Method, vtable, itable, mark word, ObjectMonitor, MDO,
  CompiledIC, BootstrapMethods, NestHost/NestMembers), architecture-
  specific code generation (x86 cmpxchg, ARM dmb ishst, NEON), and JIT
  internals (tiered compilation, escape analysis, devirtualization,
  deoptimization) that were missing. **Memory-interaction depth now
  matches DEPTH-CHECKLIST §4a fully.**

### 2026-06-04 (later — T09)

- Authored `L1/C01/T09` Object class & its methods — **455 lines, 9
  Mermaid diagrams** at the deep bar. Reference-style topic walking
  Object's 11 methods. **Language layer**: Object as universal root;
  table of all 11 methods with purpose + default behavior; three
  categories (identity/state, lifecycle, monitor). **`toString`**
  default vs override; record auto-generation. **`equals`** default
  identity vs override pattern (5-step canonical, deferred contract
  detail to T10). **`hashCode`** default identity-hash-via-mark-word
  vs override matching `equals`; the contract preview. **`getClass`**
  — `final`, reads klass pointer, returns `Class<?>`; comparison to
  `instanceof` (exact vs is-a). **`clone`** — shallow, requires
  Cloneable hack, skips constructor; the three problems; copy
  constructor / factory / record alternatives. **`finalize`** —
  deprecated since Java 9 (JEP 421 removal planned); replaced by
  `try-with-resources` + `AutoCloseable` and `Cleaner`. **Monitor
  methods** `wait`/`notify`/`notifyAll` — synchronized requirement,
  IllegalMonitorStateException, wait-set semantics, notify-vs-
  notifyAll; full mechanism deferred to L3/C01. **Memory layer**:
  Object's vtable slots at known indexes start every class's vtable;
  identity hash cached in mark word; getClass reads klass pointer in
  one machine-word load; monitor data packed into mark word.
  **Architecture layer**: cost tables — getClass ~1 ns, hashCode
  ~1-3 ns, equals default ~1 ns, monitor ops ~20-50 ns uncontended;
  JIT inlines all. **Common-mistake callouts** (8 traps): equals
  without hashCode, default toString in production, clone for deep
  copy, new finalize methods, wait outside synchronized, getClass-
  vs-instanceof confusion, override getClass attempt (final), notify
  vs notifyAll preferring notifyAll. **INTERVIEW callout** with 12
  questions covering method count, defaults, identity hash mark-word
  caching, clone problems, finalize deprecation, monitor-on-Object
  rationale, notify vs notifyAll, getClass final, getClass cost,
  vtable slot positioning, Cloneable as marker. **Practice (15
  exercises)** — default toString observation, identity vs value
  equality, hashCode contract repro, getClass exact vs instanceof,
  Class<?> reflection, shallow clone trap, copy constructor refactor,
  AutoCloseable cleanup, Cleaner non-deterministic, wait outside
  synchronized error, notify vs notifyAll race, vtable slot
  inspection, mark-word identity-hash dump, getClass final compile
  error, end-to-end explain-it-back. Recap as ~20 learning objectives
  spanning all three layers. L1 progress now **9/49** (18%); C01
  chapter **9/19** (47%).
- **Next action:** author `L1/C01/T10` equals, hashCode, toString
  contracts (see §4 brief).

### 2026-06-04 (later — T08)

- Authored `L1/C01/T08` Interfaces (default, static, private methods)
  — **519 lines, 9 Mermaid diagrams + `javap` flag listings** at the
  deep bar. **Language layer**: interface as pure contract; implicit
  `public abstract` on methods, `public static final` on fields;
  multiple implementation via `implements I1, I2, ...`; interfaces
  extending interfaces (multiple). **Java 8 `default` methods** —
  motivation (API evolution for Collection.stream); implementer
  inheritance + optional override. **The diamond problem** — two
  unrelated defaults with same signature; resolution rules: (1)
  most-specific subinterface wins, (2) class wins over interface,
  (3) class must override with `Interface.super.method()`. **Java 8
  `static`** interface methods — utility scoped to interface; NOT
  inherited (static methods are class-level). **Java 9 `private`**
  interface methods for sharing logic across defaults. **`@Functional
  Interface`** — single-abstract-method enforcement; lambda target;
  the `java.util.function` family. **Marker interfaces** —
  `Serializable`, `Cloneable`, `RandomAccess` as type tags. **Sealed
  interfaces** (T15 forward) — explicit `permits` list. **Memory
  layer**: `ACC_INTERFACE = 0x0200` + implicit `ACC_ABSTRACT`; default
  methods have `Code` attribute, abstracts don't; **the itable** —
  one per implemented interface, holding method pointers; class's
  klass has an itable list. **`invokeinterface` mechanics** — receiver
  klass → search itable list for interface → index into itable →
  indirect call; bytecode comparison with `invokevirtual`.
  **Architecture layer**: dispatch cost table — monomorphic
  inline-cached ~1 ns for both, megamorphic ~3 ns virtual vs ~4-5 ns
  interface (itable search). CHA + inline caching apply to interface
  dispatch. Lambda dispatch via `invokedynamic + LambdaMetafactory`
  → generated class → `invokeinterface`. **Common-mistake callouts**
  (7 traps): instance fields, constructor, ambiguous defaults, equals/
  hashCode/toString as defaults, multi-abstract @FunctionalInterface,
  static method via instance ref, too much default logic. **INTERVIEW
  callout** with 14 questions covering fields, multiple implement,
  defaults, diamond resolution, functional interface, static methods
  non-inheritance, ACC_INTERFACE, invokeinterface, dispatch cost,
  marker interfaces, Object methods as defaults, when prefer
  interface, Interface.super.method, sealed interfaces. **Practice
  (15 exercises)** — basic interface, multiple implementation,
  default method, diamond resolution, static interface method,
  private interface method, functional interface + lambda, javap
  ACC_INTERFACE, invokeinterface vs invokevirtual, itable SA dump,
  megamorphic itable benchmark, Comparator composition, marker
  inspection, sealed pattern switch, end-to-end explain-it-back.
  Recap as ~22 learning objectives. L1 progress now **8/49** (16%);
  C01 chapter **8/19** (42%).
- **Next action:** author `L1/C01/T09` Object class & its methods
  (see §4 brief).

### 2026-06-04 (later — T07)

- Authored `L1/C01/T07` Abstraction & abstract classes — **448 lines,
  9 Mermaid diagrams + `javap -v` flag listings** at the deep bar.
  **Language layer**: abstract class as a non-instantiable type
  with mixed concrete + abstract methods + state + constructors;
  abstract method syntax (signature + semicolon); the four
  incompatibilities (abstract + private/static/final/synchronized
  declaration); any-abstract-method-means-class-must-be-abstract
  rule; abstract-class-with-no-abstract-methods legal but unusual.
  **Constructors in abstract classes** — invoked via `super(...)`
  from concrete subclasses; enforce shared invariants (the VIN
  validation example). **Template Method pattern** as the canonical
  use — `final` orchestrator + `abstract` steps; the
  HttpRequestHandler 4-step authenticate/authorize/process/respond
  example. **Abstract-class-vs-interface practical comparison
  table** + the modern composition pattern (interface for contract +
  abstract class for shared state where genuinely needed).
  **Memory layer**: `ACC_ABSTRACT = 0x0400` flag on class entry
  AND on each abstract method's `method_info`; abstract methods
  have NO `Code` attribute (no bytecode body); javap -v output
  showing both flags. **`new` blocked at compile time** by javac
  + at link time by JVM verifier (InstantiationError). **Vtable
  slots for abstract methods point to AbstractMethodError stubs**
  — never reached in normal use because concrete subclass replaces
  the slot; AbstractMethodError appears only in binary-
  incompatibility scenarios. **Architecture layer**: identical
  dispatch to concrete classes — `invokevirtual` reads klass →
  subclass vtable slot → concrete body; **NO per-call cost
  penalty for abstraction**. JIT applies CHA + inline caching +
  devirtualization to abstract methods exactly as to concrete.
  Template Method + JIT inlining = the whole algorithm collapses
  to a flat sequence in hot code. **When NOT to use** — shared
  utility methods (use static utility class), placeholder type
  (use interface), faking multi-inheritance (use interfaces), pure
  data (use records). **Common-mistake callouts** (6 traps):
  trying to instantiate, forgetting to implement all abstract
  methods, abstract + incompatibility-modifier paradoxes, fields
  never used (design smell), overusing abstraction, runtime
  AbstractMethodError as binary-incompat signal. **INTERVIEW
  callout** with 12 questions covering constructors in abstract
  classes, concrete methods, no-abstract-methods legality,
  abstract-vs-interface, ACC_ABSTRACT, AbstractMethodError,
  Template Method, abstract + final paradox, abstract + static
  paradox, identical dispatch cost, when to prefer interfaces,
  protected abstract methods. **Practice (15 exercises)** —
  declare abstract Shape with abstract area, try to instantiate,
  forget to implement, javap -v ACC_ABSTRACT verification,
  Template Method implementation, abstract class with state +
  constructor + invariant, three paradox compile errors, empty
  abstract class, AbstractMethodError repro via binary incompat,
  abstract method overridden mid-chain, JIT inlining of abstract
  method via CHA, abstract-to-interface refactor, end-to-end
  explain-it-back. Recap as ~18 learning objectives spanning all
  three layers. L1 progress now **7/49** (14%); C01 chapter
  **7/19** (37%).
- **Next action:** author `L1/C01/T08` Interfaces (default, static,
  private methods) (see §4 brief).

### 2026-06-04 (later — T06)

- Authored `L1/C01/T06` Polymorphism (compile-time vs runtime) —
  **467 lines, 8 Mermaid diagrams** at the deep bar (§4 + §4a;
  synthesis topic comparing flavors covered individually in T04/T05/
  T13/L1-C02-T11 — naturally lighter on new mechanism diagrams).
  **Language layer**: the four flavors mapped to CS theory: ad-hoc
  (overloading), subtype (overriding + interfaces), parametric
  (generics), functional (lambdas via `invokedynamic`). The
  compile-time vs runtime distinction — overloading + generics =
  static pick at compile time; overriding + interfaces + lambdas
  + pattern-instanceof = dynamic pick at runtime. **Liskov
  Substitution Principle** in full — preconditions ≤, postconditions
  ≥, invariants preserved, exception specifications narrow only,
  no surprising side effects; the Square-Rectangle classic
  violation walked end-to-end with `doubleWidthAndCompare(Rectangle)`
  failing on Square; refactor as siblings under abstract Shape.
  Upcasting (free) vs downcasting (`checkcast` + ClassCastException);
  pattern-binding `instanceof` (Java 16+) as the safe alternative.
  **Memory layer**: all five `invoke*` opcodes mapped to dispatch
  styles; **itable structure** for interface dispatch — one vtable
  per class but multiple itables (one per implemented interface);
  `invokeinterface` may search the itable list. **`invokedynamic` +
  LambdaMetafactory** for lambda dispatch — bootstrap generates an
  implementing class, caches via CallSite; subsequent calls become
  `invokevirtual` on the generated class. **Architecture layer**:
  **dispatch cost table** — `invokestatic`/`invokespecial` ~1 ns;
  `invokevirtual` ~1-5 ns (monomorphic to megamorphic);
  `invokeinterface` ~1-7 ns (slightly higher due to itable search);
  `invokedynamic` ~1 ns after warm-up. JIT devirtualization (T05
  callback) applies to all dynamic-dispatch flavors via inline
  caching + CHA + deopt. **Design patterns** as the practical home
  of polymorphism: **Strategy** (interchangeable behavior injected
  at construction), **Template Method** (parent skeleton + subclass
  step overrides), **Factory** (returns concrete behind common
  parent); full L3/C03. Compile-time vs runtime decision table.
  **Common-mistake callouts** (6 traps): mixing overload/override
  mental model, LSP violations passing type checks, downcasting
  without `instanceof`, premature dispatch optimization, inheritance
  when composition fits, forgetting generics are erased. **INTERVIEW
  callout** with 12 questions covering polymorphism taxonomy,
  compile-time vs runtime, LSP, lambda bytecode, interface vs
  virtual cost, type erasure, hot-code cost, safe downcast
  alternative, patterns, parametric polymorphism, `final` not
  hurting polymorphism, `instanceof` chains. **Practice (15
  exercises)** — compile-time pick via javap, runtime pick via
  javap, generics erasure via getClass, lambda bootstrap via
  javap -v, dispatch cost microbenchmark, Square-Rectangle
  violation + refactor, pattern-binding `instanceof` conversion,
  pattern switch over sealed hierarchy with exhaustiveness check,
  itable dump via SA, invokeinterface vs invokevirtual cost,
  Strategy refactor, Template Method with final play(), Factory
  pattern, devirtualization observation, end-to-end explain-
  it-back of lambda chain. Recap as ~20 learning objectives
  spanning all three layers. L1 progress now **6/49** (12%); C01
  chapter **6/19** (32%).
- **Next action:** author `L1/C01/T07` Abstraction & abstract classes
  (see §4 brief).

### 2026-06-04 (later — T05)

- Authored `L1/C01/T05` Method overriding — **482 lines, 10 Mermaid
  diagrams + x86-64 native listing + javap bridge-method dump** at the
  deep bar (§4 + §4a; lower diagram count due to focused scope — much
  of the dispatch machinery was covered in T04 vtable section).
  **Language layer**: the five override-applicability rules from JLS
  §8.4.8 — (1) identical signature, (2) same-or-covariant return type
  (Java 5+), (3) same-or-narrower checked exceptions, (4) same-or-
  broader access, (5) cannot override `final`/`static`/`private`.
  Worked failure examples for each. The **`@Override` annotation** as
  a compile-time check directive; catches typos like `equlas`. Sharp
  distinction between overriding (subclass, same signature, dynamic)
  and overloading (same class, different signature, static, T13
  callback). **Memory layer**: full **bridge method** mechanism for
  covariant returns — javac synthesizes a method with the parent's
  exact signature carrying `ACC_BRIDGE | ACC_SYNTHETIC` flags,
  delegating to the real override; preserves binary compatibility
  with parent-typed callers. Worked `javap -v` showing both methods
  in the subclass and the bridge's body (`aload_0 + invokevirtual
  <real> + areturn`). Bridge methods also generated for generic
  erasure (L1/C02/T12 forward). **vtable slot replacement** revisited
  — parent assigns slot index N to method m; subclass's vtable[N]
  pointer is replaced with subclass's override; new subclass methods
  appended at later slots. **`invokevirtual` mechanism with x86-64
  listing** — `mov klass ptr` + `mov vtable[slot]` + `call register`.
  **Architecture layer**: **inline caching** at JIT-emitted call sites
  — monomorphic = compare klass to cached + inlined body (~1 ns);
  bimorphic = 2 compares + 2 inlined bodies (~2 ns); 3+ types = give
  up on inlining, vtable lookup (~3-5 ns megamorphic). The **Branch
  Target Buffer (BTB)** for indirect-call prediction — monomorphic
  hits ~1 cycle, megamorphic misses ~10-20 cycles. **CHA + deopt
  guard** revisited from T04 — JIT inlines non-final methods
  optimistically; deopts if a subclass with override loads later.
  **Devirtualization** via inline caches. Cost table for monomorphic
  vs bimorphic vs megamorphic vs cold. **Common-mistake callouts** (9
  traps): missing @Override letting typos slip, access narrowing,
  broader checked exception, non-subtype return, override of final/
  static/private, override-vs-overload confusion, field-overriding-
  doesn't-exist, constructor-calls-overridable (T02 callback), equals
  without hashCode (T10 forward). **INTERVIEW callout** with 14
  questions covering rules, covariant return, bridge methods,
  invokevirtual mechanics, JIT devirtualization, megamorphism cost,
  BTB role, access widening rationale, vtable replacement, why
  `final` cannot override, constructor non-overridability, @Override
  benefits, equals/hashCode pairing. **Practice (16 exercises)** —
  basic override with @Override, covariant return bridge inspection,
  exception narrowing, access widening, final cannot override,
  static hiding vs overriding, private "override" independence,
  invokevirtual bytecode trace, vtable SA dump, monomorphic vs
  megamorphic benchmark, CHA deopt experiment, final inlining
  comparison, PrintAssembly inline cache verification, equals/
  hashCode contract bug repro, interface default-method override,
  end-to-end explain-it-back. Recap as ~18 learning objectives
  spanning all three layers. L1 progress now **5/49** (10%); C01
  chapter **5/19**.
- **Next action:** author `L1/C01/T06` Polymorphism (compile-time vs
  runtime) (see §4 brief).

### 2026-06-04 (later — T04)

- Authored `L1/C01/T04` Inheritance & super — **685 lines, 15 Mermaid
  diagrams + bytecode listings of full constructor chains** at the
  deep bar (§4 + §4a). **Language layer**: inheritance as code-reuse-
  via-specialisation; three rules (single inheritance, Object root,
  private not inherited); `extends` syntax + restrictions (no `final`,
  no primitives, no cycle, no multi); implicit `extends Object`; the
  IS-A relationship + Liskov preview; the three uses of `super` —
  `super(...)` constructor delegation (T02 callback), `super.method(...)`
  for non-virtual parent invocation, `super.field` for shadowed-field
  access. Detailed treatment of **what's inherited** with member-by-
  member table; the constructor-non-inheritance rule + the
  private-not-inherited rule; field shadowing as static dispatch
  (worked Parent/Child reference + .x demonstration that same object,
  different declared type, sees different x); static method hiding
  vs instance overriding. Composition-over-inheritance discussion with
  the classic `Stack extends Vector` / `Properties extends Hashtable`
  / `Square extends Rectangle` mistakes; refactor to composition
  pattern. **Memory layer**: **append-only subclass object layout** —
  parent fields at original offsets, subclass fields appended; concrete
  byte layouts for Animal/Dog (24 → 32 bytes); the reorder-within-
  layer rule (P { boolean b; long c; } shows long-first within parent
  layer, separately from subclass layer); the **vtable shape** —
  parent slots preserved at parent indexes, override REPLACES slot
  pointer at same index, new methods appended; full **`invokevirtual`
  dispatch trace** (klass pointer → vtable[slot] → indirect call);
  **`invokespecial` for `super.method()`** with `javap -c` showing
  the non-virtual jump. **Constructor chain bytecode** for a 3-level
  hierarchy with `javap -c` of each `<init>` showing `aload_0 +
  invokespecial Parent.<init>`. The **Klass super-pointer chain** in
  Metaspace ending at Object → null. **Architecture layer**: **JIT
  call-site classification** — monomorphic (inline + type guard, ~1 ns),
  bimorphic (2 inlines + 2 type tests, ~2 ns), megamorphic (vtable
  lookup, no inline, ~3-5 ns); **Class Hierarchy Analysis (CHA)**
  with deopt guard — the optimistic inlining of non-final methods
  under "no override has loaded yet" assumption, deopt fires if a
  subclass loads later. **Deep hierarchy cost** — memory (parent
  fields accumulate; JFrame has ~150 inherited fields), construction
  time (chain length × inlined `<init>`), JIT effectiveness
  (wide-tree pushes toward megamorphism). **Common mistakes** (9
  traps): no-arg parent missing, field shadowing without realizing,
  @Override doesn't help fields, overridable-method-from-constructor
  (T02 callback), static-hiding-vs-overriding confusion, parent-ref-
  can-access-subclass-only-members myth, extending final classes,
  multi-inheritance via interfaces myth (only behavior, never state),
  inheritance-for-code-reuse-alone (use composition). **INTERVIEW
  callout** with 14 questions covering single inheritance + diamond,
  IS-A, what's inherited, constructor non-inheritance, `super.method`
  bytecode, override-vs-shadow, subclass layout, CHA + deopt,
  monomorphic/bimorphic/megamorphic, static hiding, deep-hierarchy
  cost, `final` rules, `super.x` shadowed, vtable layout enabling
  upcasting. **Practice (17 exercises)** — 3-level constructor chain
  bytecode, JOL subclass field-offset append, polymorphic field
  access via parent ref, `super.method()` bytecode inspection, field
  shadowing demo, `@Override` typo catch, static method hiding,
  extending final class compile error, final class JIT inlining
  benchmark, CHA deopt experiment, megamorphic-vs-monomorphic
  throughput, Klass super chain walk, Object methods on custom class,
  composition refactor of Stack, no-no-arg-parent failure +
  remediations, vtable layout via SA, end-to-end explain-it-back.
  Recap as ~22 learning objectives spanning all three layers. L1
  progress now **4/49** (8%); C01 chapter **4/19**.
- **Next action:** author `L1/C01/T05` Method overriding (see §4 brief).

### 2026-06-04 (later — T03)

- Authored `L1/C01/T03` Encapsulation & access modifiers — **709 lines,
  13 Mermaid diagrams + bytecode flag tables** at the deep bar (§4 +
  §4a; smaller diagram count justified by the topic being table-
  and-decision-tree-heavy rather than process-heavy — DEPTH-CHECKLIST
  "length follows scope" rule). **Language layer**: encapsulation as
  the discipline that funnels mutation through invariant-preserving
  methods so the constructor's invariants stick; the **four access
  levels** with visibility table (`private` < package-private <
  `protected` < `public`); **principle of least privilege** as the
  authoring discipline (start `private`, widen only when forced); the
  decision-tree diagram from `private` up. Detailed treatment of each
  level: **`private`** — class-only + nested classes share scope;
  **package-private** — same-package + the classloader-scoped package
  identity quirk; **`protected`** — package-private PLUS subclasses
  anywhere, with the **access-through-subclass-type rule** worked end-
  to-end (`other.protectedMember` legal only when other's declared
  type is `this`-class-or-subclass); **`public`** — broadest level
  + the file-name implication recap from L0/C02/T01. **Encapsulation
  as invariant enforcement** — Account example with `private long
  balance`, constructor + deposit/withdraw all guarding `balance >=
  0`. **Getter/setter pattern + modern critique** — getter/setter for
  every field is semantically equivalent to public field; JavaBeans
  exception (Spring/Jackson/Hibernate); the "expose operations not
  data" rule; the mutable-getter trap (return `Collections.unmodifiable
  List` or copy). **Records** (T14 forward) for tuple-like data
  carriers vs domain types with invariants. **Immutable-field idiom**
  (`private final` + constructor-only) yielding thread-safety,
  defensive-copy elimination, cache-key safety. **Private
  constructors** for utility classes (with `AssertionError` defense
  against reflective instantiation) and singletons (static-final-
  field pattern + the enum-singleton superior alternative, full
  L3/C03). **Memory layer**: access modifiers compile to
  **`access_flags` bits** in the .class file — full flag table
  (`ACC_PUBLIC = 0x0001`, `ACC_PRIVATE = 0x0002`, `ACC_PROTECTED =
  0x0004`, `ACC_STATIC = 0x0008`, `ACC_FINAL = 0x0010`,
  `ACC_SYNCHRONIZED/SUPER = 0x0020`, `ACC_VOLATILE/BRIDGE = 0x0040`,
  `ACC_TRANSIENT/VARARGS = 0x0080`, `ACC_ABSTRACT = 0x0400`,
  `ACC_SYNTHETIC = 0x1000`, `ACC_ANNOTATION = 0x2000`, `ACC_ENUM =
  0x4000`); **no `ACC_PACKAGE`** — package-private = absence of the
  three visibility bits. `javap -v` output examples showing flags
  on real members. **Linking + IllegalAccessError check** — the JVM
  verifier walks `getfield`/`putfield`/`invoke*` and validates
  access at LINK time, not per call; runtime cost is zero. The
  **binary-incompatibility scenario** — compile against v1 with
  `public foo()`, run against v2 with `private foo()`, observe
  `IllegalAccessError` at link time. **`javap -p`** for dumping
  private members. **Architecture layer**: **`private` methods are
  faster** — compile to `invokespecial` (not `invokevirtual`), no
  vtable lookup, JIT proves monomorphism STATICALLY, inlines without
  a deopt guard; observable ~1-3 ns per call advantage that
  compounds across millions of calls. **`final` methods + methods of
  `final` classes** get the same benefit. **Class Hierarchy Analysis
  (CHA)** — JIT inlines non-final methods when no override loaded
  yet, installs deopt guard for if-a-subclass-loads-later. **Records
  and enums are implicitly `final`** — the language enforces the
  JIT-friendly invariant. **Reflection bypass** with
  `setAccessible(true)`; cost table — direct field read ~1 ns,
  `Field.getLong` ~30-100 ns, `Method.invoke` ~50-150 ns,
  `MethodHandle.invokeExact` (cached) ~5-10 ns; MethodHandles +
  VarHandles as the modern lower-cost alternative. **JPMS preview**
  (T17 forward) — `exports` for compile-time visibility, `opens` for
  reflective visibility, `InaccessibleObjectException` from
  `setAccessible` across non-opened modules; module-level
  encapsulation as the strongest tier. **Common-mistake callouts**
  (9 traps): protected = subclass only myth, default-is-public myth,
  `final` doesn't prevent contained-mutation, getter/setter defeats
  encapsulation, public mutable arrays, protected via parent-typed
  ref rejection, package-private as external API mistake, private-
  method-overridden myth (it's shadowing), reflection without JPMS
  opens. **INTERVIEW callout** with 15 questions covering the four
  levels, protected semantics, access_flags encoding, compile-time
  vs link-time enforcement, IllegalAccessError, `private` JIT speed,
  `final` analogous benefit, reflection cost, JPMS, immutability
  checklist, principle of least privilege, getter/setter critique,
  classloader-scoped package identity. **Practice (19 exercises)**
  — direct private access compile failure, progressive widening
  observation, protected via subclass type test, `javap -v` ACC_*
  flag inspection, raw .class hex dump access_flags inspection,
  `javap -p` private member reveal, invariant via encapsulation
  Account, getter mutable trap + fix, utility class private ctor
  with `AssertionError` defense + reflection break attempt,
  static-final singleton vs enum singleton, private-method shadowing
  demo, `final` method monomorphism microbench, CHA deopt trigger
  experiment, reflection-cost microbench against direct + VarHandle,
  JPMS opens experiment, `IllegalAccessError` binary incompat repro,
  record vs domain type refactor, final field with mutable contents
  trap, full end-to-end "explain it back" of a private void log
  through ACC_PRIVATE flag → javac check → invokespecial emission
  → link-time verifier → JIT inline → reflective bypass → JPMS
  opens requirement. Recap as ~24 learning objectives spanning all
  three layers. L1 progress now **3/49** (6%); C01 chapter **3/19**.
- **Next action:** author `L1/C01/T04` Inheritance & super (see §4
  brief).

### 2026-06-04 (later — T02)

- Authored `L1/C01/T02` Fields, methods, constructors, this — **883
  lines, 19 Mermaid diagrams + multiple bytecode listings** at the deep
  bar (§4 + §4a). **Language layer**: the three primary member kinds
  (instance fields, instance methods, constructors); field initializers
  as compile-time-spliced declarations that run during `<init>` (NOT
  assignments); forward-reference rules (`int a = b;` rejected;
  `int a = this.b;` legal but reads zero); the **three uses of `this`**
  — disambiguating shadowed names (canonical `this.x = x;` setter,
  callback to T15), as an expression (fluent builders, callbacks),
  and inside a constructor for sibling delegation; **`this` is local
  slot 0 + immutable**; `this` illegal in `static`. **Methods revisited
  for instance state** — `Account.deposit(amount)` operating on `this.balance`;
  static method has no `this`; encapsulation preview. **Constructor
  declaration anatomy** — six rules (name = class name, no return type,
  visibility, no `final`/`abstract`/`static`/`synchronized`, parameters,
  `throws`); the `void Foo()`-is-a-method-not-a-constructor trap.
  **The synthesized default no-arg constructor** — visibility matches
  the class; vanishes the moment you declare ANY constructor; the
  hide-the-no-arg-by-adding-a-parameterized-one trap. **Constructor
  overloading** via the same three-phase resolution as method overloading
  (T13 callback); canonical-constructor delegation pattern. **`this(...)`
  chaining** with the **first-statement rule**; rationale — JVM verifier
  safety invariant; the telescoping-constructor anti-pattern and Builder
  /record successors. **Implicit `super()`** — every constructor body
  whose first statement isn't `super(...)` or `this(...)` gets a silent
  `super()` insertion by javac; chain reaches `Object` always; compile
  error if parent has no accessible no-arg constructor (and the
  inheritance-version of the hide-the-no-arg trap). **Initialization
  order** — the precise five-stage sequence: allocate + zero + header
  → `<init>` starts → super-call/this-call (always first) → parent
  chain completes → instance initializer blocks + field initializers
  in source order → constructor body → return. **Instance initializer
  blocks `{ ... }`** with a worked example showing interleaved
  initialization in source order. **Worked `javap -c` example** for a
  class with field initializer + initializer block + constructor body,
  showing the splicing into one `<init>` method: `aload_0 +
  invokespecial Object.<init>` (super), then sequenced `putfield`s
  for the initializers, then body assignments, then `return`.
  **`final` fields and definite assignment** — every constructor path
  must assign exactly once; compile-error examples; three remediation
  patterns. **The JMM `final` freeze guarantee** — final fields safely
  visible to other threads at constructor exit, even via non-volatile
  references; the foundation of safe immutable-object publication;
  forward to L3/C01. **Calling overridable methods from a constructor**
  — the fragile-base-class trap walked end-to-end with `Base() { init(); }`
  + `Sub` override reading uninitialized `x`; 8-step chain diagram;
  defenses (`private`/`final` methods; explicit init hook). **Leaking
  `this`** — race when constructor publishes `this` before completion;
  static factory remedy. **Memory & architecture layer**: `<init>` as
  a real JVM method with `(...)V` descriptor; `Methodref` constant-
  pool entry; slot 0 = `this`; constructors are non-virtual
  (`invokespecial` not `invokevirtual`); JIT inlining + escape analysis
  on the inlined body can eliminate the entire `new + <init>`; the
  `hypot` worked example; profile-specialized constructor compilation.
  **Common-mistake callouts** (10 traps): `void Foo()` is a method,
  `x = x` shadowing, hide-the-no-arg trap, `super(...) + this(...)`
  both in same body, statement-before-first-call, missing `final`
  assignment, overridable-method-from-constructor, leaking `this`,
  parent-reads-field-initializer-not-yet-run, constructor-throws-
  leaves-half-allocated-object-as-garbage-but-side-effects-stick.
  **INTERVIEW callout** with 15 questions covering initialization
  order, field initializer vs body assignment, implicit super,
  first-statement rule, why constructors can't be static/final/abstract,
  JMM final freeze, fragile-base-class, leaking-this, `new + dup +
  invokespecial`, `invokespecial` vs `invokevirtual`, descriptor format,
  EA elimination, throws cleanup model, void-method confusion,
  canonical `this.x = x` fix. **Practice (19 exercises)** — field
  initializer vs body precedence, `x = x` setter bug, default-
  constructor disappearance on adding a parameterized one, `javap -c`
  splicing inspection, `javap -v` constant-pool inspection, overload
  resolution on constructors, this(...) chaining, first-statement
  violation, implicit `super()` parent-has-no-no-arg failure,
  initializer-block source-order trace, three definite-assignment
  fixes, JMM `final`-freeze publish/read experiment, fragile-base-
  class reproducer + fix, leaking-this race repro + factory fix,
  EA inlining of `new Point + <init>` verified with `PrintEliminate
  Allocations`, descriptor inspection, telescoping → canonical
  refactor, constructor-throws cleanup verification, full end-to-end
  "explain it back" trace. Recap as ~27 learning objectives spanning
  all three layers. L1 progress now **2/49** (4%); C01 chapter **2/19**.
- **Next action:** author `L1/C01/T03` Encapsulation & access modifiers
  (see §4 brief).

### 2026-06-04 (parallel L1 session start)

- **Parallel-session kickoff.** The main session is authoring L0/C02/T17
  (Wrapper classes & autoboxing). This file was created to track L1
  progress without conflicting with `PROGRESS.md`.
- Authored `L1/C01/T01` Classes & Objects — **943 lines, 26 Mermaid
  diagrams + ASCII byte-layout blocks** at the deep bar (§4 + §4a).
  **Language layer**: classes as user-defined types bundling state +
  behavior; the procedural-to-OOP motivation (loose ints vs `Point`);
  class declaration anatomy (modifiers + name + extends/implements +
  body); file-name rule (recap [L0/C02/T01](content/L0-foundations/C02-java-core/T01-program-structure-class-main-statements.md));
  instance fields declaration; **field default values** (0/false/null
  contract — different from local-variable definite-assignment); a
  working class with methods (`Point.translate`, `Point.distanceTo`);
  instance method vs static method (the implicit `this` receiver — full
  coverage deferred to T02); the `new` expression and its three steps
  (allocate, init, return ref); calling instance methods; NPE on null
  receiver; the `null` reference. **The reference-vs-object two-layer
  mental model** as the single most important concept in the topic —
  reference = 4-byte address-like slot; object = heap blob; many refs
  → one object; reassignment vs mutation; reference types vs runtime
  types (upcasting preview). **Memory layer**: the `new` / `dup` /
  `invokespecial Point."<init>":()V` / `astore` bytecode quartet
  walked through `javap -c`, with an operand-stack diagram per step.
  **`<init>` vs `<clinit>`** distinction. The detailed step-by-step
  of what `new` does inside HotSpot (resolve, size, TLAB carve, zero,
  install header, push ref). **Object Memory Layout in the Heap** —
  the universal `[header 12 B][fields][padding]` shape on 64-bit
  compressed-oops HotSpot. **Object header byte layout**: 8-byte mark
  word (identity hash, lock state, GC age, marked bits) + 4-byte
  klass pointer (with compressed class pointers; uncompressed = 8).
  **Field layout by descending size** with `Point { int x, y }` (24
  bytes total = 12 + 4 + 4 + 4 pad) and the more interesting `Person {
  boolean, int, long, String }` showing the allocator reorders to
  `long → int → String → boolean`. Memory-cost table comparing
  Object/Point/Integer/Long/String/Person. Concrete-byte-count
  comparison: `Point[1_000_000]` = 24 MB vs `int[2_000_000]` = 8 MB
  (3× ratio with cache consequences). **Class loading & initialization
  five phases**: load, verify, prepare, resolve, initialize. Six
  triggers for class initialization (new, static-method invoke,
  static-field R/W except CT-constant, subclass init, Class.forName,
  main class). **Class metadata in Metaspace** (off-heap, post-Java-8,
  was PermGen), with a memory-map diagram showing heap vs Metaspace
  vs code cache vs per-thread stack. **Architecture layer**:
  **TLAB + bump-pointer allocation** — per-thread Eden slab, no
  lock, ~5-10 ns per allocation; the cost table (allocation, zeroing,
  header init, constructor, total ~15-30 ns); comparison to C++ new
  (~100-500 ns). **Compressed OOPs deep dive** — 32-bit encoded ref,
  3-bit shift to 64-bit, the 8-byte alignment trick = 32 GB heap with
  4-byte refs; the `-XX:+UseCompressedOops` toggle. **Escape analysis
  + scalar replacement** as the deepest mechanism in the topic —
  NoEscape/ArgEscape/GlobalEscape classification; scalar replacement
  promotes fields to registers; zero heap allocation; observable via
  `-XX:+PrintEliminateAllocations`. The `hypotSq` worked example.
  **Identity vs state** — `==` for reference identity (single CPU
  cmp); default `Object.equals` is identity too; `System.identityHashCode`;
  identity hash code cached in mark word. **Class hierarchy root —
  every class extends `Object`** — diagram, the 11 inherited methods,
  full coverage forward link to T09. **Object lifetime — birth to GC**:
  no destructor, no `delete`; reachability from GC roots (stack,
  statics, JNI, threads); no guaranteed reclaim timing; static
  collections as the canonical memory leak. **Generational GC preview**
  — Eden / Survivor / Old; promotion on age threshold; full
  coverage in L3/C02. **`instanceof` operator preview** including
  Java 16+ pattern-binding form. **Common mistakes** (9 traps): NPE
  on null receiver, forgot to `new`, reassignment-as-mutation, class
  vs instance confusion (Point.class vs an instance), object pooling
  hurts allocator, static-collection leak, field-order != memory-
  order, returning ref to mutable internal state, mutable HashMap
  key. **INTERVIEW callout** with 15 questions covering bytecode of
  `new`, object size, header contents, TLAB, why Java's `new` beats
  C++'s, escape analysis, compressed oops, class loading triggers,
  metadata location, `<init>` vs `<clinit>`, reference identity,
  field-order-vs-memory-order, GC roots, NPE timing, klass pointer.
  **Practice (18 exercises)** — declare a class, multi-ref vs one
  object, reassignment vs mutation, javap -c the `new`/`dup`/
  `invokespecial`/`astore`, javap -v constant pool inspection, JOL
  size dump for Integer and Point, JOL field-reorder observation,
  million-Point vs million-int memory benchmark, `-verbose:class`
  loading observation, static-initializer-runs-once observation,
  identity-vs-equals demo, NPE with helpful messages enabled,
  identity-hash-code cached in mark word, EA elimination via
  `PrintEliminateAllocations` + return-p kill, TLAB refill via
  `PrintTLAB`, compressed-oops on/off header-size shift, static
  leak repro + WeakReference fix, end-to-end "explain it back" of
  `new Point()`. Recap as ~25 learning objectives spanning all
  three layers. L1 progress now **1/49** (2%); C01 chapter **1/19**.
- **Next action:** author `L1/C01/T02` Fields, methods, constructors,
  this (see §4 brief).

## 7. Open Decisions & TODOs

- [ ] When the L0 session finishes (or pauses), **merge this file's §5/§6
      entries into `PROGRESS.md`** and delete this file. The canonical
      progress doc is `PROGRESS.md`.
- [ ] **L1/C01/README.md is still generator-output** (says all 19 topics
      are `planned`). Do not hand-edit it; it'll be regenerated when
      `python3 scripts/generate_skeleton.py` runs at end-of-session.
- [ ] Consider whether T02 (constructors, this) should split into two
      topics — constructors are deep enough alone. Current plan: one
      combined topic following the L0 chapter README order. Revisit
      after T02 draft.

## 8. Maintenance Protocol

Update **this file** while authoring L1 topics in this parallel session:

- **Finished a topic?** Set `status: complete` in the topic file's
  frontmatter; add a row to §5; move §4 Current Position to the next
  topic; append a Session Log entry to §6 (newest first); update §2 + §3
  counts.
- **At session end (or before merging back to `PROGRESS.md`):** verify
  everything in §5 has a real complete topic file. Confirm the L0
  session didn't also author the same topic (shouldn't happen but check).
