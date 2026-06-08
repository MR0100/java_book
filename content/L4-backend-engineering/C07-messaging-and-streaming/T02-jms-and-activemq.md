---
title: "JMS & ActiveMQ"
slug: jms-and-activemq
level: L4
module: "Backend Engineering"
section: "Messaging & Event Streaming"
type: concept
difficulty: senior
order: 2
tags: [jms, java-message-service, jakarta-messaging, activemq, artemis, ibm-mq, jms-queue, jms-topic, message-producer, message-consumer, jmstemplate, spring-jms, jmslistener, message-driven-pojo, durable-subscription, message-selector, jms-transaction, embedded-broker, jms-2-api, jms-vs-amqp, legacy-messaging, point-to-point-jms]
prerequisites: [messaging-concepts-queues-topics-pub-sub]
status: complete
estimated_minutes: 35
last_updated: 2026-06-08
---

# JMS & ActiveMQ

JMS (Java Message Service; now Jakarta Messaging) is the **standard Java API for messaging** since 1998 — interface contracts (`ConnectionFactory`, `Destination`, `MessageProducer`, `MessageConsumer`, `Message`) implementations of which let Java code be broker-portable in theory. **ActiveMQ** (Apache, ~2004) and its modern successor **ActiveMQ Artemis** (2014) are the dominant Java-native JMS brokers. **IBM MQ**, **TIBCO EMS**, and others provide enterprise-grade JMS for legacy stacks.

In 2026 JMS is **legacy-dominated** — new Spring services typically pick Kafka or RabbitMQ over JMS+ActiveMQ. But many existing systems still run JMS; integration patterns require knowing it; and embedded ActiveMQ for testing remains useful. **Spring JMS** (`spring-jms`, `spring-boot-starter-activemq`) integrates cleanly.

This is a short topic. We cover: the JMS programming model; ActiveMQ vs Artemis vs IBM MQ; Spring JMS (`JmsTemplate`, `@JmsListener`); transactional sends; durable subscriptions; when to use JMS vs Kafka/RabbitMQ in 2026.

> [!NOTE]
> Prerequisites: [Messaging concepts (T01)](./T01-messaging-concepts-queues-topics-pub-sub.md).

## The JMS Model

```java
ConnectionFactory cf = new ActiveMQConnectionFactory("tcp://localhost:61616");
try (JMSContext ctx = cf.createContext()) {
    Queue queue = ctx.createQueue("orders");

    // Producer
    JMSProducer producer = ctx.createProducer();
    producer.send(queue, "{ \"orderId\": 42 }");

    // Consumer
    JMSConsumer consumer = ctx.createConsumer(queue);
    String message = consumer.receiveBody(String.class, 5000);   // 5s timeout
}
```

JMS 2.0 (Jakarta Messaging) replaced verbose JMS 1.1 with `JMSContext`. Two destinations:

- `Queue` — point-to-point.
- `Topic` — publish-subscribe.

Message types: `TextMessage`, `BytesMessage`, `MapMessage`, `ObjectMessage`, `StreamMessage`. Headers (`JMSCorrelationID`, `JMSReplyTo`, `JMSExpiration`, `JMSPriority`) carry routing/control metadata.

## Spring JMS

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-artemis</artifactId>
</dependency>
```

Boot auto-configures `ConnectionFactory`, `JmsTemplate`, listener container.

### `JmsTemplate`

```java
@Service
public class OrderPublisher {
    private final JmsTemplate jms;
    public OrderPublisher(JmsTemplate jms) { this.jms = jms; }

    public void publish(Order order) {
        jms.convertAndSend("orders", order);   // JSON via Jackson by default
    }
}
```

### `@JmsListener`

```java
@Component
public class OrderListener {

    @JmsListener(destination = "orders")
    public void handle(Order order, @Header("JMSMessageID") String msgId) {
        log.info("processing {}", msgId);
        // process
    }
}
```

`@JmsListener` works like `@KafkaListener` (L4/C01/T22). Container manages consumer threads.

### Configuration

```yaml
spring:
  jms:
    pub-sub-domain: false        # queues by default; true for topics
    listener:
      concurrency: 5-10           # 5 to 10 consumer threads
      acknowledge-mode: client    # manual ack
  artemis:
    mode: embedded                # start embedded broker (test)
    # or:
    # mode: native
    # broker-url: tcp://artemis:61616
```

## Transactional Send

```java
@Transactional
public void placeOrder(Order order) {
    orderRepo.save(order);
    jms.convertAndSend("orders.placed", order);
    // both DB + JMS commit together (XA) or roll back together
}
```

For cross-resource (DB + JMS) atomicity, JMS supports XA transactions — but they're slow and brittle. The **transactional outbox** pattern (T09) is preferred in modern code.

## Durable Subscriptions

Topic subscribers normally miss messages while offline. Durable subscription:

```java
@JmsListener(destination = "events", subscription = "myDurableSub", containerFactory = "topicFactory")
public void onEvent(Event e) { ... }
```

Broker holds messages for this subscription even when consumer is down; redelivers on reconnect.

## ActiveMQ vs Artemis vs IBM MQ

- **ActiveMQ "Classic"**: older; v5.x; widely deployed legacy.
- **ActiveMQ Artemis**: rewrite; current; v2.x; high-performance.
- **IBM MQ**: commercial; ubiquitous in banks / insurance.
- **TIBCO EMS, Red Hat AMQ (Artemis-based)**: enterprise.

For new code: Artemis (open source) or managed (Amazon MQ).

## JMS Vs Kafka Vs RabbitMQ In 2026

| Aspect | JMS (Artemis) | Kafka | RabbitMQ |
|--------|:-------------:|:-----:|:--------:|
| Java standard API | ✅ | ❌ | partial |
| Broker model | queue + topic | log | exchange + queue |
| Throughput | medium | very high | high |
| Latency | low | low | low |
| Replay | weak | strong | weak |
| Tooling | mature | excellent | excellent |
| Adoption (new code) | declining | dominant for streams | dominant for tasks |
| Best for | legacy interop, embedded | event streaming | rich routing, task queues |

**New systems usually pick Kafka or RabbitMQ over JMS.** JMS lingers in: legacy banking/insurance integration; embedded testing scenarios; teams already invested.

## When JMS Still Makes Sense

- **Existing IBM MQ / TIBCO ecosystem**.
- **Embedded broker for tests** (Artemis embedded mode).
- **Standardized API**: portable Java code.
- **Strong XA support**: when XA is mandated (rare).

## Common Pitfalls

> [!WARNING]
> **Object messages with Java serialization.** Versioning hell; security risk. Use JSON.

> [!WARNING]
> **XA transactions for performance-critical paths.** 3-10× slower; brittle. Use outbox.

> [!WARNING]
> **Non-durable subscription on topic for important events.** Loss on consumer downtime.

> [!WARNING]
> **Concurrency too high.** Order broken across messages.

> [!WARNING]
> **JMS for new event-stream needs.** Kafka is the better choice.

> [!WARNING]
> **ActiveMQ Classic on new deploys.** Pick Artemis.

## Practice

1. Wire Spring Boot + Artemis (embedded). Send a message via `JmsTemplate`; consume via `@JmsListener`.
2. Configure durable subscription on a topic; consumer down; produce 5; reconnect; verify 5 delivered.
3. Use message selector to filter incoming messages by header.
4. Add XA transaction across DB and JMS; observe perf hit; compare to outbox.
5. Compare your JMS implementation with equivalent Kafka.
6. Embed Artemis for integration tests; verify it starts/stops cleanly.
7. Audit your team's stack: JMS use cases that should migrate to Kafka or RabbitMQ.

## Recap

You should now be able to:

- Use JMS 2.0 API: `JMSContext`, `JMSProducer`, `JMSConsumer`, messages.
- Use Spring JMS: `JmsTemplate`, `@JmsListener`, configuration.
- Apply durable subscriptions for offline-tolerant topic consumers.
- Configure Artemis embedded for testing.
- Recognize when JMS is legacy interop vs new-code choice.
- Prefer Kafka / RabbitMQ over JMS for greenfield.
- Avoid XA transactions; use transactional outbox.
- Avoid the canonical pitfalls: ObjectMessage, XA in hot path, non-durable topic for important events.

## Next

Continue to [RabbitMQ (AMQP)](./T03-rabbitmq-amqp.md) for the AMQP-based broker that dominates task-queue and rich-routing use cases.
