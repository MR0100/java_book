---
title: "Dockerfile best practices for Java apps"
slug: dockerfile-best-practices-for-java-apps
level: L4
module: "Backend Engineering"
section: "DevOps, Cloud & Observability"
type: concept
difficulty: senior
order: 2
tags: [dockerfile, best-practices, multi-stage-build, layer-optimization, java, spring-boot, security, non-root-user, healthcheck, signal-handling, init-system, tini, dumb-init, graceful-shutdown, build-cache, dependency-caching, distroless]
prerequisites: [docker-and-containerization-for-java]
status: complete
estimated_minutes: 45
last_updated: 2026-06-08
---

# Dockerfile best practices for Java apps

A well-crafted Dockerfile is the difference between a 50MB image that boots in 2 seconds and a 1.2GB image that takes 30 seconds and fails security scans. For Java applications specifically, there are patterns and anti-patterns that experienced engineers can spot in five lines. This topic catalogs them.

This topic assumes you understand basic Docker (T01). The focus here is on the *specific* patterns for Spring Boot and other Java apps: multi-stage builds, layer optimization, signal handling (the JVM SIGTERM trap), security hardening, build caching, and the famous "PID 1 problem."

> [!NOTE]
> Prerequisites: [Docker basics (L4/C10/T01)](./T01-docker-and-containerization-for-java.md).

## The Canonical Production Dockerfile

A production-quality Spring Boot Dockerfile:

```dockerfile
# syntax=docker/dockerfile:1.7

# Stage 1: Build
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /workspace
COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN --mount=type=cache,target=/root/.m2 ./mvnw dependency:go-offline -B
COPY src src
RUN --mount=type=cache,target=/root/.m2 ./mvnw -DskipTests package -B

# Extract layers
RUN java -Djarmode=layertools -jar target/*.jar extract

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-jammy

# Create non-root user
RUN useradd -m -u 1001 -s /bin/false appuser

# Add init system for proper signal handling
RUN apt-get update && apt-get install -y --no-install-recommends tini && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

WORKDIR /app
USER appuser

# Copy layers in order of change frequency (least to most)
COPY --from=builder --chown=appuser:appuser /workspace/dependencies/ ./
COPY --from=builder --chown=appuser:appuser /workspace/spring-boot-loader/ ./
COPY --from=builder --chown=appuser:appuser /workspace/snapshot-dependencies/ ./
COPY --from=builder --chown=appuser:appuser /workspace/application/ ./

EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=3s --start-period=30s --retries=3 \
    CMD wget -qO- http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["/usr/bin/tini", "--", "java", \
    "-XX:MaxRAMPercentage=75", \
    "-XX:+UseG1GC", \
    "-XX:+ExitOnOutOfMemoryError", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "org.springframework.boot.loader.launch.JarLauncher"]
```

This Dockerfile demonstrates 10+ best practices. Let's unpack each.

## Practice 1: Multi-Stage Builds

The Dockerfile above has two stages: `builder` (with JDK and Maven cache) and runtime (just JRE).

**Why multi-stage**:
- Build tools (Maven, Gradle, JDK with compiler) are ~150MB.
- They're not needed at runtime.
- Single-stage build → 500MB image; multi-stage → 250MB image.

Multi-stage builds are *the* single most impactful optimization for Java images.

## Practice 2: Layer Order For Cache Reuse

Docker caches layers. If a layer changes, all layers after it are invalidated.

**Bad order** (rebuilds dependencies every time):
```dockerfile
COPY . .                   # any change invalidates everything below
RUN mvn package
```

**Good order** (caches dependencies):
```dockerfile
COPY pom.xml ./
RUN mvn dependency:go-offline -B    # cached unless pom.xml changes
COPY src src
RUN mvn package
```

The Spring Boot layered JAR pattern takes this further at the runtime layer, separating dependencies (rarely change) from application classes (change every build).

## Practice 3: BuildKit Cache Mounts

The `--mount=type=cache` directive (BuildKit only) persists cache across builds without baking it into the image:

```dockerfile
RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw dependency:go-offline -B
```

Result: Maven downloads dependencies once; subsequent builds reuse the cache. Crucial for CI/CD where each build runs in a fresh container.

Enable BuildKit:
```bash
DOCKER_BUILDKIT=1 docker build .
# Or set in Docker Desktop: enabled by default in modern versions
```

## Practice 4: Non-Root User

Default container behavior: processes run as root (UID 0). If an attacker compromises the application, they have root inside the container.

**The fix**:

```dockerfile
RUN useradd -m -u 1001 -s /bin/false appuser
USER appuser
```

Specific notes:
- **Use a non-root UID** (1001 is conventional).
- **Use `/bin/false`** to prevent interactive shell.
- **`--chown=appuser:appuser`** on COPY statements so files are owned by appuser.
- **EXPOSE ports > 1024**: non-root can't bind to ports < 1024.

## Practice 5: Signal Handling (PID 1 And SIGTERM)

When Docker stops a container, it sends SIGTERM to PID 1. **PID 1 has special semantics in Linux**:
- It cannot be killed by default signals (it ignores SIGTERM unless it handles it).
- It must reap zombie children.
- The Java process as PID 1 may not behave correctly.

**Two problems**:

1. **JVM SIGTERM handling**: the JVM does handle SIGTERM but the shutdown hooks may not run if the kernel sees PID 1 as unkillable.
2. **Zombie reaping**: if your Java app forks child processes (rare but possible), zombies accumulate.

**The fix**: use an init system as PID 1. `tini` is the most common choice:

```dockerfile
RUN apt-get install -y tini
ENTRYPOINT ["/usr/bin/tini", "--", "java", "-jar", "/app/app.jar"]
```

`tini` is a minimal init that:
- Reaps zombie processes.
- Forwards SIGTERM to the Java process correctly.
- Adds ~10KB to image size.

Alternative: `dumb-init` (Yelp). Same purpose, slightly different feature set.

Without `tini`, your `@PreDestroy` Spring beans may not run during shutdown. Critical for graceful shutdown (closing DB connections, flushing logs).

## Practice 6: HEALTHCHECK Directive

The `HEALTHCHECK` instruction tells Docker how to verify container health:

```dockerfile
HEALTHCHECK --interval=30s --timeout=3s --start-period=30s --retries=3 \
    CMD wget -qO- http://localhost:8080/actuator/health || exit 1
```

Parameters:
- `--interval=30s`: check every 30 seconds.
- `--timeout=3s`: each check times out at 3 seconds.
- `--start-period=30s`: give the container 30 seconds to start before failures count.
- `--retries=3`: 3 consecutive failures → unhealthy.

For Spring Boot, hit `/actuator/health`. Most orchestrators (Kubernetes) ignore `HEALTHCHECK` and use their own probes (T14). But it's still useful for `docker run` standalone use and for orchestration that does honor it (Docker Swarm, ECS).

**Specific gotcha**: distroless images don't have `wget` or `curl`. Use Spring Boot's `--probe` flag (Spring Boot 3.2+) or write a Java probe class.

## Practice 7: Memory And GC Tuning

Modern JVM (17+) auto-detects container limits, but explicit flags are clearer:

```dockerfile
ENTRYPOINT ["java", \
    "-XX:MaxRAMPercentage=75", \
    "-XX:InitialRAMPercentage=50", \
    "-XX:+UseG1GC", \
    "-XX:+ExitOnOutOfMemoryError", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", "/app/app.jar"]
```

Each flag:
- `MaxRAMPercentage=75`: heap is 75% of container memory. Leaves 25% for JVM overhead (metaspace, code cache, threads, direct buffers).
- `InitialRAMPercentage=50`: starting heap is 50% of container memory. Reduces resize churn.
- `UseG1GC`: G1 garbage collector (default in JDK 9+, explicit for clarity).
- `ExitOnOutOfMemoryError`: container restarts on OOM; without this, the JVM might continue in a broken state.
- `Djava.security.egd=file:/dev/./urandom`: faster entropy source in containers.

**Important**: don't use `-Xmx` and `-Xms` in containers. They fix sizes; percentages adapt to whatever limit the container has.

## Practice 8: Minimal Base Images

Smaller images:
- Boot faster (less to copy).
- Pull faster (less network).
- Have smaller attack surface (fewer libraries with CVEs).

**Options**:

```dockerfile
# Standard (good default)
FROM eclipse-temurin:21-jre-jammy           # ~250MB

# Smaller via Distroless
FROM gcr.io/distroless/java21-debian12      # ~180MB, no shell

# Smaller via Alpine (use with caution)
FROM eclipse-temurin:21-jre-alpine          # ~180MB, musl libc

# Smallest: JLink custom JRE (advanced)
FROM debian:12-slim                          # ~80MB base
COPY --from=builder /custom-jre /opt/jre    # custom minimal JRE
```

**Distroless** has no shell, no package manager — a security win but harder to debug. To debug, use Distroless's `:debug` tag, which includes BusyBox.

**Alpine** uses musl libc instead of glibc. Most pure-Java code works, but native libraries (Netty epoll, BoringSSL, sigar) may fail or perform differently. Test thoroughly.

## Practice 9: .dockerignore

Critical but often forgotten. Without `.dockerignore`, the entire build context (potentially gigabytes) is sent to the Docker daemon.

```
# .dockerignore
.git
.gitignore
.idea
.vscode
target/
build/
*.iml
*.log
.env
README.md
```

Result: smaller build context, faster builds, no accidental secrets baked into the image.

## Practice 10: Pin Base Image Versions

```dockerfile
# Bad: implicit, shifts unpredictably
FROM eclipse-temurin:latest

# Good: explicit, pinned
FROM eclipse-temurin:21-jre-jammy

# Best: pin by digest for true immutability
FROM eclipse-temurin:21-jre-jammy@sha256:abc123...
```

Digest pinning is reproducible: the same Dockerfile produces the same image forever. Required for compliance, useful for debugging.

## Common Anti-Patterns

> [!WARNING]
> **Single-stage build with JDK in production.** ~150MB wasted.

> [!WARNING]
> **`apt-get update` without `--no-install-recommends`.** Installs unnecessary packages, bloats image.

> [!WARNING]
> **Forgetting `apt-get clean`.** Leaves apt caches in the image.

> [!WARNING]
> **Multiple `RUN` statements when one would do.** Each `RUN` creates a layer:
> ```dockerfile
> # Bad — 3 layers
> RUN apt-get update
> RUN apt-get install -y tini
> RUN apt-get clean
> 
> # Good — 1 layer
> RUN apt-get update && apt-get install -y --no-install-recommends tini && \
>     apt-get clean && rm -rf /var/lib/apt/lists/*
> ```

> [!WARNING]
> **`COPY . .` early.** Invalidates cache on any change.

> [!WARNING]
> **No HEALTHCHECK or readiness probe equivalent.** Orchestrator doesn't know if app is up.

> [!WARNING]
> **Running as root.** Security issue; some hosting platforms (OpenShift) reject root containers by default.

> [!WARNING]
> **No `tini` or init system.** Signal handling broken.

> [!WARNING]
> **Hardcoded secrets in ENV.** Use secrets management (T09).

> [!WARNING]
> **`CMD` instead of `ENTRYPOINT`.** `CMD` can be overridden at runtime; `ENTRYPOINT` is the "what this container does."

## Build Speed Optimizations

For CI/CD speed:

1. **BuildKit cache mounts**: as shown above.
2. **Layered JARs**: Spring Boot 2.3+.
3. **Parallel builds**: `docker buildx` builds multiple platforms simultaneously.
4. **Registry caches**: push intermediate stages to a registry for cross-CI sharing.
5. **Build args for cache-busting only when needed**: ARG values invalidate cache.

Typical Java build times:
- Cold (no cache): 3-5 minutes.
- Warm (dependencies cached): 30-60 seconds.
- Warm (code changes only): 15-30 seconds.

## Security Scanning

Production Dockerfiles need scanning for vulnerabilities:

```bash
# Trivy
trivy image myapp:latest

# Grype (Anchore)
grype myapp:latest

# Docker Scout
docker scout cves myapp:latest

# Snyk
snyk container test myapp:latest
```

These scan for known CVEs in:
- Base image OS packages.
- Java dependencies (via Maven/Gradle integration).
- Application code.

Set up CI to fail builds with critical CVEs.

## Practice

1. **Optimize an existing Dockerfile**: start with a single-stage build. Measure image size. Convert to multi-stage. Compare.
2. **Add tini**: install and configure tini. Send SIGTERM to the container; verify `@PreDestroy` Spring beans run.
3. **Non-root user**: add a non-root user. Verify with `docker exec myapp id`.
4. **Layered JAR**: enable Spring Boot's layered JAR. Build and rebuild after a code change; measure image transfer time.
5. **Distroless conversion**: convert your Dockerfile to use `distroless/java21`. Test that the app runs.
6. **HEALTHCHECK**: add HEALTHCHECK. Verify with `docker inspect myapp | grep -A 10 Health`.
7. **Vulnerability scan**: run Trivy on your image. Address critical/high CVEs.
8. **Build context audit**: check `.dockerignore` excludes correctly.
9. **GC flags**: experiment with different `-XX:MaxRAMPercentage` values. Observe memory usage.
10. **Build time benchmark**: time builds with and without BuildKit cache mounts.

## Recap

You should now be able to:

- Write a production-quality multi-stage Dockerfile for Spring Boot apps.
- Order layers for optimal cache reuse.
- Use BuildKit features (cache mounts) for fast CI builds.
- Configure non-root users for security.
- Solve the PID 1 problem with `tini`.
- Add HEALTHCHECK appropriately.
- Tune JVM memory with container-aware flags (`MaxRAMPercentage`).
- Choose base images (jre-jammy, distroless, alpine).
- Pin base image versions and (optionally) digests.
- Scan images for vulnerabilities.

## Next

Continue to [Kubernetes basics](./T03-kubernetes-basics.md) — the orchestrator that takes your container images and runs them at scale, with self-healing, scaling, and service discovery.
