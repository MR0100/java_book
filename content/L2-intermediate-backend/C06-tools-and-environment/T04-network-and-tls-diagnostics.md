---
title: "Network & TLS Diagnostics (dig, ss, lsof, nc, tcpdump, openssl)"
slug: network-and-tls-diagnostics
level: L2
module: "Intermediate Java & Backend Foundations"
section: "Tools & Environment"
type: reference
difficulty: intermediate
order: 4
tags: [dig, nslookup, host, getent, resolv-conf, ss, netstat, lsof, nc, netcat, ping, traceroute, mtr, tcpdump, wireshark, tshark, bpf, openssl, s-client, x509, tls, sni, dns, tcp, time-wait, close-wait, pcap, truststore, troubleshooting]
prerequisites: [osi-and-tcp-ip-models, ip-ports-and-sockets, dns-resolution-records, tls-ssl-and-certificates]
status: complete
estimated_minutes: 45
last_updated: 2026-06-05
---

# Network & TLS Diagnostics

When a request fails — hangs, refuses, won't resolve, won't verify — the entire skill is **isolating which layer broke** and reaching for the one tool that owns it. This file maps the [C03 OSI/TCP-IP stack](../C03-networking-fundamentals/T01-osi-and-tcp-ip-models.md) onto its diagnostic tools, top to bottom: DNS → TCP/ports → path → the wire → TLS. It expands [T01 §5](./T01-backend-toolchain-quick-reference.md).

```mermaid
flowchart TB
  Sym["a request fails — which layer?"]
  Sym --> DNS["NAME resolution<br/>'could not resolve host'<br/>→ dig · nslookup · getent"]
  Sym --> TCP["TCP / PORT<br/>'connection refused' · 'address in use'<br/>→ ss · lsof · nc"]
  Sym --> PATH["PATH / reachability<br/>'timed out' (black-hole)<br/>→ ping · traceroute · mtr · tcpdump"]
  Sym --> TLSL["TLS / certificate<br/>'certificate verify failed'<br/>→ openssl s_client · x509"]
  Sym --> APP["HTTP / app<br/>4xx / 5xx body<br/>→ curl -v (T02)"]
```

The discipline: **go bottom-up**. Confirm the name resolves, then the port is open, then TLS verifies, then blame the app. Skipping a layer is how an afternoon disappears.

---

## 1. DNS Layer — Does the Name Resolve?

The [C03 DNS](../C03-networking-fundamentals/T04-dns-resolution-records.md) lookup turns a hostname into an IP. When it fails, nothing above it can work.

### 1.1 `dig` — the professional's tool

```bash
dig api.example.com                    # full answer: QUESTION, ANSWER, AUTHORITY sections + TTLs
dig api.example.com +short             # just the IP(s) — scriptable
dig api.example.com A +short           # explicit record type (A=IPv4, AAAA=IPv6)
dig example.com MX                      # mail servers     dig example.com NS    # nameservers
dig example.com TXT                     # SPF/DKIM/verification records
dig example.com CNAME                   # alias chain
dig @8.8.8.8 api.example.com            # ask a SPECIFIC resolver (bypass your local one)
dig api.example.com +trace              # follow resolution from the ROOT servers down — find where it breaks
dig -x 8.8.8.8                          # REVERSE lookup (IP → name, PTR record)
dig api.example.com +noall +answer      # trim to just the answer lines
```

Reading the output: the **ANSWER** section has the records + their **TTL** (seconds the answer may be cached — a stale TTL explains "I updated DNS but still get the old IP"); **AUTHORITY** names the zone's nameservers; `status: NOERROR` vs `NXDOMAIN` (name doesn't exist) vs `SERVFAIL` (resolver error) is the first thing to check.

### 1.2 `getent` — what the *application* actually sees

```bash
getent hosts api.example.com           # the OS resolver result (honors /etc/hosts + nsswitch)
```

> [!WARNING]
> **`dig` queries DNS directly; it ignores `/etc/hosts`.** Your app uses the OS resolver, which checks `/etc/hosts` *first* (per `/etc/nsswitch.conf`). So `dig` can show the "real" DNS answer while your app connects somewhere else entirely because of a `/etc/hosts` override. When "dig says X but the app hits Y," use **`getent hosts`** — it follows the same path the application does.

The resolution config: **`/etc/resolv.conf`** (which resolvers + search domains), **`/etc/hosts`** (static overrides), **`/etc/nsswitch.conf`** (the order: `hosts: files dns` = hosts-file before DNS). `nslookup` and `host` are simpler alternatives to `dig` for a quick check.

---

## 2. TCP / Port Layer — Is the Port Open, and Who Owns It?

A name that resolves still needs an open TCP port behind it ([C03 ports & sockets](../C03-networking-fundamentals/T03-ip-ports-and-sockets.md)).

### 2.1 `ss` — socket statistics (the modern `netstat`)

```bash
ss -ltnp           # Listening, Tcp, Numeric (no name lookup), Process — "what's listening + which PID?"
ss -tnp            # established TCP connections with processes
ss -tn state time-wait | wc -l    # count TIME_WAIT sockets
ss -s              # summary totals by state
ss -tnp 'dport = :5432'           # filter: connections to the Postgres port
```

> [!NOTE]
> `ss` reads `/proc/net/tcp` and the kernel's netlink interface directly — far faster than the legacy `netstat -tlnp` (which it replaces on modern Linux). Old habit → new tool: `netstat -tlnp` ≈ `ss -ltnp`, `netstat -s` ≈ `ss -s`.

### 2.2 `lsof` — which process owns a socket

```bash
lsof -iTCP:8080 -sTCP:LISTEN       # who is listening on 8080? (the "Address already in use" fix)
lsof -i :5432                      # everything touching port 5432
lsof -i -nP                        # all network files, numeric (-n no DNS, -P no port names)
lsof -p 12345                      # everything (files + sockets) open by PID 12345
```

### 2.3 `nc` (netcat) — probe and move bytes

```bash
nc -vz api.example.com 443         # probe: is the port open? (-z scan, no data; -v verbose)
nc -vz -w3 host 5432               # with a 3s timeout
nc -l 9000                         # LISTEN on 9000 (a throwaway server to test the other direction)
echo -e 'GET / HTTP/1.0\r\n' | nc example.com 80   # hand-craft a raw HTTP request
nc host 5432 < /dev/null           # test pure TCP reachability to the DB
```

`nc` builds the [4-tuple](../C03-networking-fundamentals/T03-ip-ports-and-sockets.md) (src-ip:src-port → dst-ip:dst-port) and tells you whether the handshake completes — isolating "is it the network or my client/TLS?".

### 2.4 TCP connection states — what they tell you

The [C03 TCP](../C03-networking-fundamentals/T02-tcp-vs-udp.md) state machine surfaces in `ss`:

| State | Meaning / what a pile-up signals |
|-------|----------------------------------|
| `LISTEN` | a server waiting for connections |
| `ESTABLISHED` | active connection |
| `TIME_WAIT` | normal post-close wait (~60s) on the side that closed first; **many** = high connection churn (open/close per request instead of pooling — [C05/T09](../C05-databases-and-sql/) reuse fixes it) |
| `CLOSE_WAIT` | the **remote** closed but your app hasn't `close()`d its socket; **many** = a socket/connection **leak in your code** (the classic un-closed-resource bug) |
| `SYN_SENT` (piling up) | can't complete handshakes → firewall/black-hole |

---

## 3. Path & Reachability — ICMP

```bash
ping api.example.com               # round-trip reachability + RTT (latency)
traceroute api.example.com         # the hop-by-hop path; find WHERE packets die
mtr api.example.com                # traceroute + ping combined, live — per-hop packet loss %
```

> [!WARNING]
> **`ping` failing does not mean the service is down.** Many networks/firewalls/cloud security groups drop ICMP while allowing TCP — so `ping` times out but `https://` works fine. Use `ping`/`traceroute` for a *rough* path picture; confirm the actual service with `nc -vz host port` or `curl`, which use the real transport.

---

## 4. The Wire — Packet Capture

When higher tools disagree with reality, look at the actual bytes.

### 4.1 `tcpdump`

```bash
sudo tcpdump -i any -n port 5432               # all traffic to/from the DB port, numeric
sudo tcpdump -i any -n host api.example.com    # all traffic with one host
sudo tcpdump -i any -n 'tcp port 443 and host 1.2.3.4'   # BPF filter: AND/OR/not
sudo tcpdump -i any -nA port 80                # -A: print ASCII payload (watch plaintext HTTP)
sudo tcpdump -i any -n -c 50 -w cap.pcap port 5432       # capture 50 packets to a file
tcpdump -nr cap.pcap                           # read a saved capture back
```

Reading a line: `time src.port > dst.port: Flags [S], seq …` — `[S]` SYN, `[S.]` SYN-ACK, `[.]` ACK, `[P.]` push (data), `[F.]` FIN, `[R]` reset. Those flags *are* the [C03 TCP 3-way handshake](../C03-networking-fundamentals/T02-tcp-vs-udp.md) on the wire:

```mermaid
sequenceDiagram
  participant C as client
  participant S as server
  C->>S: Flags [S]    (SYN — "let's talk")
  S->>C: Flags [S.]   (SYN-ACK — "ok, you too")
  C->>S: Flags [.]    (ACK — connection ESTABLISHED)
  Note over C,S: data flows: [P.] push / [.] ack …
  C->>S: Flags [F.]   (FIN — "done")
  Note over C,S: a lone repeated [S] with no [S.] → black-holed (firewall/wrong host)
```

> [!NOTE]
> **What tcpdump actually does:** it puts the interface in promiscuous mode and pulls frames through an in-kernel **BPF** (Berkeley Packet Filter) *before* normal processing — so you see exactly what crossed the wire, every [encapsulation layer](../C03-networking-fundamentals/T01-osi-and-tcp-ip-models.md) (Ethernet → IP → TCP → payload) made literal. The BPF filter runs in the kernel so it's cheap even on busy links.

### 4.2 Wireshark / `tshark`

`tcpdump -w cap.pcap` on the server, then open `cap.pcap` in **Wireshark** on your laptop for the GUI: protocol decoding, **Follow → TCP Stream** (reassemble a whole conversation into readable text — the killer feature), display filters (`http.response.code == 500`, `tcp.flags.reset == 1`), and the TLS handshake decoded message-by-message. `tshark` is Wireshark's CLI for headless captures.

---

## 5. TLS Layer — Handshake & Certificate

A TLS failure ([C03 TLS/PKI](../C03-networking-fundamentals/T06-tls-ssl-and-certificates.md)) blocks HTTPS even when DNS and TCP are perfect. `openssl` is the inspector.

### 5.1 `openssl s_client` — perform a real handshake

```bash
openssl s_client -connect api.example.com:443 -servername api.example.com </dev/null
# -servername sets SNI — REQUIRED on shared hosts/CDNs or you get the wrong (default) cert
openssl s_client -connect host:443 -showcerts            # dump the FULL chain (leaf → intermediates)
openssl s_client -connect host:443 -tls1_3               # force a version to test support
openssl s_client -starttls postgres -connect host:5432   # opportunistic TLS (also: smtp, imap, mysql)
```

The output shows the negotiated protocol + cipher, the certificate chain, and **`Verify return code: 0 (ok)`** — or a non-zero code naming the failure (`10` expired, `19` self-signed in chain, `21` unable to verify the first cert / missing intermediate, `62` hostname mismatch).

### 5.2 `openssl x509` — inspect the certificate

```bash
# Pull a live cert and read the essentials
echo | openssl s_client -connect host:443 -servername host 2>/dev/null \
  | openssl x509 -noout -subject -issuer -dates -ext subjectAltName
# -subject  who it's for     -issuer  which CA signed it
# -dates    notBefore/notAfter (EXPIRY — the #1 cause of sudden prod TLS outages)
# -ext subjectAltName  the hostnames it's valid for (must include the one you used)

openssl x509 -in cert.pem -noout -fingerprint -sha256      # pin/compare a cert
```

### 5.3 The verification checklist

A client trusts a cert only if **all** hold ([C03 PKI](../C03-networking-fundamentals/T06-tls-ssl-and-certificates.md)):

```mermaid
flowchart LR
  Leaf["leaf cert<br/>(api.example.com)"] -->|signed by| Int["intermediate CA"]
  Int -->|signed by| Root["root CA"]
  Root -.->|"must be in the client's<br/>truststore (OS or JVM cacerts)"| Trust[("trust anchor")]
  Leaf -.->|"SAN must match<br/>the host you dialed"| Host["api.example.com"]
  Leaf -.->|"now ∈ notBefore..notAfter"| Clock["⏰ not expired"]
```

1. **Chain** — leaf → intermediate(s) → a root the client trusts. Missing intermediate (`code 21`) is the sneaky one: works in a browser (which caches intermediates) but fails from `curl`/the JVM. Always serve the full chain.
2. **Validity window** — now is within notBefore..notAfter. **Expiry is the most common prod outage**; monitor it.
3. **Hostname** — the host you connected to is in the cert's **subjectAltName** (CN is ignored by modern clients).

> [!NOTE]
> **The JVM has its own truststore**, not the OS one: `$JAVA_HOME/lib/security/cacerts`. A cert that verifies in `curl`/the browser can still throw `SSLHandshakeException: PKIX path building failed` in Java if its CA isn't in `cacerts` — common with corporate/internal CAs. Fix: import the CA with `keytool -importcert -cacerts -alias corp -file ca.pem`. This is the bridge from this topic to [C05/T09 JDBC](../C05-databases-and-sql/T09-jdbc-and-connection-pooling-hikaricp.md) TLS connections.

---

## 6. The Decision Flow

| Symptom | Layer | First command |
|---------|-------|---------------|
| `could not resolve host` / `UnknownHostException` | DNS | `dig host +short`; `getent hosts host`; check `/etc/hosts` |
| Updated DNS, still old IP | DNS (TTL/cache) | `dig host` (read TTL); flush the resolver cache |
| `Connection refused` | TCP | `ss -ltnp` (server up?); `nc -vz host port` (from client) |
| `Address already in use` | TCP | `lsof -iTCP:PORT -sTCP:LISTEN` → kill or change port |
| Connection **times out** (hangs) | path/firewall | `tcpdump` (do SYNs get replies?); `traceroute`; check security groups ([C03 firewall/NAT](../C03-networking-fundamentals/T11-firewalls-and-nat-basics.md)) |
| Many `CLOSE_WAIT` | app (socket leak) | `ss -tnp state close-wait` → fix un-closed sockets in code |
| Many `TIME_WAIT` | churn | reuse connections / pool ([C05/T09](../C05-databases-and-sql/T09-jdbc-and-connection-pooling-hikaricp.md)) |
| `certificate verify failed` | TLS | `openssl s_client -connect host:443 -servername host` |
| `PKIX path building failed` (Java only) | TLS (JVM truststore) | import the CA into `cacerts` with `keytool` |
| Cert "expired" surprise | TLS | `openssl x509 -noout -dates` |
| Wrong cert served | TLS (SNI) | add `-servername host` |

## 7. Worked Example — "My Service Can't Reach the Database"

Walk the stack bottom-up — each step rules out a layer:

```bash
# 1. DNS: does the DB hostname resolve, and to the IP you expect?
getent hosts db.internal            # → 10.0.3.12   (if empty → DNS/hosts problem, stop here)

# 2. TCP: is the DB port reachable from THIS host?
nc -vz db.internal 5432             # "succeeded!" → network OK; "timed out" → firewall/security group
                                    # ("refused" → DB not running or wrong port)

# 3. If it timed out, see if SYNs are answered:
sudo tcpdump -i any -n host db.internal and port 5432   # lone [S] with no [S.] → black-holed

# 4. TLS (if the DB enforces it): does the handshake + cert verify?
openssl s_client -starttls postgres -connect db.internal:5432 </dev/null | grep 'Verify return'

# 5. Only now suspect the app: credentials, pool exhaustion (C05/T09), wrong database name.
psql "postgresql://user:pass@db.internal:5432/appdb" -c 'SELECT 1'
```

Five commands turn "the database is down" (it usually isn't) into a precise statement of which layer actually failed.

## Recap

- **Isolate the layer, bottom-up:** DNS → TCP → path → TLS → app. The map mirrors the [C03 OSI stack](../C03-networking-fundamentals/T01-osi-and-tcp-ip-models.md).
- **DNS:** `dig` (+short/+trace/@server/record types/TTL) queries DNS directly; **`getent hosts`** follows what the *app* sees (honors `/etc/hosts`) — use it when dig and the app disagree.
- **TCP/ports:** `ss -ltnp` (modern netstat, reads `/proc/net/tcp`), `lsof -i` (which PID owns a port), `nc -vz` (probe). Read states: **`CLOSE_WAIT` pile-up = a socket leak in your code**; **`TIME_WAIT` pile-up = connection churn** (pool instead).
- **Path:** `ping`/`traceroute`/`mtr` — but ICMP is often filtered, so `ping` failing ≠ service down; confirm with `nc`/`curl`.
- **The wire:** `tcpdump` (promiscuous + in-kernel BPF) shows literal bytes + flags (`[S]`/`[S.]`/`[R]`); capture `-w` and analyze in **Wireshark** ("Follow TCP Stream").
- **TLS:** `openssl s_client -servername` (SNI!) does a real handshake; `openssl x509 -dates/-ext subjectAltName` inspects the cert; verify **chain + expiry + hostname**; remember the **JVM's separate `cacerts` truststore** (`PKIX path building failed`).

## Next

Continue to **[T05 — Local dev environment: Docker & Testcontainers](./T05-local-dev-environment-docker-testcontainers.md)** — running real dependencies in containers and wiring them into integration tests.

[Back to C06 index](./README.md) · [Back to L2 index](../README.md)
