# Saga Orchestrator (orchestration-based, with compensation)

A runnable, self-contained example of an **orchestration-based Saga** for a
multi-step business transaction with **compensation**. Place an order through a
saga with three steps:

1. **reserve inventory** (undo: release it)
2. **charge payment** (undo: refund it)
3. **confirm shipping** (undo: cancel it)

If a later step **fails**, the orchestrator runs the compensations of the
already-completed steps **in reverse order** (e.g. payment fails → release
inventory; shipping fails → refund payment, then release inventory). Saga state
and a per-step log are persisted to H2 so the transaction is **auditable** and
(conceptually) **recoverable**.

> **Backs:** L5/C02/T06 Distributed Transactions (Saga) + L6/C14/T04 (idempotency)

Runs on an in-memory **H2** database with **no external infrastructure**.

---

## Why a saga? (and why you can't just 2PC)

A single ACID transaction gives you atomicity for free: either everything
commits or everything rolls back. The moment your business operation spans
**multiple services**, each with its **own database**, that free lunch is gone:

- **You can't two-phase-commit (2PC/XA) across services in practice.** 2PC needs
  a coordinator that holds **locks** on every participant for the whole
  transaction, and every participant must support the XA protocol. That couples
  availability (one slow/dead participant blocks everyone — the "blocking"
  problem of 2PC), kills throughput under contention, and most cloud datastores
  and HTTP/REST services simply don't expose XA. It does not scale and it does
  not compose across heterogeneous services.
- **A saga trades atomicity for a sequence of local transactions plus
  compensation.** Each step commits **on its own** (`T1, T2, … Tn`). There is no
  global lock. If step `k` fails, the saga restores a *semantically* consistent
  state by running compensating transactions `Ck-1, … C1` for the steps that
  already committed. You give up isolation (intermediate states are visible) in
  exchange for availability and scale; you handle the lost isolation with
  techniques like semantic locks, reservations, and commutative updates.

This example makes the "each step commits on its own" property concrete: the
orchestrator deliberately does **not** wrap the forward pass in one
`@Transactional` method. Each service operation (and each log write) commits in
its **own** transaction (`Propagation.REQUIRES_NEW`). If we used one big
transaction, a failure would roll back the inventory reservation and there would
be **nothing to compensate** — which is exactly *not* a saga.

---

## Orchestration vs. choreography

Two ways to coordinate a saga:

| | **Orchestration** (this example) | **Choreography** |
|---|---|---|
| Coordination | A central **orchestrator** tells each participant what to do next and triggers compensations. | No central brain. Each service **reacts to events** and emits its own; the flow emerges from the choreography. |
| Control flow | Explicit, in one place. Easy to see the whole saga and reason about ordering/compensation. | Implicit, spread across services. The "saga" exists only as an emergent property of event subscriptions. |
| Coupling | Orchestrator knows all participants (more central coupling). | Services only know events (looser coupling), but the end-to-end flow is harder to follow. |
| Debuggability | One log to read; the saga state machine is centralized and auditable. | Must reconstruct flow from many services' logs/event traces. |
| Risk | Orchestrator can become a "god service" if it accretes business logic. | Cyclic event dependencies and hard-to-trace flows as steps grow. |

**When to use which:**

- **Orchestration** when the flow is **complex**, has many steps/branches,
  needs **clear ownership** of the transaction, strong **auditability**, or
  explicit recovery logic. Easier for newcomers to understand.
- **Choreography** when you want **maximum decoupling**, the flow is **simple**
  / linear, and teams own services independently and prefer event-driven
  integration. Fewer central bottlenecks, but watch out for tangled event webs.

A common middle ground: orchestrate the **critical** transactional flow,
choreograph peripheral reactions (notifications, analytics).

---

## Idempotent + commutative compensations (and why it matters)

In any real saga the orchestrator talks to participants over an unreliable
network and a crash-prone coordinator. That forces two properties:

- **At-least-once delivery.** A "charge" or a "refund" command may be delivered
  more than once (retry after a timeout, retry after the coordinator crashed and
  resumed from its log). So **every step and every compensation must be
  idempotent**: applying it twice has the same effect as applying it once.
  In this code, every operation is keyed by the caller's `orderRef` and the
  service tables have a **unique constraint** on it; a repeated `charge` finds
  the existing payment and is a no-op rather than billing the customer twice — the
  classic **idempotency-key** pattern.
- **Commutative compensations.** Compensations may run in an order you didn't
  expect relative to other concurrent activity, so they should **net out**
  regardless of interleaving. A refund of $X composes to the same financial
  result whether it lands before or after some other adjustment. We model this by
  making `release`/`refund`/`cancel` operate on absolute persisted state
  (mark RELEASED/REFUNDED/CANCELLED) rather than blind decrements, and by
  clamping the inventory release so it can't over-credit.

Compensation is **semantic**, not physical: you can't "un-charge" a card by
deleting a row — you issue a **refund**. The compensating transaction is itself a
real business operation with its own side effects.

---

## Recovery & at-least-once notes

The persisted **saga log** (`saga_instance` + `saga_step_log`) is what makes the
saga recoverable:

- Each forward step records `EXECUTED` *after* it commits; a failed step records
  `FAILED`; compensations record `COMPENSATED`. The overall saga moves through
  `STARTED → COMPENSATING → COMPENSATED` (or `COMPLETED`, or
  `COMPENSATION_FAILED` if an undo itself fails).
- On restart, a **recovery sweep** would scan for sagas stuck in `STARTED` or
  `COMPENSATING` (see `SagaInstanceRepository.findByStatusIn`) and **resume**
  them: re-run the next pending step, or continue compensating. Because every
  step is idempotent, re-running a step that *did* commit before the crash is
  safe.
- A `@Version` optimistic-lock column on the saga prevents two coordinators (or
  a live request racing the recovery sweep) from both advancing the same saga.
- If a compensation **cannot** succeed (e.g. the refund gateway is permanently
  rejecting), the saga is parked in `COMPENSATION_FAILED` for human/automated
  intervention rather than silently lost.

This example keeps the orchestrator **synchronous and in-process** for clarity.
A production orchestrator (e.g. a workflow engine, or a custom coordinator over a
message broker) would persist a command/event for each transition and drive the
saga asynchronously with retries — but the state machine and the idempotency
contract are exactly the ones modeled here.

---

## Prerequisites

- **JDK 21+** (the build targets Java 21 bytecode; any JDK ≥ 21 compiles it).
- **Maven 3.9+** (or use the `mvnw` wrapper of your repo if present).
- No database to install — H2 runs in-memory.

## Run

```bash
# from this directory
mvn test          # run the test suite (happy path + compensation paths)
mvn spring-boot:run   # start the app on http://localhost:8080
```

Then place orders:

```bash
# Happy path -> 201 Created, status COMPLETED
curl -i -X POST http://localhost:8080/api/orders \
  -H 'Content-Type: application/json' \
  -d '{"orderRef":"order-1","sku":"SKU-WIDGET","quantity":2,"amount":59.99,"address":"1 Main St"}'

# Force payment failure via a declined amount -> 422, status COMPENSATED
# (set the decline amount first by editing PaymentService, or use the test
#  switches; see "Injecting failures" below)
```

Browse the persisted saga log at the H2 console:
`http://localhost:8080/h2-console`
(JDBC URL `jdbc:h2:mem:sagaorchestrator`, user `sa`, empty password) — look at
`SAGA_INSTANCE`, `SAGA_STEP_LOG`, `RESERVATION`, `PAYMENT`, `SHIPMENT`.

### Injecting failures (for the demo/tests)

`PaymentService` and `ShippingService` expose switches so failures are
deterministic:

- `paymentService.failNextCharge()` — next charge throws (transient gateway).
- `paymentService.setDeclineAmount(new BigDecimal("120.00"))` — any charge for
  exactly that amount is declined.
- `shippingService.failNextConfirm()` — next shipping confirm throws (the **last**
  step fails, so payment *and* inventory get compensated in reverse).

The seeded inventory is `SKU-WIDGET=100`, `SKU-GADGET=5`; ordering more
`SKU-GADGET` than 5 fails the **first** step (insufficient stock).

## Expected output

`mvn test` ends with:

```
[INFO] Tests run: 8, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

In the logs you can watch a compensating run, e.g. shipping failure:

```
saga 5 -> executing step 0 'reserve-inventory'
saga 5 -> executing step 1 'charge-payment'
saga 5 -> executing step 2 'confirm-shipping'
saga 5 -> step 2 'confirm-shipping' FAILED: simulated carrier outage ...
saga 5 -> compensating step 1 'charge-payment'   <-- reverse order
saga 5 -> compensating step 0 'reserve-inventory'
saga 5 -> COMPENSATED (all undos applied in reverse order)
```

The happy-path HTTP call returns `201` with `"status":"COMPLETED"`; a
compensated order returns `422` with `"status":"COMPENSATED"` and a step log
showing the compensated steps.

---

## Files to read first

1. **`saga/SagaStep.java`** — the do/undo abstraction and the idempotency/
   commutativity contract every step must honor.
2. **`saga/SagaOrchestrator.java`** — the generic coordinator: run steps in
   order, on failure compensate completed steps **in reverse**. The heart of the
   example.
3. **`saga/SagaLog.java`** — why each transition commits in its **own**
   transaction (`REQUIRES_NEW`) and why that's split into its own bean.
4. **`service/OrderSagaService.java`** — wires the three services into an ordered
   `SagaDefinition` (the place-order saga) and runs the orchestrator.
5. **`service/PaymentService.java`** — idempotent charge/refund with injectable
   failures; the cleanest illustration of the idempotency-key pattern.
6. **`entity/SagaInstance.java` + `entity/SagaStepLog.java`** — the durable saga
   log that makes the transaction auditable and recoverable.
7. **`controller/OrderController.java`** — `POST /api/orders` mapping saga
   outcome to HTTP status (201 vs 422).
8. **Tests** — `SagaOrchestratorReverseOrderTest` (reverse-order proof),
   `OrderSagaIntegrationTest` (happy + compensation paths with DB assertions),
   `OrderControllerTest` (HTTP layer).
```
