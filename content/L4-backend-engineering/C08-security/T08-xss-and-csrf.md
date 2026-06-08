---
title: "XSS & CSRF"
slug: xss-and-csrf
level: L4
module: "Backend Engineering"
section: "Security"
type: concept
difficulty: senior
order: 8
tags: [xss, cross-site-scripting, reflected-xss, stored-xss, dom-xss, output-encoding, content-security-policy, csp, nonce, hash, csrf, cross-site-request-forgery, csrf-token, double-submit-cookie, samesite-cookie, spring-security-csrf, csrf-stateless-api, ajax-csrf, trusted-types]
prerequisites: [owasp-top-10]
status: complete
estimated_minutes: 45
last_updated: 2026-06-08
---

# XSS & CSRF

**XSS (Cross-Site Scripting)** injects attacker JavaScript into the victim's browser session — the dominant browser-side vulnerability. **CSRF (Cross-Site Request Forgery)** tricks the user's browser into making a state-changing request to a site they're logged into. Different attacks but both browser-side and both must be defended together. For browser apps in 2026, the defenses are mature: **Content Security Policy (CSP)** + **output encoding** for XSS; **SameSite cookies** + **CSRF tokens** for CSRF.

A senior engineer ships both defenses. CSP without output encoding is fragile; output encoding without CSP is insufficient; CSRF protection without SameSite is incomplete.

This topic covers: the three XSS types (reflected, stored, DOM); output encoding by context; CSP design; CSRF mechanics; the SameSite + token pattern; Spring Security CSRF integration; stateless APIs and CSRF.

> [!NOTE]
> Prerequisites: [OWASP Top 10 (T06)](./T06-owasp-top-10.md), browser security model basics.

## XSS — The Three Types

### Reflected XSS

User input echoed back into HTML without encoding:

```
GET /search?q=<script>alert(1)</script>
```

Server includes `q` in response:

```html
<p>You searched for: <script>alert(1)</script></p>
```

Script runs in victim's browser. Delivery: attacker tricks victim into clicking malicious URL.

### Stored XSS

Attacker-supplied script saved (DB / file); served to other users:

```html
<p>Comment by user42: <script>steal(document.cookie)</script></p>
```

More dangerous; persists.

### DOM XSS

Client-side JS unsafely puts user input into DOM:

```js
const name = new URLSearchParams(location.search).get('name');
document.getElementById('greet').innerHTML = 'Hello ' + name;
// ?name=<img src=x onerror=alert(1)>
```

No server involvement; pure browser.

## Output Encoding

Encode user data when inserting into HTML. The encoding depends on **where** in the document:

| Context | Encoding |
|---------|----------|
| HTML body | `&` `<` `>` `"` `'` `/` → entities |
| HTML attribute | + control chars |
| JavaScript string | escape `\` `"` `'` `\n` etc. |
| URL parameter | URL-encode |
| CSS | hex-escape |

Don't use one for another (HTML-encoding inside a `<script>` block doesn't help; the parser is JS, not HTML).

Use libraries:

- **OWASP Java Encoder**: `Encode.forHtml(s)`, `Encode.forJavaScript(s)`, etc.
- **Thymeleaf** auto-encodes via `[[${var}]]`.
- **React / Vue / Angular** auto-encode JSX / templates.

Avoid `[(${var})]` (Thymeleaf raw) or React's `dangerouslySetInnerHTML`.

## Content Security Policy

CSP is a response header that tells the browser what content it's allowed to load:

```http
Content-Security-Policy: default-src 'self'; script-src 'self' 'nonce-abc123'; style-src 'self' 'unsafe-inline'; img-src 'self' https://cdn.example.com; object-src 'none'
```

A strict CSP blocks inline scripts unless tagged with the matching nonce, blocks third-party scripts, blocks data: URLs for scripts, etc.

```html
<script nonce="abc123">/* legitimate inline */</script>
```

Even if XSS injects `<script>alert(1)</script>` without nonce, browser blocks.

Spring header writer:

```java
http.headers(h -> h.contentSecurityPolicy(csp -> csp.policyDirectives(
    "default-src 'self'; script-src 'self' 'nonce-{nonce}'; ...")));
```

(Generate nonce per request.)

CSP is **defense in depth**. With strict CSP + output encoding, XSS becomes nearly impossible.

## Trusted Types (Chrome / Edge)

CSP `require-trusted-types-for 'script'` blocks `innerHTML` etc. unless wrapped in TrustedHTML. Browser API forces every DOM-injection to go through a policy:

```js
const policy = trustedTypes.createPolicy('default', {
    createHTML: s => DOMPurify.sanitize(s)
});
element.innerHTML = policy.createHTML(userInput);
```

Effectively eliminates DOM XSS. Available in Chrome / Edge / future browsers.

## CSRF — The Attack

User logs into `bank.com` (cookie). Attacker site at `evil.com` contains:

```html
<form action="https://bank.com/transfer" method="POST">
    <input type="hidden" name="to" value="attacker">
    <input type="hidden" name="amount" value="10000">
</form>
<script>document.forms[0].submit()</script>
```

User visits `evil.com`; form auto-submits; browser sends user's `bank.com` cookie. Transfer succeeds — user never knew.

## CSRF Defenses

### SameSite Cookie

Modern browsers default `SameSite=Lax` for cookies — third-party POSTs don't include the cookie. Defeats most simple CSRF.

```http
Set-Cookie: SESSION=abc; HttpOnly; Secure; SameSite=Lax
```

- **Lax**: cross-site GETs OK; POST blocked.
- **Strict**: even cross-site link navigation drops cookie.
- **None**: cross-site allowed (requires Secure).

### CSRF Token

Server issues a random token per session; embeds in forms; verifies on POST:

```html
<form method="POST">
    <input type="hidden" name="_csrf" value="random-token-abc">
    ...
</form>
```

Attacker site can't read the token (same-origin policy); can't include it; CSRF fails.

Spring Security enables CSRF by default for session-cookie-based auth:

```java
http.csrf(c -> c.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()));
```

`withHttpOnlyFalse()` lets SPA JS read the CSRF cookie and echo in header.

### Double-Submit Cookie

Server sets two cookies: session + CSRF token. Form includes token; server verifies form token matches cookie token. Stateless CSRF defense; popular.

## Stateless APIs (JWT Bearer) And CSRF

Pure bearer-token APIs (no cookies) aren't CSRF-vulnerable: attacker site can't make the browser include an `Authorization` header it doesn't already send.

```java
http.csrf(c -> c.disable())   // OK for stateless API
    .sessionManagement(s -> s.sessionCreationPolicy(STATELESS));
```

**However**: if you use cookies for auth (even partially), CSRF is back. Mixed mode is dangerous.

## OAuth2 BFF + CSRF

BFF uses HttpOnly cookie for session. Need CSRF protection. Pattern:

- BFF sets CSRF cookie (accessible to JS).
- SPA reads cookie; sends as `X-CSRF-TOKEN` header.
- BFF compares header to cookie value.

Spring Security supports natively.

## XSS Stealing CSRF Tokens

If you have XSS, attacker can read CSRF token → CSRF is moot. XSS must be defeated first.

This is why CSP matters: blocking XSS protects CSRF defenses.

## Common Pitfalls

> [!WARNING]
> **HTML-encoding inside a script tag.** Wrong context; doesn't help.

> [!WARNING]
> **Trusting `innerHTML` with sanitized input.** DOMPurify recommended; sanitize before inject.

> [!WARNING]
> **No CSP.** Only output encoding is fragile.

> [!WARNING]
> **`'unsafe-inline'` in CSP.** Defeats most CSP value. Use nonces or hashes.

> [!WARNING]
> **CSRF disabled "because stateless".** Only safe if truly no cookies.

> [!WARNING]
> **SameSite=None without Secure.** Modern browsers reject.

> [!WARNING]
> **CSRF token reused across sessions.** Token invalidation important.

> [!WARNING]
> **No defense in depth.** CSP without output encoding; tokens without SameSite.

## Practice

1. Find a reflected XSS in a sandbox app; fix with output encoding.
2. Implement CSP with nonce; verify inline script blocked without nonce.
3. Try strict-dynamic CSP; observe behavior.
4. Test CSRF via cross-origin form POST; verify token rejected.
5. Test SameSite=Lax: cross-site navigation, then form POST; observe cookie behavior.
6. Implement CSRF in stateless API mistakenly; observe why it doesn't help.
7. Audit codebase for `innerHTML` / `dangerouslySetInnerHTML` / Thymeleaf raw `[(${var})]`.
8. Set up Trusted Types in a test app.

## Recap

You should now be able to:

- Identify XSS types: reflected, stored, DOM.
- Output-encode per context (HTML body, attribute, JS string, URL).
- Implement CSP with nonce for inline scripts; avoid `unsafe-inline`.
- Use Trusted Types (Chrome/Edge) to block DOM-based XSS.
- Defend CSRF with SameSite cookies + CSRF tokens.
- Configure Spring Security CSRF for session apps; disable for pure bearer-token.
- Apply OAuth2 BFF CSRF pattern.
- Recognize XSS as a prerequisite to many other attacks (CSRF token theft).
- Avoid the canonical pitfalls: wrong-context encoding, no CSP, unsafe-inline, CSRF off when cookies used, SameSite=None without Secure.

## Next

Continue to [CORS & cross-origin requests](./T09-cors-and-cross-origin-requests.md) for the browser's same-origin policy and the protocol that lets you opt out for specific origins.
