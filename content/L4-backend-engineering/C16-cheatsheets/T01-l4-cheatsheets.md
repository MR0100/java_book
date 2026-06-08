---
title: "L4 Cheatsheets"
slug: l4-cheatsheets
level: L4
module: "Backend Engineering"
section: "Cheatsheets"
type: cheatsheet
difficulty: senior
order: 1
tags: [cheatsheets, spring, jpa, postgres, sql, kafka, redis, kubectl, docker, k9s, helm, observability, security, http, jvm, gradle, maven]
prerequisites: []
status: complete
estimated_minutes: 30
last_updated: 2026-06-08
---

# L4 Cheatsheets

Dense reference material for L4 topics — copy-paste, glance during incidents, recall during interviews. Each section is a tight cheatsheet for one concept area. The aim is *correctness and density*, not tutorial. If the syntax isn't here, you don't need it daily.

> [!NOTE]
> Prerequisites: comfortable with L4 chapters. Use as ongoing reference.

---

## Spring Boot Quick Reference

### Annotations You'll Use Daily

| Annotation | What it does |
|---|---|
| `@SpringBootApplication` | Main class. Auto-config + scan + config. |
| `@RestController` | REST controller (= `@Controller` + `@ResponseBody`). |
| `@RequestMapping` / `@GetMapping` / `@PostMapping` / `@PutMapping` / `@DeleteMapping` / `@PatchMapping` | Route HTTP methods. |
| `@PathVariable` / `@RequestParam` / `@RequestBody` / `@RequestHeader` | Parameter binding. |
| `@Valid` | Trigger Bean Validation. |
| `@Service` / `@Repository` / `@Component` | Bean stereotypes. |
| `@Autowired` | Inject (prefer constructor injection). |
| `@Configuration` + `@Bean` | Java config. |
| `@ConfigurationProperties("foo")` | Type-safe property binding. |
| `@Value("${foo.bar}")` | Property injection (prefer `@ConfigurationProperties`). |
| `@Profile("dev")` | Conditional on profile. |
| `@ConditionalOnProperty` | Conditional on property. |
| `@Transactional` | Wrap method in TX. |
| `@RestControllerAdvice` | Global exception handler. |
| `@ExceptionHandler(MyException.class)` | Handle specific exception. |
| `@Scheduled(cron = "...")` | Schedule task. |
| `@Async` | Run async (requires `@EnableAsync`). |
| `@Cacheable("orders")` | Cache method result. |
| `@CacheEvict(value="orders", key="#id")` | Invalidate cache. |
| `@EventListener` | Listen for application events. |
| `@TransactionalEventListener(phase=AFTER_COMMIT)` | Post-commit event. |

### Useful application.yml Snippets

```yaml
server:
  port: 8080
  shutdown: graceful
  tomcat:
    threads:
      max: 200
      min-spare: 10
    connection-timeout: 5s

spring:
  application:
    name: orderhub
  threads:
    virtual:
      enabled: true     # Java 21+
  datasource:
    url: jdbc:postgresql://${DB_HOST}:5432/${DB_NAME}
    username: ${DB_USER}
    password: ${DB_PASS}
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 5000
      idle-timeout: 600000
      max-lifetime: 1800000
  jpa:
    open-in-view: false
    hibernate:
      ddl-auto: none
    properties:
      hibernate:
        jdbc.batch_size: 50
        order_inserts: true
  flyway:
    enabled: true
    locations: classpath:db/migration
  lifecycle:
    timeout-per-shutdown-phase: 30s

management:
  endpoints:
    web:
      exposure:
        include: health, info, metrics, prometheus
  endpoint:
    health:
      probes:
        enabled: true
      show-details: when-authorized
  metrics:
    distribution:
      percentiles-histogram:
        http.server.requests: true

logging:
  level:
    root: INFO
    com.example: DEBUG
```

---

## JPA Quick Reference

### Entity Annotations

```java
@Entity
@Table(name = "orders", indexes = {
    @Index(name = "idx_orders_tenant", columnList = "tenant_id, status")
})
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "tenant_id", nullable = false)
    private String tenantId;
    
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    
    @Column(precision = 12, scale = 2)
    private BigDecimal total;
    
    @CreationTimestamp
    private Instant createdAt;
    
    @UpdateTimestamp
    private Instant updatedAt;
    
    @Version
    private long version;
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
```

### Spring Data Repository Patterns

```java
public interface OrderRepository extends JpaRepository<Order, UUID> {
    // Derived query
    List<Order> findByTenantIdAndStatus(String tenantId, OrderStatus status);
    
    // Pagination
    Page<Order> findByTenantId(String tenantId, Pageable pageable);
    
    // JPQL
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.items WHERE o.tenantId = :tid")
    List<Order> findAllWithItems(@Param("tid") String tid);
    
    // Native
    @Query(value = "SELECT * FROM orders WHERE total > :min", nativeQuery = true)
    List<Order> findHighValue(@Param("min") BigDecimal min);
    
    // Projection (DTO)
    interface OrderSummary {
        UUID getId();
        BigDecimal getTotal();
    }
    List<OrderSummary> findByTenantIdAndStatus(String tenantId, OrderStatus status);
    
    // Modifying
    @Modifying
    @Query("UPDATE Order o SET o.status = 'CANCELLED' WHERE o.id = :id")
    int cancel(@Param("id") UUID id);
    
    // EntityGraph
    @EntityGraph(attributePaths = {"items", "user"})
    Optional<Order> findById(UUID id);
}
```

---

## SQL / Postgres Cheats

### EXPLAIN ANALYZE Reading

```
Seq Scan        # bad on large tables
Index Scan      # good
Index Only Scan # best
Bitmap Scan     # multi-condition
Nested Loop     # OK on small inputs
Hash Join       # large inputs, no order
Merge Join      # both sides sorted
```

### Useful psql

```bash
\l                       # list databases
\dt                      # list tables
\d table_name            # describe
\di                      # indexes
\df                      # functions
\sf func_name            # show function source
\timing on               # show query time
\x                       # expanded output
```

### Useful System Queries

```sql
-- Currently running queries
SELECT pid, state, now() - query_start AS dur, query
FROM pg_stat_activity
WHERE state = 'active'
ORDER BY dur DESC;

-- Slowest queries (needs pg_stat_statements)
SELECT query, calls, mean_exec_time, total_exec_time
FROM pg_stat_statements
ORDER BY total_exec_time DESC
LIMIT 20;

-- Table sizes
SELECT relname, pg_size_pretty(pg_total_relation_size(oid)) AS size
FROM pg_class
WHERE relkind = 'r'
ORDER BY pg_total_relation_size(oid) DESC
LIMIT 20;

-- Unused indexes
SELECT schemaname, relname, indexrelname, idx_scan
FROM pg_stat_user_indexes
WHERE idx_scan = 0;

-- Cache hit ratio (should be > 99%)
SELECT sum(heap_blks_hit)::float / sum(heap_blks_hit + heap_blks_read) AS ratio
FROM pg_statio_user_tables;
```

### Index Patterns

```sql
-- B-tree (default)
CREATE INDEX idx_users_email ON users(email);

-- Composite (most selective first when balanced)
CREATE INDEX idx_orders_tenant_status ON orders(tenant_id, status);

-- Partial
CREATE INDEX idx_pending ON orders(created_at) WHERE status = 'PENDING';

-- Expression (functional)
CREATE INDEX idx_users_email_lower ON users(LOWER(email));

-- GIN for JSONB
CREATE INDEX idx_meta ON orders USING GIN (metadata);

-- Unique
CREATE UNIQUE INDEX idx_users_email_unique ON users(LOWER(email));
```

---

## Docker & Container Cheats

### Daily docker Commands

```bash
docker ps                          # running containers
docker ps -a                       # all containers
docker images                      # local images
docker logs -f <id>                # tail logs
docker exec -it <id> sh            # shell into
docker inspect <id>                # full info
docker stats                       # live resource usage
docker system prune -a             # clean up

docker build -t myapp:1.0 .
docker run -p 8080:8080 myapp:1.0
docker push myrepo/myapp:1.0
```

### docker compose

```bash
docker compose up -d               # start
docker compose down                # stop + remove
docker compose logs -f service     # tail
docker compose exec service sh     # shell
docker compose ps                  # status
```

### Spring Boot Multi-Stage Dockerfile

```dockerfile
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /workspace
COPY . .
RUN --mount=type=cache,target=/root/.gradle ./gradlew bootJar

FROM eclipse-temurin:21-jre-jammy
RUN useradd -u 10001 -ms /bin/false app
USER app
WORKDIR /app
COPY --from=build /workspace/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "/app/app.jar"]
```

---

## Kubernetes Cheats

### kubectl Basics

```bash
kubectl get pods                   # list pods (current ns)
kubectl get pods -A                # all namespaces
kubectl get pods -n my-ns -o wide
kubectl describe pod <name>
kubectl logs -f <pod>              # tail logs
kubectl logs -f <pod> -c <container>
kubectl exec -it <pod> -- sh
kubectl port-forward <pod> 8080:8080
kubectl apply -f manifest.yaml
kubectl delete -f manifest.yaml
kubectl rollout status deployment/<name>
kubectl rollout undo deployment/<name>
kubectl scale deployment/<name> --replicas=5
kubectl get events --sort-by='.lastTimestamp' | tail -20
kubectl top pods                   # resource usage (metrics-server)
kubectl top nodes
```

### Spring Boot Deployment Template

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: orderhub
spec:
  replicas: 3
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxSurge: 1
      maxUnavailable: 0
  selector:
    matchLabels: { app: orderhub }
  template:
    metadata:
      labels: { app: orderhub }
    spec:
      terminationGracePeriodSeconds: 60
      containers:
      - name: app
        image: myrepo/orderhub:1.2.3
        ports:
        - containerPort: 8080
          name: web
        resources:
          requests: { cpu: 250m, memory: 512Mi }
          limits:   { cpu: 1000m, memory: 1Gi }
        startupProbe:
          httpGet: { path: /actuator/health/readiness, port: 8080 }
          initialDelaySeconds: 10
          periodSeconds: 5
          failureThreshold: 30
        livenessProbe:
          httpGet: { path: /actuator/health/liveness, port: 8080 }
          periodSeconds: 10
          failureThreshold: 3
        readinessProbe:
          httpGet: { path: /actuator/health/readiness, port: 8080 }
          periodSeconds: 5
          failureThreshold: 3
        lifecycle:
          preStop:
            exec: { command: ["sh", "-c", "sleep 10"] }
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: production
        - name: DB_PASSWORD
          valueFrom: { secretKeyRef: { name: db-creds, key: password } }
```

### k9s Shortcuts

```
:pod                # navigate to pods
:svc                # services
:deploy             # deployments
0 / 1 / 2…          # namespace shortcut
/<search>           # filter
l                   # logs
s                   # shell
d                   # describe
y                   # YAML
ctrl+d              # delete
ctrl+r              # refresh
?                   # help
```

---

## HTTP Quick Reference

### Status Codes

| Range | Meaning | Common codes |
|-------|---------|--------------|
| 2xx | Success | 200, 201, 202, 204 |
| 3xx | Redirect | 301, 302, 304 |
| 4xx | Client error | 400, 401, 403, 404, 409, 422, 429 |
| 5xx | Server error | 500, 502, 503, 504 |

### When to Return What

| Scenario | Status |
|----------|--------|
| GET success | 200 |
| POST creates resource | 201 + `Location` header |
| Async accepted, processing | 202 |
| DELETE success, no body | 204 |
| Invalid input | 400 |
| Missing/invalid auth | 401 |
| Auth ok but forbidden | 403 |
| Not found | 404 |
| Method not allowed | 405 |
| Conflict (optimistic lock) | 409 |
| Validation error | 422 |
| Rate limited | 429 + `Retry-After` |
| Server bug | 500 |
| Upstream gateway issue | 502 |
| Service unavailable / overloaded | 503 + `Retry-After` |
| Upstream timeout | 504 |

### Useful HTTP Headers

```
Authorization: Bearer <jwt>
Content-Type: application/json
Accept: application/json
Cache-Control: no-cache, no-store
Cache-Control: public, max-age=3600
ETag: "abc123"
If-None-Match: "abc123"
Idempotency-Key: <uuid>
X-Request-Id: <uuid>
Retry-After: 30
```

---

## JVM / Performance Cheats

### Useful JVM Flags

```bash
# Memory
-Xms2g -Xmx2g                      # fixed heap (avoid resize)
-XX:MaxRAMPercentage=75            # for containers

# GC
-XX:+UseG1GC                       # default 9+
-XX:+UseZGC                        # low-latency, JDK 15+
-XX:+UnlockExperimentalVMOptions

# GC logging
-Xlog:gc*:file=gc.log:time,uptime:filecount=5,filesize=10M

# Diagnostic
-XX:+HeapDumpOnOutOfMemoryError
-XX:HeapDumpPath=/var/log/heap.hprof
-XX:+ExitOnOutOfMemoryError

# JFR
-XX:StartFlightRecording=duration=60s,filename=recording.jfr

# Container-aware (11+, on by default)
-XX:+UseContainerSupport
```

### jcmd Commands

```bash
jcmd <pid> VM.version
jcmd <pid> GC.heap_info
jcmd <pid> GC.run                  # force GC
jcmd <pid> GC.class_histogram
jcmd <pid> Thread.print            # thread dump
jcmd <pid> GC.heap_dump /tmp/heap.hprof
jcmd <pid> JFR.start duration=60s filename=rec.jfr
jcmd <pid> VM.system_properties
jcmd <pid> VM.command_line
```

### async-profiler

```bash
# CPU
./profiler.sh -e cpu -d 30 -f cpu.html <pid>

# Memory allocation
./profiler.sh -e alloc -d 30 -f alloc.html <pid>

# Lock contention
./profiler.sh -e lock -d 30 -f lock.html <pid>

# Wall-clock (sees blocked threads)
./profiler.sh -e wall -d 30 -f wall.html <pid>
```

---

## Kafka Cheats

### kcat (formerly kafkacat)

```bash
# Produce
echo "hello" | kcat -P -b localhost:9092 -t my-topic

# Consume
kcat -C -b localhost:9092 -t my-topic -o end -e

# Consume with key
kcat -C -b localhost:9092 -t my-topic -K: -f 'KEY=%k VAL=%s\n'

# List topics
kcat -L -b localhost:9092

# Specific partition + offset
kcat -C -b localhost:9092 -t my-topic -p 0 -o 100 -c 10
```

### Consumer Group Lag

```bash
kafka-consumer-groups --bootstrap-server localhost:9092 \
    --describe --group my-group
```

### Spring Kafka

```java
@KafkaListener(topics = "orders", groupId = "order-processor")
public void handle(@Payload Order order,
                   @Header(KafkaHeaders.RECEIVED_KEY) String key) { ... }

@Bean
public ProducerFactory<String, Order> producerFactory() {
    return new DefaultKafkaProducerFactory<>(Map.of(
        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "...",
        ProducerConfig.ACKS_CONFIG, "all",
        ProducerConfig.RETRIES_CONFIG, 3,
        ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true
    ));
}
```

---

## Redis Cheats

### redis-cli Quick Hits

```bash
# Connect
redis-cli -h host -p 6379 -a password

# Basic ops
SET key value EX 60                # set with TTL
GET key
DEL key
EXISTS key
TTL key

# Hash
HSET user:1 name alice age 30
HGET user:1 name
HGETALL user:1

# List
LPUSH queue item
RPOP queue
LRANGE queue 0 -1

# Set
SADD tags java spring
SMEMBERS tags

# Sorted set
ZADD scores 100 alice 95 bob
ZRANGEBYSCORE scores 90 100

# Counter
INCR counter
INCRBY counter 5

# Info
INFO memory
DBSIZE
KEYS *                             # NEVER in prod (blocks)
SCAN 0 MATCH user:*                # use SCAN instead
```

### Spring Data Redis

```java
@Autowired RedisTemplate<String, Object> redis;

redis.opsForValue().set("user:1", user, Duration.ofMinutes(10));
User u = (User) redis.opsForValue().get("user:1");

redis.opsForHash().put("user:1", "name", "alice");
redis.opsForList().leftPush("queue", "task1");
```

---

## Observability Cheats

### PromQL

```promql
# Request rate
rate(http_server_requests_seconds_count[5m])

# Error rate
sum(rate(http_server_requests_seconds_count{status=~"5.."}[5m])) /
sum(rate(http_server_requests_seconds_count[5m]))

# p99 latency
histogram_quantile(0.99, sum by (le, uri) (rate(http_server_requests_seconds_bucket[5m])))

# JVM heap %
jvm_memory_used_bytes{area="heap"} / jvm_memory_max_bytes{area="heap"}

# Top endpoints
topk(10, sum by (uri) (rate(http_server_requests_seconds_count[5m])))
```

### Burn-Rate Alert

```yaml
- alert: ErrorBudgetBurn
  expr: |
    sum(rate(http_server_requests_seconds_count{status=~"5.."}[5m])) /
    sum(rate(http_server_requests_seconds_count[5m])) > 0.0144
  for: 5m
```

### Logback JSON Encoder

```xml
<configuration>
  <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
    <encoder class="net.logstash.logback.encoder.LogstashEncoder">
      <includeMdc>true</includeMdc>
      <pattern>
        {
          "service": "orderhub",
          "trace_id": "%X{traceId:-}",
          "span_id": "%X{spanId:-}"
        }
      </pattern>
    </encoder>
  </appender>
  <root level="INFO"><appender-ref ref="STDOUT"/></root>
</configuration>
```

---

## Security Cheats

### Spring Security Resource Server

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    @Bean
    SecurityFilterChain api(HttpSecurity http) throws Exception {
        return http
            .csrf(c -> c.disable())
            .cors(Customizer.withDefaults())
            .sessionManagement(s -> s.sessionCreationPolicy(STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/health/**", "/v3/api-docs/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated())
            .oauth2ResourceServer(o -> o.jwt(Customizer.withDefaults()))
            .build();
    }
}
```

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: https://auth.example.com
          audiences: my-api
```

---

## Test Cheats

### JUnit + Mockito

```java
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    @Mock OrderRepository repo;
    @Mock PaymentClient pay;
    @InjectMocks OrderService svc;
    
    @Test
    void placeOrder() {
        given(repo.save(any())).willReturn(buildOrder());
        given(pay.charge(any())).willReturn(success());
        
        Order o = svc.placeOrder(req);
        
        assertThat(o.getStatus()).isEqualTo(OrderStatus.PAID);
        verify(repo).save(any(Order.class));
    }
}
```

### Spring Boot Test Slices

```java
@WebMvcTest(OrderController.class)
class OrderControllerTest {
    @Autowired MockMvc mvc;
    @MockBean OrderService svc;
    
    @Test
    void getOrder() throws Exception {
        given(svc.findById("1")).willReturn(order);
        
        mvc.perform(get("/api/orders/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value("1"));
    }
}
```

### Testcontainers

```java
@SpringBootTest
@Testcontainers
class OrderIntegrationTest {
    @Container @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");
    
    @Test
    void placesOrder() { ... }
}
```

---

## Gradle Cheats

```bash
./gradlew build                    # full build
./gradlew test                     # tests
./gradlew bootRun                  # run app
./gradlew bootJar                  # build executable jar
./gradlew bootBuildImage           # buildpack image
./gradlew dependencies             # tree
./gradlew :module:test             # test one module
./gradlew --refresh-dependencies   # re-resolve
./gradlew clean
./gradlew test -Pgroups=fast       # tag filter
```

```kotlin
// build.gradle.kts
plugins {
    java
    id("org.springframework.boot") version "3.3.0"
    id("io.spring.dependency-management") version "1.1.4"
}

java { sourceCompatibility = JavaVersion.VERSION_21 }

repositories { mavenCentral() }

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<Test> { useJUnitPlatform() }
```

---

## Maven Cheats

```bash
./mvnw clean package -DskipTests
./mvnw verify
./mvnw test -Dtest=MyTest
./mvnw dependency:tree
./mvnw versions:display-dependency-updates
./mvnw spring-boot:run
./mvnw spring-boot:build-image
```

---

## Recap

The chapter is dense by design. Bookmark it; return often. The interview round and the 3 AM incident are the moments these cheats earn their keep.

The next chapter is [C17 Resources](../C17-resources/README.md) — curated books, talks, and links for going deeper on every L4 area.
