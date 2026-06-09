---
title: "Worked Design: Payment System"
slug: worked-design-payment-system
level: L5
module: "Architecture & Engineering Leadership"
section: "Distributed Systems & System Design"
type: concept
difficulty: lead
order: 21
tags: [payment, ledger, double-entry, idempotency, saga, audit, fraud, stripe, paypal, pci-dss, eventual-consistency, reconciliation, settlement, dispute, chargeback]
prerequisites: [system-design-methodology-framework, distributed-transactions-2pc-saga, idempotency-and-deduplication, event-sourcing]
status: complete
estimated_minutes: 65
last_updated: 2026-06-08
---

# Worked Design: Payment System

Design a payment system that processes card charges, refunds, and settlements. **This is the strictest-correctness design in this series**: a single lost or duplicated charge is a customer-visible failure with regulatory consequences. The architecture is dominated by *correctness invariants* — every money movement is an immutable double-entry journal entry; every external API call is idempotent; every multi-step flow is a saga; every action is auditable. Stripe, Square, Adyen, PayPal all have variations of this architecture.

## Where Modern Payment Systems Came From — From Diners Club To Stripe

Payment systems descend from a *long* history of financial infrastructure. The credit card industry (1950s+), card networks (Visa 1976, Mastercard 1979), online payments (PayPal 1998), and developer-friendly APIs (Stripe 2010) each contributed specific patterns. Understanding the lineage explains why modern payment systems have specific constraints.

### The Pre-Credit-Card Era

Before credit cards, payments were *cash*, *check*, or *store credit*. Cross-merchant credit (one card accepted at multiple merchants) didn't exist.

**Diners Club** (1950) was the first multi-merchant credit card. Founded by Frank McNamara, the Diners Club Card was accepted at a handful of New York restaurants. The card was *literally* a paper card with a list of accepted merchants.

The American Express Card (1958) and BankAmericard (1958, later Visa) followed. By the 1970s, credit cards were established consumer products.

### The 1970s — Card Networks Form

**Visa** was formed in 1976 from Bank of America's BankAmericard program. **Mastercard** evolved from the Interbank Card Association (founded 1966), renamed in 1979.

The card networks introduced *standardization*:

- **Card number format**: 16 digits, specific structure.
- **Magnetic stripe**: standard data format.
- **Authorization protocols**: standardized merchant-bank communication.

These standards enabled the modern card industry. Without them, every merchant would need separate processes for every bank.

### The 1990s — Online Payments Begin

The web era brought online payments. Major milestones:

- **CyberCash** (1994): early online payment processor.
- **VeriSign Payment Services** (1998): SSL-secured payments.
- **PayPal** (1998, founded by Max Levchin, Peter Thiel, Luke Nosek, Ken Howery): peer-to-peer email-based payments.

**PayPal** was the breakthrough. The eBay-friendly payment service grew rapidly; PayPal acquired its eBay competitor X.com (Elon Musk's company) in 2000. eBay acquired PayPal in 2002 for $1.5B.

PayPal's specific innovation: *making online payments easy*. Pre-PayPal, accepting online payments required complex merchant accounts; PayPal eliminated this friction.

### The 2010 Stripe Era

The next major shift was **Stripe** (founded 2010 by Patrick and John Collison — already covered in [T07 idempotency](./T07-idempotency-and-deduplication.md)). Stripe's specific innovation: *developer-friendly payment APIs*.

Pre-Stripe, accepting credit cards required:

1. Apply for a merchant account (weeks).
2. Integrate with payment gateways (complex APIs).
3. Manage compliance separately.

Stripe collapsed this:

1. Sign up online (minutes).
2. Add 7 lines of JavaScript (literal advertisement).
3. Compliance handled.

The developer experience was *transformative*. Stripe processed billions of dollars within years. By 2024, Stripe is one of the most valuable private tech companies ever.

### Adyen, Square, And The Modern Ecosystem

Adyen (Netherlands, 2006) became Europe's payments leader, serving enterprise customers. Square (US, 2009) focused on small businesses, providing card readers for mobile devices.

By 2024, the payment ecosystem includes:

- **Card networks**: Visa, Mastercard, American Express, Discover.
- **Payment processors**: Stripe, Adyen, Braintree, Square.
- **Acquiring banks**: traditional banks providing merchant accounts.
- **Issuing banks**: banks issuing cards to consumers.

The system involves many actors. Each transaction touches several.

### Why Payment Systems Are Hard

Payment systems have *specific* characteristics that make them hard:

1. **Money is real**: errors have financial consequences.
2. **Regulation is heavy**: PCI DSS, AML, KYC requirements.
3. **Latency matters**: customers don't tolerate slow payments.
4. **Availability is critical**: an outage costs real money per minute.
5. **Fraud is constant**: adversaries continuously attempt fraud.

These characteristics make payment system design uniquely demanding.

### The Double-Entry Bookkeeping Foundation

The deepest pattern in payment systems is **double-entry bookkeeping** — invented by Luca Pacioli in 1494 (covered in [T08 of C01](../C01-software-architecture/T08-event-sourcing.md)). Every payment creates two entries: a debit and a credit. The books must always balance.

Modern payment systems use double-entry bookkeeping *digitally*. Every transaction creates immutable journal entries. The total debits must equal the total credits.

This 500-year-old practice remains the foundation of payment system correctness.

## Why Payment Matters As An Interview Question

The payment system question tests:

1. **Correctness invariants**: money can't be created or destroyed accidentally.
2. **Idempotency**: retries must not double-charge.
3. **Sagas**: multi-step flows with compensating actions.
4. **Audit and compliance**: regulatory requirements.

Senior candidates discuss all four. The interview reveals whether the candidate understands what makes financial systems different.

## Senior Engineer's Q&A For This Design

### Q1: Why double-entry bookkeeping in software?

**Answer**: The 500-year-old practice ensures *balance invariants* — total debits must equal total credits. In software:

1. **Detect errors**: balance mismatches indicate bugs.
2. **Audit trail**: every transaction is two immutable entries.
3. **Reversibility**: refunds are reversing entries, not modifications.
4. **Compliance**: regulators require this structure.

The alternative (single-entry) makes errors invisible. With financial data, invisibility is unacceptable.

### Q2: How do you ensure idempotency in payments?

**Answer**: Multiple layers:

1. **Client idempotency keys**: per Stripe pattern (covered in T07).
2. **Server-side dedup**: store key + result, return cached result on retry.
3. **Database constraints**: unique constraints prevent duplicates.
4. **External callout dedup**: payment processors typically have their own idempotency.

Specific challenges:
- **Network failures**: client doesn't know if request succeeded.
- **Server failures**: server doesn't know if downstream succeeded.

The senior answer: layered idempotency at every boundary.

### Q3: How do you handle multi-currency transactions?

**Answer**: Several patterns:

1. **Store amount + currency**: never convert; always track original.
2. **Convert at settlement**: use spot rates with timestamps.
3. **Provider FX**: payment provider handles conversion.

Specific challenges:
- **Rate fluctuation**: rates change between authorization and settlement.
- **Rounding**: currency rounding rules vary.
- **Display**: showing user their currency, charging in card currency.

### Q4: How do you implement saga compensation for failed payments?

**Answer**: Each saga step has explicit compensation:

1. **Charge attempt**: compensation = void or refund.
2. **Inventory reservation**: compensation = release inventory.
3. **Email notification**: compensation = send cancellation email.

Critical: compensations must be idempotent. Retries during failure recovery should be safe.

Specific challenges:
- **Some operations can't be compensated**: sending email, dispatching truck.
- **Compensation failures**: retries with exponential backoff.

### Q5: How do you handle dispute and chargeback workflows?

**Answer**: Specialized workflow:

1. **Dispute received**: from card network.
2. **Evidence gathering**: prove transaction was legitimate.
3. **Provider response**: submit evidence within deadline (typically 7 days).
4. **Resolution**: win or lose dispute.
5. **Settlement adjustment**: chargeback amount + fees.

Specific challenges:
- **Time pressure**: deadlines are strict.
- **Evidence quality**: photos, receipts, communications.
- **Friendly fraud**: legitimate customer disputes legitimate charges.

### Q6: How do you ensure compliance (PCI DSS)?

**Answer**: Architectural choices:

1. **Tokenization**: don't store card numbers; use tokens.
2. **Network segmentation**: PCI scope is limited.
3. **Encryption**: at rest and in transit.
4. **Logging**: comprehensive but compliant.
5. **Annual audits**: required by card networks.

The senior insight: PCI DSS is *architectural*, not bolt-on. Compliance influences fundamental decisions.

## Common Misconceptions Explained

### "Payment systems are just databases."

False. Payment systems involve external dependencies (banks, networks), compliance requirements, fraud detection, dispute handling. Far more than CRUD.

### "Modern payment systems don't need double-entry."

False. The pattern survives because it works. New patterns haven't surpassed it.

### "Atomic transactions handle all consistency."

False. Cross-system transactions (payment + ledger + notification) can't be atomic. Sagas required.

### "Stripe handles all the complexity."

Partially false. Stripe handles card processing well; the business logic (charging customers, managing subscriptions, handling disputes) is still complex.

### "Strong consistency is too expensive for payments."

False. Eventually consistent payments allow double-charging. Strong consistency is required despite cost.

### "Cryptocurrency simplifies payments."

False. Crypto adds complexity (wallets, exchanges, volatility) while solving few problems. Most "crypto payments" are actually crypto-to-fiat conversions.

## Requirements

### Functional

- **Authorize and capture**: hold funds, then capture (or void).
- **Refund**: full or partial.
- **Recurring payments**: subscription billing.
- **Multi-currency**.
- **Dispute / chargeback** workflow.

### Out Of Scope

- The card networks themselves (Visa/Mastercard infrastructure).
- Bank reconciliation feeds (described, not built).
- Merchant onboarding / KYC.

### Non-Functional

- **Scale**: 10K payments/s peak.
- **Latency**: end-to-end charge < 2s p99.
- **Availability**: 99.99% for the API.
- **Consistency**: STRONG for every money movement. Eventual is unacceptable.
- **Audit**: every action immutable, attributable, traceable.
- **Idempotency**: every operation must be safely retryable.

## Capacity

```
10K txns/s × 86400 = ~860M txns/day
Each ledger entry: ~500 bytes (immutable)
500B × 860M × 365 × 7 yr retention = 1.1 PB
→ partitioned by date; archive older to cold storage
```

## API

```http
POST /api/v1/charges
  headers: Idempotency-Key: ...
  body: { "amount": 1000, "currency": "USD", "source": "tok_xyz", "customer": "cus_abc", "capture": true }
  returns: { "chargeId": "ch_...", "status": "succeeded" | "requires_action" | "failed" }

POST /api/v1/refunds
  headers: Idempotency-Key: ...
  body: { "chargeId": "ch_...", "amount": 1000 }

POST /api/v1/charges/{id}/capture
POST /api/v1/charges/{id}/void
```

Stripe-style. Every write requires Idempotency-Key.

## Data Model

### The Ledger (Double-Entry)

Every money movement produces **two** balanced journal entries (debit + credit).

```sql
CREATE TABLE ledger_entries (
  id            UUID PRIMARY KEY,
  txn_id        UUID NOT NULL,        -- groups debit + credit
  account_id    BIGINT NOT NULL,
  amount        BIGINT NOT NULL,       -- positive = credit, negative = debit
  currency      CHAR(3) NOT NULL,
  posted_at     TIMESTAMPTZ NOT NULL,
  -- immutable: no UPDATE, no DELETE
  -- corrections via reversing entries
  CONSTRAINT chk_currency CHECK (currency IN ('USD', 'EUR', 'GBP', ...))
);

-- Per-transaction, debits + credits must sum to zero:
CREATE TABLE transactions (
  id            UUID PRIMARY KEY,
  type          TEXT,            -- 'charge', 'refund', 'transfer'
  status        TEXT,
  created_at    TIMESTAMPTZ
);

-- Idempotency cache
CREATE TABLE idempotency_keys (
  key           TEXT PRIMARY KEY,
  txn_id        UUID,
  response      JSONB,
  expires_at    TIMESTAMPTZ
);
```

The double-entry invariant: for every `txn_id`, sum of amounts = 0. A charge of $10 from customer A to merchant B:

```
txn_id=t1, account=customer_A, amount=-1000  (debit)
txn_id=t1, account=merchant_B, amount=+1000  (credit)
```

Every refund reverses (with a new txn_id, but pointing to the original via metadata).

## High-Level Architecture

```mermaid
flowchart TB
  Client --> LB
  LB --> API[Payments API]
  API --> Idem[(Idempotency store)]
  API --> Saga[Saga orchestrator]
  Saga --> Authz[Card-network authorization]
  Saga --> Fraud[Fraud service]
  Saga --> Ledger[(Ledger DB)]
  Saga --> Audit[Audit log]
  
  Authz --> Network[Visa / Mastercard]
  Ledger -.->|"events"| Kafka[(Kafka)]
  Kafka --> Reconciler[Reconciliation]
  Kafka --> Reports[Reporting]
```

## Deep Dive A: The Charge Flow As A Saga

```mermaid
sequenceDiagram
  participant C as Client
  participant API as Payments API
  participant S as Saga
  participant F as Fraud
  participant N as Card Network
  participant L as Ledger
  participant A as Audit

  C->>API: POST /charges (Idempotency-Key)
  API->>API: check idempotency store
  alt seen
    API-->>C: cached response
  else not seen
    API->>S: start saga
    S->>F: check fraud
    F-->>S: ok / decline
    alt fraud declined
      S->>L: write declined-charge entry (no money movement)
      S->>A: audit
      S-->>API: declined
    else fraud ok
      S->>N: authorize ($amount)
      alt network declines
        S-->>API: declined
      else network approves
        S->>L: write debit (customer) + credit (pending settlement)
        S->>A: audit
        S-->>API: succeeded
      end
    end
    API->>API: store idempotency key + response
    API-->>C: response
  end
```

### Compensation On Failure

If the saga fails at step N, compensate steps 1..N−1. For payment auth + ledger update:
- Auth succeeded but ledger write failed: void the auth at the network.
- Fraud declined after the auth would have succeeded: do nothing (no auth happened).

The saga state is durable (event-sourced or DB-row); on coordinator crash, a recoverer resumes.

## Deep Dive B: Idempotency

Per the requirements, every POST has `Idempotency-Key`. The store:

```java
@Component
class IdempotencyStore {
  private final JdbcTemplate jdbc;

  public <T> T exec(String key, Supplier<T> op) {
    Optional<String> existing = jdbc.queryForOptional(
      "SELECT response FROM idempotency_keys WHERE key = ? AND expires_at > NOW()",
      String.class, key);
    if (existing.isPresent()) {
      return deserialize(existing.get());
    }
    T result = op.get();
    try {
      jdbc.update(
        "INSERT INTO idempotency_keys(key, response, expires_at) VALUES (?, ?, NOW() + INTERVAL '24 hours')",
        key, serialize(result));
    } catch (DuplicateKeyException e) {
      return deserialize(jdbc.queryForObject(
        "SELECT response FROM idempotency_keys WHERE key = ?",
        String.class, key));
    }
    return result;
  }
}
```

**24-hour TTL** by default. Stripe's pattern.

## Deep Dive C: Ledger Invariants

Every read of an account balance is a sum over the immutable ledger:

```sql
SELECT SUM(amount) FROM ledger_entries WHERE account_id = $id AND currency = 'USD';
```

For active balances, this is too slow at scale. **Materialized view**: per-account aggregate snapshot, updated on each ledger write, *plus* the ledger of changes since the snapshot. Snapshots are computed eventually (every minute, or on each entry); balance = snapshot + recent entries.

Periodic full re-aggregation verifies the snapshot.

## Deep Dive D: Reconciliation

Daily, the system compares:
- Internal ledger sums.
- Card network's settlement file.
- Bank statements.

Any discrepancy is investigated. Reconciliation is a *separate* asynchronous process that does not block payments but flags anomalies.

## Trade-Offs

| Decision | Chosen | Alternative | Reason |
|----------|--------|-------------|--------|
| Ledger | Append-only, double-entry | Mutable balance row | Audit, recovery |
| Idempotency | Required on all writes | Optional | Cost of duplicate charge >> cost of idempotency-key |
| Saga style | Orchestrated (Temporal) | Choreographed events | Explicit failure handling |
| Consistency | Strong inside a region; cross-region eventual | Multi-region strong | Latency budget |
| Schema | RDBMS (Postgres) for the ledger | NoSQL | ACID transactions; ledger correctness |

## Failure Modes

- **Network timeout during authorize**: the auth may have happened. Idempotency key on retry; recheck at the network.
- **Ledger write failure after network auth**: void the auth (compensation).
- **Database failover**: writes briefly fail; retries succeed (the API returns 5xx, client retries with same idempotency key).
- **Settlement-file mismatch**: reconciliation flags; operators investigate.
- **Currency conversion timing**: rate may shift between auth and capture; lock the rate at auth.

## Security And Compliance

- **PCI DSS**: tokenize cards; do not store raw PAN. The system handles `tok_xyz`, never `4242 4242 4242 4242`.
- **Encryption at rest**: ledger, audit, idempotency stores.
- **Audit log**: every action by every actor (user, admin, system) is immutable.
- **Segregation of duties**: refund authority differs from charge authority.

> [!INTERVIEW]
> Strong candidates name **double-entry ledger** unprompted, identify **idempotency as foundational**, and describe the **saga for cross-service operations**. Weak candidates use a single mutable `balance` column.

## Deeper Dive — Production-Grade Payment System Implementation

### Double-Entry Ledger Schema (PostgreSQL)

```sql
-- The journal: append-only entries
CREATE TABLE ledger_entries (
    entry_id BIGSERIAL PRIMARY KEY,
    txn_id UUID NOT NULL,                    -- groups entries for one logical txn
    account_id UUID NOT NULL,
    amount_minor BIGINT NOT NULL,            -- positive=debit, negative=credit
    currency CHAR(3) NOT NULL,
    direction CHAR(2) NOT NULL,              -- 'DR' or 'CR'
    posted_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    metadata JSONB
);

CREATE INDEX idx_ledger_account ON ledger_entries(account_id, posted_at);
CREATE INDEX idx_ledger_txn ON ledger_entries(txn_id);

-- Materialized balance — cached for fast reads
CREATE TABLE account_balances (
    account_id UUID PRIMARY KEY,
    currency CHAR(3) NOT NULL,
    balance_minor BIGINT NOT NULL DEFAULT 0,
    last_entry_id BIGINT NOT NULL,           -- last ledger entry applied
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version BIGINT NOT NULL DEFAULT 0        -- optimistic lock
);

-- Idempotency table
CREATE TABLE idempotency_keys (
    key UUID PRIMARY KEY,
    request_hash CHAR(64) NOT NULL,          -- SHA-256 of request body
    response JSONB,                          -- cached response
    status TEXT NOT NULL,                    -- 'PROCESSING' | 'COMPLETED' | 'FAILED'
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    expires_at TIMESTAMPTZ NOT NULL          -- 72 hours from creation
);

CREATE INDEX idx_idempotency_expires ON idempotency_keys(expires_at);
```

### Atomic Double-Entry Insert (Sum-to-Zero Invariant)

```sql
CREATE OR REPLACE FUNCTION post_transaction(
    p_txn_id UUID,
    p_entries JSONB  -- [{account_id, amount_minor, currency, direction}, ...]
) RETURNS VOID AS $$
DECLARE
    v_sum BIGINT;
    v_entry JSONB;
BEGIN
    -- Compute sum across all entries (positive for DR, negative for CR)
    SELECT COALESCE(SUM(
        CASE WHEN (e->>'direction') = 'DR'
             THEN (e->>'amount_minor')::BIGINT
             ELSE -(e->>'amount_minor')::BIGINT END
    ), 0)
    INTO v_sum
    FROM jsonb_array_elements(p_entries) e;

    -- INVARIANT: debits must equal credits
    IF v_sum != 0 THEN
        RAISE EXCEPTION 'Ledger imbalance: sum=%, must be zero', v_sum;
    END IF;

    -- Insert all entries
    FOR v_entry IN SELECT * FROM jsonb_array_elements(p_entries) LOOP
        INSERT INTO ledger_entries (
            txn_id, account_id, amount_minor, currency, direction
        ) VALUES (
            p_txn_id,
            (v_entry->>'account_id')::UUID,
            (v_entry->>'amount_minor')::BIGINT,
            v_entry->>'currency',
            v_entry->>'direction'
        );
    END LOOP;

    -- Update materialized balances atomically (same transaction)
    PERFORM update_balances_for_txn(p_txn_id);
END;
$$ LANGUAGE plpgsql;
```

### Spring Boot Charge Service with Idempotency

```java
@Service
public class ChargeService {
    private final IdempotencyRepo idempotencyRepo;
    private final FraudService fraudService;
    private final PSPAdapter pspAdapter;        // payment gateway abstraction
    private final LedgerService ledger;

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ChargeResult charge(UUID idempotencyKey, ChargeRequest request) {
        // 1. Check idempotency — return cached result if exists
        IdempotencyRecord existing = idempotencyRepo.findById(idempotencyKey).orElse(null);
        if (existing != null) {
            if (existing.requestHash().equals(hash(request))) {
                if (existing.status() == COMPLETED) return existing.response();
                if (existing.status() == PROCESSING) throw new ConflictException("In progress");
            } else {
                throw new BadRequestException("Idempotency key reused with different body");
            }
        }

        // 2. Reserve the key (atomic insert)
        idempotencyRepo.insertProcessing(idempotencyKey, hash(request));

        try {
            // 3. Run charge saga
            ChargeResult result = runChargeSaga(request);

            // 4. Persist result
            idempotencyRepo.markCompleted(idempotencyKey, result);
            return result;
        } catch (Exception e) {
            idempotencyRepo.markFailed(idempotencyKey, e.getMessage());
            throw e;
        }
    }

    private ChargeResult runChargeSaga(ChargeRequest request) {
        // Step 1: Fraud check (compensable — record decision; no rollback needed)
        FraudDecision decision = fraudService.evaluate(request);
        if (decision.isBlocked()) {
            return ChargeResult.declined("FRAUD_BLOCKED");
        }

        // Step 2: PSP authorization (compensable via void)
        AuthResult auth = pspAdapter.authorize(request);
        if (!auth.isAuthorized()) {
            return ChargeResult.declined(auth.declineCode());
        }

        try {
            // Step 3: Ledger entry — atomic with Step 4 via DB transaction
            UUID txnId = UUID.randomUUID();
            ledger.post(txnId, List.of(
                new Entry(request.merchantAccountId(), request.amount(), DR),
                new Entry(STRIPE_ACCOUNT_ID, request.amount(), CR)
            ));

            // Step 4: Capture (if needed) — converts auth to settled
            CaptureResult capture = pspAdapter.capture(auth.authId());

            return ChargeResult.succeeded(txnId, capture.captureId());

        } catch (Exception e) {
            // Compensate: void the auth
            pspAdapter.voidAuth(auth.authId());
            throw new ChargeFailedException("Ledger/capture failed; auth voided", e);
        }
    }
}
```

### Materialized Balance Refresh (Async, Eventually Consistent)

```java
@Component
public class BalanceProjector {

    // Listens to ledger entries via Kafka (Postgres CDC via Debezium)
    @KafkaListener(topics = "ledger.entries", concurrency = "4")
    @Transactional
    public void onLedgerEntry(LedgerEntry entry) {
        balanceRepo.applyDelta(
            entry.accountId(),
            entry.currency(),
            entry.direction() == DR ? entry.amountMinor() : -entry.amountMinor(),
            entry.entryId()
        );
    }

    // Periodic full recompute as guard against projection drift
    @Scheduled(cron = "0 0 4 * * *")  // 4 AM daily
    public void recomputeAllBalances() {
        balanceRepo.findAccountsWithRecentActivity(Duration.ofHours(24))
            .forEach(accountId -> {
                long ledgerSum = ledgerRepo.sumByAccount(accountId);
                long projected = balanceRepo.findById(accountId).balance();
                if (ledgerSum != projected) {
                    alertOps("Balance drift for account=" + accountId
                            + " ledger=" + ledgerSum + " projected=" + projected);
                    balanceRepo.forceSetBalance(accountId, ledgerSum);
                }
            });
    }
}
```

### Reconciliation with PSP Settlement File

```java
@Component
public class SettlementReconciler {

    @Scheduled(cron = "0 0 6 * * *")  // 6 AM daily
    public void reconcile() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        SettlementFile file = pspAdapter.downloadSettlement(yesterday);

        // Group internal txns by date
        Map<UUID, BigDecimal> internalByTxn = ledgerRepo.findByDate(yesterday)
            .stream()
            .filter(e -> e.accountId().equals(MERCHANT_ACCOUNT_ID))
            .collect(toMap(LedgerEntry::txnId, LedgerEntry::amount));

        // Group settlement records by txn
        Map<UUID, BigDecimal> settlementByTxn = file.records().stream()
            .collect(toMap(SettlementRecord::txnId, SettlementRecord::amount));

        // Find discrepancies
        Set<UUID> allTxnIds = new HashSet<>();
        allTxnIds.addAll(internalByTxn.keySet());
        allTxnIds.addAll(settlementByTxn.keySet());

        List<Discrepancy> issues = allTxnIds.stream()
            .map(txnId -> compare(txnId, internalByTxn.get(txnId), settlementByTxn.get(txnId)))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .toList();

        if (!issues.isEmpty()) {
            reconciliationAlertService.escalate(issues);
        }
    }

    private Optional<Discrepancy> compare(UUID txnId, BigDecimal internal, BigDecimal external) {
        if (internal == null) return Optional.of(Discrepancy.missingInternal(txnId, external));
        if (external == null) return Optional.of(Discrepancy.missingExternal(txnId, internal));
        if (internal.compareTo(external) != 0) {
            return Optional.of(Discrepancy.amountMismatch(txnId, internal, external));
        }
        return Optional.empty();
    }
}
```

## Deeper Dive — Stripe-Style Idempotency-Key Header Semantics

Standard Stripe behavior (which other modern PSPs adopted):

```
RULE 1: Same key + same body → return cached result
RULE 2: Same key + different body → 400 (with stable message)
RULE 3: Key without prior use → execute, cache result for 24-72h
RULE 4: Key TTL = 24h minimum, 72h preferred (Stripe uses 24h)

REQUEST HASH
  Compute SHA-256 of canonicalized request body
  Store with idempotency key
  On retry: compare hash, not body (saves storage)

CACHED RESPONSE
  Store FULL response body + status code
  On retry: return exactly what was returned originally
  Even if business state has since changed
  (Critical: must not re-execute mutation on retry)

CONCURRENT RETRIES
  Two requests with same key arrive simultaneously
  First wins (INSERT ... ON CONFLICT DO NOTHING)
  Second gets back "PROCESSING" or "COMPLETED" status
  → wait for completion via short-poll or webhook

ERROR HANDLING
  Mutation succeeded, response failed to send
    → retry with same key returns cached SUCCESS response
  Mutation failed at PSP
    → idempotency record marked FAILED; retry MAY re-attempt
    → policy decision: only retry on transient errors (network), not card declines
```

## Deeper Dive — Currency Handling

```java
// ALWAYS use minor units (integer cents/paise) — NEVER float for money
public record Money(long amountMinor, Currency currency) {
    public static Money ofDollars(double dollars, Currency usd) {
        // Convert to cents using HALF_UP rounding
        return new Money(
            BigDecimal.valueOf(dollars).movePointRight(usd.getDefaultFractionDigits())
                .setScale(0, RoundingMode.HALF_UP).longValueExact(),
            usd
        );
    }

    public BigDecimal asMajor() {
        return BigDecimal.valueOf(amountMinor)
            .movePointLeft(currency.getDefaultFractionDigits());
    }
}

// Multi-currency: store and operate in MINOR units of the txn currency
// Reconcile in major units only at display

public record CurrencyExchange(
    Currency from,
    Currency to,
    BigDecimal rate,
    Instant rateAt
) {
    public Money convert(Money source) {
        if (source.currency() != from) throw new IllegalArgumentException();
        BigDecimal converted = source.asMajor().multiply(rate);
        return Money.ofDollars(converted.doubleValue(), to);
    }
}
```

**Lock the rate at auth time** — store the rate used so reconciliation can verify. Don't reconvert at settle/refund time.

## Deeper Dive — Chargeback / Dispute Flow

```
1. CARDHOLDER FILES DISPUTE WITH BANK
   - Bank notifies merchant via webhook (typically 60-120 days after charge)
   - Status: "dispute_created"

2. MERCHANT REVIEWS
   - System pulls original charge details
   - Provides evidence (shipping, IP, signature, communication)
   - Submits via PSP API (Stripe: dispute.update endpoint)

3. BANK ARBITRATES
   - Time-bound: typically 7-21 days for evidence
   - Decides "won" or "lost"

4. LEDGER IMPACT
   - On dispute creation: HOLD amount + dispute fee (Stripe: $15)
   - On loss: chargeback entry posts (DR merchant, CR customer); fee finalized
   - On win: hold released

EVENT-DRIVEN HANDLING
  PSP webhook → Kafka topic → DisputeProcessor
  Idempotent: dispute_id is unique; INSERT ... ON CONFLICT DO NOTHING
  Audit log: every state transition tracked
  Customer service notified to help merchant

SCHEMA
  CREATE TABLE disputes (
      dispute_id UUID PRIMARY KEY,
      charge_txn_id UUID NOT NULL,
      reason_code TEXT NOT NULL,             -- "fraudulent", "product_not_received", etc.
      amount_minor BIGINT NOT NULL,
      status TEXT NOT NULL,                  -- "needs_response" | "under_review" | "won" | "lost"
      evidence JSONB,
      created_at TIMESTAMPTZ NOT NULL,
      due_by TIMESTAMPTZ NOT NULL            -- deadline for evidence submission
  );
```

## Deeper Dive — Capacity Math (Stripe-Scale)

```
INPUTS
  Transaction rate (avg)            : 5,000 tx/sec
  Transaction rate (peak, BFCM)     : 25,000 tx/sec
  Avg transaction amount            : $50
  Avg ledger entries per txn        : 4 (merchant DR, processor CR, fees, taxes)

WRITE QPS
  Ledger writes/sec (peak)          : 25k × 4 = 100k entries/sec
  PostgreSQL primary capacity       : ~50k writes/sec on tuned instance
  → MUST partition: 1 db per ~5k tx/sec region or by customer-shard

STORAGE GROWTH
  Daily ledger entries              : 5k × 4 × 86400 = 1.7B entries/day
  Bytes per entry (avg)             : ~200 (with indexes)
  Daily growth                      : ~340 GB
  Annual                            : ~125 TB
  → Sharded Postgres or move cold data to columnstore (ClickHouse) after 90 days

LATENCY BUDGETS (P99)
  API → fraud decision               : < 100ms (sync inline check)
  Fraud → PSP authorization          : < 1000ms (PSP-dependent)
  Authorization → ledger entry       : < 50ms (same-region Postgres)
  Total user-facing P99              : < 2 seconds

READS
  Balance reads                      : 50k/sec (dashboards, withdrawals, in-flight checks)
  → Heavy caching with consistent invalidation (CDC stream)

COMPLIANCE STORAGE
  Retain transaction records         : 7 years (varies by jurisdiction)
  Encrypted at rest, tokenized PII
  → Backup to immutable storage (S3 Object Lock / similar)
```

## Deeper Dive — Compliance Architecture

| Requirement | Architecture Implication |
|---|---|
| **PCI DSS** | Tokenize card numbers immediately; never store raw PAN; isolate cardholder data env (CDE) — fewer services in scope |
| **GDPR right to erasure** | Tokenize PII; soft-delete with audit; preserve financial records (legal exemption) |
| **SOX (US public companies)** | Segregation of duties (no single person can modify both payment AND audit code), quarterly access reviews |
| **PSD2 (EU)** | Strong Customer Authentication (3DS); transaction monitoring |
| **AML/KYC** | Customer verification, suspicious activity reporting (SARs), sanctions screening |
| **Open Banking** | OAuth + consent-based account access |
| **India RBI** | Local data storage requirement; cross-border data limits |

## Deeper Dive — Failure Modes Comprehensive Table

| Failure | Impact | Mitigation |
|---|---|---|
| PSP unavailable | Can't authorize new charges | Multi-PSP routing; fallback to secondary PSP; queue+retry for non-time-sensitive |
| Postgres primary down | Ledger writes blocked | Sync replica failover (Patroni / RDS Multi-AZ); RPO=0 critical |
| Idempotency key DB outage | Cannot dedup retries; risk double-charge | Local cache + DB fallback; degrade to "deny new charges" if both fail |
| Reconciliation finds drift | Money mismatch with PSP | Pause downstream; investigate; manual correction with paper trail |
| Fraud check timeout | Can't decide; block or allow? | Two policies: high-value=block, low-value=allow + post-hoc review |
| Settlement file corrupt | Can't reconcile | Retry download; manual fetch from PSP support; defer 1 day |
| Currency rate stale | Wrong amounts in cross-currency | Pin rate at auth; refresh rate hourly; alert if stale > 4h |
| Auth captured but DB write failed | "Phantom auth" — money held but no record | Periodic auth-vs-ledger reconciliation; void zombie auths |
| Webhook signature failure | Can't trust PSP event | Reject + alert; only accept signed webhooks |
| Refund issued, no original found | Manual refund attempt | Refund table requires charge_txn_id FK; reject if missing |

## Practice

1. **Implement double-entry.** Write the Postgres schema and the insert procedure that enforces sum-to-zero per txn.
2. **Idempotency key test.** Force-retry a charge with the same key; verify one charge, one ledger entry.
3. **Saga compensation drill.** Build a 3-step saga (fraud → auth → ledger). Force failure at step 3; verify compensation reverses steps 1–2.
4. **Reconciliation script.** Compare internal ledger sums to a simulated network settlement file; find discrepancies.
5. **Balance computation.** For 1B ledger entries, compute the active balance for an account in < 10 ms.
6. **Currency conversion.** Add multi-currency support; lock the rate at auth; verify reconciliation.
7. **Chargeback flow.** Sketch the dispute / chargeback workflow.
8. **Stripe SDK study.** Read Stripe's [API documentation](https://stripe.com/docs/api) for idempotency. Reverse-engineer their design.
9. **PCI scope reduction.** Identify which services are in PCI scope. Minimize.
10. **The skeptic conversation.** A junior engineer wants to use eventual consistency for the ledger. Write a 200-word response on why strong consistency is non-negotiable.

## Recap

You should now be able to:

- Design a **payment system** with a double-entry, append-only ledger as the truth.
- Make every write **idempotent** with the Stripe-style `Idempotency-Key` header.
- Orchestrate **multi-step flows** as sagas with explicit compensation.
- Maintain **active balances** as materialized views over the immutable ledger.
- Implement **reconciliation** as an async process comparing internal vs external truth.
- Apply **PCI DSS, encryption, audit logging, segregation of duties** as foundational security.
- Refuse eventual consistency for money movements; defend the design against latency complaints.

## Next

Continue to [Worked Design: Notification System](./T22-worked-design-notification-system.md) — multi-channel notifications (email, SMS, push) with retries, deduplication, and delivery guarantees.
