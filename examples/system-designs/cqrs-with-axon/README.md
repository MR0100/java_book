# CQRS — Command Query Responsibility Segregation (hand-rolled, no Axon)

**Backs: L5/C01 Software Architecture (CQRS)**

A small, fully runnable Spring Boot example that implements **CQRS** for a `Product`
catalog: **writes** go through *commands* against a normalized write model, **reads**
hit a **separate, denormalized read model** that an event-driven **projection** keeps up
to date. It runs entirely on in-memory **H2** with **zero external infrastructure** — no
broker, no Axon, no event store server.

> **Why is the directory called `cqrs-with-axon` if there's no Axon?**
> CQRS is a *pattern*, not a library. To keep this example self-contained and runnable
> with one command, it hand-rolls the pattern with plain Spring (an in-process event bus,
> two JPA models, an `@TransactionalEventListener` projection). The directory name is kept
> for traceability, and the section **"How Axon Framework would do this"** below maps every
> hand-rolled piece to its Axon equivalent so you know the productized path.

---

## What is CQRS?

Most applications use **one model** for both reading and writing. CQRS splits that in two:

- **Command side (write model):** shaped for *correctness and invariants*. It accepts
  **commands** ("create this product", "change this price"), validates them, mutates the
  authoritative state, and emits **domain events** describing what happened. It is
  normalized and contention-aware.
- **Query side (read model):** shaped for *fast, convenient reads*. It is a **separate
  schema**, often denormalized (precomputed display fields, derived flags, rollups). It is
  populated by **projections** that consume the write side's events. Queries never touch
  the write model.

```
 POST /products            CreateProduct ─┐
 POST /products/{id}/price ChangePrice    ├─▶ ProductCommandService ──(validate, mutate)──▶ product_write (write model)
 POST /products/{id}/stock AdjustStock   ─┘            │
                                                       │  publishes domain EVENTS
                                                       ▼  (in-process ApplicationEventPublisher)
                                          ProductProjection  (@TransactionalEventListener, AFTER_COMMIT)
                                                       │  denormalizes
                                                       ▼
 GET /products            ◀── reads ONLY ── product_view (read model)
 GET /products/{id}            the read model
```

### Why split read and write?

- **Independent scaling.** Most systems are *read-heavy* (often 10:1, 100:1, or more).
  With separate models you can scale, cache, and replicate the read side aggressively
  (read replicas, search indexes, caches) without touching write throughput or correctness.
- **Independent optimization.** The write model stays normalized and invariant-focused.
  The read model is denormalized for the exact queries the UI/API needs — no joins, no
  on-the-fly formatting. You pay the denormalization cost *once* at projection time, not on
  every read.
- **Independent evolution.** The read schema can change or be rebuilt (replay the events)
  without altering the write schema. The only contract between the two sides is the **event
  stream**. (This repo's `ReadWriteSeparationTest` asserts exactly this independence.)
- **Multiple read models.** You can have several projections off the same events — one for a
  list view, one for a search index, one for analytics — each optimized differently.

### Relationship to Event Sourcing (and that they're separable)

CQRS and **Event Sourcing (ES)** are frequently mentioned together but are **independent**
choices:

- **CQRS without ES (this example):** the write model stores *current state* in a regular
  table (`product_write`). Events are emitted *to update the read model*, but they are not
  the system of record — if you lose them you still have the write table.
- **ES adds:** the events themselves become the **source of truth**. Instead of storing
  current state, you append every event to an **event store** and rebuild state by replaying
  them. The read model is then *always* a projection of the event log, and you can replay to
  rebuild it from scratch or build brand-new projections retroactively.

You can do CQRS with a plain CRUD write side (here), and you can do ES without splitting read
and write. They compose well, but adopting one does **not** require the other.

### Eventual consistency between the two sides

Because the read model is updated *after* and *separately from* the write, there is a window
where a successful write is not yet visible to queries. This example makes that gap explicit:

- The projection is a **`@TransactionalEventListener(phase = AFTER_COMMIT)`**, so it runs
  **only after the write transaction commits** (a rolled-back command never touches the read
  model) and in its **own** transaction (`REQUIRES_NEW`).
- Here the lag is microseconds (in-process, same JVM). In a real distributed system the event
  crosses a **broker** and the lag is milliseconds-to-seconds. *The pattern is identical; only
  the latency changes.*

**Consequences you must design for:**

- **Reads can be stale.** A client that POSTs a command and immediately GETs may not see its
  own write yet. The controller here returns `201`/`202` with an id (not the projected view)
  precisely because the view may not exist yet. (Patterns to cope: read-your-writes via the
  write side for that one read, client-side optimistic UI, version/ETag polling.)
- **Projections must be idempotent.** Events can be redelivered (at-least-once). The handlers
  here upsert, so replaying an event is harmless.

The tests assert convergence with **Awaitility** (`await().untilAsserted(...)`) rather than
assuming the read model is instantly consistent — the honest way to test an eventually
consistent view.

---

## How Axon Framework would do this

Axon is the mainstream Java framework that *productizes* CQRS (and optionally ES). Mapping
this example onto Axon:

| Hand-rolled here | Axon Framework equivalent |
| --- | --- |
| `Commands.*` records | Command messages dispatched via the **`CommandGateway`** |
| `ProductCommandService` (validate → mutate → publish) | An **`@Aggregate`** class with **`@CommandHandler`** methods; state changes are applied by calling `AggregateLifecycle.apply(event)` |
| `Events.*` records | Event messages; `@EventSourcingHandler` methods inside the aggregate rebuild its state from them |
| `ApplicationEventPublisher` (in-process bus) | Axon's **event bus / event store**; events are persisted and distributed |
| (no event store — write table is source of truth) | The **event store** (e.g. Axon Server, or JPA/JDBC) makes events the source of truth = **Event Sourcing** |
| `ProductProjection` (`@TransactionalEventListener`) | A projection bean with **`@EventHandler`** methods, grouped by **`@ProcessingGroup`** |
| "run after commit, idempotent upsert" | A **tracking event processor** that streams events from the store with a tracking token, supports **replay** (rebuild the read model), and gives at-least-once delivery (hence idempotent handlers) |
| `ProductQueryService` | Either direct repository reads, or Axon's **`QueryGateway`** + **`@QueryHandler`** (incl. subscription queries for live-updating reads) |

The key thing Axon buys you over this hand-rolled version: a **persistent event store with
tracking tokens and replay**, **distributed** command/event routing, and **subscription
queries**. The *concepts* — commands, aggregates, events, projections, eventual consistency —
are exactly what you see in this code.

---

## When CQRS is overkill

CQRS adds real cost: two models to keep in sync, a projection to maintain, eventual-consistency
bugs, more moving parts to test and operate. **Don't reach for it by default.** Skip it when:

- The domain is simple **CRUD** with no meaningful read/write asymmetry.
- Read and write loads are comparable and a single model serves both well.
- The team can't absorb the operational complexity (extra schemas, projections, monitoring lag).
- You need **strong** read-after-write consistency everywhere and can't tolerate any staleness.

Often you get 80% of the benefit more cheaply: read replicas, a cache, or simply separate
*read DTOs / query methods* over one model (sometimes called "CQRS-lite") without a separate
persisted read store. Adopt full CQRS when a **specific** part of the system is read-heavy,
needs differently-shaped reads, or must scale read and write independently.

---

## Prerequisites

- **JDK 21+** (the project targets Java 21 bytecode; it compiles and runs fine on newer JDKs
  via `--release 21`, which the Spring Boot parent configures automatically).
- **Maven 3.9+** (or use the bundled `mvnw` if present).
- No database, broker, or Axon install needed — H2 is in-memory and on the classpath.

## Run commands

```bash
# from this directory
mvn test          # compile + run the 9 tests (write→read flow, separation, web layer)
mvn spring-boot:run   # start the app on http://localhost:8080
```

Try the endpoints once it's running:

```bash
# WRITE: send a command (returns 201 + the new id)
curl -i -X POST localhost:8080/products \
  -H 'Content-Type: application/json' \
  -d '{"sku":"PEN-1","name":"Pen","price":1.50,"initialStock":100}'

# READ: query the denormalized read model (use the id from above)
curl localhost:8080/products/1
curl localhost:8080/products            # all
curl 'localhost:8080/products?inStock=true'  # in-stock only (read-model-shaped query)

# change price / stock (commands)
curl -X POST localhost:8080/products/1/price -H 'Content-Type: application/json' -d '{"newPrice":1.25}'
curl -X POST localhost:8080/products/1/stock -H 'Content-Type: application/json' -d '{"delta":-100}'
```

Inspect both models side by side at the H2 console: <http://localhost:8080/h2-console>
(JDBC URL `jdbc:h2:mem:cqrs`, user `sa`, no password), then:

```sql
SELECT * FROM PRODUCT_WRITE;   -- normalized write model (has a `version` column)
SELECT * FROM PRODUCT_VIEW;    -- denormalized read model (priceFormatted, inStock, displayLabel, lastUpdated)
```

## Expected output

`mvn test` ends with:

```
Tests run: 9, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

`mvn spring-boot:run` seeds two products via commands and logs the projected read model:

```
=== CQRS demo: sending commands (writes) ===
Created product id=1 (WIDGET-1)
Created product id=2 (GADGET-1)
Changed GADGET-1 price to 19.50 and drained its stock to 0
=== Querying the READ model (projection output) ===
Widget (WIDGET-1) $9.99 — in stock   [stock=50]
Gadget (GADGET-1) $19.50 — sold out  [stock=0]
In-stock-only query returns 1 product(s): [WIDGET-1]
```

Notice the read model shows `priceFormatted`, the `in stock` / `sold out` flag, and the
`displayLabel` — fields the **write** model never stores. That denormalization is the projection's job.

## Files to read first

1. **`command/Commands.java`** + **`event/Events.java`** — the write-side vocabulary (intent)
   vs. the facts emitted. The C and the "what flows downstream".
2. **`write/ProductCommandService.java`** — the command handler: validate → mutate write model →
   publish event. Note it has **no** dependency on the read side.
3. **`read/ProductProjection.java`** — the projection: consumes events `AFTER_COMMIT` and
   denormalizes them into the read model. This is the bridge and the source of eventual consistency.
4. **`read/ProductView.java`** — the denormalized read model; compare its fields to
   `write/Product.java` to see the schemas are independent.
5. **`query/ProductQueryService.java`** + **`api/ProductController.java`** — the Q side: reads
   only the read model; the controller shows the POST(command)/GET(query) split.
6. **`CqrsFlowTest.java`** and **`ReadWriteSeparationTest.java`** — prove the write changes *and*
   the read model converges, and that the two models are genuinely decoupled.
```
