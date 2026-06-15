# `observability-stack/` — Metrics + tracing for the url-shortener

Wires the app into a metrics-and-tracing pipeline: Prometheus scrapes its
Micrometer metrics, alert rules watch the golden signals, and an OpenTelemetry
Collector fans traces out to a tracing backend.

Backing topics: **L4/C10** (observability) and **lab-06** (distributed tracing).

## Files

| File | Kind / API | Demonstrates |
|------|------------|--------------|
| `servicemonitor.yaml` | `ServiceMonitor` · `monitoring.coreos.com/v1` | Declarative Prometheus scrape of `/actuator/prometheus`. |
| `prometheusrule.yaml` | `PrometheusRule` · `monitoring.coreos.com/v1` | Golden-signal alerts (errors, p95 latency, heap saturation, target-down). |
| `otel-collector-configmap.yaml` | `ConfigMap` · `v1` | The Collector pipeline: OTLP receivers → batch/limit processors → Tempo/Jaeger exporters. |
| `otel-collector-deployment.yaml` | `Namespace`+`SA`+`Deployment`+`Service` · `v1`/`apps/v1` | Runs the Collector as a hardened gateway with a stable OTLP endpoint. |

## The two pillars here

### Metrics (Prometheus + Micrometer)

Spring Boot + `micrometer-registry-prometheus` exposes `/actuator/prometheus`.
The `ServiceMonitor` tells the prometheus-operator to scrape it — no
`prometheus.yml` edits, no scrape annotations. **Watch out:** the
`ServiceMonitor`/`PrometheusRule` `release:` label must match your Prometheus's
`serviceMonitorSelector`/`ruleSelector`, or they are silently ignored (the #1
"why isn't it scraping?" gotcha — see the inline comment).

### Tracing (OpenTelemetry Collector → Tempo/Jaeger)

```
url-shortener (OTel Java agent / Micrometer Tracing, OTLP)
        │  OTEL_EXPORTER_OTLP_ENDPOINT=http://otel-collector.observability:4317
        ▼
OTel Collector  ──(otlp)──►  Grafana Tempo   (or Jaeger)
        │
        └─► Grafana queries Tempo for traces; correlates with Prometheus
            metrics and Loki logs via shared trace/span IDs.
```

To emit traces from the app, run it with the OpenTelemetry Java agent:

```
-javaagent:/otel/opentelemetry-javaagent.jar
OTEL_SERVICE_NAME=url-shortener
OTEL_EXPORTER_OTLP_ENDPOINT=http://otel-collector.observability.svc.cluster.local:4317
OTEL_TRACES_EXPORTER=otlp
```

## Grafana / Tempo / Jaeger

- **Grafana** — the single pane of glass: dashboards over Prometheus (metrics)
  and Tempo (traces), with exemplars linking a latency spike straight to the
  trace that caused it.
- **Tempo** — Grafana's trace store; OTLP-native, cheap (object storage), pairs
  naturally with the Collector's `otlp/tempo` exporter (the default here).
- **Jaeger** — the classic alternative; also speaks OTLP (≥ v1.35). Switch by
  uncommenting the `otlp/jaeger` exporter in the ConfigMap and adding it to the
  traces pipeline.

`kube-prometheus-stack` bundles Prometheus, Alertmanager, and Grafana; add Tempo
via the `grafana/tempo` (or `tempo-distributed`) Helm chart.

## Apply

```bash
# Collector first (creates the `observability` namespace).
kubectl apply -f otel-collector-configmap.yaml
kubectl apply -f otel-collector-deployment.yaml

# Metrics scrape + alerts (require the prometheus-operator CRDs to exist).
kubectl apply -f servicemonitor.yaml
kubectl apply -f prometheusrule.yaml

# Confirm Prometheus discovered the target:
#   Prometheus UI → Status → Targets → expect job="url-shortener" UP.
kubectl -n observability rollout status deploy/otel-collector
```

## Prerequisites & substitutions

- **prometheus-operator CRDs** (`ServiceMonitor`, `PrometheusRule`) installed —
  e.g. via `kube-prometheus-stack`. Without them the two monitoring manifests
  won't even validate against the API server.
- Match the `release:` label to YOUR Prometheus release name.
- Substitute the **Tempo/Jaeger endpoint** in `otel-collector-configmap.yaml`.
- Pin the Collector image to a **digest** in prod (the manifest pins a version
  tag for readability).
- **Scraping inside a strict-mTLS mesh:** if PeerAuthentication is STRICT,
  either keep the Collector namespace out of the mesh (done here) and scrape the
  app's merged sidecar metrics port (`15020`), or give Prometheus the Istio
  certs. Plain `scheme: http` to `8080` works only when the app port is excluded
  from mTLS.
