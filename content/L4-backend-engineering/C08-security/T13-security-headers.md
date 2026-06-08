---
title: "Security headers"
slug: security-headers
level: L4
module: "Backend Engineering"
section: "Security"
type: concept
difficulty: senior
order: 13
tags: [security-headers, content-security-policy, csp, strict-transport-security, hsts, x-frame-options, x-content-type-options, referrer-policy, permissions-policy, cross-origin-opener-policy, coop, cross-origin-embedder-policy, coep, cross-origin-resource-policy, corp, spring-security-headers, browser-security]
prerequisites: [xss-and-csrf, tls-in-practice]
status: complete
estimated_minutes: 35
last_updated: 2026-06-08
---

# Security headers

The browser enforces many security rules — but only if the **server tells it to** via response headers. **Security headers** are the simple, free, high-impact addition to any web service. CSP defeats most XSS; HSTS prevents downgrade attacks; X-Frame-Options stops clickjacking; Referrer-Policy controls privacy leaks; Permissions-Policy restricts browser features. Setting them is a one-time configuration; not setting them is a vulnerability.

Spring Security ships sensible defaults; this topic ensures you understand what each header does and where the defaults need tuning.

> [!NOTE]
> Prerequisites: [XSS & CSRF (T08)](./T08-xss-and-csrf.md), [TLS (T11)](./T11-tls-in-practice.md), Spring Security basics.

## The Header Catalog

| Header | Purpose | Spring default |
|--------|---------|:--------------:|
| `Content-Security-Policy` | restrict resource loading; defeats XSS | manual |
| `Strict-Transport-Security` | force HTTPS | yes (with TLS) |
| `X-Content-Type-Options: nosniff` | block MIME sniffing | yes |
| `X-Frame-Options: DENY` | prevent clickjacking | yes |
| `Referrer-Policy` | control referer info | yes (no-referrer or strict-origin) |
| `Permissions-Policy` | restrict APIs (camera, mic, etc.) | manual |
| `Cross-Origin-Opener-Policy` | isolate browsing contexts | manual |
| `Cross-Origin-Embedder-Policy` | restrict cross-origin embeds | manual |
| `Cross-Origin-Resource-Policy` | who can embed this | manual |
| `Cache-Control: no-store` | prevent caching sensitive | yes (auth pages) |
| `X-XSS-Protection` | deprecated; explicitly disable | set to 0 |

## Spring Security Defaults

```java
http.headers(h -> h
    .contentTypeOptions(Customizer.withDefaults())
    .frameOptions(FrameOptionsConfig::deny)
    .httpStrictTransportSecurity(hsts -> hsts.maxAgeInSeconds(31536000).includeSubDomains(true))
    .referrerPolicy(r -> r.policy(STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
    .xssProtection(x -> x.disable())
    .cacheControl(Customizer.withDefaults())
);
```

These are mostly set automatically; the customizations above explicitly extend.

## CSP (Recap From T08)

```http
Content-Security-Policy:
    default-src 'self';
    script-src 'self' 'nonce-abc123';
    style-src 'self';
    img-src 'self' data: https://cdn.example.com;
    font-src 'self';
    connect-src 'self' https://api.example.com;
    frame-ancestors 'none';
    base-uri 'self';
    form-action 'self';
    object-src 'none';
    upgrade-insecure-requests
```

Common directives:

- `default-src` — fallback.
- `script-src` — JS sources.
- `style-src` — CSS.
- `img-src` — images.
- `connect-src` — XHR/fetch/WebSocket targets.
- `frame-ancestors 'none'` — replaces X-Frame-Options.
- `base-uri` — restricts `<base href="...">`.
- `form-action` — where forms can submit.
- `object-src 'none'` — block Flash etc.
- `upgrade-insecure-requests` — upgrade HTTP→HTTPS automatically.

Use **nonces** or **hashes** for inline scripts/styles; avoid `unsafe-inline`. `strict-dynamic` allows nonce-tagged scripts to load further scripts.

### Report-Only Mode

For testing without breaking:

```http
Content-Security-Policy-Report-Only: ...
Content-Security-Policy-Report-To: csp-endpoint
```

Browser reports violations; doesn't block. Iterate until clean.

## HSTS

```http
Strict-Transport-Security: max-age=31536000; includeSubDomains; preload
```

After first connection over HTTPS, browser remembers — subsequent attempts on HTTP auto-upgraded. `preload` submits to browser-hardcoded list (https://hstspreload.org/).

**Be sure before preload** — once on the list, removal takes months.

## X-Frame-Options vs frame-ancestors

```http
X-Frame-Options: DENY
```

Prevents page being iframed. Defeats clickjacking. CSP's `frame-ancestors 'none'` replaces it; both work.

## X-Content-Type-Options

```http
X-Content-Type-Options: nosniff
```

Forces browser to use Content-Type as declared; doesn't guess. Prevents type-confusion attacks (e.g., uploaded "image" interpreted as script).

## Referrer-Policy

Controls what's in the `Referer` header on outbound clicks:

- `no-referrer` — never send.
- `strict-origin` — send only origin (no path) for cross-origin.
- `strict-origin-when-cross-origin` — full referer same-origin; only origin cross-origin. Modern default.

```http
Referrer-Policy: strict-origin-when-cross-origin
```

## Permissions-Policy

Restricts browser APIs:

```http
Permissions-Policy: camera=(), microphone=(), geolocation=(self), payment=()
```

- `camera=()` — block.
- `geolocation=(self)` — allow same-origin only.

Reduces attack surface; signals intent.

## COOP / COEP / CORP

For high-security (SharedArrayBuffer, precise timing):

```http
Cross-Origin-Opener-Policy: same-origin
Cross-Origin-Embedder-Policy: require-corp
Cross-Origin-Resource-Policy: same-origin
```

Isolates browsing contexts; prevents Spectre-class side-channel attacks. Required for SharedArrayBuffer.

## Cache-Control For Sensitive

```http
Cache-Control: no-store
```

Prevents browser, CDN, intermediate caches from storing sensitive responses. For account pages, post-login content.

Spring Security defaults to no-store for protected paths.

## X-XSS-Protection — Disable

```http
X-XSS-Protection: 0
```

Old IE feature; caused issues. Explicitly disable. Modern browsers ignore.

## Tools

- **securityheaders.com** — grade your site.
- **observatory.mozilla.org** — Mozilla's free scanner.
- **Lighthouse** (Chrome DevTools) — security audit.

Run after deploy; aim for A+.

## Spring Implementation

```java
@Bean
public SecurityFilterChain filter(HttpSecurity http) throws Exception {
    return http
        .headers(h -> h
            .contentSecurityPolicy(csp -> csp.policyDirectives(
                "default-src 'self'; script-src 'self' 'nonce-{nonce}'; ..."))
            .httpStrictTransportSecurity(hsts -> hsts
                .maxAgeInSeconds(31536000)
                .includeSubDomains(true)
                .preload(true))
            .referrerPolicy(r -> r.policy(STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
            .permissionsPolicy(p -> p.policy("camera=(), microphone=(), geolocation=(self)"))
            .frameOptions(FrameOptionsConfig::deny)
        )
        // ... other config
        .build();
}
```

## Common Pitfalls

> [!WARNING]
> **No CSP.** XSS one click away.

> [!WARNING]
> **`unsafe-inline` in CSP.** Defeats most CSP value.

> [!WARNING]
> **HSTS preload without commitment.** Hard to undo.

> [!WARNING]
> **No HSTS.** Downgrade attack risk.

> [!WARNING]
> **X-Frame-Options ALLOW-ALL.** Clickjacking enabled.

> [!WARNING]
> **No Referrer-Policy.** Leaks paths to third parties.

> [!WARNING]
> **No Permissions-Policy.** Unlocked APIs.

> [!WARNING]
> **Disabling Spring defaults wholesale.** Loses protections.

> [!WARNING]
> **Cache-Control: public on sensitive.** CDN serves to other users.

## Practice

1. Run securityheaders.com against your service; note grade.
2. Add CSP with report-only; iterate based on reports; switch to enforced.
3. Enable HSTS without preload first; verify; consider preload.
4. Add Permissions-Policy disabling unused APIs.
5. Test X-Frame-Options: try iframe your site; verify blocked.
6. Add COOP/COEP to enable SharedArrayBuffer; verify.
7. Audit your service: re-run securityheaders.com; aim for A+.
8. Document headers + rationale in security policy.

## Recap

You should now be able to:

- Set the full security-header suite: CSP, HSTS, X-Content-Type-Options, X-Frame-Options, Referrer-Policy, Permissions-Policy, COOP/COEP/CORP, Cache-Control.
- Iterate CSP via report-only → enforced.
- Apply HSTS with preload deliberately.
- Configure Spring Security's `headers(...)` builder.
- Grade via securityheaders.com; aim for A+.
- Avoid the canonical pitfalls: no CSP, unsafe-inline, preload without commitment, no HSTS, leaving defaults disabled.

## Next

Continue to [API security best practices](./T14-api-security-best-practices.md) for the consolidated checklist of API-layer security — combining everything above into per-endpoint discipline.
