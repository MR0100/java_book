# `istio-circuit-breaker/` — Mesh-level circuit breaking & fault injection

Implements the **circuit breaker** and **bulkhead** resilience patterns at the
service-mesh layer — no Java library, no application redeploy.

Backing topics: **L5/C02/T14** (resilience patterns) and the **Netflix case
study, L5/C12/T01** (Hystrix → Envoy/mesh circuit breaking).

## Files

| File | Role |
|------|------|
| `destinationrule.yaml` | The circuit breaker: `connectionPool` (bulkhead / load shedding) + `outlierDetection` (eject unhealthy hosts). **Apply this.** |
| `virtualservice.yaml` | *Optional* fault injection (delays + 503s, header-gated) to TEST that the breaker and your client timeouts actually work. |

## How the two halves work together

```
                 connectionPool (bulkhead)          outlierDetection (breaker)
client ──► Envoy ──► caps concurrent conns/   ──► strips out hosts returning
   ▲                  requests; fast-fails 503      5xx; probes them back in
   │                  when caps are hit               │
   └─────────────────── 503 fast-fail ◄───────────────┘
```

1. **connectionPool** bounds how much in-flight work can pile up against the
   dependency. Hitting a cap returns 503 immediately (**fail fast**) instead of
   queueing requests until everything times out (**fail slow** → latency
   collapse, the classic cascading-failure trigger).
2. **outlierDetection** watches per-host error rates and **ejects** a backend
   that keeps returning 5xx, then re-admits it after `baseEjectionTime`. That is
   the circuit "opening" and "half-opening" — done by the sidecar, per host.

`maxEjectionPercent` and `minHealthPercent` stop the breaker from ejecting the
*entire* pool (an open circuit that can never close).

## From Hystrix to the mesh (L5/C12/T01)

Netflix's **Hystrix** put the circuit breaker *inside* the JVM (a library
wrapping every remote call). The mesh approach moves the identical pattern into
the Envoy sidecar: language-agnostic, uniform across services, and tunable
without a code change or redeploy. Hystrix is now in maintenance mode precisely
because mesh/Envoy (and, in-process, Resilience4j) superseded it. Use mesh
breaking for cross-service calls; use Resilience4j in-process when you need a
fallback method or finer-grained, business-aware control.

## Testing it (fault injection)

```bash
kubectl apply -f destinationrule.yaml      # the breaker
kubectl apply -f virtualservice.yaml       # optional fault injection

# Drive faulted traffic and watch the breaker trip. Only x-chaos:on is faulted.
for i in $(seq 1 50); do
  curl -s -H 'x-chaos: on' -o /dev/null -w '%{http_code}\n' http://url-shortener/
done | sort | uniq -c        # expect a mix of 200 / 503 / slow responses

# Inspect Envoy's circuit-breaker + outlier stats on the sidecar:
kubectl -n url-shortener exec deploy/url-shortener -c istio-proxy -- \
  pilot-agent request GET stats | grep -E 'outlier|circuit_breakers|pending'
```

**Remove `virtualservice.yaml` after testing** — it is a chaos tool, not a
steady-state config.

## Prerequisites

- Istio mesh installed; `url-shortener` namespace in the mesh.
- The Deployment/Service from `../spring-boot-3-deployment/`.
