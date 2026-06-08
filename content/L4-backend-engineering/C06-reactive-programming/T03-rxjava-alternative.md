---
title: "RxJava (alternative)"
slug: rxjava-alternative
level: L4
module: "Backend Engineering"
section: "Reactive Programming"
type: concept
difficulty: senior
order: 3
tags: [rxjava, rxjava-3, observable, flowable, single, maybe, completable, rxjava-vs-reactor, netflix, rx-java-history, android-rxjava, reactive-streams-compat, reactor-bridge]
prerequisites: [project-reactor-mono-flux]
status: complete
estimated_minutes: 30
last_updated: 2026-06-08
---

# RxJava (alternative)

RxJava (Netflix, 2014) was the **first major Java reactive library**, predating Reactive Streams and Reactor. It popularized the operator-based reactive style in JVM ecosystems. Today (2026) it's still widely used — Android development standardized on RxJava 2/3; many older Java backend services use it. **Project Reactor (T02) is the Spring default** in 2026; new Spring services typically don't touch RxJava. But understanding it matters when joining a team with existing RxJava code or maintaining Android-related backends.

This is a short comparison topic. The concepts are identical — same Reactive Streams protocol, same Backpressure, same operator catalog. The differences are API naming, type hierarchy, and ecosystem.

> [!NOTE]
> Prerequisites: [Project Reactor (T02)](./T02-project-reactor-mono-flux.md).

## The Type Hierarchy

| RxJava | Reactor | Cardinality |
|--------|---------|------------|
| `Single<T>` | `Mono<T>` | 1 |
| `Maybe<T>` | `Mono<T>` | 0..1 |
| `Completable` | `Mono<Void>` | only completion signal |
| `Observable<T>` | (no equivalent; ≈ Flux without backpressure) | 0..N (no BP) |
| `Flowable<T>` | `Flux<T>` | 0..N with backpressure |

RxJava 1 had only `Observable`; RxJava 2 added `Flowable` for backpressure; the distinction is: use `Flowable` for unbounded streams; `Observable` for finite or fast small streams without backpressure concerns.

`Single` and `Maybe` and `Completable` are conceptual specializations of `Mono` — different return types per intent.

## Code Comparison

### Simple Map

```java
// RxJava
Flowable.range(1, 100)
    .filter(n -> n % 2 == 0)
    .map(n -> n * 10)
    .take(5)
    .subscribe(System.out::println);

// Reactor (identical)
Flux.range(1, 100)
    .filter(n -> n % 2 == 0)
    .map(n -> n * 10)
    .take(5)
    .subscribe(System.out::println);
```

Nearly identical at the operator level.

### Async Composition

```java
// RxJava
userRepo.findById(42)                // Single<User>
    .flatMap(user -> orderRepo.findRecent(user.id())) // Single<List<Order>>
    .subscribe(orders -> log.info("got {}", orders));

// Reactor
userRepo.findById(42L)               // Mono<User>
    .flatMap(user -> orderRepo.findRecent(user.getId())) // Mono<List<Order>>
    .subscribe(orders -> log.info("got {}", orders));
```

Same structure.

### Schedulers

```java
// RxJava
flowable.subscribeOn(Schedulers.io())
        .observeOn(Schedulers.computation());

// Reactor
flux.subscribeOn(Schedulers.boundedElastic())
    .publishOn(Schedulers.parallel());
```

`observeOn` ↔ `publishOn`. Scheduler names differ:

| RxJava | Reactor |
|--------|---------|
| `Schedulers.io()` | `Schedulers.boundedElastic()` |
| `Schedulers.computation()` | `Schedulers.parallel()` |
| `Schedulers.single()` | `Schedulers.single()` |
| `Schedulers.trampoline()` | `Schedulers.immediate()` |
| `Schedulers.from(Executor)` | `Schedulers.fromExecutor(Executor)` |

## When Teams Pick RxJava

- **Android development**: Kotlin-Coroutines is winning, but RxJava is still common.
- **Legacy Java services** built before Reactor was mature.
- **Cross-platform** (Android + backend) sharing reactive code.
- **Specific operator** RxJava has and Reactor doesn't (rare; both are feature-complete).

## When Spring Teams Pick Reactor

- **Default for Spring WebFlux**.
- **Reactor Context** integrates with Spring Security / Sleuth / Micrometer.
- **R2DBC** drivers are Reactor-based.

If you're greenfield Spring, **use Reactor**. If you're maintaining or interop'ing with Android, RxJava is fine.

## Interop — Both Sides Of The Bridge

Both implement Reactive Streams; convert easily:

```java
// RxJava → Reactor
Flowable<String> rxFlowable = Flowable.just("a", "b");
Flux<String> flux = Flux.from(rxFlowable);

// Reactor → RxJava
Flux<String> flux = Flux.just("a", "b");
Flowable<String> rxFlowable = Flowable.fromPublisher(flux);
```

`Single<T>` ↔ `Mono<T>`:

```java
Single<String> single = ...;
Mono<String> mono = Mono.from(single.toFlowable());

Mono<String> mono = ...;
Single<String> single = Single.fromPublisher(mono);
```

## Reactor Has Won The Spring Story

For new Spring code in 2026, **don't use RxJava**. Reactor + Reactor Context + Spring Reactor Add-ons / Tracing is the integrated story. Interop is for talking to existing libraries.

## Common Pitfalls

> [!WARNING]
> **Mixing RxJava and Reactor in one chain.** Possible but confusing; pick one per service.

> [!WARNING]
> **RxJava 2 vs 3 incompatibility.** Different package names. Pin the major version.

> [!WARNING]
> **`Observable` for backpressure-requiring streams.** Use `Flowable`.

> [!WARNING]
> **Backpressure-related OOM.** Confirm you're on `Flowable`, not `Observable`.

## Practice

1. Convert a small Reactor pipeline to RxJava equivalent; compare line-by-line.
2. Bridge: produce with Reactor; consume with RxJava client. Verify works.
3. Compare RxJava 3 scheduler names to Reactor's; map your service's usage.
4. Decide: would your Spring app benefit from RxJava? Usually no.

## Recap

You should now be able to:

- Map RxJava types (Single / Maybe / Completable / Observable / Flowable) to Reactor (Mono / Flux).
- Translate operators / schedulers between the two.
- Bridge via Reactive Streams interfaces.
- Default to Reactor for Spring; recognize RxJava in Android / legacy contexts.

## Next

Continue to [Backpressure](./T04-backpressure.md) for the deep treatment of demand control — the core innovation of Reactive Streams — and the strategies for hot publishers, slow consumers, and overload protection.
