---
title: "API security best practices"
slug: api-security-best-practices
level: L4
module: "Backend Engineering"
section: "Security"
type: concept
difficulty: senior
order: 14
tags: [api-security, owasp-api-top-10, broken-object-level-authorization, bola, broken-object-property-level-authorization, bopla, broken-authentication, broken-function-level-authorization, unrestricted-resource-consumption, security-misconfiguration-api, secure-api-defaults, input-validation, output-encoding, api-versioning-security, deprecation, monitoring-api, api-checklist]
prerequisites: [owasp-top-10, idempotency-in-apis, rate-limiting-and-throttling]
status: complete
estimated_minutes: 35
last_updated: 2026-06-08
---

# API security best practices

OWASP maintains a separate **API Security Top 10** (2023 latest) covering risks specific to APIs (as opposed to general web apps). The risks differ: APIs often don't have UI rendering (so no traditional XSS); they have fine-grained authorization (BOLA / BOPLA dominate); they often have machine clients with different attack patterns. This topic consolidates the API-level security discipline — what to add to every endpoint, what defaults to set, what to monitor.

This is a checklist topic: ten concrete rules + the matching Spring patterns. Use it for code review, security review, design review.

> [!NOTE]
> Prerequisites: T01-T13 of C08, [Idempotency (L4/C05/T03)](../C05-apis-advanced/T03-idempotency-in-apis.md), [Rate limiting (L4/C05/T10)](../C05-apis-advanced/T10-rate-limiting-and-throttling.md).

## OWASP API Top 10 (2023)

| # | Risk | Mitigation |
|---|------|-----------|
| 1 | Broken Object Level Authorization (BOLA) | per-resource owner check |
| 2 | Broken Authentication | OAuth2/OIDC, MFA, no weak passwords |
| 3 | Broken Object Property Level Authorization (BOPLA) | field-level access control |
| 4 | Unrestricted Resource Consumption | rate limit, pagination, body size |
| 5 | Broken Function Level Authorization | role checks per endpoint |
| 6 | Unrestricted Access to Sensitive Business Flows | bot detection, rate limit per flow |
| 7 | Server-Side Request Forgery | URL allowlist, block internal IPs |
| 8 | Security Misconfiguration | secure defaults, headers, TLS |
| 9 | Improper Inventory Management | API versioning, retired endpoints removed |
| 10 | Unsafe Consumption of APIs | validate downstream responses; don't blindly forward |

## The Per-Endpoint Checklist

Each endpoint should answer:

1. **Authenticate**: who's calling? Spring Security filter.
2. **Authorize URL**: are they allowed to hit this URL? `authorizeHttpRequests`.
3. **Authorize resource**: are they allowed to access *this specific* resource? `@PreAuthorize`.
4. **Validate input**: `@Valid` + business rules.
5. **Output projection**: only fields the user should see.
6. **Rate limit**: per-key.
7. **Idempotency**: writes idempotent or keyed.
8. **Audit log**: write to audit log.
9. **Error handling**: no leaks (stack traces, internal IDs).
10. **Telemetry**: structured logs + metrics.

## BOLA (#1 Risk) Example

```java
// VULNERABLE: any authenticated user can fetch any order
@GetMapping("/api/orders/{id}")
public Order get(@PathVariable long id) {
    return orderRepo.findById(id).orElseThrow();
}

// FIXED: verify ownership
@PreAuthorize("@orderPermissions.canView(authentication, #id)")
@GetMapping("/api/orders/{id}")
public Order get(@PathVariable long id) {
    return orderRepo.findById(id).orElseThrow();
}
```

Or check inside:

```java
public Order get(long id, String currentUser) {
    Order o = orderRepo.findById(id).orElseThrow();
    if (!o.getCustomerId().equals(currentUser)) throw new AccessDeniedException();
    return o;
}
```

BOLA is the **single most common API vulnerability** — easy to miss; trivial to exploit.

## BOPLA (Field-Level)

```java
// VULNERABLE: API returns all fields, including SSN, password hash
public UserResponse get(long id) {
    return userRepo.findById(id).map(UserResponse::of).orElseThrow();
    // UserResponse exposes too many fields
}

// FIXED: project to safe fields
public record UserResponse(Long id, String name, String email) { /* no SSN */ }
```

Or per-role projections:

```java
public Object get(long id, Authentication auth) {
    User u = userRepo.findById(id).orElseThrow();
    if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
        return UserAdminResponse.of(u);  // includes audit fields
    }
    return UserPublicResponse.of(u);     // minimal fields
}
```

## Mass Assignment (BOPLA write side)

```java
// VULNERABLE
@PutMapping("/api/users/{id}")
public User update(@PathVariable long id, @RequestBody User updated) {
    updated.setId(id);
    return userRepo.save(updated);   // attacker sets role=ADMIN, balance=10000
}

// FIXED: DTO with only writable fields
@PutMapping("/api/users/{id}")
public User update(@PathVariable long id, @RequestBody UpdateUserRequest req) {
    User u = userRepo.findById(id).orElseThrow();
    u.setName(req.name());
    u.setEmail(req.email());
    return userRepo.save(u);
}

public record UpdateUserRequest(String name, String email) { }   // no role, no balance
```

## Unrestricted Resource Consumption

- **Rate limit** every endpoint (T10 of C05).
- **Pagination**: `Pageable` with bounded max size.
- **Body size limit**: `spring.servlet.multipart.max-file-size`, `spring.servlet.multipart.max-request-size`.
- **Timeout** every external call.
- **CPU / memory limits** at container level.

```java
public Page<Order> list(@RequestParam @Min(0) int page, @RequestParam @Min(1) @Max(100) int size) { ... }
```

## SSRF Defense

```java
private static final Set<String> ALLOWED_HOSTS = Set.of("api.partner.com");

public String fetch(String url) {
    URI parsed = URI.create(url);
    String host = parsed.getHost();
    if (!ALLOWED_HOSTS.contains(host)) throw new IllegalArgumentException();
    InetAddress addr = InetAddress.getByName(host);
    if (addr.isLoopbackAddress() || addr.isLinkLocalAddress() || addr.isSiteLocalAddress()) {
        throw new IllegalArgumentException();
    }
    return restClient.get().uri(parsed).retrieve().body(String.class);
}
```

Or use the Java `HttpClient.Builder().proxy(...)` with an outbound proxy that has its own allowlist.

## Inventory Management

- Keep an **API catalog** (OpenAPI specs).
- **Deprecate** endpoints with sunset date.
- **Remove** retired endpoints after sunset.
- **Version**: `/v1/`, `/v2/`; sunset v1.

## Error Handling — No Leaks

```java
// LEAK
@ExceptionHandler(Exception.class)
public ResponseEntity<String> handle(Exception e) {
    return ResponseEntity.status(500).body(e.getStackTrace().toString());   // ❌
}

// SAFE
@ExceptionHandler(Exception.class)
public ResponseEntity<ProblemDetail> handle(Exception e, HttpServletRequest req) {
    log.error("error at {}", req.getRequestURI(), e);   // logged server-side
    ProblemDetail pd = ProblemDetail.forStatus(INTERNAL_SERVER_ERROR);
    pd.setTitle("Internal error");
    return ResponseEntity.status(500).body(pd);
}
```

No stack traces; no internal IDs; no SQL fragments; no class names.

## Logging Discipline

Log:

- Trace ID, user ID, action, outcome.

Don't log:

- Passwords, tokens, full credit cards, SSN.
- Full request bodies on success (audit only key fields).

Redaction filters at the logger level. Verified in dev.

## Telemetry For Security

- **Failed auth count** per IP / per user.
- **403 / 401 rate**.
- **DLQ depth** (T10 of C07).
- **Unusual request shape** (massive payloads, weird paths).

Alert on anomalies.

## Common Pitfalls

> [!WARNING]
> **No object-level auth.** BOLA — #1 API risk.

> [!WARNING]
> **DTO with too many fields.** BOPLA.

> [!WARNING]
> **Mass assignment.** Attacker sets fields they shouldn't.

> [!WARNING]
> **No rate limit.** Abuse / DoS.

> [!WARNING]
> **Unbounded pagination.** Memory exhaust.

> [!WARNING]
> **No timeout on outbound calls.** Resource exhaust.

> [!WARNING]
> **Stack traces in 500 responses.** Information leak.

> [!WARNING]
> **No API versioning.** Can't deprecate cleanly.

> [!WARNING]
> **No telemetry on auth failures.** Brute force invisible.

> [!WARNING]
> **Forwarding downstream errors verbatim.** May contain sensitive info.

## Practice

1. Audit every endpoint for BOLA. Add per-resource checks.
2. Review DTOs for BOPLA: are any sensitive fields exposed?
3. Convert entity-receiving controllers to DTO-based.
4. Add `@Min` / `@Max` on pagination params.
5. Implement SSRF defense on URL-accepting endpoints.
6. Test error responses: no stack traces leaked?
7. Set up failed-auth metric + alert.
8. Catalog APIs; deprecate any orphaned.

## Recap

You should now be able to:

- Apply the per-endpoint checklist: authenticate, authorize URL, authorize resource, validate, project, rate limit, idempotent, audit log, error handling, telemetry.
- Defend BOLA (#1 API risk) via per-resource permission checks.
- Defend BOPLA via narrow DTOs + role-based projections.
- Prevent mass assignment via DTOs.
- Rate limit + paginate + timeout to prevent resource exhaustion.
- Defend SSRF via host allowlist + internal IP block.
- Maintain API inventory; deprecate cleanly.
- Avoid info leaks in errors.
- Telemetry on security events.
- Avoid the canonical pitfalls: missing BOLA, exposed fields, mass assignment, unbounded pagination, leaked stack traces.

## Next

Continue to [Dependency & supply-chain security](./T15-dependency-and-supply-chain-security.md) for the discipline of trusted-software-supply-chain — SBOM, signing, dependency pinning, CVE scanning.
