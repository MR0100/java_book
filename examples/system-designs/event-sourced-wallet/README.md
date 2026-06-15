# Event-Sourced Wallet — balance derived by replaying an append-only log

> **Backs: Event Sourcing — ties to L5/C12/T02 Stripe (ledgers) + L6/C14/T04 (idempotency / ledgers)**

A runnable Spring Boot 3.3 / Java 21 example of **event sourcing** for a money wallet /
ledger. The wallet's balance is **never stored as a mutable column**. Instead, every change
is recorded as an immutable **domain event** in an append-only `event_store` table, and the
current balance is **recomputed on demand by replaying** that wallet's events (a `fold`).
Invariants — chiefly *no overdraft* — are enforced on the **command side, before** an event
is ever appended.

It boots on **in-memory H2 with zero external infrastructure** — no Postgres/MySQL, no
broker, no cache.

---

## The idea in one line

```
balance(wallet) = events(wallet).fold(0, applyEvent)      // state is a function of history
```

A traditional design keeps a `balance` column and does `UPDATE wallet SET balance = balance - 300`.
That throws away *how* you got there: a single number with no history. Event sourcing flips it —
the **log of facts is the source of truth**, and the balance is a disposable projection you can
rebuild any time:

| | Traditional (state-stored) | Event-sourced (this example) |
|---|---|---|
| Source of truth | the `balance` column | the ordered `event_store` rows |
| A withdrawal | `UPDATE ... SET balance = balance - x` | `INSERT MoneyWithdrawn(x)` |
| Read balance | `SELECT balance` | replay/fold the event stream |
| Audit trail | bolt-on, often lossy | **inherent and complete** |
| "Balance last Tuesday?" | impossible (overwritten) | replay the prefix of the log |
| Bug in a past calc | corrupted, unrecoverable | fix the fold, **replay to correct** |

---

## How it works here

### 1. Events are the source of truth (sealed interface + records)

`WalletEvent` is a **sealed interface** permitting four immutable **records**:
`MoneyDeposited`, `MoneyWithdrawn`, `MoneyTransferredOut`, `MoneyTransferredIn`. Past-tense
names because each one is a fact that *already happened*. `sealed` + `record` gives an
exhaustive, immutable, value-based model — the compiler forces every fold/switch to handle
the complete set of event types.

### 2. The append-only store

`EventStoreEntry` is the **only** table: `(id, aggregate_id, sequence_number, event_type,
payload JSON, occurred_at)`. There is **no wallet table and no balance column**. Rows are only
ever `INSERT`ed — nothing in the codebase updates or deletes one. A unique constraint on
`(aggregate_id, sequence_number)` gives per-wallet ordering and optimistic concurrency (two
concurrent appends at the same sequence collide; one loses and retries).

### 3. The aggregate is a fold (`Wallet`)

`Wallet.replay(id, events)` starts from `empty` and applies each event left-to-right. `apply`
is a **pure** transition (deposits/credits add, withdrawals/debits subtract) and is exhaustive
over the sealed hierarchy. The `Wallet` is immutable — each `apply` returns a new value.

### 4. Commands: load → decide → append (`WalletService`)

Every command handler:

1. **loads** the aggregate by replaying its stream (never reading a balance),
2. **decides** — enforces the no-overdraft invariant against the replayed state; if it fails it
   throws and appends **nothing**,
3. **appends** the resulting event(s) at the next sequence number, in one `@Transactional` unit.

A **transfer** produces **two** events — a `MoneyTransferredOut` debit on the source and a
`MoneyTransferredIn` credit on the destination, correlated by a `transferId` — appended
**atomically**. If appending the second leg fails, the first rolls back; the ledger can never be
left half-moved. This mirrors double-entry bookkeeping.

### 5. The payoffs you get for free

- **Full audit trail:** `GET /wallets/{id}/events` (one wallet) and `GET /events` (everything).
- **Temporal queries / time travel:** `GET /wallets/{id}/balance?asOf=N` replays only the first
  `N` events — "what was the balance as of the Nth fact?".
- **The log is the truth:** a test re-folds the raw stored rows independently and gets the same
  number; there is no hidden state anywhere.

---

## Money is integer minor units (`long` cents), never `double`

Currency is stored and computed as a `long` count of **cents**. `double`/`float` are **binary**
floating point: `0.1 + 0.2 != 0.3`, and the rounding error **accumulates over a long event log** —
fatal for a ledger that must reconcile to the cent. A `long` of cents is **exact**, is one 8-byte
CPU word (cheap to add/compare), and overflows are detectable. For multi-currency or sub-cent
units you would reach for `BigDecimal` (exact decimal, but heavier — boxed, allocates); here a
`long` of cents is the simplest exact representation. **The one rule: never `double` for money.**

---

## Snapshot mechanism (note)

Replaying from event #1 every read is fine for thousands of events but eventually slow for a
hot, long-lived aggregate. The standard optimization is a **snapshot**: periodically persist the
folded state at sequence *N* (e.g. `{balanceCents, sequenceNumber}`), then on load start from the
latest snapshot and replay only events *> N*. Snapshots are a pure **cache / optimization** — they
are derived from and must always be reproducible by the log, never a second source of truth, and
can be deleted and rebuilt at will. This example deliberately omits snapshots to keep the fold
visible; adding one is a `snapshots` table plus a "load latest snapshot, then tail" branch in
`WalletService.load`.

---

## Trade-offs and when NOT to use it

**Costs you take on:**

- **Schema evolution of events.** Events are immutable and live *forever*; a v1 event must still
  deserialize years later. You version event types and **upcast** old payloads to new shapes on
  read — you cannot just "change the column". (Here, the `event_type` discriminator is the seam:
  `EventSerializer` routes by it, so renaming a Java class doesn't orphan old rows.)
- **Read performance / replay cost.** Deriving state by replay is more work than `SELECT balance`;
  you mitigate with **snapshots** and **read models**.
- **Eventual read models / CQRS pairing.** Real systems project the log into query-optimized read
  models (e.g. a `balances` table, search indexes) updated asynchronously by subscribers tailing the
  log. That introduces **eventual consistency** between write and read sides — event sourcing pairs
  naturally with **CQRS**. `GET /events` is exactly the ordered stream such a projector subscribes to.
- **Conceptual overhead.** More moving parts than a CRUD table; the team must think in events.

**When NOT to use it:**

- Simple CRUD where you never need history, audit, or temporal queries — a `balance` column is
  simpler and faster; don't pay the tax.
- When you need strong, immediate read-after-write consistency on complex queries and can't tolerate
  eventual read models.
- When the team isn't ready for event versioning/upcasting discipline — a botched event schema is
  expensive because the log is forever.

**When it shines:** money/ledgers, audit-critical domains (finance, healthcare, compliance),
anything where "how did we get to this state?" and "what was it at time T?" are first-class
questions — which is exactly why payment ledgers (Stripe-style) and idempotent financial systems
lean on it.

---

## Prerequisites

- **JDK 21+** (the project targets Java 21 bytecode; it also compiles/runs on newer JDKs via
  `--release 21`).
- **Maven 3.9+** (or use the `mvnw` wrapper if present).
- No database, broker, or other infrastructure — H2 runs in-memory inside the JVM.

---

## Run it

```bash
# from this directory
mvn test                 # run the JUnit 5 suite (no app startup needed beyond the test context)
mvn spring-boot:run      # start the HTTP API on http://localhost:8080
```

### Exercise the API (with the app running)

```bash
# deposit 100.00 (10000 cents) into wallet "alice"
curl -s -X POST localhost:8080/wallets/alice/deposit \
     -H 'Content-Type: application/json' -d '{"amountCents":10000}'
# -> {"walletId":"alice","balanceCents":10000}

# withdraw 25.00
curl -s -X POST localhost:8080/wallets/alice/withdraw \
     -H 'Content-Type: application/json' -d '{"amountCents":2500}'
# -> {"walletId":"alice","balanceCents":7500}

# overdraft is rejected (422) and appends NO event
curl -s -X POST localhost:8080/wallets/alice/withdraw \
     -H 'Content-Type: application/json' -d '{"amountCents":999999}'
# -> 422 {"title":"Insufficient funds", ...}

# transfer 30.00 alice -> bob (two events, atomic)
curl -s -X POST localhost:8080/wallets/alice/transfer \
     -H 'Content-Type: application/json' -d '{"toWalletId":"bob","amountCents":3000}'
# -> {"transferId":"...","fromWalletId":"alice","fromBalanceCents":4500,"toWalletId":"bob","toBalanceCents":3000}

# current balance (replayed)
curl -s localhost:8080/wallets/alice/balance
# -> {"walletId":"alice","balanceCents":4500}

# TIME TRAVEL: balance after only the first 2 events
curl -s 'localhost:8080/wallets/alice/balance?asOf=2'
# -> {"walletId":"alice","balanceCents":7500}

# the audit trail for one wallet
curl -s localhost:8080/wallets/alice/events

# the entire log across all wallets
curl -s localhost:8080/events
```

You can also browse the raw append-only table at **http://localhost:8080/h2-console**
(JDBC URL `jdbc:h2:mem:walletdb`, user `sa`, empty password) to confirm there is only an
`EVENT_STORE` table — and no balance column anywhere.

### Expected output

`mvn test` runs two test classes (7 + 4 tests). Expected:

```
[INFO] Tests run: 11, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

The suite asserts: deposit+withdraw then replayed balance; overdraft rejected with **no event
appended**; gap-free ordered sequence numbers; balance reconstructed at any point in time; a
transfer producing two atomic events across both wallets; transfer overdraft writing neither leg;
and that an independent re-fold of the raw log matches the service's balance.

---

## Files to read first

1. **`event/WalletEvent.java`** — the sealed event hierarchy: why events (past tense) are the
   source of truth, why sealed + records, and the integer-money rationale.
2. **`domain/Wallet.java`** — the **fold**: `replay` + the pure, exhaustive `apply`. This is the
   heart of event sourcing — state as a function of history.
3. **`service/WalletService.java`** — the command handlers: **load → decide (enforce no-overdraft)
   → append**, the atomic two-event transfer, and the temporal `balanceCentsAsOf` query.
4. **`store/EventStoreEntry.java`** — the append-only table: per-aggregate sequence numbers and the
   uniqueness constraint that provides ordering + optimistic concurrency.
5. **`event/EventSerializer.java`** — payload JSON (de)serialization and the `event_type`
   discriminator as the schema-evolution seam.
6. **`WalletServiceTest.java`** — the proofs: replay, overdraft-appends-nothing, ordering,
   time travel, atomic transfer, log-is-truth.

## Project layout

```
src/main/java/com/javamastery/examples/wallet/
├── WalletApplication.java                  # @SpringBootApplication (no scheduling, no broker)
├── event/
│   ├── WalletEvent.java                    # sealed interface — the closed set of facts
│   ├── MoneyDeposited.java                 # record event
│   ├── MoneyWithdrawn.java                 # record event
│   ├── MoneyTransferredOut.java            # record event (debit leg of a transfer)
│   ├── MoneyTransferredIn.java             # record event (credit leg of a transfer)
│   └── EventSerializer.java                # event <-> payload JSON (Jackson), type discriminator
├── domain/
│   ├── Wallet.java                         # the aggregate: replay (fold) + pure apply
│   └── InsufficientFundsException.java     # raised on the command side, before any append
├── store/
│   ├── EventStoreEntry.java                # the single append-only event_store row
│   └── EventStoreRepository.java           # append + ordered reads (no update/delete/balance)
├── service/
│   └── WalletService.java                  # command handlers (load->decide->append) + queries
├── web/
│   ├── WalletController.java               # POST deposit/withdraw/transfer, GET balance/events
│   ├── EventLogController.java             # GET /events — the whole log (audit trail / CQRS feed)
│   ├── Dtos.java                           # request/response records
│   └── ApiExceptionHandler.java           # overdraft -> 422, validation -> 400 (ProblemDetail)
└── config/
    └── AppConfig.java                      # injectable Clock for deterministic timestamps

src/test/java/com/javamastery/examples/wallet/
├── WalletServiceTest.java                  # replay / invariant / ordering / time-travel / transfer
└── WalletControllerTest.java              # HTTP end-to-end incl. overdraft -> 422
```
