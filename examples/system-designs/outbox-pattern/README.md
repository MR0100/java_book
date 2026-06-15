# Transactional Outbox — reliable event publication without distributed transactions

> **Backs: L5/C02/T06 Distributed Transactions & Saga (outbox) + messaging**

A runnable Spring Boot 3.3 / Java 21 example of the **Transactional Outbox** pattern.
It boots on H2 with **no external database and no message broker** — the "broker" is a
pluggable `EventPublisher` interface whose default implementation just logs to stdout
and collects messages in memory. You can see the entire mechanic without installing
Kafka.

---

## The problem it solves: the dual-write problem

You want to do two things when an order is created: **save it to the database** and
**publish an `OrderCreated` event to a broker** (Kafka/RabbitMQ) so other services
(billing, shipping, analytics) react. The naive code is:

```java
orderRepository.save(order);          // 1. write to DB, commit
kafkaTemplate.send("orders", event);  // 2. publish to broker
```

These touch **two different systems with two independent commits**, and there is **no
transaction that spans both**. Every way a crash can interleave breaks consistency:

| Failure | Result |
|---|---|
| Crash **after** the DB commit, **before** the publish | Order exists, event **LOST** — downstream never hears about it |
| Publish first, then crash before the DB commit | Event published for an order that **doesn't exist** (phantom) |
| Broker accepts the send but the ack is lost → you retry | Event **double-published** |

You can't fix this with `try/catch`. The only way to atomically commit a DB row **and**
a broker message together is a **distributed transaction (XA / two-phase commit)** —
slow, operationally painful, and not supported by most modern brokers (Kafka included).

## The fix: outbox + relay

Don't write to the broker in the request path at all. Instead, in **one local database
transaction**, write **both**:

1. the business `Order` row, and
2. an `OutboxEvent` row describing the `OrderCreated` event.

Both are rows in the **same** database, so the database's ordinary single-resource
transaction makes them **commit-or-roll-back together** — atomic, no 2PC. See
`OrderService.createOrder()`.

A separate **relay** (`OutboxRelay`, a `@Scheduled` poller) then:

1. polls the outbox for unpublished rows (oldest first),
2. publishes each via the `EventPublisher` (→ the broker), and
3. marks each row `published = true`.

Because the relay's progress is itself a column in the database, it is **crash-safe and
restartable**: anything not yet marked published is simply picked up on the next poll.
No event is ever silently lost — the order's existence *guarantees* the event was
recorded, and the relay *guarantees* it eventually ships.

```
POST /api/orders ──▶ OrderService  ──(one TX)──▶  [orders] + [outbox_event]   (commit together)
                                                          │
                          @Scheduled  OutboxRelay  ◀───────┘  polls unpublished rows
                                       │
                                       ├─ publisher.publish(...)   ──▶ broker (Kafka)
                                       └─ row.markPublished()      ──▶ outbox_event.published = true
```

## At-least-once + idempotent consumers

Publishing to the broker and marking the row published are **two separate writes to two
systems** and cannot be made atomic (again, that's the thing we're avoiding). The relay
does them publish-**then**-mark. If it crashes in between, the broker already has the
message but our row still says `published = false`, so the next poll **publishes it
again**.

That makes delivery **at-least-once**, never exactly-once: a consumer may see the same
event more than once. **Consumers must therefore be idempotent.** The standard technique
is to deduplicate on the event's stable id — `OutboxEvent.eventId`, carried to the broker
as `PublishedMessage.eventId` — e.g. a `processed_events` table the consumer inserts on
first sight, or an upsert keyed on that id.

> Cross-reference: the dedicated **idempotency / idempotent-consumer** example covers
> that consumer side in depth.

## Production alternative to polling: CDC (Debezium)

Polling adds latency (bounded by the poll interval) and constant query load. The
production-grade alternative is **Change Data Capture**: a tool like **Debezium** tails
the database's transaction log (Postgres WAL / MySQL binlog) and streams new `outbox_event`
rows to Kafka with **no polling at all**. It's the same outbox table and the same
at-least-once + idempotency guarantees — just a log-tailing relay instead of a
`@Scheduled` one. Debezium even ships a dedicated *Outbox Event Router* for exactly this.

---

## Prerequisites

- **JDK 21+** (the project targets Java 21 bytecode; building on a newer JDK is fine)
- **Maven 3.9+**
- No database, no Kafka, no Docker — H2 and the in-memory publisher are bundled.

## Run commands

```bash
# Run the tests (proves the flow + the atomic rollback)
mvn test

# Start the app (embedded Tomcat on :8080)
mvn spring-boot:run
```

Create an order — this does the atomic order + outbox write:

```bash
curl -s -X POST http://localhost:8080/api/orders \
  -H 'Content-Type: application/json' \
  -d '{"customer":"alice","product":"widget","quantity":3,"amount":29.97}'
```

Response (`201 Created`):

```json
{"id":1,"customer":"alice","product":"widget","quantity":3,"amount":29.97,"createdAt":"..."}
```

Within ~1 second the relay polls and "publishes" the event. Watch the application log:

```
... OrderController  : (returns immediately — no broker call in the request path)
... LoggingEventPublisher : PUBLISH topic=orders key=1 eventId=<uuid> type=OrderCreated payload={"orderId":1,"customer":"alice",...}
```

You can also open the **H2 console** at `http://localhost:8080/h2-console`
(JDBC URL `jdbc:h2:mem:outbox`, user `sa`, no password) and watch
`SELECT * FROM OUTBOX_EVENT` flip `PUBLISHED` from `FALSE` to `TRUE`.

## Expected test output

```
OutboxFlowIntegrationTest
  ✓ createOrderWritesUnpublishedOutboxRow_thenRelayPublishesIt
  ✓ relayIsIdempotentAcrossRuns_publishesEachRowOnlyOnce
OutboxAtomicWriteRollbackTest
  ✓ orderRollsBackWhenOutboxWriteFails

BUILD SUCCESS — Tests run: 3, Failures: 0, Errors: 0, Skipped: 0
```

---

## Files to read first

1. **`OrderService.java`** — the atomic dual write; the single `@Transactional` method
   that saves the order *and* the outbox row together. The long comment here explains the
   dual-write failure modes and why this is the fix.
2. **`OutboxEvent.java`** — the outbox row: why each field exists, and the
   at-least-once / idempotency note (`eventId` as the consumer's dedupe key).
3. **`OutboxRelay.java`** — the `@Scheduled` poller: publish-then-mark, at-least-once
   semantics, multi-instance safety (`FOR UPDATE SKIP LOCKED`), and the CDC/Debezium note.
4. **`EventPublisher.java`** + **`LoggingEventPublisher.java`** — the broker seam that
   lets this run with zero infrastructure (swap for a `KafkaEventPublisher` in prod).
5. **`OutboxFlowIntegrationTest.java`** / **`OutboxAtomicWriteRollbackTest.java`** — the
   happy path and the commit-together / roll-back-together proof.

## Project layout

```
src/main/java/com/javamastery/examples/outbox/
├── OutboxApplication.java            # @SpringBootApplication + @EnableScheduling
├── controller/OrderController.java   # POST /api/orders
├── service/OrderService.java         # the atomic order + outbox write (@Transactional)
├── relay/OutboxRelay.java            # @Scheduled poller: publish + mark published
├── publisher/
│   ├── EventPublisher.java           # broker abstraction (the seam)
│   ├── LoggingEventPublisher.java    # default: logs + collects in memory (no broker)
│   └── PublishedMessage.java         # message shape handed to the publisher
├── entity/
│   ├── OrderEntity.java              # business entity (table: orders)
│   └── OutboxEvent.java              # the outbox row
├── repository/
│   ├── OrderRepository.java
│   └── OutboxEventRepository.java    # findUnpublishedBatch(...) with SKIP LOCKED
└── dto/
    ├── CreateOrderRequest.java       # record — request body
    ├── OrderResponse.java            # record — response body
    └── OrderCreatedEvent.java        # record — the event payload (serialized to outbox)
```
