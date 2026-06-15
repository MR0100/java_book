# Kubernetes + Istio manifests for the `url-shortener` service

Production-quality, heavily-commented Kubernetes (1.29+) and Istio manifests
that deploy and operate the book's `url-shortener` Spring Boot 3 service. Read
each file top-to-bottom — the comments **are** the teaching material.

These accompany the Java backend book and implement, in real YAML, the patterns
discussed in the noted topics.

## The four directories

| Directory | Demonstrates | Backing topics |
|-----------|--------------|----------------|
| [`spring-boot-3-deployment/`](spring-boot-3-deployment/) | Hardened Deployment + Service + HPA + ConfigMap. Full container/pod **security hardening**, **JVM container right-sizing**, and Actuator-backed **startup/readiness/liveness probes**. | L4/C08/T19 (security), L5/C03/T15 (JVM sizing), L4/C10 (probes) |
| [`istio-canary/`](istio-canary/) | **90/10 canary** traffic split (v1/v2) with header-based routing for internal testers; progressive-rollout guidance; canary vs blue-green. | L5 deployment strategies / progressive delivery |
| [`istio-circuit-breaker/`](istio-circuit-breaker/) | **Mesh-level circuit breaker**: `connectionPool` (bulkhead) + `outlierDetection` (eject unhealthy hosts), plus optional fault injection for chaos testing. | L5/C02/T14 (resilience), L5/C12/T01 (Netflix case study) |
| [`observability-stack/`](observability-stack/) | Prometheus **ServiceMonitor** + **PrometheusRule** alerts + an **OpenTelemetry Collector** (Deployment + ConfigMap) exporting traces to Tempo/Jaeger. | L4/C10 (observability), lab-06 (tracing) |

## API versions used (all current/stable)

| Resource | apiVersion |
|----------|------------|
| Deployment | `apps/v1` |
| Service, ConfigMap, Namespace, ServiceAccount | `v1` |
| HorizontalPodAutoscaler | `autoscaling/v2` |
| VirtualService, DestinationRule (Istio) | `networking.istio.io/v1` (use `v1beta1` on older Istio; schema is identical) |
| ServiceMonitor, PrometheusRule (prometheus-operator) | `monitoring.coreos.com/v1` |

## Prerequisites

1. **A Kubernetes cluster, 1.29+.** Local options:
   - **kind:** `kind create cluster --image kindest/node:v1.30.0`
   - **minikube:** `minikube start --kubernetes-version=v1.30.0 --cpus=4 --memory=8192`
2. **Istio** (for `istio-canary/` and `istio-circuit-breaker/`):
   ```bash
   istioctl install --set profile=demo -y
   # The url-shortener namespace is created with istio-injection=enabled by
   # spring-boot-3-deployment/configmap.yaml.
   ```
   On minikube you also need a tunnel for ingress: `minikube tunnel`.
3. **Prometheus Operator + Grafana** (for `observability-stack/`):
   ```bash
   helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
   helm install kube-prometheus-stack prometheus-community/kube-prometheus-stack \
     -n monitoring --create-namespace
   # Note the Helm release name — it sets the `release:` label the
   # ServiceMonitor/PrometheusRule must match.
   ```
   Optional tracing backend: `helm install tempo grafana/tempo -n observability`.

## Apply order (full stack)

```bash
# 1) App: namespace + config FIRST (creates the url-shortener namespace),
#    then workload, Service, autoscaler.
kubectl apply -f spring-boot-3-deployment/configmap.yaml
kubectl apply -f spring-boot-3-deployment/deployment.yaml \
              -f spring-boot-3-deployment/service.yaml \
              -f spring-boot-3-deployment/hpa.yaml

# 2) Istio traffic management (requires Istio installed).
kubectl apply -f istio-canary/destinationrule.yaml
kubectl apply -f istio-canary/virtualservice.yaml
kubectl apply -f istio-circuit-breaker/destinationrule.yaml
# Fault injection is OPTIONAL / test-only:
# kubectl apply -f istio-circuit-breaker/virtualservice.yaml

# 3) Observability (requires the prometheus-operator CRDs).
kubectl apply -f observability-stack/otel-collector-configmap.yaml
kubectl apply -f observability-stack/otel-collector-deployment.yaml
kubectl apply -f observability-stack/servicemonitor.yaml
kubectl apply -f observability-stack/prometheusrule.yaml

# Verify.
kubectl -n url-shortener get deploy,svc,hpa,pods
kubectl -n url-shortener get virtualservice,destinationrule
```

Each subdirectory's README has its own scoped apply/verify commands.

## Image references — substitute before applying

All workloads use **placeholder** images. Replace before you deploy:

- App: `registry.example.com/url-shortener@sha256:0000...` in
  `spring-boot-3-deployment/deployment.yaml` (and a `v2` image for the canary).
  Get the digest with `crane digest registry.example.com/url-shortener:1.0.0`.
- Tracing backend endpoint: `tempo.observability...:4317` in
  `observability-stack/otel-collector-configmap.yaml`.
- Prometheus `release:` label in the two `monitoring.coreos.com/v1` manifests.

> **Secrets are out of scope here.** The ConfigMap holds non-secret config only.
> Put DB passwords / keys in a `Secret` (or `ExternalSecret`) and reference them
> via `secretKeyRef` — never in a ConfigMap.

## Validation status

YAML well-formedness was verified by parsing every file with PyYAML (all
multi-document files load cleanly). No live cluster, `kubectl`, `kubeval`, or
`kubeconform` was available in the authoring environment, so **schema** validation
against the Kubernetes/Istio/prometheus-operator OpenAPI was not run. Before
applying, validate against your cluster:

```bash
kubectl apply --dry-run=server -f <dir>/        # server-side schema + admission
kubeconform -strict -summary <dir>/*.yaml       # offline schema (Istio/CRDs need -schema-location)
```
