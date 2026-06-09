---
title: "OAuth2 / OpenID Connect / JWT with Spring Security"
slug: oauth2-openid-connect-jwt-with-spring-security
level: L4
module: "Backend Engineering"
section: "Spring Framework & Ecosystem"
type: concept
difficulty: senior
order: 15
tags: [oauth2, oidc, openid-connect, jwt, json-web-token, jws, jwe, jwk, jwks, jwks-uri, authorization-code-flow, pkce, code-challenge, code-verifier, client-credentials, refresh-token, implicit-grant-deprecated, password-grant-deprecated, device-code-flow, resource-server, authorization-server, opaque-token, introspection-endpoint, bearer-token, access-token, id-token, refresh-token-rotation, scopes, audience, claims, kid, alg, rs256, hs256, es256, eddsa, public-key, jwt-decoder, nimbus-jwt-decoder, signature-validation, issuer-validation, audience-validation, expiration-validation, jwk-source, jwk-cache, key-rotation, oauth2-client, registered-clients, oauth2-login, spring-authorization-server, authorization-code-grant, token-endpoint, jwt-authentication-token, oauth2-authentication-token, granted-authorities-converter, custom-jwt-converter]
prerequisites: [spring-security-authentication-and-authorization, spring-mvc-rest-controllers]
status: complete
estimated_minutes: 95
last_updated: 2026-06-08
---

# OAuth2 / OpenID Connect / JWT with Spring Security

Modern backend services almost never store passwords. Authentication is *delegated* — to Google / Microsoft / Okta / Auth0 / your own SSO IdP — using the **OAuth2** authorization protocol, with **OpenID Connect (OIDC)** layered on top for *authentication* (OAuth2 alone is "delegated access"; OIDC adds "delegated identity"). The token format that carries the identity from issuer to your service is almost always **JWT (JSON Web Token)** — a self-contained, signed, base64-encoded credential your service can validate without a network call to the issuer (cache the public key, verify the signature, read the claims, done).

If T14 was the *infrastructure* of Spring Security (the filter chain, the manager, the context), T15 is the *protocol* — the precise OAuth2 / OIDC flows, the JWT structure and validation rules, Spring's three OAuth2 modules (**Resource Server**, **Client**, **Authorization Server**), and the operational reality of token lifetime, key rotation, audience validation, scope-based authorization, and the CVE catalog of common JWT mistakes (CVE-2022-21449 ECDSA bypass, JWT `alg=none`, key confusion, replay).

The depth-bar this topic clears: at the **language layer**, OAuth2 vocabulary (grant type, scope, client, resource server, authorization server, refresh token, PKCE, audience, opaque token, introspection), JWT structure (header / payload / signature), OIDC's ID-token claims, and Spring Security 6's three OAuth2 modules with their configuration DSL. At the **memory layer**, the JWT validation pipeline — header parsing, signature verification with RSA/ECDSA/EdDSA (CPU cost: ~50–200 µs per token for RS256), JWK cache (key fetched once, cached for hours, refreshed on rotation), claims validation (issuer / audience / expiration). At the **architecture layer** — the heart — the **authorization code + PKCE flow** end-to-end (the only flow you should be using for browser/mobile clients in 2026), **client-credentials** for service-to-service, **how Spring Security 6 wires each module**, the **JWT validator pipeline** (signature + iss + aud + exp + custom claims + scope-to-authorities mapping), and the **pitfalls** that have caused real breaches.

> [!NOTE]
> Prerequisites: [Spring Security (T14)](./T14-spring-security-authentication-and-authorization.md) — the filter chain and `SecurityContext`. HTTP request/response (L2/C04/T01). Public-key cryptography basics — what a signature verifies, what a key fingerprint is.

## The Vocabulary

OAuth2 (RFC 6749, 2012) defines four parties:

- **Resource Owner** — the user.
- **Client** — the application requesting access on the user's behalf (your front-end, mobile app, or backend service).
- **Resource Server** — the API holding the user's data (your backend).
- **Authorization Server** — the system that authenticates the user and issues tokens (Google, Okta, your own).

```mermaid
flowchart LR
  U["Resource Owner (user)"]
  C["Client (app)"]
  AS["Authorization Server (issuer)"]
  RS["Resource Server (your API)"]
  U -->|"login"| AS
  C -->|"redirect for auth"| AS
  AS -->|"access token"| C
  C -->|"call API with Bearer token"| RS
  RS -->|"validate token"| RS
```

The currency exchanged:

- **Access token** — opaque to the client; presented to the resource server; short-lived (5–60 min); confers permission.
- **Refresh token** — long-lived (hours to days); used to obtain new access tokens without re-prompting the user.
- **ID token** (OIDC only) — a JWT containing the user's identity claims; *consumed by the client*, not the resource server.

A **scope** is a string the client requests (`openid`, `profile`, `email`, `read:orders`, `write:orders`) that the user consents to. The authorization server embeds the granted scopes in the token; the resource server enforces them on each request.

```mermaid
flowchart LR
  AT["Access Token (Bearer)"]
  RT["Refresh Token"]
  IT["ID Token (OIDC)"]
  AT -->|"for"| RS["API requests"]
  RT -->|"for"| Renew["renew access token"]
  IT -->|"for"| Client["client knows who logged in"]
```

## The Authorization Code Flow with PKCE

In 2026 there is exactly one OAuth2 flow you should be using for end-user authentication: **Authorization Code with PKCE** (RFC 7636). The historical alternatives:

- **Implicit grant** — deprecated (RFC 8252, 6749 errata). Returns access tokens in URL fragments → leak risk.
- **Resource Owner Password Credentials** — deprecated (RFC 8252). The client sees the user's password — defeats the point.
- **Authorization Code** (without PKCE) — needs a client secret. Fine for confidential clients (server-side) but useless for public clients (mobile, SPA) that cannot keep a secret.
- **Authorization Code with PKCE** — works for both. The current best practice.

### The Flow

```mermaid
sequenceDiagram
  participant User
  participant App as Client (SPA / mobile / backend)
  participant AS as Authorization Server
  participant RS as Resource Server
  App->>App: 1. generate code_verifier (random ~43-char string)
  App->>App: 2. code_challenge = SHA256(code_verifier) base64url
  App->>AS: 3. /authorize?response_type=code&client_id=...&code_challenge=...&code_challenge_method=S256&scope=openid profile email&redirect_uri=...&state=...
  AS->>User: 4. login + consent UI
  User-->>AS: 5. credentials + consent
  AS-->>App: 6. redirect to redirect_uri?code=AUTH_CODE&state=...
  App->>AS: 7. POST /token: code=AUTH_CODE&code_verifier=...&grant_type=authorization_code&client_id=...&redirect_uri=...
  AS->>AS: 8. verify SHA256(code_verifier) == stored code_challenge
  AS-->>App: 9. {access_token, refresh_token, id_token, expires_in}
  App->>RS: 10. GET /api/... Authorization: Bearer ACCESS_TOKEN
  RS->>RS: 11. validate token (signature, iss, aud, exp, scope)
  RS-->>App: 12. response
```

Why PKCE? The `code` parameter in step 6 travels through the browser's URL bar / system intent. An attacker who intercepts it (a malicious app on the device, browser history) cannot exchange it for a token without the matching `code_verifier`, which never left the client app. PKCE binds the code to the originating client without requiring a secret.

The `state` parameter in step 3/6 prevents CSRF on the redirect.

## JWT — Structure and Validation

A JWT is **three base64url segments joined by dots**:

```
eyJhbGciOiJSUzI1NiIsImtpZCI6ImtpZC0xMjMifQ.eyJzdWIiOiJ1c2VyXzQyIiwiaXNzIjoiaHR0cHM6Ly9pc3N1ZXIuZXhhbXBsZS5jb20iLCJhdWQiOiJvcmRlcnMtc2VydmljZSIsImlhdCI6MTcxNzc3MDAwMCwiZXhwIjoxNzE3NzczNjAwLCJzY29wZSI6InJlYWQ6b3JkZXJzIHdyaXRlOm9yZGVycyJ9.SIGNATURE_BYTES
```

Decoded:

**Header** (algorithm + key id):

```json
{ "alg": "RS256", "kid": "kid-123" }
```

**Payload** (claims):

```json
{
  "sub": "user_42",
  "iss": "https://issuer.example.com",
  "aud": "orders-service",
  "iat": 1717770000,
  "exp": 1717773600,
  "scope": "read:orders write:orders"
}
```

**Signature** — the cryptographic signature of `base64url(header) + "." + base64url(payload)` using the algorithm and the issuer's private key.

```mermaid
flowchart LR
  H["header: {alg, kid}"]
  P["payload: {sub, iss, aud, iat, exp, scope, ...}"]
  S["signature: sign(header.payload, private-key)"]
  JWT["JWT = base64url(H) . base64url(P) . base64url(S)"]
  H --> JWT
  P --> JWT
  S --> JWT
```

### Standard Claims (RFC 7519)

| Claim | Meaning |
|-------|---------|
| `iss` | Issuer — the authorization server's URL |
| `sub` | Subject — the user (or client) id |
| `aud` | Audience — the resource server(s) the token is for |
| `exp` | Expiration time (Unix seconds) |
| `nbf` | Not before — earliest valid time |
| `iat` | Issued at |
| `jti` | JWT ID — unique per token, useful for revocation |

OIDC adds: `nonce` (prevents replay), `auth_time` (when the user authenticated), `acr` (auth context class), and user-info claims (`email`, `name`, `picture`, …).

### Validating a JWT

The minimum checks a resource server must perform:

1. **Signature** — verify with the issuer's public key.
2. **`iss`** — exactly matches the expected issuer URL.
3. **`aud`** — includes your service's identifier.
4. **`exp`** — not expired (with a small clock-skew tolerance, ~60 s).
5. **`nbf`** — if present, time is past it.
6. **`alg`** — matches the algorithm you accept. **Never accept `alg=none`.** **Never accept a symmetric algorithm (`HS256`) if you expect asymmetric (`RS256`).**
7. **Optional**: `jti` against a revocation list; custom claims like `scope`, `tenant`.

The `kid` (key id) in the header tells you *which* of the issuer's keys to use. Issuers expose their public keys at a **JWKS endpoint** (`https://issuer.example.com/.well-known/jwks.json`), an JSON array of JWKs (JSON Web Keys). Spring's `NimbusJwtDecoder` fetches and caches the JWKS, picks the key matching the `kid`, and verifies.

```mermaid
flowchart TB
  Tok["JWT received in Authorization: Bearer ..."]
  Hdr["parse header: {alg=RS256, kid=kid-123}"]
  Fetch["fetch JWKS from jwks_uri (cached)"]
  Pick["pick JWK where kid=kid-123"]
  Key["extract public key (RSA exponent + modulus)"]
  Verify["verify signature with public key + alg"]
  Claims["read payload claims"]
  Iss["iss == expected?"]
  Aud["aud contains us?"]
  Exp["exp > now (with skew)?"]
  Scope["map scope claim → GrantedAuthority list"]
  AuthTok["produce JwtAuthenticationToken"]
  Tok --> Hdr --> Fetch --> Pick --> Key --> Verify --> Claims --> Iss --> Aud --> Exp --> Scope --> AuthTok
```

## Spring Security as a Resource Server

The most common Spring Security OAuth2 role for backend services. Add the starter:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
</dependency>
```

Configure:

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: https://issuer.example.com
          # or: jwk-set-uri: https://issuer.example.com/.well-known/jwks.json
          # or: public-key-location: classpath:public.pem
```

`issuer-uri` is preferred — Spring fetches the OIDC discovery document at `{issuer}/.well-known/openid-configuration`, which contains the `jwks_uri`, supported algorithms, etc. Auto-configured.

The minimal `SecurityFilterChain`:

```java
@Bean
public SecurityFilterChain api(HttpSecurity http) throws Exception {
    return http
        .securityMatcher("/api/**")
        .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
        .oauth2ResourceServer(o -> o.jwt(Customizer.withDefaults()))
        .sessionManagement(s -> s.sessionCreationPolicy(STATELESS))
        .csrf(CsrfConfigurer::disable)
        .build();
}
```

With these ~10 lines, every request to `/api/**` must carry a valid JWT bearer token issued by your IdP.

### Mapping Scopes / Roles to Authorities

The default `JwtAuthenticationConverter` maps the `scope` (or `scp`) claim into `GrantedAuthority` instances with the `SCOPE_` prefix:

```
scope: "read:orders write:orders"
→ authorities: ["SCOPE_read:orders", "SCOPE_write:orders"]
```

Authorize:

```java
.authorizeHttpRequests(auth -> auth
    .requestMatchers(GET, "/api/orders/**").hasAuthority("SCOPE_read:orders")
    .requestMatchers(POST, "/api/orders/**").hasAuthority("SCOPE_write:orders")
    .anyRequest().authenticated()
)
```

For roles instead of scopes (or in addition), customize the converter:

```java
@Bean
public JwtAuthenticationConverter jwtConverter() {
    JwtGrantedAuthoritiesConverter scopes = new JwtGrantedAuthoritiesConverter();
    scopes.setAuthorityPrefix("SCOPE_");
    scopes.setAuthoritiesClaimName("scope");

    JwtAuthenticationConverter c = new JwtAuthenticationConverter();
    c.setJwtGrantedAuthoritiesConverter(jwt -> {
        Collection<GrantedAuthority> auth = new ArrayList<>(scopes.convert(jwt));
        // also include realm_access.roles (Keycloak style)
        Object realmAccess = jwt.getClaims().get("realm_access");
        if (realmAccess instanceof Map<?, ?> ra && ra.get("roles") instanceof List<?> roles) {
            roles.forEach(r -> auth.add(new SimpleGrantedAuthority("ROLE_" + r)));
        }
        return auth;
    });
    return c;
}
```

Wire to the resource-server config:

```java
.oauth2ResourceServer(o -> o.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtConverter())));
```

### Reading the JWT in a Controller

```java
@RestController
public class OrderController {

    @GetMapping("/api/orders")
    public List<Order> list(@AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        return orderService.findByUser(userId);
    }
}
```

`@AuthenticationPrincipal` resolves to the `Jwt` (the parsed token). The `JwtAuthenticationToken` wraps it as the `SecurityContext`'s `Authentication`.

### Opaque Tokens — Introspection

Some IdPs issue *opaque* tokens (random strings, not JWT). Validation requires calling the IdP's introspection endpoint:

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        opaquetoken:
          introspection-uri: https://issuer/oauth2/introspect
          client-id: my-resource-server
          client-secret: ${INTROSPECT_SECRET}
```

Spring calls `POST /oauth2/introspect` with the token; the IdP returns `{ "active": true, "sub": "...", "scope": "...", ... }`. Higher network cost than JWT (one HTTP call per request); use a cache (`CaffeineOpaqueTokenIntrospector` wrap) in production.

JWT vs opaque trade-off: JWT is faster and self-contained but cannot be revoked mid-life (need to wait for expiry, or maintain a revocation list). Opaque is per-request but revocable instantly. Choose based on your security/perf needs.

## Spring Security as an OAuth2 Client

For server-side apps that *log users in* via an external IdP (Google, Okta, etc.):

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-client</artifactId>
</dependency>
```

Configure:

```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          google:
            client-id: ${GOOGLE_CLIENT_ID}
            client-secret: ${GOOGLE_CLIENT_SECRET}
            scope: openid,profile,email
            redirect-uri: "{baseUrl}/login/oauth2/code/{registrationId}"
        provider:
          google:
            issuer-uri: https://accounts.google.com
```

`SecurityFilterChain`:

```java
@Bean
public SecurityFilterChain web(HttpSecurity http) throws Exception {
    return http
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/", "/login").permitAll()
            .anyRequest().authenticated())
        .oauth2Login(Customizer.withDefaults())
        .build();
}
```

Spring wires the entire OAuth2 dance — when an unauthenticated user hits a protected URL, they're redirected to `/oauth2/authorization/google`, which redirects to Google, comes back to `/login/oauth2/code/google`, exchanges code for tokens, stores the `OAuth2AuthenticationToken` in the session, and continues to the original URL.

### Calling Other Services with the User's Token

```java
@Bean
public RestClient restClient(OAuth2AuthorizedClientManager mgr) {
    return RestClient.builder()
        .requestInterceptor((req, body, exec) -> {
            OAuth2AuthorizeRequest auth = OAuth2AuthorizeRequest
                .withClientRegistrationId("google")
                .principal(SecurityContextHolder.getContext().getAuthentication())
                .build();
            OAuth2AuthorizedClient client = mgr.authorize(auth);
            req.getHeaders().setBearerAuth(client.getAccessToken().getTokenValue());
            return exec.execute(req, body);
        })
        .build();
}
```

Spring obtains a token, refreshes if needed, and attaches it as `Authorization: Bearer ...` to outgoing requests.

### Service-to-Service: Client Credentials

For backend-to-backend calls (no user involved):

```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          inventory-service:
            authorization-grant-type: client_credentials
            client-id: ${INV_CLIENT_ID}
            client-secret: ${INV_CLIENT_SECRET}
            scope: read:inventory
        provider:
          inventory-service:
            token-uri: https://issuer/oauth2/token
```

The client gets its own access token (no user); use it to call the other service. Spring caches the token until expiry and refreshes automatically.

## Spring Authorization Server

If you need to *be* an authorization server (issue tokens to your own clients), Spring Authorization Server (separate project, GA 2022) is the path. Self-hosts an OAuth2 + OIDC IdP.

Coverage of running your own IdP is deep enough for its own topic — typically you use a third-party (Keycloak, Auth0, Okta) instead of running your own.

## JWT Pitfalls — The CVE Catalog

> [!WARNING]
> **`alg=none` acceptance.** Some libraries default to accepting the unsigned `alg=none` JWT. An attacker can forge any token. Spring's `NimbusJwtDecoder` rejects `alg=none` by default — verify your configuration matches.

> [!WARNING]
> **`HS256` vs `RS256` key confusion.** If your verifier accepts both `HS256` and `RS256`, an attacker can take the issuer's *public* RSA key, sign a token with HMAC-SHA256 using the public-key bytes as the secret, and the verifier (using the public key as if it were the HMAC secret) accepts it. **Pin the algorithm.** Spring 6: `JwtDecoderBuilder.jwsAlgorithm(SignatureAlgorithm.RS256)`.

> [!WARNING]
> **CVE-2022-21449 — ECDSA signature bypass.** Pre-fix JDKs accepted ECDSA signatures with `r=0, s=0`. If your IdP uses ES256/ES384/ES512, **update your JDK** to a fixed version.

> [!WARNING]
> **Missing `aud` validation.** A token issued for another service is accepted by yours. Validate `aud`.

> [!WARNING]
> **Missing `iss` validation.** Spring's `issuer-uri` configuration adds it automatically. If you wire the decoder manually, add an `IssuerValidator`.

> [!WARNING]
> **Trusting `kid` blindly.** A token with `kid=../../malicious.pem` once worked against naive implementations that loaded the key from a path. Use Spring's JWKS-based key resolution, which only accepts keys present in the JWKS.

> [!WARNING]
> **Reusing access tokens across services.** Each token should be `aud`-bound to a specific resource server. A token for the orders service should not be valid for the payments service. Configure `aud` claims at the IdP.

> [!WARNING]
> **Long-lived access tokens.** A 24-hour access token gives an attacker who steals one 24 hours of access. Use short access tokens (5–15 min) + refresh tokens.

> [!WARNING]
> **Storing tokens in `localStorage`.** Browser JavaScript can read it; XSS = total compromise. Use HttpOnly cookies (with `SameSite=Lax` or `Strict`) for browser apps. For mobile, secure native storage (Keychain / Keystore).

> [!WARNING]
> **Allowing `nbf` and `exp` past clock skew.** Default tolerance is 60 s; do not extend without thought.

## OIDC ID Token — Don't Use For API Authorization

The ID token (OIDC) carries the user's identity to the *client*. It is **not** for resource-server authorization.

```json
{
  "iss": "https://accounts.google.com",
  "sub": "user_42",
  "aud": "your-client-id",   // ← your CLIENT, not your resource server
  "exp": ...,
  "iat": ...,
  "nonce": "random",
  "email": "alice@example.com",
  "email_verified": true,
  "name": "Alice Example"
}
```

Two confusion points:

- The `aud` is your *client*, not your API. A resource server validating an ID token (and accepting `aud=client-id`) is misconfigured.
- The lifetime is usually short (5–15 min); the ID token is consumed once at login. Long-lived sessions use *cookies* or refresh tokens, not the ID token.

Use the access token for API calls; use the ID token at login for who-just-logged-in. Different jobs.

## Worked Example — End-to-End

A SPA (React) talking to a Spring resource server, via Auth0.

1. **Front-end** initiates Authorization Code + PKCE with Auth0; receives `access_token` (JWT, aud=`orders-api`) and `id_token` (aud=`spa-client-id`).
2. SPA stores tokens in memory (not localStorage). Calls `GET /api/orders` with `Authorization: Bearer ACCESS_TOKEN`.
3. Spring's resource-server config — `issuer-uri: https://your-tenant.auth0.com/` — auto-fetches JWKS, decodes the JWT, validates signature + iss + aud + exp.
4. `JwtAuthenticationConverter` reads `scope: "read:orders write:orders"` claim → authorities `SCOPE_read:orders`, `SCOPE_write:orders`.
5. `authorizeHttpRequests` rule `requestMatchers(GET, "/api/orders/**").hasAuthority("SCOPE_read:orders")` passes.
6. Controller method runs.

```java
@Bean
public SecurityFilterChain api(HttpSecurity http) throws Exception {
    return http
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(GET, "/api/orders/**").hasAuthority("SCOPE_read:orders")
            .requestMatchers(POST, "/api/orders/**").hasAuthority("SCOPE_write:orders")
            .anyRequest().authenticated())
        .oauth2ResourceServer(o -> o.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtConverter())))
        .sessionManagement(s -> s.sessionCreationPolicy(STATELESS))
        .csrf(CsrfConfigurer::disable)
        .cors(Customizer.withDefaults())
        .build();
}

@Bean
public JwtAuthenticationConverter jwtConverter() {
    JwtGrantedAuthoritiesConverter scopes = new JwtGrantedAuthoritiesConverter();
    scopes.setAuthorityPrefix("SCOPE_");
    JwtAuthenticationConverter c = new JwtAuthenticationConverter();
    c.setJwtGrantedAuthoritiesConverter(scopes);
    return c;
}

@Bean
public CorsConfigurationSource corsConfig() {
    CorsConfiguration cors = new CorsConfiguration();
    cors.setAllowedOrigins(List.of("https://app.example.com"));
    cors.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    cors.setAllowedHeaders(List.of("Authorization", "Content-Type"));
    cors.setMaxAge(3600L);
    UrlBasedCorsConfigurationSource src = new UrlBasedCorsConfigurationSource();
    src.registerCorsConfiguration("/api/**", cors);
    return src;
}
```

Yaml:

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: https://your-tenant.auth0.com/
          audiences: orders-api
```

That is the whole resource-server config. Auth0 / Keycloak / Okta / your own IdP — same shape.

## Common Pitfalls

> [!WARNING]
> **Configuring `jwk-set-uri` directly when `issuer-uri` would do.** `issuer-uri` reads the OIDC discovery document and configures issuer + jwks_uri together. Using `jwk-set-uri` alone skips issuer validation.

> [!WARNING]
> **Missing `audiences` config.** Without it, Spring does not validate `aud`. Tokens for other services are accepted. Add `spring.security.oauth2.resourceserver.jwt.audiences`.

> [!WARNING]
> **Default `JwtAuthenticationConverter` prefix `SCOPE_`.** If your authorization rules use `hasRole`/`hasAuthority` with different prefixes, configure the converter to match. Inconsistency causes silent 403s.

> [!WARNING]
> **CORS not enabled for the OAuth2 endpoints.** Your SPA's `OPTIONS /api/orders` preflight has no `Authorization`; Spring Security rejects it. Make sure `permitAll()` matches CORS preflights — or use `cors(Customizer.withDefaults())` which short-circuits preflights.

> [!WARNING]
> **Storing tokens in browser localStorage.** XSS reads them all. Use HttpOnly cookies; sacrifice a bit of cross-origin convenience for the security.

> [!WARNING]
> **Skipping refresh-token rotation.** Spring's default does not rotate refresh tokens on use; an exfiltrated refresh token works until expiry. Enable rotation at the IdP (Okta / Auth0 support it).

> [!WARNING]
> **Long token TTL "for convenience."** 24-hour access tokens are too long. Make access tokens short (5–15 min), refresh tokens shorter than you expected (24 h, not 30 d).

## Deeper Dive — All Five OAuth2 Grant Types

### 1. Authorization Code + PKCE (the modern default)

**When**: SPAs, mobile apps, server-side web apps. Any client that can keep a redirect URI registered.

```
User clicks "Login" → app redirects to:
  /authorize?response_type=code
            &client_id=abc
            &redirect_uri=https://app/callback
            &scope=openid+profile+orders.read
            &state=xyz123                       ← CSRF protection
            &code_challenge=BASE64URL(SHA256(verifier))
            &code_challenge_method=S256

User logs in at IdP → IdP redirects back:
  https://app/callback?code=AUTH_CODE&state=xyz123

App exchanges code for tokens (server-to-server):
  POST /token
  grant_type=authorization_code
  &code=AUTH_CODE
  &redirect_uri=https://app/callback
  &client_id=abc
  &code_verifier=ORIGINAL_VERIFIER   ← proves it was the same client that started

Response:
  { "access_token": "eyJ...", "refresh_token": "abc...", "id_token": "eyJ..." }
```

**PKCE rationale**: prevents authorization-code interception in mobile/SPA. Even if attacker steals the code from the redirect, they can't exchange it without the original `code_verifier`.

### 2. Client Credentials (machine-to-machine)

**When**: backend service calling another backend service. No user context.

```
POST /token
grant_type=client_credentials
&client_id=svc-payment
&client_secret=SECRET
&scope=payments.write

→ { "access_token": "eyJ..." }
```

Spring OAuth2 Client config:
```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          payment-api:
            client-id: svc-payment
            client-secret: ${PAYMENT_SECRET}
            authorization-grant-type: client_credentials
            scope: payments.write
        provider:
          payment-api:
            token-uri: https://auth.example.com/oauth/token
```

```java
// Inject the OAuth2 client
@Service
public class PaymentClient {
    private final WebClient webClient;

    public PaymentClient(WebClient.Builder builder,
                         OAuth2AuthorizedClientManager manager) {
        var oauth2Filter = new ServletOAuth2AuthorizedClientExchangeFilterFunction(manager);
        oauth2Filter.setDefaultClientRegistrationId("payment-api");
        this.webClient = builder.filter(oauth2Filter).build();
    }

    public Payment charge(Charge charge) {
        return webClient.post().uri("/api/charges").bodyValue(charge)
                        .retrieve().bodyToMono(Payment.class).block();
    }
}
```

### 3. Resource Owner Password Credentials (DEPRECATED — don't use)

```
POST /token
grant_type=password
&username=alice
&password=hunter2
&client_id=app
```

**Why deprecated** (OAuth 2.1 will remove it): the client app sees the user's password. Defeats the whole point of OAuth (delegation without sharing credentials). Only justifiable for legacy first-party apps with no migration path.

### 4. Implicit Flow (DEPRECATED — don't use)

```
/authorize?response_type=token&...
→ redirect with #access_token=eyJ... in URL fragment
```

**Why deprecated**: tokens in URL → leak via referrer headers, browser history, server logs. Use Authorization Code + PKCE instead (works for SPAs too).

### 5. Device Code Flow (TVs, CLI tools, IoT)

**When**: device has no browser or is hard to type on.

```
Step 1: Device requests:
  POST /device/authorize?client_id=tv-app
  → { "device_code": "xyz", "user_code": "ABCD-EFGH",
      "verification_uri": "https://example.com/device",
      "expires_in": 600, "interval": 5 }

Step 2: Device shows user_code on screen ("Visit example.com/device, enter ABCD-EFGH")

Step 3: User goes to verification_uri on phone/laptop, enters user_code, approves.

Step 4: Device polls every 5s:
  POST /token
  grant_type=urn:ietf:params:oauth:grant-type:device_code
  &device_code=xyz
  &client_id=tv-app
  → 400 authorization_pending  (until user approves)
  → 200 { "access_token": "eyJ...", "refresh_token": "abc..." }
```

Used by Netflix on TVs, GitHub CLI, kubectl OIDC plugin, AWS CLI SSO.

## Deeper Dive — JWT Validation Step-by-Step

When a request arrives with `Authorization: Bearer eyJhbGc...`:

```
1. PARSE
   - Split on '.'  → header.payload.signature (3 parts)
   - Base64-decode header + payload to JSON
   - If structure wrong → 401 "Malformed token"

2. HEADER VALIDATION
   - alg ∈ allowed set? (e.g., {RS256, ES256}; NEVER {none, HS256-when-RS256-expected})
   - kid (key ID) present? → use to find correct key

3. SIGNATURE VERIFICATION
   - Fetch JWKS from /.well-known/jwks.json (cached, with refresh on kid miss)
   - Find key by kid; algo must match
   - Compute hash(header.payload) with key; compare to signature
   - If mismatch → 401 "Invalid signature"

4. CLAIM VALIDATION
   - exp > now? (else "Token expired")
   - nbf <= now? (not-before; else "Token not yet valid")
   - iss == expected_issuer? (else "Wrong issuer")
   - aud contains expected_audience? (else "Wrong audience")
   - iat <= now? (sanity check)

5. AUTHORIZATION
   - Extract scope / roles / authorities from claims
   - Map to Spring Authority objects
   - Build SecurityContext with Authentication

6. (Optional) Check denylist
   - For known-revoked JTIs (Redis set lookup)
   - Adds ~1ms; trades stateless purity for revocation capability
```

**Spring Security 6 does all this automatically** given:
```yaml
spring.security.oauth2.resourceserver:
  jwt:
    issuer-uri: https://auth.example.com/realms/myapp
    audiences: my-api
```

Plus:
```java
http.oauth2ResourceServer(rs -> rs.jwt(jwt -> jwt
    .jwtAuthenticationConverter(jwtAuthenticationConverter())));
```

## Deeper Dive — JWKS Key Rotation Strategy

The IdP rotates its signing key periodically (typically weekly):

```
Time T:
  Active key: kid="key-2024-W23-001" (used to sign new tokens)
  Previous key: kid="key-2024-W22-001" (still valid for verification — last 7 days of tokens)

JWKS endpoint returns BOTH:
  { "keys": [
      { "kid": "key-2024-W23-001", "kty": "RSA", "n": "...", "e": "AQAB" },
      { "kid": "key-2024-W22-001", "kty": "RSA", "n": "...", "e": "AQAB" }
    ]
  }

Time T+1 week:
  New active: kid="key-2024-W24-001"
  Previous: kid="key-2024-W23-001"
  Old removed: kid="key-2024-W22-001" (its tokens expired by now)
```

Spring Security caches JWKS (default 5 min). On unknown `kid`, it refreshes from JWKS endpoint. So new keys are picked up automatically; old keys remain valid until their tokens naturally expire.

**Critical**: never use HS256 (symmetric) for multi-service architectures — every service needs the same secret = wider compromise blast radius. Use RS256 / ES256 (asymmetric) so only the IdP has the private key.

## Deeper Dive — Refresh Token Rotation (Reuse Detection)

```java
@Service
public class RefreshTokenService {
    @Transactional
    public TokenPair refresh(String refreshToken) {
        RefreshTokenRecord record = repo.findById(refreshToken)
            .orElseThrow(() -> new InvalidTokenException());

        if (record.used()) {
            // CRITICAL: refresh token reused → session compromised
            // Revoke entire family of refresh tokens (all from the user's current session)
            repo.revokeFamilyByUserId(record.userId(), record.sessionId());
            // Log + alert security team
            log.warn("Refresh token reuse detected: user={} session={}",
                     record.userId(), record.sessionId());
            throw new SecurityException("Token reuse detected; please re-login");
        }

        // Mark old as used + issue new pair
        record.markUsed();
        String newRefresh = repo.save(new RefreshTokenRecord(
            UUID.randomUUID().toString(),
            record.userId(),
            record.sessionId(),
            Instant.now().plus(7, DAYS)
        )).id();

        String newAccess = accessTokenService.create(
            record.userId(),
            Duration.ofMinutes(15)
        );

        return new TokenPair(newAccess, newRefresh);
    }
}
```

**Attack scenario** this defeats:
1. Attacker steals refresh token (XSS, network).
2. Either attacker or legitimate user uses it first → issues new pair, old marked used.
3. The other party uses it (the now-used one) → triggers reuse detection.
4. All session tokens revoked → user must re-authenticate.

**Window of attack**: from theft to next legitimate refresh (typically minutes-to-hours instead of days).

## Deeper Dive — Common JWT Vulnerabilities (CVE Catalog)

| Vulnerability | Mechanism | Mitigation |
|---|---|---|
| **alg=none** | Token with `"alg":"none"` and no signature accepted | Whitelist allowed algorithms; reject `none` |
| **alg substitution** | Send `alg=HS256` when server expects `alg=RS256`; HMAC the public key as the secret | Configure decoder for ONE expected algorithm |
| **Key confusion** | Public key used as HMAC secret | Server should only accept matching algorithm per key type |
| **Weak HS256 secret** | Brute-force 256-bit symmetric key offline | Use 256+ bit random secret; better: asymmetric RS256/ES256 |
| **JKU/X5U injection** | Token specifies attacker-controlled `jku` for JWKS URL | Server must hard-code or whitelist JWKS URL |
| **Replay attack** | Old captured token reused | Short TTL (15 min); JTI denylist for sensitive operations |
| **Storage in localStorage** | XSS reads token | HttpOnly cookies; SameSite=Strict |
| **Logged in URL** | Token in query param logged by server/CDN | Always header `Authorization: Bearer ...` |
| **Long TTL access token** | Stolen token valid for days | 5-15 min access; refresh for new ones |
| **No audience check** | Token for service A accepted by service B | Validate `aud` claim |

## Deeper Dive — OAuth2 vs OIDC vs SAML

| Aspect | OAuth 2.0 | OpenID Connect | SAML 2.0 |
|---|---|---|---|
| **Purpose** | Authorization | Authentication + Authorization | Authentication + SSO |
| **Year** | 2012 | 2014 | 2005 |
| **Format** | JSON / opaque | JWT (id_token) + JSON | XML / SAML assertions |
| **Transport** | HTTP/REST | HTTP/REST | HTTP POST/Redirect/SOAP |
| **Token** | access_token (opaque or JWT) | id_token (JWT) + access_token | SAML Assertion (XML) |
| **Use case** | Delegated API access | Web/mobile login + delegated access | Enterprise SSO, B2B federation |
| **Complexity** | Medium | Medium-high | High |
| **Modern relevance** | Standard for APIs | Standard for login | Mostly legacy enterprise |

**When to use which**:
- **Building a public API**: OAuth 2.0 + JWT
- **User login for web/mobile app**: OIDC (OAuth 2.0 + id_token)
- **Enterprise federation with legacy systems**: SAML
- **Multi-tenant SaaS where customers bring their own IdP**: support both OIDC + SAML

## Practice

1. Set up Auth0 (free tier) or run Keycloak in Docker. Register a client. Build a Spring resource server validating its tokens.
2. Use `curl` to obtain a token via client-credentials and call your API. Inspect the JWT on `jwt.io` to see its claims.
3. Customize `JwtAuthenticationConverter` to extract Keycloak-style `realm_access.roles` claims into Spring authorities with `ROLE_` prefix.
4. Build a React SPA using `oidc-client-ts` or `@auth0/auth0-react`. Authorization code + PKCE. Call your Spring resource server.
5. Build a backend client using `OAuth2AuthorizedClientManager`. Configure the inventory-service registration with client-credentials. Call another service.
6. Add CORS configuration. Verify preflights succeed and the SPA can call the API.
7. Try a JWT with an expired `exp`. Verify the response is 401 with a clear error.
8. Try a JWT signed by a different IdP. Verify rejection (issuer mismatch).
9. Set up Spring Authorization Server in a separate module and have your services use it as the IdP. Issue and validate tokens.

## Recap

You should now be able to:

- Explain OAuth2's four roles (resource owner, client, resource server, authorization server) and the difference between access tokens, refresh tokens, and ID tokens.
- Walk the Authorization Code + PKCE flow end-to-end, including why PKCE exists.
- Read a JWT's three parts, decode standard claims (`iss`, `sub`, `aud`, `exp`, `nbf`, `iat`, `jti`), and articulate validation requirements.
- Configure Spring Security as a Resource Server with `issuer-uri`, JWT decoding, JWKS caching, audience validation, and a custom `JwtAuthenticationConverter` for scopes/roles.
- Configure Spring Security as an OAuth2 Client for browser login (`oauth2Login`) and service-to-service (`client_credentials`), and attach tokens to outgoing `RestClient` requests.
- Choose JWT vs opaque tokens (introspection) based on speed and revocation needs.
- Map JWT scopes/roles into Spring authorities using `JwtGrantedAuthoritiesConverter`.
- Use `@AuthenticationPrincipal Jwt` to read claims in controllers.
- Avoid the CVE-catalog pitfalls: `alg=none`, key confusion, missing `aud`/`iss`, long-lived tokens, localStorage storage.
- Explain when Spring Authorization Server is appropriate vs using a third-party IdP.

## Next

Continue to [Method-Level Security](./T16-method-level-security.md) for the deep treatment of `@EnableMethodSecurity`, `@PreAuthorize` / `@PostAuthorize` / `@Secured` / `@RolesAllowed`, SpEL-based authorization (T06), `MethodSecurityExpressionRoot`, and how to integrate domain-level permission checks via `PermissionEvaluator`.
