# Project Progress & Session Handoff

> [!IMPORTANT]
> **Read this file first at the start of every session, and update it before
> you finish.** It is the single source of truth for *what this project is*,
> *how we work*, and *exactly where to pick up*. Authoring 371 topics is a
> multi-day, multi-session effort — this file is how each session inherits the
> intent and continues from where the last one stopped.

> [!NOTE]
> Why this file and not the generated indexes: `scripts/generate_skeleton.py`
> hard-codes every topic as `planned` in `CURRICULUM.md` and the section
> READMEs (it doesn't read the authored files), so those **understate
> progress**. The counts and lists *here* are the truth. The individual topic
> file's frontmatter `status` is authoritative per topic.

---

## 1. The Mission (the 0 → 100 intent)

Build **one resource that takes a reader from absolute beginner to senior/lead
Java backend engineer, plus interview mastery** — a "0 to hero" master book.

- **Java-first, backend-focused.** Spring, APIs, databases, system design, and
  distributed-systems concepts are all in scope.
- **Audience spans all levels**, not beginners only. Each topic serves its tier
  while staying useful to advanced readers (via skippable "Going deeper"
  sidebars).
- **Markdown-first now; a web build (HTML/React) comes later.** Every `.md`
  must follow the conventions so that future render is fully scriptable.
- **Six learning modules + a dedicated interview module:** L0 Foundations → L1
  Core Java & OOP → L2 Intermediate & Backend Foundations → L3 Advanced Java &
  the JVM → L4 Backend Engineering → L5 Architecture & Leadership → L6 Interview
  Mastery. Each L0–L5 module also has its own interview-prep section.

## 2. How We Work (read before authoring)

| Rule | Where |
|------|-------|
| **Structure is generated — never hand-edit indexes.** Edit `scripts/generate_skeleton.py`'s `MODULES` data and re-run to reshape; it overwrites `CURRICULUM.md` and all section/module READMEs but never deletes authored topic files. | `scripts/generate_skeleton.py` |
| **Formatting rules** (frontmatter, headings, callouts, links). | [CONVENTIONS.md](CONVENTIONS.md) |
| **Depth/quality bar every topic must clear** before `complete`. | [DEPTH-CHECKLIST.md](DEPTH-CHECKLIST.md) |
| **The depth reference topic** — match or exceed it. | [L0/C01/T01 · How Computers Run Programs](content/L0-foundations/C01-cs-foundations/T01-how-computers-run-programs-cpu-memory-binary.md) |
| **Addressing** — refer to anything by code path `L#/C##/T##` (e.g. `L0/C01/T01`). | [CONVENTIONS.md](CONVENTIONS.md) §1 |
| **Topic template** to start from. | [templates/topic-template.md](templates/topic-template.md) |
| **When unsure** whether a topic belongs, or how deep to go — **ask the user.** | — |

## 3. Progress At A Glance

**66 / 371 concept topics complete (17.8%).** L0 complete; **L2 active this session** — C01 done (9/9), C02 done (11/11), C03 done (11/11), **C04 Web & REST COMPLETE (4/4)**. One L3 topic (C01/T01) stays done; L3 paused. **C04 just finished — awaiting user direction; only `L2/C05` Databases & SQL (9) remains to finish L2.**

| Module | Title | Concept sections | Topics | Complete | % |
|--------|-------|:---:|:---:|:---:|:---:|
| L0 | Foundations | 2 | 30 | **30** | **100% (concept + cross-cutting)** |
| L1 | Core Java & OOP | 3 | 49 | 0 | 0% (parallel session) |
| L2 | Intermediate Java & Backend Foundations | 5 | 44 | 35 | 80% **(active — C01+C02+C03+C04 done, C05 last)** |
| L3 | Advanced Java & the JVM | 3 | 41 | 1 | 2% (paused — 1 topic done) |
| L4 | Backend Engineering | 10 | 128 | 0 | 0% |
| L5 | Architecture & Engineering Leadership | 3 | 50 | 0 | 0% |
| L6 | Interview Mastery | 4 | 29 | 0 | 0% |
| **Total** | | **30** | **371** | **66** | **17.8%** |

> [!NOTE]
> The 371 count is **concept topics** only. Each module also has cross-cutting
> sections (Tools & Environment, Hands-On + level project, Best Practices,
> Interview Prep, Q&A/FAQ, Cheatsheets, Resources) that need authoring too —
> tracked as a separate phase once the concept topics of a module are underway.

## 4. Current Position — Resume Here

- **Module:** L2 — Intermediate Java & Backend Foundations (active this session per user direction; pivoted from L3). **L1 is being handled by a parallel session — leave L1 alone.** L0 is FULLY COMPLETE. L3 paused with 1 topic done (`L3/C01/T01` Threads & Runnable — stays complete; resume there if/when the user redirects back to L3).
- **L0 chapter status:** all complete (concept + cross-cutting); see §5.
- **L2/C01:** ✅ COMPLETE 9/9. **L2/C02:** ✅ COMPLETE 11/11. **L2/C03 Networking & Web Fundamentals:** ✅ COMPLETE 11/11 (T01 OSI/TCP-IP, T02 TCP/UDP, T03 IP/ports/sockets, T04 DNS, T05 HTTP/HTTPS, T06 TLS, T07 Cookies/sessions/tokens, T08 Proxies, T09 Load balancers, T10 CDNs, T11 Firewalls & NAT — all done).
- **L2/C04 Web & REST Basics:** ✅ COMPLETE 4/4 (T01 HTTP in depth, T02 REST principles, T03 API design, T04 Content negotiation & serialization — all done). **User chose C04** (after C03 checkpoint).
- **✅ C04 DEEPENING COMPLETE — all 4 files brought to the deep bar** (remediation after the user flagged them shallow). Final sizes: **T01 344 ln/4.3k w, T02 248/3.6k, T03 231/3.1k, T04 234/3.1k** (from 218/227/197/193). New standing rule in memory `feedback_no-shallow-non-core-topics.md`: web/REST/design/tooling topics MUST hit the same deep, expert-dense bar (RFCs, full enumerations, edge cases, security/CVE mechanisms, perf). **Counts unchanged (66/371 — already-complete topics deepened, not new.)**
- **⚠️ CHECKPOINT — only one L2 chapter remains: `L2/C05` Databases & SQL (9 topics)** → finishing it makes **L2 fully COMPLETE (44/44)**. First topic = `L2/C05/T01` → see `content/L2-intermediate-backend/C05-databases-and-sql/README.md`. Other option: resume **L3** (paused, 1/41). Curriculum order → C05 next. L1 = parallel session, leave alone. **ASK THE USER before auto-starting — and apply the new no-shallow depth bar to C05.** — how the SAME resource (T02) is represented in different formats, and how Java objects ↔ JSON/XML on the wire. **CLOSES C04 (4/4)** → after it only C05 remains in L2. **Language layer**: **content negotiation** — the client says what it wants (`Accept: application/json` — T01 negotiation headers), the server picks a representation and replies with `Content-Type` (T01); `Accept-Language`/`Accept-Encoding` (gzip); server-driven vs agent-driven; quality values (`q=`); 406 Not Acceptable. **Serialization/marshalling** — Java object ↔ wire bytes; **JSON** (the dominant web format — text, simple, JS-native; vs **XML** verbose/schematic/legacy SOAP; brief vs Protobuf/binary — gRPC callback T02). **Jackson** (the de-facto Java JSON library): `ObjectMapper` (readValue/writeValue — serialize/deserialize); annotations (`@JsonProperty`/`@JsonIgnore`/`@JsonInclude`/`@JsonFormat`/`@JsonCreator`/`@JsonValue`/`@JsonTypeInfo` for polymorphism); the data-binding tree (JsonNode) vs streaming (JsonParser/Generator) vs full data-binding (POJO); modules (JavaTimeModule for java.time, Kotlin, records — L1/C01/T14 callback); Gson/JSON-B alternatives. **The tolerant reader** (T03 callback — `@JsonIgnoreProperties(ignoreUnknown=true)` → forward/backward compat; the backward-compat discipline made concrete). **Memory/architecture layer**: the **three Jackson processing models** trade-off — **streaming** (JsonParser/Generator, lowest memory/fastest, token-by-token, no tree — for huge payloads), **tree model** (JsonNode, whole doc in memory, flexible), **data binding** (POJO, convenient, reflection-or-codegen cost) — the memory-vs-convenience axis (ties to L0 streaming idea, T04 cost); **reflection cost** (Jackson uses reflection by default to read/write fields → startup + per-call cost; mitigated by caching `ObjectMapper` [thread-safe, expensive to create — reuse it!] + afterburner/blackbird modules + compile-time codegen); **the serialization boundary as the API contract** (T02/T03 — the JSON shape IS the contract; Jackson annotations control it; field-name mapping decouples Java naming from wire naming — MapStruct/Lombok C02 echo); payload size (JSON text vs binary — C03/T05 cost model; gzip Content-Encoding); **security** (deserialization of untrusted input → the polymorphic-deserialization RCE class [Jackson CVEs, `enableDefaultTyping` danger] — C02/T11 vuln callback; never deserialize untrusted polymorphic JSON). **Java mapping**: Spring Boot auto-configures Jackson (`@RestController` returns a POJO → JSON via HttpMessageConverter); `ObjectMapper` reuse; records as DTOs (L1/C01/T14); custom serializers/deserializers. **Common mistakes**: creating an ObjectMapper per request (expensive — reuse a singleton), not handling unknown fields (brittle — ignoreUnknown for tolerant reader T03), exposing entities directly as JSON (leaks DB shape — use DTOs, T03 leaky-abstraction callback + JPA lazy-loading serialization traps L2/C05 fwd), polymorphic deserialization of untrusted input (RCE), date/time format chaos (use JavaTimeModule + ISO-8601), giant payloads via full data-binding (use streaming), circular references (bidirectional → @JsonManagedReference/@JsonBackReference or infinite loop). INTERVIEW (content negotiation, Accept/Content-Type, JSON vs XML vs binary, Jackson ObjectMapper + the 3 models, streaming vs tree vs binding, tolerant reader/ignoreUnknown, ObjectMapper-reuse, DTO-vs-entity, polymorphic-deserialization security). Practice (serialize/deserialize a POJO with Jackson, content-negotiate JSON vs XML via Accept, ignoreUnknown tolerant reader, streaming a huge JSON array, custom serializer, record DTO, measure ObjectMapper-per-call vs reused, java.time formatting, the unknown-field forward-compat demo). Must hit DEPTH-CHECKLIST §4 (the 3-processing-models + reflection-cost + serialization-as-contract + payload + deserialization-security mechanism is the §4a anchor; closes C04 + ties to C02 codegen, C03 cost, L1 records). **After T04, C04 COMPLETE (4/4); L2 = 35/44; only C05 Databases & SQL [9] left → then L2 COMPLETE. Checkpoint with the user after T04 (start C05, or other).** — the practical craft of designing a good REST API (applies T01 HTTP semantics + T02 REST principles to concrete design decisions). **Language layer**: **resource modeling** (nouns, collections/members/sub-resources — T02 callback; granularity — not too coarse/fine; relationships — embed vs link/reference; composite/singleton resources; avoiding deep nesting `/a/1/b/2/c/3`). **Versioning** (the big one — APIs are contracts that must evolve without breaking clients): strategies — **URI versioning** (`/v1/users` — explicit, cache-friendly, most common), **header versioning** (`Accept: application/vnd.api.v1+json` — cleaner URIs, harder to test), **query param** (`?version=1`); **when to version** (breaking changes only — additive changes shouldn't); **backward compatibility** (add fields don't remove/rename, tolerant reader, deprecation policy/sunset headers). **Pagination** (collections can be huge → never return everything): **offset/limit** (`?offset=20&limit=10` — simple, but slow + inconsistent on deep pages / shifting data), **cursor/keyset** (`?after=<cursor>` — stable + scales, the modern default, ties to T01 ETag/L2-C05-DB-index fwd), page metadata (total/links/next — HATEOAS T02). **Filtering / sorting / field selection** — `?status=active&sort=-created&fields=id,name` (sparse fieldsets — the REST answer to GraphQL over-fetch T02); search. **Other design**: bulk operations, async (202 + status polling — T01), idempotency keys (T01), rate limiting (429 T01), HATEOAS links (T02), consistent errors (problem+json T02), the API as a published **contract** (OpenAPI/Swagger spec). **Memory/architecture layer** (lighter — design topic): **the API is a CONTRACT** (T02 uniform-interface callback — once published, clients depend on it → versioning/backward-compat is about not breaking that contract; the deep reason additive-only changes matter); **pagination performance** (offset = O(n) skip on the DB → deep pages slow; cursor/keyset = O(log n) index seek — L2/C05 DB-index fwd; the why-cursor-scales mechanism); **cache-ability of design choices** (URI versioning + stable resource URLs cache well at the CDN T01/C03-T10; query-param explosion hurts cache keys — C03/T10 Vary/cache-key callback); **field selection reduces payload** (bandwidth, the cost model C03/T05). **Common mistakes**: no versioning strategy (breaking clients), versioning on every change (additive should be compatible), offset pagination on huge/deep datasets (slow + inconsistent), returning unbounded collections (no pagination → OOM/timeout), deep nesting, breaking changes without deprecation, inconsistent filter/sort syntax, exposing DB internals (leaky abstraction — ids/schema), no OpenAPI spec. INTERVIEW (resource modeling, versioning strategies + when to version, backward compatibility, offset vs cursor pagination + why cursor scales, filtering/field selection, the API as a contract, async 202, OpenAPI). Practice (design a paginated/filterable collection API, version an API for a breaking change, offset vs cursor pagination [+ measure deep-page cost], add filtering/sorting/sparse-fieldsets, backward-compatible field addition, problem+json errors, write an OpenAPI snippet, critique an API's design). Must hit DEPTH-CHECKLIST §4 (the API-as-contract + cursor-pagination-performance + cache-ability + field-selection-payload mechanism is the §4a anchor; applies T01/T02 + forward-links L2/C05 DB). — the architectural style for web APIs (builds on C04/T01 HTTP semantics). **Language layer**: what REST is (Representational State Transfer — Fielding's dissertation; an architectural STYLE, not a protocol/standard); the **constraints** — **client-server** (separation), **statelessness** (each request self-contained — C03/T05/T07 callback, the scaling enabler), **cacheability** (C04/T01 caching headers), **uniform interface** (the core — resources + representations + self-descriptive messages + HATEOAS), **layered system** (proxies/LBs/CDNs transparent — C03/T08-T10), **code-on-demand** (optional). **The uniform interface in practice**: **resources** (nouns, not verbs — `/users/5` not `/getUser`), **identified by URIs**, **manipulated via representations** (JSON — T04), **HTTP methods as the verbs** (GET/POST/PUT/PATCH/DELETE map to CRUD — C04/T01 idempotency callback), **status codes** convey outcome (C04/T01). **Resource modeling** — collections (`/users`) + members (`/users/5`) + sub-resources (`/users/5/orders`); nouns + HTTP verbs instead of RPC-style verb-in-URL. **HATEOAS** (Hypermedia As The Engine Of Application State — responses include links to related actions/resources; the most-debated, least-implemented constraint; the **Richardson Maturity Model** L0-L3). **REST vs RPC vs GraphQL** (brief contrast — REST resource-oriented, RPC action-oriented/gRPC, GraphQL query-language single-endpoint). **Best practices**: consistent naming (plural nouns, kebab/snake), proper status codes + methods (C04/T01), statelessness (no server session per client — tokens C03/T07), versioning (preview T03), pagination/filtering (preview T03), HATEOAS where it helps, idempotency (C04/T01), error response shape (problem+json RFC 7807). **Memory/architecture layer** (lighter — an architectural/design topic): **statelessness as the scaling property** (C03/T05/T07/T09 callback — any server handles any request → horizontal scale + cacheability; the deep tie); the **uniform interface as the decoupling contract** (client + server evolve independently because they share HTTP's generic semantics — C04/T01 semantics-as-contract callback; this is WHY REST scales to the whole web); caching leverages the uniform interface (GET cacheable — C03/T10); REST's constraints map directly onto the infra you built in C03 (layered system = proxies/LBs/CDNs work BECAUSE REST is stateless + uniform). **Common mistakes**: verbs in URIs (`/createUser` — RPC not REST), statefulness (server sessions breaking scaling — C03/T07/T09), ignoring HTTP method/status semantics (C04/T01), over/under-using HATEOAS, chatty APIs (n+1 → GraphQL/batch), inconsistent naming, tunneling everything through POST, not being RESTful where it doesn't fit (REST isn't always right — RPC/GraphQL/gRPC have their place). INTERVIEW (what is REST, the 6 constraints, statelessness + why it scales, uniform interface, resources vs RPC verbs, idempotency in REST, HATEOAS + Richardson maturity, REST vs RPC vs GraphQL, status codes in REST). Practice (model a resource API for a domain [users/orders], turn an RPC-style API into REST, design the right method+status for CRUD ops, add HATEOAS links, statelessness with tokens not sessions, problem+json errors, critique a bad API). Must hit DEPTH-CHECKLIST §4 (the statelessness-scaling + uniform-interface-decoupling-contract + constraints-map-to-infra mechanism is the §4a anchor; ties C03 networking/infra to API design).
  - `L2/C03/T10` already forward-links to it (its Next). Covers **firewalls & NAT (basics)** — the network-boundary mechanisms that filter traffic and translate addresses; **CLOSES C03 (11/11)** → checkpoint with the user after. Ties up loose ends from earlier topics (NAT promised in T03 private ranges; firewall/DDoS in T08/T10). **Language layer**: **Firewalls** — filter traffic by rules (allow/deny). Types: **packet-filter / stateless** (per-packet rules on IP/port/protocol — L3/L4, T01/T03), **stateful** (tracks connection state — the 4-tuple/TCB T02/T03 — allows return traffic of established connections), **application/L7 firewall / WAF** (inspects HTTP — T05, blocks SQLi/XSS — the T08/T10 WAF callback); default-deny vs default-allow; ingress vs egress filtering; **security groups / network ACLs** (cloud firewalls); host vs network firewall. **NAT (Network Address Translation)** — translates private IPs (T03 10/8·192.168 ranges) ↔ public IPs; **why NAT exists** (IPv4 exhaustion T03 — many private hosts share one public IP); **how it works** — the NAT table maps (internal IP:port) ↔ (public IP:port) per connection (the 4-tuple/PAT — Port Address Translation/masquerading); **the asymmetry** — outbound connections work transparently, but **inbound is blocked by default** (no NAT-table entry → the firewall-like side effect of NAT) → **port forwarding** to expose a service; the consequences (breaks peer-to-peer → STUN/TURN/hole-punching; why you can't reach a private IP from outside, T03 callback; IPv6 reduces the need). **Memory/architecture layer**: the **NAT table as connection state** (T02/T03 — per-connection 4-tuple mapping in the router's memory; entries time out; table size limits = a scaling constraint); stateful firewall = same connection-tracking idea (conntrack); the **performance cost** (every packet checked/rewritten — fast-path/hardware offload); the **NAT-as-accidental-firewall** insight (inbound-deny-by-default is a side effect, not real security → don't rely on it; defense in depth); CGNAT (carrier-grade); how this ties the addressing story together (T01 layers, T03 IP/ports, T02 connection-as-state). **Java angle**: a server behind NAT/firewall needs **port forwarding** or a public IP/LB (T09) to be reachable; bind to 0.0.0.0 not 127.0.0.1 (T03 callback) AND the firewall must allow the port; outbound usually works (NAT), inbound needs explicit opening; cloud security-group rules; the real client IP behind NAT+proxy (X-Forwarded-For T08). **Common mistakes**: relying on NAT as security (it's not a firewall — inbound-deny is a side effect), default-allow firewall, forgetting egress filtering, port not opened in the firewall (app binds fine but unreachable — T03 bind-vs-firewall confusion), NAT breaking P2P without STUN/TURN, stateful-firewall connection timeouts dropping long-lived idle connections (keep-alive T05/T02), security-group misconfiguration (too open 0.0.0.0/0). INTERVIEW (firewall types stateless/stateful/WAF, default-deny, NAT what/why/how, NAT table, why inbound blocked + port forwarding, NAT vs firewall, security groups, NAT and P2P/STUN, IPv6 and NAT). Practice (write iptables/ufw rules allow/deny a port, stateful vs stateless observe return traffic, set up NAT/port-forward on a router, see the NAT table [conntrack], a cloud security group, WAF rule block SQLi, bind 0.0.0.0 + open the firewall port to reach a Java app, trace why a private IP is unreachable). Must hit DEPTH-CHECKLIST §4 (the stateful-connection-tracking + NAT-table-as-state + inbound-deny-side-effect + L3/L4/L7-filtering mechanism is the §4a anchor). **After T11, C03 COMPLETE (11/11) — ASK THE USER about next direction (C04 Web & REST [4], C05 Databases & SQL [9], or resume L3).** — geographically-distributed caching that serves content from a server near the user (the climax of the edge-infra arc T08→T09→T10; a CDN IS a globally-distributed caching reverse proxy + anycast LB). **Language layer**: why CDN — the **RTT/latency cost is dominated by distance** (T05 cost model — speed of light; a user in Tokyo hitting a US origin pays huge RTT); a CDN puts **edge servers (PoPs — points of presence)** worldwide and serves cached content from the nearest → cut latency + offload the origin + absorb traffic spikes/DDoS. **What a CDN caches/does** — static assets (images/CSS/JS/video — the classic), **edge caching** of cacheable responses (T05 Cache-Control/ETag/max-age callback), **origin offload** (the origin only sees cache misses), TLS termination at the edge (T06), compression, increasingly **dynamic/edge compute** (Cloudflare Workers/Lambda@Edge — running code at the edge, L4/L5 fwd), DDoS protection + WAF. **How requests reach the nearest edge** — **anycast** (T04 — one IP, BGP routes to nearest PoP) and/or **DNS-based steering** (T04 — geo-aware authoritative answers return the nearest edge's IP); the CNAME-to-the-CDN setup. **Cache mechanics** — cache hit/miss/revalidation (T05 304/ETag), TTL, **cache invalidation/purge** (the "two hard things" — purge vs versioned URLs/cache-busting `app.v123.js`), **cache key** (URL + headers), **origin shield** (a mid-tier cache reducing origin load). **Memory/architecture layer**: the **distance-is-latency** physics (T05 RTT — why moving the server closer is the only way to cut propagation delay; ~5ms per 1000km minimum); **consistent hashing** (T09 callback — distributing cache keys across edge nodes within a PoP with minimal reshuffle = cache affinity); the **cache-hit-ratio** economics (higher hit ratio = less origin load + lower latency + lower cost — the core CDN metric); **push vs pull** CDN (pull = lazy-fill on first miss, the common model; push = pre-upload); **edge vs origin** tiering (browser cache → CDN edge → origin shield → origin — the multi-layer cache hierarchy, T05 caching callback); static-vs-dynamic (static trivially cacheable; dynamic needs edge compute or short TTL or bypass); the CDN as the ultimate **anycast + reverse-proxy + LB** synthesis (ties T08+T09+T04). **Java angle**: set proper Cache-Control/ETag headers (T05) so the CDN can cache; cache-bust with versioned asset URLs; the origin behind the CDN sees only misses + the CDN's IPs (X-Forwarded-For T08 for the real client); never cache authenticated/personalized responses at a shared edge (Cache-Control: private — the T07 callback). **Common mistakes**: caching personalized/authenticated content at a shared CDN (data leak — Cache-Control: private/no-store, T07), no cache-busting → stale assets after deploy (versioned URLs), wrong cache headers (origin not cacheable → 0% hit ratio), cache-key explosion (varying on too many headers → low hit ratio), forgetting the origin still needs capacity for misses + uncacheable, purge-as-the-only-strategy (slow/global — prefer versioned URLs), not using the CDN for TLS/DDoS, treating dynamic content as cacheable. INTERVIEW (what/why CDN, edge/PoP, anycast vs DNS steering, cache hit ratio, push vs pull, cache invalidation/busting, what to cache vs not [static vs personalized T07], CDN = reverse-proxy+LB+anycast synthesis, distance-is-latency T05, edge compute). Practice (put a CDN/Cloudflare in front of a site, observe cache HIT/MISS headers, set Cache-Control + watch hit ratio, cache-bust with a versioned URL, measure latency from far away with/without CDN, see the edge IP via anycast/traceroute T04, never-cache a personalized page, edge-compute hello-world). Must hit DEPTH-CHECKLIST §4 (the distance-is-latency + cache-hierarchy + hit-ratio + anycast-steering + consistent-hashing mechanism is the §4a anchor; SYNTHESIZES T04 DNS/anycast + T05 caching + T08 reverse proxy + T09 LB — the arc finale). **After T10, only T11 Firewalls & NAT remains → C03 complete (11/11); checkpoint with the user.** — distributing traffic across many backend instances (the T08 reverse-proxy "load balancing" job, deep-dived; the horizontal-scaling enabler T07-statelessness callback). **Language layer**: why LB (one server can't handle the load / SPOF → run N instances, spread requests → scalability + availability + zero-downtime deploys). **L4 vs L7 load balancing** (the central axis, T08 callback) — **L4** (transport, T01/T02): forwards by IP:port/TCP, fast, protocol-agnostic, no payload inspection (NLB); **L7** (application, T05): reads HTTP → route by path/host/header/cookie, terminate TLS (T06), content-based routing (ALB). **Balancing algorithms** — round-robin, weighted, least-connections, least-response-time, IP/consistent **hash** (sticky by client), random-two-choices; **session affinity / sticky sessions** (T07 callback — route a user to the same backend for in-memory sessions, and why stateless/shared-store removes the need). **Health checks** — the LB probes backends (active: periodic /health; passive: observe failures) and **removes unhealthy** ones from the pool → availability; the failover mechanism. **Where LBs live** — hardware (F5) vs software (HAProxy/Nginx/Envoy) vs cloud (AWS ELB/ALB/NLB, GCP) vs **DNS load balancing** (multiple A records / geo, T04 callback) vs **anycast** (T04 — same IP many sites). **Memory/architecture layer**: the **two-connection model** again (T08 — LB terminates + re-originates; L4 can do **DSR**/direct-server-return where the response bypasses the LB); **connection vs request** balancing (L4 pins a whole TCP connection to one backend; L7 can balance each request — matters for HTTP/2 multiplexing T05 + keep-alive); the **stateless-backend requirement** (T07 — true horizontal scaling needs no sticky sessions → shared session store/tokens; sticky sessions are a crutch); **health-check + removal** as the availability mechanism (vs the LB itself as a SPOF → redundant LBs + DNS/anycast, T08 choke-point callback); **the LB as the new choke point** (must scale itself); consistent hashing (why it minimizes reshuffling when a backend is added/removed — cache-affinity, ties to T10 CDN + distributed systems L4/L5). **Java angle**: app instances behind an LB must be **stateless** (T07) or share state (Redis); read the real client IP via X-Forwarded-For (T08); graceful shutdown + health endpoints (Spring Actuator /health) so the LB drains connections before a deploy. **Common mistakes**: sticky sessions masking non-stateless backends (breaks on failover/scaling — go stateless T07), no/बad health checks (routing to dead backends), the LB as an unmonitored SPOF, L4-when-you-need-L7 (can't route by path) or vice versa, ignoring connection-vs-request balancing with keep-alive/H2, thundering-herd on a cold backend after scale-up, not draining connections on deploy (dropped requests). INTERVIEW (why LB, L4 vs L7, algorithms, sticky sessions + why stateless is better, health checks/failover, DNS/anycast LB, the LB as SPOF, connection vs request balancing, consistent hashing, draining). Practice (HAProxy/Nginx LB across 2-3 Java instances, round-robin vs least-conn, kill a backend + watch health-check failover, sticky sessions demo + the failover problem, L4 vs L7 config, X-Forwarded-For through the LB, /health endpoint + graceful drain). Must hit DEPTH-CHECKLIST §4 (the L4-vs-L7 + algorithms + health-check-failover + stateless-requirement + consistent-hashing mechanism is the §4a anchor; the climax of the T08→T09→T10 edge-infra arc). — intermediaries that sit between client and server (T05 HTTP callback). **Language layer**: a **proxy** is a middleman that forwards requests; the crucial distinction — **forward proxy** (sits in front of CLIENTS, acts on their behalf — corporate egress, content filtering, caching, anonymity/VPN-ish, the client configures it) vs **reverse proxy** (sits in front of SERVERS, acts on their behalf — the client thinks it's talking to the origin; Nginx/HAProxy/Envoy/Apache). **What a reverse proxy does** (the backend workhorse) — **TLS termination** (T06 callback — decrypt at the edge, plain HTTP to backends), **load balancing** (distribute across backends — T09 fwd, the overlap), **caching** (serve cached responses — T05 Cache-Control/ETag, CDN T10 fwd), **compression** (gzip/brotli), **request routing** (path/host-based → different services — the API-gateway role), **rate limiting / WAF / security** (shield + filter), **header manipulation** (X-Forwarded-For/Proto — how the backend learns the real client IP T03 since the proxy is the apparent source), **buffering** (absorb slow clients), **serving static files**. **Forward proxy** uses — egress control/filtering, caching, privacy. **The CONNECT method** (T05 callback — tunneling HTTPS through a forward proxy). **API gateway** (a specialized reverse proxy for microservices — auth/routing/rate-limit/aggregation, L4/L5 fwd). **Memory/architecture layer**: the proxy as an **L7 (application) intermediary** (T01 — it reads/understands HTTP, unlike an L4/TCP load balancer which just forwards bytes — the L4-vs-L7 distinction, T09 overlap); the **two-connection model** (client↔proxy and proxy↔backend are SEPARATE TCP connections T02/T03 — the proxy terminates one and originates another → connection pooling/keep-alive reuse to the backend, the C10k-mitigation; why the backend sees the proxy's IP not the client's → X-Forwarded-For); TLS termination architecture (T06 — where certs live, internal plaintext vs re-encrypt); the **single-choke-point** trade-off (the reverse proxy is a SPOF + bottleneck → needs its own redundancy, but centralizes cross-cutting concerns = the value); **transparent vs explicit** proxies. **Common mistakes**: confusing forward vs reverse proxy, trusting X-Forwarded-For blindly (spoofable unless the proxy is trusted + strips inbound), forgetting the proxy terminates TLS (backend logs show proxy IP, T06), not configuring real-client-IP propagation, the proxy as an unmonitored SPOF, double-caching/stale cache, header size/timeout mismatches between proxy and backend, assuming a reverse proxy load-balances (it CAN but they're distinct roles — T09). INTERVIEW (forward vs reverse proxy, what a reverse proxy does, TLS termination, X-Forwarded-For/real client IP, L4 vs L7 proxy, API gateway, CONNECT tunneling, reverse proxy as SPOF, proxy vs load balancer vs CDN). Practice (set up Nginx as a reverse proxy in front of a Java app, TLS-terminate at Nginx + plain HTTP to backend, observe X-Forwarded-For, path-based routing to 2 services, add caching/gzip, a forward proxy with CONNECT, reason L4-vs-L7). Must hit DEPTH-CHECKLIST §4 (the L7-intermediary + two-connection-model + TLS-termination + X-Forwarded-For + choke-point mechanism is the §4a anchor; bridges to T09 load balancers + T10 CDNs). — how **state** is added on top of **stateless HTTP** (T05 callback — "HTTP is stateless; state is layered via cookies/sessions/tokens"). **Language layer**: the problem — HTTP is stateless (T05), so the server can't tell two requests come from the same user → need a way to carry identity across requests. **Cookies** — the `Set-Cookie` response header + the `Cookie` request header (the browser echoes it back on every request to the domain); cookie **attributes** (Domain, Path, Expires/Max-Age, **Secure** [HTTPS-only, T06], **HttpOnly** [no JS access → XSS defense], **SameSite** [Strict/Lax/None → CSRF defense]); session vs persistent cookies; size limits (~4KB). **Server-side sessions** — the cookie holds an opaque **session ID**; the server stores the actual state (user, cart) in memory/Redis/DB keyed by that ID; the classic web-app model. **Tokens / stateless auth** — instead of server-side session storage, put a **signed token** in the client; the server verifies the signature, no lookup. **JWT** (JSON Web Token — header.payload.signature, base64url; claims sub/exp/iat/iss; signed HMAC or RSA/EC — T06 asymmetric callback); **Bearer tokens** in the `Authorization: Bearer` header. **Sessions vs tokens trade-off** — server-side sessions (stateful, easy revoke, server storage + sticky-session/shared-store needed for scaling T09) vs JWT (stateless, scales horizontally, self-contained, but **hard to revoke** before expiry + size + the "store it where" XSS-vs-CSRF dilemma). **OAuth2/OIDC** (delegated auth — "Login with Google"; access/refresh tokens; the authorization-code flow) at a high level. **Memory/architecture layer**: where state lives (client cookie [≤4KB, sent every request → overhead] vs server session store [Redis/DB → a lookup + a scaling dependency] vs self-contained token [no lookup but no easy revoke]); the **stateless-scaling payoff** (T05/T09 — stateless tokens let any server handle any request without sticky sessions or a shared session store — the horizontal-scaling enabler); the cookie-vs-localStorage storage decision (HttpOnly cookie = XSS-safe but CSRF-prone → SameSite; localStorage = XSS-exposed but CSRF-safe) — the security trade-off; token size on every request (JWT bloat) vs a tiny session ID; signature verification cost (HMAC cheap vs RSA verify). **Security** — **XSS** (steal tokens/cookies → HttpOnly, CSP), **CSRF** (ride the auto-sent cookie → SameSite, CSRF tokens), session fixation, token theft/replay (short expiry + refresh), **always over HTTPS** (T06 — Secure flag). **Java mapping**: servlet `HttpSession` (`request.getSession()`, JSESSIONID cookie — the classic server-side session, forward to L4); Spring Security sessions vs JWT; reading/writing cookies (`Cookie`/`Set-Cookie`); a JWT library (jjwt/nimbus) verify/sign. **Common mistakes**: storing sensitive data in a cookie/JWT payload (it's readable — base64 ≠ encryption!), no HttpOnly/Secure/SameSite, JWT-can't-revoke surprise (use short expiry + refresh + denylist), giant JWTs sent every request, session in memory breaking horizontal scaling (use a shared store / sticky sessions T09), CSRF on cookie auth, trusting an unverified/`alg:none` JWT, long-lived tokens. INTERVIEW (stateless HTTP + how state is added, cookie attributes Secure/HttpOnly/SameSite, server-session vs JWT trade-off, JWT structure + base64≠encryption, XSS vs CSRF + defenses, sessions and horizontal scaling, OAuth2 at a glance, where to store a token). Practice (Set-Cookie/Cookie round trip, inspect cookies + attributes in DevTools, server-side session w/ JSESSIONID, decode a JWT on jwt.io [see it's readable!], verify a JWT signature in Java, SameSite CSRF demo, HttpOnly XSS defense, sticky-session vs shared-store reasoning). Must hit DEPTH-CHECKLIST §4 (the where-state-lives + stateless-scaling + XSS-vs-CSRF-storage-tradeoff + signature mechanism is the §4a anchor; ties to T05 statelessness, T06 signing/HTTPS, T09 scaling). — the encryption/authentication layer that makes HTTP into HTTPS (T05 callback — "HTTPS = HTTP + TLS"). **Language layer**: what TLS provides — **confidentiality** (encryption), **integrity** (tamper detection/MAC), **authentication** (the server is who it claims — and optionally the client, mTLS); TLS vs SSL (SSL is the deprecated predecessor; "SSL" colloquially = TLS); where it sits (between TCP T02/T03 and HTTP T05 — an app-layer security wrapper, T01 "presentation-ish" callback); versions (TLS 1.2 vs **1.3** — 1.3 dropped insecure ciphers + cut the handshake to 1-RTT/0-RTT). **The handshake** (the core mechanism) — ClientHello (cipher suites + supported versions + SNI) → ServerHello (chosen cipher) + **certificate** → key exchange (**ECDHE** ephemeral Diffie-Hellman → forward secrecy) → both derive the **session keys** → Finished; then symmetric encryption for the actual data. The **hybrid crypto** insight: **asymmetric** (slow, public/private key) used ONLY to authenticate + agree on a key; **symmetric** (fast, AES) for the bulk data — best of both. **Certificates & PKI** — an **X.509 certificate** binds a public key to a domain, **signed by a CA** (Certificate Authority); the **chain of trust** (leaf → intermediate → root CA in the OS/browser trust store); how the client verifies (signature chain + domain match + validity dates + not revoked); **self-signed** vs CA-signed; **Let's Encrypt**/ACME (free automated certs); **revocation** (CRL/OCSP/OCSP-stapling); wildcard/SAN certs; **CAA** DNS record (T04 callback — which CA may issue). **Memory/architecture layer**: the asymmetric-vs-symmetric cost model (why hybrid — RSA/ECDHE handshake is expensive, AES bulk is cheap + hardware-accelerated AES-NI); the handshake RTT cost (1-2 RTT — T05 RTT-cost-model callback; why TLS 1.3 1-RTT + session resumption/0-RTT matter; TLS terminates the latency budget); **forward secrecy** (ephemeral keys → past sessions safe even if the private key leaks later); the trust-store as the root of trust (compromised CA = broken trust, real incidents); SNI (one IP, many certs — virtual hosting T05); where TLS runs (often TERMINATED at a load balancer/reverse proxy T08/T09 — the backend sees plain HTTP; the TLS-termination architecture). **Java mapping**: `javax.net.ssl` (`SSLSocket`/`SSLContext`/`SSLEngine` — the NIO async one), `HttpsURLConnection`/`HttpClient` (TLS by default for https URLs, T05); the **JVM trust store** (`cacerts`, `keytool`); `TrustManager`/`KeyManager`; the classic mistakes (disabling cert validation / trust-all-certs = catastrophic, self-signed in dev). **Common mistakes**: trusting-all-certs/disabling validation (MITM), expired certs, hostname-mismatch ignored, mixing SSL/TLS-version confusion, self-signed in prod, not stapling OCSP, forgetting cert renewal (Let's Encrypt 90-day), assuming TLS hides everything (SNI + cert + traffic size leak metadata), private key in the repo. INTERVIEW (what TLS provides, the handshake, asymmetric-vs-symmetric/hybrid, certificates + chain of trust + CA, forward secrecy, TLS 1.3 improvements, TLS termination, mTLS, SNI, the JVM trust store). Practice (openssl s_client to inspect a handshake + cert chain, view a cert's chain/SAN/validity in a browser, generate a self-signed cert with keytool, Let's Encrypt/ACME flow, HttpClient over https, add a cert to the JVM cacerts, observe TLS 1.3 1-RTT in Wireshark, the trust-all-certs anti-pattern + why it's dangerous). Must hit DEPTH-CHECKLIST §4 (the handshake + hybrid-crypto + chain-of-trust + RTT-cost + forward-secrecy mechanism is the rich §4a anchor; ties to T05 HTTPS, T04 CAA, T08/T09 termination). — the application-layer (L7, T01) request/response protocol the web runs on, end to end. **This is a BIG, central topic** — budget the full §4 depth. **Language layer**: HTTP as a **text-based, stateless request/response** protocol over TCP (T02/T03); the **anatomy** — request line (method + path + version), **methods** (GET/POST/PUT/PATCH/DELETE/HEAD/OPTIONS + safe/idempotent semantics), **headers** (Host, Content-Type, Content-Length, Accept, User-Agent, Authorization, Cache-Control, Cookie — T07 fwd), the body; the response — **status line + status codes** (1xx/2xx/3xx/4xx/5xx — 200/201/204/301/302/304/400/401/403/404/409/429/500/502/503), headers, body; **content negotiation**, **chunked transfer encoding** (the T02 framing callback — Content-Length vs chunked solves the "TCP is a stream not messages" problem!), MIME types. **The full lifecycle** end-to-end (ties the whole chapter): URL → **DNS** resolve (T04) → **TCP** handshake (T02) → **TLS** handshake if HTTPS (T06 fwd) → send request → server processes → response → render; **connection management** — HTTP/1.0 connection-per-request vs **1.1 keep-alive/persistent connections** (the T02/T03 ephemeral-port + handshake-cost payoff) + pipelining; **HTTP/2** (binary framing, multiplexing many streams over one TCP connection, header compression HPACK, server push) — but still **TCP head-of-line blocking** (T02 callback); **HTTP/3 over QUIC/UDP** (T02 — solves HOL blocking, 0-RTT). **Statelessness** + how state is added back (cookies/sessions/tokens — T07). **HTTPS** = HTTP over **TLS** (T06 fwd — encryption/auth/integrity). **Memory/architecture layer**: HTTP as **text on the wire** (you can literally type it via telnet/nc — T03 callback; the request/response bytes); **chunked encoding framing** (length-prefixed chunks — the explicit solution to TCP's stream-not-messages, T02 IMPORTANT callback); **caching** (Cache-Control/ETag/If-None-Match/304 — the conditional-request mechanism, CDN tie T10); the cost model (RTTs: DNS + TCP + TLS + request = why keep-alive/HTTP-2-multiplexing/HTTP-3 each cut round-trips; head-of-line blocking at HTTP-1 [one req/conn], HTTP-2 [TCP-level], HTTP-3 [solved]); **idempotency/safety** as a correctness contract (retries, T05/L4). **Java mapping**: `java.net.HttpURLConnection` (legacy) vs the modern **`java.net.http.HttpClient`** (Java 11+, HTTP/2, sync + async/CompletableFuture — L2/C01 callback); building requests/reading responses; `HttpServer` (com.sun) / servlet/Spring forward to L4. **Common mistakes**: treating GET as non-idempotent / using GET for mutations, ignoring status-code semantics (200-for-everything), not reusing connections (keep-alive — T02/T03 cost), confusing HTTP/2 multiplexing with HTTP/3 (TCP HOL still bites H2), forgetting HTTPS≠HTTP-secure-magic (it's TLS T06), mishandling chunked/Content-Length framing, caching header confusion (Cache-Control/ETag), assuming stateless means no sessions (cookies T07). INTERVIEW (HTTP methods + idempotency/safety, status code families, the full URL→render lifecycle, keep-alive, HTTP/1.1 vs 2 vs 3 + HOL blocking, chunked vs Content-Length, stateless + cookies, HTTPS=HTTP+TLS, caching/ETag/304, HttpClient). Practice (raw HTTP via telnet/nc/curl -v, each method, read status codes, keep-alive vs close, chunked response, HttpClient sync+async, observe the full lifecycle in browser devtools/Wireshark, caching with ETag/304, compare H1/H2/H3). Must hit DEPTH-CHECKLIST §4 (the text-on-wire + chunked-framing + RTT-cost + HOL-blocking-across-versions mechanism is the rich §4a anchor; ties DNS/TCP/TLS/sockets together). — the distributed name→IP directory that turns `example.com` into an IP (T03) before any connection. **Language layer**: why DNS (humans use names, packets need IPs T03; names are stable while IPs change; one name → many IPs for load balancing T09). **The resolution flow** — stub resolver (the OS) → **recursive resolver** (your ISP's/8.8.8.8/1.1.1.1) → **root** servers (13 logical, return the TLD referral) → **TLD** servers (`.com`, return the authoritative NS) → **authoritative** server (returns the actual record); recursive vs iterative queries; the walk from root down. **Caching + TTL** — every level caches by **TTL** (the record's time-to-live); the resolver cache, OS cache, browser cache; why a DNS change "propagates" slowly (TTL expiry, not real propagation); negative caching. **Record types** — **A** (name→IPv4), **AAAA** (→IPv6), **CNAME** (alias→another name), **MX** (mail servers), **NS** (delegation/authoritative nameservers), **TXT** (SPF/DKIM/verification), **SOA** (zone metadata), **PTR** (reverse IP→name), **SRV** (service location); the zone file. **Mostly UDP port 53** (T02 callback — small query/response, app-level retry; falls back to **TCP** for large responses/zone transfers — the 512-byte UDP limit + EDNS). **Memory/architecture layer**: the **hierarchical distributed database** (the dotted name is a path up a tree — `www.example.com.` read right-to-left: root→com→example→www; the trailing-dot root); the query/response **wire format** (the 12-byte DNS header + question/answer/authority/additional sections, the message ID for matching async UDP responses, name compression with pointers — T01 wire-format/byte-layout angle); why it's UDP (latency T02) + the 512-byte boundary; **anycast** (root/TLD servers are one IP answered by many physically-distributed machines — BGP routes to the nearest, the load/latency mechanism); DNS as a security surface (cache poisoning → **DNSSEC** signatures; DoH/DoT encrypted DNS; DNS used for CDN steering T10 + service discovery). **Java mapping**: `InetAddress.getByName()`/`getAllByName()` (the JVM calls the OS resolver — T03 InetAddress callback); the JVM DNS cache (`networkaddress.cache.ttl` — the infamous default that caches forever / too long, a real production gotcha); resolution happens implicitly when you `new Socket("name", port)`. **Common mistakes**: assuming DNS changes are instant (TTL caching), the JVM DNS-cache-TTL gotcha (stale IPs after failover), CNAME-at-the-apex problem, confusing A vs CNAME, forgetting DNS is a dependency/SPOF + adds latency to the first connection, ignoring negative caching, hardcoding IPs to "skip DNS" (loses failover/LB). INTERVIEW (what is DNS, the resolution chain root→TLD→authoritative, recursive vs iterative, A/AAAA/CNAME/MX/NS/TXT, TTL/caching/propagation, why UDP+53, anycast, DNSSEC, the JVM cache gotcha). Practice (dig +trace to watch the root→TLD→authoritative walk, query each record type with dig/nslookup, observe TTL countdown in cache, see UDP→TCP fallback for big responses, InetAddress.getAllByName in Java, the JVM DNS cache TTL setting, reverse PTR lookup). Must hit DEPTH-CHECKLIST §4 (the hierarchical-distributed-tree + caching/TTL + UDP-wire-format + anycast mechanism is the §4a anchor). — the addressing layer that ties T01/T02 to actual Java networking code. **Language layer**: **IP addresses** — IPv4 (32-bit, dotted-quad, ~4.3B exhausted) vs IPv6 (128-bit, hex, the fix); **subnets/CIDR** (the network/host split, `/24` masks — L0/C01/T02 binary-mask callback), private ranges (10/8, 172.16/12, 192.168/16) + **NAT** (T11 forward), loopback 127.0.0.1/::1, 0.0.0.0 (any), special addresses; **ports** (16-bit, 0-65535; well-known <1024 e.g. 80/443/22/53, registered, ephemeral; the OS assigns an ephemeral source port per outbound connection). **The socket = the (protocol, IP, port) endpoint**; a **connection = the 4-tuple** (src IP, src port, dst IP, dst port) — what uniquely identifies a TCP connection and how one server port handles thousands of clients (each a distinct 4-tuple, T02 TCB callback). **The socket API** — the BSD sockets abstraction (the boundary between app and kernel, T01): server side `socket()→bind()→listen()→accept()` (accept returns a NEW socket per client), client side `socket()→connect()`; then read/write; close. **Java mapping** (the concrete payoff): `InetAddress`/`InetSocketAddress`, `ServerSocket`(bind+listen+accept) + `Socket` (TCP, T02), `DatagramSocket` (UDP), the accept-returns-new-Socket model, `localhost`/binding to 0.0.0.0 vs a specific interface; a tiny TCP echo server/client. **Memory/architecture layer**: a socket is a **file descriptor** (Unix "everything is a file" — the fd indexes a kernel socket structure; T01/T02 kernel-state callback) → fd limits (`ulimit`), the listen **backlog** queue (SYN queue + accept queue), how `accept()` dequeues an established connection; the **ephemeral port range** bounding outbound connections to one destination (~28k, the source-port-exhaustion angle — connection pooling callback T02/T05); IPv4 address as a 32-bit int (the dotted-quad is just 4 bytes — binary/hex, byte-order/network-byte-order BIG-ENDIAN callback to L0 endianness); host-vs-network byte order (htons/ntohs — why ports/addresses are big-endian on the wire). **Common mistakes**: confusing a port with a socket with a connection (4-tuple), binding to 127.0.0.1 then surprised it's unreachable externally (vs 0.0.0.0), fd/port exhaustion, privileged-port (<1024) needs root, forgetting accept() returns a new socket (blocking the listener), NAT/private-IP confusion (T11), assuming IP identifies a host uniquely (NAT/multi-homing). INTERVIEW (IPv4 vs IPv6, what's a socket, the 4-tuple, how one port serves many clients, ephemeral ports, well-known ports, socket=fd, bind 0.0.0.0 vs localhost, CIDR/subnet). Practice (TCP echo server/client, inspect with ss/netstat the 4-tuples, bind to localhost vs 0.0.0.0 and test reachability, ephemeral port observation, fd-as-socket via /proc or lsof, CIDR math, IPv6 socket). Must hit DEPTH-CHECKLIST §4 (socket=fd + 4-tuple + kernel backlog + byte-order is the §4a anchor; ties the whole networking-addressing model to Java code). — the two transport-layer (L4, T01 callback) protocols and when to use each. **Language layer**: both ride on IP (T01 encapsulation — TCP segment / UDP datagram inside an IP packet); the core contrast — **TCP** = connection-oriented, reliable, ordered, flow-controlled, congestion-controlled, byte-STREAM; **UDP** = connectionless, unreliable, unordered, message/DATAGRAM, minimal. **TCP mechanics**: the **3-way handshake** (SYN / SYN-ACK / ACK — connection setup), sequence + ACK numbers (reliability + ordering), retransmission on loss/timeout, the **sliding window** (flow control — don't overrun the receiver), **congestion control** (slow start / AIMD / cwnd — don't overrun the network), the 4-way close (FIN/ACK) + TIME_WAIT; head-of-line blocking. **UDP mechanics**: fire-and-forget datagrams, no handshake/ACK/ordering, 8-byte header (vs TCP 20+), preserves message boundaries (one send = one datagram, unlike TCP's stream), app must handle loss/ordering itself. **When each**: TCP for correctness-critical (HTTP/1-2, DB, file transfer, email); UDP for latency/loss-tolerant or one-to-many (DNS T04, VoIP/video, gaming, DHCP, QUIC/HTTP-3 which rebuilds reliability over UDP in userspace — T05 forward). **Memory/architecture layer**: the header byte-layout (TCP 20-60B with options: ports/seq/ack/flags/window/checksum; UDP 8B: src/dst port/length/checksum — T01 header-overhead callback); the **OS kernel** owns the TCP state machine + send/receive buffers + retransmit timers (T01 — JVM delegates; the socket buffer is kernel memory); **head-of-line blocking** at the byte-stream level; why UDP has lower latency (no handshake RTT, no retransmit waits) + the buffer-bloat/Nagle's-algorithm nuance; the connection as kernel state (a TCP control block per socket — the C10k/resource angle, L3/L4 forward). **Java mapping** (T01/T03 callback): `Socket`/`ServerSocket` = TCP, `DatagramSocket`/`DatagramPacket` = UDP; blocking vs the message-vs-stream API difference. **Common mistakes**: assuming UDP "doesn't work" (it's just unreliable-by-design), expecting TCP to preserve message boundaries (it's a STREAM — must frame yourself, T05/length-prefix), ignoring TIME_WAIT under high connection churn, Nagle vs delayed-ACK latency interaction, UDP without app-level reliability where it's needed, forgetting MTU/fragmentation for big UDP datagrams (T01). INTERVIEW (TCP vs UDP, 3-way handshake, how TCP reliability/ordering/flow/congestion work, stream vs datagram, when UDP, QUIC, head-of-line blocking, what's in the headers). Practice (TCP client/server with Socket, UDP with DatagramSocket, observe handshake in Wireshark, message-boundary loss over TCP stream → framing, packet loss with UDP, compare headers). Must hit DEPTH-CHECKLIST §4 (the handshake + reliability state machine + header layout + kernel-buffer mechanism is the rich §4a anchor — networking topic so byte-layout = the header/wire format).
  - `L2/C02/T10` already forward-links to it (its Next). Covers **dependency vulnerability scanning** — finding KNOWN-vulnerable dependencies (vs T07 static analysis which finds bugs in YOUR code; this scans your DEPENDENCIES against vuln databases). **CLOSES C02 (11/11)** — natural point to check with the user on direction (C03 Networking next, or pivot). NOTE: builds directly on T03 dependency management (the transitive dependency graph) — the vulns are usually in TRANSITIVE deps you didn't choose. **Language layer**: the problem — modern apps are mostly third-party code (the dependency tree, T03/T04); a known CVE in any direct OR transitive dep is YOUR vulnerability (Log4Shell/CVE-2021-44228 as the canonical example — a transitive log4j2 RCE that hit everyone). **Software Composition Analysis (SCA)** = scanning the dependency tree against vulnerability databases. The **databases/identifiers**: **CVE** (Common Vulnerabilities and Exposures — the public ID), **NVD** (NIST National Vulnerability Database), **CVSS** (severity score 0-10, base/temporal/environmental), **GHSA** (GitHub Security Advisories), **OSV** (Google open-source vuln DB), the vendor advisories. **The tools**: **OWASP Dependency-Check** (free, Maven/Gradle plugin; matches deps to CVEs via NVD; the CPE/coordinate-matching + false positives), **OWASP dependency-track** (a platform consuming SBOMs), **Snyk** (commercial, richer DB + fix advice + PR automation), **GitHub Dependabot** (alerts + automated dependency-bump PRs — T05 PR callback), **Gradle/Maven Versions** plugins (find outdated, not vulns), **Sonatype/OSS Index**, **Grype/Trivy** (container + dep scanning). **SBOM** (Software Bill of Materials — CycloneDX / SPDX formats — the inventory of everything in your build; increasingly required, e.g. US EO 14028). **The remediation flow**: scan → triage (real-reachable vs false-positive/unreachable) → upgrade the dep (or override the transitive version, T03 dependencyManagement/resolutionStrategy callback) → if no fix, mitigate/suppress with justification (the suppression-file pattern, T07 callback) → re-scan. **CI gate** (T05/T06/T07 callback) — fail the build on a new HIGH/CRITICAL vuln; the build-break-vs-warn policy; scheduled re-scans (new CVEs appear for OLD code — a dep that was clean yesterday is vulnerable today, the key difference from T07 which only changes when YOUR code does). **Memory/architecture layer** (light §4a — a tooling/process topic, but anchor it): how matching works — coordinate/CPE matching (groupId:artifactId:version → known CVE entries) and its false-positive problem (name collisions); reachability analysis (advanced tools check if the vulnerable CODE PATH is actually called — reduces noise, ties to T07 dataflow/call-graph); scanning happens at build time on the FULL resolved transitive graph (T03 — must resolve first), so it's a build/CI concern + a scheduled concern (the DB changes independently of your code); supply-chain dimension (typosquatting, compromised packages, the build itself as an attack surface — provenance/signing, SLSA). **Common mistakes**: only scanning direct deps (most vulns are transitive — T03), no CI gate (advisory-only = ignored), no scheduled re-scan (new CVEs for unchanged code), alert fatigue / not triaging reachability (drowning), blind auto-upgrade breaking the build, suppressing without justification/expiry, ignoring the SBOM requirement, treating a CVSS score as risk without context (reachability/exposure). INTERVIEW (what is SCA, CVE/CVSS/NVD, Log4Shell, transitive vuln, Dependabot, SBOM, why re-scan unchanged code, scanning vs T07 static analysis, remediation flow). Practice (add OWASP Dependency-Check, introduce a known-vulnerable dep e.g. old log4j and see the CVE, find a TRANSITIVE vuln in the tree, fix by overriding the version T03, suppress a false positive, set a CI gate failing on CRITICAL, generate a CycloneDX SBOM, enable Dependabot). Must hit DEPTH-CHECKLIST §4 (lighter §4a — the coordinate-matching + transitive-graph + DB-changes-independently mechanism anchors it). **After T11, C02 is COMPLETE (11/11) — ASK THE USER about next direction (L2/C03 Networking, or other).**
  - `L2/C02/T09` already forward-links to it (its Next). Covers **annotation processing** — the GENERAL mechanism behind T08 Lombok and T09 MapStruct. **THIRD and capstone of the C02 annotation trio** (T08 Lombok → T09 MapStruct → **T10 Annotation processing**) — T08/T09 showed two USERS; T10 explains the full **JSR-269 (`javax.annotation.processing`)** model and lets the reader WRITE one. **Language layer**: what annotations are first (metadata on code — `@Override`/`@Deprecated`/`@FunctionalInterface` built-ins; declaring `@interface`; elements/members with defaults; **meta-annotations** `@Retention` (SOURCE vs CLASS vs RUNTIME — the crucial axis: SOURCE = compile-time only e.g. Lombok/`@Override`; RUNTIME = readable via reflection e.g. Spring/Jackson; CLASS = in bytecode but not loaded), `@Target` (where it can go), `@Documented`, `@Inherited`, `@Repeatable`); the THREE ways annotations are consumed — (1) **compile-time annotation processing** (this topic — code gen), (2) **runtime reflection** (`getAnnotation`, RUNTIME retention — Spring/Jackson/JUnit), (3) **bytecode tools** (CLASS retention). **The processor API**: implement `javax.annotation.processing.Processor` (usually extend `AbstractProcessor`), `@SupportedAnnotationTypes`/`@SupportedSourceVersion` (or override), the `process(annotations, roundEnv)` method returning a boolean (claimed); registration via `META-INF/services/javax.annotation.processing.Processor` (or Google `@AutoService`). **The processing model**: javac runs processors in **ROUNDS** — round reads annotated elements (`roundEnv.getElementsAnnotatedWith`), may generate new files, generated files are fed into the NEXT round, until no new files (a fixpoint); `processingOver()` final round. **The two key services** (T09 callback): the **`Element` model** (read-only program structure — `TypeElement`/`ExecutableElement`/`VariableElement`; the **visitor** pattern; `Element` vs `TypeMirror` = declaration vs type-usage) for READING, and the **`Filer`** for WRITING new source/class/resource files (often via JavaPoet for readable codegen); the **`Messager`** for compile errors/warnings tied to an element (how a processor reports `@Mapping` problems). **Memory/architecture layer**: WHY processors can only ADD files, never modify existing classes (the round model + the read-only Element API — the sanctioned design that forces MapStruct's `*Impl` and that Lombok BREAKS via internal `JCTree` — full T08/T09 payoff); the cost (processors run inside javac → build-time only, T06/T07/T08/T09 zero-runtime-footprint echo; incremental-compilation interaction — a non-incremental processor can slow builds, Gradle's `@Incremental`/isolating-vs-aggregating processors); SOURCE-retention annotations vanish (not in bytecode) vs RUNTIME (in the constant pool, reflectively readable — L0/C01/T04 bytecode callback) vs CLASS; the `-processor`/`-proc:none`/processor-path mechanics (T01/T02 annotationProcessor scope). **Real processors** survey: Lombok (the hack), MapStruct, Dagger (DI graph at compile time), AutoValue/AutoService/Immutables, Micronaut/Quarkus (compile-time DI+AOP, the "no runtime reflection" frameworks — L4 callback), JPA static metamodel, `@AutoService`. **Common mistakes**: wrong `@Retention` (RUNTIME annotation expected by reflection but declared SOURCE → invisible at runtime; or vice-versa), expecting a processor to modify an existing class (can't — generate or use Lombok-style hack), forgetting `META-INF/services` registration, infinite generation loop (generate a file that triggers generating another forever), poor `Messager` use (errors not tied to elements → bad diagnostics), non-incremental processor killing build speed, reflection-at-runtime when compile-time codegen would be faster (the Micronaut vs Spring argument). INTERVIEW (what is annotation processing, JSR-269, retention policies, rounds, Element vs TypeMirror, Filer, why can't modify existing class / Lombok contrast, compile-time vs runtime annotations, real processors). Practice (declare a custom annotation with each retention + observe via javap/reflection; write a tiny AbstractProcessor that generates a class; register via services; use Messager to emit a compile error; observe rounds; reflectively read a RUNTIME annotation; confirm a SOURCE one is absent from bytecode). **COMPLETES the annotation trio + nearly completes C02 (10/11; only T11 vuln-scanning left).** Must hit DEPTH-CHECKLIST §4 (the rounds + Element/Filer + retention-in-bytecode mechanism is the rich §4a anchor).
  - `L2/C02/T08` already forward-links to it (its Next). Covers **MapStruct** — the compile-time bean-mapping code generator. **SECOND of the C02 annotation trio** (T08 Lombok → **T09 MapStruct** → T10 Annotation processing) — and the KEY CONTRAST to Lombok: where Lombok mutates the AST in place (a hack), MapStruct is a **textbook STANDARD JSR-269 annotation processor** that GENERATES A NEW class (the mapper implementation) — so T09 is the natural bridge into T10's full mechanism. **Language layer**: the problem — mapping between object models (JPA entity ↔ DTO ↔ API model) is endless hand-written `dto.setName(entity.getName())` boilerplate that's error-prone (forget a field → silent data loss) and slow to maintain. MapStruct: declare a `@Mapper` interface with abstract methods (`CarDto toDto(Car car);`), and the processor generates the implementation at compile time. **Core**: `@Mapper` (interface or abstract class), `@Mapping` (field-level: `source`/`target`/`expression`/`constant`/`ignore`/`dateFormat`/`numberFormat`), automatic same-name mapping, nested/deep mapping, collection mapping (List<Car>→List<CarDto>), `@Mapping` with `qualifiedByName`/`@Named` for custom logic, `uses=` to compose mappers, update mapping (`@MappingTarget` to map into an existing object), `componentModel = "spring"` (generate a Spring `@Component` bean for DI — L4 callback), `@BeanMapping`/`@ValueMapping`/`@InheritConfiguration`, `unmappedTargetPolicy = ERROR` (fail the build if a target field is unmapped — the safety win). **Why MapStruct over reflection-based mappers (ModelMapper/Dozer)**: MapStruct generates PLAIN getter/setter calls at compile time → **fast (no reflection), type-safe (mapping errors are COMPILE errors), debuggable (you can read/step the generated code), zero runtime reflection overhead**. **Memory/architecture layer** (the depth — and the T10 bridge): MapStruct is a **standard annotation processor (JSR 269 / `javax.annotation.processing.Processor`)** registered via `META-INF/services` (or `-processor`); javac runs it in the **annotation-processing round**; it reads the `@Mapper` interface via the **`Element`/`TypeMirror` model** (the compiler's read-only API for program structure) and uses the **`Filer`** to WRITE a NEW source file (`CarMapperImpl.java`) which javac then compiles in a subsequent round (the multi-round processing model — generated code can itself be processed). **CRITICAL CONTRAST with Lombok (T08)**: a standard processor can ONLY generate NEW files, it CANNOT modify the annotated type — which is exactly why MapStruct produces a *separate* `*Impl` class while Lombok had to reach into javac internals to edit yours; same trigger, opposite mechanism. The generated impl is **real compiled bytecode**, plain getter/setter calls, **zero runtime cost / no runtime reflection** (T06/T07/T08 zero-runtime-footprint echo); MapStruct is `annotationProcessor`/`provided` scope (a tiny runtime annotations jar). Generated code lives in `build/generated/`/`target/generated-sources` — readable, the big debuggability win. **Lombok + MapStruct together**: a known ordering gotcha — Lombok must run first so the getters/setters exist for MapStruct to see (the `lombok-mapstruct-binding` artifact / processor ordering). **Common mistakes**: forgetting `unmappedTargetPolicy=ERROR` (silent unmapped fields = data loss), name mismatches without `@Mapping` (unmapped), Lombok/MapStruct processor-ordering (getters not seen), reflection-mapper habits (using ModelMapper for "less config" but losing compile-safety/perf), not reading the generated code (it's right there), missing `componentModel` for Spring DI, ambiguous mapping methods, mutable/immutable target mismatch (records as targets need constructor mapping). INTERVIEW (what is MapStruct, vs reflection mappers, how it works = standard processor generating a new class, vs Lombok's in-place mutation, unmappedTargetPolicy, componentModel=spring). Practice (define a @Mapper, read the generated *Impl in build/generated, unmappedTargetPolicy=ERROR catches a missing field, nested/collection mapping, qualifiedByName custom logic, componentModel=spring bean, Lombok+MapStruct ordering, compare to a reflection mapper). Must hit DEPTH-CHECKLIST §4 (the standard-processor / Filer / Element-model mechanism is the §4a anchor and the bridge to T10).
  - `L2/C02/T07` already forward-links to it (its Next). Covers **Lombok** — the annotation-based boilerplate eliminator. **This is the FIRST of the C02 annotation-processing trio** (T08 Lombok → T09 MapStruct → T10 Annotation processing) — so T08 introduces annotation processors at a *user* level and T10 then explains the *mechanism* in full; keep T08 practical but plant the architecture hook that T10 pays off. **Language layer**: what Lombok is (compile-time code generation driven by annotations — `@Getter`/`@Setter`, `@ToString`, `@EqualsAndHashCode`, `@Data` (the bundle), `@NoArgsConstructor`/`@AllArgsConstructor`/`@RequiredArgsConstructor`, `@Builder` (the builder pattern for free), `@Value` (immutable — records callback to L2/C01/T08), `@Slf4j`/`@Log` (logger field), `@SneakyThrows`, `@NonNull`, `@Cleanup`, `@With`). Why it exists (Java's verbosity — POJOs are 80% boilerplate getters/setters/equals/hashCode/toString); the delombok tool (see the generated source). **Records vs Lombok** (Java 16+ records cover the immutable-data-carrier case natively — T08/C01 callback — so for new code records often replace `@Value`/`@Data`; Lombok still wins for mutable JPA entities, builders on non-records, loggers, and pre-record codebases). **Memory/architecture layer** (this is the depth hook): Lombok is NOT runtime reflection and NOT a normal annotation processor — it's a **compile-time bytecode/AST manipulator** that hooks into `javac` via the annotation-processing round (it registers as a processor) but then **mutates the compiler's AST in place** (an unofficial, internal-API trick using `com.sun.tools.javac` — that's why it needs `--add-opens`/`-add-exports` on newer JDKs and why it's considered a "hack") — it ADDS the methods/fields to the AST so they're in the emitted bytecode, **zero runtime cost** (the getters/equals ARE real compiled methods, byte-identical to hand-written ones — T06/T07 zero-runtime-footprint callback; not reflection). Contrast with a *standard* annotation processor (T10) which can only GENERATE NEW files, not modify existing classes — Lombok's in-place AST mutation is why it's powerful and controversial. IDE support needs the Lombok plugin (the IDE must run the same AST trick to see the generated members). **Common mistakes**: `@Data` on JPA entities (equals/hashCode on all fields → breaks lazy loading / identity; use `@Getter/@Setter` + careful equals), `@EqualsAndHashCode` including mutable fields used as map keys, `@Builder` with no sensible defaults / required-field validation, `@SneakyThrows` hiding checked exceptions, Lombok version vs JDK mismatch (the internal-API breakage), over-using Lombok where a record fits, delombok-ing not understood by the team, putting business logic where Lombok-generated methods are expected. INTERVIEW (what is Lombok, compile-time vs runtime, how does it work / AST mutation, records vs Lombok, @Data dangers on entities). Practice (add Lombok, @Data/@Builder/@Value, delombok to see generated code, javap to confirm real methods in bytecode, records-vs-Lombok comparison, @Data-on-entity pitfall). Must hit DEPTH-CHECKLIST §4 (the compile-time AST-mutation mechanism is the §4a anchor; lighter on byte-layout).
  - `L2/C02/T06` already forward-links to it. Covers **static analysis (PMD, SpotBugs, SonarQube)** — finding bugs/smells/vulnerabilities WITHOUT running the code, the deeper tier above T06's style-only linting. **Language layer**: position it on the spectrum — formatter (layout) → linter/Checkstyle (style + simple patterns, T06) → **static analysis** (bug + security + smell detection). **PMD** — source/**AST**-based (T06 lexer→parser→AST callback); detects dead code, unused variables, empty catch blocks, overcomplicated expressions, code smells; rule categories (best-practices/design/performance/security); the **CPD** copy-paste detector. **SpotBugs** (successor to FindBugs) — **BYTECODE-based** (analyses the compiled `.class`, NOT source — the key contrast with PMD); matches bug patterns (null-deref, resource leak, broken equals/hashCode, bad `==` on boxed, concurrency bugs, infinite recursive loops); **FindSecBugs** plugin for security (injection, crypto misuse); rank/confidence. **SonarQube** — a **platform** (server + scanner) aggregating many analysers; the **quality gate** (pass/fail thresholds on bugs/vulnerabilities/code-smells/coverage/duplications/security-hotspots), **"clean as you code"** (gate on NEW code, not the whole legacy base), technical-debt quantification, **SonarLint** IDE plugin, SonarCloud (hosted). **Concepts**: false positives + **suppression** (`@SuppressWarnings`, `@SuppressFBWarnings`, `//NOPMD`, baselines), severity/confidence, fail-the-build gate (T05/T06 CI callback). **Memory/architecture layer** (lighter §4a but anchored): the **source-AST vs bytecode** mechanism is the core — PMD/Checkstyle parse source to an AST (T06); SpotBugs reads the **constant pool + bytecode** of `.class` files (L0/C01/T04 bytecode callback — it sees the actual `invokevirtual`/`getfield`/dataflow), which is why it catches a null-deref PMD can't; **data-flow analysis** over the control-flow graph (tracking nullness/taint); the **fundamental limit** — static analysis is undecidable in general (halting-problem-adjacent), so it's necessarily conservative/approximate → **both false positives and false negatives are unavoidable**; abstract-interpretation idea; build-time cost (SpotBugs needs compiled classes → runs after compile; SonarQube scans are heavier), **cached + CI-side** (T02/T06 callback). **Common mistakes**: ignoring the report (alert fatigue), too-strict gate (people game it), suppressing instead of fixing, no baseline on legacy (drowning in pre-existing findings — use "clean as you code"), confusing scopes (PMD=source vs SpotBugs=bytecode), running heavy scans on every keystroke, treating false positives as real bugs / all findings as equal severity, gating on overall code instead of new code. INTERVIEW. Practice. Must hit DEPTH-CHECKLIST §4 (lighter §4a — tooling, but the AST-vs-bytecode + dataflow + undecidability mechanism anchors it).
  - `L2/C02/T05` already forward-links to it. Covers **code formatters & linters (Checkstyle, Spotless)** — automated code-style enforcement. **Language layer**: the distinction between **formatters** (rewrite code to a canonical layout — google-java-format, palantir-java-format, Spotless as the orchestrator) and **linters/style-checkers** (flag deviations + some bug patterns — Checkstyle) — T19 code-style callback. **Formatters**: google-java-format (opinionated, no config — one true format), palantir-java-format (a google-java-format fork with a different line-wrapping), Eclipse/IntelliJ formatters (configurable). **Spotless** — the build-plugin orchestrator that runs a formatter (and other steps: import ordering, license headers, trailing-whitespace) as part of the build; `spotlessCheck` (verify, fail build on violation) vs `spotlessApply` (auto-fix). **Checkstyle** — a configurable style checker (naming conventions, import order, Javadoc presence, line length, brace placement — the rules from T19) driven by an XML ruleset (Sun/Google presets or custom); runs as a Maven/Gradle plugin bound to the verify/check phase. **The workflow**: format-on-save (IDE) + pre-commit hook (auto-format changed files) + CI verification (fail the build if not formatted/clean) — T19's modern workflow, T05 PR-gate callback. **Memory/architecture layer** (light §4a — a tooling topic): formatters/linters parse the source into an AST (T03 lexer→parser callback) and either re-emit it canonically (formatter) or walk it checking rules (linter); zero runtime impact (T19 — style is compile-time/source-only, stripped at the lexer, bit-identical bytecode); the build-time cost (formatting/checking adds seconds to CI, cached by hashing inputs — T02 build-cache callback). **Common mistakes**: formatter vs linter confusion, not enforcing in CI (drift), format wars across IDEs (settle with a shared formatter), Checkstyle ruleset too strict/noisy, running spotlessApply in CI (should be Check — apply is local), license-header churn, not caching the check. INTERVIEW. Practice. Must hit DEPTH-CHECKLIST §4 (lighter §4a).
  - `L2/C02/T04` already forward-links to it. Covers **Git workflows (branching, PRs, rebasing)** — the collaboration layer of the developer workflow. NOTE: builds on **L0/C01/T10 Introduction to Git** (the parallel session may or may not have authored it — it's in L0/C01 which IS done, so T10 EXISTS — the object model, commit DAG, branches-as-pointers, staging). This topic is the WORKFLOW layer on top. **Language layer**: **branching strategies** — feature branches, trunk-based development (short-lived branches + frequent merges to main), GitFlow (main/develop/feature/release/hotfix — heavier, falling out of favour), GitHub Flow (main + feature branches + PRs, lightweight). **Pull requests / merge requests** — the code-review + CI gate before merging to main; the PR lifecycle (open → review → approve → merge); draft PRs; required reviews + status checks. **Merge vs rebase** — merge (creates a merge commit, preserves history as-it-happened, non-linear) vs rebase (replays your commits on top of the new base, linear history, rewrites commit SHAs); the golden rule (never rebase shared/pushed history others have based work on); interactive rebase (`rebase -i` — squash/reword/reorder/drop commits to clean up before merging). **Merge strategies for PRs** — merge commit, squash-and-merge (collapse a feature branch into one commit), rebase-and-merge (linear, no merge commit). **Resolving conflicts** (when two branches change the same lines; the 3-way merge; conflict markers <<<< ==== >>>>). **Other workflow tools** — `git stash` (shelve WIP), `cherry-pick` (apply one commit elsewhere), `bisect` (binary-search for the commit that introduced a bug — T14 binary search callback), `reflog` (recover lost commits), `git blame` (who/when/why a line). **Memory/architecture layer** (lighter §4a, but tie to the commit DAG from L0/C01/T10): a branch is just a movable pointer (a 41-byte ref file) to a commit; merge creates a commit with two parents (the DAG gains a diamond); rebase creates NEW commits (new SHAs — content-addressed, T10 callback) and abandons the old ones (recoverable via reflog until GC); the commit DAG is immutable+content-addressed so "rewriting history" really means "create new commits and move the pointer"; fast-forward merge (just move the pointer, no merge commit, when the branch is a direct descendant). **Common mistakes**: rebasing shared history (the cardinal sin), force-push to a shared branch without --force-with-lease, merge-vs-rebase confusion, giant PRs (hard to review), long-lived branches (merge hell), committing secrets/large files, not pulling before pushing, resolving conflicts wrong (picking one side blindly), squashing away useful history. INTERVIEW. Practice. Must hit DEPTH-CHECKLIST §4 (lighter §4a — workflow topic, but the commit-DAG mechanism ties it down).
  - `L2/C02/T01`–`T03` forward-link to it. Covers **multi-module projects** — splitting a codebase into multiple build modules that build together. **Language layer**: why multi-module (separation of concerns; independent modules with their own dependencies; reuse; faster incremental builds since only changed modules rebuild; enforce architectural boundaries via the dependency graph). The **aggregator/parent** structure — Maven: a parent POM with `<packaging>pom</packaging>` + `<modules>` listing submodules; each submodule has its own POM with `<parent>` reference; inheritance of dependencyManagement/properties/plugins from parent (T01 callback). Gradle: `settings.gradle(.kts)` with `include(":module-a", ":module-b")`; the root build script + per-module build scripts; `subprojects {}`/`allprojects {}` for shared config (or convention plugins, the modern approach). **Inter-module dependencies** — Maven `<dependency>` on a sibling module's GAV; Gradle `implementation(project(":module-a"))` (the project() notation). **The reactor** (Maven's build ordering — it topologically sorts modules by their interdependencies and builds in dependency order; `mvn -pl module -am` builds a module + its dependencies, `-amd` + dependents). Gradle builds the module DAG similarly. **Build order = topological sort of the module graph** (a module builds after its dependencies — must be a DAG, no cycles). **Common structures**: api/impl split, layered (domain/service/web), feature modules. **Memory/architecture layer**: each module produces its own JAR (its own artifact, own classpath contribution); the build is parallelizable across independent modules (Gradle `--parallel`, Maven `-T`); incremental builds rebuild only changed modules + dependents (T02 build-cache/incremental callback — the big multi-module speed win); shared `dependencyManagement`/version-catalog (T03 callback) keeps versions consistent across modules; circular module dependencies are forbidden (the graph must be a DAG — cycle = build error). **Common mistakes**: circular module dependencies, version drift across modules (use parent dependencyManagement / version catalog), over-modularization (too many tiny modules = build overhead), wrong inter-module scope, building the whole project when -pl/-am would do, putting everything in one module (no boundaries). INTERVIEW. Practice. Must hit DEPTH-CHECKLIST §4 + §4a.
  - `L2/C02/T01` and `T02` already forward-link to it. Covers **dependency management & version conflicts** in depth — the deep treatment of what T01/T02 introduced. **Language layer**: the **dependency graph** (direct + transitive closure); **why conflicts happen** (diamond dependencies — two paths to different versions of the same artifact, the classic A→B→D:1.0 and A→C→D:2.0). **Resolution strategies**: Maven **nearest-wins** (shortest path, first-declared tie) vs Gradle **highest-wins** — worked side-by-side example showing they pick DIFFERENTLY. **Diagnosing conflicts**: `mvn dependency:tree -Dverbose` (shows omitted-for-conflict), `gradle dependencies` / `dependencyInsight`. **Forcing/overriding a version**: Maven `dependencyManagement` (the authoritative version), `<exclusions>`; Gradle `resolutionStrategy.force()`, `constraints {}`, `strictly()` version constraints, `exclude`. **BOMs** (Bill of Materials — import a curated, mutually-compatible version set; Spring Boot BOM; Maven `<scope>import</scope>` vs Gradle `platform()`). **Version ranges** (`[1.0,2.0)` — and why they're risky/discouraged for reproducibility). **Dependency locking** (Gradle's `dependency-locking` / lockfiles; Maven's reproducible-build approaches) for reproducible builds. **Classpath hell / JAR hell** — what goes wrong at RUNTIME when the wrong version is resolved (NoSuchMethodError, NoClassDefFoundError, AbstractMethodError — a compiled-against-X-but-running-against-Y mismatch, T05/L0 callback to UnsupportedClassVersionError family) — these are LINKAGE errors that surface at runtime, not compile time, because the classpath had a different version than compilation. **Shading/relocation** (the shadow/shade plugins rewrite package names to avoid conflicts — e.g. a library bundling its own relocated Guava). **Memory/architecture layer**: the classpath is an ordered list searched first-match-wins (L0/C03 callback) — only ONE version of a class is loaded per classloader; the conflict is resolved at BUILD time (which JAR ends up on the classpath) but the SYMPTOM is at RUNTIME (linkage); classloader isolation (parent-delegation; OSGi/module-path as stronger isolation than the flat classpath — L1/C01 JPMS callback). **Common mistakes**: ignoring dependency:tree warnings, version ranges in production, transitive-version surprise, runtime NoSuchMethodError from a resolved-wrong version, not using a BOM for a framework family, diamond-dependency without pinning, assuming compile success means runtime safety. INTERVIEW. Practice. Must hit DEPTH-CHECKLIST §4 + §4a.
  - `L2/C02/T01` already forward-links to it. Covers **Gradle** — the programmable build tool, the modern alternative to Maven (T01). **Language layer**: Gradle vs Maven philosophy (imperative/programmable Groovy or Kotlin DSL vs Maven's declarative XML; tasks vs lifecycle phases; flexibility vs convention). **Build scripts** — `build.gradle` (Groovy DSL) vs `build.gradle.kts` (Kotlin DSL — type-safe, IDE-friendly, now preferred); `settings.gradle(.kts)` for multi-project. **The task graph** — Gradle's core abstraction: a build is a **directed acyclic graph (DAG) of tasks**; each task has inputs/outputs/actions + dependsOn relationships; `gradle build` resolves and executes the task DAG in dependency order. Built-in tasks from the `java`/`application` plugins (compileJava, processResources, test, jar, build, run). Custom tasks. **Plugins** — `plugins { id 'java' }`, the plugin DSL; community plugins. **Dependencies** — `dependencies { implementation '...'; testImplementation '...' }`; the **configurations** (implementation vs api vs compileOnly vs runtimeOnly vs testImplementation — and the api-vs-implementation distinction that controls transitive exposure, a key Gradle concept Maven lacks); dependency resolution **picks HIGHEST version** (vs Maven nearest-wins — T01/T03 callback); version catalogs (libs.versions.toml). **Repositories** (mavenCentral(), google(), custom; same artifact format as Maven). **Memory/architecture layer**: the **Gradle Daemon** — a long-lived background JVM that stays warm between builds (avoids JVM startup + JIT re-warmup each build — the big speed advantage over Maven which starts fresh each time); the **build cache** (local + remote — reuses task outputs across builds/machines by hashing inputs); **incremental builds** (Gradle tracks task input/output fingerprints and skips up-to-date tasks — far better than Maven's staleness checks, T01 callback); **configuration phase vs execution phase** (Gradle evaluates the build script to build the task graph, THEN executes — the source of "configuration time" cost and the configuration-cache optimisation); parallel task execution. **Common mistakes**: imperative logic in the wrong phase (config vs execution), api-vs-implementation misuse (leaking transitives), Groovy-DSL dynamic-typing footguns (prefer Kotlin DSL), not using the daemon/cache, mixing Gradle versions (wrapper!), forgetting the Gradle wrapper (gradlew). INTERVIEW (Gradle vs Maven, task graph, daemon, api vs implementation, highest-vs-nearest). Practice. Must hit DEPTH-CHECKLIST §4 + §4a.
  - `L2/C01/T08` already forward-links to it. Covers **new language features by version, Java 8 → 21+** — a release-by-release tour of what changed, why it matters, and where each feature is covered in depth elsewhere. **This is a reference/survey topic** (type: concept but survey-flavoured) — it ties the whole book's modern-Java threads together chronologically. **Language layer**, organised by release (LTS marked): **Java 8** (2014, LTS) — lambdas (T01), functional interfaces (T02), method refs (T03), streams (T04-06), Optional (T07), default/static interface methods, `java.time`, `CompletableFuture`, Nashorn (later removed). **Java 9** (2017) — JPMS modules, `var`-less, `List/Set/Map.of`, private interface methods, `Stream.takeWhile/dropWhile/iterate(3-arg)/ofNullable`, `Optional.stream/or/ifPresentOrElse`, JShell, Compact Strings (JEP 254 — T06), the new `StringConcatFactory` (T06). **Java 10** (2018) — `var` local type inference (T18), `List.copyOf`, `Optional.orElseThrow()`, GC improvements. **Java 11** (2018, LTS) — `var` in lambda params, `String` methods (isBlank/strip/lines/repeat), `Files.readString/writeString`, the standardized HTTP Client, single-file source launcher, the removal of Nashorn/applets/Java EE modules. **Java 12-13** — switch expressions preview, text blocks preview. **Java 14** (2020) — switch expressions standard (JEP 361 — T08-control-flow), records preview, pattern matching for instanceof preview, helpful NPE messages, NPE detail. **Java 15** — text blocks standard (JEP 378 — T06), sealed classes preview, hidden classes (JEP 371 — T01 lambdas), ZGC/Shenandoah production. **Java 16** — records standard (JEP 395 — T08), pattern matching for instanceof standard, Stream.toList, mapMulti, Vector API incubator. **Java 17** (2021, LTS) — sealed classes standard (JEP 409), the big LTS; pattern matching for switch preview; removal of the deprecated SecurityManager path. **Java 18-20** — pattern matching for switch + record patterns previews, simple web server, UTF-8 by default. **Java 21** (2023, LTS) — virtual threads (JEP 444 — L3/C01/T14), pattern matching for switch standard (JEP 441 — T08-control-flow), record patterns standard (JEP 440), sequenced collections, structured concurrency preview, string templates preview (later withdrawn). **Java 22-25** — foreign function & memory API, structured concurrency, scoped values, etc. (note: the book's knowledge cutoff is ~2026 — cover through the latest LTS, Java 25 if applicable). **The release cadence** — 6-month time-based releases since Java 9 (Sep/Mar); LTS every 2-3 years (8, 11, 17, 21, 25); preview features + the `--enable-preview` flag mechanism; how to read a JEP. **Why this matters**: knowing what's available in your target version; interview "what's new in Java X" questions; migration considerations. **Memory/architecture layers** are light for this survey topic — but each feature links to its deep topic (lambdas→invokedynamic, records→shallow immutability, virtual threads→Loom, compact strings→byte[]). **Cross-reference table** mapping feature → version → deep-dive topic. **Common mistakes**: using preview features in production without --enable-preview awareness; targeting an old --release and missing features; confusing LTS vs non-LTS support windows; assuming a feature exists in an older runtime (UnsupportedClassVersionError). INTERVIEW (what's new in 8/11/17/21). Practice (check your version, enable preview, find when a feature landed). **Completes the L2/C01 Functional & Modern Java chapter (9/9).** Must hit DEPTH-CHECKLIST §4 (lighter §4a — survey topic, but the per-feature deep-links carry the mechanism).
  - `L2/C01/T07` already forward-links to it. Covers **functional programming style & immutability** — the principles that tie the whole chapter together. **Language layer**: the **FP tenets** in Java — **pure functions** (same input → same output, no side effects), **immutability** (data doesn't change after construction), **first-class functions** (lambdas/method refs as values — T01/T03), **referential transparency** (an expression can be replaced by its value), **declarative over imperative** (streams say what, not how — T04). **Immutability in Java**: how to make an immutable class (final class, final private fields, no setters, defensive copies of mutable inputs/outputs, no `this` escape during construction) — revisit String (T06) as the canonical immutable; **records** (Java 16+, JEP 395) as the concise immutable-data syntax (auto final fields + accessors + equals/hashCode/toString; compact constructor for validation; still need defensive copies for mutable components); immutable collections (`List.of`/`Map.of`/`Set.of`, `List.copyOf`, `Collections.unmodifiable*` — and the shallow-immutability caveat). **Why immutability**: thread-safety for free (no synchronization needed — shared safely, T12 JMM preview), safe map keys / hashCode caching (T06 String lesson), no defensive copying needed downstream, easier reasoning (no spooky action at a distance), safe publication. **The cost**: more allocation (a "change" is a new object); the persistent-data-structure / structural-sharing idea (how functional languages mitigate; Java's `List.copyOf` doesn't but builder patterns help). **Side-effect-free streams** (T04/T06 callback — pure lambdas required for correct parallel). **Functional error handling** preview (Optional T07 instead of null; Either/Result patterns; exceptions break referential transparency). **Memory layer**: immutable objects can be shared freely (one instance, many references — no copies); the allocation cost of copy-on-change; the `final` field JMM safe-publication guarantee (a properly-constructed immutable object's final fields are visible to all threads without synchronization — full in L3/C01/T12); String interning / hashCode caching as immutability payoffs. **Architecture layer**: the JIT can treat final fields as constants (constant-folding across an immutable object's reads); EA can stack-allocate short-lived immutables; immutability enables aggressive caching and lock-free sharing; the GC cost of high allocation (young-gen churn) vs the cache/locality benefit of compact immutables. **Common mistakes**: "immutable" class with a mutable field exposed (no defensive copy — array/Date/collection leak), final reference to a mutable object (the reference is final, the object isn't), forgetting defensive copies in records with mutable components, mutating through an aliased reference, equating `final` with deeply immutable, `Collections.unmodifiableList` wrapping a still-mutable backing list, premature immutability hurting a hot allocation path. INTERVIEW. Practice. **This is the chapter's synthesis topic** — pulls together lambdas/streams/Optional into the FP mindset. Must hit DEPTH-CHECKLIST §4 + §4a.
  - `L2/C01/T06` already forward-links to it. Covers **`Optional<T>`** — the container-for-a-maybe-absent-value, the principled alternative to returning `null`. **Language layer**: why Optional exists (null is the "billion-dollar mistake"; NPE; null doesn't document intent); creation — `Optional.of(x)` (throws on null!), `Optional.ofNullable(x)` (null→empty), `Optional.empty()`; querying — `isPresent`/`isEmpty` (Java 11+)/`get` (avoid!); **functional consumption** — `ifPresent(Consumer)`, `ifPresentOrElse(Consumer, Runnable)` (Java 9+), `map(Function)`, `flatMap(Function returning Optional)`, `filter(Predicate)`; **defaulting** — `orElse(value)` (eager!), `orElseGet(Supplier)` (lazy), `orElseThrow()`/`orElseThrow(Supplier)`; `or(Supplier<Optional>)` (Java 9+); `stream()` (Java 9+ — 0-or-1 element stream, for flatMapping). The **`orElse` vs `orElseGet` trap** — `orElse` ALWAYS evaluates its argument (even when present!), so `orElse(expensiveDefault())` always runs the expensive call; `orElseGet(() -> expensiveDefault())` only runs it when empty. **map vs flatMap** (flatMap when the mapper itself returns Optional — avoids Optional<Optional<T>>). The primitive `OptionalInt`/`OptionalLong`/`OptionalDouble` (T17 boxing callback). **Best-practice rules** (Effective Java 55): use Optional as a RETURN type for maybe-absent results; NEVER as a field, parameter, or in collections (use empty collection instead); never `Optional.get()` without checking; don't wrap a collection in Optional. **Memory layer**: Optional is a thin immutable wrapper — one reference field; `Optional.empty()` is a shared singleton (zero allocation); `Optional.of(x)` allocates one ~16-byte object holding the reference; the boxing of primitive values (why OptionalInt exists); EA can eliminate short-lived Optionals (T01/T15 callback) — so a `findFirst().map(...).orElse(...)` chain often allocates nothing after JIT. **Architecture layer**: Optional adds an allocation + a few method calls vs a raw null check; for hot paths the null check is faster (which is why the JDK's own hot internals still use null) — Optional is for API clarity, not perf; EA mitigates; the megamorphic caveat on shared Optional-returning utilities. **Common mistakes**: Optional.of(null) NPE, get() without isPresent, orElse(expensive()) always-evaluates, Optional field/parameter, Optional<List> instead of empty list, isPresent+get instead of map/orElse, nesting Optionals, Optional in a hot loop. INTERVIEW. Practice. Must hit DEPTH-CHECKLIST §4 + §4a.
  - `L2/C01/T04` and `T05` already forward-link to it. Covers **parallel streams** — `collection.parallelStream()` / `stream.parallel()`. **Language layer**: how a sequential stream becomes parallel (one method call); what changes (work split across threads, results merged); the **correctness requirements** — operations must be **stateless**, **non-interfering** (don't modify the source), and for reductions the accumulator/combiner must be **associative** + the identity a true identity (else wrong results); encounter-order vs `findAny`/`forEach` non-determinism; `unordered()` to relax ordering. **Memory layer**: the **`Spliterator.trySplit()`** decomposition (T04 callback) — the source splits into chunks recursively (binary tree of splits); good splitters (ArrayList/arrays → cheap O(1) balanced splits) vs bad splitters (LinkedList/iterate/Files.lines → poor or sequential splits → no parallel benefit); SIZED/SUBSIZED characteristics enable balanced splitting. **Architecture layer**: the **ForkJoinPool.commonPool** — the shared work-stealing pool parallel streams run on (size = CPU cores − 1 by default; `-Djava.util.concurrent.ForkJoinPool.common.parallelism=N`); the fork/join recursive decomposition (split until small enough, compute leaves, join/merge up the tree); **work-stealing** (idle threads steal tasks from busy threads' deques); the **overhead** (split + task scheduling + merge + common-pool contention) that makes parallel SLOWER for small data / cheap ops; the **N×Q rule of thumb** (parallelism pays only when N elements × Q cost-per-element is large enough — roughly ≥ ~10k cheap ops or fewer expensive ops); the **blocking-task hazard** (a parallel stream doing blocking I/O ties up common-pool threads, starving the whole JVM — use a custom ForkJoinPool or don't parallelise blocking work); CONCURRENT collectors (T05 callback) avoid the merge. **When to parallelise** decision guide (large N + expensive Q + splittable source + associative + stateless + no blocking → maybe; otherwise sequential). **Common mistakes**: parallelising small/cheap streams (slower), stateful/side-effecting ops (race/wrong results), shared mutable accumulation via forEach (data race — use collect), non-associative reduce (wrong answer), blocking I/O on the common pool (starvation), assuming parallel preserves order, LinkedList/iterate source (no benefit), measuring without JMH/warmup. INTERVIEW. Practice with parallel-vs-sequential benchmarks, trySplit observation, common-pool size, blocking-starvation demo. Must hit DEPTH-CHECKLIST §4 + §4a.
  - `L2/C01/T04` already forward-links to it. Covers **Collectors** — the mutable-reduction strategy objects that `Stream.collect(...)` uses to build a result. **Language layer**: `collect` as mutable reduction (vs `reduce`'s immutable fold); the **`Collector<T, A, R>`** interface — supplier (create container A), accumulator (fold T into A), combiner (merge two A for parallel), finisher (A→R), characteristics (CONCURRENT/UNORDERED/IDENTITY_FINISH). The **`Collectors` factory** catalogue: `toList`/`toSet`/`toCollection`/`toUnmodifiableList`; `toMap`(key,val[,merge,supplier]) + the duplicate-key IllegalStateException trap; `joining`(delimiter/prefix/suffix); `counting`, `summingInt`/`averagingInt`/`summarizingInt`, `minBy`/`maxBy`, `reducing`; `mapping`, `filtering` (Java 9+), `flatMapping` (Java 9+); `collectingAndThen`; **`groupingBy`** (classifier → Map<K, List<T>>) + downstream collector (groupingBy(c, counting()), groupingBy(c, mapping(...)), nested groupingBy); `groupingByConcurrent`; **`partitioningBy`** (boolean classifier → Map<Boolean, List<T>>); `teeing` (Java 12+ — two collectors + merge). **Memory layer**: the 3-arg `collect(supplier, accumulator, combiner)` form maps directly to the Collector's three functions; the container is mutated in place (one ArrayList grown, not N immutable folds — why collect beats reduce for collections); the combiner only runs in parallel; downstream collectors compose into nested containers. **Architecture layer**: sequential collect = supplier once + accumulator per element + finisher once (no combiner); parallel collect = per-thread containers + combiner merges (associativity required); CONCURRENT+UNORDERED collectors (groupingByConcurrent) share one ConcurrentMap across threads (no merge) — faster for unordered parallel; IDENTITY_FINISH skips the finisher. **Common mistakes**: toMap duplicate-key IllegalStateException (provide a merge function), reduce-instead-of-collect for mutable accumulation (O(N²) string concat), mutable downstream not thread-safe in parallel, ordering surprises with toSet/groupingBy (HashMap unordered), modifying a collected unmodifiable list. INTERVIEW. Practice. Must hit DEPTH-CHECKLIST §4 + §4a.
  - `L2/C01/T03` already forward-links to it. Covers the **Streams API** — the central modern-Java data-processing abstraction. **This is a BIG topic** — likely the longest in the chapter; budget ~110-130 min and the full §4 depth. **Language layer**: what a stream is (a *lazy*, *single-use* pipeline over a source — NOT a data structure; doesn't store elements); **stream sources** (`Collection.stream()`, `Arrays.stream`, `Stream.of`, `Stream.iterate`/`generate`, `IntStream.range`, `Files.lines`, `Random.ints`); **intermediate operations** (lazy, return a new stream) — `filter`, `map`, `mapToInt`/`mapToObj`, `flatMap` (and `mapMulti` Java 16+), `distinct`, `sorted`, `peek`, `limit`, `skip`, `takeWhile`/`dropWhile` (Java 9+); **terminal operations** (eager, trigger execution, consume the stream) — `forEach`/`forEachOrdered`, `collect`, `reduce`, `count`, `min`/`max`, `anyMatch`/`allMatch`/`noneMatch`, `findFirst`/`findAny`, `toArray`, `toList` (Java 16+), `sum`/`average` (primitive streams); the **lazy-evaluation model** — intermediate ops build a pipeline but do nothing until a terminal op runs; **short-circuiting** (`findFirst`, `anyMatch`, `limit` stop early); **single-use** (a stream throws `IllegalStateException` if reused — `stream has already been operated upon or closed`); **stateless vs stateful** intermediate ops (`map`/`filter` stateless; `sorted`/`distinct`/`limit` stateful — need to see elements). **Memory layer**: the pipeline is a **linked chain of `Sink` objects** (the internal `AbstractPipeline`/`Sink` machinery) — each stage wraps the next; elements flow **one at a time through the whole chain** (depth-first, not breadth-first) so there's no intermediate collection per stage (the key efficiency vs naive collection-per-step); the spliterator drives the source; **lazy = the chain is built as objects but not traversed until the terminal `forEach`/`collect` calls into the spliterator**. Boxing in object streams vs `IntStream` (T02/T17 callback). **Architecture layer**: the JIT inlines the whole stateless pipeline (map+filter fuse into one loop body after warmup — comparable to a hand-written loop); stateful ops (`sorted`, `distinct`) materialise a buffer (break the fusion); the **megamorphic risk** when one pipeline shape sees many lambda types (T01/T02); short-circuit avoids processing the tail; stream overhead vs a plain for-loop (small per-element but real — for trivial hot loops a `for` can win; for readability + parallelism streams win). Preview `parallelStream` (full in T06). **Common mistakes**: reusing a stream (IllegalStateException); side-effects in `map`/`peek` (should be pure); `peek` for logic (it's for debugging only, may be skipped under optimisation in Java 9+); forgetting streams are lazy (nothing runs without a terminal op); `forEach` instead of `collect` for building collections (thread-unsafe ad-hoc accumulation); boxing via `Stream<Integer>` instead of `IntStream`; infinite streams without `limit`; `sorted` without a comparator on non-Comparable; modifying the source during streaming. INTERVIEW. Practice. Must hit DEPTH-CHECKLIST §4 + §4a.
- **In progress (unfinished drafts):** none.
- **Immediate next action:** author **only** `L2/C01/T09` New language features by version against DEPTH-CHECKLIST (one topic at a time per user instruction). **This completes the L2/C01 Functional & Modern Java chapter (9/9)** — after it, advance to `L2/C02` (Build Tools & Workflow, 11 topics) as the next chapter, or check with the user on priorities.
- **Note on L2 prereqs:** L2's functional chapter assumes L1 (interfaces, anonymous classes, generics) which the parallel session owns. Where an L1 topic isn't authored yet, L2 topics **forward-reference** it by code path + reasonable slug (e.g. L1/C01 interfaces, L1/C02 generics/collections). Cross-references may point to planned-but-not-yet-authored files; expected and tracked.

## 5. Per-Chapter Tracker (concept chapters)

Status values: `not-started` · `in-progress` · `done`. Address a chapter as
`L#/C##` (e.g. `L0/C01`).

### L0 — Foundations
| Chapter | Done / Total | Status |
|---------|:---:|--------|
| C01-cs-foundations | 11 / 11 | **done** (concept) |
| C02-java-core | 19 / 19 | **done** (concept) |
| C03-tools-and-environment | 1 / 1 | **done** (cross-cutting) |
| C04-hands-on | 2 / 2 | **done** (cross-cutting) |
| C05-best-practices | 2 / 2 | **done** (cross-cutting) |
| C06-interview-prep | 1 / 1 | **done** (cross-cutting) |
| C07-qa-faq | 1 / 1 | **done** (cross-cutting) |
| C08-cheatsheets | 1 / 1 | **done** (cross-cutting) |
| C09-resources | 1 / 1 | **done** (cross-cutting) |

### L1 — Core Java & OOP
| Chapter | Done / Total | Status |
|---------|:---:|--------|
| C01-oop | 0 / 19 | **(handled by parallel session — leave alone)** |
| C02-collections-and-core-apis | 0 / 23 | not-started |
| C03-testing-fundamentals | 0 / 7 | not-started |

### L2 — Intermediate Java & Backend Foundations
| Chapter | Done / Total | Status |
|---------|:---:|--------|
| C01-functional-and-modern-java | 9 / 9 | ✅ **done** (this session) |
| C02-build-tools-and-workflow | 11 / 11 | **complete** ✅ |
| C03-networking-fundamentals | 11 / 11 | **complete** ✅ |
| C04-web-and-rest-basics | 4 / 4 | **complete** ✅ |
| C05-databases-and-sql | 0 / 9 | not-started |

### L3 — Advanced Java & the JVM
| Chapter | Done / Total | Status |
|---------|:---:|--------|
| C01-concurrency | 1 / 17 | **in-progress** (started this session) |
| C02-jvm-internals-and-performance | 0 / 14 | not-started |
| C03-design-patterns-and-principles | 0 / 10 | not-started |

### L4 — Backend Engineering
| Chapter | Done / Total | Status |
|---------|:---:|--------|
| C01-spring-framework | 0 / 25 | not-started |
| C02-persistence-jpa-hibernate | 0 / 16 | not-started |
| C03-databases-advanced | 0 / 6 | not-started |
| C04-nosql-and-caching | 0 / 12 | not-started |
| C05-apis-advanced | 0 / 11 | not-started |
| C06-reactive-programming | 0 / 7 | not-started |
| C07-messaging-and-streaming | 0 / 11 | not-started |
| C08-security | 0 / 16 | not-started |
| C09-testing-advanced | 0 / 8 | not-started |
| C10-devops-and-observability | 0 / 16 | not-started |

### L5 — Architecture & Engineering Leadership
| Chapter | Done / Total | Status |
|---------|:---:|--------|
| C01-software-architecture | 0 / 14 | not-started |
| C02-distributed-systems-and-system-design | 0 / 23 | not-started |
| C03-engineering-leadership | 0 / 13 | not-started |

### L6 — Interview Mastery
| Chapter | Done / Total | Status |
|---------|:---:|--------|
| C01-foundations-of-interviewing | 0 / 2 | not-started |
| C02-dsa-for-interviews | 0 / 14 | not-started |
| C03-design-interviews | 0 / 2 | not-started |
| C04-behavioral-and-company-tracks | 0 / 11 | not-started |

## 6. Completed Topics (ground truth — append each finished topic)

| Topic file | Title | Completed |
|------------|-------|-----------|
| `L0/C01/T01` · `content/L0-foundations/C01-cs-foundations/T01-how-computers-run-programs-cpu-memory-binary.md` | How Computers Run Programs | 2026-05-28 |
| `L0/C01/T02` · `content/L0-foundations/C01-cs-foundations/T02-number-systems-binary-hex-and-basic-bit-math.md` | Number Systems & Basic Bit Math | 2026-05-29 |
| `L0/C01/T03` · `content/L0-foundations/C01-cs-foundations/T03-what-is-a-programming-language-compiled-vs-interpreted.md` | What Is a Programming Language; Compiled vs Interpreted | 2026-05-29 |
| `L0/C01/T04` · `content/L0-foundations/C01-cs-foundations/T04-source-to-bytecode-to-jvm-to-machine-code.md` | Source to Bytecode to JVM to Machine Code | 2026-05-29 |
| `L0/C01/T05` · `content/L0-foundations/C01-cs-foundations/T05-jdk-vs-jre-vs-jvm.md` | JDK vs JRE vs JVM | 2026-05-29 |
| `L0/C01/T06` · `content/L0-foundations/C01-cs-foundations/T06-installing-java-and-setting-up-path-java-home-windows-macos-linux.md` | Installing Java & Setting Up PATH / JAVA_HOME | 2026-05-29 |
| `L0/C01/T07` · `content/L0-foundations/C01-cs-foundations/T07-choosing-and-using-an-ide.md` | Choosing & Using an IDE | 2026-05-29 |
| `L0/C01/T08` · `content/L0-foundations/C01-cs-foundations/T08-command-line-terminal-basics.md` | Command-Line / Terminal Basics | 2026-05-29 |
| `L0/C01/T09` · `content/L0-foundations/C01-cs-foundations/T09-problem-solving-and-pseudocode.md` | Problem Solving & Pseudocode | 2026-05-29 |
| `L0/C01/T10` · `content/L0-foundations/C01-cs-foundations/T10-introduction-to-git-and-version-control.md` | Introduction to Git & Version Control | 2026-05-29 |
| `L0/C01/T11` · `content/L0-foundations/C01-cs-foundations/T11-reading-errors-and-stack-traces.md` | Reading Errors & Stack Traces | 2026-05-29 |
| `L0/C02/T01` · `content/L0-foundations/C02-java-core/T01-program-structure-class-main-statements.md` | Program Structure (class, main, statements) | 2026-05-29 |
| `L0/C02/T02` · `content/L0-foundations/C02-java-core/T02-variables-and-primitive-types.md` | Variables & Primitive Types | 2026-06-01 |
| `L0/C02/T03` · `content/L0-foundations/C02-java-core/T03-literals-and-constants-final.md` | Literals & Constants (`final`) | 2026-06-01 |
| `L0/C02/T04` · `content/L0-foundations/C02-java-core/T04-operators-arithmetic-relational-logical-bitwise-assignment.md` | Operators (arithmetic, relational, logical, bitwise, assignment) | 2026-06-01 |
| `L0/C02/T05` · `content/L0-foundations/C02-java-core/T05-type-conversion-and-casting.md` | Type Conversion & Casting | 2026-06-01 |
| `L0/C02/T06` · `content/L0-foundations/C02-java-core/T06-strings-and-text-blocks.md` | Strings & Text Blocks | 2026-06-02 |
| `L0/C02/T07` · `content/L0-foundations/C02-java-core/T07-stringbuilder-stringbuffer.md` | StringBuilder / StringBuffer | 2026-06-02 |
| `L0/C02/T08` · `content/L0-foundations/C02-java-core/T08-control-flow-if-else-switch-switch-expressions.md` | Control Flow (if/else, switch, switch expressions) | 2026-06-02 |
| `L0/C02/T09` · `content/L0-foundations/C02-java-core/T09-loops-while-do-while-for-for-each.md` | Loops (while, do-while, for, for-each) | 2026-06-04 |
| `L0/C02/T10` · `content/L0-foundations/C02-java-core/T10-break-continue-labels.md` | break / continue / labels | 2026-06-04 |
| `L0/C02/T11` · `content/L0-foundations/C02-java-core/T11-arrays-1-d-multi-dimensional.md` | Arrays (1-D, multi-dimensional) | 2026-06-04 |
| `L0/C02/T12` · `content/L0-foundations/C02-java-core/T12-methods-parameters-return-values.md` | Methods, parameters, return values | 2026-06-04 |
| `L0/C02/T13` · `content/L0-foundations/C02-java-core/T13-method-overloading.md` | Method overloading | 2026-06-04 |
| `L0/C02/T14` · `content/L0-foundations/C02-java-core/T14-recursion.md` | Recursion | 2026-06-04 |
| `L0/C02/T15` · `content/L0-foundations/C02-java-core/T15-variable-scope-and-lifetime.md` | Variable scope & lifetime | 2026-06-04 |
| `L0/C02/T16` · `content/L0-foundations/C02-java-core/T16-varargs.md` | Varargs | 2026-06-04 |
| `L0/C02/T17` · `content/L0-foundations/C02-java-core/T17-wrapper-classes-and-autoboxing.md` | Wrapper classes & autoboxing | 2026-06-04 |
| `L0/C02/T18` · `content/L0-foundations/C02-java-core/T18-var-local-variable-type-inference.md` | var (local variable type inference) | 2026-06-04 |
| `L0/C02/T19` · `content/L0-foundations/C02-java-core/T19-comments-javadoc-and-code-style.md` | Comments, Javadoc & code style | 2026-06-04 |
| `L0/C03/T01` · `content/L0-foundations/C03-tools-and-environment/T01-toolchain-quick-reference.md` | Toolchain Quick Reference | 2026-06-04 |
| `L0/C04/T01` · `content/L0-foundations/C04-hands-on/T01-exercises.md` | Exercises | 2026-06-04 |
| `L0/C04/T02` · `content/L0-foundations/C04-hands-on/T02-project-number-guessing-game.md` | Level Project — Number-Guessing Game | 2026-06-04 |
| `L0/C05/T01` · `content/L0-foundations/C05-best-practices/T01-l0-idioms.md` | L0 Idioms | 2026-06-04 |
| `L0/C05/T02` · `content/L0-foundations/C05-best-practices/T02-l0-pitfalls-catalogue.md` | L0 Pitfalls Catalogue | 2026-06-04 |
| `L0/C06/T01` · `content/L0-foundations/C06-interview-prep/T01-foundations-questions.md` | Foundations Interview Questions | 2026-06-04 |
| `L0/C07/T01` · `content/L0-foundations/C07-qa-faq/T01-faq.md` | L0 FAQ | 2026-06-04 |
| `L0/C08/T01` · `content/L0-foundations/C08-cheatsheets/T01-l0-cheatsheet.md` | L0 Cheatsheet | 2026-06-04 |
| `L0/C09/T01` · `content/L0-foundations/C09-resources/T01-resources.md` | L0 Resources | 2026-06-04 |
| `L3/C01/T01` · `content/L3-advanced-jvm/C01-concurrency/T01-threads-and-runnable.md` | Threads & Runnable | 2026-06-04 |
| `L2/C01/T01` · `content/L2-intermediate-backend/C01-functional-and-modern-java/T01-lambda-expressions.md` | Lambda expressions | 2026-06-04 |
| `L2/C01/T02` · `content/L2-intermediate-backend/C01-functional-and-modern-java/T02-functional-interfaces-function-predicate-supplier-consumer.md` | Functional interfaces (Function, Predicate, Supplier, Consumer) | 2026-06-04 |
| `L2/C01/T03` · `content/L2-intermediate-backend/C01-functional-and-modern-java/T03-method-and-constructor-references.md` | Method & constructor references | 2026-06-04 |
| `L2/C01/T04` · `content/L2-intermediate-backend/C01-functional-and-modern-java/T04-streams-api-intermediate-and-terminal-operations.md` | Streams API (intermediate & terminal operations) | 2026-06-04 |
| `L2/C01/T05` · `content/L2-intermediate-backend/C01-functional-and-modern-java/T05-collectors-and-grouping.md` | Collectors & grouping | 2026-06-04 |
| `L2/C01/T06` · `content/L2-intermediate-backend/C01-functional-and-modern-java/T06-parallel-streams.md` | Parallel streams | 2026-06-04 |
| `L2/C01/T07` · `content/L2-intermediate-backend/C01-functional-and-modern-java/T07-optional-in-depth.md` | Optional in depth | 2026-06-04 |
| `L2/C01/T08` · `content/L2-intermediate-backend/C01-functional-and-modern-java/T08-functional-programming-style-and-immutability.md` | Functional programming style & immutability | 2026-06-04 |
| `L2/C01/T09` · `content/L2-intermediate-backend/C01-functional-and-modern-java/T09-new-language-features-by-version-java-8-to-21-plus.md` | New language features by version (Java 8 to 21+) | 2026-06-04 |
| `L2/C02/T01` · `content/L2-intermediate-backend/C02-build-tools-and-workflow/T01-maven-lifecycle-pom-dependencies-plugins.md` | Maven (lifecycle, POM, dependencies, plugins) | 2026-06-04 |
| `L2/C02/T02` · `content/L2-intermediate-backend/C02-build-tools-and-workflow/T02-gradle-tasks-build-scripts-dependencies.md` | Gradle (tasks, build scripts, dependencies) | 2026-06-04 |
| `L2/C02/T03` · `content/L2-intermediate-backend/C02-build-tools-and-workflow/T03-dependency-management-and-version-conflicts.md` | Dependency management & version conflicts | 2026-06-04 |
| `L2/C02/T04` · `content/L2-intermediate-backend/C02-build-tools-and-workflow/T04-multi-module-projects.md` | Multi-module projects | 2026-06-04 |
| `L2/C02/T05` · `content/L2-intermediate-backend/C02-build-tools-and-workflow/T05-git-workflows-branching-prs-rebasing.md` | Git workflows (branching, PRs, rebasing) | 2026-06-04 |
| `L2/C02/T06` · `content/L2-intermediate-backend/C02-build-tools-and-workflow/T06-code-formatters-and-linters-checkstyle-spotless.md` | Code formatters & linters (Checkstyle, Spotless) | 2026-06-04 |
| `L2/C02/T07` · `content/L2-intermediate-backend/C02-build-tools-and-workflow/T07-static-analysis-pmd-spotbugs-sonarqube.md` | Static analysis (PMD, SpotBugs, SonarQube) | 2026-06-04 |
| `L2/C02/T08` · `content/L2-intermediate-backend/C02-build-tools-and-workflow/T08-lombok.md` | Lombok | 2026-06-04 |
| `L2/C02/T09` · `content/L2-intermediate-backend/C02-build-tools-and-workflow/T09-mapstruct.md` | MapStruct | 2026-06-04 |
| `L2/C02/T10` · `content/L2-intermediate-backend/C02-build-tools-and-workflow/T10-annotation-processing.md` | Annotation processing | 2026-06-04 |
| `L2/C02/T11` · `content/L2-intermediate-backend/C02-build-tools-and-workflow/T11-dependency-vulnerability-scanning.md` | Dependency vulnerability scanning | 2026-06-04 |
| `L2/C03/T01` · `content/L2-intermediate-backend/C03-networking-fundamentals/T01-osi-and-tcp-ip-models.md` | OSI & TCP/IP models | 2026-06-04 |
| `L2/C03/T02` · `content/L2-intermediate-backend/C03-networking-fundamentals/T02-tcp-vs-udp.md` | TCP vs UDP | 2026-06-04 |
| `L2/C03/T03` · `content/L2-intermediate-backend/C03-networking-fundamentals/T03-ip-ports-and-sockets.md` | IP, ports & sockets | 2026-06-04 |
| `L2/C03/T04` · `content/L2-intermediate-backend/C03-networking-fundamentals/T04-dns-resolution-records.md` | DNS (resolution, records) | 2026-06-04 |
| `L2/C03/T05` · `content/L2-intermediate-backend/C03-networking-fundamentals/T05-http-https-lifecycle.md` | HTTP/HTTPS lifecycle | 2026-06-04 |
| `L2/C03/T06` · `content/L2-intermediate-backend/C03-networking-fundamentals/T06-tls-ssl-and-certificates.md` | TLS/SSL & certificates | 2026-06-04 |
| `L2/C03/T07` · `content/L2-intermediate-backend/C03-networking-fundamentals/T07-cookies-sessions-and-tokens.md` | Cookies, sessions & tokens | 2026-06-04 |
| `L2/C03/T08` · `content/L2-intermediate-backend/C03-networking-fundamentals/T08-proxies-and-reverse-proxies.md` | Proxies & reverse proxies | 2026-06-04 |
| `L2/C03/T09` · `content/L2-intermediate-backend/C03-networking-fundamentals/T09-load-balancers.md` | Load balancers | 2026-06-04 |
| `L2/C03/T10` · `content/L2-intermediate-backend/C03-networking-fundamentals/T10-cdns.md` | CDNs | 2026-06-04 |
| `L2/C03/T11` · `content/L2-intermediate-backend/C03-networking-fundamentals/T11-firewalls-and-nat-basics.md` | Firewalls & NAT (basics) | 2026-06-04 |
| `L2/C04/T01` · `content/L2-intermediate-backend/C04-web-and-rest-basics/T01-http-in-depth-methods-status-headers.md` | HTTP in depth (methods, status, headers) | 2026-06-04 |
| `L2/C04/T02` · `content/L2-intermediate-backend/C04-web-and-rest-basics/T02-rest-principles-and-best-practices.md` | REST principles & best practices | 2026-06-04 |
| `L2/C04/T03` · `content/L2-intermediate-backend/C04-web-and-rest-basics/T03-api-design-resources-versioning-pagination-filtering.md` | API design (resources, versioning, pagination, filtering) | 2026-06-04 |
| `L2/C04/T04` · `content/L2-intermediate-backend/C04-web-and-rest-basics/T04-content-negotiation-and-serialization-json-xml-jackson.md` | Content negotiation & serialization (JSON/XML, Jackson) | 2026-06-04 |

## 7. Open Decisions & TODOs

- [ ] **Generator status-awareness.** `scripts/generate_skeleton.py` always
      prints `planned`. Recommended fix: have it read each topic file's
      frontmatter `status` and reflect it in the index tables, so progress
      shows truthfully. Until then, this file is the source of truth.
- [x] **Cross-cutting sections** authoring phase — **decided 2026-06-04**:
      author L0's cross-cutting sections (C03–C09, 9 files total) right
      after L0 concept topics finish, before moving to L2+ in this
      session. (L1 concept topics are owned by a parallel session.)
- [x] Re-deepen `L0/C01/T03` to the raised DEPTH-CHECKLIST — **done** (181 → 320
      lines, 13 diagrams).
- [ ] Optional: give `L0/C01/T01` a light pass to confirm it meets the new
      diagram-per-concept bar (it already meets the spirit; user said it's good).

## 8. Session Log (append-only, newest first)

### 2026-06-04 (later — ⚠️ QUALITY FEEDBACK + C04 DEEPENING began; deepened T01)
- **User feedback (important):** "all the files in the C04 have the
  shallow depth and low quality of the information... include the more
  informations that is knowledgefull." This is the **2nd shallowness
  flag** (1st = L0 C03–C09 cross-cutting). Root cause: I under-deliver
  depth on "design/web/tooling" topics — C04 came out ~190-227 ln vs the
  400-600-ln core bar. **Recorded a new memory** `feedback_no-shallow-
  non-core-topics.md` (+ MEMORY.md pointer): web/REST/design/tooling
  topics MUST hit the same deep, expert-dense bar — exact RFCs, full
  enumerations, edge cases, security/CVE classes, perf specifics, worked
  examples; ~200 ln = too thin. Refines `feedback_topic-depth-memory-
  architecture` (layers must be RICHLY filled, not just present).
- **Remediation:** re-deepening C04 T01–T04 **one file per round**.
  **Counts UNCHANGED (66/371)** — already complete; this is quality
  remediation, not new topics.
- **Done this round — `L2/C04/T01` deepened 218 → 344 ln** (~4.3k words,
  5 tables, 2 diagrams). Added: **HTTP message grammar** (RFC 9112 start-
  line/headers/CRLF/body; HTTP/2-3 binary framing + pseudo-headers);
  **full method matrix** (+TRACE/CONNECT, safe/idempotent/cacheable
  precise defs, **PUT-upsert→201**, **PATCH idempotency** JSON-Merge-Patch
  RFC7386 vs JSON-Patch RFC6902, method-override/tunneling anti-pattern,
  idempotency-keys→effectively-once); **complete status vocab** (100+
  Expect/101-WebSocket, 201+Location/202/204/206, **301/302/303/307/308**
  permanent×method-preservation table, 4xx 400/401/403/404/405/406/409/
  410/412/415/422/428/429/451, 5xx **500/502/503/504 distinctions**);
  **headers by role** (Host/virtual-hosting, Content-Type media-type
  structure +suffix, **Content-Length vs Transfer-Encoding**, full
  **Cache-Control directive table** max-age/s-maxage/no-cache/no-store/
  private/public/must-revalidate/immutable/stale-while-revalidate, **ETag
  weak/strong**, **Vary as cache-key**, Authorization schemes, **X-
  deprecated RFC6648 / Forwarded RFC7239**); **conditional requests** full
  If-* family + If-Range + weak/strong validators + optimistic-concurrency
  412 diagram; **CORS** simple-vs-preflighted exact criteria + all
  Access-Control-* headers + credentials/wildcard-forbidden + Max-Age/
  Expose-Headers + browser-only clarification; **NEW: message-framing
  security** (request smuggling CL.TE/TE.CL, response splitting, Host
  injection); architecture (semantics-as-contract deeper, at-least-once→
  effectively-once, headers-as-perf-lever, smuggling). **14 INTERVIEW,
  16 Practice.** Cross-links C03/T02/T05/T06/T07/T08/T09/T10.
### 2026-06-04 (later — C04 deepening cont'd: T02 REST principles)
- Deepened `L2/C04/T02` REST principles 227 → **248 ln / 3.6k words**
  (~48% more content). Added: Fielding's **derivation** (null-style +
  one constraint at a time, each a property-for-a-cost); the architectural
  properties REST maximizes; **per-constraint property+cost** (client-
  server, stateless [+ the re-sent-context cost], cacheable [+ stale
  risk], uniform [+ efficiency cost], layered [+ latency], code-on-demand
  [+ visibility → why optional]); the **4 uniform-interface sub-
  constraints** (resource-as-membership-function, representations,
  **self-descriptive messages → visibility for intermediaries**, HATEOAS);
  **hypermedia formats** HAL/JSON:API/Siren/Hydra +HAL example +IANA link-
  rels; Richardson **L0-L3 each with an example**; **worked Level-2 orders
  API** table (controller sub-resource for non-CRUD actions, relationship-
  as-sub-resource, negotiation-as-uniform-interface); deeper **REST vs
  gRPC vs GraphQL** table (protobuf/streaming/no-HTTP-caching, query-
  language/N+1/caching-hard) + HTTP-2-mitigates-chattiness; RFC 7807
  problem+json full structure; architecture (statelessness-cost-explicit,
  uniform-interface-efficiency-trade-off, constraints↔C03-infra). 14
  INTERVIEW, 14 Practice. Counts unchanged (66/371).
### 2026-06-04 (later — C04 deepening cont'd: T03 API design)
- Deepened `L2/C04/T03` API design 197 → **231 ln / 3.1k words** (~48%
  more). Added: resource-modeling **security** (IDOR via sequential IDs →
  opaque UUID/ULID + per-object authz; **mass assignment** → DTO binding;
  own WARNING); the **breaking-change taxonomy** table (breaking vs
  additive, incl. the enum-value/tolerant-reader nuance); versioning
  strategies table +**who-uses-which** (URI/most, media-type-header/
  GitHub, date-header/Stripe, query) +semver-major-in-URI +**consumer-
  driven contracts (Pact)** +`Deprecation`/`Sunset` RFC8594; pagination
  offset row-walk + **write-inconsistency concrete example**, cursor
  **composite tiebreaker** `(created_at,id)` + opaque base64 cursors +
  `Link` header RFC8288 + the `COUNT(*)` total cost; **filter query
  languages** RSQL/FIQL/OData/JSON:API + allowlist + expensive-query DoS;
  **rate-limit algorithms** table (fixed/sliding/token-bucket/leaky) +
  429/Retry-After/RateLimit-*; full **problem+json** structure +207/202;
  OpenAPI; architecture (API-as-contract + consumer-driven, cursor O(log
  n) composite-index seek vs offset O(n), cache-key fragmentation +Vary,
  field-selection payload). 14 INTERVIEW, 14 Practice. Counts unchanged.
- Resume: deepen `L2/C04/T04` serialization (193 ln) — **the LAST C04
  deepening**; then checkpoint with the user (start C05, or resume L3).
  See §4 for the T04 plan.

### 2026-06-04 (later — C04 deepening DONE: T04 serialization → ✅ ALL 4 C04 FILES DEEPENED)
- Deepened `L2/C04/T04` serialization 193 → **234 ln / 3.1k words**
  (~50% more). Added: content-negotiation **selection algorithm** +
  proactive/reactive/suffix strategies; **the JSON data model & gotchas**
  (RFC 8259 — **53-bit number-precision trap** → serialize big IDs/money
  as strings; no date type/comments/NaN; UTF-8; dup-key UB); formats
  table +XML **XXE** +**Protobuf schema evolution** (field-numbers/add-
  only/reserved = additive-compat at the format level); **Jackson
  architecture** (JsonFactory→parser→ObjectMapper) + **full annotation
  catalog table** (@JsonProperty/Alias/Ignore/Include/Format/Creator/
  AnyGetter/Unwrapped/**@JsonView**/Managed-Back-Reference/TypeInfo) +
  **mix-ins** + Ser/Deser-Features + naming-strategies + modules +records;
  3 processing models (streaming O(1)-memory/tree/databind + hybrid);
  **reflection cost** (BeanSer/Deser cache → reuse singleton; **Blackbird**
  via LambdaMetafactory — L2/C01/T01 invokedynamic callback); serialization-
  as-contract (name decoupling); payload (gzip/br/binary, C03/T05);
  **deserialization SECURITY — the gadget-chain mechanism explained**
  (untrusted JSON names a class whose construction side-effects → RCE;
  same family as native ObjectInputStream + Log4Shell JNDI) + defense
  (**PolymorphicTypeValidator allowlist**, default-typing-off, patch
  C02/T11, XXE/JEP-290); Java mapping (Spring HttpMessageConverter +
  produces/consumes, DTO-not-entity + JPA lazy-load/open-session-in-view
  anti-pattern L2/C05 fwd). 14 INTERVIEW, 14 Practice. Counts unchanged.
- **✅ C04 DEEPENING COMPLETE — all 4 files at the deep bar** (T01 344/
  4.3kw, T02 248/3.6kw, T03 231/3.1kw, T04 234/3.1kw, from 218/227/197/
  193). Quality remediation done. **CHECKPOINT: asked the user re
  direction — only C05 Databases & SQL [9] remains to finish L2 (44/44),
  or resume L3. Apply the new no-shallow depth bar going forward.**

### 2026-06-04 (later — L2/C04/T04 Content negotiation & serialization — ✅ C04 COMPLETE 4/4 — L2 hit 80%)
- One topic this round. `L2/C04/T04` Content negotiation & serialization
  (JSON/XML, Jackson) — **193 lines, 2 Mermaid diagrams (content
  negotiation + 3-processing-models) + JSON/XML/binary table + Jackson
  code** at the deep bar. **The LAST C04 topic → chapter COMPLETE 4/4.**
  **Language layer**: **content negotiation** (diagram — client Accept/
  Accept-Language/Accept-Encoding T01 → server picks representation + sets
  Content-Type; q-values; server-driven; **406** Not Acceptable). **
  Serialization** (object↔wire bytes); **JSON vs XML vs binary** table
  (JSON text/JS-native web-default; XML verbose/legacy/SOAP; binary
  Protobuf compact/schema'd gRPC T02). **Jackson** (code) — **ObjectMapper**
  readValue/writeValue; annotations (@JsonProperty rename / @JsonIgnore /
  @JsonInclude / @JsonFormat / @JsonTypeInfo); modules (JavaTimeModule,
  records L1/C01/T14); Gson/JSON-B alts. **Tolerant reader**
  (@JsonIgnoreProperties ignoreUnknown → T03 backward-compat made concrete
  — old clients survive added fields). **Architecture layer**: **the 3
  processing models** (diagram — **streaming** JsonParser token-by-token
  low-memory for huge docs / **tree** JsonNode whole-doc-in-memory flexible
  / **data-binding** POJO convenient — the memory-vs-convenience axis);
  **reflection cost** (Jackson reflects by default + caches per-type →
  **REUSE the thread-safe ObjectMapper singleton**; per-request creation =
  #1 perf bug; afterburner/blackbird codegen); **serialization IS the
  contract** (T02/T03 — JSON shape = the contract; @JsonProperty
  **decouples** Java↔wire names = C02/T08-T09 codegen echo → why serialize
  a **DTO not entity** T03); **payload** (JSON-text bigger than binary;
  **gzip** Content-Encoding ~70% C03/T05 cost); **deserialization
  SECURITY** (polymorphic deser / enableDefaultTyping → untrusted JSON
  picks a gadget class → **RCE**, the Jackson-CVE family — C02/T11 echo;
  never on untrusted input). **Java**: Spring auto-Jackson (@RestController
  POJO→JSON, @RequestBody JSON→obj via HttpMessageConverter); inject the
  ObjectMapper singleton; records as DTOs (L1/C01/T14); DTO-not-entity
  (T03 — avoids DB-schema leak + JPA lazy-load/LazyInitializationException
  traps, L2/C05 fwd). IMPORTANT=reuse-ObjectMapper; WARNING=never-deser-
  untrusted-polymorphic (RCE); TIP=tolerant-reader + DTO-not-entity.
  **Common mistakes** (8): ObjectMapper-per-request, unhandled-unknowns,
  entity-serialization, polymorphic-untrusted-deser, date-chaos, OOM-
  payloads, circular-refs, name-coupling. **INTERVIEW** 12 Q. **Practice
  (13)** — round-trip POJO+record, negotiate JSON-vs-XML, tolerant reader,
  stream huge array, custom serializer, @JsonProperty decouple, reuse-cost
  measure, java.time, forward-compat, DTO-vs-entity lazy-trap, circular-
  ref, security reasoning, explain-it-back. Recap ~5 + **chapter-completion
  note + Next → C05 README**. Progress 65 → 66/371 (17.5% → 17.8%); L2 row
  34/44 → 35/44 (80%); **C04-web-rest 3/4 → 4/4 COMPLETE ✅**. Wired C04
  README (T04 + frontmatter→complete), L2 README (C04 row→complete).
- **✅ MILESTONE: L2/C04 Web & REST Basics COMPLETE (4/4).** Full web-API
  layer: HTTP semantics in depth (T01), REST principles (T02), API design
  (T03), serialization (T04). L2 now **35/44 (80%)** — C01 (9/9) + C02
  (11/11) + C03 (11/11) + C04 (4/4) done; **only C05 Databases & SQL [9]
  remains → then L2 COMPLETE (44/44).**
- **⚠️ CHECKPOINT: asked the user about next direction (C05 Databases &
  SQL to finish L2, or resume L3). Do NOT auto-start — await the user's
  choice.** See §4.

### 2026-06-04 (later — L2/C04/T03 API design)
- One topic this round. `L2/C04/T03` API design (resources, versioning,
  pagination, filtering) — **197 lines, 2 Mermaid diagrams (versioning
  breaking-vs-additive + offset-scan-vs-index-seek) + versioning/offset-
  vs-cursor tables** at the deep bar (design topic, lighter §4a). The
  practical craft applying T01 semantics + T02 REST principles.
  **Language layer**: **resource modeling** (granularity, collections/
  members/sub-resources T02, **embed-vs-link** relationships, avoid deep
  nesting). **Versioning** (table URI/header/query; **version ONLY on
  breaking changes**; **backward-compat** = add-don't-remove/rename +
  tolerant-reader + Sunset deprecation; diagram additive→no-version vs
  breaking→version). **Pagination** (table offset/limit vs cursor/keyset;
  offset simple-but-O(n)-deep-and-unstable vs cursor O(log n)-index-seek-
  and-stable = the modern default; page metadata/HATEOAS T02). **Filtering/
  sorting/sparse-fieldsets** (`?fields=` = REST's answer to GraphQL over-
  fetch T02; C03/T05 payload). **Other** (bulk/207, async 202 T01,
  idempotency T01, 429 T01, problem+json T02, **OpenAPI** = contract made
  explicit). **Architecture layer**: **the API IS a CONTRACT** (T02
  uniform-interface — payload shape as binding as URLs/status → additive-
  only evolution → why a rename is an outage; "semantics-are-the-API"
  applied to payload shape); **why cursor pagination scales** (diagram +
  WARNING — offset `OFFSET 1000000` = scan+discard 1M rows O(n) vs cursor
  `WHERE id>cursor` = index seek O(log n), constant-cost-per-page, stable;
  **L2/C05 DB-index forward**); **cache-ability of design choices** (C03/
  T10 — URI versioning + stable URLs cache well; query-param explosion
  fragments the cache key/Vary); **field selection = payload/cost lever**
  (C03/T05). IMPORTANT=API-as-contract (additive-only); WARNING=no-offset-
  on-deep-datasets (use cursor); TIP=additive-changes + OpenAPI + Sunset.
  **Common mistakes** (8): no-versioning, over-versioning, offset-on-deep,
  unbounded-collections, deep-nesting, undeprecated-breaking, inconsistent-
  syntax, exposing-DB-internals. **INTERVIEW** 12 Q. **Practice (13)** —
  design collection API, version a breaking change, offset-vs-cursor +
  measure deep-page cost, offset-inconsistency demo, filter/sort/fields,
  backward-compat field add, problem+json, OpenAPI snippet, embed-vs-link,
  async 202, critique, cache impact, explain-it-back. Recap ~6. Cross-
  links T01/T02, C03/T05/T10; fwd T04, L2/C05. Progress 64 → 65/371
  (17.3% → 17.5%); L2 row 33/44 → 34/44 (77%); C04-web-rest 2/4 → 3/4
  (only T04 left). Wired C04 README.
- Resume at `L2/C04/T04` — Content negotiation & serialization (JSON/XML,
  Jackson); **the LAST C04 topic → C04 COMPLETE 4/4 after; then only C05
  remains in L2.** Content negotiation (Accept→Content-Type T01, Accept-
  Language/Encoding, q-values, 406); JSON vs XML vs binary (gRPC T02);
  **Jackson** (ObjectMapper readValue/writeValue, annotations @JsonProperty/
  Ignore/Include/Format/TypeInfo, modules JavaTimeModule/records-L1/C01/T14);
  tolerant reader (@JsonIgnoreProperties ignoreUnknown → T03 backward-compat
  concrete); architecture = **3 Jackson processing models** (streaming/tree/
  data-binding — memory-vs-convenience), **reflection cost** (reuse the
  thread-safe ObjectMapper singleton! afterburner/codegen), **serialization-
  as-contract** (JSON shape IS the contract T02/T03; field-name mapping
  decouples Java↔wire, C02 codegen echo), payload (JSON-vs-binary + gzip,
  C03/T05), **deserialization security** (polymorphic-deser RCE / Jackson
  CVEs / never deserialize untrusted polymorphic JSON — C02/T11 vuln echo);
  Java = Spring auto-Jackson + DTO-not-entity (T03 leaky-abstraction + JPA
  lazy-load serialization traps L2/C05 fwd). §4 full. **After T04 → C04
  done (4/4), L2 = 35/44; only C05 DB&SQL [9] left → then L2 COMPLETE.
  Checkpoint with the user after T04.**

### 2026-06-04 (later — L2/C04/T02 REST principles & best practices — L2 hit 75%)
- One topic this round. `L2/C04/T02` REST principles & best practices —
  **227 lines, 2 Mermaid diagrams (constraints→properties + constraints↔
  C03-infra synthesis) + RPC-vs-REST/REST-vs-RPC-vs-GraphQL tables** at
  the deep bar (design topic, lighter §4a). The architectural style built
  on T01 HTTP semantics. **Language layer**: REST = Fielding's
  architectural **STYLE** (constraints, NOT a protocol/standard →
  "RESTful" is a spectrum). **The 6 constraints** (diagram→properties):
  client-server, **stateless** (self-contained req, no server client
  context — C03/T05/T07 → scaling C03/T09), **cacheable** (T01 headers →
  CDN C03/T10), **uniform interface**, **layered-system** (proxies/LBs/
  CDNs transparent C03/T08-T10), code-on-demand. **Uniform interface**
  (the heart, 4 sub) — resources-via-URIs + manipulation-via-
  representations (JSON T04) + self-descriptive-messages + **HATEOAS**.
  **Resources-not-verbs** (table RPC `/getUser` vs REST `GET /users/5`;
  collections/members/sub-resources; CRUD→methods T01). **HATEOAS +
  Richardson Maturity Model** (L0 RPC → L1 resources → L2 verbs+status
  [most APIs] → L3 HATEOAS). **REST vs RPC vs GraphQL** (table — resource
  vs action vs query-language; when each). **Best practices** (naming/
  methods+status T01/statelessness-tokens C03-T07/versioning T03/
  pagination T03/idempotency T01/**RFC 7807 problem+json**). **Architecture
  layer (the depth)**: **statelessness as the scaling property** (C03/T05/
  T07/T09 — any server any request → horizontal scale + cacheable; the
  recurring theme elevated to a principle); **the uniform interface as a
  DECOUPLING CONTRACT** (T01 semantics-as-contract — client/server/
  intermediaries share HTTP generic semantics → evolve independently +
  a cache/proxy/CDN handles ANY REST API generically without knowing the
  domain → **WHY REST scales to the whole web**); **constraints MAP onto
  the C03 infra** (diagram + IMPORTANT — stateless→LB-routes-freely C03/T9,
  cacheable→CDN C03/T10, layered→proxy-transparent C03/T08; the synthesis
  — REST's constraints ARE the properties that make the C03 edge infra
  possible). WARNING=resources-nouns-methods-verbs (not RPC-in-URL); TIP=
  don't-be-dogmatic (most APIs L2; gRPC/GraphQL have their place).
  **Common mistakes** (8): verbs-in-URIs, statefulness, ignoring-HTTP-
  semantics, over/under-HATEOAS, chatty-n+1, inconsistent-naming, POST-
  tunneling, forcing-REST-where-it-doesn't-fit. **INTERVIEW** 12 Q.
  **Practice (13)** — model an API, de-RPC it, CRUD mapping, HATEOAS,
  make-stateless, Richardson level, naming, problem+json, chattiness,
  critique, map-to-infra, choose-a-style, explain-it-back. Recap ~6.
  Cross-links T01 + C03/T05/T07/T08/T09/T10; fwd C04/T03/T04. Progress
  63 → 64/371 (17.0% → 17.3%); **L2 row 32/44 → 33/44 (75%)**; C04-web-rest
  1/4 → 2/4. Wired C04 README.
- Resume at `L2/C04/T03` — API design (resources, versioning, pagination,
  filtering); the practical craft applying T01 semantics + T02 REST
  principles. Resource modeling (granularity, embed-vs-link, avoid deep
  nesting); **versioning** (URI vs header vs query; version-only-on-
  breaking-changes; backward-compat/tolerant-reader/deprecation);
  **pagination** (offset/limit vs **cursor/keyset** — why cursor scales,
  L2/C05 DB-index fwd); filtering/sorting/**sparse-fieldsets** (the REST
  answer to GraphQL over-fetch); async 202 (T01), idempotency (T01),
  problem+json (T02), OpenAPI; architecture = **API-as-a-CONTRACT** (T02
  uniform-interface — additive-only/backward-compat) + cursor-pagination-
  performance (offset O(n)-skip vs keyset O(log n)-index, L2/C05 fwd) +
  cache-ability of design choices (C03/T10) + field-selection-payload
  (C03/T05 cost). §4 full (lighter §4a). **After T03 + T04 → C04 COMPLETE
  (4/4); only C05 DB&SQL [9] left to finish L2.**

### 2026-06-04 (later — L2/C04/T01 HTTP in depth — C04 STARTED — 🎯 crossed 17%)
- **User chose L2/C04 Web & REST next** (at the C03-completion checkpoint,
  via AskUserQuestion — picked C04 over C05/L3). One topic this round.
  `L2/C04/T01` HTTP in depth (methods, status, headers) — **218 lines,
  2 Mermaid diagrams (optimistic-concurrency sequence + CORS preflight
  sequence) + methods/status/headers tables** at the deep bar.
  **DELIBERATELY DEEPER than C03/T05** (which did the network-lifecycle
  view) — this is the **API-engineering** view: semantics-as-contract,
  idempotency keys, optimistic concurrency, CORS (no repetition; T05 is
  a prereq/cross-link). **Language layer**: **methods in depth** (table
  w/ safe+idempotent+cacheable) as **CONTRACTS** (safe→prefetch/crawl;
  idempotent→retry-safe for clients/proxies/LBs C03/T09); **PUT vs POST
  vs PATCH** (idempotent-full-replace vs create/non-idempotent vs
  partial); **idempotency keys** (Idempotency-Key header + dedupe → make
  POST retriable, Stripe pattern). **Status codes in depth** (table w/
  when-to-use; **401 vs 403** = authn-who-are-you vs authz-you-cant;
  409/422/429; why codes matter to caches/proxies/LBs/clients/monitoring).
  **Headers in depth** (table grouped: content/negotiation/auth/caching/
  CORS/rate-limit/forwarding). **Conditional requests** — ETag/If-None-
  Match→**304** (caching C03/T05/T10) AND **If-Match→optimistic
  concurrency/412** (prevent lost updates — the deeper API use, sequence
  diagram); Range→206. **CORS** — same-origin policy + Access-Control-
  Allow-* opt-in + **preflight** (OPTIONS, sequence diagram); the key
  clarification: **browser-enforced + browser-ONLY, NOT server-side
  security** (curl/server-to-server ignore it). **Architecture layer**:
  **HTTP semantics as a CONTRACT** the whole infra relies on (caches/CDNs
  C03/T10 cache by status+Cache-Control+Vary; proxies/LBs C03/T08-T09
  route/retry/health-check by method/status; clients branch on families)
  → a wrong status/non-idempotent-GET makes infra MISBEHAVE (CDN caches
  a failure, proxy double-charges) → **the semantics ARE the API**;
  idempotency+retries (at-least-once → idempotency keys bridge); correct
  headers = a performance lever (304 avoids bytes, T05 cost model).
  IMPORTANT=semantics-are-a-contract; WARNING=401-vs-403 + never-GET-for-
  mutations; TIP=idempotency-key + If-Match optimistic concurrency.
  **Common mistakes** (8): GET-for-mutations, 200-for-everything, 401/403
  confusion, PUT/POST/PATCH misuse, no-idempotency-keys, wrong-cache-
  headers, CORS-as-security, ignored-conditional-requests. **INTERVIEW**
  12 Q. **Practice (14)** — methods via curl, idempotency PUT-vs-POST,
  idempotency-key retry, 401-vs-403, 304, optimistic-concurrency 412,
  negotiation, CORS preflight + curl-ignores, 429+Retry-After, Range 206,
  status discipline, caching, wrong-status demo, explain-it-back. Recap
  ~6. Cross-links C03/T05/T06/T07/T08/T09/T10; fwd C04/T02/T03/T04.
  Progress 62 → 63/371 (**16.7% → 17.0% — crossed 17%**); L2 row 31/44 →
  32/44 (73%); **C04-web-rest 0/4 → 1/4 STARTED**. Wired C04 README (T01 +
  frontmatter→in-progress).
- Resume at `L2/C04/T02` — REST principles & best practices (the
  architectural style; builds on C04/T01 HTTP semantics). Fielding's 6
  constraints (client-server, **statelessness** C03/T05/T07, cacheability
  C04/T01, **uniform interface** [resources+representations+methods-as-
  verbs+HATEOAS], layered-system C03/T08-T10, code-on-demand); resources-
  not-verbs (nouns + HTTP verbs vs RPC verb-in-URL); HATEOAS + Richardson
  Maturity Model; REST vs RPC vs GraphQL; best practices (naming/status/
  versioning-T03/pagination-T03/idempotency-T01/problem+json); architecture
  = statelessness-as-scaling (C03/T09) + uniform-interface-as-decoupling-
  contract (C04/T01 semantics-as-contract) + REST-constraints-map-to-the-
  C03-infra. §4 full (lighter §4a — design topic; statelessness-scaling +
  uniform-interface-decoupling is the anchor).

### 2026-06-04 (later — L2/C03/T11 Firewalls & NAT — ✅ C03 COMPLETE 11/11)
- One topic this round. `L2/C03/T11` Firewalls & NAT (basics) — **178
  lines, 1 Mermaid diagram (NAT translation + table) + firewall-types
  table** at the deep bar (a "basics" closer; ties up NAT promised in T03
  + firewall/DDoS from T08/T10). **The LAST C03 topic → chapter COMPLETE
  11/11.** **Language layer**: **Firewalls** — rule-based filtering,
  **default-deny** posture, ingress/egress; **types table** — stateless
  packet-filter (L3/L4 per-packet, no memory), **stateful** (tracks the
  connection 4-tuple/conntrack T02/T03 → return traffic auto-allowed),
  **L7/WAF** (inspects HTTP T05, blocks SQLi/XSS — T08/T10); host/network/
  cloud security-groups(stateful)+NACLs(stateless). **NAT** — translates
  private↔public IP (T03 ranges); **why = IPv4 exhaustion** (many hosts
  share one public IP); **how = PAT/masquerading** (NAT table maps
  internal IP:port ↔ public IP:port per connection/4-tuple T02; diagram).
  **The inbound asymmetry** (the key insight) — unsolicited inbound has
  NO table entry → dropped → NAT **blocks inbound by default**; →
  **port forwarding** to expose a service, can't-reach-a-private-IP (T03),
  **NAT breaks P2P** (both behind NAT → STUN/TURN/ICE hole-punching),
  IPv6 removes NAT but needs a real firewall. **Architecture layer**:
  **NAT table = connection state** (T02/T03 — per-conn 4-tuple in router
  memory; **entries time out** → idle keep-alive dies behind NAT, needs
  TCP keepalives T05/T02; **size limit** → scaling constraint, CGNAT);
  **stateful firewall = same conntrack idea**; per-packet **performance
  cost** (check/rewrite, hw offload); **NAT IS NOT A FIREWALL** (inbound-
  deny is a SIDE EFFECT not security — does nothing for outbound/app-layer/
  compromised-host → use a real default-deny firewall + defense in depth);
  the **addressing-story synthesis** (L3/L4/L7 filtering T01 + NAT
  translates IP/ports T03 + connection-as-state T02). **Java angle**:
  server behind NAT/firewall needs port-forward / public-IP / LB (T09) /
  reverse proxy (T08); **bind 0.0.0.0 AND open the firewall port** (both
  required — T03 bind-vs-firewall); outbound works (stateful), inbound
  needs opening; cloud SG (not 0.0.0.0/0); real client IP via XFF (T08).
  IMPORTANT=NAT-not-security; WARNING="app runs but unreachable"=firewall-
  or-bind (T03); TIP=stateful auto-allows outbound return traffic.
  **Common mistakes** (8): NAT-as-security, default-allow, no-egress-
  filter, bind-vs-firewall confusion, P2P-without-STUN, idle-keepalive-
  timeout, over-open-SG, wrong-firewall-layer. **INTERVIEW** 12 Q.
  **Practice (13)** — iptables/ufw rules, stateful-vs-stateless return
  traffic, NAT+port-forward, inspect conntrack table, cloud SG, bind-vs-
  firewall trap, egress block, WAF SQLi block, P2P/STUN reasoning,
  keepalive timeout, unreachable-private-IP, default-deny conversion,
  explain-it-back. Recap ~6 + **chapter-completion note + Next → C04
  README**. Progress 61 → 62/371 (16.4% → 16.7%); L2 row 30/44 → 31/44
  (70%); **C03-networking 10/11 → 11/11 COMPLETE ✅**. Wired C03 README
  (T11 + frontmatter→complete), L2 README (C03 row→complete).
- **✅ MILESTONE: L2/C03 Networking & Web Fundamentals COMPLETE (11/11).**
  Full stack: OSI/TCP-IP + encapsulation, TCP/UDP, IP/ports/sockets, DNS,
  HTTP/HTTPS lifecycle, TLS, cookies/sessions/tokens, and the edge-infra
  arc (reverse proxies → load balancers → CDNs → firewalls & NAT). L2 now
  31/44 (70%) — C01 (9/9) + C02 (11/11) + C03 (11/11) done; **only C04
  Web & REST [4] + C05 DB & SQL [9] remain to finish L2**.
- **⚠️ CHECKPOINT: asked the user about next direction (C04 Web & REST
  next in curriculum order, or C05 Databases & SQL, or resume L3). Do NOT
  auto-start the next chapter — await the user's choice.** See §4.

### 2026-06-04 (later — L2/C03/T10 CDNs — edge-infra arc finale)
- One topic this round. `L2/C03/T10` CDNs — **202 lines, 3 Mermaid
  diagrams (near-edge-vs-distant-origin + reaching-the-edge anycast/DNS +
  cache hierarchy) + callouts** at the deep bar (synthesis topic →
  diagrams/prose over tables). The **T08→T09→T10 edge-infra arc FINALE**
  — a CDN = globally-distributed caching reverse proxy (T08) + anycast LB
  (T09/T04); synthesizes T04 DNS/anycast + T05 caching + T06 TLS + T08
  proxy + T09 consistent-hashing. **Language layer**: **why CDN —
  distance IS latency** (T05 RTT — speed of light ~5ms/1000km; Tokyo↔
  Virginia ~110ms min × several RTTs; diagram near-edge-hit vs distant-
  origin) → edge servers/PoPs serve cached content from the nearest →
  lower latency + origin offload + DDoS absorption. **What it caches/
  does** — static assets, edge caching (T05 Cache-Control/ETag), origin
  offload (95% hit = origin sees 5%), TLS at the edge (T06), compression,
  **edge compute** (Workers/Lambda@Edge), DDoS/WAF (T11). **Reaching the
  nearest edge** (diagram) — **anycast** (one IP, BGP→nearest) +/or
  **DNS geo-steering** (CNAME to CDN, geo-aware answers) — T04. **Cache
  mechanics** — hit/miss/revalidate (304 T05), TTL, **cache key/Vary**
  (over-varying → fragmentation), **invalidation: purge vs versioned-URL
  cache-busting** (app.[hash].js → new version=new URL, old cached
  forever immutable — preferred), origin shield. **Architecture layer**:
  **distance-is-latency physics** (can't beat propagation in software —
  shorten distance [CDN] or cut RTTs [keep-alive/H2-3/TLS1.3]); **cache
  hierarchy** (diagram — browser→edge→shield→origin, miss falls through);
  **cache-hit-ratio economics** (the core metric — 95%→99% cuts origin
  5×); **consistent hashing** (T09 — URL→cache node, minimal reshuffle,
  affinity); **push vs pull** (pull lazy-fill common, push pre-upload);
  **static vs dynamic** (static cacheable; personalized NOT at shared
  edge → short-TTL/private/edge-compute); **the synthesis** (anycast/DNS
  T04 → reverse-proxy edge T08 → LB+cache T09/T05 → TLS T06). **Java
  angle**: Cache-Control (public+max-age+immutable for versioned assets;
  no-store/private for personalized T07) + ETag; versioned asset URLs
  (cache-bust); origin sees misses + CDN IPs → X-Forwarded-For (T08);
  **NEVER cache personalized at a shared edge**. IMPORTANT=CDN-defeats-
  distance (can't-optimize-in-software); WARNING=never-cache-personalized
  (data leak, T07); TIP=versioned-URLs over purge. **Common mistakes**
  (8): cache-personalized-leak, no-cache-busting, wrong-headers-0%-hit,
  cache-key-explosion, origin-still-needs-capacity, purge-only, not-using-
  CDN-for-TLS/DDoS, static-vs-dynamic-misclassify. **INTERVIEW** 12 Q.
  **Practice (13)** — front a site + HIT/MISS header, hit-ratio, cache-
  bust, distance latency, traceroute to edge (anycast), ETag 304,
  never-cache-private, Vary fragmentation, origin offload load-test,
  edge-compute hello-world, push-vs-pull, classify endpoints, explain-it-
  back. Recap ~6. Cross-links T04/T05/T06/T07/T08/T09; Next→T11. Progress
  60 → 61/371 (16.2% → 16.4%); L2 row 29/44 → 30/44 (68%); C03-networking
  9/11 → 10/11 (only T11 left). Wired C03 README.
- Resume at `L2/C03/T11` — Firewalls & NAT (basics); **the LAST C03
  topic → C03 COMPLETE 11/11 after, then CHECKPOINT with user.**
  Firewalls (stateless packet-filter L3/L4, **stateful** connection-
  tracking via 4-tuple/conntrack T02/T03, L7/WAF inspects HTTP T05/T08/
  T10; default-deny; security groups); **NAT** (private↔public T03,
  IPv4-exhaustion why, NAT-table per-connection 4-tuple mapping/PAT,
  **inbound-blocked-by-default side effect** → port forwarding, breaks
  P2P→STUN/TURN); architecture = NAT-table-as-connection-state (T02/T03,
  timeouts/size limits), stateful-firewall=conntrack, **NAT-as-accidental-
  firewall** (not real security), CGNAT; Java = server behind NAT/firewall
  needs port-forward/public-IP/LB (T09), bind 0.0.0.0 + open the firewall
  port (T03 bind-vs-firewall), X-Forwarded-For behind NAT+proxy (T08).
  §4 full — stateful-tracking + NAT-table-as-state + inbound-deny-side-
  effect + L3/L4/L7-filtering mechanism. **After T11 → C03 done (11/11);
  ASK USER re direction (C04 Web&REST [4] / C05 DB&SQL [9] / resume L3).**

### 2026-06-04 (later — L2/C03/T09 Load balancers — 60 topics)
- One topic this round. `L2/C03/T09` Load balancers — **220 lines, 3
  Mermaid diagrams (LB fan-out + health-check/failover + consistent-
  hashing ring) + L4-vs-L7/algorithms tables** at the deep bar. The T08
  reverse-proxy "load balancing" job deep-dived; the horizontal-scaling
  enabler (T07). **Language layer**: **why LB** (diagram — scalability
  [N instances] + availability [health-check out the dead] + zero-
  downtime deploys). **L4 vs L7** table (T08 axis — L4 TCP/IP:port,
  fast/protocol-agnostic/pins-a-connection/NLB vs L7 reads-HTTP/route-by-
  path-host-cookie/TLS-terminate/per-request/ALB). **Algorithms** table
  (round-robin/weighted/least-conn/least-response/IP-hash/consistent-hash/
  power-of-two-choices). **Sticky sessions** (T07 — pin user to a backend
  for in-memory session; a **crutch** that breaks failover/rebalancing →
  prefer stateless: shared store/JWT). **Health checks + failover**
  (diagram — active /health + passive; remove unhealthy; graceful drain →
  zero-downtime). **Where LBs live** (HW F5/software HAProxy-Nginx-Envoy/
  cloud ELB-ALB[L7]-NLB[L4]/**DNS LB** T04/**anycast** T04; layered:
  anycast→DNS→regional LB→L7 LB→backends). **Architecture layer**:
  **two-connection + DSR** (T08 — L4 can let the response bypass the LB
  straight to client, for high-throughput downloads); **connection-vs-
  request balancing** (L4 pins a whole TCP conn → with keep-alive/HTTP-2
  T05 all requests hit the same backend = uneven; L7 balances per
  request); **the stateless-backend requirement** (T07 — any backend
  serves any request = the scaling enabler; sticky = workaround);
  **LB-as-new-choke-point** (T08 — moves the SPOF; make it redundant
  active-active/passive + DNS/anycast T04); **consistent hashing**
  (diagram — keys+nodes on a ring → add/remove moves only ~1/N keys vs
  hash%N reshuffling all → cache affinity T10, sharding). **Java angle**:
  stateless apps (T07) or shared Redis; X-Forwarded-For real client IP
  (T03/T08); Actuator /health (check real deps!); graceful shutdown
  (SIGTERM→readiness→drain→exit). IMPORTANT=scalable+available-but-only-
  if-stateless (sticky=crutch); WARNING=LB-is-new-SPOF (redundant+meaningful
  health checks); TIP=L4+keep-alive pins connection→use L7 for per-request,
  DSR for downloads. **Common mistakes** (8): sticky-masking-non-stateless,
  shallow-health-checks, unmonitored-SPOF, wrong-layer, connection-vs-
  request-ignorance, no-draining, hash%N, under-provision/thundering-herd.
  **INTERVIEW** 12 Q. **Practice (14)** — HAProxy/Nginx across instances,
  algorithm compare, kill-backend failover, sticky+lost-session→Redis fix,
  L4-vs-L7, keep-alive pinning, XFF, Actuator health, graceful drain,
  consistent-hashing key-movement, DNS LB, power-of-two, N+1 sizing,
  explain-it-back. Recap ~6. Cross-links T01-T08; fwd T10. Progress 59 →
  **60/371 (15.9% → 16.2%)**; L2 row 28/44 → 29/44 (66%); C03-networking
  8/11 → 9/11. Wired C03 README.
- Resume at `L2/C03/T10` — CDNs (geo-distributed caching; the
  T08→T09→T10 edge-infra arc FINALE — a CDN = globally-distributed
  caching reverse proxy + anycast LB). **distance-IS-latency** physics
  (T05 RTT — move the server near the user; edge servers/PoPs); what a
  CDN caches/does (static assets, edge caching T05 Cache-Control/ETag,
  origin offload, TLS T06, edge compute, DDoS/WAF); reaching the nearest
  edge via **anycast**/**DNS steering** (T04); cache mechanics (hit/miss/
  revalidate 304, TTL, **invalidation: purge vs versioned-URL cache-
  busting**, cache key, origin shield); architecture = distance-is-latency
  + cache hierarchy (browser→edge→shield→origin) + **cache-hit-ratio**
  economics + push-vs-pull + **consistent hashing** (T09) + static-vs-
  dynamic; Java = Cache-Control/ETag headers + versioned URLs + never-
  cache-personalized (Cache-Control: private, T07). §4 full — SYNTHESIZES
  T04 DNS/anycast + T05 caching + T08 reverse proxy + T09 LB.
  **After T10, only T11 Firewalls & NAT remains → C03 complete (11/11) —
  checkpoint with the user on direction.**

### 2026-06-04 (later — L2/C03/T08 Proxies & reverse proxies)
- One topic this round. `L2/C03/T08` Proxies & reverse proxies — **191
  lines, 2 Mermaid diagrams (forward-vs-reverse + two-connection model) +
  reverse-proxy-jobs table + callouts** at the deep bar. Intermediaries
  between client/server (T05). **Language layer**: **forward proxy**
  (fronts CLIENTS — egress/filter/cache/privacy, client-configured,
  hides the client) vs **reverse proxy** (fronts SERVERS — Nginx/HAProxy/
  Envoy, client thinks it's the origin, hides the server) [diagram];
  mnemonic forward=hides-who's-asking, reverse=hides-who's-answering.
  **What a reverse proxy does** (table) — TLS termination (T06), load
  balancing (T09-fwd), caching (T05/T10-fwd), compression, **routing**
  (path/host → API-gateway role), rate-limit/WAF, header manipulation
  (XFF), buffering, static files. **Forward proxy** — egress/filter/
  privacy + **CONNECT** method (T05/T06 — raw TCP tunnel for HTTPS it
  can't decrypt). **API gateway** = reverse proxy for microservices
  (auth T07/routing/rate-limit/aggregation). **Architecture layer**:
  **L7 intermediary** (reads HTTP — route/cache/rewrite) vs **L4
  byte-forwarder** (forwards TCP bytes, protocol-agnostic) — **the
  L4-vs-L7 distinction** (T01, the axis of T09); **the two-connection
  model** (diagram — proxy TERMINATES client TCP + ORIGINATES a separate
  backend TCP, T02/T03 → backend keep-alive pooling [C10k mitigation],
  backend sees PROXY IP not client → **X-Forwarded-For**, buffering);
  **TLS-termination architecture** (T06 — certs at edge, plain HTTP to
  backend → why the Java app sees HTTP); **single choke-point trade-off**
  (SPOF + bottleneck → needs redundancy [multi-proxy + upstream LB/DNS/
  anycast T09/T10] BUT = the one place to centralize TLS/auth/rate-limit/
  cache/routing); transparent vs explicit. **Java angle**: app sits
  BEHIND a reverse proxy → read client IP from XFF not socket (T03),
  trust X-Forwarded-Proto for scheme (T06), Spring ForwardedHeaderFilter.
  IMPORTANT=forward-fronts-client/reverse-fronts-server; WARNING=XFF
  spoofable (trust only from your proxy that strips inbound); TIP=know
  where TLS terminates + what the backend sees. **Common mistakes** (8):
  forward/reverse confusion, blind-XFF-trust, forgetting-TLS-termination,
  lost-client-IP, unmonitored-SPOF, double-caching, proxy/backend
  mismatches, proxy≠load-balancer conflation. **INTERVIEW** 12 Q.
  **Practice (13)** — Nginx reverse proxy in front of a Java app, TLS-
  terminate, XFF logging, path routing, caching+gzip, rate-limit 429,
  Squid forward proxy, CONNECT tunnel, L4-vs-L7, XFF spoof+strip, Spring
  ForwardedHeaderFilter, redundancy, explain-it-back. Recap ~5. Cross-
  links T01/T02/T03/T05/T06; fwd T09/T10. Progress 58 → 59/371 (15.6% →
  15.9%); L2 row 27/44 → 28/44 (64%); C03-networking 7/11 → 8/11. Wired
  C03 README.
- Resume at `L2/C03/T09` — Load balancers (distribute traffic across N
  backends; the T08 reverse-proxy "load balancing" job deep-dived; the
  horizontal-scaling enabler, T07 statelessness). **L4 vs L7** LB (T08
  axis — L4 fast/protocol-agnostic/by-IP-port NLB vs L7 route-by-path/
  host/cookie + TLS-terminate ALB); **algorithms** (round-robin/weighted/
  least-conn/least-response/IP-hash/consistent-hash); **sticky sessions**
  (T07 — and why stateless/shared-store is better); **health checks +
  removal** (the availability/failover mechanism); where LBs live (HW/
  software/cloud ELB-ALB-NLB/**DNS LB**/anycast T04); architecture =
  two-connection (+ DSR), connection-vs-request balancing (H2/keep-alive),
  **stateless-backend requirement** (T07), LB-as-new-choke-point (SPOF →
  redundant + DNS/anycast T08), **consistent hashing** (minimal reshuffle
  → cache affinity T10); Java = stateless apps behind LB + /health
  (Actuator) + graceful drain on deploy. §4 full — L4-vs-L7 + algorithms
  + health-check-failover + stateless-requirement + consistent-hashing
  mechanism; the climax of the T08→T09→T10 edge-infra arc.

### 2026-06-04 (later — L2/C03/T07 Cookies, sessions & tokens)
- One topic this round. `L2/C03/T07` Cookies, sessions & tokens — **229
  lines, 3 Mermaid diagrams (cookie round-trip + session-store model +
  where-state-lives) + cookie-attributes/sessions-vs-tokens/XSS-vs-CSRF
  tables + Java code** at the deep bar. How **state** is layered on
  **stateless HTTP** (T05). **Language layer**: the problem (stateless →
  need a per-request identifier; state on server [session] or client
  [token]). **Cookies** — Set-Cookie/Cookie auto-echo (sequence diagram);
  **attributes table** (Domain/Path/Expires, **Secure** [HTTPS T06],
  **HttpOnly** [no JS → XSS defense], **SameSite** [cross-site → CSRF
  defense]); ~4KB, sent every request. **Server-side sessions** (diagram)
  — opaque session ID in cookie + server store (memory/Redis/DB); easy
  revoke; sticky-sessions/shared-store to scale (T09). **Tokens/JWT** —
  header.payload.signature base64url, claims (sub/exp/iat/iss), HMAC or
  RSA/EC signing (T06); **base64 ≠ encryption** (signed not encrypted,
  readable! IMPORTANT callout — never put secrets); Bearer tokens. **
  Sessions-vs-tokens trade-off table** — session (stateful, easy revoke,
  needs sticky/shared store) vs JWT (stateless, no storage, scales
  horizontally, **hard to revoke** → short expiry + refresh + denylist);
  OAuth2/OIDC glance (access/refresh/ID tokens, auth-code flow).
  **Architecture layer**: **where state physically lives** (diagram —
  server store [lookup/req + scaling dep] vs self-contained token [no
  lookup, no central control] vs the ~4KB cookie sent every request →
  JWT bloat); **the stateless-scaling payoff** (T05/T09 — any server
  handles any request, no sticky/shared store → horizontal scaling, the
  big JWT win); **the XSS-vs-CSRF storage dilemma** (table — HttpOnly
  cookie = XSS-safe but CSRF-prone→SameSite; localStorage = CSRF-safe but
  XSS-exposed; no free lunch → HttpOnly+Secure+SameSite + kill XSS at
  source); **verify-vs-lookup cost** (JWT verify = CPU/no-I/O, session =
  I/O/cheap-CPU; HMAC shared-secret vs RSA public-key-verify for
  microservices T06). **Java mapping** (code): servlet **HttpSession**/
  **JSESSIONID** (server-side, in-memory→sticky/shared, Spring Session→
  Redis) vs JWT libs (jjwt/nimbus verify). WARNING=storage trade-off no
  perfect answer; TIP=JWT can't-revoke → short+refresh+denylist or use
  sessions for instant logout. **Common mistakes** (8): secrets-in-
  payload, missing cookie flags, can't-revoke-surprise, giant JWTs,
  in-memory-session scaling break, CSRF on cookie auth, unverified/alg:
  none JWT, long-lived tokens. **INTERVIEW** 12 Q. **Practice (13)** —
  cookie round-trip, inspect attributes, HttpSession, decode-a-JWT (it's
  readable!), verify+tamper, SameSite CSRF, HttpOnly XSS, sticky-vs-shared,
  refresh flow, alg:none rejection, OAuth2 flow, storage decision,
  explain-it-back. Recap ~6. Cross-links T05/T06; fwd T08/T09. Progress
  57 → 58/371 (15.4% → 15.6%); L2 row 26/44 → 27/44 (61%); C03-networking
  6/11 → 7/11 (past midpoint). Wired C03 README.
- Resume at `L2/C03/T08` — Proxies & reverse proxies (intermediaries
  between client/server, T05). **Forward proxy** (in front of CLIENTS —
  egress/filter/cache/privacy, client-configured, CONNECT tunneling) vs
  **reverse proxy** (in front of SERVERS — Nginx/HAProxy/Envoy; client
  thinks it's the origin); what a reverse proxy does (TLS termination T06,
  load balancing T9-fwd, caching T05/T10-fwd, compression, path/host
  routing = API-gateway role, rate-limit/WAF, X-Forwarded-For header,
  buffering, static files); architecture = **L7 intermediary** (T01 —
  reads HTTP, vs L4/TCP forwarder = the L4-vs-L7 distinction T09) +
  **two-connection model** (client↔proxy and proxy↔backend separate TCP
  T02/T03 → backend-pool keep-alive, sees proxy IP → X-Forwarded-For) +
  TLS-termination architecture + single-choke-point SPOF/centralization
  trade-off. Java: Nginx in front of a Java app. §4 full — L7-intermediary
  + two-connection + TLS-termination + X-Forwarded-For mechanism; bridges
  to T09 LBs + T10 CDNs.

### 2026-06-04 (later — L2/C03/T06 TLS/SSL & certificates)
- One topic this round. `L2/C03/T06` TLS/SSL & certificates — **207
  lines, 3 Mermaid diagrams (handshake sequence + hybrid-crypto + chain-
  of-trust) + Java code** at the deep bar (security topic → diagrams over
  tables). The encryption/auth layer behind HTTPS (T05 "HTTPS=HTTP+TLS").
  **Language layer**: TLS provides **confidentiality** (encryption),
  **integrity** (MAC/AEAD), **authentication** (certs; +mTLS); TLS vs SSL
  (SSL deprecated) + versions (1.2/1.3, 1.3 = 1-RTT + dropped weak
  ciphers); sits between TCP (T02) and HTTP (T05). **The handshake**
  (sequence diagram) — ClientHello (versions/ciphers/key-share/**SNI**) →
  ServerHello + **certificate** + server key-share → both derive the
  shared secret (DH) → verify cert → Finished → symmetric app data; TLS
  1.3 1-RTT + resumption/0-RTT. **Hybrid crypto** (diagram + IMPORTANT) —
  **asymmetric** (cert + ephemeral DH) ONLY to authenticate + agree a key;
  **symmetric** (AES) for all bulk data → trust-between-strangers + speed.
  **Certificates & PKI** — X.509 binds public key→domain, CA-signed;
  **chain of trust** (diagram: leaf→intermediate→**root** in the trust
  store); client verifies chain + SAN match (T04) + dates + revocation;
  self-signed vs CA; **Let's Encrypt/ACME** (90-day auto); revocation
  (OCSP/stapling); **SNI** (one IP many certs); **CAA** record (T04).
  **Architecture layer**: **asym-vs-sym cost model** (handshake is the
  cost; AES cheap + AES-NI hardware → why keep-alive T05 amortizes);
  **handshake RTT** (T05 cost model — TLS 1.3 1-RTT, why QUIC/HTTP-3
  merges it); **forward secrecy** (ephemeral ECDHE → stolen long-term key
  can't decrypt past recorded sessions); **trust store = root of trust**
  (compromised CA = MITM, DigiNotar; Certificate Transparency); **TLS
  termination** at LB/reverse-proxy (T08/T09 — backend sees plain HTTP).
  **Java mapping** (code): javax.net.ssl (SSLContext/SSLSocket/SSLEngine-
  for-NIO), HttpClient auto-TLS for https (T05), the JVM **cacerts** trust
  store + **keytool**, TrustManager/KeyManager. WARNING=**never disable
  cert validation** (trust-all TrustManager = MITM); TIP=openssl s_client
  / browser cert viewer / keytool -list. **Common mistakes** (8): trust-
  all-certs, expired certs, hostname mismatch, self-signed-in-prod, old
  TLS/weak ciphers, ignored revocation, TLS-hides-everything (SNI/cert/
  size metadata leak), committed private key. **INTERVIEW** 12 Q.
  **Practice (13)** — openssl s_client handshake, read a cert chain,
  self-signed w/ keytool, Let's Encrypt/ACME, HttpClient https, import CA
  to cacerts, Wireshark TLS 1.3, the trust-all anti-pattern (+ MITM demo),
  weak-version rejection, CAA check, cert-error types, mTLS, explain-it-
  back. Recap ~6. Cross-links T02/T04/T05 (+ C02/T11 secret-scanning);
  fwd T07/T08/T09. Progress 56 → 57/371 (15.1% → 15.4%); L2 row 25/44 →
  26/44 (59%); C03-networking 5/11 → 6/11. Wired C03 README.
- Resume at `L2/C03/T07` — Cookies, sessions & tokens (how **state** is
  added on **stateless HTTP**, T05 callback). Cookies (Set-Cookie/Cookie,
  attributes Secure[T06]/HttpOnly/SameSite); server-side sessions (opaque
  session ID → server store Redis/DB); **tokens/JWT** (header.payload.
  signature, base64≠encryption, Bearer, HMAC/RSA signing T06); sessions-
  vs-JWT trade-off (stateful+revocable vs stateless+scales-but-hard-to-
  revoke); OAuth2/OIDC glance; architecture = where-state-lives + the
  stateless-horizontal-scaling payoff (T09) + XSS-vs-CSRF storage trade-
  off (HttpOnly-cookie vs localStorage) + signature-verify cost; Java
  servlet HttpSession/JSESSIONID + JWT libs. §4 full — where-state-lives +
  stateless-scaling + XSS-vs-CSRF + signature mechanism is the §4a anchor.

### 2026-06-04 (later — L2/C03/T05 HTTP/HTTPS lifecycle — 🎯 crossed 15%)
- One topic this round. `L2/C03/T05` HTTP/HTTPS lifecycle — **291 lines
  (the chapter's biggest — central synthesis topic), 4 Mermaid diagrams
  (chunked framing + lifecycle waterfall sequence + version/HOL evolution
  + ETag/304 conditional-request) + methods/status/version tables + raw
  HTTP examples + HttpClient sync+async code** at the deep bar. The L7
  (T01) protocol the web runs on; ties DNS/TCP/TLS/sockets together.
  **Language layer**: stateless text request/response over TCP (port
  80/443). **Request anatomy** — request line + **methods table**
  (GET/HEAD/OPTIONS/POST/PUT/PATCH/DELETE with **safe** + **idempotent**
  columns — the retry contract) + headers (Host/Content-Type/Length/
  Accept/Auth/Cache-Control/Cookie-T07) + body. **Response anatomy** —
  status line + **status-code families table** (1xx/2xx/3xx/4xx/5xx;
  200/201/204/301/304/400/401/403/404/429/500/502/503). **Framing** —
  **Content-Length vs Transfer-Encoding: chunked** (length-prefixed
  chunks, diagram) = **the concrete solution to TCP stream-not-messages**
  (T02 IMPORTANT callback). **Full lifecycle** (sequence diagram) — URL →
  DNS(T04) → TCP handshake(T02/T03) → TLS handshake(T06) → request →
  response → render; sub-resources reuse the conn → latency = a STACK OF
  RTTs. **Connection mgmt + evolution** (table + diagram): HTTP/1.0 conn-
  per-request; **1.1 keep-alive** (reuse, amortize handshake T02/T03, ~6
  parallel conns); **2 binary multiplexed streams / one TCP + HPACK** (but
  TCP-level HOL); **3 over QUIC/UDP** (independent streams → HOL SOLVED +
  faster handshake). **Statelessness** (state layered via cookies/tokens
  T07 → enables scaling T09); **HTTPS = HTTP + TLS** (T06, not magic).
  **Architecture layer**: **text on the wire** (telnet-typeable T03,
  H2+ went binary); **the RTT cost model** (DNS+TCP+TLS+request → every
  optimization = RTT reduction: keep-alive/H2-multiplex/H3/CDN-T10/0-RTT);
  **HOL blocking across versions** (app→transport→solved — the evolution
  throughline); **caching** — Cache-Control + **ETag/If-None-Match → 304
  Not Modified** (conditional request, diagram; CDN T10); **idempotency
  as a retry contract** (GET/PUT/DELETE retriable, POST not → LBs T09,
  resilient clients). **Java mapping** (code): modern **HttpClient** (11+,
  HTTP/2, sync send + async sendAsync→CompletableFuture) vs legacy
  HttpURLConnection. WARNING=respect method semantics (GET safe); TIP=
  curl -v / telnet / DevTools waterfall = RTT model visible. **Common
  mistakes** (9): GET-for-mutations, wrong status codes, no keep-alive,
  H2-multiplex≠HOL-solved, HTTPS-as-magic, framing mishandling, caching-
  header confusion, stateless≠sessionless, blind-POST-retry. **INTERVIEW**
  13 Q. **Practice (15)** — raw HTTP via telnet, curl -v, methods, status
  codes, keep-alive, chunked, length-framing, HttpClient sync+async,
  ETag/304, H2-vs-H1, HTTP/3, idempotency retry, DevTools waterfall,
  explain-it-back. Recap ~7. Cross-links T01/T02/T03/T04; fwd
  T06/T07/T09/T10. Progress 55 → 56/371 (**14.8% → 15.1% — crossed 15%**);
  L2 row 24/44 → 25/44 (57%); C03-networking 4/11 → 5/11. Wired C03 README.
- Resume at `L2/C03/T06` — TLS/SSL & certificates (HTTPS = HTTP+TLS, T05
  callback). What TLS provides (confidentiality/integrity/authentication
  + mTLS); the **handshake** (ClientHello/ServerHello/cert/ECDHE→session
  keys/Finished); **hybrid crypto** (asymmetric to authenticate+agree-key,
  symmetric AES for bulk — the cost model); **certificates & PKI** (X.509,
  CA-signed chain of trust, leaf→intermediate→root trust store, Let's
  Encrypt/ACME, revocation OCSP, SNI, CAA-T04); architecture = asym-vs-sym
  cost, handshake RTT (T05 cost model — TLS 1.3 1-RTT/0-RTT), **forward
  secrecy** (ephemeral keys), TLS termination at LB/proxy (T08/T09);
  Java javax.net.ssl/SSLContext/HttpClient + the JVM **cacerts** trust
  store + the trust-all-certs anti-pattern. §4 full — handshake +
  hybrid-crypto + chain-of-trust mechanism is the §4a anchor.

### 2026-06-04 (later — L2/C03/T04 DNS)
- One topic this round. `L2/C03/T04` DNS (resolution, records) — **218
  lines, 2 Mermaid diagrams (resolution sequence stub→recursive→root→TLD
  →authoritative + the delegated hierarchy tree) + record-types table +
  Java code** at the deep bar. The name→IP directory (T03) consulted
  before any connection. **Language layer**: why DNS (stable name→IP
  indirection T03 → change/failover/one-name-many-IPs LB-T09/CDN-T10);
  the **resolution flow** (sequence diagram) — stub (OS) → recursive
  resolver (8.8.8.8/ISP) → root → TLD → authoritative; **recursive vs
  iterative** (resolver answers you recursively, its steps are iterative
  referrals/NS); name read right-to-left (trailing-dot root). **Caching
  + TTL** — every answer cached by TTL → most lookups hit a cache;
  **"propagation" is a MYTH** (no push — old cached values live until
  TTL expires; lower TTL before a change); negative caching (SOA min);
  browser→stub→recursive→auth cache stack. **Record types** table
  (A/AAAA/CNAME[no-apex]/MX/NS/TXT[SPF-DKIM]/SOA/PTR/SRV/CAA) + zone.
  **UDP:53** (T02 — tiny query, low latency) + **TCP fallback** (>512B/
  EDNS, TC bit, zone transfers AXFR). **Architecture layer**: the
  **hierarchical distributed (delegated) tree** (diagram — root knows
  TLDs, TLD knows domains' NS, auth holds records → no single owner/
  bottleneck); the **wire format** (12B header: transaction **ID** for
  matching async UDP responses, flags QR/AA/TC/RD/RA/RCODE, section
  counts; name compression pointers — T01 byte-layout); **anycast** (one
  IP announced from 100s of sites via BGP → "13 root servers" = hundreds;
  load/latency/DDoS-resilience, CDN T10); **security** (unauthenticated →
  cache poisoning/Kaminsky → DNSSEC signatures + DoH/DoT encryption).
  **Java mapping** (code): InetAddress.getByName/getAllByName via the OS
  resolver (T03); **the JVM DNS-cache gotcha** — networkaddress.cache.ttl
  caches in-process (historically forever) → **stale IP after failover**
  (DB/RDS/LB) → set 30-60s. IMPORTANT=propagation-is-TTL-expiry; WARNING=
  JVM-cache-stale-IP-after-failover; TIP=dig +trace / dig TYPE / TTL
  countdown. **Common mistakes** (8): instant-change assumption, JVM-cache
  gotcha, apex CNAME, A-vs-CNAME, DNS-as-free/SPOF/latency, negative
  caching, hardcoding-IPs, absurdly-low TTLs. **INTERVIEW** 12 Q.
  **Practice (13)** — dig +trace, query record types, TTL countdown,
  UDP→TCP fallback, getAllByName, reproduce cache gotcha, compare
  resolvers, map the tree, read wire in Wireshark, TTL-before-change,
  negative caching, CDN steering, explain-it-back. Recap ~6. Cross-links
  T01/T02/T03; fwd T05/T06/T09/T10. Progress 54 → 55/371 (14.6% →
  14.8%); L2 row 23/44 → 24/44 (55%); C03-networking 3/11 → 4/11. Wired
  C03 README.
- Resume at `L2/C03/T05` — **HTTP/HTTPS lifecycle (BIG central topic —
  full §4 depth)**. The L7 request/response protocol: anatomy (methods +
  safe/idempotent, headers, status codes 1xx-5xx), chunked-vs-Content-
  Length framing (**the explicit solution to TCP's stream-not-messages**,
  T02 callback), the full URL→DNS(T04)→TCP(T02)→TLS(T06)→request→render
  lifecycle (ties the chapter together), connection mgmt (keep-alive →
  the T02/T03 handshake+ephemeral-port payoff), **HTTP/1.1 vs 2 vs 3**
  (multiplexing, HPACK, QUIC/UDP solving HOL blocking — T02), statelessness
  + cookies (T07), HTTPS=HTTP+TLS (T06); architecture = text-on-wire +
  chunked framing + RTT cost model + HOL-blocking-across-versions; Java
  `HttpClient` (11+, HTTP/2, sync+async) vs HttpURLConnection. §4 full —
  this one ties DNS/TCP/TLS/sockets together; budget extra length.

### 2026-06-04 (later — L2/C03/T03 IP, ports & sockets)
- One topic this round. `L2/C03/T03` IP, ports & sockets — **227 lines,
  3 Mermaid diagrams (4-tuple multiplexing + socket-API flow + listen-
  backlog queues) + ranges table + TCP-header... no, special-ranges
  table + Java code** at the deep bar. The addressing layer tying T01/T02
  to Java code. **Language layer**: **IP** — IPv4 (32-bit, dotted-quad,
  ~4.3B exhausted) vs IPv6 (128-bit hex); **CIDR/subnets** (network/host
  split, /24 mask = bitwise AND, L0/C01/T02); ranges table (loopback
  127.0.0.1, private 10/8·172.16/12·192.168/16 → NAT T11, 0.0.0.0 any,
  link-local). **Ports** — 16-bit; well-known 0-1023 (80/443/22/53,
  privileged), registered, ephemeral 49152-65535 (OS-assigned source
  ports). **Socket = (proto,IP,port); connection = 4-TUPLE** (src IP/port,
  dst IP/port) — the KEY insight, diagram of 3 clients → one server :443,
  each a distinct 4-tuple → distinct TCB (T02): **how one port serves
  thousands of clients**. **Socket API** (flow diagram) — server
  socket→bind→listen→accept (**returns a NEW socket per client**)→rw→close;
  client socket→connect (OS picks ephemeral src port); the
  accept-returns-new-socket model. **Java mapping** (code): InetAddress/
  InetSocketAddress, ServerSocket(bind+listen, accept→Socket), Socket
  (InputStream/OutputStream), DatagramSocket (UDP, no accept); new
  ServerSocket(8080) binds 0.0.0.0. **Architecture layer**: **socket = a
  FILE DESCRIPTOR** (Unix everything-is-a-file; fd indexes the kernel
  socket struct/TCB T02; read/write syscalls; **fd limits ulimit → C10k**;
  unclosed accepted socket = fd leak); the **listen backlog** (SYN queue
  + accept queue; diagram; full accept queue → refused/dropped);
  **ephemeral-port exhaustion** (same dst → only src port varies, ~28k
  cap → **connection pooling** T05, SO_REUSEADDR); **IPv4 = 32-bit int**
  (192.168.1.1 = 0xC0A80101, masking = bit-AND L0/C01/T02) in **network
  byte order = BIG-ENDIAN** (htons/htonl in C; Java handles it + is BE
  internally). IMPORTANT=port-vs-socket-vs-connection(4-tuple); WARNING=
  127.0.0.1-binds-loopback-only vs 0.0.0.0; TIP=accept returns new socket,
  keep the accept loop free (C10k/NIO entry). **Common mistakes** (8):
  port/socket/connection confusion, 127.0.0.1-for-external, fd/port
  exhaustion, privileged-port-without-root, blocking the accept loop,
  forgetting-accept-returns-new-socket/fd-leak, NAT/private-IP confusion,
  IP-uniquely-identifies-host (NAT/multi-homing). **INTERVIEW** 12 Q.
  **Practice (14)** — echo server w/ 2 clients, ss/netstat 4-tuples,
  bind-scope reachability, ephemeral ports, lsof socket-as-fd, address-
  in-use+SO_REUSEADDR, CIDR math, privileged port, IPv4-as-int, UDP
  no-accept, IPv6 ::1, new-socket-per-accept logging, backlog saturation,
  explain-it-back. Recap ~6. Cross-links T01/T02/L0-C01-T02; fwd
  T04/T05/T11. Progress 53 → 54/371 (14.3% → 14.6%); L2 row 22/44 →
  23/44 (52%); C03-networking 2/11 → 3/11. Wired C03 README.
- Resume at `L2/C03/T04` — DNS (resolution, records); the name→IP
  directory (T03) before any connection. Resolution chain stub→recursive
  →root→TLD→authoritative; caching+TTL ("propagation" = TTL expiry);
  record types A/AAAA/CNAME/MX/NS/TXT/SOA/PTR/SRV; UDP:53 + TCP fallback
  (T02 + 512B/EDNS); architecture = hierarchical distributed tree +
  12B DNS header wire format + anycast + DNSSEC/DoH; Java
  InetAddress.getByName + the JVM DNS-cache-TTL production gotcha
  (networkaddress.cache.ttl). §4 full — the distributed-tree + caching/
  TTL + UDP-wire-format + anycast is the §4a anchor.

### 2026-06-04 (later — L2/C03/T02 TCP vs UDP)
- One topic this round. `L2/C03/T02` TCP vs UDP — **237 lines, 3 Mermaid
  diagrams (UDP 8B header + 3-way-handshake sequence + sliding window) +
  core-contrast table + TCP-header table + Java code** at the deep bar.
  Both L4 (T01) transport protocols on IP; opposite bargains. **Language
  layer**: the **contrast table** (connection/reliability/ordering/data-
  model/flow/congestion/header/latency/use). **UDP** — minimal datagram,
  8B header (src/dst port/length/checksum), **preserves message
  boundaries** (1 send = 1 recv), fire-and-forget, app owns reliability;
  low latency (no handshake/retransmit/HOL), 1-to-many. **TCP** — the
  meat: **3-way handshake** (SYN/SYN-ACK/ACK, syncs seq numbers, 1 RTT
  cost) [sequence diagram]; **reliability+ordering** (seq numbers + ACKs
  + retransmit on timeout/dup-ACK + receiver reorders); **STREAM not
  messages** (write boundaries NOT preserved → must frame yourself,
  length-prefix/delimiter — HTTP Content-Length, T05); **sliding window**
  flow control (receiver-advertised buffer) [diagram]; **congestion
  control** (slow start / AIMD — don't overrun the network); 4-way close
  + TIME_WAIT (2×MSL, port exhaustion); **head-of-line blocking** (1 lost
  segment stalls all behind → why HTTP/2-over-TCP suffers, HTTP/3 uses
  QUIC/UDP). **TCP header table** (20-60B: ports/seq/ack/flags/window/
  checksum/options vs UDP 8B — T01 overhead callback). **When each** —
  TCP (HTTP/DB/SSH/SMTP), UDP (DNS T04/VoIP/gaming/DHCP/multicast), QUIC/
  HTTP-3 (reliability re-built per-stream over UDP, T05). **Architecture
  layer**: TCP state machine + **TCB per connection** lives in the OS
  KERNEL (JVM delegates, T01); **connection = kernel state** (TCB + 2
  buffers + fd) → C10k scaling, thread-per-conn doesn't scale; **write()
  ≠ delivered** (copies to kernel send buffer, async retransmit); Nagle
  vs delayed-ACK + TCP_NODELAY; why UDP is lower latency; big-UDP > MTU
  fragmentation (1 lost fragment = whole datagram lost, T01). **Java
  mapping** (code): Socket/ServerSocket=TCP (InputStream/OutputStream =
  stream, frame yourself!), DatagramSocket/DatagramPacket=UDP (messages);
  thin kernel wrappers, NIO for non-blocking. IMPORTANT=stream-not-
  messages framing trap; WARNING=UDP unreliable BY DESIGN (QUIC re-adds
  it); TIP=handshake 1-RTT cost → connection reuse/keep-alive. **Common
  mistakes** (8): expect TCP message boundaries, UDP-unreliability-as-bug,
  TIME_WAIT churn, oversized UDP, Nagle/NODELAY, thread-per-conn at scale,
  write()=delivered myth, wrong protocol. **INTERVIEW** 12 Q. **Practice
  (14)** — TCP/UDP echo, see handshake in Wireshark, stream-vs-message
  framing, loss demo, read headers, handshake-RTT cost, Nagle toggle,
  big-UDP fragmentation, TIME_WAIT accumulation, choose-protocol, QUIC,
  explain-it-back. Recap ~6. Cross-links T01, L0/C01/T02; forward-refs
  T03/T04/T05. Progress 52 → 53/371 (14.0% → 14.3%); L2 row 21/44 → 22/44
  (50%); C03-networking 1/11 → 2/11. Wired C03 README.
- Resume at `L2/C03/T03` — IP, ports & sockets (the addressing layer
  tying T01/T02 to Java code; IPv4/IPv6, CIDR/subnets/private-ranges/NAT,
  ports well-known/ephemeral, **socket = (proto,IP,port)** + **connection
  = 4-tuple** [how one port serves many clients, T02 TCB callback]; the
  BSD socket API socket/bind/listen/accept/connect; Java InetAddress/
  ServerSocket/Socket/DatagramSocket + accept-returns-new-socket;
  architecture = **socket=file-descriptor** [fd limits, listen backlog/
  SYN+accept queues], ephemeral-port exhaustion, IPv4-as-32-bit-int +
  network-byte-order BIG-ENDIAN [L0 endianness callback]). §4 full — the
  socket=fd + 4-tuple + kernel backlog + byte-order is the §4a anchor.

### 2026-06-04 (later — L2/C03/T01 OSI & TCP/IP models — C03 STARTED)
- **User chose L2/C03 Networking next** (at the C02-completion checkpoint,
  via AskUserQuestion — picked C03 over C04/C05/L3). One topic this round.
  `L2/C03/T01` OSI & TCP/IP models — **221 lines, 3 Mermaid diagrams
  (OSI↔TCP/IP mapping + encapsulation nesting + packet physical journey)
  + OSI-7-layer table + where-each-layer-lives table** at the deep bar.
  **NOTE on depth for networking topics**: §4a "byte-level memory layout"
  reinterpreted as the **wire/header format** — encapsulation byte
  structure, header sizes, MTU; the "architecture layer" = the physical
  packet journey + where each layer is implemented (app/JVM vs OS-kernel
  stack vs NIC vs wire). **Language layer**: why layer (independent
  replaceable concerns glued by encapsulation); **OSI 7** (App/Present/
  Session/Transport/Network/DataLink/Physical) table w/ job+PDU+addressing+
  examples + mnemonics; **TCP/IP 4-5 layer** model + the OSI↔TCP/IP
  mapping (OSI 5/6/7→App, 1/2→Link). **Wire layer (the depth)**:
  **encapsulation** — data→**segment**(TCP,ports)→**packet**(IP,addrs)→
  **frame**(Ethernet,MACs)→bits, the nested-envelope structure, peer
  layers read only their own header; **header overhead** (~58B: Eth 18 +
  IPv4 20 + TCP 20) + **MTU** ~1500 → segmentation/fragmentation (T02
  forward). **Architecture layer**: the **physical journey** (app→kernel
  stack→NIC→router hops→host) with the KEY insight — **IP packet is
  end-to-end (constant IP) but the Ethernet frame is REBUILT every hop
  (MAC per-hop, TTL decrements)**; routers=L3, switches=L2; **where each
  layer lives** (app=JVM, transport/internet=OS kernel TCP/IP stack [NOT
  the JVM], link=NIC, physical=wire). **Java mapping**: code is L7; JVM
  DELEGATES TCP/IP to the OS kernel via the socket API (Socket→TCP,
  DatagramSocket→UDP, InetAddress→IP/DNS T04); **diagnose-by-layer**
  (dig/ping/traceroute/telnet-nc/curl-v each probe a specific layer).
  IMPORTANT callout = encapsulation/layer-independence is the superpower;
  TIP = diagnose by layer; WARNING = don't over-literalize OSI 5/6 (TLS
  is really app-layer over TCP, T06). **Common mistakes** (7): names
  without encapsulation, JVM-implements-TCP myth, MAC-vs-IP confusion,
  OSI-as-reality, segment/packet/frame conflation, ignoring MTU/overhead,
  not diagnosing by layer. **INTERVIEW** 12 Q. **Practice (13)** — draw
  both models, PDU trace, Wireshark see-encapsulation-live, spot MAC/IP/
  port, traceroute hops, tool-to-layer, overhead math, diagnose-by-layer,
  find-your-layer-in-Java, layer-independence, hop-by-hop changes, place
  TLS/DNS/VPN, explain-it-back. Recap ~6. Cross-links L0/C01/T02 binary/
  hex; forward-refs T02/T03/T05/T06. Progress 51 → 52/371 (13.7% →
  14.0%); L2 row 20/44 → 21/44 (48%); **C03-networking 0/11 → 1/11 STARTED**.
  Wired C03 README (T01 + frontmatter status→in-progress).
- Resume at `L2/C03/T02` — TCP vs UDP (the two L4 transport protocols;
  TCP connection-oriented/reliable/ordered/stream w/ 3-way handshake +
  seq/ack + sliding-window flow control + congestion control vs UDP
  connectionless/unreliable/datagram/8B-header; when each; QUIC/HTTP-3;
  header byte-layout + kernel state-machine/buffers T01 callback; Java
  Socket-vs-DatagramSocket; message-boundary/framing trap). §4 full —
  the handshake + reliability state machine + header layout is the §4a
  anchor.

### 2026-06-04 (later — L2/C02/T11 Dependency vulnerability scanning — ✅ C02 COMPLETE 11/11)
- One topic this round. `L2/C02/T11` Dependency vulnerability scanning —
  **216 lines, 3 Mermaid diagrams (remediation flow + CI-gate-vs-
  scheduled + coordinate matching) + ecosystem/tools tables** at the
  deep bar (§4; §4a lighter — the coordinate-matching + transitive-graph
  + DB-changes-independently mechanism anchors it). **The LAST C02
  topic — chapter now COMPLETE 11/11.** The counterpart to T07 static
  analysis: T07 scans YOUR code, SCA scans your DEPENDENCIES against vuln
  DBs. **Language layer**: the problem — apps are mostly third-party
  code; a CVE in any direct OR **transitive** dep (T03) is your vuln;
  **Log4Shell** (CVE-2021-44228, CVSS 10.0, log4j2 RCE, mostly transitive)
  as the canonical case. **The ecosystem** table — **CVE** (the ID),
  **NVD** (NIST DB), **CVSS** (0-10 severity ≠ risk), **GHSA**, **OSV**,
  vendor advisories. **Tools** table — OWASP Dependency-Check (free,
  noisier), Dependency-Track, **Snyk** (reachability + fix PRs),
  **Dependabot** (alerts + auto-bump PRs, T05), **Grype/Trivy**
  (containers+deps); note vuln-scanning ≠ outdated-checking
  (versions/dependencyUpdates). **SBOM** — CycloneDX/SPDX inventory;
  "can't respond to what you can't enumerate"; EO 14028 compliance.
  **Remediation flow** (diagram): scan resolved graph → triage
  (reachability/scope) → fix by upgrading / **overriding the transitive
  version** (T03 dependencyManagement/constraints) / mitigate+suppress
  w/ justification (T07 pattern) → re-scan. **CI gate + scheduled
  re-scan** (diagram) — gate fails on new HIGH/CRITICAL (T05/T06/T07);
  **THE key difference**: a new CVE hits UNCHANGED code → must re-scan on
  a SCHEDULE, not just on code change. **Architecture layer**:
  **coordinate/CPE matching** (groupId:artifactId:version → DB entry;
  diagram) + the **false-positive** problem (name collision, shaded jars
  → suppression file, T07); **reachability** analysis (call-graph — does
  your code CALL the vuln path? T07 dataflow callback); scans the
  **resolved** transitive graph (T03 — resolution decides the actual
  version); the **DB-changes-independently** property (result = your
  graph × DB-at-scan-time → the unique "re-scan unchanged code finds new
  vulns", unlike every other chapter tool); **supply-chain frontier**
  (typosquatting, poisoned releases, Sigstore/SLSA/lockfiles).
  **Common mistakes** (8): direct-only scanning, no CI gate, no scheduled
  re-scan, alert fatigue/no reachability, blind auto-upgrade, suppress
  w/o justification, ignoring SBOM, SCA-as-all-of-security. **INTERVIEW**
  12 Q (SCA, CVE/CVSS/NVD, Log4Shell, vs T07, why re-scan unchanged code,
  transitive fix via override, SBOM, Dependabot, false positives,
  reachability, CVSS≠risk). **Practice (13)** — add Dependency-Check,
  see a CVE, find+fix a transitive vuln via override (T03), suppress a
  false positive, CI gate fails on CRITICAL, generate CycloneDX SBOM,
  Dependabot bump PR, same-code-new-findings, triage drill, reachability,
  vuln-vs-outdated, explain-it-back. Recap ~6 + **chapter-completion
  note + Next → C03 README**. Progress 50 → 51/371 (13.5% → 13.7%); L2
  row 19/44 → 20/44 (45%); **C02-build-tools 10/11 → 11/11 COMPLETE ✅**.
  Wired C02 README (T11 + frontmatter status→complete), L2 README (C01 +
  C02 rows → complete).
- **✅ MILESTONE: L2/C02 Build Tools & Developer Workflow COMPLETE (11/11).**
  L2 now 20/44 (45%) — C01 (9/9) + C02 (11/11) done.
- **⚠️ CHECKPOINT: asked the user about next direction (C03 Networking
  next in curriculum order, or another L2 chapter, or resume L3). Do NOT
  auto-start the next chapter — await the user's choice.** See §4.

### 2026-06-04 (later — L2/C02/T10 Annotation processing)
- One topic this round. `L2/C02/T10` Annotation processing — **247
  lines, 3 Mermaid diagrams (3 consumption modes + the round model +
  the trio-resolution validate/generate/mutate) + retention table +
  AbstractProcessor code** at the deep bar (§4; the rounds + Element/
  Filer + retention-in-bytecode mechanism is the rich §4a anchor). The
  **CAPSTONE of the C02 annotation trio** (T08 Lombok → T09 MapStruct →
  **T10**) — generalizes the two users into the JSR-269 mechanism and
  lets the reader WRITE a processor. **Language layer**: annotations as
  **metadata** (built-ins; declaring `@interface` with elements +
  defaults); **meta-annotations** — **`@Retention`** (SOURCE = compile-
  only/discarded e.g. @Override/Lombok; CLASS = in bytecode not loaded,
  the default; RUNTIME = in bytecode + reflectively readable e.g. Spring/
  JUnit/JPA) as the axis deciding consumption mode, plus `@Target`/
  `@Documented`/`@Inherited`/`@Repeatable`. **The 3 consumption modes**
  (diagram): compile-time processing (this topic), runtime reflection
  (RUNTIME retention — Spring/Jackson/JUnit), bytecode tools (CLASS);
  the modern shift from #2→#1 (Micronaut/Quarkus/Dagger = compile-time
  DI → fast startup + native-image). **The processor API**: extend
  **`AbstractProcessor`**, `@SupportedAnnotationTypes`/`@Supported
  SourceVersion`, `process(annotations, roundEnv)` → boolean (claimed);
  registration via `META-INF/services` or `@AutoService`; the
  `annotationProcessor` path / `-processor`/`-proc:none` (T01/T02).
  **Architecture layer (the depth)**: the **round model** (javac runs
  processors repeatedly; generated files feed the NEXT round to a
  no-new-files fixpoint; final `processingOver()` round) — diagram;
  **`Element` vs `TypeMirror`** (declaration vs type-usage; TypeElement/
  ExecutableElement/VariableElement; READ-ONLY); **`Filer`** (create new
  files, JavaPoet) + **`Messager`** (diagnostics tied to an Element —
  good UX vs throwing). **THE TRIO RESOLUTION** (diagram + WARNING): a
  processor can ONLY add files, NEVER modify an existing class (Element
  read-only + Filer new-files-only by design) → this FORCES MapStruct's
  `*Impl` / Dagger's `DaggerXComponent` / AutoValue's `AutoValue_Foo`
  generated-sibling pattern, and is exactly the rule **Lombok bypasses**
  via internal `JCTree` — same trigger, 3 behaviours (validate / generate-
  sibling [sanctioned] / mutate-in-place [hack]). **Retention in the
  bytecode** (L0/C01/T04 callback): SOURCE → absent, CLASS →
  `RuntimeInvisibleAnnotations`, RUNTIME → `RuntimeVisibleAnnotations`
  (javap -v proves it; why Spring @Component MUST be RUNTIME, Lombok
  @Getter is SOURCE). **Build cost**: processors run inside javac →
  build-time, **zero runtime footprint** (T06/T07/T08/T09 echo);
  **incremental** processors (isolating/aggregating vs non-incremental =
  Gradle recompiles everything). **Real-processor survey**: Lombok,
  MapStruct, Dagger/Hilt, AutoValue/AutoService/Immutables, JPA
  metamodel, Micronaut/Quarkus. **Common mistakes** (8): wrong
  @Retention (invisible to consumer), expecting in-place modification,
  missing registration (silent no-run), infinite generation loop,
  throwing instead of Messager, non-incremental (slow builds), runtime-
  reflection-where-codegen-fits, mis-scoped @Target. **INTERVIEW** 12 Q.
  **Practice (14)** — retention via javap -v, reflect a RUNTIME
  annotation, write an AbstractProcessor generating a companion, register
  + run, Messager error, observe rounds, walk elements, @Target restrict,
  @Repeatable, two consumption modes, incrementality build-speed, trace
  the trio, read generated code, explain-it-back. Recap ~6 objectives.
  Progress 49 → 50/371 (13.2% → 13.5%); L2 row 18/44 → 19/44 (43%);
  C02-build-tools 9/11 → 10/11. Wired the L2/C02 README link.
- Resume at `L2/C02/T11` — Dependency vulnerability scanning (**the LAST
  C02 topic — after it, C02 = 11/11 COMPLETE → ASK THE USER about next
  direction**). SCA: scan the transitive dep graph (T03) against vuln DBs
  (CVE/NVD/CVSS/GHSA/OSV); Log4Shell as the canonical transitive RCE;
  tools (OWASP Dependency-Check, Snyk, Dependabot, Grype/Trivy); SBOM
  (CycloneDX/SPDX); remediation flow (scan→triage reachability→override
  version T03→suppress→re-scan); CI gate (T05/T06/T07) + scheduled
  re-scan (new CVEs hit UNCHANGED code — the key difference from T07);
  coordinate/CPE matching + false positives + reachability. §4 full,
  §4a lighter (matching + transitive-graph + DB-changes-independently
  mechanism anchors it).

### 2026-06-04 (later — L2/C02/T09 MapStruct)
- One topic this round. `L2/C02/T09` MapStruct — **194 lines, 2 Mermaid
  diagrams (processing-rounds flow + Lombok-vs-MapStruct contrast) +
  reflection-mapper comparison table + code snippets** at the deep bar
  (§4; the standard-processor mechanism is the §4a anchor + the bridge
  to T10). Second of the C02 annotation trio (T08 Lombok → **T09
  MapStruct** → T10 Annotation processing). **Language layer**: the
  **mapping problem** (entity↔DTO↔API model = endless setX(getX())
  boilerplate, silent data loss on a forgotten field); **`@Mapper`**
  interface → generated `*Impl`; **`@Mapping`** (source/target rename,
  expression/constant/ignore, nested `a.b.c`), automatic same-name
  mapping, collection mapping, custom logic (`qualifiedByName`+`@Named`,
  `uses=`, default methods), **`@MappingTarget`** update-into-existing,
  **`unmappedTargetPolicy=ERROR`** (unmapped field → BUILD FAILURE — the
  safety win), **`componentModel="spring"`** (injectable bean),
  `@ValueMapping`/`@InheritConfiguration`. **MapStruct vs reflection
  mappers** (ModelMapper/Dozer) comparison table — compile-time codegen
  → fast (no reflection), type-safe (mismatch = compile error),
  debuggable vs runtime reflection = slow/opaque/silent-skip.
  **Architecture layer (the depth + T10 bridge)**: MapStruct is a
  **standard JSR-269 processor** (`javax.annotation.processing.Processor`,
  registered via `META-INF/services`, on the `annotationProcessor` path
  T01/T02); javac runs it in **rounds**; it **READS** the `@Mapper` via
  the **`Element`/`TypeMirror`** model (read-only, public API) and
  **WRITES** a new `CarMapperImpl.java` via the **`Filer`**, compiled in
  a later round. **THE CRITICAL CONTRAST with Lombok (T08)** (own
  diagram + IMPORTANT callout): a standard processor can ONLY generate
  NEW files, CANNOT modify the annotated type → exactly why MapStruct
  emits a sibling `*Impl` while Lombok reached into javac internals to
  mutate yours; same trigger, opposite mechanism; T10 generalizes.
  Generated `*Impl` = **real reflection-free bytecode** (L0/C01/T04),
  **zero runtime cost** (T06/T07/T08 echo), **readable** in
  `build/generated/`. **Records as targets** (L1/C01/T14) → constructor-
  based mapping (no setters). **Lombok+MapStruct ordering** WARNING —
  needs `lombok-mapstruct-binding` so Lombok's accessors exist when
  MapStruct inspects the mapper (else empty mappings). **Common
  mistakes** (8): no unmappedTargetPolicy=ERROR, name mismatch without
  @Mapping, Lombok ordering, defaulting to a reflection mapper, not
  reading generated code, missing componentModel, ambiguous methods,
  heavy expression() strings. **INTERVIEW** 12 Q (mechanism = generates
  a class; vs reflection mappers; vs Lombok; unmappedTargetPolicy;
  componentModel). **Practice (14)** — first mapper, read the *Impl,
  unmappedTargetPolicy catches a dropped field, rename+nested, collection
  mapping, qualifiedByName/uses, @MappingTarget, componentModel=spring,
  record target (constructor mapping), Lombok ordering fix, vs ModelMapper,
  no-reflection check, trace the rounds, explain-it-back. Recap ~6
  objectives. Progress 48 → 49/371 (12.9% → 13.2%); L2 row 17/44 → 18/44
  (41%); C02-build-tools 8/11 → 9/11. Wired the L2/C02 README link.
- Resume at `L2/C02/T10` — Annotation processing (the GENERAL JSR-269
  mechanism behind T08/T09 — the trio capstone; annotations + retention
  SOURCE/CLASS/RUNTIME + @Target/meta-annotations; the 3 consumption
  modes [compile-time processing / runtime reflection / bytecode];
  `AbstractProcessor`.`process()` + rounds + `Element`/`TypeMirror` read
  + `Filer` write + `Messager`; WHY processors can only ADD files not
  modify [the Lombok-breaks-this payoff]; retention-in-bytecode L0/C01/T04
  callback; incremental-processor build cost; real processors survey
  [Dagger/AutoValue/Micronaut/Quarkus compile-time DI]; lets the reader
  WRITE one). **Completes the annotation trio + brings C02 to 10/11 —
  only T11 dependency-vulnerability-scanning left, then C02 done.**

### 2026-06-04 (later — L2/C02/T08 Lombok)
- One topic this round. `L2/C02/T08` Lombok — **200 lines, 2 Mermaid
  diagrams + 3 dense tables (annotation catalogue, records-vs-Lombok
  decision, the 3 add-behaviour mechanisms) + code snippets** at the
  deep bar (§4; the compile-time AST-mutation mechanism is the §4a
  anchor, lighter on byte-layout). First of the C02 annotation trio
  (T08 Lombok → T09 MapStruct → T10 Annotation processing). **Language
  layer**: the **boilerplate problem** (POJO = 80% getters/setters/
  equals/hashCode/toString/ctor that drifts when a field is added);
  the **annotation catalogue** — `@Getter`/`@Setter`, `@ToString`,
  `@EqualsAndHashCode`, the constructor annotations, **`@Data`** (mutable
  bundle) / **`@Value`** (immutable bundle), **`@Builder`** (+`@Builder.
  Default`/`@Singular`), `@Slf4j`, `@NonNull`, `@SneakyThrows`,
  `@Cleanup`, `@With`. **Records vs Lombok** decision table — records
  (Java 16+, L1/C01/T14) for new immutable carriers (native, no dep,
  pattern-matchable) vs Lombok for mutable classes/entities/builders/
  loggers/pre-record code. **Architecture layer (the depth hook)**: the
  3 ways to add behaviour from an annotation — (1) runtime reflection
  (Jackson), (2) **standard JSR-269 processor** (generate NEW files —
  MapStruct/T09), (3) **Lombok = hook javac + MUTATE the class's AST
  IN PLACE** via internal `com.sun.tools.javac` APIs (injects method
  nodes before code-gen). **Consequence 1 — zero runtime cost**: the
  generated methods are **real, byte-identical bytecode** (L0/C01/T04;
  `javap -p` shows them), **no reflection, no runtime dep** (`compileOnly`/
  `provided`, T01/T02) — T06/T07 zero-runtime-footprint echo but Lombok
  ADDS bytecode (a generator, not a checker). **Consequence 2 — it's a
  hack**: internal APIs (JSR-269 forbids modifying existing classes →
  T10), so **`--add-opens` on JPMS**, **JDK-upgrade fragility**, **IDE
  plugin needed** (same AST trick to see members), **`delombok`** to
  expand/inspect/remove. **CRITICAL CONTRAST** (IMPORTANT callout):
  MapStruct (T09) = standard processor generating a NEW `*Impl` class
  (can't touch yours); Lombok MUTATES yours — same trigger, opposite
  mechanism; T10 details the sanctioned path. **`@Data`-on-JPA-entity
  WARNING**: all-field equals/hashCode/toString breaks identity, lazy
  loading (LazyInitializationException), mutable-key, and bidirectional
  toString recursion → use `@Id`-based equals (L1/C01/T10). **Common
  mistakes** (8): @Data on entities, @EqualsAndHashCode mutable keys,
  @Builder without @Builder.Default, @SneakyThrows overuse, Lombok/JDK
  version mismatch, missing IDE plugin/--add-opens, Lombok-where-a-record-
  fits, treating it as runtime magic. **INTERVIEW** 12 Q (heavy on "how
  does it work = AST mutation" + "vs standard processor" + "@Data-on-
  entity"). **Practice (14)** — add Lombok, @Data, @Builder+Default+
  Singular, @Value-vs-record, delombok to read generated source, javap
  proves real methods, no-runtime-dep check, entity pitfall + @Id fix,
  mutable-key bug, @Slf4j, @NonNull/@SneakyThrows, break-it (no plugin),
  records-vs-Lombok decision drill, explain-it-back. Recap ~5 objectives.
  Progress 47 → 48/371 (12.7% → 12.9%); L2 row 16/44 → 17/44 (39%);
  C02-build-tools 7/11 → 8/11. Wired the L2/C02 README link. Cross-links
  now resolve to real L1 files: L1/C01/T14 records, L1/C01/T10 equals/
  hashCode contracts (parallel session has authored them).
- Resume at `L2/C02/T09` — MapStruct (compile-time bean mapper;
  @Mapper/@Mapping, unmappedTargetPolicy=ERROR, componentModel=spring;
  the KEY CONTRAST to Lombok = **standard JSR-269 processor that
  GENERATES A NEW `*Impl` class** via Element-model + Filer, can't
  modify your type → the natural bridge into T10's full mechanism;
  vs reflection mappers (ModelMapper/Dozer) = compile-safe + fast +
  debuggable + zero reflection; Lombok+MapStruct processor-ordering
  gotcha). Second of the C02 annotation trio.

### 2026-06-04 (later — L2/C02/T07 Static analysis)
- One topic this round. `L2/C02/T07` Static analysis (PMD, SpotBugs,
  SonarQube) — **264 lines, 4 Mermaid diagrams + sound-vs-complete
  table + PMD/SpotBugs/Sonar config snippets** at the deep bar (§4; §4a
  lighter but anchored by the AST-vs-bytecode + dataflow + undecidability
  mechanism). The correctness/security tier above T06's style linting.
  **Language layer**: the **static-analysis spectrum** (formatter →
  linter/Checkstyle → static analysis → runtime/dynamic); static =
  inspect without executing. **PMD** — **source-AST** smells (dead code,
  unused vars, complexity/design, error-prone constructs), rule
  categories, XPath custom rules, **CPD** copy-paste detector. **SpotBugs**
  (FindBugs successor) — **BYTECODE-based** (analyses `.class`, runs after
  compile), CFG + data-flow bug patterns (NP_NULL_ON_SOME_PATH, broken
  equals/hashCode, boxing perf, concurrency), pattern code + rank +
  confidence; **FindSecBugs** taint-based security (source→sink).
  **SonarQube** — the **platform** (server + scanner + dashboard);
  Bug/Vulnerability/Code-Smell/Security-Hotspot taxonomy; **technical
  debt** (SQALE); the **quality gate** (pass/fail on NEW code:
  bugs/vulns/coverage/duplication/hotspots) → CI fails, branch protection
  blocks merge (T05); **"clean as you code"** (gate new code, not legacy);
  **SonarLint** IDE plugin, **SonarCloud** hosted. **False positives** —
  inherent; suppress narrowly with justification (`@SuppressFBWarnings`/
  `//NOPMD`/Sonar mark), triage by rank/severity/confidence, baseline the
  legacy. **Workflow** — IDE (SonarLint) → pre-commit (light) → CI gate
  (authoritative, heavier — needs compiled classes). **Architecture
  layer** (the depth): **source-AST vs bytecode** substrate distinction
  (PMD/Checkstyle on AST = intent; SpotBugs on bytecode = behaviour,
  L0/C01/T04 callback — normalized/desugared, explicit control flow,
  constant pool) — why SpotBugs catches a dataflow null-deref PMD can't;
  **data-flow analysis** over the **control-flow graph** (nullness
  lattice NULL/NOT_NULL/NULLABLE/UNKNOWN merged at joins to a fixpoint;
  **taint analysis** = same machinery, tainted/sanitized source→sink);
  **abstract interpretation**; the **undecidability limit** (Rice's
  theorem / halting → no analyser is sound + complete + terminating →
  every tool **approximates**, leaning **sound-incomplete** [false
  positives] or **complete-unsound** [false negatives]) — why false
  pos/neg are unavoidable math, not tool immaturity; build-time cost
  (SpotBugs/Sonar CI-weight, cached T02), **zero runtime footprint**;
  **a clean run is NOT a correctness proof** (WARNING callout).
  **Common mistakes** (8): alert fatigue, gamed too-strict gate,
  suppress-not-fix, no legacy baseline, confusing substrates (PMD source
  vs SpotBugs bytecode), heavy scans on every keystroke, clean-scan =
  correct, all-findings-equal. **INTERVIEW** 12 Q. **Practice (15)** —
  PMD + CPD, SpotBugs null-on-path (vs source linter), broken equals,
  FindSecBugs taint, substrate contrast, stand up SonarQube + scanner,
  quality gate fails PR, clean-as-you-code baseline, narrow suppression,
  SonarLint shift-left, cost/footprint timing, trace the CFG/nullness
  lattice, find a false negative, explain-it-back. Recap ~7 objectives.
  Progress 46 → 47/371 (12.4% → 12.7%); L2 row 15/44 → 16/44 (36%);
  C02-build-tools 6/11 → 7/11. Wired the L2/C02 README link.
- Resume at `L2/C02/T08` — Lombok (annotation-based boilerplate
  eliminator; @Data/@Builder/@Value/@Slf4j etc.; records-vs-Lombok;
  the depth hook = compile-time **AST mutation** via the javac
  internal API — NOT reflection, NOT a standard processor — zero runtime
  cost, real bytecode methods, T06/T07 zero-footprint callback; sets up
  T10 annotation-processing mechanism; @Data-on-JPA-entity pitfall).
  First of the C02 annotation trio (T08 Lombok → T09 MapStruct → T10
  Annotation processing).

### 2026-06-04 (later — L2/C02/T06 Code formatters & linters)
- One topic this round. `L2/C02/T06` Code formatters & linters
  (Checkstyle, Spotless) — **282 lines, 3 Mermaid diagrams + 4 tables +
  Gradle/XML config snippets** at the deep bar (§4; lighter §4a as a
  tooling topic, but the AST-mechanism + bytecode-identical + caching
  facts anchor it). The automation layer on L0/C02/T19 code style.
  **Language layer**: **why automate** (bikeshedding, review noise,
  drift, doesn't scale → let a tool own style). **Formatter-vs-linter**
  table — formatter **rewrites** to a canonical layout (the fix),
  linter **reads + reports** deviations (no fix); "a formatter writes,
  a linter reads"; complementary not competing. **Formatters** —
  **google-java-format** (deliberately non-configurable → kills
  bikeshedding, deterministic), palantir fork; **Spotless** as the
  build-side **orchestrator** (delegates to a formatter + simple steps:
  import order, license header, trailing whitespace) with the crucial
  **`spotlessApply`** (rewrite/fix, local) vs **`spotlessCheck`**
  (verify/gate, bound to `check`, CI) split + Gradle config snippet.
  **Linter** — **Checkstyle** with an **XML ruleset** (`Checker` →
  `TreeWalker` → check modules: MethodName/ConstantName/
  MissingJavadocMethod/CyclomaticComplexity/MagicNumber/NeedBraces) +
  Gradle `checkstyle` plugin (maxWarnings=0); enforces the T19 rules a
  formatter can't express (names, Javadoc presence, complexity).
  **The three-gate workflow** — format-on-save (IDE) → pre-commit hook
  (Git, T05) → **CI gate** on the PR (authoritative, unbypassable, T05
  branch protection); golden rule: **developer fixes (`apply`), CI
  verifies (`check`)**. **Architecture layer**: both run the **lexer →
  parser → AST** front-end (L0/C01/T03 callback) — a formatter
  **discards layout + pretty-prints the AST** (why it's deterministic +
  erases manual alignment), a linter **walks the AST** matching rule
  patterns (Checkstyle's `TreeWalker`). **Zero runtime impact** — the
  **lexer discards whitespace + comments**, so a formatted and an
  unformatted file compile to **byte-identical `.class`** (T19; provable
  via `javac`+`diff`/`javap`). **Build-time cost, cached/incremental**
  (T02 — inputs = sources + config → `UP-TO-DATE` warm); **never shipped
  in the JAR**. **Common mistakes** (8): formatter/linter confusion,
  `spotlessApply` in CI (hides violations — should be Check), not
  enforcing in CI at all, giant reformat buried in a feature PR (use
  `.git-blame-ignore-revs`, T05 blame callback), formatter↔Checkstyle
  config conflict (perpetual failure), over-configuring Checkstyle,
  fighting the formatter, ignored warnings. **INTERVIEW** 12 Q.
  **Practice (15)** — add Spotless, Check-vs-Apply, bind to `check`,
  prove bytecode identical via diff, add Checkstyle, custom rule,
  division-of-labour demo, format-on-save, pre-commit hook, CI gate, the
  CI anti-pattern, blame-friendly reformat, config conflict, caching
  UP-TO-DATE, explain-it-back. Recap ~6 objectives. Progress 45 →
  46/371 (12.1% → 12.4%); L2 row 14/44 → 15/44 (34%); C02-build-tools
  5/11 → 6/11. Wired the L2/C02 README link.
- Resume at `L2/C02/T07` — Static analysis (PMD, SpotBugs, SonarQube;
  finding bugs/smells/vulns WITHOUT running code — the tier above T06
  linting; PMD source-AST vs **SpotBugs bytecode** distinction,
  SonarQube platform + quality gate + "clean as you code", dataflow
  analysis, the undecidability limit → unavoidable false pos/neg, CI
  gate; §4 full, §4a lighter — AST-vs-bytecode + dataflow mechanism
  anchors it).

### 2026-06-04 (later — L2/C02/T05 Git workflows)
- One topic this round. `L2/C02/T05` Git workflows (branching, PRs,
  rebasing) — **396 lines, 12 Mermaid diagrams + strategy/merge/tool
  tables** at the deep bar (§4 + §4a) — the collaboration layer on
  L0/C01/T10 Git fundamentals (which EXISTS, completed L0/C01).
  **Language layer**: **branching strategies** table (trunk-based:
  one main + tiny branches + feature flags, CI/CD velocity; GitHub Flow:
  main + feature + PR, lightweight default; GitFlow: main/develop/
  feature/release/hotfix, versioned releases, heavy/falling-out-of-favour).
  **Pull requests** — the code-review + CI gate; lifecycle (open→CI
  checks→review→address→approve→merge); draft PRs; branch protection
  (required reviews + status checks, no direct push to main); the PR
  as the unit of review + integration point (T05 forward-link to CI
  T11/formatters T06). **Merge vs rebase** — the core distinction:
  merge creates a 2-parent merge commit (non-linear, preserves reality)
  vs rebase replays commits onto a new base (linear but REWRITES SHAs
  because content-address includes parent, T10 callback); comparison
  table. **The GOLDEN RULE** (never rebase shared/published history —
  rewrites SHAs, diverges others' histories; rebase only local/unshared).
  **Interactive rebase** (-i: pick/squash/fixup/reword/edit/drop/reorder
  to clean WIP before PR). **PR merge strategies** (merge-commit full
  history / squash-and-merge collapse-to-one popular / rebase-and-merge
  linear). **Conflict resolution** — the 3-way merge (base/ours/theirs;
  non-overlapping auto-merged, overlaps flagged); conflict markers
  <<<< ==== >>>>; rebase resolves per-commit. **The toolbox** table —
  stash (shelve WIP), cherry-pick (copy one commit), bisect (binary-
  search the bug commit O(log n), T14 callback), reflog (recover lost
  commits, the safety net), blame (who/when/why a line), revert (undo
  via NEW commit, shared-safe vs reset rewrite). **Architecture layer**
  (tying to T10 object model): **a branch is a 41-byte pointer** (ref
  file with a commit SHA; O(1) create/switch, not a copy; HEAD points to
  the current branch); **merge = 2-parent commit** (DAG diamond),
  **fast-forward** just moves the pointer when no divergence; **rebase =
  NEW commits new SHAs** (content-address + new parent) + OLD commits
  ORPHANED but in the object store, reflog-recoverable until GC (~30
  days); "rewriting history" = the immutable content-addressed DAG can't
  be mutated, so create new commits + move the pointer (why rebase is
  recoverable AND why rebasing shared history is dangerous); 3-way merge
  on tree objects (T10). **Common mistakes** (9): rebasing shared history
  (cardinal sin), force-push without --force-with-lease, merge-vs-rebase
  confusion, giant PRs, long-lived branches (merge hell), committing
  secrets/large-files (in history forever), not pulling before pushing,
  blindly resolving conflicts, working directly on main. **INTERVIEW**
  with 12 questions. **Practice (15 exercises)** — feature branch + PR,
  merge-vs-rebase visual graph, new-SHA-from-rebase, golden-rule
  violation (sandbox), interactive-rebase cleanup, squash-merge,
  conflict resolution, fast-forward vs true merge, stash, cherry-pick,
  bisect, reflog recovery, blame, --force-with-lease protection,
  end-to-end explain-it-back. Recap as ~9 learning objectives. Progress
  44 → 45/371 (11.9% → 12.1%); L2 row 13/44 → 14/44 (32%); C02-build-
  tools 4/11 → 5/11. Wired the L2/C02 README link.
- Resume at `L2/C02/T06` — Code formatters & linters (Checkstyle,
  Spotless; formatter-vs-linter distinction, google-java-format,
  Spotless orchestrator spotlessCheck-vs-Apply, Checkstyle XML rulesets,
  format-on-save + pre-commit + CI-verify workflow, AST parse + zero
  runtime impact T19/T03 callback).

### 2026-06-04 (later — L2/C02/T04 Multi-module projects)
- One topic this round. `L2/C02/T04` Multi-module projects — **393 lines,
  10 Mermaid diagrams + structure/command tables** at the deep bar
  (§4 + §4a). **Language layer**: why multi-module (separation of
  concerns, independent dep sets, reuse, faster incremental builds,
  ENFORCED architectural boundaries). **Maven aggregator/parent
  structure** (parent packaging=pom + <modules>; submodule <parent>
  ref inherits dependencyManagement/properties/plugins, T01 callback;
  aggregator-vs-parent role distinction). **Gradle structure**
  (settings.gradle include; per-module build scripts; convention
  plugins preferred over subprojects{} which hurts config-cache/
  parallelism). **Inter-module deps** (Maven <dependency> on sibling
  GAV; Gradle implementation(project(":domain")) / api(project) for
  transitive exposure, T02 callback; resolve to sibling build output
  not a repo). **The reactor** (Maven) — topological sort of the module
  DAG (T14 callback) so each module builds AFTER its deps; cycle = build
  error; -pl/-am/-amd subset builds. Gradle computes the cross-module
  task DAG. **Common structures** table (layered domain/service/web,
  api/impl split, feature modules, shared/common). **Shared version
  consistency** (parent dependencyManagement / version catalog, T03
  callback). **Architecture layer**: each module → its OWN JAR
  (multi-module = build-time org; runtime = one flat classpath, T03
  callback); **independent modules build in PARALLEL** (mvn -T 4/-T 1C,
  gradle --parallel — independent DAG branches concurrent); **incremental
  rebuild = changed module + transitive dependents** (change domain →
  domain+service+web; change web → only web — the multi-module speed
  win, T02 callback) + **api-vs-implementation shrinks the blast radius**
  (impl change in service doesn't recompile web if web sees only
  service's api, T02/T03); **the no-cycles DAG requirement ENFORCES
  architecture** — web→service→domain valid; domain→web cycle = build
  error = why domain literally CANNOT depend on web (the build tool
  turns an architectural rule into a mechanical guarantee — the
  underrated insight). **Common mistakes** (8): circular module deps,
  version drift, over-modularization overhead, api-overuse for
  inter-module, building-everything-when-subset-would-do,
  everything-in-one-module, subprojects{} cross-config, misreading
  reactor order. **INTERVIEW** with 12 questions. **Practice (14
  exercises)** — 3-module project, inter-module dep + boundary test,
  reactor order, circular-dep build error, subset build -pl -am,
  incremental blast radius (domain vs web change), api-vs-implementation
  blast radius, parallel build, shared versions, version drift, api/impl
  split, convention plugin, module JARs, end-to-end explain-it-back.
  Recap as ~8 learning objectives. Progress 43 → 44/371 (11.6% →
  11.9%); L2 row 12/44 → 13/44 (30%); C02-build-tools 3/11 → 4/11.
  Wired the L2/C02 README link.
- Resume at `L2/C02/T05` — Git workflows (branching strategies,
  PRs, merge-vs-rebase, interactive rebase, merge strategies, conflict
  resolution, stash/cherry-pick/bisect/reflog/blame; builds on L0/C01/T10
  Git intro which EXISTS — commit DAG, branches-as-pointers; the workflow
  layer on top, with branch-as-ref-pointer + merge-2-parents + rebase-
  new-SHAs commit-DAG mechanism).

### 2026-06-04 (later — L2/C02/T03 Dependency management & version conflicts)
- One topic this round. `L2/C02/T03` Dependency management & version
  conflicts — **439 lines, 10 Mermaid diagrams + resolution/linkage-error
  tables** at the deep bar (§4 + §4a) — the deep treatment of what
  T01/T02 introduced. **Language layer**: the dependency graph (direct +
  transitive closure, hundreds of nodes). **Diamond dependencies** (two
  transitive paths to different versions; classpath holds only one).
  **Nearest-vs-highest SIDE-BY-SIDE** — Maven nearest-wins vs Gradle
  highest-wins on the SAME graph picking DIFFERENTLY (declare D:1.0 depth1
  + transitive D:2.0 depth2 → Maven 1.0, Gradle 2.0); the migration/
  reasoning trap. **Diagnosing** — mvn dependency:tree -Dverbose
  (omitted-for-conflict), gradle dependencyInsight --dependency (selected
  + reason + paths). **Overriding** — Maven dependencyManagement
  (authoritative hard-pin) + exclusions; Gradle constraints/force/
  strictly (build-time failure) + exclude. **BOMs** (curated compatible
  version set; Spring Boot BOM; Maven scope=import vs Gradle platform()).
  **Version ranges** [1.0,2.0) — discouraged (non-reproducible, drifts).
  **Dependency locking** (Gradle --write-locks lockfiles; reproducible).
  **JAR HELL — the runtime symptom** (the deep payload): conflicts
  resolved at BUILD time (which JAR on classpath) but symptom at RUNTIME
  — compiled-against-D:2.0.foo(int,int) but D:1.0 on runtime classpath →
  NoSuchMethodError; the **linkage-error family table** (NoSuchMethodError/
  NoSuchFieldError/NoClassDefFoundError/AbstractMethodError/
  IncompatibleClassChangeError); **WHY runtime not compile** — JVM
  resolves symbolic references LAZILY (T04 verify/prepare/resolve), so
  the error surfaces only when the call site first executes (deep in
  prod); compile-success ≠ runtime-safety; distinct from
  UnsupportedClassVersionError (T09 class-file version vs API mismatch
  same Java version). **Shading/relocation** (Shadow/Shade uber JARs;
  rewrite com.google.common → com.mylib.shaded... so bundled copy can't
  collide; library private-copy escape hatch; T11 hidden-dependency
  cost). **Architecture layer**: classpath = flat ordered list,
  **first-match-wins** (L0/C03 callback); **one class per name per
  classloader** (T15 — no version dimension = root cause); **stronger
  isolation** — OSGi (per-bundle classloaders → multiple versions
  coexist), JPMS module-path (strong encapsulation, validated module
  graph, L1/C01 callback); **lazy linkage timing** (why "ran fine for a
  month then crashed" version bugs exist — the broken call site hadn't
  run yet). **Common mistakes** (8): ignoring tree warnings, version
  ranges in prod, transitive-version surprise, compile-success ≠
  runtime-safety, no BOM for a framework family, letting diamonds
  self-resolve, shading everything, not locking. **INTERVIEW** with 12
  questions. **Practice (15 exercises)** — build a diamond, nearest-vs-
  highest, dependencyInsight, force-version (Maven + Gradle), exclusion,
  strictly-failure, BOM, reproduce NoSuchMethodError (compile-2.0 run-1.0),
  NoClassDefFoundError, version-range drift, dependency-lock,
  shading-relocation inspection, classpath first-match-wins order,
  end-to-end explain-it-back (Jackson 2.16-compile 2.13-runtime).
  Recap as ~11 learning objectives. Progress 42 → 43/371 (11.3% →
  11.6%); L2 row 11/44 → 12/44 (27%); C02-build-tools 2/11 → 3/11.
  Wired the L2/C02 README link.
- Resume at `L2/C02/T04` — Multi-module projects (aggregator/parent
  structure, inter-module dependencies, the reactor / topological build
  order, parallel + incremental multi-module builds, shared
  dependencyManagement/version-catalog, no-circular-deps DAG).

### 2026-06-04 (later — L2/C02/T02 Gradle)
- One topic this round. `L2/C02/T02` Gradle (tasks, build scripts,
  dependencies) — **498 lines, 10 Mermaid diagrams + Gradle-vs-Maven +
  configuration tables + Kotlin DSL/TOML examples** at the deep bar
  (§4 + §4a). **Language layer**: **Gradle-vs-Maven philosophy table**
  (programmable DSL vs declarative XML; task DAG vs lifecycle; highest-vs-
  nearest; api-vs-implementation; daemon-vs-fresh-JVM; Android default).
  **Build scripts** — build.gradle (Groovy) vs build.gradle.kts (Kotlin,
  type-safe, PREFERRED); settings.gradle; the Gradle Wrapper (gradlew,
  always use it). **The task graph (DAG)** — Gradle's core abstraction;
  task = name+inputs+outputs+actions+dependsOn; gradle build resolves +
  executes the DAG in dependency order (with a worked DAG diagram).
  **Built-in tasks** (java plugin: compileJava/processResources/classes/
  test/jar/assemble/check/build-aggregate; application: run). **Custom
  tasks** (tasks.register + doLast/doFirst). **Plugins** (plugins DSL;
  core by name vs community by id+version from Portal; java-library adds
  api). **Dependencies + configurations table** (implementation/api/
  compileOnly/runtimeOnly/testImplementation/annotationProcessor mapped
  to Maven scopes). **THE api-vs-implementation distinction** (Gradle's
  headline feature Maven lacks) — implementation HIDDEN from consumers
  (encapsulation + faster builds since changing impl doesn't recompile
  consumers — ABI unchanged), api VISIBLE (java-library, only for
  public-API types); default-to-implementation guidance + the
  recompile-blast-radius benefit. **Highest-version resolution** (vs
  Maven nearest, T01/T03 callback) + resolutionStrategy/constraints +
  **version catalogs** (libs.versions.toml, type-safe, the
  dependencyManagement equivalent). **Repositories** (mavenCentral/
  google/custom; Maven format; ~/.gradle/caches separate from ~/.m2).
  **Architecture layer — WHY Gradle is fast**: **the Gradle Daemon**
  (long-lived warm JVM between builds → avoids JVM startup AND JIT
  re-warmup, the single biggest edge over Maven's fresh-JVM-per-build
  T01; T12 JIT callback); **the build cache** (task outputs keyed by
  input hash; local ~/.gradle + remote shared cache → never build the
  same thing twice across a team); **incremental builds** (input/output
  fingerprints → UP-TO-DATE skip; internally-incremental Java compile;
  far better than Maven staleness T01). **Configuration phase vs
  execution phase** — three phases (init→configuration evaluates ALL
  scripts + builds the DAG EVERY build→execution runs selected tasks);
  config-time work slows every build → execution work belongs in
  doLast; the configuration cache skips the config phase. **Parallel
  task execution** (--parallel across subprojects). **The Gradle
  Wrapper** (pins version per project, reproducible, always ./gradlew).
  **Common mistakes** (8): config-vs-execution phase confusion,
  api/implementation misuse, Groovy dynamic-typing footguns (prefer
  Kotlin DSL), not using daemon/cache, not using wrapper (version
  drift), misreading UP-TO-DATE/FROM-CACHE as a bug, mixing Maven/
  Gradle caches, overriding built-in tasks incorrectly. **INTERVIEW**
  with 12 questions (Gradle-vs-Maven, task graph, api-vs-implementation,
  highest-wins, daemon, build cache, incremental, config-vs-execution,
  wrapper, Groovy-vs-Kotlin, version catalog, why-impl-change-doesnt-
  recompile-consumers). **Practice (15 exercises)** — minimal project,
  task list + DAG (--dry-run), custom task, config-vs-execution
  observation, add dependency, api-vs-implementation two-module demo,
  recompile-blast-radius (UP-TO-DATE on impl change), highest-wins
  conflict, incremental/UP-TO-DATE, build cache FROM-CACHE, daemon
  speedup, version catalog, wrapper version match, Kotlin-vs-Groovy
  typo, end-to-end explain-it-back. Recap as ~11 learning objectives.
  Progress 41 → 42/371 (11.1% → 11.3%); L2 row 10/44 → 11/44 (25%);
  C02-build-tools 1/11 → 2/11. Wired the L2/C02 README link.
- Resume at `L2/C02/T03` — Dependency management & version conflicts
  (the deep treatment: diamond conflicts, nearest-vs-highest side-by-
  side, dependency:tree/dependencyInsight diagnosis, force/constraints/
  exclusions, BOMs, version ranges, dependency locking, JAR-hell runtime
  linkage errors NoSuchMethodError/NoClassDefFoundError, shading/
  relocation, classpath first-match-wins + classloader isolation).

### 2026-06-04 (later — L2/C02/T01 Maven · new chapter started)
- User chose **L2/C02 Build Tools & Workflow** as the next chapter (after
  L2/C01 completed). One topic this round. `L2/C02/T01` Maven (lifecycle,
  POM, dependencies, plugins) — **578 lines, 14 Mermaid diagrams + lifecycle/
  scope/repository tables + pom.xml examples** at the deep bar (§4; §4a
  appropriately lighter for a tooling topic but the under-the-hood section
  ties to JVM mechanism). **Language layer**: Maven as declarative +
  convention-over-configuration; minimal pom.xml. **Coordinates (GAV)** —
  groupId:artifactId:version + packaging, globally unique = the address.
  **Standard directory layout** (src/main/java, src/main/resources,
  src/test/java, target/classes). **The build lifecycle** — three
  lifecycles (default/clean/site); default phases in order (validate→
  compile→test-compile→test→package→verify→install→deploy); **the
  phase-runs-all-prior rule**. **Phases bind to plugin goals** (compile→
  compiler:compile, test→surefire:test, package→jar:jar; bindings from
  packaging type). **Plugins and goals** (plugin:goal syntax, dependency:
  tree, mvn clean install). **Dependencies** — declaration + the **scope
  table** (compile/provided/runtime/test/system/import with classpath/
  packaged/transitive columns); **transitive dependencies**; **dependency
  mediation = NEAREST WINS** (shortest path, first-declared on tie — vs
  Gradle highest; deep conflict handling deferred to T03);
  **dependencyManagement + BOMs** (central versions, import scope);
  **exclusions**. **Repositories** — local ~/.m2 cache, Central, corporate
  remotes; download→cache flow; **coordinates→path** mapping
  (org/springframework/spring-core/6.1.0/spring-core-6.1.0.jar);
  **SNAPSHOT (mutable) vs release (immutable)** + the never-release-on-
  SNAPSHOT warning. **Inheritance + super POM + effective POM**
  (help:effective-pom). **Properties + profiles** (-P, activation).
  **Under the hood**: mvn is a shell script launching a JVM running
  Maven; **compiler plugin → javac → target/classes** (T04 source→
  bytecode callback); **Surefire forks a test JVM** for isolation; **jar
  plugin = zip target/classes + MANIFEST** (T19 packaging callback);
  local repo = filesystem cache; **limited incremental compile** (a
  Gradle advantage, T02 foreshadow). **Common Maven commands** reference.
  **Common mistakes** (9): phase-vs-goal confusion, forgetting
  phase-runs-prior, wrong dependency scope, nearest-wins surprises,
  releasing on SNAPSHOT, over-using install (pollutes ~/.m2), editing
  target/, not gitignoring target/, build-JDK vs --release mismatch.
  **INTERVIEW** with 12 questions (POM, coordinates, lifecycle, phase-vs-
  goal, scopes, transitive, nearest-wins, dependencyManagement, local-vs-
  central, SNAPSHOT-vs-release, clean-install, effective-POM). **Practice
  (15 exercises)** — minimal project, lifecycle trace (mvn -X), phase vs
  goal, add dependency + dependency:tree, scopes, transitive conflict
  (verbose tree), exclusion, dependencyManagement, local-repo path,
  SNAPSHOT install, effective-POM inheritance, properties+profile,
  skipTests, Surefire fork (separate PID), end-to-end explain-it-back.
  Recap as ~11 learning objectives. Progress 40 → 41/371 (10.8% →
  11.1%); L2 row 9/44 → 10/44 (23%); C02-build-tools 0/11 → 1/11.
  Wired the L2/C02 README link.
- Resume at `L2/C02/T02` — Gradle (programmable build tool; task DAG,
  Groovy/Kotlin DSL, configurations api-vs-implementation, highest-
  version resolution, the Gradle Daemon + build cache + incremental
  builds, configuration-vs-execution phases).

### 2026-06-04 (later — L2/C01/T09 version features · 🎉 C01 CHAPTER COMPLETE)
- One topic this round. `L2/C01/T09` New language features by version
  (Java 8 → 21+) — **358 lines, 3 Mermaid diagrams + extensive
  release-by-release tables + feature→version→deep-topic cross-reference**
  at the deep bar appropriate for a survey topic (§4; §4a light — the
  per-feature deep-links carry mechanism). **Release cadence** — 6-month
  feature releases (Mar/Sep) since Java 9; LTS every 2-3 years (8/11/17/
  21/25), the production targets; non-LTS get ~6 months. **Preview
  mechanism** — --enable-preview at compile AND run; can change/be
  removed (string templates withdrawn in 23); preview lifecycle diagram
  (preview → second/third preview → standard). **JEP process**.
  **Per-release tables** (LTS marked) with deep-topic links: Java 8 LTS
  (the functional revolution — lambdas/streams/Optional/method-refs/
  default-static interface methods/java.time/CompletableFuture/
  Metaspace/Nashorn); Java 9 (JPMS modules/List.of/private interface
  methods/stream takeWhile-dropWhile-iterate3-ofNullable/Optional
  stream-or-ifPresentOrElse/JShell/Compact Strings JEP254/
  StringConcatFactory JEP280/G1 default); Java 10 (var JEP286/copyOf/
  orElseThrow/toUnmodifiable); Java 11 LTS (var-in-lambda JEP323/String
  isBlank-strip-lines-repeat/Files.readString/HTTP Client JEP321/
  single-file launcher JEP330/Optional.isEmpty/removed EE+CORBA JEP320);
  Java 12-13 (switch-expr+text-blocks previews; teeing collector 12);
  Java 14 (switch expr STANDARD JEP361/records preview JEP359/pattern
  instanceof preview/helpful NPE JEP358); Java 15 (text blocks STANDARD
  JEP378/sealed preview/hidden classes JEP371/Nashorn removed/ZGC+
  Shenandoah prod); Java 16 (records STANDARD JEP395/pattern instanceof
  STANDARD JEP394/Stream.toList/mapMulti/strong encapsulation JEP396);
  Java 17 LTS (sealed STANDARD JEP409/pattern switch preview JEP406/
  strongly encapsulate internals JEP403 — the big modernisation target);
  Java 18-20 (UTF-8 default JEP400/simple web server JEP408/virtual
  threads preview JEP425/record patterns preview JEP405/structured
  concurrency+scoped values incubator); Java 21 LTS (virtual threads
  STANDARD JEP444/pattern switch STANDARD JEP441/record patterns
  STANDARD JEP440/sequenced collections JEP431/generational ZGC JEP439/
  string templates preview-later-withdrawn); Java 22-25 frontier (FFM
  API standard JEP454/stream gatherers JEP461→485/class-file API/
  simpler main methods/flexible constructor bodies/compact object
  headers JEP450→519/Leyden AOT; Java 25 current LTS Sept 2025).
  **Which-version-to-target** decision guide (new → latest LTS; Java 8
  → migrate to 17/21; --release = deployment target). **Full feature→
  version→deep-topic cross-reference table.** **Common mistakes** (6):
  preview in production, --release too low/high, LTS-vs-non-LTS
  confusion, UnsupportedClassVersionError (newer-than-runtime),
  Java-8-migration gotchas (removed modules/encapsulation), version-
  dependent helpful-NPE. **INTERVIEW** with 12 questions (what's-new-in
  8/11/17/21, cadence, LTS, preview, records-since-16, switch-expr-
  since-14, --release, UnsupportedClassVersionError, JEP). **Practice
  (10 exercises)** — check version, class-file major version (52/55/61/
  65), --release targeting (record fails on 11), preview feature,
  UnsupportedClassVersionError repro, feature archaeology (preview→
  standard release), String-methods-by-version, migration audit
  (sun.*/JAXB), LTS timeline, explain-it-back (min LTS for records +
  pattern switch). Recap as ~7 learning objectives.
- **🎉 MILESTONE: L2/C01 Functional & Modern Java chapter COMPLETE —
  9/9 topics** (T01 lambdas → T02 functional interfaces → T03 method
  refs → T04 streams → T05 collectors → T06 parallel streams → T07
  Optional → T08 FP style+immutability → T09 version features). ~5,150
  lines across 9 topics, ~115 Mermaid diagrams + extensive bytecode/
  javap listings + the full modern-Java toolkit at the deep §4/§4a bar.
  Progress 39 → 40/371 — **crosses 10.8%**; L2 row 8/44 → 9/44 (20%);
  C01-functional 8/9 → 9/9 DONE.
- **Resume at `L2/C02/T01`** — the next chapter, Build Tools & Workflow
  (Maven/Gradle/dependency management/build lifecycle, 11 topics). Read
  the C02 README for the planned T01 filename. (C01 completion is a
  natural milestone — could also check user priorities before opening
  the new chapter.)

### 2026-06-04 (later — L2/C01/T08 FP style & immutability · chapter synthesis)
- One topic this round. `L2/C01/T08` Functional programming style &
  immutability — **474 lines, 11 Mermaid diagrams + recipe/payoff tables**
  at the deep bar (§4 + §4a). The chapter's **synthesis topic** —
  pulls lambdas/functional-interfaces/method-refs/streams/Optional into
  the FP mindset. **Language layer**: the **FP tenets** table (pure
  functions, immutability, first-class functions, referential
  transparency, higher-order + composition, declarative-over-imperative).
  **Pure functions** (deterministic + side-effect-free → testable/
  parallel-safe/memoizable/reasoning-friendly; stream lambdas MUST be
  pure for parallel — T06 callback). **Referential transparency** (expr
  == value; side effects/mutation/exceptions break it). **The immutable-
  class recipe** (final class, private final fields, no setters,
  defensive-copy mutable inputs AND outputs, no this-escape) — String
  the canonical (T06); the `final ≠ immutable` warning (final List still
  mutates). **Records** (JEP 395) — auto final fields/accessors/equals/
  hashCode/toString, implicitly final; **compact constructor** for
  validation/normalisation; **shallow immutability** — copy mutable
  components via List.copyOf in the compact ctor. **Immutable
  collections** table — List.of/copyOf (own storage, truly immutable)
  vs Collections.unmodifiable* (read-only VIEW, backing still mutable —
  the shallow-immutability caveat with a worked demo). **Why
  immutability** payoff table — thread-safety-for-free (no writes → no
  races → no sync, the headline), safe map keys + cached hashCode (T6),
  no downstream defensive copy, easier reasoning, safe publication,
  failure atomicity. **The cost** — copy-on-change allocation; the
  wither pattern; mitigations (generational GC reclaims young copies
  cheaply; structural sharing / persistent data structures — Vavr).
  **Functional error handling** — Optional (T07) / Either / Result over
  null and over throwing for expected absence (exceptions break
  referential transparency). **Memory layer**: immutables shared freely
  (one instance, many refs — String interning, flyweight, caching);
  **the final-field JMM safe-publication guarantee** (JLS §17.5, full
  L3/C01/T12) — WHY immutables are thread-safe WITHOUT locks (a
  properly-constructed object's final fields are visible to all threads
  after construction, no sync; without it, publishing can expose
  partially-constructed state); hashCode caching. **Architecture
  layer**: JIT treats trusted final fields as near-constants (folded/
  cached reads); **escape analysis stack-allocates short-lived
  immutables** (copy-on-change often free, T01/T15); enables lock-free
  sharing (no sync overhead) + memoization; the GC trade-off
  (young-gen churn vs generational-GC-cheap-young-collection + compact-
  immutable cache locality). **Common mistakes** (10 traps): mutable
  field exposed without defensive copy, final-ref-to-mutable-object,
  missing record component copy, aliased mutation, final ≠ deeply
  immutable, unmodifiableList over mutable backing, List.of(mutables)
  shallow, premature immutability on hot path, side effects in stream
  lambdas, throwing where Optional is cleaner. **INTERVIEW** with 12
  questions. **Practice (16 exercises)** — pure-vs-impure, immutable-
  class recipe + defensive copy, final≠immutable, record basics,
  compact-ctor validation, shallow-record-immutability + fix,
  unmodifiableList view, wither pattern, thread-safe sharing,
  hashCode caching, EA on short-lived immutable, copy-on-change churn,
  side-effect-free stream parallel, Optional vs exception,
  deep-vs-shallow, end-to-end explain-it-back. Recap as ~11 learning
  objectives. Progress 38 → 39/371 (10.2% → 10.5%); L2 row 7/44 →
  8/44 (18%); C01-functional 7/9 → 8/9 — **one topic (T09 version
  features) left to COMPLETE the C01 chapter.** Wired the L2/C01 README.
- Resume at `L2/C01/T09` — New language features by version (Java 8 →
  21+; release-by-release survey tying the book's modern-Java threads
  together, LTS cadence, preview mechanism, feature→version→deep-topic
  cross-reference). **Completes the C01 chapter.** After that → L2/C02
  Build Tools & Workflow, or check user priorities.

### 2026-06-04 (later — L2/C01/T07 Optional in depth)
- One topic this round. `L2/C01/T07` Optional in depth — **458 lines, 10
  Mermaid diagrams + API tables + Optional class internals** at the deep
  bar (§4 + §4a). **Language layer**: why Optional exists (null = the
  billion-dollar mistake; doesn't document intent; compiler can't help) —
  Optional puts maybe-absence in the TYPE. **Creation** — of(x) throws
  NPE on null, ofNullable(x) null→empty (the safe factory), empty()
  shared singleton. **Imperative API** (isPresent/isEmpty 11+/get) as
  mostly-to-avoid ("null with extra steps"). **Functional API** — map
  (transform if present, null→empty), flatMap (mapper returns Optional —
  flattens, avoids Optional<Optional>), filter (keep if present AND
  matches), ifPresent/ifPresentOrElse (9+). **Defaulting** — orElse
  (eager!), orElseGet (lazy supplier), orElseThrow()/orElseThrow(Supplier),
  or (9+ fallback Optional chains), stream (9+). **THE orElse-vs-orElseGet
  trap** — orElse(x) evaluates x eagerly at the call site even when
  present (orElse(expensive()) always runs the expensive call / side
  effect); orElseGet(() -> expensive()) is lazy — only when empty.
  **map vs flatMap** rule. **Chaining idiom** (findUser.map.map.filter.
  orElse — null-safe, short-circuits). **Optional.stream()** +
  flatMap(Optional::stream) to drop empties from a Stream<Optional>.
  **OptionalInt/Long/Double** (T17 boxing callback — no map/flatMap/
  filter). **Best-practice rules** (Effective Java 55) — return-type
  ONLY; NEVER field/parameter/collection (use empty collection); never
  get() without proven presence; don't wrap a collection in Optional;
  Optional not Serializable by design. **Memory layer**: Optional is a
  final class with one `final T value` (null == empty); empty() returns
  a shared EMPTY singleton (ZERO allocation); of(x) allocates one
  16-byte wrapper (12 hdr + 4 ref + 4 pad, T17 layout) holding the
  reference (value not copied). **Optional<Integer> allocates TWICE**
  (box int → Integer 16B + wrap → Optional 16B = 32B) vs OptionalInt
  (primitive int + boolean isPresent, no boxing) — WHY the primitive
  optionals exist. **Escape analysis eliminates short-lived Optionals**
  (T01/T15/T17) — findUser.map.orElse where the Optional doesn't escape
  is scalar-replaced → typically 0 allocation after JIT (return-and-
  immediately-consume pattern nearly free); EA fails when stored in a
  field / returned up layers / passed to non-inlined call. **Architecture
  layer**: Optional = alloc (unless EA) + virtual method calls vs a raw
  one-comparison null check — WHY the JDK's hot internals (HashMap/
  ArrayList) still use null; Optional is for API clarity at BOUNDARIES,
  not perf-critical hot paths; EA mitigates the return-and-consume
  pattern; megamorphic caveat (T01/T02) on shared Optional-mapping
  utilities. **Common mistakes** (10 traps): of(null) NPE, get()
  without guard, orElse-eager, Optional field, Optional parameter,
  Optional<List> instead of empty list, isPresent+get instead of
  functional, nesting Optionals, Optional in hot loop, orElse(null),
  serializing Optional. **INTERVIEW** with 12 questions. **Practice
  (16 exercises)** — of-vs-ofNullable, orElse-eager-trap demo, map
  chain, map-vs-flatMap (Optional<Optional>), filter, ifPresentOrElse,
  orElseThrow, or-chain (cache→db), Optional.stream flatten, OptionalInt
  no-boxing, empty-singleton identity, EA elimination via
  PrintEliminateAllocations, double-allocation measurement, best-practice
  violations + fixes, isPresent+get→functional refactor, end-to-end
  explain-it-back. Recap as ~11 learning objectives. Progress 37 →
  38/371 (10.0% → 10.2%); L2 row 6/44 → 7/44 (16%); C01-functional 6/9 →
  7/9 (TWO topics left: T08 FP style + immutability, T09 version
  features — then the C01 chapter is complete). Wired the L2/C01 README.
- Resume at `L2/C01/T08` — Functional programming style & immutability
  (the chapter's synthesis topic — pure functions, immutability,
  records, immutable collections, thread-safety-for-free, JMM safe
  publication, structural sharing, side-effect-free streams).

### 2026-06-04 (later — L2/C01/T06 Parallel streams · L2 crosses 10% of total)
- One topic this round. `L2/C01/T06` Parallel streams — **434 lines, 9
  Mermaid diagrams + source/order tables + decision guide** at the deep
  bar (§4 + §4a). **Language layer**: parallelStream()/parallel()/
  sequential() — whole-pipeline flag, last-call-wins, no per-stage
  mixing. **The four correctness contracts** parallel imposes (sequential
  doesn't care): stateless ops, non-interfering (no source modification),
  **associative accumulator + true identity** (subtraction non-associative
  → wrong non-deterministic results; wrong identity combined per chunk),
  no side effects on shared mutable state (forEach(list::add) data race →
  use collect). The **silently-wrong-results** danger (no exception).
  **Encounter order** table (forEach unordered/fast vs forEachOrdered
  ordered/slow; findAny parallel-friendly vs findFirst ordered;
  unordered() relaxes). **Memory layer**: **Spliterator.trySplit()**
  recursive decomposition into a balanced binary chunk tree (T04
  callback); **good splitters** (ArrayList/arrays/IntStream.range —
  O(1) balanced midpoint, SIZED+SUBSIZED) vs **bad** (LinkedList O(n)
  unbalanced, Stream.iterate sequential, Files.lines/generate IO — poor
  or no split → no parallel benefit). **Architecture layer**: the shared
  **ForkJoinPool.commonPool** (size cores−1, JVM-wide, -Djava...
  parallelism); **fork/join** recursive task tree (split→compute leaves
  →join/merge, full mechanism L3/C01/T13); **work-stealing** (idle
  threads steal from busy deques' tails, self-balancing); the
  **overhead** (split+schedule+merge+contention) making parallel SLOWER
  for small N/cheap Q; the **N×Q rule** (~10k cheap ops or fewer
  expensive — Goetz/Lea); the **blocking-task hazard** (blocking I/O on
  the common pool ties up shared threads → starves the WHOLE JVM's
  parallel machinery + default CompletableFutures → app freeze; fixes:
  don't-parallelise-blocking / custom ForkJoinPool / async+virtual-
  threads); **custom-pool isolation trick** (pool.submit(() ->
  ...parallelStream()...).get() runs the stream on that pool);
  **concurrent collectors** avoid the merge (T05 callback). **Full
  when-to-parallelise decision-guide diagram** (large N×Q + splittable
  source + stateless/associative + no-blocking + measured → parallelise;
  else sequential). Worked associativity example (sum OK, subtraction
  wrong). **Common mistakes** (10 traps): small/cheap parallel,
  stateful/side-effecting lambdas, forEach(shared::add) race,
  non-associative reduce, blocking-I/O starvation, forEach-order
  assumption, LinkedList/iterate/Files.lines source, no-JMH-warmup,
  wrong identity, nesting parallel streams. **INTERVIEW** with 12
  questions. **Practice (16 exercises)** — parallel-vs-seq small (slower)
  and large+expensive (speedup), N×Q sweep, non-associative wrongness,
  wrong-identity, forEach order, forEach data race, trySplit observation
  (ArrayList vs LinkedList), source-matters benchmark, common-pool size,
  blocking-starvation demo + custom-pool fix, custom-pool isolation,
  groupingBy vs groupingByConcurrent, unordered speedup, findAny vs
  findFirst, end-to-end explain-it-back. Recap as ~11 learning
  objectives. Progress 36 → 37/371 — **crosses 10.0% of the whole book**;
  L2 row 5/44 → 6/44 (14%); C01-functional 5/9 → 6/9 (two topics left in
  the chapter: T07 Optional, T08 FP style, T09 version features — wait,
  three: T07/T08/T09). Wired the L2/C01 README link.
- Resume at `L2/C01/T07` — Optional in depth (creation, functional
  consumption map/flatMap/filter, orElse-vs-orElseGet trap, best-practice
  rules, OptionalInt boxing, EA elimination).

### 2026-06-04 (later — L2/C01/T05 Collectors & grouping)
- One topic this round. `L2/C01/T05` Collectors & grouping — **549 lines,
  11 Mermaid diagrams + full Collectors catalogue tables + groupingBy
  internals** at the deep bar (§4 + §4a). **Language layer**: `collect`
  as **mutable reduction** vs `reduce` **immutable fold** — the O(N) vs
  O(N²) lesson (immutable-copy-per-step is the canonical perf mistake,
  T07 callback). The **`Collector<T,A,R>` interface** five components
  (supplier/accumulator/combiner/finisher/characteristics —
  CONCURRENT/UNORDERED/IDENTITY_FINISH) + the **3-arg collect(supplier,
  accumulator, combiner)** form exposing the first three. **Collectors
  catalogue**: toList/toSet/toCollection/toUnmodifiable*; toMap (+merge
  +supplier) with the **duplicate-key IllegalStateException trap** and
  merge-function fix (keep-first/last/sum/concat); joining (StringBuilder
  internally — avoids O(N²)); counting/summingInt/averagingInt(→Double)/
  summarizingInt/minBy/maxBy/reducing; adapting downstream collectors
  mapping/filtering/flatMapping/collectingAndThen. **groupingBy** — the
  workhorse (classifier → Map<K,List<T>>; single-arg defaults downstream
  to toList) with a downstream-collector table (count/avg/names/highest-
  paid/set per group), custom map type (TreeMap/LinkedHashMap), nested
  grouping (Map<Dept,Map<Title,List<Emp>>>), groupingByConcurrent.
  **partitioningBy** (boolean → Map<Boolean,List<T>>) with its two
  differences (ALWAYS both true+false keys; optimised 2-entry map).
  **teeing** (Java 12+ — two collectors one pass). **Memory layer**:
  the container is mutated in place (one ArrayList grown via add(),
  amortised O(1), ~log N reallocs — T07; not N immutable copies);
  **groupingBy internals** worked out — `map.computeIfAbsent(key, k ->
  downstream.supplier())` per key then `downstream.accumulator(group,
  element)`; downstream finisher per group unless IDENTITY_FINISH.
  **Architecture layer**: **sequential collect** = supplier ONCE +
  accumulator per element + finisher ONCE, **combiner NEVER**;
  **parallel collect** = per-thread containers (supplier per thread) +
  **combiner merge** pairwise (must be associative) — WHY collect is
  parallel-safe while ad-hoc forEach(list::add) is a data race (T04
  callback); **concurrent collectors** (groupingByConcurrent/
  toConcurrentMap, CONCURRENT+UNORDERED) share ONE ConcurrentHashMap
  across threads, accumulate concurrently, NO merge — faster for
  unordered parallel; **IDENTITY_FINISH** skips the finisher (toList/
  toSet/groupingBy have it; toUnmodifiableList/collectingAndThen/
  averagingInt don't). **Common mistakes** (9 traps): toMap dup-key
  throw, reduce-for-mutable O(N²), ordering with toSet/groupingBy
  (HashMap unordered → LinkedHashMap/TreeMap), modifying unmodifiable
  result, partitioningBy-always-both-keys, collector return-type
  confusion (averagingInt→Double), null classifier result,
  collect(toList()) vs cleaner Stream.toList(), forgetting the
  downstream default. **INTERVIEW** with 12 questions. **Practice
  (17 exercises)** — collect-vs-reduce O(N)-vs-O(N²) benchmark, 3-arg
  collect, toMap dup-key + merge fix, joining-vs-reduce, count/avg/
  names per group, nested grouping, partitioningBy-empty-partition,
  ordering (HashMap/TreeMap/LinkedHashMap), teeing, collectingAndThen,
  summarizingInt, sequential-vs-parallel combiner-call observation,
  groupingByConcurrent benchmark, word-frequency two ways, end-to-end
  explain-it-back. Recap as ~9 learning objectives. Progress 35 →
  36/371 (9.4% → 9.7%); L2 row 4/44 → 5/44 (11%); C01-functional 4/9 →
  5/9. Wired the L2/C01 README link.
- Resume at `L2/C01/T06` — Parallel streams (trySplit decomposition,
  ForkJoinPool.commonPool, work-stealing, N×Q rule, blocking-starvation
  hazard, when-to-parallelise guide).

### 2026-06-04 (later — L2/C01/T04 Streams API · the big one)
- One topic this round (per one-at-a-time instruction). `L2/C01/T04`
  Streams API (intermediate & terminal operations) — **679 lines, 14
  Mermaid diagrams + extensive op tables + the deep Sink-chain
  internals** at the deep bar (§4 + §4a). **Language layer**: stream
  defined by three properties — **lazy** (intermediate ops do nothing
  until a terminal op), **single-use** (reuse → IllegalStateException),
  **possibly infinite** (must short-circuit); a stream is **NOT a data
  structure** (stores no elements; a view over a source). Pipeline
  anatomy (source → 0..N intermediate → 1 terminal). **Sources** table
  (collection.stream, Arrays.stream, Stream.of/iterate/generate,
  IntStream.range, String.chars, Files.lines + close-it warning).
  **Intermediate ops** split stateless (filter/map/mapToInt/mapToObj/
  flatMap/mapMulti/peek) vs stateful (distinct/sorted/limit/skip/
  takeWhile/dropWhile) with the distinction explained (stateless needs
  only the current element; stateful keeps state — sorted BUFFERS ALL).
  flatMap diagram. **peek-is-debugging-only** warning (may not run —
  count() skip). **Terminal ops**: reduction (reduce/collect/count/
  min/max/sum/summaryStatistics), short-circuiting search/match
  (anyMatch/allMatch/noneMatch/findFirst/findAny), iteration (forEach/
  forEachOrdered), to-array/toList (Java 16+ unmodifiable). **Laziness
  model** with a worked TRACE proving depth-first one-at-a-time flow
  ('a' filtered out → 'bb' filter+map+forEach → 'ccc' ...) and the
  naive-two-intermediate-lists contrast. **Short-circuiting** on
  infinite streams. **Single-use** + create-fresh-from-source.
  **Encounter order** (forEach vs forEachOrdered; findAny vs findFirst;
  unordered()). **Primitive streams + boxing** (T02/T17 callback).
  **Memory layer** — THE deep part: the pipeline is a **linked chain
  of stage objects** holding the lambdas (nothing traversed at build
  time); the terminal op builds a **Sink chain** (Sink = Consumer +
  begin/accept/end/cancellationRequested); wrapSink wraps from last
  stage back to source so the HEAD sink is the first op, cascading to
  the terminal sink; the **Spliterator** drives the source pushing each
  element into the head sink; elements flow **ONE AT A TIME, depth-
  first** through the whole chain (no per-stage collection — the key
  efficiency); **stateful ops materialise** (sorted buffers all,
  distinct keeps a HashSet, limit a counter — breaking the flow).
  **Spliterator** characteristics (ORDERED/SORTED/DISTINCT/SIZED) enable
  optimisations (SIZED → count() skips traversal → peek may not run).
  Memory-footprint summary table. **Architecture layer**: the JIT
  **fuses + inlines** the stateless sink chain into effectively one
  loop body after warm-up (abstraction nearly free; EA eliminates
  per-element wrappers); **stateful ops break the fusion** (materialise
  + extra pass → push filter BEFORE sorted); **megamorphic cliff**
  (T01/T02) when a stream is reused with many lambda types;
  short-circuit via cancellationRequested; honest **stream-vs-for-loop
  comparison** (within ~1-2× after warmup, often equal; plain for wins
  on trivial hot loops via RCE+SIMD from T09; streams win on
  readability/complex pipelines/parallelism); parallel-stream preview
  (trySplit → ForkJoinPool.commonPool, full in T06). **Common mistakes**
  (11 traps): reusing a stream, side-effects/stateful-lambdas in ops,
  peek-for-logic, no-terminal-op (does nothing), forEach-to-build-a-
  collection (data race in parallel; use collect), boxing via
  Stream<Integer>, infinite-without-limit (hangs), sorted on
  non-Comparable (CCE), modifying source mid-stream (CME), not closing
  Files.lines (resource leak), overusing streams for trivial loops.
  **INTERVIEW** with 12 questions. **Practice (17 exercises)** — lazy
  trace, no-terminal-nothing, reuse-throws, short-circuit-on-infinite,
  limit-on-infinite, flatMap, stateful-buffering (sorted memory spike +
  infinite hang), filter-before-sort, primitive-vs-boxed benchmark,
  peek-unreliability (count skip), toList immutability, forEach vs
  forEachOrdered parallel, Spliterator characteristics, count() skip,
  stream-vs-loop benchmark, Files.lines leak, end-to-end explain-it-back.
  Recap as ~13 learning objectives spanning all three layers. Progress
  34 → 35/371 (9.2% → 9.4%); L2 row 3/44 → 4/44 (9%); C01-functional
  3/9 → 4/9. Wired the L2/C01 README link.
- Resume at `L2/C01/T05` — Collectors & grouping (Collector interface,
  Collectors factory catalogue, groupingBy/partitioningBy/teeing,
  mutable-reduction internals, parallel combiner/CONCURRENT collectors).

### 2026-06-04 (later — L2/C01/T03 Method & constructor references)
- One topic this round (per user's one-at-a-time instruction). `L2/C01/T03`
  Method & constructor references — **482 lines, 9 Mermaid diagrams +
  reference tables + worked javap (direct MethodHandle vs synthetic
  lambda$)** at the deep bar (§4 + §4a). **Language layer**: `::` as a
  compact lambda for a pure pass-through call; same target typing as
  lambdas. **The four kinds** — static (`ClassName::staticMethod`),
  bound instance (`instance::method`, receiver fixed), unbound instance
  (`ClassName::instanceMethod`, receiver becomes first arg — `String::
  length`), constructor (`ClassName::new`) + the array-constructor
  variant (`Type[]::new` = `IntFunction<Type[]>`). The **bound-vs-unbound
  distinction** (the #1 confusion) — `greeting::length` is `Supplier`
  (receiver fixed) vs `String::length` is `Function<String,Integer>`
  (receiver = arg). `this::`/`super::` forms. The **eager-receiver
  gotcha** (a bound ref evaluates its receiver expression ONCE at
  creation, not per call — NOT equivalent to the lambda if the receiver
  has side effects). **When `::` can't replace a lambda** (reorder/
  transform/constant/extra-work/chain → lambda). **Overload + static-
  vs-unbound resolution** (compiler picks by target descriptor;
  ambiguity → cast or lambda). **Memory layer**: same `invokedynamic`
  + `LambdaMetafactory` as lambdas — BUT for DIRECT refs **no synthetic
  method** is generated; the bootstrap's `MethodHandle` points STRAIGHT
  at the target. Worked javap: `viaLambda` has synthetic `lambda$main$0`
  + handle `REF_invokeStatic Demo.lambda$main$0`; `viaRef` (`String::
  length`) has NO synthetic method + handle `REF_invokeVirtual
  String.length` (direct). **The five MethodHandle reference kinds**
  table (`REF_invokeStatic`/`invokeVirtual`/`invokeInterface`/
  `invokeSpecial`/`newInvokeSpecial`). **Capture behaviour** mirrors
  T01 — static/unbound/constructor refs are non-capturing singletons
  (0 alloc after first); bound refs (incl. `this::`) capture the
  receiver (1 obj per receiver; `this::` leak risk). **Architecture
  layer**: one less indirection (no synthetic wrapper → cheaper link,
  smaller class); JIT inlines through the cached CallSite at monomorphic
  sites; unbound refs to virtual methods dispatch on the first arg's
  runtime type (devirtualised when monomorphic); megamorphic cliff still
  applies; `this::method` leak (T01 callback). **Common mistakes** (8
  traps): bound/unbound confusion, eager-receiver gotcha, `this::` leak,
  expecting reorder/transform, overload/static-vs-unbound ambiguity,
  array-ctor confusion (`Type[]::new` not `Type::new`), boxing sneaking
  in (prefer `ToIntFunction` target), reference identity. **INTERVIEW**
  with 12 questions. **Practice (16 exercises)** — four kinds,
  bound-vs-unbound shape, Comparator.comparing, Stream.toArray,
  javap direct-handle vs synthetic, constructor handle kind,
  non-capturing-singleton vs bound-capture, eager-receiver gotcha,
  this:: leak, overload disambiguation, static-vs-unbound, primitive
  target via `::`, map with ctor ref, super:: form, can't-replace
  cases, end-to-end explain-it-back. Recap as ~11 learning objectives.
  Progress 33 → 34/371 (8.9% → 9.2%); L2 row 2/44 → 3/44 (7%);
  C01-functional 2/9 → 3/9. Wired the L2/C01 README link.
- Resume at `L2/C01/T04` — Streams API (the big one; budget ~110-130 min
  for full depth — lazy pipeline, Sink-chain internals, intermediate vs
  terminal ops, short-circuiting, single-use, fusion + JIT inlining).

### 2026-06-04 (later — L2/C01/T02 Functional interfaces)
- Per user instruction ("one topic at a time for quality/depth"), authored
  a single topic this round. `L2/C01/T02` Functional interfaces
  (Function, Predicate, Supplier, Consumer) — **615 lines, 12 Mermaid
  diagrams + full java.util.function reference tables + bytecode listings**
  at the deep bar (§4 + §4a). **Language layer**: the **four core shapes**
  — Function<T,R> (apply, T→R), Predicate<T> (test, T→boolean),
  Supplier<T> (get, ()→T), Consumer<T> (accept, T→void) — distinguished by
  input arity + output kind, with deliberately distinct method names.
  **Arity-2 variants** (BiFunction/BiPredicate/BiConsumer; no BiSupplier).
  **Operator specialisations** (UnaryOperator<T> = Function<T,T>;
  BinaryOperator<T> = BiFunction<T,T,T>). **Combinators** — Function
  andThen/compose/identity (andThen = this-first; compose = other-first,
  like f∘g), Predicate and/or/negate/isEqual/not (static `not` for method
  refs — Java 11+), Consumer andThen, BinaryOperator minBy/maxBy.
  **The full 43-interface primitive-specialisation matrix** with every
  interface + method signature in grouped tables: primitive suppliers
  (IntSupplier etc. + BooleanSupplier), consumers, predicates, primitive-in
  IntFunction<R>, To-projections (ToIntFunction<T>), 6 cross-conversions
  (IntToLong etc.), unary/binary operators, ObjIntConsumer + ToIntBiFunction.
  **Legacy/other FIs** — Runnable (()→void), Callable<V> (()→V throws
  Exception — vs Supplier which can't throw checked), Comparator<T>
  (one abstract compare + rich comparing/thenComparing/reversed/
  nullsFirst/naturalOrder combinators). **Memory layer**: the KEY depth —
  WHY 43 interfaces: generics can't hold primitives → Function<Integer,
  Integer> erases to apply(Object):Object → BOXES; primitive specialisations
  stay primitive. **Byte-level boxing analysis** — worked synthetic-method
  bytecode for `x -> x+1` as Function<Integer,Integer> (intValue unbox +
  iadd + Integer.valueOf box) vs IntUnaryOperator (just iadd, descriptor
  (I)I). **Descriptor comparison table** (Object/Object erased vs I/I vs
  Object/I vs I/Object). Quantified: a 1M-element boxed stream allocates
  ~2M Integers (~32 MB garbage) vs zero for the primitive path; ~10-50×
  throughput — same lesson as IntStream vs Stream<Integer> (T17).
  **Architecture layer**: combinator chains build a small **object graph**
  — each andThen/compose allocates a new capturing lambda holding its
  operands (worked diagram of times2.andThen(plus1).andThen(toStr) = 2
  wrapper objects); the JIT **inlines the whole chain when monomorphic**
  (runs as fast as straight-line code, EA eliminates allocation); the
  **megamorphic cliff** (T01/T02) when a combinator-built function is fed
  through a shared call site with many types; **why the JDK pre-generated
  43** (numeric-stream throughput; no value-type generics yet — Project
  Valhalla will eventually generalise this). **Common mistakes** (9 traps):
  boxed Function<Integer,Integer> in hot loops, Supplier/Function
  confusion, expecting Consumer to return a value, andThen/compose order
  mix-up, negate() vs static not(), Function<T,Boolean> instead of
  Predicate<T>, stateful FIs across threads (data race in parallel
  streams), expecting BiFunction.compose (doesn't exist), reinventing
  standard interfaces. **INTERVIEW** with 12 questions (four shapes,
  Supplier vs Callable, andThen vs compose, why 43 interfaces,
  Function<Integer,Integer> vs IntUnaryOperator, when to write custom,
  UnaryOperator, negate vs not, is Comparator a FI, Function<T,Boolean>
  vs Predicate, andThen wraps-or-fuses, BiSupplier nonexistence).
  **Practice (16 exercises)** — four shapes, andThen-vs-compose,
  predicate combinators + short-circuit, Predicate.not with method ref,
  boxing bytecode (javap synthetic method comparison), boxing benchmark
  (10M ints, ~10-50× + GC), custom throwing interface, Comparator chain,
  comparingInt vs comparing, combinator-allocation in-loop-vs-hoisted,
  Function.identity in collector, BinaryOperator.minBy, Consumer fan-out,
  stateful-consumer race, megamorphic functional call site, end-to-end
  explain-it-back of IntStream.map vs boxed-stream.map. Recap as ~10
  learning objectives spanning all three layers. Progress 32 → 33/371
  (8.6% → 8.9%); L2 row 1/44 → 2/44 (5%); C01-functional 1/9 → 2/9.
  Wired the L2/C01 README topic link.
- Resume at `L2/C01/T03` — Method & constructor references.

### 2026-06-04 (later — PIVOT L3 → L2 · L2/C01/T01 Lambda expressions)
- **User pivot:** redirected this session from L3 to **L2** ("start with L2
  first"). The L3/C01/T01 Threads & Runnable authored just before stays
  complete (needed eventually; depth-bar quality) but L3 is now paused;
  L2 is the active module. Reframed PROGRESS §3/§4/§5 accordingly.
- Authored `L2/C01/T01` Lambda expressions — **698 lines, 17 Mermaid
  diagrams + worked javap (invokedynamic + BootstrapMethods + synthetic
  method)** at the deep bar (§4 + §4a). **Language layer**: lambda =
  anonymous function, an instance of a **functional interface** (SAM —
  single abstract method); `@FunctionalInterface` enforcement; what counts
  as the one abstract method (default/static/private/Object methods don't
  count). All **syntax forms** — `()->`, `x->`, `(x,y)->`, `(int x)->`,
  `(var x)->` (Java 11+ for annotations), expression vs block body. **Target
  typing** — a lambda has no standalone type; inferred from the target
  functional interface at assignment/argument/return/cast; can't assign to
  bare `var`; interaction with overload resolution (cast to disambiguate).
  **Variable capture** — effectively-final locals (captured by value),
  instance fields (via captured `this`), static fields (direct); **why
  effectively-final** (lambda may outlive the stack frame, so copies the
  value; reassignment would diverge). **Accidental `this` capture →
  memory leak** (reading a field captures the whole enclosing object; copy
  to a local to avoid). **Lambda `this` vs anonymous-inner-class `this`**
  (the top interview distinction) — lambda `this` = enclosing instance, no
  new scope, can't self-reference; anon-class `this` = the anon instance,
  own fields/scope. **Checked exceptions** — only those the target method
  declares; standard JDK interfaces declare none; 3 workarounds (unchecked
  wrap, custom throwing interface, Callable). **Memory layer** — the KEY
  DEPTH: **lambdas do NOT compile to anonymous inner classes**. They
  compile to **`invokedynamic`** + a private **synthetic method**
  (`lambda$main$0`) + a **`LambdaMetafactory.metafactory`** bootstrap;
  **no extra `.class` file** at compile time (contrast anon classes →
  `Outer$1.class`). Worked `javap -c -p` showing the invokedynamic at the
  use site + the synthetic method; `javap -v` showing the BootstrapMethods
  attribute with the MethodHandle to the synthetic body. **Runtime
  mechanism** — first execution bootstraps the metafactory → spins up a
  **hidden class** (JEP 371, Java 15+; replaced the old
  `Unsafe.defineAnonymousClass`) implementing the interface → returns a
  cached **CallSite**; subsequent executions reuse it. **Non-capturing
  lambdas = singletons** (one reused instance, zero allocation after
  first); **capturing lambdas = a new object per evaluation** holding
  captured fields. **Why invokedynamic** — no compile-time class
  explosion, runtime-upgradable strategy, the non-capturing singleton
  optimisation. **Architecture layer** — after warm-up the JIT inlines
  through the linked CallSite (lambda as fast as a direct call);
  **escape analysis** eliminates non-escaping capturing-lambda allocation
  (why stream pipelines allocate nothing for the lambda in the common
  case); the **megamorphic cliff** — a shared lambda-consuming method
  (`transform(list, fn)` called with many different lambdas) drives its
  internal `fn.apply` call site megamorphic, collapsing inlining; the
  perf fix (let the JIT inline the helper into each caller). Comparison
  to C function pointers / C++ closures / JS-Python by-reference capture.
  **Common mistakes** (9 traps): lambdas-are-anon-classes misconception,
  `this` confusion, non-effectively-final capture, mutating captured
  state (data race in parallel streams/executors), accidental `this`-
  capture leak, checked exceptions in standard interfaces, over-long
  lambdas (extract to method + method ref), the megamorphic cliff,
  relying on lambda identity (`==`). **INTERVIEW callout** with 12
  questions covering what-a-lambda-is, functional interface/SAM,
  do-lambdas-compile-to-anon-classes (NO), target typing, why
  effectively-final, this-semantics difference, non-capturing-singleton
  vs capturing-allocation, LambdaMetafactory, hidden classes (JEP 371),
  lambda perf + megamorphic cliff, checked exceptions, why invokedynamic.
  **Practice (16 exercises)** — three syntaxes, target typing + boxing,
  var rejection, effectively-final, this-semantics demo, javap the
  lambda (find invokedynamic + synthetic + BootstrapMethods), no-extra-
  class-file proof (vs anon classes generating $1/$2/$3.class),
  non-capturing-singleton identity-hash verification, capturing-
  allocation distinct-identity + GC, escape-analysis observation via
  PrintEliminateAllocations, megamorphic-cliff benchmark, this-capture
  memory-leak repro + fix, checked-exception 3 fixes, overload
  ambiguity + cast, self-reference impossibility, full end-to-end
  "explain it back". Recap as ~14 learning objectives spanning all
  three layers. Progress 31 → 32/371 (8.4% → 8.6%); L2 row 0/44 → 1/44;
  C01-functional 0/9 → 1/9. Wired the L2/C01 README topic link.
- Resume at `L2/C01/T02` — Functional interfaces (Function/Predicate/
  Supplier/Consumer).

### 2026-06-04 (later — L3 started · T01 Threads & Runnable)
- User picked **L3** (Advanced Java & the JVM) as the next module for this
  session (L1 stays with the parallel session; L0 fully complete).
- Authored `L3/C01/T01` Threads & Runnable — **684 lines, 12 Mermaid
  diagrams + tables + stack-layout ASCII** at the deep bar (§4 + §4a).
  **Language layer**: thread = unit of execution within a process; the
  three motivations (latency hiding, parallelism, responsiveness); `Thread`
  vs `Runnable` separation — Thread is the worker, Runnable is the work;
  **five ways to start a thread** — implement Runnable + new Thread,
  lambda-over-Runnable (modern shorthand), extending Thread (avoid),
  `Thread.ofPlatform()...start(...)` (modern factory, Java 21+),
  `Thread.ofVirtual()...start(...)` (Loom preview); the **start() vs
  run() beginner trap** (run is synchronous; start spawns); one-shot
  semantics (`IllegalThreadStateException` on second start); **join()**
  for waiting; **naming discipline** (thread name, threadId(), debug
  visibility); **ThreadGroup** (mostly deprecated); **daemon vs user
  threads** (user keeps JVM alive; daemon doesn't; setDaemon before
  start); **priorities** (advisory on most OSes; don't tune perf with
  it); **uncaught exception handlers** (per-thread + global default; a
  thread that throws without a handler dies silently — production
  must install). **Memory layer**: Thread object byte layout (~200 B
  fields on heap incl. tid, name, priority, group, target, daemon,
  threadStatus, eetop native handle, stackSize, parkBlocker,
  threadLocals); **the eetop native handle** points to HotSpot's
  `JavaThread` C++ structure which holds JVM TLS (TLAB pointers, GC
  state, lock metadata, JIT compile state). **1-to-1 platform-thread
  mapping**: one Java platform thread = one OS kernel thread (~16 B
  heap header + ~200 B Java fields + native handle + ~1 MB virtual
  stack + ~1-2 KB kernel TCB; ~50-100 µs to create). **Per-thread
  stack ASCII layout** (high addresses, stack grows down, guard page,
  -Xss bytes). **Heap is shared** across all threads; only the stack
  is per-thread. **Architecture layer**: **kernel scheduler** —
  Linux CFS (Completely Fair Scheduler, virtual-runtime-based,
  1-4 ms slices, nice value -20..19), Windows MLFQ (32 priorities,
  ~15 ms desktop / 120 ms server quantum), macOS Mach-derived;
  preemption + time-slicing diagrammed. **Context switch cost** —
  ~1-10 µs direct (register save + restore + potential TLB flush) +
  indirect cache-eviction cost (often dominant); **~1000× a method
  call**. **Native thread mechanics** — Linux/macOS `pthread_create`
  with HotSpot's `java_start` C++ entry that sets up JVM TLS; Windows
  `CreateThread`. **JNI attach** — native threads can call Java code
  via `AttachCurrentThread`. **Pre-Loom thread cap** — ~16k platform
  threads on 64-bit Linux due to virtual address space; `ulimit -u`
  and `ulimit -s` interact; `OutOfMemoryError: unable to create new
  native thread` is the failure mode. **Virtual threads (T14) remove
  the cap.** **Common mistakes** (9 traps): run() instead of start(),
  start() twice, no thread name (Thread-7 useless in stack traces),
  extending Thread + coupling work to worker, setDaemon after start,
  swallowing uncaught exceptions, premature priority tuning,
  unbounded `new Thread()` per request (thread leak), Thread.sleep as
  synchronisation. **INTERVIEW callout** with 12 questions covering
  Thread vs Runnable, start vs run, start-twice exception, daemon vs
  user, underlying OS mechanism (pthread_create / CreateThread),
  thread cost (~1 MB + ~50-100 µs), context-switch cost (~1-10 µs),
  join, uncaught-exception silent death, thread cap, weak priority
  semantics, the JVM main thread. **Practice (17 exercises)** —
  three forms of same task, naming, start vs run, start-twice,
  join-for-completion, daemon-keeps-JVM-alive negative test,
  setDaemon-after-start exception, uncaught exception silent death
  + per-thread handler, default uncaught handler, push-thread-count-
  to-limit (`OutOfMemoryError`), inspect thread metadata, spawn cost
  vs method-call cost (~1000×), context-switch microbench with
  volatile flag, ofPlatform factory, ofVirtual million-threads
  preview, jcmd Thread.print thread dump, JNI attach (advanced).
  Recap as ~17 learning objectives spanning all three layers.
  Progress 30 → 31/371 (8.1% → 8.4%); L3 row updated 0/41 → 1/41 (2%);
  C01-concurrency 0/17 → 1/17.
- Resume at `L3/C01/T02` — Thread lifecycle & states.

### 2026-06-04 (final — L0 cross-cutting DEEPENED + READMEs wired)
- **User correction (second round):** previous cross-cutting files (C03–C09)
  were too shallow, and the chapter READMEs didn't link to the topic files
  I created. Two distinct issues; fixed both:
  - **READMEs fixed (1 of 2):** updated each of `L0/C03..C09/README.md` plus
    the `L0/README.md` module index to:
    - Link to its T01 (and T02 where applicable) topic file with a Topics
      table mirroring the `L0/C02/README.md` shape.
    - Show topic-level status as "complete" where applicable.
    - Add a one-sentence framing note about what the chapter contains.
    - Mark all 9 sections "complete" in the L0 module README.
  - **Content deepened (2 of 2):** rewrote/expanded each of the 9 cross-cutting
    files with substantially more depth — added mechanism per entry, more
    examples, more diagrams, more entries, and meta-advice sections for the
    Q&A types. Before/after line counts:
    - `L0/C03/T01-toolchain-quick-reference.md`: 356 → **854 lines, 14
      Mermaid diagrams.** Added: JDK install-tree internals, PATH-lookup
      mechanism, SDKMAN symlink mechanism, IDE inspection internals, the
      `javac` lexer→parser→sem→lower→emit pipeline, `java` launcher class
      loading + tiered JIT (interpreter → C1 → C2 → deopt), JDWP debugger
      wire-protocol architecture, IDE workflow diagrams, sampling-vs-
      instrumentation profilers, GC overview at L0 level, classpath
      depth + module-path preview, minimal Maven `pom.xml` + Gradle
      `build.gradle.kts`, runnable JAR + `jlink` modular packaging,
      common-error table with first-move diagnosis, stack-trace reading
      recipe with Java 14+ helpful-NPE messages.
    - `L0/C04/T01-exercises.md`: 198 → **613 lines.** Expanded from 12 to
      **20 exercises**. Each now has: clear acceptance criteria,
      detailed edge-cases (incl. Integer.MIN_VALUE-style traps), explicit
      hints, stretch goals, topic backreferences. Added a **self-grading
      rubric** with mastery/proficient/familiar per concept area; tips for
      working the exercises (solo, javap-per-session, pair the hard ones).
    - `L0/C04/T02-project-number-guessing-game.md`: 374 → **670 lines.**
      Added: EOF/Ctrl+D handling with `hasNextLine`; manual-testing
      checklist (12 test cases); automated testing without JUnit (piped
      stdin with seeded Random); **dependency-injection preview**
      (refactor Random into a parameter for deterministic tests);
      refactoring lesson (extract constants, split methods, separate I/O
      from logic, Optional for bad input); JAR packaging recipe with
      `jar cfm` and `jar cfe`; **L1 OO redesign preview** showing
      Game/Outcome/RoundResult and what L1 makes explicit (testability,
      swappable CLI, encapsulated state); stretch goals with hints not
      just titles (Difficulty enum, smart range tracker, args parsing
      helper, two-player, inverted/computer-guesses, script replay,
      stats).
    - `L0/C05/T01-l0-idioms.md`: 349 → **670 lines.** Expanded from 24 to
      **30 idioms** with a fixed shape per entry (pattern, why-it-works
      mechanism, consequence, topic link). Added: `List.of`/`Map.of` for
      immutable literals, `Objects.requireNonNull` at API boundaries,
      `Optional<T>` for returns-not-fields, records preview, defensive
      null checks at system boundaries only, single-responsibility per
      method, `int captured = i` workaround + for-each freshness.
    - `L0/C05/T02-l0-pitfalls-catalogue.md`: 434 → **958 lines.** Expanded
      from 35 to **45 traps**. Each now has a fixed shape: trap (repro),
      why it happens (mechanism — bytecode/JIT/JLS/runtime), how to spot
      (IDE warning name + SpotBugs rule + code-review heuristic), the fix,
      topic link. Added: equals/hashCode mismatch (HashSet "miss"),
      mutable object as Map key (hash invariant broken), infinite
      recursion via toString cycle, NaN comparison surprises,
      `LinkedList.get(i)` in a loop, `new Random()` per call,
      `Files.readAllLines` for huge files, catching `Exception` broadly,
      returning `null` from Stream-producing methods, method length /
      cyclomatic complexity.
    - `L0/C06/T01-foundations-questions.md`: 419 → **721 lines.** Added
      **meta-advice sections**: "Answering a what's-the-difference
      question" (5-step recipe), "Answering a why-does-this-do-X question",
      "Live-coding patterns" (state assumptions, walk approach, write
      incrementally, test against examples, talk while you think), and
      "Why did you make that choice?" preparation. Expanded from ~30 to
      **~50 questions** across 11 sections (added Modern Java section
      Java 17+: records, sealed, pattern-switch; Algorithmic /
      Live-coding warm-ups section with reverse, isPalindrome, reverse
      linked list, find missing; Behavioural / Code Review section with
      ArrayList vs HashMap choice, code-review-quality string-concat
      example, how-to-test refactoring case study). Added bytecode trace
      Q ("Trace `for (int i = 0; i < 5; i++) sum += i;`"); deeper memory
      layer Q ("Walk through `new Point(1,2)` byte-by-byte"); meta-Q
      on what `final` means in different positions.
    - `L0/C07/T01-faq.md`: 258 → **445 lines.** Added categories: "When
      the Output Looks Weird" (printf escapes, multithread interleaving,
      locale decimal-comma, unicode-escape source-magic), "JVM Behaviour
      Questions" (startup slow, JIT warmup, hot method definition,
      deoptimisation, System.exit, class-load logging, static block
      multiple-times), "When the IDE and Command Line Disagree" (IDE
      classpath superset, cache invalidation, JAR-not-on-IDE-classpath),
      "When My JAR Mysteriously Misbehaves" (Main-Class missing,
      NoClassDefFoundError for lib, loader constraint violation,
      resources missing), "When the JVM Starts But Seems Stuck" (100% CPU,
      deadlock, long GC pauses, memory growth + heap-dump recipe). Total
      grew from ~30 to **~55 entries**.
    - `L0/C08/T01-l0-cheatsheet.md`: 369 → **562 lines.** Added: text-
      block syntax, `printf` index-reuse `%1$s`, **Collection complexity
      table** (ArrayList/LinkedList/ArrayDeque/HashSet/LinkedHashSet/
      TreeSet/HashMap/LinkedHashMap/TreeMap × add/remove/get/iterate),
      **Exception hierarchy diagram** (Throwable → Error/Exception →
      RuntimeException), **Common annotations table** (Override/
      Deprecated/SuppressWarnings/SafeVarargs/FunctionalInterface +
      meta-annotations), **Regex quick reference** (~25 most-used
      patterns), **`java.time` quick reference** (LocalDate/LocalTime/
      LocalDateTime/ZonedDateTime/Instant + ChronoUnit/Period/Duration/
      DateTimeFormatter), **Build / Run / Inspect cheatsheet** (one-page
      command summary). Added a **"What you DON'T need to memorise"**
      section (Math overloads, regex flavour, Files/Paths/Collectors —
      let the IDE auto-import).
    - `L0/C09/T01-resources.md`: 231 → **369 lines.** Added: **"How to
      Read the JLS Without Crying"** strategy section, **"How to Use
      Baeldung Effectively"** (date-check, JDK-version-check,
      runnable-examples, cross-reference docs), **Curated Reading Paths
      by Goal** (interview prep / backend dev / JVM internals / OpenJDK
      contribution), **Annotated Talk Indexes** for Brian Goetz greatest
      hits, Aleksey Shipilev greatest hits, and Java+Concurrency talks
      (Doug Lea / Ron Pressler / Heinz Kabutz), **GitHub Repos to Study**
      (openjdk/jdk java.util, google/guava, apache/commons-lang,
      spring-projects/spring-boot, Netflix/zuul + Hystrix), **What NOT
      to Read** (pre-Java-8 tutorials, Java EE 7/8 books, legacy Vector/
      Hashtable/StringBuffer examples, old OCP books unless certifying,
      ads-heavy tutorial sites), recommendations by reader profile,
      **How to Stay Current** strategy (subscribe to Java newsletter,
      read JEP list per release, watch one Brian Goetz talk per release).
- **Total cross-cutting content (deepened):** ~5 862 lines across 9 files
  (vs ~2 988 before — roughly doubled). Combined with the 30 concept
  topics (~17 850 lines), **L0 module total ≈ 23 700 lines across 39
  files** with hundreds of diagrams, dozens of native-assembly listings,
  countless bytecode listings, and complete depth coverage.
- **Status:** L0 is now structurally complete AND deepened. **30 / 371**
  concept-topic count remains the same (cross-cutting files aren't in
  the 371 count by design). Ready to ASK USER what to start next in this
  session (options laid out in §4); will not autonomously move to L2 or
  another module until the user picks.

### 2026-06-04 (later — L0 cross-cutting burst, 🎉 L0 STRUCTURALLY COMPLETE)
- **User correction** (this session): I had jumped to L1/C01/T01 after T19 without
  realising a parallel session is handling L1. User clarified the running setup
  is two parallel sessions on the same project; this session should complete
  L0's cross-cutting reference material (C03–C09) before doing anything else.
  Rolled back the L1/C01/T01 prep work in PROGRESS.md and reframed §4 around
  L0 cross-cutting authoring; left the L1 chapter row as **"handled by parallel
  session — leave alone."**
- **Authored 9 cross-cutting files across L0/C03–C09** (~2 988 lines total) —
  all at the appropriate `type` (reference / exercise / project / best-practices
  / interview-qa / qa / cheatsheet / resources) and `difficulty: beginner`:
  - `L0/C03/T01-toolchain-quick-reference.md` (356 ln) — JDK install per OS,
    SDKMAN/jenv multi-version management, IDE comparison + shortcut tables,
    `javac` / `java` / `javap` / `javadoc` / `jshell` CLI, single-file
    source-launcher (Java 11+), diagnostic tools (jps/jstack/jmap/jcmd/jconsole/
    JMC/async-profiler), classpath syntax per OS, L0-relevant JVM flags table,
    build-tool orientation (Maven/Gradle/Ant/Bazel), plain `javac` project
    skeleton, common toolchain errors table.
  - `L0/C04/T01-exercises.md` (198 ln) — 12 graded exercises across C01+C02
    topics: FizzBuzz, sum-of-digits, palindrome (string + int), Fibonacci
    three ways, Sieve of Eratosthenes, in-place reverse, anagram check,
    matrix transpose, word counter with `Map.merge`, recursive power
    (naive + divide-and-conquer), bytecode-trace `javap -c` of loop vs
    recursion, Integer-cache bug reproduction; each with acceptance criteria
    + stretch goals + topic-link backreferences.
  - `L0/C04/T02-project-number-guessing-game.md` (374 ln) — full L0 level
    project. Spec, decomposition diagram, method table, 6-step build-up from
    `main` skeleton through `pickSecret` / `readGuess` / `respondToGuess` /
    `playOneGame` / `askPlayAgain` / `main` wire-up; complete solution code
    (~120 lines); 10 stretch goals (difficulty levels, high score, hints,
    arg-driven config, file replay, EOF handling, stats, two-player mode,
    inverted mode); "what you've demonstrated" map back to T01-T19 topics.
  - `L0/C05/T01-l0-idioms.md` (349 ln) — 24 positive idioms with canonical
    forms + topic links: half-open intervals, `Integer.valueOf`/`equals`,
    `IntStream` over `Stream<Integer>`, `StringBuilder` in loops vs `+`
    inline, `this.field` setter, `getOrDefault`/`merge`, defensive copy,
    early return/continue, `final` for locals, `List.of` for read-only,
    SCREAMING_SNAKE_CASE constants, intent-driven loop forms, labelled
    break for multi-loop escape, switch expressions, pattern-matching
    `instanceof`, `var` for verbose types, try-with-resources, WHY-not-WHAT
    comments, JIT-friendly RCE form, trust LICM for `arr.length`,
    `System.arraycopy` for bulk copy, primitive types in hot paths,
    `javap -c` habit.
  - `L0/C05/T02-l0-pitfalls-catalogue.md` (434 ln) — 35 canonical L0
    traps with repro + diagnosis + fix + topic link: `Integer ==` cache,
    `String ==`, `arr.equals` reference, `List.remove(int)` trap,
    `Arrays.asList(int[])` size 1, NPE on unbox-null, off-by-one,
    dangling-else, stray-semicolon, switch fall-through, integer
    overflow silent, `Math.abs(Integer.MIN_VALUE)`, integer division
    truncation, `byte` arithmetic promotion, float equality, CME during
    `for-each`, lambda capture of loop counter, `continue` in `while`
    forgetting counter, variable shadowing in setter, missing base case
    → SOE, tail-recursive Java still SOEs, pass-by-reference myth,
    `length` vs `length()` vs `size()`, `boolean[]` byte-per-element,
    `ArrayStoreException`, shallow `clone()` on 2-D, `Object[]` →
    `Object...` quirk, autoboxing in hot loop, locale case conversion,
    returning internal mutable state, static initialiser throw → class
    permanently broken, static-field memory leak, `compareTo` subtraction
    overflow, `var x = new ArrayList<>()` defaulting to Object, O(N²)
    string concat in loop.
  - `L0/C06/T01-foundations-questions.md` (419 ln) — ~30 interview Q&A
    in CONVENTIONS §9 format (### Q + Difficulty + Asked at + Answer +
    Follow-ups), organised in 8 sections: JVM/JDK/JRE/compilation,
    variables/types/operators, control flow/loops, arrays/strings/memory,
    methods/parameters/recursion, wrappers/boxing/generics-lite, memory/
    stack/heap, style/best practices. "Asked at" tags target Indian
    MNC service-companies (TCS/Infosys/Wipro/Accenture/Cognizant/Capgemini).
    Distilled from the per-topic INTERVIEW callouts in C02.
  - `L0/C07/T01-faq.md` (258 ln) — 30+ plain-English FAQ entries
    organised by Setup & Toolchain, Code That Doesn't Work, Conceptual
    Questions, When Things Get Weird. Less formal than C06; closer to
    "explain it to a friend who just hit something confusing while
    writing their second program." Each answer + topic-link to the deep
    version.
  - `L0/C08/T01-l0-cheatsheet.md` (369 ln) — dense quick-reference
    one-pager: primitive types + sizes + defaults + wrappers, literal
    suffixes, operator-precedence table, escape sequences, `printf`
    format specifiers, control-flow + loop syntax, String methods,
    Arrays methods, `Math` highlights, wrapper statics, `IntStream`
    quick reference, common bytecode opcodes table, method-descriptor
    letters, L0-relevant JVM flags, `javap` + `jshell` quick reference,
    standard streams, half-open-range conventions, naming-convention
    table, common exceptions table.
  - `L0/C09/T01-resources.md` (231 ln) — annotated bibliography: official
    JDK API docs, JLS, JVMS, official tutorials, books for beginners
    (Head First Java; A Beginner's Guide; Java Programming Language) and
    for L1+ (Effective Java; Java Concurrency in Practice; Java
    Performance), free online (Baeldung; GeeksforGeeks; Codecademy;
    Coursera), paid (JetBrains Academy; Pluralsight; Udemy), YouTube
    channels (official Java; Marco Codes; Java Brains; Devoxx;
    GOTO; Bro Code; Derek Banas), blogs (Heinz Kabutz's Java Specialists
    newsletter; Shipilev; Horstmann; Linkowski), podcasts (Inside Java;
    Foojay), tooling docs (Maven; Gradle; JOL; JMH; async-profiler;
    JMC; hsdis), JEP index with L0-relevant JEPs cross-referenced
    (254/280/286/323/359/361/378/394/409/441/444), Unicode standard
    pointer, Q&A communities (Stack Overflow; r/java; r/learnjava),
    Slack/Discord (VirtualJUG; JetBrains; OpenJDK lists), recommended
    reading-order path through L0 → L1 → L2 → L3.
- **🎉 MILESTONE: L0 Foundations is STRUCTURALLY COMPLETE.** All 30 concept
  topics across C01+C02 at the deep DEPTH-CHECKLIST §4 + §4a bar, plus 9
  cross-cutting reference files across C03–C09 at the appropriate type-
  specific bar. Progress remains 30/371 concept topics (8.1%) since the
  cross-cutting work isn't counted in the 371. **Total L0 content (concept +
  cross-cutting): ~17 850 lines across 39 files, ~170+ Mermaid diagrams,
  ~12 native-assembly listings, dozens of bytecode listings, ~25 tables.**
- **Asking user** what to start next in this session — options laid out in §4:
  (1) L2 module, (2) L3 module, (3) skip to specific topic, (4) L1 cross-cutting
  backfill, (5) generator-status fix (PROGRESS §7), (6) re-deepen earlier topics.
  Default recommendation: option 1 or 2 for forward momentum.

### 2026-06-04 (later — T19 + 🎉 L0 MILESTONE)
- Authored `L0/C02/T19` Comments, Javadoc & code style — **611 lines, 8
  Mermaid diagrams + bytecode-identity proof + naming/style tables**.
  **Language layer**: three comment forms (`//`, `/* */`, `/** */`);
  non-nesting block comments. **Lexer-strips-comments** rule revisited
  from T03. **The Unicode-escape-in-comment trap** — `
` inside a
  `//` terminates the comment because Unicode escapes are processed
  BEFORE the lexer scans tokens/comments; lint warning on `\u` outside
  string literals. **WHY-not-WHAT principle** echoing CLAUDE.md root —
  default to no comments; add when explaining a non-obvious *why*,
  external context, subtle invariant, or deliberate non-idiomatic
  choice. Bad/good comment examples. **Javadoc anatomy** — first
  sentence as summary; body paragraphs separated by `<p>`; standard
  **block tags** table (`@param`/`@return`/`@throws`/`@see`/`@since`/
  `@deprecated`/`@author`/`@version`/`@serial`); standard **inline
  tags** table (`{@code}`/`{@link}`/`{@linkplain}`/`{@literal}`/
  `{@value}`/`{@inheritDoc}`); HTML in Javadoc usage; when to write
  Javadoc (always for public+protected in shared codebases; often
  package-private/private in long-lived; never trivial accessors).
  **`package-info.java`** and **`module-info.java`** (Java 9+ JPMS)
  for package/module-level docs. **Code style — naming conventions
  table**: PascalCase classes/interfaces, camelCase methods/fields/
  locals, SCREAMING_SNAKE_CASE constants, lowercase-dot-separated
  packages, single-uppercase generic type params. **Single-letter
  names** reserved for truly local scopes (i/j/k/n/e/o). **Indentation**
  (4-space Oracle, 2-space Google), line length (80-120), **K&R/
  Egyptian brace placement** as Java convention, spacing rules,
  vertical-alignment avoidance. **Memory layer**: comments stripped at
  the lexer stage; **bit-identical bytecode** proof for two sources
  (no comments vs heavy comments); Javadoc lives in source files only,
  not in `.class`; libraries ship `-sources.jar` alongside main JAR
  for IDE hover-doc; `-javadoc.jar` ships pre-built HTML site. **`-g`
  debug info doesn't carry comments** — only LocalVariableTable +
  LineNumberTable + SourceFile. **Architecture layer**: no runtime
  effect; build-time costs (javadoc generation, format/analyser CI
  steps). **Tooling table**: checkstyle, spotbugs, PMD, error-prone,
  google-java-format, palantir-java-format, IDE reformatters; modern
  workflow with pre-commit hooks + CI verification. **Common mistakes**
  (9 traps): comments that lie, commenting WHAT, missing Javadoc on
  public APIs, inconsistent style, hardcoded TODOs without tickets,
  commented-out code, style wars in code review, Unicode-escape tricks,
  half-documented APIs missing key tags, vanity `@author` tags.
  **INTERVIEW callout** with 11 questions covering comment forms,
  Javadoc tool, standard tags, `{@code}` vs `<code>`, lexer stripping,
  bytecode identity, naming conventions, WHY-not-WHAT principle,
  style tools, `package-info.java`. **Practice (16 exercises)** —
  write Javadoc, generate javadoc HTML, bytecode identity confirmation,
  Unicode-escape trap reproduction, block-comment non-nesting,
  WHY-vs-WHAT analysis on existing code, package-info.java,
  google-java-format application, naming-convention tour,
  `@deprecated` tag vs `@Deprecated` annotation, `{@link}` inline
  hyperlink, `{@code}` with generics, source-jar inspection, CI
  style-checker integration, TODO half-life analysis. Recap as ~12
  learning objectives. **Next link points to L1/C01/T01 (Classes &
  objects)** with closing milestone-celebration paragraph.
- **🎉 MILESTONE: L0 Foundations is 100% COMPLETE — 30/30 concept
  topics.** Both chapters done: `L0/C01-cs-foundations` 11/11
  (2026-05-28 to 2026-05-29) + `L0/C02-java-core` 19/19 (2026-05-29
  to 2026-06-04). Today's L0/C02 burst across T09-T19 (11 topics in
  a single session-pair) covered: Loops (T09, 1162 ln), break/
  continue/labels (T10, 979 ln), Arrays (T11, 986 ln), Methods (T12,
  937 ln), Method overloading (T13, 696 ln), Recursion (T14, 735 ln),
  Variable scope & lifetime (T15, 837 ln), Varargs (T16, 556 ln),
  Wrapper classes & autoboxing (T17, 674 ln), var (T18, 480 ln),
  Comments/Javadoc/style (T19, 611 ln). Total today: **~9,650 lines
  of new content, ~150 Mermaid diagrams + ~12 native-assembly
  listings + countless bytecode listings**. All topics hit
  DEPTH-CHECKLIST §4 + §4a (language layer + memory layer +
  architecture layer, with byte-level memory diagrams, JVM bytecode
  walkthroughs, and CPU/JIT under-the-hood treatment). Progress
  29 → 30 / 371 (7.8% → 8.1%); next phase opens **L1 — Core Java &
  OOP** with `L1/C01/T01` Classes & objects as the immediate
  follow-up.
- Resume at `L1/C01/T01` — Classes & objects (start of OOP module).

### 2026-06-04 (later — T18)
- Authored `L0/C02/T18` var (local variable type inference) — **480 lines,
  6 Mermaid diagrams + bytecode identity proof** at the deep bar
  appropriate for a syntactic topic. **Language layer**: `var` as Java
  10+ local variable type inference (JEP 286); Java remains strictly
  statically typed — `var` is **pure compile-time syntactic sugar**.
  **RHS must determine the type** rule — `var x = 5` infers `int`,
  `var s = "hi"` infers `String`, `var list = new ArrayList<String>()`
  infers `ArrayList<String>` (most-specific type, NOT `List<String>`).
  Illegal forms — `var x;` (no init), `var x = null;` (no inferrable),
  `var x = {1,2,3}` (literal without explicit type). **Allowed
  contexts**: locals, `for`, `for-each`, try-with-resources, lambda
  params (Java 11+ JEP 323 — required for annotations on lambdas).
  **NOT allowed**: fields, method parameters, return types, constructor
  parameters, catch variables. **Field rejection rationale** — field
  types are part of class API; reflection/IDE refactoring need them
  visible. **The diamond + var gotcha** — `var list = new ArrayList<>()`
  infers `ArrayList<Object>` because neither side has the type;
  always specify type on RHS. **Inferred type is most-specific** —
  use explicit interface type for polymorphic reassignment. **`final
  var`** composition. **Style guidance** from JEP 286 — use when RHS
  makes type obvious or type is verbose; avoid when it hides important
  type info. **Memory layer**: **bit-identical bytecode** between
  `ArrayList<String> list = ...` and `var list = new ArrayList<String>()`
  with worked `javap -c` proof; same `astore_1`; `LocalVariableTable`
  records the inferred type as if explicit (via `javap -v` showing
  Signature `Ljava/util/ArrayList;` and `LocalVariableTypeTable`
  `Ljava/util/ArrayList<Ljava/lang/String;>;`); **JVM never sees
  `var`**. **`var` is a "reserved type name," not a keyword** — still
  usable as identifier name (`int var = 5;` compiles); cannot be a
  type name (`class var {...}` rejected). **Architecture layer**:
  zero runtime effect; JIT sees the inferred type just as it would
  see an explicit type. **Common mistakes** (7 traps): `var x;`
  without init, `var x = null`, diamond+var defaulting to Object,
  trying var for fields, trying var for parameters/returns,
  expecting polymorphic reassignment, hiding important type info.
  **INTERVIEW callout** with 12 questions covering JEP 286, reserved
  type name status, allowed contexts, diamond gotcha, bytecode
  identity, dynamic-typing misconception, field rejection rationale,
  `final var`, `var x = null` rejection, polymorphism limitation,
  zero perf impact. **Practice (16 exercises)** — basic inference,
  String inference, generic list, diamond+var trap, for-each with
  var, try-with-resources, field/parameter/return rejection, var
  as identifier name, bytecode identity confirmation,
  LocalVariableTable inspection, `final var`, wrapper vs primitive
  inference, diamond fix, style judgment exercise. Recap as ~11
  learning objectives. Progress 29/371. L0 now **97%** complete,
  C02 chapter **18 / 19** — one topic away from completing the
  chapter and the L0 module.
- Resume at `L0/C02/T19` — Comments, Javadoc & code style (last
  topic of C02 and L0 concept topics).

### 2026-06-04 (later — T17)
- Authored `L0/C02/T17` Wrapper classes & autoboxing — **674 lines, 12
  Mermaid diagrams + bytecode listings + per-wrapper byte-size table +
  cache table** at the deep bar (§4 + §4a). **Language layer**: the
  eight wrapper classes (Byte/Short/Integer/Long/Float/Double/Boolean/
  Character) — immutable, `final`, single private value field; the
  six numeric wrappers extend `Number` (with `intValue`/`longValue`/
  `floatValue`/`doubleValue`); Boolean and Character independent.
  **`valueOf` vs deprecated `new`** — Java 9+ deprecated public
  constructors; always use `valueOf` (which autoboxing compiles to).
  **Autoboxing** = implicit `valueOf(primitive)` at every primitive-
  to-reference context (assignment, method arg, return, generic,
  mixed arithmetic). **Unboxing** = implicit `intValue()/longValue()`
  etc. at wrapper-to-primitive contexts. **Character utilities**
  (isDigit/isLetter/isWhitespace/toUpperCase). **Boolean** has
  exactly 2 instances (TRUE/FALSE) ever. **Memory layer**: full
  per-wrapper byte layout table — **Integer/Float/Boolean/Byte/
  Short/Character = 16 B** (12 header + value + padding); **Long/
  Double = 24 B** (12 + 4 pad before value + 8). Header overhead
  dominates small wrappers (Boolean is 16× its primitive!). **The
  `IntegerCache`** mechanism — pre-allocated 256 Integers for
  `-128..127` at class init; `Integer.valueOf(i)` returns the
  cached instance in range, allocates fresh outside. The
  `-XX:AutoBoxCacheMax=N` knob raising the upper bound (default
  127). **Other wrappers' caches**: Boolean (TRUE/FALSE only),
  Byte (all 256), Short (-128..127), Character (0..127 = ASCII),
  Long (-128..127). **Float and Double have NO cache** — every
  valueOf allocates. **The `Integer == Integer` cache-boundary
  trap** worked example — `valueOf(127) == valueOf(127)` returns
  true; `valueOf(128) == valueOf(128)` returns false; always use
  `.equals()` or unbox. **`List<Integer>` 5× blowup** (T05/T11
  callback) — quantified: int[1M] = ~4 MB contiguous vs
  Integer[1M] = ~20 MB scattered; ~50× slower for tight sum loops
  due to cache-disastrous pointer-chase. **Bytecode mechanics** —
  `invokestatic WrapperType.valueOf:(P)LWrapperType;` for autobox;
  `invokevirtual intValue:()P` for unbox. Worked `javap -c`
  showing `counter++` on Integer = unbox + iadd + rebox.
  **Architecture layer**: **escape analysis eliminates non-
  escaping wrapper allocations** (just like StringBuilder T07 and
  varargs array T16) — scalar replacement; box vanishes; zero
  cost. EA fail conditions (stored in field, returned, added to
  collection, megamorphic call). **The #1 Java performance trap:
  autoboxing in hot loops** — Long counter in 100M-iter loop =
  100M Long allocations (~2.4 GB garbage); fix with primitive
  `long`. **`Stream<Integer>` vs `IntStream`** — specialised
  primitive streams (IntStream/LongStream/DoubleStream) avoid
  per-element box/unbox; 10-50× speedup on numeric work.
  **`Optional<Integer>` vs `OptionalInt`** — same idea.
  **`LongAdder`** for high-contention concurrent counters vs
  AtomicLong; never use `AtomicReference<Long>`. **`BigInteger`/
  `BigDecimal`** are not wrappers — arbitrary precision, every
  arithmetic allocates, ruinous in hot loops. **Common mistakes**
  (10 traps): Integer==Integer cache trap, autobox in hot loop,
  NPE on unboxing null, Boolean reference-comparison style,
  deprecated `new Integer()`, Map.get-of-missing-key NPE, mixing
  wrapper/primitive in conditionals (one autobox per branch),
  compareTo overflow via `a - b` subtraction (use Integer.compare),
  BigInteger for counters, Stream<Integer> vs IntStream.
  **INTERVIEW callout** with 12 questions covering int vs Integer,
  autoboxing definition, IntegerCache range, the 127-vs-128 trap,
  equals() vs ==, -XX:AutoBoxCacheMax, which wrappers don't
  cache, Long object size, autobox bytecode, NPE on null unbox,
  Stream<Integer> vs IntStream perf, hot-loop fixes. **Practice
  (17 exercises)** — wrapper constants tour, cache trap repro,
  -XX:AutoBoxCacheMax extension, JOL layout dump, autobox/unbox
  bytecode inspection, counter++ wrapper bytecode, hot-loop
  perf trap with Long vs long timing, EA observation via
  PrintEliminateAllocations, Stream<Integer> vs IntStream
  benchmark, Map.get NPE trap + getOrDefault fix, Map.merge
  word counter, Boolean cache confirmation, Float no-cache,
  compareTo overflow via subtraction + Integer.compare fix,
  AtomicLong vs LongAdder under contention, full end-to-end
  "explain it back" trace of `Integer x = 5; Integer y = 5;
  boolean eq = (x == y);` showing cache hit and reference
  equality. Recap as ~17 learning objectives spanning all three
  layers. Progress 28/371. L0 now **93%** complete, C02 chapter
  **17 / 19**.
- Resume at `L0/C02/T18` — var (local variable type inference).

### 2026-06-04 (later — T16)
- Authored `L0/C02/T16` Varargs — **556 lines, 10 Mermaid diagrams +
  bytecode listings for declaration + call site** at the deep bar
  (§4 + §4a). **Language layer**: varargs as Java 5+ caller-side
  syntactic sugar for an array parameter; declaration syntax
  `T... name`; the **two hard rules** (last parameter only; one per
  method); inside the method body, the varargs param IS a `T[]`
  (length, iterable, indexable). **Five calling forms** — 0/1/2/N
  inline args, or pass an array directly (the array IS the varargs).
  **The `Object[]` → `Object...` quirk** (T13 callback) — passing
  `Object[] arr` to `log(Object...)` makes args.length == arr.length,
  NOT 1; `(Object) arr` to force single-element interpretation.
  **`log(null)` NPE trap** — null treated as null array; `args.length`
  throws NPE; `(Object) null` for single-null. **Generic varargs +
  heap pollution** — runtime array type is `Object[]` due to erasure;
  `@SafeVarargs` on `static`/`final`/`private`/record-constructor
  generic varargs to suppress the warning when the implementation
  is safe. **Overload resolution recap** (T13) — fixed-arity always
  beats varargs. **Memory layer**: **varargs IS array parameter at
  bytecode** — method descriptor identical (e.g., `(Ljava/lang/
  String;[Ljava/lang/Object;)V`); only difference is the
  **`ACC_VARARGS` (0x0080)** flag in the method's access flags, used
  by reflection (`Method.isVarArgs()`) and javac for call-site
  sugar, but **ignored by the JVM at execution**. **Call-site code
  generation** worked end-to-end via `javap -c`: for `log("x", 1, 2,
  3)`, javac emits `anewarray Object 3` + `aastore` × 3 (with
  `Integer.valueOf` autoboxing) + `invokestatic`. Three things happen
  per varargs call: array allocation (16-byte header + 4×N refs);
  N stores; N possible autobox allocations. **No extra allocation
  when caller passes an array directly** — just `aload` + `invokestatic`.
  Worked `javap -v` showing the flag combination `0x0089` = PUBLIC |
  STATIC | VARARGS. **Architecture layer**: per-call heap allocation
  cost (~50-150 ns when EA fails); **escape analysis can eliminate
  the array** (scalar replacement → elements in registers → zero GC
  pressure) when args doesn't escape the called method. EA fail
  conditions — array stored in field, returned, passed to non-inlined
  callee. **The SLF4J / Log4j low-arity-overload pattern** — provide
  fixed-arity overloads (`log(String)`, `log(String, Object)`,
  `log(String, Object, Object)`) to skip the array allocation for
  common 1-2 arg cases; only 3+ args fall through to varargs.
  Throughput-cost table comparing the three cases. **`String.format`
  + `printf` are slow beyond varargs** — Formatter allocation,
  format-string parsing per call, result allocation; hot-path
  formatting should use StringBuilder or pre-cache. **Common
  mistakes** (8 traps): varargs not last (compile error), `Object[]`
  → `Object...` quirk (cast to Object to fix), `@SafeVarargs` missing
  on generic, `log(null)` NPE, perf assumption that varargs is free,
  storing args array externally (EA fail → real allocation),
  confusing `Object...` with `Object[]...`, `Arrays.asList(int[])`
  size-1 trap. **INTERVIEW callout** with 12 questions covering
  what varargs is, bytecode descriptor identity, caller-side array
  allocation, runtime cost, last-parameter rule, the
  `Object[]→Object...` quirk + cast workaround, `@SafeVarargs`
  purpose + restriction, why SLF4J has low-arity overloads, fixed-
  arity vs varargs precedence, `log(null)` NPE behaviour, two
  varargs forbidden, sugar status. **Practice (15 exercises)** —
  declare and call varargs, ACC_VARARGS flag inspection via
  `javap -v`, call-site bytecode via `javap -c`, identical-bytecode
  proof between varargs call and explicit `new Object[]` call,
  pass-array-directly no-allocation proof, `Object[]→Object...`
  quirk repro + cast fix, null trap repro + cast fix, generic
  varargs warning + @SafeVarargs suppression, `Arrays.asList(int[])`
  size-1 vs `Arrays.asList(Integer[])` size-N, per-call allocation
  microbench with `-XX:-DoEscapeAnalysis` toggle, EA observability
  via `PrintEliminateAllocations`, SLF4J low-arity-overload
  implementation + test, varargs in nested calls, varargs-vs-
  generic-overload ambiguity with null, full end-to-end "explain it
  back" trace through javac, bytecode, JIT inlining, EA elision.
  Recap as ~14 learning objectives spanning all three layers.
  Progress 27/371. L0 now **90%** complete, C02 chapter **16 / 19**.
- Resume at `L0/C02/T17` — Wrapper classes & autoboxing.

### 2026-06-04 (later — T15)
- Authored `L0/C02/T15` Variable scope & lifetime — **837 lines, 17
  Mermaid diagrams + lifetime-table + reference-chain diagrams** at
  the deep bar (§4 + §4a). **Language layer**: scope (compile-time:
  where a name is visible) vs lifetime (runtime: when storage exists
  and is reclaimed) clearly distinguished. **Four variable kinds**:
  local, parameter, instance field, static field — each with its
  scope rule. **Local scope** — declaration to end of enclosing
  block; declaration-before-use; nested blocks can declare new
  locals but cannot shadow outer locals (Java rule, unlike C/C++/
  Rust/Kotlin). **For-loop scope** revisited (T09 callback).
  **try/catch/try-with-resources scope** — exception variable
  catch-only; try-local locals not visible in catch/finally; the
  hoist-outside pattern. **Parameter scope** = whole method body.
  **Instance field scope** = whole class body (hoisted — visible
  before declaration line). **Static field scope** = same +
  `ClassName.field` from outside. **Field shadowing** — local/
  parameter with same name as field shadows it; `this.field` to
  resolve; canonical setter idiom `this.x = x;` worked example
  with the "value = value" bug. **Definite assignment** — locals
  require provable assignment on all paths; fields don't (get
  defaults). **Effectively final** preview for lambda capture.
  **Lifetime layer**: per-kind lifetime table. **Local lifetime**
  = method-entry frame allocation to frame pop on return; slot
  pre-allocated frame-wide; **compiler can reuse slots across
  non-overlapping scopes** (with `javap -v` evidence). **Parameter
  lifetime** = same as locals; slot 0 = `this`/first-param.
  **Instance field lifetime** = `new` to GC of containing object.
  **Static field lifetime** = class init to class unload (essentially
  forever for app classloader); **static fields are GC roots** —
  source of memory leaks if collections grow unbounded. **Returned
  references extend lifetime** — local goes out of scope but the
  referenced object lives via caller's reference. **Memory layer**:
  byte-level placement per kind. Locals/parameters in **stack
  frame's local-variable array** (T02 callback) — 32-bit slots;
  long/double take 2; JIT register-allocates hot slots. Instance
  fields **inside the heap object** with field reordering by
  descending size for padding minimisation (T02 callback). Static
  fields in the **Class object's metadata in Metaspace** (modern
  HotSpot post-Java-8; pre-8 was PermGen with hard limit;
  `-XX:MaxMetaspaceSize`). Worked memory-region diagram (heap +
  Metaspace + per-thread stack + code cache). **Source-level names
  are mostly lost at runtime** — bytecode uses slot numbers + field
  offsets; names persist only in optional `LocalVariableTable`
  debug attribute (emitted by `javac -g`) + constant-pool
  `Fieldref`/`Methodref`. **Architecture layer**: **JIT register
  allocation + liveness analysis** — hot locals live in CPU
  registers (x86-64 edi/r10d; ARM64 w0..w28); cold ones spill to
  frame slots; live-range diagram showing two non-overlapping
  variables sharing a physical register; observable lifetime can
  be SHORTER than source scope. **Escape analysis** — non-escaping
  `new` allocations are scalar-replaced (fields → registers; no
  heap allocation; lifetime = method scope; **zero GC pressure**);
  `-XX:+PrintEliminateAllocations` observability. EA fail conditions
  (assigned to field, passed to non-inlined call, returned).
  **GC reachability** with reference-chain diagram — roots
  (stack frames, static fields, JNI handles, active threads);
  reachable from any root = live; unreachable = garbage; **GC
  reclaim timing is not guaranteed** (eventually, not when).
  **`final` and lifetime** — doesn't change lifetime; affects
  definite assignment, JIT constant-folding, JMM safe-publication
  (L3/C01 forward link). **Class initialisation timing diagram**:
  load → verify → prepare (defaults) → resolve → initialise
  (initialisers + static{} blocks in source order); triggered by
  first active use (new, static field R/W, static method, reflective
  access, subclass init); `Class.forName(name, false, loader)`
  loads without initialising. Static initialiser throws →
  `ExceptionInInitializerError` once + `NoClassDefFoundError`
  forever. **Common mistakes** (9 traps): local-outside-scope,
  shadowing without `this.`, static-field memory leak, expecting
  local to persist across calls, lambda capture of non-effectively-
  final counter, definite-assignment error, returned-reference
  lifetime extension, static initialiser throwing (class
  unusable thereafter), unsynchronised static mutation across
  threads. **INTERVIEW callout** with 12 questions covering
  scope-vs-lifetime, for-counter scope, shadowing+this.,
  definite assignment, static field lifetime/Metaspace location,
  static collections as leak source, escape analysis, `final`
  effects, `javac -g` debug attributes, slot reuse, GC roots.
  **Practice (16 exercises)** — for-counter scope error, block
  scope nested reuse, locals-shadowing-locals rejection, setter
  shadow + this.fix, field forward reference, definite-assignment
  error, static-collection leak observation via jvisualvm,
  static-initialiser-throw repro showing ExceptionInInitializerError
  + NoClassDefFoundError, LocalVariableTable inspection with -g,
  bytecode without -g, slot reuse confirmation, EA observation
  via PrintEliminateAllocations on Point allocation, lambda
  capture of for-counter fails / for-each succeeds, static-field
  GC reachability via Reference tracking, class init timing
  observation, full end-to-end "explain it back" of scope+lifetime
  for a tiny class with all 4 variable kinds. Recap as ~20
  learning objectives spanning all three layers. Progress 26/371.
  L0 now **87%** complete, C02 chapter **15 / 19**.
- Resume at `L0/C02/T16` — Varargs.

### 2026-06-04 (later — T14)
- Authored `L0/C02/T14` Recursion — **735 lines, 16 Mermaid diagrams +
  bytecode listing + substitution trace tables** at the deep bar
  (§4 + §4a). **Language layer**: recursion as a method calling
  itself; the **base case + recursive case** template; the **input
  must reduce toward the base** rule (else infinite recursion). The
  **substitution model** for hand-tracing — `factorial(5) → 5 ×
  factorial(4) → ...` evaluated back up. **Direct vs indirect
  (mutual) recursion** (isEven/isOdd worked example). **Classic
  examples** with their recursion shapes: **factorial** (linear,
  O(n) depth), **gcd via Euclid** (logarithmic, O(log min)),
  **naive Fibonacci** (exponential O(2ⁿ) — explained why: each call
  spawns two more; subproblems overlap massively; `fib(40)` ~1s,
  `fib(50)` ~17 min), **tree traversal trio** (pre/in/post-order
  DFS — same recursion, three visit positions; depth = tree height,
  O(log n) balanced / O(n) chain), **mergesort divide-and-conquer**
  (O(n log n) — depth = log n; each level O(n) work), **backtracking
  permutations** (depth n; leaves n!). **Memoization** as the
  exponential-to-polynomial transformation — array-based or
  HashMap-based cache; gateway to **dynamic programming** (deferred
  to L6/C02 DSA). Memo table for problems with overlapping
  subproblems: Fibonacci, climbing stairs, knapsack, edit
  distance, LCS. **Memory layer**: every recursive call is a plain
  `invokestatic`/`invokevirtual` (T12 callback) — JVM has **no
  concept** of recursion. Worked `javap -c` of factorial showing
  the self-`invokestatic`. **Each call allocates a fresh stack
  frame** with its own params and locals (T02 frame layout); the
  previous frame waits, suspended, for the inner call's return.
  Stack-frame visualisation showing 5 nested frames for
  `factorial(5)` mid-execution. **Frame size 50-200 B** estimate
  → **`-Xss` (default ~512 KB-1 MB) gives ~3 000-10 000 depth**;
  table of `-Xss` vs depth (64 KB → 600; 512 KB → 5 000; 1 MB →
  10 000; 4 MB → 40 000). **`StackOverflowError` mechanics** —
  JVM detects frame allocation past stack limit via guard page;
  throws SOE; stack trace shows thousands of identical frames.
  **Depth-probe code** to measure your stack budget. **Architecture
  layer**: **Return Address Stack** (RAS, ~16-32 entries on Intel;
  ~8-16 on ARM) revisited — predicts `ret` targets at ~1 cycle
  for shallow recursion; **deep recursion overflows RAS**, older
  entries evicted, predictor falls back to BTB, ~10-20 cycle
  mispredict per evicted return — small but real perf cost.
  **Tail recursion** — recursive call is the *last operation*
  (nothing after it); the canonical accumulator pattern
  `factorialTail(int n, int acc)` worked example. **Tail-call
  optimisation (TCO)** — compiler rewrites the recursive call as
  a frame-reusing `jmp` back to the top; constant stack depth.
  **HotSpot does NOT do TCO** by deliberate design: (1) full
  stack traces are a language contract; (2) security managers /
  `AccessController.doPrivileged` rely on actual stack;
  (3) profilers / debuggers / `StackWalker` API. So tail-
  recursive Java still grows the stack and still `StackOverflows`.
  **Scala (`@tailrec`)**, **Kotlin (`tailrec`)**, **Clojure
  (`recur`)** all DO emit TCO bytecode (backward `goto` into the
  same method) on the JVM. **Iterative conversion** with a
  manual `Deque<Node>` stack — immune to `StackOverflowError`
  (Deque lives on the heap, bounded only by heap size); worked
  recursive vs iterative DFS comparison. **Recursion vs
  iteration decision table** — clarity, stack overhead,
  StackOverflowError risk, TCO availability (none in Java), JIT
  optimisation (iteration gets the full suite — LICM, unroll,
  SIMD — T09 callback). Rule of thumb: tree/divide-conquer/
  backtracking → recursion; linear loops → iteration; deep
  unbounded linear recursion → switch to iteration. **Common
  mistakes** (9 traps): missing base case, wrong base case for
  edge inputs (`n == 1` skips zero), recomputation in naive
  Fibonacci (memoize!), accidental indirect recursion
  (`User.toString` calling `formatUser` calling `toString`),
  expecting TCO (use iteration in Java), shared static state
  contamination across calls, modifying a shared parameter in
  backtracking without copying (defensive `new ArrayList<>(chosen)`),
  deep recursion on unbounded linked list, missing `null` guard
  before recursing into children. **INTERVIEW callout** with 12
  questions covering the base+recursive structure, naive Fib
  complexity, memoization fix, tail recursion definition,
  HotSpot no-TCO + why, which JVM languages do TCO, direct vs
  indirect, when to convert to iteration, RAS hardware
  prediction relationship, recursive-call bytecode, `-Xss` depth
  bound. **Practice (17 exercises)** — hand-trace factorial,
  `javap -c` recursion bytecode, StackOverflowError-depth probe,
  `-Xss` vs depth measurement, naive vs memoized Fib timing,
  Euclid gcd depth trace, tree-traversal trio output prediction,
  mergesort implementation + depth trace, permutation count via
  backtracking, mutual recursion StackOverflow, tail-recursive
  trap (HotSpot SOEs despite tail form), iterative conversion to
  while loop, manual Deque DFS, deep linked-list count throws
  vs iterative succeeds, backtracking shared-list bug repro +
  defensive-copy fix, full end-to-end "explain it back" of
  `factorial(3)` at JVM level. Recap as ~16 learning objectives
  spanning all three layers. Progress 25/371. L0 now **83%**
  complete, C02 chapter **14 / 19**.
- Resume at `L0/C02/T15` — Variable scope & lifetime.

### 2026-06-04 (later — T13)
- Authored `L0/C02/T13` Method overloading — **696 lines, 12 Mermaid
  diagrams + bytecode listings + constant-pool descriptor tables** at
  the deep bar (§4 + §4a). **Language layer**: overloading as
  multiple methods sharing a name in the **same class**, distinguished
  by **parameter list** (the **signature** = name + parameter types).
  Use cases: numerical operations (`Math.max` 4 variants), constructor
  defaults (StringBuilder family), API ergonomics (`println(int/long/
  String/Object)`). **Signature rule** with table — what counts (name,
  parameter types, count, order) and what doesn't (param names,
  return type, throws, modifiers). **Return type alone cannot
  distinguish overloads** — compile error; rationale (call sites
  often discard the return; resolution by destination would break
  locality). **Constructor overloading** with `this(...)` delegation —
  first-statement constraint, tree-rooted-at-one-canonical pattern,
  builder-pattern alternative for telescoping (deferred to L3/C03).
  **The three-phase resolution algorithm** (JLS §15.12.2) walked
  exhaustively: **phase 1** — no widening, no boxing, no varargs;
  **phase 2** — adds primitive widening; **phase 3** — adds
  autoboxing/unboxing and varargs. **Strict phase ordering** — phase
  1 wins over phase 2 wins over phase 3; **widening beats boxing**
  is the famous consequence. **"Most specific" tie-break** — m1 more
  specific than m2 if every arg type of m1 is assignable to m2's;
  ambiguous if no unique most-specific exists. **The `List.remove`
  trap** — `list.remove(int)` removes by **index**, `remove(Object)`
  removes by **value**; `list.remove(5)` on `List<Integer>` picks
  `remove(int)` (phase 1 win); fix with `Integer.valueOf(5)` or
  `(Object) 5`. **Null ambiguity** — null assignable to all reference
  types; sibling-class overloads → ambiguous; cast to disambiguate.
  **Varargs** rules — phase 3 only; fixed-arity always beats varargs;
  varargs-vs-varargs picks most specific; **`Object[]` to `Object...`
  is treated as the array IS the varargs** (Java 5 backward-compat
  quirk). **Phase-3 boxing surprises** worked through
  `f(Object/Number/Integer)` with `f(5)` picking most-specific
  Integer; remove the Integer overload and Number wins (still
  phase 3). **Memory layer**: each overload is a **separate
  `method_info`** in the .class with its own bytecode, own
  exception table, own debug info. **Method descriptor encoding**
  table — `B/C/D/F/I/J/S/Z/V` for primitives + void, `L<binary
  name>;` for references, `[` prefix for arrays — and the full
  form `(<params>)<return>`. Example descriptors: `(II)I`, `(JJ)J`,
  `(DD)D`, `(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/
  String;`, `(Ljava/lang/String;[Ljava/lang/Object;)V`. Worked
  `javap -c -p` for a class with `add(int,int)` and `add(long,
  long)` showing the **two distinct `invokestatic` opcodes
  referencing different constant-pool `Methodref` entries with
  different descriptors** — proof that **resolution is baked in
  at compile time**. Constant-pool `Methodref` entries shown.
  **Architecture layer**: **zero runtime cost** — overload
  resolution is purely compile-time; JVM looks up *one* specific
  method by name+descriptor; no runtime overload search; the
  `invoke*` opcode dispatches directly. Contrast with **dynamic
  dispatch** of `invokevirtual` for overriding (vtable lookup,
  T12 callback). **Each overload is JIT-compiled independently** —
  separate inlining decisions, separate register allocations,
  separate deopt traps; from the JIT's perspective they're as
  related as two unrelated methods. **Overloading vs overriding
  distinction** — overloading = compile-time, same class,
  different params, static dispatch, zero cost; overriding =
  runtime, subclass, same signature, vtable dispatch, ~1-3
  cycle cost. Diagram for both. Full coverage of overriding in
  L1/C01. **Common mistakes** (9 traps): overload by return type
  alone, forgetting widening-beats-boxing, `List.remove(int)`
  trap on `List<Integer>`, ambiguous null, sibling-class
  ambiguity (Comparable/Serializable on String), API-evolution
  source-incompat risk when adding overloads, confusing
  overloading with overriding, `Object[]`→`Object...` subtlety,
  phase-3 boxing surprises ("which most-specific?"). **INTERVIEW
  callout** with 12 questions covering signature definition,
  return-type-alone rule, the 3-phase algorithm, widening beats
  boxing, the `List.remove` trap, null ambiguity, static-vs-
  dynamic dispatch, runtime cost, descriptor encoding, "most
  specific" definition, fixed-arity vs varargs, cross-class
  inheritance. **Practice (16 exercises)** — tour `Math.max`
  descriptors, reproduce the `List<Integer>.remove(2)` trap,
  widening-beats-boxing demo, null-ambiguity demo, descriptor
  inspection for 4 overloads, constant-pool `Methodref`
  inspection, constructor delegation trace, most-specific demo
  on Animal/Dog hierarchy, varargs-vs-fixed test, varargs-vs-
  varargs most-specific, phase-3 boxing (`f(Object/Number)`
  picking Number), add Integer overload and observe pick change,
  return-type-clash compile error, throws-clause non-distinction
  compile error, full 5-overload resolution drill with 5 different
  argument types predicted, end-to-end "explain it back" trace
  of `add(1,2)` through compile-time pick + bytecode emit + JVM
  lookup + JIT compile. Recap as ~16 learning objectives spanning
  all three layers. Progress 24/371. L0 now **80%** complete,
  C02 chapter **13 / 19**.
- Resume at `L0/C02/T14` — Recursion.

### 2026-06-04 (later — T12)
- Authored `L0/C02/T12` Methods, parameters, return values — **937 lines,
  20 Mermaid diagrams + 1 x86-64 native-assembly listing + bytecode
  listings for every invoke* opcode** at the deep bar (§4 + §4a).
  **Language layer**: method as Java's only behaviour construct — name,
  params, return type. Full **declaration syntax** with all six parts
  (modifiers, generics, return type, name, parameter list, throws,
  body) and the **signature = name + parameter types** rule. **`static`
  vs instance**: static = belongs to the class, no `this`, called as
  `ClassName.method`; instance = belongs to objects, implicit `this`,
  called as `obj.method`. Access-modifier preview (`private` /
  package-private / `protected` / `public`) deferred to L1/C01.
  **Return type** — the "every path returns or throws" compile-time
  check for non-void; `return;` optional in `void` methods (control
  falls off closing brace). **Three invocation forms**: instance,
  static, unqualified (resolves to instance-on-`this` then static
  in the current class). **Left-to-right argument evaluation** per
  JLS §15.7.4 — deterministic, unlike C. **Pass-by-value deep dive**:
  Java is **strictly** pass-by-value; the argument value is copied
  into the callee's frame slot. For primitives the value is bits;
  for objects the value is the reference (4 bytes compressed; 8
  uncompressed). Three worked examples: (1) primitive `void incr(int
  x) { x++; }` — caller's variable unchanged; (2) reference mutation
  `void zero(Box b) { b.n = 0; }` — caller's box mutated because both
  hold the same reference; (3) reference **reassignment** `void
  replace(Box b) { b = new Box(); b.n = 999; }` — caller's box
  untouched (this is the test that disproves pass-by-reference).
  The "Java is pass-by-reference for objects" **myth debunked** —
  pass-by-reference would mean reassignment propagates; Java cannot;
  Java is pass-value-of-the-reference. Defensive-copy pattern for
  accepting mutable args. **Parameter limits** — 255 slots per
  method in the JVM; long/double take 2 each. **Memory layer**:
  fresh stack frame per call (T02 frame layout revisited) — locals
  + operand stack + frame data; parameter-slot population at call
  (slot 0 = `this` for instance methods; then params; then declared
  locals). **The five `invoke*` opcodes**: `invokestatic` (no
  receiver, link-time resolved), `invokevirtual` (instance methods,
  vtable dispatch), `invokespecial` (constructors, `super.method()`,
  `private` — non-virtual), `invokeinterface` (interface methods,
  itable lookup), `invokedynamic` (lambdas, string concat from Java 9+,
  pattern matching switch from Java 21 — bootstrap-once, CallSite-
  cached). Worked `javap -c` for each with the constant-pool
  descriptor (`(II)I`, `(Ljava/lang/String;)V`, etc.). **Return
  opcode family**: `return` (void), `ireturn` (int + promoted byte/
  short/char/boolean), `lreturn` (long), `freturn` (float),
  `dreturn` (double), `areturn` (reference). **Worked end-to-end
  frame trace** — call site pushes args, `invokestatic` allocates
  callee frame, callee runs, `ireturn` pops + deallocates + pushes
  to caller's stack. **vtable dispatch mechanics** — class
  metadata's per-class array of method pointers indexed by table
  slot; `invokevirtual` reads receiver's klass pointer → loads
  vtable[slot] → indirect call. ~1-3 cycles on hot code; BTB hit/
  miss. **Method resolution** at class-load time (verify, prepare,
  resolve, init from L0/C01/T04). **Architecture layer**:
  **System V AMD64 ABI** (Linux x86-64) — first 6 int args in
  `rdi, rsi, rdx, rcx, r8, r9`; first 8 FP in `xmm0..xmm7`; return
  in `rax`/`xmm0`; spill beyond — with a worked native listing of
  `compute(a,b,c,d,e)`. **ARM64 AAPCS** — first 8 int args in
  `x0..x7`; FP in `v0..v7`; return in `x0`/`v0`. **`call`/`ret`
  machinery** with the **Return Address Stack (RAS)** — a hardware
  stack mirroring software returns; predicts `ret` at ~1 cycle;
  mispredicts ~10-20 cycles on `setjmp`/`longjmp`/very deep
  recursion. **JIT inlining as the most important optimisation** —
  copies callee body into caller; eliminates the call sequence;
  enables cross-procedure const-prop, dead-code elim, escape
  analysis; controlled by `-XX:MaxInlineSize=35` (cold method
  bytecode size), `-XX:FreqInlineSize=325` (hot), `-XX:MaxInlineLevel=15`.
  **Monomorphic / bimorphic / megamorphic** call-site classification —
  monomorphic (1 target → inline, call vanishes), bimorphic (2 targets
  → type-test + 2 inlined bodies + vtable fallback), polymorphic /
  megamorphic (many → vtable lookup). **Class Hierarchy Analysis
  (CHA)** — JIT proves monomorphism by checking no override exists in
  loaded classes; inlines aggressively; **deoptimises** if an
  overriding class loads later. `final` and `private` as static
  guarantees of non-virtual dispatch / inlinability. **Native frame
  shape** is not a 1-to-1 of JVM frame — JIT register-allocates
  frequently-used locals to registers, spills others to stack; operand
  stack is entirely virtual at the native level. **Deoptimisation**
  as the safety net for aggressive JIT assumptions. **Code cache**
  (`-XX:ReservedCodeCacheSize`, ~240 MB default). **Common mistakes**
  (10 traps): forgetting return on non-void, mismatched return type,
  the pass-by-reference myth, modifying a primitive parameter
  uselessly, mutating a shared object unexpectedly (defensive copy
  fix), parameter shadowing without `this.`, infinite recursion →
  `StackOverflowError` (50-200 B/frame; `-Xss` controls depth),
  returning a reference to internal mutable state (encapsulation
  leak; use `Collections.unmodifiableList` or defensive copy),
  recursion vs iteration for hot loops. **INTERVIEW callout** with
  13 questions covering pass-by-value vs by-reference, the 5
  invoke opcodes, what each is used for, vtable, monomorphic/
  megamorphic, CHA, why `private` is faster, MaxInlineSize, RAS
  prediction of `ret`, StackOverflowError, signature-vs-declaration.
  **Practice (17 exercises)** — pass-by-value primitive/reference/
  reassignment trio, `javap -c` of all 5 invoke opcodes, return-
  opcode family, `StackOverflowError` reproduction, `-Xss` recursion-
  depth experiment, defensive-copy demo, JIT inlining via
  `PrintInlining`, monomorphic vs megamorphic call-site benchmark,
  `final` as inlining hint, RAS-mispredict microbench, end-to-end
  "explain it back" trace through frame allocation, ireturn,
  frame teardown, JIT inlining. Recap as ~17 learning objectives
  spanning all three layers. Progress 23/371. L0 now **77%**
  complete, C02 chapter **12 / 19**.
- Resume at `L0/C02/T13` — Method overloading.

### 2026-06-04 (later — T11)
- Authored `L0/C02/T11` Arrays (1-D, multi-dimensional) — **986 lines, 19
  Mermaid diagrams + 3 native-assembly listings + multiple bytecode
  listings** at the deep bar (§4 + §4a). **Language layer**: arrays as
  the **only built-in aggregate** in Java — the foundation under every
  container in `java.util`. **1-D arrays**: declaration syntax
  (idiomatic `int[] arr` vs legacy C-style `int arr[]`, plus the
  multi-decl trap `int[] a, b[]` where b is int[][]); creation with
  `new int[size]` (JVM-guaranteed zero-init — never garbage),
  default-values table, `NegativeArraySizeException`, the practical
  max length ~2³¹ - 8; array literals — declaration form `{1,2,3}` vs
  expression form `new int[]{1,2,3}` (only the expression form is
  legal in a method argument); indexing 0-based half-open `[0, length)`;
  the **`length` field** (NOT a method!) and the three-way memorisation
  trap with `String.length()` and `Collection.size()`; **array
  covariance** — `String[] IS-A Object[]`, the runtime
  `ArrayStoreException` from `aastore`'s implicit type check, the
  historical-design contrast with generics (erasure → invariance at
  compile time, no runtime check). **The `Arrays` utility class**:
  full method table (toString, deepToString, equals/deepEquals,
  hashCode, fill, sort, parallelSort, binarySearch, copyOf,
  copyOfRange, stream, asList) with cost annotations. The
  **`arr.equals` reference-equality trap** (use `Arrays.equals`).
  The **`Arrays.asList(int[])` size-1 trap** — varargs `T...`
  cannot bind to a primitive, so the whole `int[]` becomes a single
  element of type `int[]` (`size()` returns 1); fix with
  `IntStream.of(arr).boxed().toList()`. **`Arrays.asList` returns
  a fixed-size List** — `add`/`remove` throw `UnsupportedOperation`.
  **`System.arraycopy`** for bulk copy. **`clone()`** semantics —
  fine for 1-D primitives and 1-D references, but **shallow** for
  multi-D (inner sub-arrays are SHARED!), with a per-row clone
  recipe for deep copy. **Multi-D arrays as "arrays of arrays"**:
  Java has no true 2-D; `int[][]` is an `int[]` of `int[]`s,
  non-contiguous; `new int[3][4]` allocates 4 arrays (1 outer +
  3 inner); `new int[3][]` allocates only the outer, leaves rows
  null for jagged arrays; multi-D literal syntax; two-level indexing
  via `aaload` + `iaload`. **Row-major vs column-major** introduction
  (deep coverage in architecture). **Flat `int[r*c]` trade-off** for
  dense numeric work (matrices, image buffers, tensors). **3-D+**
  via `multianewarray`. **Memory layer**: full byte-level **array
  header** — 12-byte object header (mark word + klass pointer,
  compressed in modern HotSpot) + 4-byte length field + padding =
  16 bytes overhead; then N × sizeof(elem) + alignment padding to 8.
  **Per-type element-size table** with concrete byte totals for
  `int[10]` (56 B), `Integer[10]` (~200 B), etc. **`boolean[]` is
  byte-per-element, not bit-per-element** — for bitmaps use `BitSet`.
  Headline comparison: **`int[1M] ≈ 4 MB contiguous one allocation`
  vs `Integer[1M] ≈ 20 MB scattered across 1,000,001 allocations`**
  — 5× memory, ~10× cache penalty. **Per-type element opcodes table**:
  `iaload`/`iastore`, `laload`, `faload`, `daload`, `aaload`/`aastore`,
  `baload`/`bastore`, `caload`, `saload`; plus `newarray`,
  `anewarray`, `multianewarray`, `arraylength`. **Three implicit
  checks per access** — null (NPE), bounds (AIOOBE), array-store
  (`aastore` only, ArrayStoreException) with the per-check diagram.
  Worked `javap -c` for 1-D sum loop showing `arraylength`,
  `iaload`, `iinc`, backward `goto`. Worked `javap -c` for 2-D
  `grid[r][c]` showing two indexed loads (`aaload` + `iaload`).
  `multianewarray [[I, 2` for rectangular 2-D allocation. Lifetime
  and GC notes — multi-D inner arrays stay reachable via the outer.
  **Architecture layer**: **scaled-index addressing** with worked
  x86-64 (`mov eax, [rdx + rdi*4]`) and ARM64 (`ldr w4, [x3, w0, sxtw
  #2]`) showing the `*sizeof(elem)` is FREE in the address-generation
  unit — for `int[]` (scale 4), `long[]` (scale 8), `byte[]` (scale 1),
  etc. **Bounds-check elimination** revisited from T09 — the
  difference between the naive `cmp + jae + mov` and the post-RCE
  single `mov`; idiomatic `for (int i = 0; i < arr.length; i++)` is
  the pattern that hands the JIT the easy proof. **Cache lines and
  prefetcher** — 64-byte L1 lines = 16 ints, stride-1 detection after
  3-4 misses, future-line streaming, L1-throughput sequential scans.
  **`int[]` vs `Integer[]` cache disaster** — quantified: 1,000,000
  cold cache misses on `Integer[]` walk vs ~62,500 warm hits on
  `int[]`; ~50-100× speed difference. **Row-major vs column-major**
  on 1000×1000 `int[][]` — ~10× difference; the universal rule
  "iterate the rightmost index in the innermost loop" (inverts in
  Fortran/MATLAB). **`Arrays.sort` algorithm split**: primitive
  arrays use **dual-pivot quicksort** (Yaroslavskiy, in-place,
  O(n log n) avg, ~20% faster than classical quicksort because dual
  pivots reduce comparisons); Object arrays use **Timsort** (stable,
  O(n log n) worst, exploits pre-existing sorted runs, O(n) extra
  space). `parallelSort` uses fork/join parallel merge-sort.
  **`System.arraycopy` as HotSpot intrinsic** lowering to `rep movsb`
  (Intel ERMS) / `vmovdqu` AVX2 32-byte loops (x86-64) or `ldp.q`/
  `stp.q` 32-byte pairs (ARM64); runs at memory bandwidth (~10 GB/s);
  no hand-written loop can match. **False sharing** preview on
  parallel array writes to adjacent cache-line slots (full coverage
  in L3/C01). **Escape analysis** on short-lived arrays — scalar
  replacement can eliminate small bounded-size array allocations.
  **Common mistakes** (11 traps): `length` vs `length()`, 0-vs-1-based
  off-by-one, `arr.equals` (reference!) vs `Arrays.equals` (value),
  `Arrays.asList(int[])` size-1 trap, shallow `clone()` on 2-D
  sharing inner rows, pass-by-reference misconception, allocation
  in hot loops, `int[]`→`List<Integer>` autobox cost, `boolean[]`
  byte-per-element, `Object[]` covariance landmines,
  `Arrays.copyOf` truncation/extension semantics. **INTERVIEW
  callout** with 12 questions covering `length` vs `length()`,
  `Arrays.asList(int[])` trap, covariance and `ArrayStoreException`,
  why generics aren't covariant, `int[][]` memory layout, byte sizes,
  `iaload` cost + RCE, dual-pivot vs Timsort, `System.arraycopy`
  intrinsic, `int[]` vs `Integer[]` 50× perf. **Practice (17
  exercises)** — JOL layout dump for int[]/Integer[]/Object[], int[]
  vs Integer[] memory measurement, int[] vs Integer[] sum benchmark,
  row-major vs column-major 2-D matrix benchmark, flat int[]
  reimplementation, javap -c 1-D and 2-D access, multianewarray
  observation, RCE inspection via PrintAssembly, ArrayStoreException
  reproduction, shallow clone() 2-D trap, Arrays.asList(int[]) trap,
  dual-pivot vs Timsort observation on random/partial-sorted input,
  System.arraycopy intrinsic via PrintIntrinsics + manual-loop
  slowdown, AVX2/NEON auto-vectorisation via PrintAssembly,
  boolean[] byte-cost measurement, full end-to-end "explain it
  back" tracing `g = new int[3][4]; g[1][2] = 42`. Recap as ~22
  learning objectives spanning all three layers. Progress 22/371.
  L0 now **73%** complete, C02 chapter **11 / 19**.
- Resume at `L0/C02/T12` — Methods, parameters, return values.

### 2026-06-04 (later — T10)
- Authored `L0/C02/T10` break / continue / labels — **979 lines, 17 Mermaid
  diagrams + 1 native-assembly listing + 4 bytecode listings (one per loop-
  control form)** at the deep bar (§4 + §4a). **Language layer**: the four
  early-exit forms — **`break`** (exit nearest enclosing loop or switch),
  **`continue`** (skip rest of iteration, re-test), **labelled
  `break`/`continue`** (target an outer loop by name), and **`return`**
  (exit the method). The **`break` in switch vs break in loop**
  distinction — both exit the *nearest* enclosing construct; a `break` in
  a switch nested in a loop exits **only the switch**, and `break <label>;`
  is the *only* one-step way to exit the loop from inside a nested switch.
  Arrow-form switch arms don't need `break` (T08 callback). The
  **`continue`-target trap** — in `while`/`do-while`, `continue` jumps to
  the **test**; in `for`, it jumps to the **update clause** — so a
  mechanical for→while migration with a `continue` path silently becomes
  an infinite loop because the counter step that was in the `for` update
  no longer runs. Fix: step the counter *before* the `continue`, or
  restructure. The **early-`continue` guard pattern** as an alternative
  to deep `if/else` nesting (the "early-return" style applied to loops).
  **Labels**: lowercase identifier + `:` before a statement (most often
  a loop); scope = inside the labelled statement only; separate
  namespace from variables; duplicate labels in nested scopes rejected;
  labelling a non-loop (a plain block) is legal and gives a structured-
  goto-forward — discouraged but permitted. **Labelled `break`** for
  multi-loop escape (worked 2-D search example, compared to the
  found-flag + outer-condition-guard alternative — strictly worse on
  both readability and perf). **Labelled `continue`** for "skip this
  outer-loop element entirely" (worked flagged-customer example).
  **`return` vs `break outer;`** — prefer `return` when the whole
  method is the loop; prefer labelled `break` when there's more work
  after the loop. **Memory layer**: every form lowers to a single
  **`goto`** opcode with a compiler-placed target — `break;` → goto
  after-the-loop; `continue;` (while/do-while) → goto test; `continue;`
  (for) → goto update; `break outer;` → goto after-outer-loop;
  `continue outer;` → goto outer-update. *One* opcode, no exception, no
  unwinding, no stack manipulation. Worked `javap -c` for each form
  with annotated offsets, including the labelled-`break`-from-nested-
  loops case showing the single `goto 40` that bypasses inner-close,
  outer-update, outer-close. **Labels have no runtime cost** — pure
  compile-time names, no opcode, no frame slot, no constant-pool entry,
  no LocalVariableTable entry. Operand-stack invariant at every label
  enforced by the verifier — language-level reason `break`/`continue`
  are statements, not expressions. **Architecture layer**: full **x86-64**
  native listing of a `for` with a `continue` path showing the forward
  short `jmp .skip` for `continue`, the backward `jmp .top` predicted
  taken, and the `jge .end` forward predicted not-taken; `break` is a
  forward `jmp` to after-the-loop — statically predicted not-taken,
  so one mispredict (~10–20 cycles) on the exit iteration, amortised
  invisible across the loop run; dynamic predictor (2-bit counters +
  pattern history) learns "always breaks on iter k" after a few runs.
  Labelled `break` is the same forward-`jmp` instruction, just farther
  in offset — no special CPU support needed. **No deoptimisation** —
  `break`/`continue` are in-method jumps the JIT compiles as plain
  branches (contrast with exception throw/catch which *does* unwind,
  deferred to later topics). `return` lowers to the **ABI return
  sequence** — move expr to return register (`eax`/`rax` on x86-64,
  `w0`/`x0` on ARM64), restore caller frame, `ret` (CPU's **Return
  Address Stack** predicts the target). **Common mistakes** (8 traps):
  wrong-level `break` in nested loops, `continue` in `while`
  forgetting the counter, stray-semicolon empty-body causing `break`
  outside a loop (caught at compile), label-after-non-statement parse
  error, label shadowing in nested scopes, redundant `break` in
  arrow-form switch arms (compile warning), `break` from inside a
  `try` block (does NOT skip `finally` — `finally` always runs;
  this is the correct pattern), `continue` in `for-each` doesn't
  re-read the current element (moves to next via iterator/index),
  **`return` from inside a lambda exits only the lambda** (not the
  enclosing method — major trap for readers from imperative
  backgrounds). **INTERVIEW callout** with 11 questions covering
  semantics, the for-vs-while continue target, why Java has labels (no
  general goto), bytecode for `break` (single `goto`), labelled-break
  cost, can-you-break-out-of-try (yes, finally runs), lambda-return
  non-local exit, label/variable namespaces, operand-stack at label,
  labelled-break vs found-flag perf. **Practice (15 exercises)** —
  trace simple break in `javap -c`, while-vs-for continue target diff,
  for→while migration bug repro, 2-D search labelled-break vs flag
  pattern bytecode comparison, labelled-continue customer skip,
  return-vs-break-outer refactor + bytecode diff, labelled block (rare),
  lambda `return` trap, stray break compile error, label shadowing
  rejection, infinite-loop+break, branch-prediction microbench (last-
  iter break vs random-iter break), break-in-try finally verification,
  `javap -l` showing label names absent from LocalVariableTable, full
  end-to-end "explain it back" of `continue outer`. Recap as ~15
  learning objectives spanning all three layers. Progress 21/371. L0
  now **70%** complete, C02 chapter **10 / 19**.
- Resume at `L0/C02/T11` — Arrays (1-D, multi-dimensional).

### 2026-06-04
- Authored `L0/C02/T09` Loops (while, do-while, for, for-each) — **1,162
  lines, 24 Mermaid diagrams + 4 native-assembly listings + bytecode blocks
  per loop form** (~30+ visuals) at the deep bar (§4 + §4a). **Language
  layer**: the four forms — `while` (test-before, 0+ runs, idiom for
  "consume until empty"), `do-while` (test-after, 1+ runs, idiom for
  input-validation / retry / menu), the C-style `for(init; cond; update)`
  with multi-variable init, optional clauses, **loop-variable scope**
  (declared in init = lives only inside loop, deliberate departure from
  pre-C99 C), and **`for-each`** (Java 5+, the `:` reading "in") over
  arrays and `Iterable`s. **Choosing the right loop** decision diagram.
  **`break`/`continue` preview** (`break` → exit; `continue` → skip-to-
  next-iter, *runs `for` update first*) with the classic "rewrite `for`
  as `while` and forget the counter step in the `continue` path"
  warning. Labelled break/continue preview. Full detail deferred to T10.
  **Memory layer**: the universal **backward `goto`** that closes every
  loop + **forward conditional `if_icmp*`** (inverted) that exits.
  Worked `javap -c` for **`while`** (forward `if_icmpge` at top, body,
  `iinc`, backward `goto`), **`do-while`** (body first, **non-inverted**
  `if_icmplt` at bottom — fuses the backward jump and the test into one
  opcode), and **`for`** (bit-identical bytecode to the equivalent
  `while` — `for` is purely syntactic sugar). The dedicated **`iinc`**
  opcode for `i++` (operand-stack-free, in-place on the local — vs the
  4-opcode `i = i + 1`). **`for-each` two desugarings**: over an
  **array**, javac emits an indexed `for` with synthetic locals for the
  array-reference snapshot, the **cached length** (read once before the
  loop — source of the folklore that "for-each hoists the length"),
  the index, and the element — **no `Iterator` allocation**, `iaload`
  for element access; over an **`Iterable`**, javac emits `Iterator it
  = c.iterator(); while (it.hasNext()) { x = (T) it.next(); ... }` —
  **one** `Iterator` allocation per loop run, `invokeinterface`
  `hasNext`/`next`, `checkcast` for generics-erased element. Operand-
  stack trace of one `i < 5` iteration. `break`/`continue` as bytecode
  `goto`s to compiler-placed labels. **Architecture layer**: full
  **x86-64** and **ARM64** native-assembly listings for a counting
  loop — `cmp + jcc + add [rdx+rdi*4] + inc + jmp` on x86-64;
  `cmp + b.cond + ldr [x3, w0, sxtw #2] + add + b` on ARM64; both
  using **scaled-index addressing** so the `*sizeof(int)` is free.
  **Loop-invariant code motion (LICM)** with synthetic preheader
  basic block (worked `Math.sqrt(2)` + `arr.length` example).
  **Strength reduction** table (`*2`→`<<1`, `/8`→`>>>3`, `%16`→`&0xF`,
  multiplicative induction variables → additive). **Range-check
  elimination** — the JIT proves `0 ≤ i < arr.length`, removes the
  per-access bounds-check branch; this is the single optimisation that
  makes Java numeric loops ~C-speed; emphasis on writing the
  idiomatic `for (int i = 0; i < arr.length; i++)` form to hand the
  JIT the easy proof. **Loop unrolling** by 4 worked example
  (main-loop + tail; `-XX:LoopUnrollLimit` knob). **Auto-vectorisation**
  deep dive — full **AVX2** listing (`vmovdqu` + `vpaddd` ymm
  registers, 8 ints/instruction) and **NEON** listing (`ldr q + add
  v.4s + str q`, 4 ints/instruction); 4-16x speedup; `-XX:+UseSuperWord`
  default; Vector API (JEP 338) for explicit SIMD when auto-vec won't
  fire. **Loop peeling** (specialise first iter for alignment/null) and
  **software pipelining** (interleave next-iter work with current).
  **Induction-variable simplification** collapsing tied counters.
  **Branch prediction on the backward branch**: statically predicted
  taken, ~99% correct, single mispredict per loop run on the final
  not-taken — amortised invisible. **Cache and prefetcher** — stride-1
  detection after 3-4 accesses, future cache-line streaming, sequential
  scan at L1 throughput; `int[]` cache-line packing (16 ints/line) vs
  `Integer[]` indirection (pointer per element, prefetcher can't follow).
  **Escape analysis** on `for-each` over `Iterable`: the `Iterator`
  is scalar-replaced (fields in registers, no heap alloc) when it
  doesn't escape — explains why `for-each` overhead is zero for the
  common case. **§4a coverage**: loop-variable **frame slot** (4 bytes
  for `int`, register-allocated by JIT to `edi`/`w20` in hot code,
  spilled only on deopt/debugger); **induction-variable lifetime**
  (exactly the loop scope for `for`-declared counters, vs longer
  for hoisted `while`-counters); **memory efficiency table**
  comparing indexed `ArrayList.get(i)` vs `for-each` vs
  `list.forEach(lambda)`; **`LinkedList.get(i)` is O(n)** and turns a
  loop into O(n²) — use `for-each` (linked iterator is O(1) per step).
  **Common mistakes** (9 traps): off-by-one (`<=` vs `<`), stray
  semicolon, missing update, `for-each` modification →
  **ConcurrentModificationException** via fail-fast `modCount` (fixes:
  `Iterator.remove()`, `removeIf`, iterate-a-copy), async-modify
  race, lambda capture of non-effectively-final counter (and the
  `int captured = i;` workaround, plus the for-each "free" version),
  cached `length` going stale, missed `do-while` semicolon,
  `for-each` over `Map` (use `entrySet`/`keySet`/`values`),
  `LinkedList` indexed loop. **INTERVIEW callout** with 12 questions
  covering `while` vs `do-while`, `for` vs `for-each`, the two
  desugarings, ConcurrentModificationException mechanism, bytecode
  shape, range-check elimination, auto-vectorisation, `LinkedList`
  iteration cost, loop unrolling, backward-branch prediction,
  induction variables, `iinc` efficiency. **Practice (17 exercises)**
  — empty `while(true);` body, four-form translation, `javap -c`
  inspection per form, `for-each` array vs List bytecode comparison,
  EA observability via `-XX:+PrintEliminateAllocations`, range-check
  elimination verification via `-XX:+PrintAssembly`, defeating RCE,
  unroll-limit microbench, AVX2/NEON `-XX:+PrintAssembly` SIMD hunt
  + `-XX:-UseSuperWord` slowdown, branch-prediction win microbench,
  ConcurrentModificationException reproducer + 3 fixes, `LinkedList`
  O(n²) indexed-loop measurement, lambda-capture demo, end-to-end
  "explain it back" tracing one loop from source through bytecode,
  operand stack, JIT native, RCE, AVX2, prefetcher. Recap as ~24
  learning objectives spanning all three layers. Progress 20/371.
  L0 now **67%** complete, C02 chapter **9 / 19**.
- Resume at `L0/C02/T10` — break / continue / labels.

### 2026-06-02
- Authored `L0/C02/T08` Control Flow (if/else, switch, switch expressions) —
  **1,087 lines, 19 Mermaid diagrams + 10 ASCII bytecode/asm/byte-layout
  blocks** (~29 visuals) at the deep bar (§4 + §4a). **Language layer**:
  the three selection forms — `if`/`else`/`else if` with the **dangling-else
  rule** (always brace!), the **ternary `?:`** revisited from T04 as an
  expression form with full JLS §15.25 typing rules. Classical **`switch`
  statement** — allowed selector types (`byte`/`short`/`char`/`int` + enum +
  `String` from Java 7+), the **CT-constant requirement** on case labels
  (T03 forward link), **fall-through** semantics and `break`, `default`
  placement, empty case bodies for value-set sharing. **Switch
  expressions** (Java 14+, JEP 361): the `->` arrow form, multi-label cases
  (`case 1, 2, 3 ->`), block arms with **`yield`** (soft keyword), the
  no-mix-arrow-and-colon rule, **exhaustiveness checking** for enum/sealed.
  **Pattern matching for `switch`** (Java 21, JEP 441): **type patterns**
  (`case Circle c ->`) extending T05's pattern-binding `instanceof` into
  switch, **guarded patterns** via `when <boolean>`, the **`null` case**
  (closing the pre-21 NPE-on-null-switch surprise), **dominance ordering**
  (specific before general, enforced by javac), **sealed-class
  exhaustiveness** (preview from L1/C01 — adding a `permits` subtype breaks
  every consumer switch at compile time), and **record patterns** for
  destructuring. **Memory layer**: the `if_icmp*` / `ifX` / `goto` bytecode
  family (T04 callback) inverted to skip-on-false; the two switch opcodes
  **`tableswitch`** (O(1) indexed jump; byte layout: opcode + padding +
  default + low + high + N offsets) vs **`lookupswitch`** (O(log n) binary
  search; sorted (key, offset) pairs) and `javac`'s **density heuristic**
  (~30% threshold or the explicit `4*(high-low+1) ≤ 8*npairs` cost
  formula). Worked examples for dense/mixed/sparse case sets. The
  **String switch two-step lowering** (Java 7+): hashCode → tableswitch
  on hash → per-hash `equals` chain handles collisions (the `"Aa"`/`"BB"`
  case from T06) → synthetic int marker → second tableswitch on marker
  → user body. The **enum switch indirection** via synthetic
  `$SwitchMap$EnumType` `int[]` in an anonymous inner class, lazy-
  initialised, swallowing `NoSuchFieldError` so separately-recompiled
  enums don't break the consumer — the `iaload` + `tableswitch` pattern.
  **Pattern-switch bytecode**: single `invokedynamic
  SwitchBootstraps.typeSwitch` with class-label static args returns a
  case index; downstream `tableswitch` dispatches; guards re-invoke the
  bootstrap from the next index (effectively a loop). The yield bytecode
  shape (no new opcode — just operand-stack + goto to a join label).
  **Architecture layer**: JIT lowering — `if` → `cmp` + `jcc` (x86-64) or
  `cmp` + `b.cond` (ARM64); short ternaries → **`cmov`/`csel`**
  (branchless, ~1 cycle, no mispredict risk); `tableswitch` → indirect
  jump through a `.rodata` jump table (`sub idx, low; cmp idx, span; ja
  default; jmp [JT + idx*8]` on x86-64; `adr + ldr + br` on ARM64);
  `lookupswitch` → binary-search tree of `cmp + jcc`. Deep dive on
  **branch prediction** — 2-bit saturating counters per branch,
  history-indexed pattern tables, the ~10-20 cycle mispredict cost. The
  **Branch-Target Buffer (BTB)** for indirect jumps — a switch on a hot
  enum value hits the BTB (~2-3 cycles); a switch on randomly-distributed
  input misses (~10-20 cycles). The sorting-before-branchy-reduction
  trick (relate to T01). **When to use what** decision guide — `if` for
  ≤3 boolean conditions, `?:` for inline selection, classical `switch`
  for statement-side dispatch on int/enum/String, switch expression for
  value-producing dispatch, pattern-matching switch for polymorphic
  dispatch over sealed hierarchies. **Common mistakes**: missing `break`
  (with `-Xlint:fallthrough` recommendation), `=` vs `==`, NPE on `switch
  (null)` pre-21, enum-constant addition without exhaustive coverage,
  dangling-else without braces, stray `;` after `if`, side-effects in
  `?:`, `switch` on `long`/`float`/`double`/`boolean` rejection,
  non-CT-constant labels, hashCode-collision performance, dominated
  patterns, non-exhaustive sealed switch. **INTERVIEW callout** with 10
  questions covering bytecode lowering, table/lookup heuristic, String
  switch mechanism, enum SwitchMap, pattern-matching constructs, JIT
  jump-table assembly, BTB behaviour, null case, allowed selector
  types. **Practice (17 exercises)** — javap if-bytecode trace, ternary
  vs if cmov verification, table/lookup javap inspection, String switch
  disassembly, hashCode-collision switch, enum SwitchMap inspection,
  fallthrough warning enable, arrow-form port, yield block arm,
  exhaustiveness compile-error demo, sealed-hierarchy pattern switch,
  guarded patterns + null, dominance compile error, BTB perf microbench
  (hot vs random selectors), explain-it-back end-to-end. Recap as ~17
  learning objectives spanning all three layers. Progress 19/371. L0
  now **63%** complete, C02 chapter **8 / 19**.
- Resume at `L0/C02/T09` — Loops (while, do-while, for, for-each).

### 2026-06-02 (earlier — T07)
- Authored `L0/C02/T07` StringBuilder / StringBuffer — **923 lines, 20 Mermaid
  diagrams + 7 ASCII byte-layout / grow-timeline blocks** at the deep bar
  (§4 + §4a). Closed the **mutability counterpart** to T06's String.
  **Language layer**: the quadratic-concat problem motivating the mutable
  buffer; full **type hierarchy** (`CharSequence` ← `Appendable` ←
  `AbstractStringBuilder` → `StringBuilder` + `StringBuffer`) with the
  rationale for the package-private base class. The **constructor family**
  (`()`=16, `(int)`=exact, `(String/CharSequence)`=`.length()+16`). Every
  one of the 13 **`append` overloads** named with its mechanism — `boolean`
  writes "true"/"false", `int` uses `Integer.getChars` two-digits-at-a-time
  (no intermediate String), `double` uses `FloatingDecimal`, `Object`
  indirects through `String.valueOf`, `char[]` does an `arraycopy`, etc.
  Full **`insert`/`delete`/`replace`/`reverse`/`setCharAt`/`deleteCharAt`**
  mechanics (O(n) shifts via `arraycopy`; surrogate-aware reverse). The
  **`toString()` fresh-copy** semantics. Modern **`+` operator** (T06's
  `invokedynamic` mechanism revisited from the buffer side): single
  expression → no `StringBuilder` at runtime; loops/conditionals → explicit
  `StringBuilder` still right. **Memory layer**: full byte-level layout of
  a `StringBuilder` instance (header 12 + count 4 + coder 1 + 3 pad + value
  ref 4 + 4 pad = 24 bytes) plus the separate backing `byte[]` (16-byte
  header + capacity bytes + padding). `StringBuffer` is ~32 bytes due to
  the extra `toStringCache` field. The crucial **`count` vs
  `value.length`** distinction — code-units-used vs array-capacity. The
  **`coder` byte** mirror of Compact Strings; the **`inflate()` path**
  (LATIN1 → UTF16 walk-and-widen on the first out-of-range append; one-way,
  never re-narrows). **Growth rule** `newCap = max(min, oldCap*2 + 2)` —
  the `+2` hedge for tiny buffers; amortised O(1) per append; worked
  example with 100 appends from default capacity 16 walking through cap
  16→34→70→142 and counting total bytes copied (~120). Pre-sizing
  eliminates all grows. **Architecture layer**: **`System.arraycopy` as a
  HotSpot intrinsic** → `rep movsb` (Intel ERMS) / `rep movsq` / AVX2
  loops on x86-64; `ldp/stp` / NEON `ldp.q`/`stp.q` 32-byte pairs on
  ARM64. The grow path runs at memory bandwidth. **`synchronized` cost
  deep dive**: `lock cmpxchg` on the mark word + memory fence + reverse
  on exit = ~20-50 cycles per `StringBuffer` method call; multiplied
  across thousands of appends makes `StringBuilder` ~5-10× faster.
  Biased-locking history (JEP 374 removal). **Escape analysis + scalar
  replacement** — the *deepest* mechanism in the topic and the real
  reason Java 9+ short concats are fast: HotSpot C2 classifies
  allocations as NoEscape/ArgEscape/GlobalEscape; NoEscape `StringBuilder`s
  get fields lifted into registers (`count` → reg, `value` → stack-byte[])
  and the heap allocation is **never emitted**. The "non-escaping idiom"
  (`return new StringBuilder()....toString();`) — only the final `String`
  survives. EA limits (escape via field/return/un-inlined call kills it).
  `-XX:+PrintEscapeAnalysis` / `+PrintEliminateAllocations` observability.
  **Lifetime table** for every piece — local var on stack frame; wrapper
  on heap (or **eliminated by EA**); initial `byte[16]` on heap (same fate);
  intermediate `value` arrays during grow become garbage; `toString()`
  result `String` outlives the buffer. **Common-mistake callouts**:
  storing SB as map key/value, `+=` in loops (still O(N²) even with
  Java 9+ `invokedynamic`!), capacity miscalculation, `StringBuffer`
  where `StringBuilder` would do, sharing SB across threads (race),
  `equals()` between buffers (inherited `Object.equals` = reference
  identity!), `toString` inside the loop, `insert(0, ...)` cost,
  `append((char[])null)` NPE (vs `append((Object)null)` = "null"),
  naive surrogate-breaking reverse. **Practice (17 exercises)** —
  quadratic demo + measurement, `javap` of loop concat, JOL layout
  dump for SB and SBuf, grow-log reflection subclass, pre-sizing
  payoff, inflate trigger via `'€'`, `append(int)` vs `Integer.toString`
  microbench, EA on/off with `-XX:-DoEscapeAnalysis`, EA-killing
  by static-field escape, StringBuffer overhead measurement,
  `arraycopy` intrinsic inspection via `PrintAssembly`, surrogate-
  preserving reverse demo, `insert(0,...)` vs reverse-trick, `toString`
  snapshot semantics, Java 9+ concat BootstrapMethods inspection,
  `equals` reference-identity trap. **INTERVIEW callout** with 11
  questions covering quadratic concat, SB vs SBuf, AbstractStringBuilder,
  length-vs-capacity, default capacity, growth rule, `toString` copy
  semantics, `inflate`, escape analysis, Java 9+ `+` mechanism,
  `synchronized` cost. Recap as ~16 learning objectives spanning all
  three layers. Progress 18/371. L0 now **60%** complete, C02 chapter
  **7 / 19**.
- Resume at `L0/C02/T08` — Control Flow (if/else, switch, switch expressions).

### 2026-06-02 (earlier)
- Authored `L0/C02/T06` Strings & Text Blocks — **1,174 lines, 24 Mermaid
  diagrams + ~12 ASCII byte-layout / encoding / asm-listing blocks** at the
  deep bar (§4 + §4a). **Closed every loose end the prior five topics left
  open about Strings:** the T02 surrogate-pair flag, the T03 interning intro,
  the T04 `+`-operator `StringConcatFactory` mechanism, the T05 String-
  conversion category, plus the §4a memory/architecture coverage.
  **Language layer**: `String` as an immutable reference type; the four
  invariants (immutable, `final`, literal-interned, hash-cached); the
  literal-vs-`new String(...)` distinction; the full API tour with
  mechanism per method — `length()`/`isEmpty()`/`isBlank()`,
  `charAt`/`codePointAt`, `substring` (always copies post-7u6),
  `indexOf`/`lastIndexOf`/`contains`, `equals`/`equalsIgnoreCase`,
  `compareTo` (UTF-16 code-unit lexicographic — NOT alphabetical),
  `startsWith`/`endsWith`, `replace` (NOT regex) vs `replaceAll`/
  `replaceFirst` (regex), `split` (regex), `trim` vs `strip` (Java 11+
  Unicode-aware), `toLowerCase(Locale.ROOT)` and the Turkish-locale trap,
  `format`/`formatted`/`join`, `chars()`/`codePoints()` streams, `intern()`.
  Text blocks (Java 15+, JEP 378): `"""` syntax, the incidental-whitespace
  stripping algorithm anchored on the closing `"""` indent, `\s` and
  `\<newline>` escapes, and the punchline that a text block compiles to
  **the same `String`** as the equivalent regular literal. **Memory layer**:
  full byte-level layout of a String wrapper (header 12 + coder 1 + 3 pad +
  hash 4 + hashIsZero 4 + value ref 4 + 4 pad = 32 bytes) + the separate
  `value` `byte[]` (16-byte header + N bytes + padding). The `coder` byte
  encoding (0=LATIN1, 1=UTF16). **Compact Strings (JEP 254, Java 9+)** —
  why char[]→byte[]+coder saves ~50% heap on typical workloads, the
  StringLatin1 vs StringUTF16 helper-class delegation, the Latin-1 boundary
  trap (one non-Latin-1 char inflates the entire array), `-XX:-CompactStrings`
  knob. **Surrogate-pair closure**: UTF-16 code unit vs Unicode code point,
  BMP vs supplementary planes, the high (`0xD800`–`0xDBFF`) / low (`0xDC00`–
  `0xDFFF`) surrogate ranges, the encode rule `((CP - 0x10000) >> 10) |
  0xD800` / `((CP - 0x10000) & 0x3FF) | 0xDC00`, worked `"😀"` example with
  the exact byte layout, and the punchline `"😀".length() == 2` because
  `length()` returns **code units**. **String pool deep dive**: the
  `StringTable` is a heap-resident `WeakReference` hash table (moved from
  PermGen in Java 7), `-XX:StringTableSize` tunable, `-XX:+PrintString
  TableStatistics` observable, `-XX:+UseStringDeduplication` G1 option,
  when to use vs abuse `intern()`. **StringConcatFactory deep dive**: the
  pre-9 StringBuilder-chain bytecode vs the Java 9+ single `invokedynamic
  makeConcatWithConstants` call, the **recipe string** syntax (`\1` =
  dynamic arg, `\2` = constant), the bootstrap-once / `MethodHandle`-bound
  CallSite mechanic, why the result is faster (single allocation, no
  StringBuilder object, coder-aware result, JIT-inlinable). **Architecture
  layer**: SIMD intrinsics on `String.equals`/`indexOf`/`hashCode` —
  `ArraysSupport.mismatch` maps to **x86-64 SSE2 `pcmpeqb`/`pmovmskb` /
  AVX2 `vpcmpeqb`** or **ARM64 NEON `cmeq.16b`/`shrn`**, 16-32 bytes per
  iteration, ~memory-bandwidth throughput. Full x86-64 assembly listing
  of the SSE2 equals fast path. The `vectorizedHashCode` Horner-unrolled
  SIMD trick. `-XX:+PrintIntrinsics` / `+PrintAssembly` observability.
  **Immutability section**: why immutability buys hash caching, safe
  concurrent sharing, safe interning, safe map keys, **TOCTOU defence**
  in `ClassLoader.loadClass` / file path / network URL handling. The
  pre-7u6 substring-shared-backing-array historical footnote and the fix.
  **Lifetime table**: where every piece of a String lives and when it's
  reclaimed (local var in stack frame; pooled literal in StringTable ~forever;
  `new String` on heap; substring fresh independent storage; Java 9+
  concat intermediates never escape to the heap). **Common-mistake
  callouts**: `length() != codepoint count`, `==` on Strings,
  `new String("literal")` waste, `+` in tight loops, NPE on null `.equals`,
  regex confusion (`replace` vs `replaceAll`/`split`), `compareTo` code-unit
  order, locale-sensitive case conversion, Latin-1 boundary heap inflation.
  **Practice (17 exercises)** including JOL memory measurement, coder-field
  reflection, surrogate-pair hand-encoding, code-point iteration, substring
  GC verification, Compact-Strings on/off comparison, text-block whitespace
  stripping, `javap` for `invokedynamic` + recipe string, StringTable size
  tuning + `PrintStringTableStatistics`, pre-7u6 substring-leak simulation,
  Turkish-locale trap, SIMD intrinsic inspection via `PrintIntrinsics` +
  `PrintAssembly`, hash-collision pair `"Aa"`/`"BB"`. INTERVIEW callout
  with 11 questions covering immutability, length-vs-code-point, pool
  location, 7u6 substring change, Compact Strings, `invokedynamic` concat,
  SIMD intrinsification, surrogate-pair decode, when to intern, text-block
  compilation. Recap as ~14 learning objectives spanning all three layers.
  Progress 17/371. L0 now **57%** complete, C02 chapter **6 / 19**.
- Resume at `L0/C02/T07` — StringBuilder / StringBuffer.

### 2026-06-01
- **=== Session closed for 2026-06-01. ===** Today's net result: **raised the
  depth bar** (DEPTH-CHECKLIST §4a "Must-Cover for Any Data-Touching Topic"
  + a feedback memory pinned to the index) after the user flagged the first
  T02 draft as too shallow on memory/architecture; then authored four
  consecutive deep-bar topics — **T02 Variables & Primitive Types** (932 ln,
  31 visuals), **T03 Literals & Constants `final`** (725 ln, 18 visuals),
  **T04 Operators** (828 ln, 22 visuals), **T05 Type Conversion & Casting**
  (717 ln, 18 visuals). All four hit the §4a 6-point bar (byte-level
  layout, call-time interaction, lifetime, architecture incl. x86-64 /
  ARM64 native code, memory efficiency, cache/register). Net authored:
  **~3,200 lines, ~89 visuals, 4 topics**. Progress moved **12 → 16 / 371
  (3.2% → 4.3%)**, L0 now **53%** complete, C02 chapter **5 / 19**. No
  unfinished drafts open. **Resume at `L0/C02/T06` — Strings & Text Blocks**
  (see §4 for full scope brief — char[] → byte[]+coder Compact Strings,
  full surrogate-pair closure, text blocks, interning revisited, the
  Latin-1/UTF-16 dual encoding).

- Authored `L0/C02/T05` Type Conversion & Casting — **717 lines, 14 Mermaid
  diagrams + ~4 ASCII bit-pattern diagrams** at the deep bar (§4 + §4a).
  Walked all eight JLS §5 conversion categories. **Language layer**:
  widening primitive (the ladder + the lossy `long→float` precision-loss
  rule); narrowing primitive (the explicit-cast requirement, plus the JLS
  §5.2 CT-constant exception `byte b = 100`); reference widening (free
  upcast) and narrowing (`checkcast` + `ClassCastException`); pattern-
  binding `instanceof` (Java 16+); autoboxing/unboxing semantics. **Memory
  layer**: complete bytecode conversion-opcode table — `i2l`/`i2f`/`i2d`,
  `l2i`/`l2f`/`l2d`, `f2i`/`f2l`/`f2d`, `d2i`/`d2l`/`d2f`, `i2b`/`i2c`/`i2s`,
  `checkcast`, `instanceof`. Deep mechanism on each narrowing — `i2b`/`i2s`
  mask + **sign-extend**; `i2c` masks + **zero-extends** because char is
  unsigned (with worked ASCII bit-patterns); `l2i` drops the high 32 bits;
  `f2i`/`d2i`/`f2l`/`d2l` truncate-toward-zero with **JLS saturation**
  semantics (NaN → 0; +Inf or > MAX → MAX_VALUE; −Inf or < MIN →
  MIN_VALUE). **Architecture layer**: native instructions on **x86-64**
  (`movsxd` for sign-extend; `cvtsi2sd` for int→double; `cvttsd2si` for
  truncate-convert; `movsx`/`movzx` for sub-int) and **ARM64** (`sxtw`,
  `scvtf`, `fcvtzs`, `sxtb`/`sxth`/`uxth`). Key insight: **`l2i` is free**
  on 64-bit CPUs (just the low 32 bits of the same register). Big section
  on the **saturating-float-to-int JIT fixup** showing the real assembler
  sequence `cvttsd2si` + branch-on-`INT_MIN` + NaN test + saturate, plus
  the simpler ARM64 `fcvtzs` shortcut. **Autoboxing**: `Integer.valueOf`
  source + the `IntegerCache.cache[i + 128]` for −128…127; `-XX:Auto
  BoxCacheMax` knob; byte-level **Integer object layout** (header 12 +
  value 4 = 16 bytes); the `List<Integer>` vs `int[]` 5× memory blow-up
  tied back to T02. The other wrapper caches (`Boolean`, `Byte`, `Short`,
  `Long`, `Character` ranges; `Float`/`Double` no cache). **Common-mistake
  callouts**: lossy `long→float` silent loss; `(int) NaN == 0`; `(int) -3.9
  == -3` truncation direction; `Integer == Integer` cache boundary;
  unbox-NPE; `Object[] = String[]` array-store check; deep-call-chain
  `ClassCastException`. **Practice (15 exercises)** including widening
  ladder, lossy-widening test for `long→float`, full narrowing-prediction
  set across NaN/Inf/overflow, `javap` conversion-bytecode hunt,
  `(char)-1`/`(short)char(-1)` round-trip, reference up/down + the failing
  `Cat→Dog` cast, `Integer.valueOf(127)`/`(128)` cache-boundary demo,
  unboxing-NPE reproducer, JIT inspection for `(int)d`, CT-constant
  acceptance/rejection chain, `Object[]` array-store, and an "explain it
  back" for `Integer x = 200; int y = x + 1;`. Interview callout: 9
  questions. Progress 16/371.
- Resume at `L0/C02/T06` — Strings & Text Blocks.

### 2026-06-01 (earlier)
- Authored `L0/C02/T04` Operators (arithmetic, relational, logical, bitwise,
  assignment) — **828 lines, 18 Mermaid diagrams + ~4 ASCII truth/bit/shift
  diagrams** at the deep bar (§4 + §4a). Covered all ~40 Java operators
  across 8 categories. **Language layer**: full precedence/associativity
  table; unary and binary numeric promotion rules in order (double → float →
  long → int); integer-division truncate-toward-zero + sign-of-dividend
  remainder; division-by-zero asymmetry (`ArithmeticException` for integer,
  ±Infinity/NaN for float); IEEE 754 NaN propagation + signed-zero
  semantics; sub-int arithmetic widening (the `byte b = b + 1` failure);
  prefix vs postfix increment; relational + equality (primitive bit-compare
  vs reference pointer-compare); short-circuit `&&`/`||` vs non-short-
  circuit `&`/`|` on booleans; bitwise AND/OR/XOR/NOT with **gate-level
  ASCII diagram**; shift `<<`/`>>`/`>>>` with **barrel-shifter Mermaid** +
  the **5-bit/6-bit shift-count masking rule**; assignment + compound
  assignment with the **implicit-narrowing-cast trick** (JLS §15.26.2);
  String concatenation via `invokedynamic StringConcatFactory` (Java 9+);
  ternary `?:` typing rules; `instanceof` preview (pattern matching).
  **Memory layer**: complete **bytecode opcode family table** (`iadd`/
  `ladd`/`fadd`/`dadd`, `ishl`/`lshl`, `if_icmplt`/`lcmp`+`iflt`, `iinc`,
  `fcmpl`/`fcmpg`/`dcmpl`/`dcmpg` with NaN handling); **operand-stack
  mechanics** for nested expressions (`a + b*c - d` traced step by step
  with stack-depth diagram); `max_stack` Code-attribute connection to
  frame size; compound-assignment-to-field bytecode (load-modify-store
  sequence with the **non-atomicity warning** for concurrent code, forward
  to L3/C01). **Architecture layer**: JIT-emitted native code on **x86-64**
  (`imul`/`lea`/`sub` sequence; LEA's free-add trick) and **ARM64**
  (`mul`/`add`/`sub`); **ALU flags** ZF/SF/CF/OF (x86) and NZCV (ARM64)
  driving conditional branches; division-is-slow (20–80 cycle IDIV vs 1-
  cycle add) motivating **strength reduction** (`x*2` → `x<<1`, `x/8` →
  `x>>>3`, `x%16` → `x & 0xF`, divide-by-magic-number); other JIT optims —
  constant folding, common subexpression elimination, loop-invariant code
  motion (preview), dead-code elimination, branch layout, range-check
  elimination. **Practice (17 exercises)** including promotion sanity,
  `Integer.MIN_VALUE / -1` overflow, `iinc` confirmation via `javap -c`,
  short-circuit-vs-`&`, bitwise idioms (set/clear/toggle/read bit),
  shift-mask demo, `>>` vs `>>>` sign behaviour, precedence trap,
  concat order, ternary typing, 1000-thread `count++` race (with
  `AtomicInteger` fix), and `PrintAssembly` inspection. Interview
  callout: 8 questions including ALU/JIT depth. Progress 15/371.
- Resume at `L0/C02/T05` — Type Conversion & Casting.

### 2026-06-01 (earlier)
- Authored `L0/C02/T03` Literals & Constants (`final`) (725 lines, **18
  visuals: 14 Mermaid + 4 ASCII byte-layout diagrams**) at the new deep bar
  (DEPTH-CHECKLIST §4 + §4a). Covered: literal = source-code form vs runtime
  value (full pipeline diagram source → lexer → CP entry → bytecode → operand
  stack → CPU register); **integer literals** in all four bases (decimal /
  hex `0x` / binary `0b` / octal leading-zero), underscore rules, `L` suffix,
  octal trap; **floating-point literals** in three forms (decimal /
  scientific / hex float `0x1.8p3`), why hex-float exists (exact IEEE 754
  expression); **character literals** with the full escape table, plus the
  classic **Unicode-escape gotcha** (escape processing runs BEFORE
  tokenisation — `
` inside a `//` comment terminates the comment);
  **boolean & null literals** (bit-level: `false`=0, `null`=all-zero bits);
  **String literals** with the auto-interning preview. Deep section on the
  **constant pool** with byte-level layouts of `CONSTANT_Integer` (5 bytes),
  `CONSTANT_Long`/`Double` (9 bytes; two CP indices quirk), `CONSTANT_String`
  (3 bytes, indirects to `CONSTANT_Utf8`). Full **literal-loading bytecode
  family**: `iconst_*`, `lconst_0/1`, `fconst_0/1/2`, `dconst_0/1`, `bipush`,
  `sipush`, `ldc` / `ldc_w`, `ldc2_w`, `aconst_null` — with the smallest-
  opcode-that-fits rule and a `javap -c` walkthrough. **JIT bridge**: small
  literals encoded as immediate operands (`mov eax, 42` / `mov w0, #42`),
  large/64-bit literals placed in a constants region and memory-loaded.
  **`final` keyword**: locals/params/instance/static — including that
  `final` on locals is **pure compile-time enforcement** (no bytecode
  difference) while `final` on fields sets the `ACC_FINAL` flag and gives
  JIT a constant-folding hint. **Compile-time constants** per JLS §15.29 —
  what counts, the `javac`-inlines-at-use-site mechanism, and the famous
  **cross-jar recompile gotcha** (library bumps `VERSION = 2`, consumer
  jar still prints `1` until rebuilt). **`final` and the JMM**
  (safe-publication, full coverage deferred to `L3/C01`). **String
  interning deep dive**: pool is a heap-resident hash table (moved from
  PermGen in Java 7), CT-constant String expressions interned too,
  `.intern()` semantics, `-XX:StringTableSize`. **Memory-footprint
  comparison table** for 5 constant patterns. 14 practice exercises
  including the cross-jar bug reproducer (#7 → #8), the Unicode-escape-in-
  comment exploit (#9), `final`-is-shallow (#10), blank-final analysis (#11),
  lambda-capture preview (#12), and JIT immediate-vs-memory observation
  (#13). Interview callout: 8 questions. Progress 14/371.
- Resume at `L0/C02/T04` — Operators.

### 2026-06-01 (earlier)
- Authored `L0/C02/T02` Variables & Primitive Types — **first draft** (469
  lines, 14 visuals) covered the language layer + IEEE 754 + slot view but
  the user flagged it as **too shallow**: missing byte-level memory layout,
  call-time memory interaction, lifetime, x86/ARM/32-vs-64-bit architecture
  detail, JIT → native register mapping, compressed oops, alignment,
  endianness, and memory efficiency.
- **Raised the depth bar.** Added DEPTH-CHECKLIST §4a "Must-Cover for Any
  Data-Touching Topic" — a 6-point list (byte layout, call mechanics,
  lifetime, architecture, efficiency, cache/register interaction) that every
  topic touching data must answer with a diagram. Saved a feedback memory
  pointing to the same list (`feedback_topic-depth-memory-architecture.md`).
- **Rewrote `L0/C02/T02`** to the new bar (469 → **932 lines**; 14 → **31
  visuals: 23 Mermaid + 8 ASCII byte/bit layouts**). New sections added on
  top of the language-layer content:
  - **Sizes Fixed by JLS** — Java's WORA contrast with C's data models
    (ILP32 / LP64 / LLP64).
  - **From JVM Type to Native CPU** — what the JIT emits for `int add42(int)`
    on x86-64 (`lea eax, [rdi+42]; ret`) vs ARM64 (`add w0, w0, #42; ret`);
    32-bit machines use register pairs for `long`.
  - **Inside a JVM Stack Frame** — byte-level: frame data + locals + operand
    stack; 32-bit logical slots vs 8-byte physical slots on 64-bit HotSpot;
    `-Xss` and `StackOverflowError` as a real physical limit.
  - **Where Variables Actually Live** — full map of locals / instance fields
    / array elements / static fields with reclamation owner.
  - **Inside a Heap Object** — 12/16-byte header (mark word + klass ptr),
    field reordering by descending size, 8-byte padding; concrete byte
    layout for a `Point` instance.
  - **32-bit vs 64-bit JVM and Compressed OOPs** — the divide-by-8 trick
    earning 32 GB of heap with 4-byte references.
  - **Method Calls and Pass-by-Value** — primitives copied vs references-as-
    pointers-by-value; full frame setup/teardown trace; common-myth callout.
  - **Variable Lifetime in Memory** — frame pop / GC / class unload, plus a
    JIT-escape-analysis note.
  - **Memory Efficiency** — `int[1_000_000]` ≈ 4 MB vs `Integer[1_000_000]`
    ≈ 20 MB (5×) with byte math + cache-locality consequence.
  - **CPU Caches** — L1/L2/L3 hierarchy, 64-byte cache lines, why primitive
    arrays beat object-of-primitives.
  - **Endianness** — `.class` is big-endian, x86/ARM are little-endian, JVM
    bridges them.
  - **Worked Example** — single Mermaid diagram showing where every
    variable of a small program physically lives across stack/heap/class.
  Practice doubled (8 → 15 exercises, including pass-by-value primitive vs
  reference, stack-overflow with `-Xss`, JIT inspection with `PrintAssembly`,
  memory-math with JOL). Interview callout expanded to 9 questions.
  Recap expanded to ~20 bullets covering all three layers
  (language + memory + architecture). Progress 13/371.
- Resume at `L0/C02/T03` — Literals & Constants (`final`).

### 2026-05-29
- Assessed depth & progress: skeleton 100% (7 modules, 74 sections, 371
  topics), content 1/371 complete.
- Created [DEPTH-CHECKLIST.md](DEPTH-CHECKLIST.md) — the quality/coverage bar
  every concept topic must clear; linked it from `CONVENTIONS.md`.
- Created this `PROGRESS.md` session-handoff + tracker.
- Discovered the generator hard-codes `status: planned` (logged in §7).
- Renamed the addressing scheme to `L#/C##/T##` codes — chapters now `C##-…`,
  topics `T##-…` (levels keep `L#-…`). Updated the generator, regenerated all
  indexes, moved the one authored topic to `L0/C01/T01`, and fixed every path
  reference (CONVENTIONS, DEPTH-CHECKLIST, templates, this file, memory).
- Authored `L0/C01/T02` Number Systems & Basic Bit Math (411 lines) at the T01
  depth bar; verified against DEPTH-CHECKLIST. Progress now 2/371.
- Authored `L0/C01/T03` What Is a Programming Language; Compiled vs Interpreted
  (181 lines — conceptual, so shorter than T02 by scope, not by rigor). Progress 3/371.
- **Raised the depth standard** (`DEPTH-CHECKLIST.md`): every topic must now
  explain mechanism *under the hood* (data flow through memory/CPU, down to
  gates/electricity) **and carry a diagram for every concept** — not just one
  per page. T01's depth is now the floor, not the ceiling.
- **Rebuilt `L0/C01/T02` to the new bar** (411 → 643 lines; 29 diagrams):
  added transistor bit-storage, half/full/ripple adders, the adder-subtractor
  circuit, sign extension, overflow flags, the barrel shifter, masking, and
  register/ALU dataflow. `L0/C01/T03` queued for the same treatment (see §7).
- **Rebuilt `L0/C01/T03` to the new bar** (181 → 320 lines; 2 → 13 diagrams):
  added the compiler pipeline (lexer→parser→AST→semantic→codegen), the
  interpreter loop vs fetch-decode-execute, the compiled-vs-interpreted memory
  layout, the JIT pipeline (profiler→hot→code cache), and a `javap -c`
  operand-stack walkthrough. C01 T01–T03 are all now at the deep standard.
- Authored `L0/C01/T04` Source to Bytecode to JVM to Machine Code (311 lines,
  13 diagrams) at the deep bar: `.class` anatomy (0xCAFEBABE, constant pool),
  stack-based bytecode + frames, class loading (verify/prepare/resolve/init) and
  parent-delegation loaders, JVM runtime data areas, tiered JIT + code cache,
  and an end-to-end trace. Progress 4/371.
- Extended `L0/C01/T04` with a **"Worked Examples"** section (311 → 548 lines):
  5 sample programs (loops, functions, classes/objects, static, if/else), each
  shown across Java source → bytecode (`javap -c`) → representative x86-64
  assembly + machine-code hex bytes, with a construct→bytecode→native summary
  table and a `javap`/`PrintAssembly` reproduce tip.
- Authored `L0/C01/T05` JDK vs JRE vs JVM (232 lines, 10 diagrams) at the deep
  bar: the nested layers (JDK ⊃ JRE ⊃ JVM), on-disk layout, javac-is-itself-a-
  Java-program, spec-vs-implementation (HotSpot/OpenJ9/GraalVM; OpenJDK vendors),
  and the modern no-standalone-JRE / `jlink` situation. Progress 5/371.
- Authored `L0/C01/T06` Installing Java & PATH/JAVA_HOME (245 lines, 6 diagrams)
  at the deep bar: per-OS JDK install (brew / winget / apt / dnf / SDKMAN) plus
  deep mechanism — PATH resolution (first-match-wins), JAVA_HOME vs PATH,
  env-var inheritance (why you must reopen the terminal), verification,
  multi-JDK switching, and the three classic troubleshooting cases. Progress 6/371.
- Authored `L0/C01/T07` Choosing & Using an IDE (177 lines, 6 diagrams) at the
  deep bar: Run = `javac`+`java` demystified, IDE comparison, and the mechanisms
  behind completion / live-errors / navigation (background AST + symbol index,
  T03), indexing / LSP, and the debugger over **JDWP** reading stack frames (T04).
  Progress 7/371.
- Authored `L0/C01/T08` Command-Line / Terminal Basics (245 lines, 9 diagrams)
  at the deep bar: terminal vs shell, the shell's read→parse→find(PATH)→fork/exec
  →wait loop, filesystem tree + paths, command anatomy, standard streams +
  redirection + pipes (= Java `System.out`/`err`), exit codes, Ctrl-C/SIGINT as
  an OS signal (T01), and the by-hand `javac`/`java` loop. Progress 8/371.
- Authored `L0/C01/T09` Problem Solving & Pseudocode (239 lines, 6 diagrams) at
  the deep bar: plan-vs-code, algorithms, pseudocode, the three building blocks
  (mapped to T01 jumps), flowcharts, the Understand→Plan→Execute→Review method,
  decomposition, a full worked example (find-max: pseudocode → flowchart → trace
  table → Java), the pseudocode→Java mapping, and FizzBuzz. Progress 9/371.
- Authored `L0/C01/T10` Introduction to Git & Version Control (233 lines, 9
  diagrams) at the deep bar: why VCS, centralized vs distributed, the three
  areas (working/staging/repo), the **object model** (blob/tree/commit, SHA
  content-addressing, dedup), the commit DAG + tamper-evidence, branches as
  pointers + HEAD, merging, remotes, and `.gitignore`. Progress 10/371.
- Authored `L0/C01/T11` Reading Errors & Stack Traces (229 lines, 5 diagrams) at
  the deep bar: the 3 kinds of problems, compile-time errors, exceptions
  unwinding up the call stack, the **stack-trace = call-stack-frames** anatomy
  (T04), the fast reading method, a worked NPE debug, the common exceptions, and
  the Throwable hierarchy. **MILESTONE: `L0/C01` (CS & Programming Foundations)
  is COMPLETE — 11/11, all at the deep standard.** Progress 11/371. Next: `L0/C02`
  Java Language — Core (T01 = program structure).
- Started **`L0/C02` — Java Language — Core.** Authored `L0/C02/T01` Program
  Structure (class, main, statements) (233 lines, 6 diagrams) at the deep bar:
  Hello World dissected token by token, the class/file-name rule, the full
  `main` signature (esp. *why* `static`), how the JVM finds & calls `main`
  (T04/T05), statements + `System.out.println` = stdout (T08), blocks, comments,
  command-line args, and common beginner errors. Progress 12/371.
- **=== Session closed for 2026-05-29. ===** Today's net result: created the
  depth standard (`DEPTH-CHECKLIST.md`) and this handoff doc, established the
  `L#/C##/T##` addressing scheme, **completed all of `L0/C01` (11/11)** at the
  deep (under-the-hood + diagram-per-concept) bar, and **started `L0/C02`
  (1/19)**. Total **12 / 371**. **Resume at `L0/C02/T02` — Variables & Primitive
  Types** (see §4 for the exact path). No unfinished drafts open.

### 2026-05-28
- Generated the full skeleton; decided to keep all ~371 topics.
- Authored the first topic (L0 · How Computers Run Programs) at full depth —
  now the reference standard.

## 9. Maintenance Protocol (keep this file true)

Update **this file** whenever the project state changes:

- **Finished a topic?** Set `status: complete` + `last_updated` in the topic
  file → add a row to §6 → bump the counts in §3 and the section row in §5 →
  move §4 "Current Position" to the next topic.
- **Made a decision or changed a convention?** Note it in §7 and §8.
- **Changed structure?** Edit `generate_skeleton.py`, re-run it, then update
  the totals in §3 and §5.
- **Always leave §4 accurate** — it is the one thing the next session reads to
  know the single next action.
