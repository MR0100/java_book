---
title: "Testcontainers"
slug: testcontainers
level: L4
module: "Backend Engineering"
section: "Testing — Advanced"
type: concept
difficulty: senior
order: 3
tags: [testcontainers, docker, postgres, kafka, redis, mongodb, integration-testing, junit5, reusable-containers, ryuk, dynamic-properties, container-lifecycle, parallelization]
prerequisites: [spring-boot-test-slices]
status: complete
estimated_minutes: 50
last_updated: 2026-06-08
---

# Testcontainers

Testcontainers (started ~2015 by Richard North, now a Docker Inc. project) is the Java library that broke the "tests need an in-memory DB" assumption. It launches real services — Postgres, MySQL, Kafka, Redis, MongoDB, RabbitMQ, Elasticsearch, even arbitrary custom images — in Docker, exposes ephemeral ports, and tears them down after the test. The result is integration tests that use *exactly* the same software as production, with millisecond-of-difference dialect behavior, real binary protocols, and real wire-format edge cases.

For 2026, Testcontainers is the senior default for any test that touches a database, queue, or third-party service. This topic covers the core model (containers as JUnit resources), the Spring Boot 3.1+ `@ServiceConnection` integration, container reuse, Singleton container pattern, and the operational concerns (Docker availability, CI parallelization, cleanup via Ryuk).

> [!NOTE]
> Prerequisites: [Spring Boot test slices (L4/C09/T02)](./T02-spring-boot-test-slices.md). Docker available locally and in CI.

## Why Testcontainers Won

Before Testcontainers, integration tests had three options:
1. **In-memory DBs** (H2, HSQLDB): wrong dialect, missing features.
2. **Shared dev DB**: state pollution, flaky tests.
3. **Manually-managed local services**: brittle setup, "works on my machine".

Testcontainers gave a fourth:
4. **Containerized real services per test run**: same software as prod, clean state, automatic cleanup.

The trade-off: Docker dependency (developer machines and CI). In 2026 that's universal — even GitHub-hosted runners and most enterprise CI support Docker.

## Minimal Postgres Example

```xml
<dependency>
  <groupId>org.testcontainers</groupId>
  <artifactId>junit-jupiter</artifactId>
  <version>1.20.4</version>
  <scope>test</scope>
</dependency>
<dependency>
  <groupId>org.testcontainers</groupId>
  <artifactId>postgresql</artifactId>
  <version>1.20.4</version>
  <scope>test</scope>
</dependency>
```

```java
@Testcontainers
class OrderRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
        .withDatabaseName("test")
        .withUsername("test")
        .withPassword("test");

    @Test
    void canConnect() throws Exception {
        try (Connection conn = DriverManager.getConnection(
                postgres.getJdbcUrl(),
                postgres.getUsername(),
                postgres.getPassword())) {
            assertThat(conn.isValid(2)).isTrue();
        }
    }
}
```

Lifecycle:
- `@Container` + `static` → one container per class, started before all tests, stopped after.
- `@Container` + instance → one container per test method (slower).
- `@Testcontainers` extension manages start/stop.

## Spring Boot Integration — `@ServiceConnection` (3.1+)

Before Spring Boot 3.1, you had to wire Testcontainers into Spring properties manually:

```java
@Container
static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

@DynamicPropertySource
static void props(DynamicPropertyRegistry r) {
    r.add("spring.datasource.url", postgres::getJdbcUrl);
    r.add("spring.datasource.username", postgres::getUsername);
    r.add("spring.datasource.password", postgres::getPassword);
}
```

Spring Boot 3.1+ simplifies with `@ServiceConnection`:

```java
@SpringBootTest
@Testcontainers
class OrderTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired OrderRepository repo;

    @Test
    void persists() {
        repo.save(new Order("user-1", 99.99));
        assertThat(repo.count()).isEqualTo(1);
    }
}
```

Spring auto-configures the `DataSource` from the container — no manual property wiring. `@ServiceConnection` works for many containers (Postgres, MySQL, MariaDB, MongoDB, Kafka, RabbitMQ, Redis, Cassandra, Neo4j, OpenSearch, Elasticsearch, etc.).

## Other Common Containers

### Kafka

```java
@Container
@ServiceConnection
static KafkaContainer kafka = new KafkaContainer(
    DockerImageName.parse("confluentinc/cp-kafka:7.5.0"));
```

Spring Boot auto-configures `spring.kafka.bootstrap-servers`.

### Redis (via GenericContainer)

```java
@Container
@ServiceConnection(name = "redis")
static GenericContainer<?> redis = new GenericContainer<>("redis:7-alpine")
    .withExposedPorts(6379);
```

### MongoDB

```java
@Container
@ServiceConnection
static MongoDBContainer mongo = new MongoDBContainer("mongo:7");
```

### RabbitMQ

```java
@Container
@ServiceConnection
static RabbitMQContainer rabbit = new RabbitMQContainer("rabbitmq:3-management");
```

### Custom Image

```java
@Container
static GenericContainer<?> custom = new GenericContainer<>("my-org/custom-service:1.0")
    .withExposedPorts(8080)
    .withEnv("CONFIG", "test")
    .waitingFor(Wait.forHttp("/actuator/health"));

String url = "http://" + custom.getHost() + ":" + custom.getMappedPort(8080);
```

## Singleton Container Pattern

Per-class containers are simple but slow (one boot per class). For a 200-class test suite, that's 200 Postgres boots.

The senior pattern: one shared singleton container across the entire JVM run.

```java
public abstract class IntegrationTestBase {

    static final PostgreSQLContainer<?> POSTGRES;

    static {
        POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine")
            .withReuse(true);   // crucial
        POSTGRES.start();
    }

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        r.add("spring.datasource.username", POSTGRES::getUsername);
        r.add("spring.datasource.password", POSTGRES::getPassword);
    }
}

@SpringBootTest
class OrderTest extends IntegrationTestBase { ... }
```

All tests extending `IntegrationTestBase` share one Postgres container. Boots once at JVM startup; reused across all tests.

Concerns:
- **State isolation**: tests must clean up after themselves (transactional rollback or explicit cleanup).
- **Schema**: Flyway/Liquibase migrations run once.

## Container Reuse — Cross-JVM

`withReuse(true)` + `~/.testcontainers.properties` containing `testcontainers.reuse.enable=true` keeps the container running *between JVM runs*.

```bash
echo 'testcontainers.reuse.enable=true' >> ~/.testcontainers.properties
```

Now `mvn test` finishes, container survives. Next `mvn test` reuses it instantly.

In CI: typically don't enable (fresh per build). In dev: enable for fast iteration.

## Wait Strategies

Containers can take time to be ready. Default wait strategies:
- `Wait.forListeningPort()`: TCP port responds.
- `Wait.forHttp("/healthz").forStatusCode(200)`: HTTP endpoint healthy.
- `Wait.forLogMessage(".*ready to accept.*", 1)`: log pattern.

```java
new GenericContainer<>("my-service:1.0")
    .waitingFor(Wait.forHttp("/healthz")
        .withStartupTimeout(Duration.ofMinutes(2)));
```

Pre-built containers (Postgres, Kafka) have sane defaults.

## Network Between Containers

For multi-service tests:

```java
Network network = Network.newNetwork();

GenericContainer<?> db = new PostgreSQLContainer<>("postgres:16")
    .withNetwork(network)
    .withNetworkAliases("db");

GenericContainer<?> app = new GenericContainer<>("my-app:1.0")
    .withNetwork(network)
    .withEnv("DB_URL", "jdbc:postgresql://db:5432/test")
    .dependsOn(db);
```

`db` is reachable from `app` as hostname `db`.

## Ryuk — The Garbage Collector

Testcontainers starts a sidecar called *Ryuk* — a small container that auto-cleans up containers if the JVM crashes. Without Ryuk: stale containers eat resources.

In some CI environments (where containers can't start containers), disable Ryuk:

```bash
export TESTCONTAINERS_RYUK_DISABLED=true
```

But then you must clean up manually.

## CI Considerations

### GitHub Actions

```yaml
- name: Run tests
  run: ./mvnw test
```

GitHub-hosted runners have Docker. Just works.

### Docker-in-Docker

In Kubernetes-based CI, you may need Docker-in-Docker or a sidecar. Testcontainers supports this via the `testcontainers.docker.host` config.

### Resource Limits

Containers consume RAM/CPU. In small CI runners, scale down — fewer parallel tests, smaller image variants (`postgres:16-alpine` vs full).

## Parallelization

Run test classes in parallel for faster CI. Configure JUnit 5:

```properties
# junit-platform.properties
junit.jupiter.execution.parallel.enabled = true
junit.jupiter.execution.parallel.mode.default = same_thread
junit.jupiter.execution.parallel.mode.classes.default = concurrent
```

With singleton containers, parallelism is safe: all classes share one container; isolated by transactions.

With per-class containers, each class needs its own — N classes × N containers in parallel. Watch resource usage.

## Migrations And Seed Data

```java
@Container
@ServiceConnection
static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
    .withInitScript("init-schema.sql");
```

Or rely on Flyway/Liquibase auto-run at Spring boot.

## Cleanup Strategies

After every test:

**Option A**: `@Transactional` rollback. Fast, simple, but doesn't test commits.

**Option B**: Truncate tables after each test:

```java
@Autowired DataSource dataSource;

@AfterEach
void cleanup() throws SQLException {
    try (Connection c = dataSource.getConnection(); Statement s = c.createStatement()) {
        s.execute("TRUNCATE TABLE orders, users, payments CASCADE");
    }
}
```

**Option C**: Snapshot-and-restore (advanced; use `pg_dump`).

## Real-World: Kafka Producer Test

```java
@SpringBootTest
@Testcontainers
class OrderEventPublisherTest {

    @Container
    @ServiceConnection
    static KafkaContainer kafka = new KafkaContainer(
        DockerImageName.parse("confluentinc/cp-kafka:7.5.0"));

    @Autowired OrderEventPublisher publisher;
    @Autowired KafkaTemplate<String, String> template;

    @Test
    void publishesEvent() {
        publisher.publish(new OrderPlacedEvent("order-1", "user-1", 99.99));

        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps(
            "test-group", "true", kafka);
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerProps,
                new StringDeserializer(), new StringDeserializer())) {
            consumer.subscribe(List.of("order-events"));
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(5));
            assertThat(records).hasSize(1);
            assertThat(records.iterator().next().value()).contains("order-1");
        }
    }
}
```

Real Kafka. Real broker. Same software as prod.

## Performance Numbers

Typical M3 Mac:
- Postgres 16 alpine cold start: ~1.5s.
- Kafka cold start: ~5s.
- MongoDB cold start: ~2s.
- Redis cold start: ~0.3s.

With singleton + reuse: nearly free amortized cost.

## Anti-Patterns

> [!WARNING]
> **One container per test method.** Wasteful.

> [!WARNING]
> **No singleton pattern.** Per-class boots add up.

> [!WARNING]
> **`postgres:latest`.** Pin tags so tests are reproducible.

> [!WARNING]
> **No state cleanup between tests.** Sharing containers means sharing pollution.

> [!WARNING]
> **Big images** (`postgres:16` ~ 350 MB; `postgres:16-alpine` ~ 80 MB). Use alpine variants.

> [!WARNING]
> **Disabling Ryuk without manual cleanup.** Containers leak.

> [!WARNING]
> **Hardcoded ports.** Use `getMappedPort`.

> [!WARNING]
> **Wait by sleeping.** Use Testcontainers wait strategies.

> [!WARNING]
> **Network without aliases.** Container-to-container DNS breaks.

## Common Misconceptions

> [!WARNING]
> **"Testcontainers is slow."** With singleton + reuse, it's nearly as fast as H2.

> [!WARNING]
> **"Requires Docker Desktop license."** Many alternatives (Colima, Rancher Desktop, OrbStack) work.

> [!WARNING]
> **"Doesn't work in CI."** GitHub, GitLab, CircleCI, Jenkins — all support Docker.

> [!WARNING]
> **"Only for databases."** Anything containerized works.

> [!WARNING]
> **"Replaces unit tests."** Complements them. Unit tests for logic; Testcontainers for integration.

## Practice

1. **First Postgres container**: write a `@DataJpaTest` against real Postgres.
2. **`@ServiceConnection`**: use the Spring Boot 3.1+ shortcut.
3. **Singleton container**: extract `IntegrationTestBase` with shared static container.
4. **Container reuse**: enable `testcontainers.reuse.enable`; observe second run starts instantly.
5. **Kafka test**: integration test that produces and consumes.
6. **Multi-container test**: app + Postgres + Redis on a shared network.
7. **Custom image**: containerize a small Go/Node service; test against it from Java.
8. **CI**: run your suite in GitHub Actions.
9. **Parallelization**: enable parallel test execution; verify safety.
10. **Migration**: use Flyway with the container; verify schema applied.

## Recap

You should now be able to:

- Start Postgres, Kafka, Redis, Mongo in tests via Testcontainers.
- Use `@ServiceConnection` in Spring Boot 3.1+.
- Implement the singleton container pattern for speed.
- Enable cross-JVM reuse for fast local iteration.
- Configure wait strategies and networks.
- Handle CI integration.
- Avoid common Testcontainers pitfalls.

## Next

Continue to [Behavior-Driven Development (BDD, Cucumber)](./T04-behavior-driven-development-bdd-cucumber.md) — the Gherkin-based style for expressing tests as executable specifications collaborated on with non-engineers.
