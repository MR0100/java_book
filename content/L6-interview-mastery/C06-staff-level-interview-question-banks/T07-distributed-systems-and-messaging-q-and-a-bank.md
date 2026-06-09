---
title: "Distributed Systems & Messaging — Q&A Bank (Staff Level)"
slug: distributed-systems-and-messaging-q-and-a-bank
level: L6
module: "Interview Mastery (FAANGM + MNC)"
section: "Staff-Level Interview Question Banks"
type: interview-qa
difficulty: senior
order: 7
tags: [distributed-systems, kafka, rabbitmq, consensus, replication, qa-bank, staff]
prerequisites: [system-design-and-architecture-q-and-a-bank]
status: complete
estimated_minutes: 55
last_updated: 2026-06-09
---

# Distributed Systems & Messaging — Q&A Bank (Staff Level)

**55+ questions** on distributed-systems fundamentals + Kafka + RabbitMQ + SQS + consensus + replication + clock + ordering.

## Consensus

### Q: What problem does Raft solve?

- **Difficulty:** senior
- **Asked at:** distributed-systems-curious

**Answer.** Replicated state machine: get N nodes to agree on the same sequence of operations despite some failing. Used in etcd, Consul, TiKV, CockroachDB, Kafka KRaft. Simpler to understand than Paxos. Phases: **leader election**, **log replication**, **safety** (only the leader's log can be committed; quorum = majority).

### Q: Paxos vs Raft?

- **Difficulty:** senior
- **Asked at:** distributed-systems-deep

**Answer.** Both solve consensus. **Paxos** — Leslie Lamport's original; theoretically minimal; notoriously hard to understand and implement. **Multi-Paxos** is the variant used in practice (Chubby, Spanner). **Raft** — designed for understandability + implementation simplicity; explicit leader; same correctness as Paxos. Most modern systems chose Raft for the implementation cost.

### Q: Why did Kafka replace Zookeeper with KRaft?

- **Difficulty:** senior
- **Asked at:** modern Kafka shops

**Answer.** Zookeeper as Kafka's metadata store added: operational complexity (separate cluster), scaling limit (~200k partitions), inconsistent semantics (ZK vs Kafka have different sessions). KRaft (KIP-500, GA in 3.3+) embeds Raft consensus in Kafka brokers themselves — single system to operate, scales to millions of partitions, faster controller failover. As of 2024, Zookeeper deprecated; KRaft is default for new clusters.

## Replication + Consistency

### Q: Single-leader vs multi-leader vs leaderless replication?

- **Difficulty:** senior
- **Asked at:** distributed deep

**Answer.**
- **Single-leader** — one writes, others replicate. Postgres, MySQL primary, Kafka partition. Easy correctness, scales reads not writes.
- **Multi-leader** — multiple writeable nodes. Required for multi-DC active-active. Write conflicts must be resolved (CRDTs, app-defined merge, last-write-wins).
- **Leaderless** (Dynamo-style) — clients write to W nodes, read from R; if R+W>N, quorum guarantees latest read. Cassandra, DynamoDB. Tunable consistency.

### Q: Strong vs eventual vs causal consistency?

- **Difficulty:** senior
- **Asked at:** distributed deep

**Answer.** **Strong (linearizable)** — every read returns the most recent write; appears atomic. Expensive. **Eventual** — replicas converge eventually; reads may be stale. Cheap. **Causal** — if op A causally precedes op B, all observers see A before B. Doesn't require global order, only respects causality. CRDTs are a way to build eventually-consistent systems.

### Q: What's a quorum?

- **Difficulty:** senior
- **Asked at:** distributed deep

**Answer.** Subset of nodes that must agree for an operation to commit. For N replicas: read quorum **R**, write quorum **W**. If **R + W > N**, any read overlaps with any prior write — strong consistency guarantees. Cassandra: `R = QUORUM, W = QUORUM` gives strong; `R = ONE` gives latency at cost of consistency. Trade latency vs consistency.

### Q: CRDT — what + when?

- **Difficulty:** senior
- **Asked at:** modern distributed (Notion, Figma, collab)

**Answer.** Conflict-free Replicated Data Type. Designed so concurrent updates merge automatically without conflict (commutative, associative, idempotent operations). Examples: G-Counter (grow-only), PN-Counter, OR-Set (observed-remove set), LWW-Register, RGA (sequence for collaborative text). Used in: Riak, Redis modules, Figma, Yjs (CRDT for real-time collab). No coordination needed — eventual consistency with merge correctness.

## Distributed Locking

### Q: How do you implement a distributed lock with Redis?

- **Difficulty:** senior
- **Asked at:** modern distributed

**Answer.** `SET key uniqueValue NX EX 30` — set-if-not-exists with TTL. NX = atomic. TTL prevents lock orphan if holder dies. To release: Lua script that deletes only if value matches (avoid releasing someone else's lock after expiry):

```lua
if redis.call("get", KEYS[1]) == ARGV[1] then
  return redis.call("del", KEYS[1])
else return 0 end
```

### Q: What's wrong with Redis Redlock?

- **Difficulty:** senior
- **Asked at:** distributed-systems-deep

**Answer.** Redlock (multi-Redis quorum lock) was criticised by **Martin Kleppmann** (2016): network delays, clock skew, and GC pauses can cause two clients to believe they hold the lock simultaneously. **Antirez** (Redis author) disagreed publicly. Both have merits. Practical takeaway: distributed locks via timeouts are inherently fragile under failure. Use **fencing tokens** (monotonically increasing version sent with each protected operation) to make the protected resource reject stale lock holders.

### Q: Fencing tokens — what + why?

- **Difficulty:** senior
- **Asked at:** distributed-deep

**Answer.** Lock service returns a **monotonically-increasing token** with each acquisition. Client passes the token with each protected operation. Resource rejects operations with a token < highest seen. If a stale holder (paused by GC, network glitch) tries to act after lock expired, its token is old and operation fails. Robust against any timeout-based fragility.

## Time + Ordering

### Q: Lamport clocks vs vector clocks?

- **Difficulty:** senior
- **Asked at:** distributed-deep

**Answer.** **Lamport clock** — single counter per node; increments on local event; sent with messages; receiver sets `clock = max(local, received) + 1`. Gives partial order — captures causality. **Vector clock** — array of counters, one per node; gives complete causality info — can detect concurrent events that Lamport can't. Vector clocks O(N) overhead; Lamport O(1).

### Q: Why is wall-clock time unreliable in distributed systems?

- **Difficulty:** senior
- **Asked at:** distributed-deep + banking

**Answer.** NTP synchronisation has 1-100ms drift; bad networks have hundreds of ms. Servers can have wildly different wall clocks. Implications: can't rely on `Instant.now()` for ordering across machines; "leader's clock is 5 seconds ahead" can cause stale-data reads in time-based logic. Use logical clocks (Lamport/vector) or **TrueTime** (Spanner's GPS+atomic-clock + uncertainty bound).

### Q: TrueTime — what + why?

- **Difficulty:** senior
- **Asked at:** Google + distributed-deep

**Answer.** Google Spanner's clock API. Returns `[earliest, latest]` — an interval that bounds the current time. Backed by GPS receivers + atomic clocks in each data centre, ±few ms. By **waiting out the uncertainty** before committing, Spanner achieves external consistency (transactions ordered as wall-clock saw them) globally. Without TrueTime, achieving same property requires consensus per transaction — slower.

## Messaging: Kafka

### Q: Kafka — partition vs topic vs consumer group?

- **Difficulty:** mid-senior
- **Asked at:** universal

**Answer.** **Topic** = logical stream. **Partition** = ordered log within a topic; the unit of parallelism. **Consumer group** = set of consumers sharing the topic's load; each partition assigned to one consumer in the group. Throughput scales with partition count; consumers in a group can't exceed partitions (excess sit idle).

### Q: How does Kafka guarantee ordering?

- **Difficulty:** senior
- **Asked at:** modern Kafka shops

**Answer.** **Per-partition** ordering only. Messages within a partition are strictly ordered; across partitions, no guarantee. To order all messages for a user → key by `user_id`, partitioner sends same key to same partition. Cost: hot partitions if one user has 80% of traffic.

### Q: Kafka delivery semantics — at-most-once / at-least-once / exactly-once?

- **Difficulty:** senior
- **Asked at:** modern Kafka shops

**Answer.**
- **At-most-once** — producer fires + forgets; consumer commits offset before processing. Messages can be lost.
- **At-least-once** (default) — producer retries until ack; consumer commits offset after processing. Messages can be duplicated.
- **Exactly-once** (since 0.11) — **idempotent producer** (sequence numbers + PID dedupe retries within partition) + **transactional API** (atomic writes across partitions + offset commit) + **read-committed consumer**. End-to-end only within Kafka; external sinks need their own idempotency.

### Q: Kafka idempotent producer — how?

- **Difficulty:** senior
- **Asked at:** Kafka-deep shops

**Answer.** Producer assigned a Producer ID (PID) and per-partition sequence number. Broker dedupes messages with same `(PID, partition, sequence)`. Retries don't produce duplicates within a single producer session. Enable: `enable.idempotence=true` (default since 3.0). Caveat: PID is session-scoped — producer restart starts fresh PID and dedup window resets.

### Q: Kafka transactional API — what?

- **Difficulty:** senior
- **Asked at:** Kafka-deep shops

**Answer.** `producer.initTransactions()` once; then `beginTransaction()` → `send()` → `sendOffsetsToTransaction()` → `commitTransaction()` or `abortTransaction()`. Atomic across **multiple partitions + offset commits**. Consumer with `isolation.level=read_committed` sees only committed messages. Enables exactly-once stream processing (read → process → write atomically).

### Q: Consumer rebalance — what happens?

- **Difficulty:** senior
- **Asked at:** Kafka-deep

**Answer.** When a consumer joins/leaves, the group coordinator re-assigns partitions across remaining consumers. **Stop-the-world** by default — all consumers pause processing until rebalance completes. KIP-429 added **incremental cooperative rebalancing** — partial assignments shift without full pause. Configure with `partition.assignment.strategy=CooperativeStickyAssignor`.

### Q: How do you avoid rebalance storms?

- **Difficulty:** senior
- **Asked at:** Kafka-operations shops

**Answer.**
- **Static membership** (`group.instance.id`) — consumer restart doesn't trigger rebalance during `session.timeout.ms`.
- **Cooperative rebalancing** (KIP-429) — incremental.
- Long-running message processing → ensure `max.poll.interval.ms` is higher than expected processing time; otherwise broker thinks consumer is dead.
- **Pre-warm consumers** before deploy — avoid simultaneous restarts.

### Q: Kafka log compaction vs retention?

- **Difficulty:** senior
- **Asked at:** Kafka-deep

**Answer.**
- **Retention** — keep messages for N days/MB; older deleted. Default for event streams.
- **Compaction** — keep only the latest message per key; old versions deleted by background compactor. Used for **changelog / state** topics — e.g., user-profile-updates where you only care about current state. Required for Kafka Streams state stores.

### Q: When use Kafka Streams vs raw consumer?

- **Difficulty:** senior
- **Asked at:** Kafka-deep shops

**Answer.** **Kafka Streams** is a library: provides stateful operations (joins, aggregations, windows), exactly-once, state stores (RocksDB-backed), elastic scaling via partition reassignment. Use for stream processing pipelines (transform topic A into topic B, with windowed aggregates). Raw consumer for simple consume-and-process. Alternative: **Flink** for more complex stream processing with event-time + watermarks.

## RabbitMQ

### Q: Kafka vs RabbitMQ — when each?

- **Difficulty:** senior
- **Asked at:** modern messaging

**Answer.** **Kafka** — durable log, replay-able, high throughput (millions/sec), stream processing native, pull model. Best for event streaming, analytics pipelines, microservice event bus. **RabbitMQ** — push model, complex routing (exchanges), priority queues, per-message TTL, work-queue patterns. Lower throughput (~tens of thousands/sec). Best for traditional task queues, request-reply, complex routing logic. They overlap; Kafka has largely won the new-system default.

### Q: RabbitMQ exchange types?

- **Difficulty:** mid-senior
- **Asked at:** RabbitMQ shops

**Answer.**
- **Direct** — route by exact routing key match to queue binding.
- **Fanout** — broadcast to all bound queues.
- **Topic** — pattern-match routing key (`order.*.eu`).
- **Headers** — match on message headers.

Producers publish to exchange; exchange routes to queues per bindings; consumers read from queues.

### Q: RabbitMQ ack / nack / requeue?

- **Difficulty:** mid-senior
- **Asked at:** RabbitMQ shops

**Answer.** Manual ack mode: consumer fetches message; processes; calls `basicAck(deliveryTag)` on success, `basicNack(deliveryTag, requeue=true|false)` on failure. `requeue=true` puts message back; `requeue=false` discards (or routes to DLX if configured). Without manual ack (auto-ack), broker considers delivered = done — risky.

## SQS + Cloud Queues

### Q: SQS standard vs FIFO?

- **Difficulty:** mid-senior
- **Asked at:** AWS shops

**Answer.** **Standard** — unbounded throughput, at-least-once delivery, **no ordering guarantee**, **occasional duplicates**. Cheap, default. **FIFO** — strict ordering within a `MessageGroupId`, exactly-once (within 5-min dedup window via `MessageDeduplicationId`), capped at 300 TPS (3000 with batching). Use Standard unless you specifically need ordering or strict dedup.

### Q: SQS visibility timeout — what?

- **Difficulty:** mid-senior
- **Asked at:** AWS shops

**Answer.** When a consumer receives a message, SQS hides it from other consumers for the **visibility timeout** (default 30s). Consumer must `deleteMessage` before timeout to consume; else message becomes visible again (redelivered to another consumer). Set timeout > expected processing time. If processing exceeds, extend via `changeMessageVisibility` (heartbeat pattern).

### Q: DLQ — what + when?

- **Difficulty:** mid-senior
- **Asked at:** AWS + Kafka shops

**Answer.** **Dead Letter Queue**: messages that failed processing N times routed to a separate queue. Prevents poison messages from blocking the main queue indefinitely. Operators inspect DLQ, fix bug or manually requeue. SQS DLQ via `RedrivePolicy` (`maxReceiveCount`). Kafka: implement manually — push to `topic.DLQ` on permanent failure.

## End-to-End Patterns

### Q: Outbox pattern — repeat the deep walkthrough.

- **Difficulty:** senior
- **Asked at:** modern Kafka shops

**Answer.** Problem: writing to DB + publishing to Kafka isn't atomic. Solution: in the same DB transaction, write business state + insert into `outbox` table. A separate **outbox poller** reads new outbox rows and publishes to Kafka; marks rows published after success. Failure → poller retries (rows still unpublished). Pairs well with Debezium CDC streaming the outbox table directly into Kafka.

### Q: Inbox pattern — what + why?

- **Difficulty:** senior
- **Asked at:** modern Kafka shops

**Answer.** Mirror of outbox on the consumer side. When consuming a message, atomically insert into `inbox` table (keyed by message ID) **before** processing. If same message arrives twice (at-least-once Kafka), the second insert violates uniqueness — skip processing. Provides effectively-once semantics on the consumer side.

### Q: Saga orchestration vs choreography?

- **Difficulty:** senior
- **Asked at:** microservices shops

**Answer.**
- **Orchestration** — central orchestrator calls services in sequence, handles failures + compensations explicitly. Easier to understand + monitor; couples orchestrator to all participants.
- **Choreography** — services react to events; emit events on success/failure; everyone listens. Decoupled but harder to see the overall flow.

For 3-4 step sagas, choreography is fine. For 7+ step or complex error paths, orchestration wins maintainability.

### Q: Exactly-once delivery — possible?

- **Difficulty:** senior
- **Asked at:** modern Kafka, distributed-deep

**Answer.** End-to-end exactly-once across systems is provably impossible — Two Generals' Problem / FLP impossibility. What's achievable: **effectively-once** via at-least-once + **idempotent consumer** + dedup. Kafka has "exactly-once" within Kafka boundaries (idempotent + transactional API). External sinks (DB, downstream service) need their own idempotency for the chain to be safe.

### Q: Retry storm — what + mitigations?

- **Difficulty:** senior
- **Asked at:** modern resilience

**Answer.** Downstream is slow/erroring; clients retry aggressively; downstream gets MORE load; crashes harder. Mitigations:
- **Exponential backoff** — 1s, 2s, 4s, 8s, ...
- **Jitter** — randomise to avoid synchronised retries. **Full jitter** (random in [0, backoff]) wins most measurements.
- **Bounded retry count** — fail fast after N.
- **Circuit breaker** — open on threshold, skip retries entirely.
- **Token bucket on retry** — separate from baseline RPS budget.

## Stream Processing

### Q: Kafka Streams vs Flink — when each?

- **Difficulty:** senior
- **Asked at:** stream-processing shops

**Answer.** **Kafka Streams** — Java library, embedded in your app, no cluster needed. Lightweight, simple deploy. Limited to Kafka-only sources. **Flink** — separate cluster, all sources (Kafka, Pulsar, file, JDBC), richer state management, true event-time processing with watermarks, savepoints for upgrades, more complex windowing. Use Streams for simple Kafka→Kafka. Use Flink for complex multi-source pipelines and large state.

### Q: Event time vs processing time?

- **Difficulty:** senior
- **Asked at:** stream processing

**Answer.** **Event time** — when the event happened in the real world (timestamp in payload). **Processing time** — when the system processes the event. Event time accounts for **late-arriving** events (network delays, replays) by using **watermarks** — "we believe all events before time T have arrived." Processing time is simpler but produces wrong answers for any analysis spanning time windows.

### Q: Watermarks — what?

- **Difficulty:** senior
- **Asked at:** stream processing

**Answer.** Watermark of time T means "the system believes no events with timestamp < T will arrive in the future." Triggers window closure. Configurable lateness tolerance: events older than (watermark - allowed_lateness) are dropped or sent to a side output. Trade-off: longer lateness = more correct results but higher latency on emission.

## Deeper Dive — Code-Backed Walkthroughs

### 1. Kafka producer + consumer — full production config

```java
public class PaymentEventProducer {
    private final KafkaProducer<String, PaymentEvent> producer;
    private final String topic;

    public PaymentEventProducer(String bootstrapServers, String topic) {
        this.topic = topic;
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class.getName());

        // Reliability
        props.put(ProducerConfig.ACKS_CONFIG, "all");                              // wait for all in-sync replicas
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);                 // dedup on retry within session
        props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 5);        // max with idempotent
        props.put(ProducerConfig.RETRIES_CONFIG, Integer.MAX_VALUE);               // retry until success or timeout
        props.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, 120_000);             // 2 min hard timeout

        // Throughput
        props.put(ProducerConfig.LINGER_MS_CONFIG, 10);                            // batch up to 10ms
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 32 * 1024);                    // 32 KB
        props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "lz4");                  // best speed/compression tradeoff

        // Memory
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 64 * 1024 * 1024);          // 64 MB local buffer

        this.producer = new KafkaProducer<>(props);
    }

    public CompletableFuture<RecordMetadata> publish(String key, PaymentEvent event) {
        CompletableFuture<RecordMetadata> future = new CompletableFuture<>();
        producer.send(new ProducerRecord<>(topic, key, event), (metadata, exception) -> {
            if (exception != null) future.completeExceptionally(exception);
            else future.complete(metadata);
        });
        return future;
    }
}

public class PaymentEventConsumer {
    private final KafkaConsumer<String, PaymentEvent> consumer;

    public PaymentEventConsumer(String bootstrapServers, String groupId, String topic) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class.getName());

        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");            // start from beginning if no offset
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);                // manual commit for processing safety
        props.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");        // only see committed (skip aborted)
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 500);
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 5 * 60 * 1000);      // 5 min — must process within this

        // Cooperative rebalance (KIP-429) — no full stop-the-world
        props.put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG,
                  CooperativeStickyAssignor.class.getName());

        // Static membership reduces rebalance on restart (set the instance ID)
        props.put(ConsumerConfig.GROUP_INSTANCE_ID_CONFIG, System.getenv("POD_NAME"));

        this.consumer = new KafkaConsumer<>(props);
        consumer.subscribe(List.of(topic));
    }

    public void runLoop() {
        while (!Thread.currentThread().isInterrupted()) {
            ConsumerRecords<String, PaymentEvent> records = consumer.poll(Duration.ofMillis(500));
            for (ConsumerRecord<String, PaymentEvent> record : records) {
                process(record.value());                           // your business logic
            }
            consumer.commitSync();                                  // commit after processing batch
        }
    }
}
```

### 2. Outbox pattern + Debezium CDC

```sql
-- The outbox table; written in the same transaction as the business state
CREATE TABLE outbox_events (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    aggregate_id VARCHAR(64) NOT NULL,
    event_type   VARCHAR(64) NOT NULL,
    payload      JSONB NOT NULL,
    created_at   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    published    BOOLEAN NOT NULL DEFAULT FALSE
);
CREATE INDEX ix_outbox_unpublished ON outbox_events(created_at) WHERE NOT published;
```

```java
@Service
public class OrderService {
    private final OrderRepository orders;
    private final OutboxEventRepository outbox;

    @Transactional
    public Order placeOrder(OrderRequest req) {
        Order order = new Order(req);
        orders.save(order);                                    // ← business write

        outbox.save(new OutboxEvent(                            // ← same tx, atomic with business write
            order.getId(),
            "OrderPlaced",
            toJson(new OrderPlacedEvent(order))
        ));

        return order;
    }
}
```

**Debezium consumes the outbox table** via Postgres logical replication and publishes to Kafka:

```yaml
# Debezium connector config
name: orders-outbox-connector
config:
  connector.class: io.debezium.connector.postgresql.PostgresConnector
  database.hostname: db.example.com
  database.port: 5432
  database.user: debezium
  database.dbname: orders
  database.server.name: orders
  table.include.list: public.outbox_events
  plugin.name: pgoutput
  publication.autocreate.mode: filtered
  transforms: outbox
  transforms.outbox.type: io.debezium.transforms.outbox.EventRouter
  transforms.outbox.route.by.field: event_type
  transforms.outbox.table.field.event.payload: payload
```

Result: every row inserted into `outbox_events` becomes a Kafka message on a topic named after `event_type`. Outbox rows can be archived/deleted after a retention period.

**Why this beats dual-write**: writing to DB + publishing to Kafka separately can fail half-way; outbox makes both atomic via the single DB transaction.

### 3. Distributed lock with Redlock + fencing

```java
public class DistributedLockService {
    private final List<JedisPool> redisNodes;                  // 5 independent Redis nodes
    private final AtomicLong fencingTokenSeq = new AtomicLong();

    public Optional<FencedLock> tryAcquire(String resource, Duration ttl) {
        long start = System.currentTimeMillis();
        String value = UUID.randomUUID().toString();
        long fencingToken = fencingTokenSeq.incrementAndGet();

        int acquired = 0;
        for (JedisPool pool : redisNodes) {
            try (Jedis jedis = pool.getResource()) {
                if ("OK".equals(jedis.set("lock:" + resource, value, SetParams.setParams().nx().px(ttl.toMillis())))) {
                    acquired++;
                }
            } catch (Exception e) {
                // node unreachable; continue trying others
            }
        }

        long elapsed = System.currentTimeMillis() - start;
        boolean haveMajority = acquired >= (redisNodes.size() / 2 + 1);
        boolean stillValid = elapsed < ttl.toMillis() * 0.8;          // leave margin for clock drift

        if (haveMajority && stillValid) {
            return Optional.of(new FencedLock(resource, value, fencingToken));
        }

        // Couldn't get majority — release any acquired locks
        releaseAll(resource, value);
        return Optional.empty();
    }

    public void release(FencedLock lock) {
        String script = """
            if redis.call("get", KEYS[1]) == ARGV[1] then
                return redis.call("del", KEYS[1])
            else return 0 end""";
        for (JedisPool pool : redisNodes) {
            try (Jedis jedis = pool.getResource()) {
                jedis.eval(script, List.of("lock:" + lock.resource()), List.of(lock.value()));
            } catch (Exception e) { /* best-effort */ }
        }
    }

    private void releaseAll(String resource, String value) { /* same as release() */ }

    public record FencedLock(String resource, String value, long fencingToken) {}
}
```

**Critical**: combine with fencing tokens at the resource side (covered in T06 walkthrough #5). Redlock alone isn't enough under network partition + GC pause scenarios per Kleppmann.

### 4. Vector clock for conflict detection

```java
public class VectorClock {
    private final Map<String, Long> versions = new HashMap<>();   // nodeId → count

    public void increment(String nodeId) {
        versions.merge(nodeId, 1L, Long::sum);
    }

    public void merge(VectorClock other) {
        for (var e : other.versions.entrySet()) {
            versions.merge(e.getKey(), e.getValue(), Long::max);
        }
    }

    public enum Relation { BEFORE, AFTER, CONCURRENT, EQUAL }

    public Relation compare(VectorClock other) {
        boolean thisHasGreater = false, otherHasGreater = false;
        Set<String> allNodes = new HashSet<>();
        allNodes.addAll(this.versions.keySet());
        allNodes.addAll(other.versions.keySet());
        for (String node : allNodes) {
            long a = this.versions.getOrDefault(node, 0L);
            long b = other.versions.getOrDefault(node, 0L);
            if (a > b) thisHasGreater = true;
            if (b > a) otherHasGreater = true;
        }
        if (thisHasGreater && otherHasGreater) return Relation.CONCURRENT;       // conflict
        if (thisHasGreater) return Relation.AFTER;
        if (otherHasGreater) return Relation.BEFORE;
        return Relation.EQUAL;
    }
}
```

**Use case**: Dynamo-style storage detects concurrent writes (CONCURRENT result) and surfaces them to the application for conflict resolution (CRDTs, last-write-wins, or app-level merge).

### 5. Token bucket rate limiter — distributed + atomic via Lua

```lua
-- limiter.lua: atomic token-bucket consume
local key            = KEYS[1]
local capacity       = tonumber(ARGV[1])
local refillPerSec   = tonumber(ARGV[2])
local nowMs          = tonumber(ARGV[3])
local cost           = tonumber(ARGV[4])

local data           = redis.call("HMGET", key, "tokens", "lastRefillMs")
local tokens         = tonumber(data[1])
local lastRefillMs   = tonumber(data[2])

if tokens == nil then
    tokens = capacity
    lastRefillMs = nowMs
end

-- Refill based on elapsed time
local elapsedSec     = (nowMs - lastRefillMs) / 1000
local refillAmount   = elapsedSec * refillPerSec
tokens               = math.min(capacity, tokens + refillAmount)
lastRefillMs         = nowMs

local allowed = 0
if tokens >= cost then
    tokens = tokens - cost
    allowed = 1
end

redis.call("HMSET", key, "tokens", tokens, "lastRefillMs", lastRefillMs)
redis.call("EXPIRE", key, 3600)
return allowed
```

```java
@Component
public class RedisRateLimiter {
    private final StringRedisTemplate redis;
    private final DefaultRedisScript<Long> script;

    public RedisRateLimiter(StringRedisTemplate redis,
                            @Value("classpath:limiter.lua") Resource limiterScript) throws IOException {
        this.redis = redis;
        this.script = new DefaultRedisScript<>(
            new String(limiterScript.getInputStream().readAllBytes()), Long.class);
    }

    public boolean tryAcquire(String userId, int capacity, int refillPerSec, int cost) {
        Long allowed = redis.execute(
            script,
            List.of("rl:" + userId),
            String.valueOf(capacity),
            String.valueOf(refillPerSec),
            String.valueOf(System.currentTimeMillis()),
            String.valueOf(cost)
        );
        return allowed != null && allowed == 1;
    }
}
```

**Why Lua**: Redis executes scripts atomically — no race between read-modify-write across multiple clients. Without it, two clients reading "9 tokens left" simultaneously would both decrement, allowing 2 calls when only 1 was budgeted.

## Sources & Further Reading

- [Designing Data-Intensive Applications — Kleppmann](https://dataintensive.net/)
- [Kafka Definitive Guide](https://www.oreilly.com/library/view/kafka-the-definitive/9781492043072/)
- [Confluent blog](https://www.confluent.io/blog/)
- [Martin Kleppmann — Distributed locks paper](https://martin.kleppmann.com/2016/02/08/how-to-do-distributed-locking.html)
- [Antirez — Redlock response](http://antirez.com/news/101)

## Recap

55+ Q&As on consensus, replication, consistency, distributed locking, time/ordering, Kafka deep, RabbitMQ, SQS, end-to-end patterns (outbox/inbox/saga). The conversations that decide modern microservices interviews.

## Next

Continue to [Microservices, APIs & Cloud — Q&A Bank](./T08-microservices-apis-and-cloud-q-and-a-bank.md).
