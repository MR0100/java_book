---
title: "L1 Resources"
slug: l1-resources
level: L1
module: "Core Java & OOP"
section: "Resources"
type: resources
difficulty: intermediate
order: 1
tags: [resources, books, effective-java, docs, jeps, specs, testing, junit, tdd, talks, reading-paths, bibliography]
prerequisites: []
status: complete
last_updated: 2026-06-05
---

# L1 Resources

Annotated reading and watch list for going beyond this book at L1 level — object-oriented Java, the collections framework and core APIs, and your first real testing. Honest opinions on what's worth your time *now* versus what to save for L3/L4. Plus **curated paths by goal** and a **what-not-to-read-yet** section so you don't drown.

> [!TIP]
> If you read one thing from this page, read **Effective Java** (below). It *is* the L1–L2 curriculum in book form, and nearly every idiom and pitfall in C06 traces back to it.

## The One Book — Effective Java (3rd ed.)

- **Effective Java, 3rd Edition** — Joshua Bloch. <https://www.oreilly.com/library/view/effective-java-3rd/9780134686097/>

The single highest-leverage book at this level, by the author of the Java collections framework. It's 90 short "items," each a focused best-practice with the *why*. The mapping to this module is almost one-to-one:

| EJ items | L1 topic |
|---|---|
| 10–14 (`equals`/`hashCode`/`Comparable`) | [C01/T10](../C01-oop/T10-equals-hashcode-tostring-contracts.md), [C02/T07](../C02-collections-and-core-apis/T07-comparable-vs-comparator.md) |
| 15–18 (accessibility, immutability, composition) | [C01/T03](../C01-oop/T03-encapsulation-and-access-modifiers.md), [T04](../C01-oop/T04-inheritance-and-super.md), [T19](../C01-oop/T19-immutability-and-immutable-class-design.md) |
| 20–22, 34–38 (interfaces, enums) | [C01/T08](../C01-oop/T08-interfaces-default-static-private-methods.md), [T13](../C01-oop/T13-enum-types-with-fields-methods.md) |
| 26–33 (generics) | [C02/T11–T12](../C02-collections-and-core-apis/T12-generics-bounded-types-wildcards-type-erasure.md) |
| 54–55 (empty collections, `Optional`) | [C02/T19](../C02-collections-and-core-apis/T19-optional.md) |
| 69–77 (exceptions) | [C02/T09–T10](../C02-collections-and-core-apis/T09-exceptions-try-catch-finally-checked-vs-unchecked.md) |

**How to read it:** don't read cover-to-cover first pass — read the item for the topic you just studied here, then revisit the whole book after L2. It rewards re-reading for years.

## Books

- **Core Java, Vol. I — Fundamentals (12th ed.)** — Cay Horstmann. The thorough, reliable reference for the language and core libraries. Use it as a lookup companion, not a cover-to-cover read.
- **Modern Java in Action** — Urma, Fusco, Mycroft. The best treatment of lambdas, streams, `Optional`, and the functional side of modern Java — bridges into L2.
- **Java Generics and Collections** — Naftalin & Wadler. The deep dive on exactly C02's generics + collections; read it when erasure and wildcards stop making sense.
- **Head First Java (3rd ed.)** — Sierra, Bates, Robson. Gentle, visual, great if OOP hasn't *clicked* yet — but lighter than this book's depth.
- *(Testing books have their own section below.)*

## Testing Resources

The C03 testing stack has excellent first-party docs — use them over blog posts:

- **JUnit 5 User Guide** — <https://junit.org/junit5/docs/current/user-guide/> — the authoritative reference; the lifecycle and parameterized-test sections are gold.
- **Mockito** — <https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html> — the class Javadoc is a surprisingly good tutorial.
- **AssertJ** — <https://assertj.github.io/doc/> — fluent-assertion reference.
- **"Mocks Aren't Stubs"** — Martin Fowler. <https://martinfowler.com/articles/mocksArentStubs.html> — the definitive piece on stub-vs-mock and the London/Detroit schools ([C03/T04](../C03-testing-fundamentals/T04-test-doubles-stub-mock-spy-fake.md)).
- **Test-Driven Development by Example** — Kent Beck. The original TDD book; short, practical, still the best ([C03/T06](../C03-testing-fundamentals/T06-test-driven-development-tdd.md)).
- **Growing Object-Oriented Software, Guided by Tests (GOOS)** — Freeman & Pryce. The London-school, outside-in, mock-driven design book — read after you're comfortable with TDD.
- **xUnit Test Patterns** — Gerard Meszaros. The test-double taxonomy (dummy/stub/spy/mock/fake) comes from here; a reference, not a page-turner.
- **"Is TDD Dead?"** — the 2014 DHH / Beck / Fowler conversations. <https://martinfowler.com/articles/is-tdd-dead/> — the honest, nuanced take on TDD's value and limits.

## Build & Tooling Docs

- **Maven** — <https://maven.apache.org/guides/getting-started/> — the lifecycle + dependency-mechanism guides cover everything in [C04](../C04-tools-and-environment/T01-build-dependencies-and-project-tooling.md).
- **Gradle User Manual** — <https://docs.gradle.org/current/userguide/userguide.html> — dense but authoritative.
- **Maven Central search** — <https://central.sonatype.com/> — where you find a library's exact coordinates.
- **JaCoCo** — <https://www.jacoco.org/jacoco/trunk/doc/> — coverage counters + how the agent instruments bytecode ([C03/T07](../C03-testing-fundamentals/T07-test-coverage-jacoco.md)).
- **Error Prone** — <https://errorprone.info/> — its bug-pattern catalogue reads like a pitfalls list ([C06/T02](../C06-best-practices/T02-l1-pitfalls-catalogue.md)).

## Official Documentation

- **Java SE 21 API docs** — <https://docs.oracle.com/en/java/javase/21/docs/api/> — bookmark it. At L1 you live in `java.util` (collections), `java.lang` (Object, wrappers, String), `java.time`, `java.util.regex`, `java.util.stream`, and `java.io`/`java.nio.file`.
- **The Java Tutorials / dev.java** — <https://dev.java/learn/> — the official, current learning portal (Collections, Generics, Lambdas, Records, Sealed Classes trails map directly onto C01–C02).
- **The Collections Framework Overview** — the design FAQ + the `java.util` package doc explain *why* the hierarchy is shaped as it is.

## Online Learning

- **Baeldung** — <https://www.baeldung.com/> — the L1 workhorse. There's a focused, example-driven article on virtually every collection, exception, generics question, JUnit feature, and Mockito pattern. Search "baeldung <topic>"; trust it for *how*, cross-check the API docs for *exact* behaviour.
- **Jenkov Tutorials** — <https://jenkov.com/> — clear, deep single-topic write-ups (collections, generics, java.io).
- **Java Guides / official trails** — solid for the modern-feature trails (records, sealed types, pattern matching).

## YouTube Channels

- **Coding with John** — <https://www.youtube.com/@CodingWithJohn> — short, crystal-clear videos on *exactly* L1 topics (`equals`/`hashCode`, generics, `Optional`, exceptions, streams). Probably the single best free video resource for this level.
- **Java Brains** (Koushik Kothagal) — structured, well-paced series on core Java (and Spring later).
- **Telusko** / **Amigoscode** — beginner-friendly, broad coverage; good for a second explanation when something doesn't click.
- **Nicolai Parlog (@nipafx)** — modern-Java features (records, sealed types, pattern matching) from someone on the JDK advocacy side.

## JEPs — The Modern Language Features

The C01 features you learned have JEPs worth skimming for the *design rationale*:

| Feature | JEP | L1 topic |
|---|---|---|
| Records | [JEP 395](https://openjdk.org/jeps/395) | [C01/T14](../C01-oop/T14-record-types.md) |
| Sealed Classes | [JEP 409](https://openjdk.org/jeps/409) | [C01/T15](../C01-oop/T15-sealed-classes-and-interfaces.md) |
| Pattern Matching for `instanceof` | [JEP 394](https://openjdk.org/jeps/394) | C01 |
| Switch Expressions | [JEP 361](https://openjdk.org/jeps/361) | C01/C02 |
| Text Blocks | [JEP 378](https://openjdk.org/jeps/378) | strings |

**Inside Java** — <https://inside.java/> — the JDK team's blog/podcast; the best way to follow where the language is going.

## Standards & Specs (skim, don't memorise)

- **The Java Language Specification (JLS)** — <https://docs.oracle.com/javase/specs/> — the ground truth. At L1, the useful chapters are §8 (Classes), §9 (Interfaces), §4.2.4 (floating point), and §15.12 (overload resolution). Read a *section* when a behaviour surprises you — never cover-to-cover.
- **Unicode / CLDR** — underpins i18n ([C02/T23](../C02-collections-and-core-apis/T23-internationalization-i18n-and-formatting.md)); reference only.

## Talks (Annotated)

- **Venkat Subramaniam** — anything. His talks on OOP done right, functional Java, and refactoring are entertaining and deep. Start with "Twelve Ways to Make Code Suck Less."
- **Brian Goetz** — "Stewardship: the Sobering Parts" and his records/sealed-classes design talks explain *why* the language is the way it is.
- **Kevlin Henney** — "Seven Ineffective Coding Habits" and "Programming with GUTs" — sharp on OOP design and testing mindset.
- **Trisha Gee** — practical IntelliJ + testing + modern-Java talks aimed exactly at the L1→L2 developer.

## GitHub Repos to Study

- **OpenJDK source** — <https://github.com/openjdk/jdk> — read the real implementations behind C02: `java.util.ArrayList`, `HashMap` (the treeification + resize logic), `Objects`, `Optional`. More readable than you'd expect, and it makes the Big-O and the contracts concrete.
- **TheAlgorithms/Java** — <https://github.com/TheAlgorithms/Java> — clean reference implementations to compare your kata solutions against ([C05 exercises](../C05-hands-on/T01-exercises.md)).
- **akullpp/awesome-java** — <https://github.com/akullpp/awesome-java> — the curated index of the Java library ecosystem; where to look when you need "a library for X."
- **junit5** / **mockito** repos — reading a mature project's *own* test suite is one of the best ways to learn idiomatic testing.

## Curated Reading Paths by Goal

- **"I have a Java interview in 3 weeks"** → this module's [C07 questions](../C07-interview-prep/T01-core-java-and-oop-questions.md) + [C06 idioms/pitfalls](../C06-best-practices/T01-l1-idioms.md) → Effective Java items on equals/hashCode, generics, enums, exceptions → Baeldung for any gap.
- **"I want to write clean OOP"** → Effective Java items 15–22 + GOOS → practice by refactoring the [C05 project](../C05-hands-on/T02-project-library-management-system.md).
- **"I want to get good at testing"** → JUnit 5 guide → "Mocks Aren't Stubs" → TDD by Example → GOOS → kata practice ([C05 exercises](../C05-hands-on/T01-exercises.md) #17–#19).
- **"I want depth on collections/generics"** → Java Generics and Collections → the `java.util` package docs → read the OpenJDK `ArrayList`/`HashMap` source.

## What NOT to Read Yet

Save these — they'll make more sense (and matter more) after L1:

- **Java Concurrency in Practice (Goetz)** — the concurrency bible, but it's **L3** material. The `synchronized`/`volatile`/JMM content will overwhelm now.
- **Spring / Spring Boot docs** — **L4**. Learn the language and testing first; frameworks make sense once the fundamentals are reflex.
- **"Optimizing Java" / Aleksey Shipilev's blog / deep GC tuning** — **L3**. Fascinating, but premature.
- **Gang of Four "Design Patterns"** — read *selectively* (Strategy, Factory, Builder), but L3 has a dedicated patterns chapter; don't pattern-ify everything now.
- **The full JLS/JVMS** — a reference to *consult*, never to read through.

## Communities & Staying Current

- **r/java** and **r/learnjava** — the latter is genuinely beginner-friendly.
- **Stack Overflow** — read the high-voted answers on the canonical questions (e.g. "Why doesn't `==` work on Strings?") — they're often better than tutorials.
- **dev.java** + **Inside Java** newsletter/podcast — the official pulse of the platform.

## Recap

The short version: **read Effective Java alongside this module**, use the **first-party docs** (Java API, JUnit 5, Maven) over random blogs, lean on **Baeldung** for quick how-tos, skim the **JEPs** for the modern features, and **defer concurrency, Spring, and deep JVM tuning** to later levels. Pick a path above that matches your goal and follow it — breadth without a goal just produces bookmarks you never open.

## Next

This closes the `L1/C10` Resources chapter — and with it, **L1 — Core Java & OOP** in full. Continue to **[L2 — Intermediate Java & Backend Foundations](../../L2-intermediate-backend/)**: functional & modern Java, build tools in depth, networking, web & REST, and databases — where the object-oriented, tested, well-built foundation you assembled here becomes a real backend.
