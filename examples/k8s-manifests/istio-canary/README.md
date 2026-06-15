# `istio-canary/` — Canary traffic splitting with Istio

Routes 90% of traffic to the stable `v1` and 10% to a `v2` canary, with a
header escape hatch so internal testers can hit `v2` directly.

## Files

| File | Role |
|------|------|
| `destinationrule.yaml` | Declares the `v1` / `v2` **subsets** (by pod `version` label) and the load-balancing policy. |
| `virtualservice.yaml` | The **routing** rules: header match → `v2` for testers, then a weighted 90/10 split for everyone else. |

## Mental model: which object does what

- **VirtualService = "where does this request go?"** (matching, weights,
  retries, timeouts).
- **DestinationRule = "what are the destinations, and how do we talk to
  them?"** (subset definitions, LB, connection pools, outlier detection).

You need both: the VirtualService can only reference subset names that the
DestinationRule defines.

## Canary vs blue-green

| | **Canary** (this dir) | **Blue-green** |
|---|---|---|
| Traffic | Gradually shifted: 5% → 10% → 50% → 100% | Flipped all-at-once from blue to green |
| Blast radius | Small and tunable (only the canary % is exposed) | Full — a bad green hits 100% instantly |
| Resource cost | Run a few extra v2 pods alongside v1 | Run a **full second copy** of the stack |
| Rollback | Turn the weight back to 0 | Flip the router back to blue |
| Best for | Validating a risky change against real traffic with live metrics | Fast, clean cutover when you can't tolerate mixed versions (e.g. incompatible schema) |

With Istio, **both** are just edits to the `weight` fields: canary = many small
steps; blue-green = a single 100→0 / 0→100 flip. The same manifests express
either strategy.

## Progressive rollout

Advance `v2`'s weight only while its error rate and latency stay within SLO
(watch the dashboards from `observability-stack/`):

```
90/10 → 75/25 → 50/50 → 25/75 → 0/100
```

Automate it with **Argo Rollouts** or **Flagger** — they patch these exact
weights on a schedule and auto-roll-back on a metric regression. This directory
is the manual baseline those tools build on.

## Prerequisites

- An Istio mesh installed and the `url-shortener` namespace labelled
  `istio-injection=enabled` (done in `../spring-boot-3-deployment/configmap.yaml`).
- A `v2` Deployment: copy `../spring-boot-3-deployment/deployment.yaml`, rename
  to `url-shortener-v2`, set `version: v2` on the pod labels, and point it at
  the v2 image digest.

## Apply

```bash
kubectl apply -f destinationrule.yaml   # define subsets FIRST
kubectl apply -f virtualservice.yaml    # then the routing that references them

# Verify the split (run a loop; ~1 in 10 should hit v2).
for i in $(seq 1 20); do
  kubectl -n url-shortener exec deploy/url-shortener -c url-shortener -- \
    wget -qO- http://url-shortener/actuator/info | grep -o '"version":"[^"]*"'
done

# Force the canary as an internal tester:
#   curl -H 'x-canary: always' http://url-shortener/...
```

Backing topics: progressive delivery / deployment strategies (L5 architecture &
leadership), Istio traffic management.
