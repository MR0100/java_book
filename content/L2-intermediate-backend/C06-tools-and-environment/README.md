---
title: "Intermediate Java & Backend Foundations — Tools & Environment"
slug: l2-tools-and-environment
level: L2
module: "Intermediate Java & Backend Foundations"
section: "Tools & Environment"
type: index
difficulty: intermediate
order: 6
tags: [section-index, tools, toolchain, curl, httpie, postman, psql, dbeaver, flyway, dig, tcpdump, openssl, docker, testcontainers, jq]
prerequisites: []
status: complete
estimated_minutes: 3
last_updated: 2026-06-05
---

# Intermediate Java & Backend Foundations — Tools & Environment

The **concept chapters** of L2 teach the *mechanism* — how build tools resolve a dependency graph (C02), how TCP/TLS/HTTP carry bytes (C03), how REST shapes an API (C04), how a query planner walks a B-tree (C05). This chapter teaches the **tools you actually run** to work with that machinery day to day: the HTTP clients you poke an endpoint with, the database clients you inspect a table with, the packet- and TLS-level diagnostics you reach for when the network misbehaves, and the containers you spin a real Postgres up in for an integration test.

These are **reference** topics, not concept topics — they don't count toward the 371 concept total. But they hold the **same depth bar**: real commands, the mechanism humming under each one, edge cases, and troubleshooting recipes — not a shallow "install X, run Y" sampler.

> [!NOTE]
> **Tier:** Junior to Mid
> **Prerequisites:** L2 concept chapters — [C02 Build Tools](../C02-build-tools-and-workflow/), [C03 Networking](../C03-networking-fundamentals/), [C04 Web & REST](../C04-web-and-rest-basics/), [C05 Databases & SQL](../C05-databases-and-sql/)

## Topics

| # | Topic | File | Maps to | Status |
|---|-------|------|---------|--------|
| 01 | Backend toolchain quick reference | [`T01-backend-toolchain-quick-reference.md`](./T01-backend-toolchain-quick-reference.md) | all of L2 | **complete** |
| 02 | HTTP & API clients (curl, HTTPie, Postman, DevTools) | [`T02-http-and-api-clients.md`](./T02-http-and-api-clients.md) | C03/C04 | **complete** |
| 03 | Database clients & migration tools (psql/mysql, DBeaver, Flyway/Liquibase) | [`T03-database-clients-and-migration-tools.md`](./T03-database-clients-and-migration-tools.md) | C05 | **complete** |
| 04 | Network & TLS diagnostics (dig, ss, tcpdump, openssl) | [`T04-network-and-tls-diagnostics.md`](./T04-network-and-tls-diagnostics.md) | C03 | **complete** |
| 05 | Local dev environment: Docker & Testcontainers | [`T05-local-dev-environment-docker-testcontainers.md`](./T05-local-dev-environment-docker-testcontainers.md) | all of L2 | **complete** |

## How this chapter relates to the rest of L2

```mermaid
flowchart LR
  subgraph Concepts["L2 concept chapters — the mechanism"]
    C02["C02 Build tools"]
    C03["C03 Networking"]
    C04["C04 Web & REST"]
    C05["C05 Databases & SQL"]
  end
  subgraph Tools["C06 Tools & Environment — what you run"]
    T01["T01 Toolchain map"]
    T02["T02 HTTP/API clients"]
    T03["T03 DB clients + migrations"]
    T04["T04 Network/TLS diagnostics"]
    T05["T05 Docker + Testcontainers"]
  end
  C03 --> T02
  C04 --> T02
  C05 --> T03
  C03 --> T04
  C02 --> T05
  C05 --> T05
  T01 -.-> T02 & T03 & T04 & T05
```

[Back to L2 index](../README.md) · [Master curriculum](../../../CURRICULUM.md)
