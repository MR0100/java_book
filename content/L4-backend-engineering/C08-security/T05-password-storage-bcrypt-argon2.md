---
title: "Password storage (bcrypt, Argon2)"
slug: password-storage-bcrypt-argon2
level: L4
module: "Backend Engineering"
section: "Security"
type: concept
difficulty: senior
order: 5
tags: [password-hashing, bcrypt, argon2, scrypt, pbkdf2, salt, pepper, work-factor, memory-hard, gpu-resistant, asic-resistant, password-storage, delegating-password-encoder, spring-security-bcrypt, password-upgrade, passkey, webauthn, plain-text-password-never, sha-not-for-passwords, owasp-recommendation, breach-response, hash-comparison]
prerequisites: []
status: complete
estimated_minutes: 40
last_updated: 2026-06-08
---

# Password storage (bcrypt, Argon2)

If your service stores user passwords, **how you store them is the single most important security decision you'll make** — outranking everything else. Get it wrong (plaintext, unsalted SHA, weak work factor): a database leak = every user account compromised + immediate liability. Get it right (Argon2id or bcrypt with appropriate work factor): leaked hashes are computationally useless for most attackers. The catch: "right" changes as hardware improves — work factors need increasing every 2-3 years.

L4/C01/T14 covered Spring's `PasswordEncoder` interface. **This topic** is the algorithms themselves — the why behind the choices, the OWASP recommendations, the modern alternatives (passkeys / WebAuthn that eliminate passwords entirely).

> [!NOTE]
> Prerequisites: Basic cryptography (hashing, salting). [Spring Security (L4/C01/T14)](../C01-spring-framework/T14-spring-security-authentication-and-authorization.md).

## The Rules

**Never**:

- Store plaintext.
- Use fast hashes (MD5, SHA-1, SHA-256/512) for passwords.
- Use unsalted hashes.
- Reinvent your own scheme.

**Always**:

- Use a **slow, salted, memory-hard** algorithm: **Argon2id** or **bcrypt**.
- Set work factor / memory cost so each hash takes ≥ 100 ms.
- Re-hash on each successful login if the parameters changed.
- Use constant-time comparison.

## Why Slow Hashing

Cryptographic hashes (SHA-256, etc.) are fast: ~10 GH/s on modern GPUs. An 8-character password has ~10^15 combinations — guessable in days. Slow hashes raise the cost per guess from nanoseconds to milliseconds — *the same 10^15 combinations now take centuries*.

Modern attackers use GPUs and ASICs. Memory-hard algorithms (Argon2, scrypt) further raise cost because GPU/ASIC has limited memory per compute unit.

## The Algorithm Landscape

| Algorithm | Year | OWASP rec | Why |
|-----------|-----|:---------:|-----|
| **Argon2id** | 2015 | **#1** | memory-hard; PHC winner |
| **scrypt** | 2009 | second | memory-hard |
| **bcrypt** | 1999 | third | mature; not memory-hard; still strong |
| **PBKDF2** | 2000 | fourth | NIST-approved; not memory-hard |
| MD5, SHA-* | various | **never** | too fast |

**OWASP Top recommendation (2024)**: Argon2id; bcrypt where Argon2 unavailable.

### Argon2id

```
m_cost = 19456 KB
t_cost = 2 iterations
parallelism = 1
salt = random 16 bytes
```

These are **OWASP-recommended** baseline. Tune higher for higher-stakes systems.

Hash format:

```
$argon2id$v=19$m=19456,t=2,p=1$<base64 salt>$<base64 hash>
```

Parameters embedded in hash — verification re-uses them.

### bcrypt

```
work_factor = 12  (i.e., 2^12 = 4096 iterations)
salt = random (built-in)
```

Hash format:

```
$2b$12$<22-char salt><31-char hash>
```

OWASP recommends work factor **≥ 12** as of 2024 (was 10 a few years ago). Adjust as hardware improves.

### Why Not SHA-256

SHA-256 is too fast. A modern GPU computes 10^10 SHA-256 per second. An 8-char password is brute-forced in hours.

People sometimes argue "I salt it though". Salt prevents rainbow-table precomputation. But it doesn't slow down per-guess speed. With SHA-256 + salt, attacker still grinds 10^10 guesses/sec per password. **Slow** is what protects.

## Spring Security `PasswordEncoder`

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
}
```

This creates a `DelegatingPasswordEncoder` supporting multiple formats; encoded hashes include a prefix like `{bcrypt}` or `{argon2}`:

```
{bcrypt}$2b$12$abc...
{argon2}$argon2id$v=19$...
```

Migrate users by upgrading hashes on login:

```java
@Service
public class AuthService {
    public boolean authenticate(String username, String rawPassword) {
        User u = userRepo.findByUsername(username).orElseThrow();
        if (!encoder.matches(rawPassword, u.getPasswordHash())) return false;
        if (encoder.upgradeEncoding(u.getPasswordHash())) {
            u.setPasswordHash(encoder.encode(rawPassword));
            userRepo.save(u);
        }
        return true;
    }
}
```

Old bcrypt-10 hashes get re-encoded to current default on next login.

## Salts

A unique random salt per password prevents:

- **Rainbow tables**: precomputed hash lookups.
- **Identical-password detection**: two users with same password produce different hashes.

Both Argon2 and bcrypt auto-generate salt; included in hash output. Never set a fixed salt.

## Peppers

A server-side secret hashed in addition to salt:

```
hash = bcrypt(password + pepper, salt)
```

Pepper is stored separately (env var, HSM). DB leak alone doesn't enable cracking — attacker also needs the pepper.

Useful but adds operational complexity (rotate pepper requires re-hashing). Most apps skip.

## Constant-Time Comparison

Comparing hashes with `equals()` leaks timing info (if hashes differ in byte 1, comparison short-circuits faster than if they differ in byte 30 — measurable over many requests). Use:

```java
MessageDigest.isEqual(a.getBytes(), b.getBytes());
```

Or rely on the password encoder's `matches()` (which does this correctly).

## Tuning Work Factor

Aim for **~100 ms per hash on production hardware**:

- Login latency tolerable.
- Brute-force cost high.

Benchmark:

```java
long start = System.nanoTime();
encoder.encode("test");
long ms = (System.nanoTime() - start) / 1_000_000;
```

If < 100 ms, increase factor. If > 500 ms, decrease.

Update every 1-2 years as hardware accelerates.

## Passkeys / WebAuthn — The Future

The best password is **no password**. Passkeys (FIDO2/WebAuthn) replace passwords with public-key crypto bound to the device:

- User registers a passkey (Touch ID, Face ID, hardware key).
- Browser/OS holds private key.
- Site stores public key.
- Login: cryptographic challenge/response. No password ever transmitted.

Phishing-resistant; no password to leak.

Spring Security 6.4+ ships WebAuthn support. Adopting passkeys eliminates the password-storage problem.

```java
http.webAuthn(Customizer.withDefaults());
```

For new apps in 2026: implement passkey login alongside passwords. Migrate users toward passkeys over time.

## Breach Response

If your hash storage is leaked:

1. **Force password reset for all users**. They use their existing password; you hash with current parameters; require new password to be different.
2. **Rotate pepper** (if used).
3. **Bump work factor** to outpace whatever cracking attackers might do.
4. **Notify users** per legal requirements.
5. **Investigate**: how did breach happen? Fix root cause.

Speed matters — hashes leaked yesterday cracked tomorrow.

## Common Pitfalls

> [!WARNING]
> **Plaintext storage.** Catastrophic.

> [!WARNING]
> **MD5 / SHA-* for passwords.** Too fast.

> [!WARNING]
> **Unsalted hash.** Rainbow tables.

> [!WARNING]
> **Same salt for all users.** Defeats purpose.

> [!WARNING]
> **Custom hashing scheme.** Don't.

> [!WARNING]
> **Work factor low to "fix slow login".** UX < security.

> [!WARNING]
> **`equals()` to compare hashes.** Timing attack.

> [!WARNING]
> **Logging the password.** Leak surface.

> [!WARNING]
> **No re-hashing on parameter update.** Old weak hashes stay.

> [!WARNING]
> **No rate limiting on login.** Brute force enabled.

## Practice

1. Implement Spring `DelegatingPasswordEncoder`; verify bcrypt + Argon2 prefixes.
2. Benchmark current work factor; tune to ~100 ms.
3. Implement password upgrade on login.
4. Add rate limiting to the login endpoint (T10 of C05).
5. Migrate test users from bcrypt to Argon2; verify works.
6. Try WebAuthn / passkey login flow; compare UX.
7. Audit your storage: ever plaintext logged? Audit / fix.
8. Plan breach response runbook.

## Recap

You should now be able to:

- Hash passwords with Argon2id (OWASP top recommendation) or bcrypt (mature alternative).
- Set work factors so each hash takes ~100 ms; update over time.
- Use Spring's `DelegatingPasswordEncoder` for multi-algorithm support and migration.
- Implement password upgrade-on-login.
- Use unique random salts (auto-handled by Argon2/bcrypt); skip per-app pepper unless justified.
- Use constant-time comparison.
- Adopt passkeys/WebAuthn for new apps; phase out passwords.
- Have breach response plan.
- Avoid the canonical pitfalls: plaintext, fast hashes, no salt, custom schemes, equals comparison, missing rate limit.

## Next

Continue to [OWASP Top 10](./T06-owasp-top-10.md) for the dominant industry checklist of web app security risks and the Spring-aware mitigations.
