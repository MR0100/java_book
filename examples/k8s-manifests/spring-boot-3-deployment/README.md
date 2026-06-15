# `spring-boot-3-deployment/` — Hardened Deployment, Service, HPA, ConfigMap

A production-grade baseline for running a Spring Boot 3 service (the book's
`url-shortener`) on Kubernetes 1.29+. Every file is heavily commented; this
README is the map.

## What this demonstrates

| File | Demonstrates |
|------|--------------|
| `configmap.yaml` | Namespace (with Istio injection + Pod Security Admission `restricted`), a token-less ServiceAccount, and a non-secret ConfigMap. Apply this **first** — the others depend on the namespace/SA. |
| `deployment.yaml` | The full **security hardening checklist** (L4/C08/T19), JVM container right-sizing (L5/C03/T15), and Actuator-backed startup/readiness/liveness probes (L4/C10). |
| `service.yaml` | A ClusterIP Service with an Istio-named `http` port spanning both canary subsets. |
| `hpa.yaml` | `autoscaling/v2` HPA on CPU, with commented examples for memory and Prometheus-Adapter custom metrics, plus `behavior` anti-flap tuning. |

## The hardening checklist (L4/C08/T19) — where each control lives

All in `deployment.yaml` unless noted:

- `runAsNonRoot: true`, `runAsUser/runAsGroup: 1000` — never run as root.
- `readOnlyRootFilesystem: true` — immutable container FS, with writable
  `emptyDir` volumes mounted only at `/tmp` and `/var/tmp`.
- `allowPrivilegeEscalation: false`, `privileged: false`.
- `capabilities.drop: [ALL]` — strip every Linux capability.
- `seccompProfile.type: RuntimeDefault` — block dangerous syscalls.
- `automountServiceAccountToken: false` — on both the pod and the SA
  (`configmap.yaml`).
- Digest-pinned image (`@sha256:...`) — immutable, reproducible deploys.
- Pod Security Admission `enforce: restricted` at the namespace level
  (`configmap.yaml`) — the cluster rejects any pod that regresses on the above.

## JVM right-sizing (L5/C03/T15)

The container sets `-XX:MaxRAMPercentage=75.0` instead of a fixed `-Xmx`. The
JVM is cgroup-aware (JDK 10+), so the heap scales with the **container memory
limit**, leaving ~25% headroom for off-heap memory (metaspace, thread stacks,
code cache, direct buffers). Setting `-Xmx == limit` is the classic cause of
container OOM-kills. See the inline comments on the `resources` and `env`
blocks.

## Probes (L4/C10)

Wired to Spring Boot Actuator health groups:

- **startupProbe** → `/actuator/health/readiness` — covers slow JVM cold start
  (up to 150s) so a long boot isn't mistaken for a hang.
- **readinessProbe** → `/actuator/health/readiness` — pulls the pod from
  Service endpoints when a dependency is down, **without** restarting it.
- **livenessProbe** → `/actuator/health/liveness` — restarts only on
  unrecoverable state. Keep it dependency-free to avoid restart storms.

## Apply

```bash
# 1) Namespace + ServiceAccount + ConfigMap (creates the namespace).
kubectl apply -f configmap.yaml

# 2) Workload + Service + autoscaler.
kubectl apply -f deployment.yaml -f service.yaml -f hpa.yaml

# Watch the rollout.
kubectl -n url-shortener rollout status deploy/url-shortener
kubectl -n url-shortener get pods,svc,hpa

# Quick health check from inside the cluster.
kubectl -n url-shortener exec deploy/url-shortener -c url-shortener -- \
  wget -qO- http://localhost:8080/actuator/health/readiness
```

## Before you apply — substitutions

- **Image digest**: replace the placeholder
  `registry.example.com/url-shortener@sha256:0000...` in `deployment.yaml` with
  your CI-built digest (`crane digest registry.example.com/url-shortener:1.0.0`).
- **Secrets**: the ConfigMap holds non-secret config only. Create a `Secret`
  (or `ExternalSecret`) for `SPRING_DATASOURCE_PASSWORD` and reference it from
  the Deployment with `secretKeyRef`.
- **Replica/HPA bounds, resource sizes, zone topology keys**: tune to your
  cluster and SLOs.
