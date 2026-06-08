---
title: "JWT (structure, validation, pitfalls)"
slug: jwt-structure-validation-pitfalls
level: L4
module: "Backend Engineering"
section: "Security"
type: concept
difficulty: senior
order: 4
tags: [jwt, json-web-token, jws, jwe, signed, encrypted, hs256, rs256, es256, eddsa, alg-none, key-confusion, jwks, kid, claims-standard, claims-private, exp, iat, nbf, iss, aud, sub, jti, jwt-cves, cve-2022-21449, jwt-best-practices, do-not-store-secrets-in-jwt, payload-too-large, jwt-revocation-denylist, jwks-cache]
prerequisites: [oauth2-and-openid-connect]
status: complete
estimated_minutes: 50
last_updated: 2026-06-08
---

# JWT (structure, validation, pitfalls)

JSON Web Token (RFC 7519) is **a compact, URL-safe representation of claims** signed (JWS) or encrypted (JWE). Used everywhere OAuth2/OIDC tokens are exchanged; also in service-to-service auth, password reset emails, signed magic links. Despite the simple format, JWT has accumulated a **rogues' gallery of CVEs** — `alg=none` acceptance, key confusion (RS256 ↔ HS256), ECDSA bypass (CVE-2022-21449), missing claim validation. A senior engineer treats JWT validation as a tight discipline.

L4/C01/T15 covered Spring's JWT decoder. **This topic** is the format itself — independent of Spring — so you understand exactly what's signed, what's not, what's verified, what's not. Master this; the libraries and their occasional bugs become inspectable.

> [!NOTE]
> Prerequisites: [OAuth2/OIDC (T03)](./T03-oauth2-and-openid-connect.md). Basic crypto: HMAC, RSA, ECDSA signatures.

## The Format

A JWT is **three base64url-encoded segments joined by dots**:

```
header.payload.signature
```

For example (a JWS):

```
eyJhbGciOiJSUzI1NiIsImtpZCI6ImtpZC0xMjMifQ.eyJzdWIiOiJ1c2VyXzQyIiwiaXNzIjoiaHR0cHM6Ly9pc3N1ZXIuZXhhbXBsZS5jb20iLCJhdWQiOiJvcmRlcnMtc2VydmljZSIsImlhdCI6MTcxNzc3MDAwMCwiZXhwIjoxNzE3NzczNjAwfQ.SIGNATURE_BYTES_HERE
```

Decoded:

```json
// header
{ "alg": "RS256", "kid": "kid-123" }

// payload
{
  "sub": "user_42",
  "iss": "https://issuer.example.com",
  "aud": "orders-service",
  "iat": 1717770000,
  "exp": 1717773600
}

// signature: signature of "header.payload" using algorithm + key
```

The **signature** is what makes JWT trustworthy. Without verification, the payload is just a base64 string anyone can read.

## JWS vs JWE

- **JWS (JSON Web Signature, RFC 7515)**: signed. Payload **visible to anyone** — base64-decoded JSON. Anyone with the verification key can validate.
- **JWE (JSON Web Encryption, RFC 7516)**: encrypted. Payload hidden. Only holder of decryption key can read.

99% of tokens you'll see are JWS. JWE is rarer (used for sensitive claims in transit).

```json
// JWE example (5 segments, not 3)
header.encrypted_key.iv.ciphertext.tag
```

## Algorithms

| Family | Algorithms | Key |
|--------|-----------|-----|
| **HMAC** | HS256, HS384, HS512 | symmetric secret |
| **RSA** | RS256, RS384, RS512, PS256, PS384, PS512 | asymmetric (public/private) |
| **ECDSA** | ES256, ES384, ES512 | asymmetric (smaller keys) |
| **EdDSA** | Ed25519 | asymmetric (modern; recommended) |
| **None** | none | no signature; **never accept** |

**Recommendation**: RS256 (compatibility) or ES256/EdDSA (smaller, modern).

HS256 (symmetric) is fine within one app (signing magic links to yourself); not for cross-org tokens (everyone with the secret can forge).

## Standard Claims (RFC 7519)

| Claim | Meaning |
|-------|---------|
| `iss` | Issuer (who created the token) |
| `sub` | Subject (the user/entity) |
| `aud` | Audience (who the token is for) |
| `exp` | Expiration time (UNIX seconds) |
| `nbf` | Not before (UNIX seconds) |
| `iat` | Issued at (UNIX seconds) |
| `jti` | JWT ID (unique per token; for revocation lists) |

OIDC adds:

| Claim | Meaning |
|-------|---------|
| `nonce` | client-supplied; prevents replay |
| `auth_time` | when user authenticated |
| `acr` | authentication context class |
| `email`, `name`, `picture` | user info |

Custom claims (private) are also embedded:

```json
{
  "sub": "user_42",
  "tenant_id": "tenant_7",
  "roles": ["admin", "billing"],
  "scope": "read:orders write:orders"
}
```

## Validation — Five Checks

Every JWT validation must include:

1. **Parse header**: extract `alg`, `kid`.
2. **Allow only expected algorithm(s)**: reject `alg=none`; reject unexpected like `HS256` if you expected RS256.
3. **Fetch public key**: from JWKS (`kid` lookup) or hardcoded.
4. **Verify signature**: using the algorithm + public key.
5. **Validate claims**: `iss` matches expected; `aud` includes you; `exp` in future; `nbf` in past; (optional) `jti` not in denylist.

Skipping any = vulnerability.

## The CVE Hall Of Fame

### `alg=none` Acceptance

```json
{ "alg": "none" }
```

Some libraries accepted "none" — no signature verification. Attacker forges any token. Always reject; whitelist allowed algorithms.

### Key Confusion (RS256 → HS256)

Server expects RS256 (public key). Attacker signs token with HS256 using the **public key bytes as the HMAC secret**. Vulnerable libraries don't enforce algorithm; HMAC-verify with the public key succeeds. **Pin the algorithm**:

```java
JwtDecoder decoder = NimbusJwtDecoder.withJwkSetUri(jwks).jwsAlgorithm(SignatureAlgorithm.RS256).build();
```

### CVE-2022-21449 (Java ECDSA Bypass)

Pre-fix JDKs accepted ECDSA signatures with `r=0, s=0` (Psychic Signatures). Any token "verified" successfully. **Update JDK** to a fixed version. Affects ES256/384/512.

### Missing `aud` / `iss`

Token issued for service A accepted by service B. Always validate.

### Long-Lived Tokens

No revocation possible until `exp`. Limit access tokens to minutes; use refresh tokens.

## JWKS

JSON Web Key Set: the IdP's public keys, fetched at `jwks_uri`:

```json
{
  "keys": [
    { "kid": "kid-123", "kty": "RSA", "alg": "RS256", "n": "...", "e": "AQAB" },
    { "kid": "kid-456", "kty": "EC", "crv": "P-256", "x": "...", "y": "..." }
  ]
}
```

Client caches JWKS; looks up by `kid` from token header; uses the matching key to verify.

**Cache TTL**: typically hours; refresh on miss (new `kid` seen) or per HTTP cache directives. Don't fetch per request (latency + load on IdP).

## Don't Store Secrets In JWT

A common mistake: putting PII or secrets in claims. Even with HTTPS, claims are visible (just base64-decode the payload).

If you need confidentiality, use JWE — but most use cases don't require it. Just keep sensitive data out of claims.

## Revocation

JWT is stateless — once issued, valid until `exp`. To revoke before then:

- **Denylist (jti-based)**: server keeps a list of revoked `jti`s; checks each request. Adds state; defeats stateless win.
- **Allowlist**: keep a list of valid `jti`s; treat unlisted as revoked. Even more stateful.
- **Short expiry**: 5-15 min access tokens; refresh tokens rotate.

For modern APIs: short expiry + refresh rotation is the norm. Denylists for emergencies (security incident).

## Payload Size

JWT travels in every request header. A 4 KB JWT = 4 KB per request. Roles, group memberships, fine-grained scopes balloon claims.

Keep payload small:

- Critical claims only.
- Resolve role-permission mapping at the resource server.
- Use references (claim says `user_id`; RS looks up details).

Aim for < 1 KB.

## Spring Implementation Recap

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: https://issuer.example.com
          audiences: orders-service
          jws-algorithms: RS256
```

`issuer-uri` triggers discovery + JWKS fetch + automatic validation of `iss`/`aud`/`exp`. Spring handles the discipline.

For non-Spring code, use Nimbus directly:

```java
NimbusJwtDecoder decoder = NimbusJwtDecoder.withJwkSetUri(jwks)
    .jwsAlgorithm(SignatureAlgorithm.RS256)
    .build();
Jwt jwt = decoder.decode(tokenString);
```

## Common Pitfalls

> [!WARNING]
> **Accepting `alg=none`.** Forgeable tokens.

> [!WARNING]
> **Not pinning algorithm.** Key confusion attack.

> [!WARNING]
> **Pre-fix JDK with ES256.** CVE-2022-21449. Update.

> [!WARNING]
> **No `aud` validation.** Token sharing across services.

> [!WARNING]
> **No `iss` validation.** Tokens from rogue IdP accepted.

> [!WARNING]
> **Storing secrets in claims.** Visible to anyone.

> [!WARNING]
> **Long expiry.** Stolen token = lifetime access.

> [!WARNING]
> **JWKS fetched per request.** Latency + load. Cache.

> [!WARNING]
> **Payload too large.** Header overhead per request.

> [!WARNING]
> **Custom decoder skipping clock skew.** 60s leeway is standard.

## Practice

1. Parse a JWT via `jwt.io`; identify header, payload, signature.
2. Generate JWS with HS256 + RS256; observe signatures differ.
3. Validate JWT with Nimbus; verify all five checks happen.
4. Force `alg=none` in a token; verify library rejects.
5. Test pinning: token with `RS256` against decoder expecting `ES256` — reject.
6. Set up JWKS cache; verify only one fetch per kid.
7. Decode a JWE; confirm payload not visible until decrypted.
8. Audit your token: what's in `aud` / `iss`? Validated?

## Recap

You should now be able to:

- Parse JWT into header, payload, signature; understand JWS vs JWE.
- Pick signing algorithm: RS256 / ES256 / EdDSA; avoid HS256 cross-org and `none` ever.
- Validate the five checks: parse, pin algorithm, get key, verify signature, validate claims.
- Use JWKS with caching for key rotation.
- Avoid storing secrets in claims (JWS payload is public).
- Implement revocation via short expiry + refresh; denylist for emergency.
- Keep payload small (< 1 KB).
- Avoid the CVE-class pitfalls: alg=none, key confusion, ECDSA bypass, missing iss/aud.

## Next

Continue to [Password storage (bcrypt, Argon2)](./T05-password-storage-bcrypt-argon2.md) for hashing algorithms, work factors, and the right way to store passwords.
