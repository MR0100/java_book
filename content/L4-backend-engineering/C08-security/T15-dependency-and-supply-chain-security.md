---
title: "Dependency & supply-chain security"
slug: dependency-and-supply-chain-security
level: L4
module: "Backend Engineering"
section: "Security"
type: concept
difficulty: senior
order: 15
tags: [supply-chain, dependency-scanning, sbom, cyclonedx, spdx, sigstore, cosign, slsa, dependency-confusion, typosquatting, log4shell, cve, owasp-dependency-check, snyk, dependabot, renovate, lock-files, reproducible-builds, signed-commits, github-actions-hardening]
prerequisites: [owasp-top-10]
status: complete
estimated_minutes: 40
last_updated: 2026-06-08
---

# Dependency & supply-chain security

A Spring Boot app has 100-200 transitive dependencies; each can have vulnerabilities or be hijacked by a malicious maintainer. The 2020s have demonstrated this risk vividly: **Log4Shell (CVE-2021-44228)** affected every Java service with Log4j 2.x; **xz/liblzma backdoor (2024)** showed a multi-year sophisticated attack; **dependency confusion** trivially hijacks internal packages. **Software supply-chain security** is now mandatory — SBOM, signing, scanning, and reproducibility.

A senior engineer ships supply-chain controls as standard: dependency scanning in CI, SBOM published with every release, signed container images, locked dependency versions, monitored updates.

> [!NOTE]
> Prerequisites: [OWASP Top 10 (T06)](./T06-owasp-top-10.md), Maven/Gradle basics.

## The Threats

| Threat | Example |
|--------|---------|
| **Known CVE in transitive dep** | Log4Shell |
| **Malicious maintainer** | xz/liblzma 2024 |
| **Typosquatting** | "lombook" instead of "lombok" |
| **Dependency confusion** | private package name on public registry |
| **Compromised build infrastructure** | injected at build time |
| **Compromised signing key** | trojanized release |

## Defense Layers

### 1. Dependency Scanning

Tools that compare your deps against CVE databases:

- **OWASP Dependency-Check** (free, open).
- **Snyk** (commercial, mature).
- **GitHub Dependabot** (free for GitHub repos).
- **Renovate** (open-source updater).
- **Trivy** (containers + deps).

```xml
<plugin>
    <groupId>org.owasp</groupId>
    <artifactId>dependency-check-maven</artifactId>
    <version>9.0.7</version>
    <executions>
        <execution>
            <goals><goal>check</goal></goals>
        </execution>
    </executions>
</plugin>
```

Run in CI; fail the build on critical CVEs.

### 2. SBOM (Software Bill of Materials)

A machine-readable list of every dependency:

```bash
mvn org.cyclonedx:cyclonedx-maven-plugin:makeAggregateBom
# produces target/bom.xml or bom.json
```

Formats: **CycloneDX** (OWASP), **SPDX** (Linux Foundation).

SBOM enables:

- Quick "are we vulnerable to CVE-X?" answer.
- Compliance (executive orders mandate SBOMs).
- Customer trust.

Publish SBOM with every release artifact.

### 3. Signed Artifacts

Sign containers + JARs to prove origin:

- **Sigstore / cosign**: keyless signing via OIDC.
- **GPG-signed Maven artifacts**.

```bash
cosign sign --identity-token $(...) ghcr.io/myorg/myapp:1.0.0
```

Consumers verify before deploying:

```bash
cosign verify --certificate-identity ... ghcr.io/myorg/myapp:1.0.0
```

### 4. Lock Dependency Versions

Reproducible builds require deterministic versions.

Maven: explicit versions; avoid `LATEST` or `RELEASE`.

Gradle: use **lock files** (`gradle.lockfile`):

```bash
gradle dependencies --write-locks
```

Locks pin transitives; rebuild produces identical bytes.

### 5. Update Discipline

- **Dependabot / Renovate** open PRs for updates.
- **Weekly minor + patch updates**.
- **Major versions: planned**.
- **Security advisories: immediate** (within 24-72h for critical).

### 6. Use Trusted Sources

- Maven Central for Java deps.
- Verify gradle plugin source.
- Avoid copy-pasting unknown dep coords.
- Use private repository (Nexus, Artifactory) as proxy + cache.

## Dependency Confusion

If your internal package `com.mycompany.utils` is also published publicly with same name, builds may pull public version. Attackers do this intentionally.

Mitigations:

- **Scoped names**: `@mycompany/utils` (npm) or `com.mycompany.internal.utils` (Maven).
- **Configure Maven/Gradle to never pull internal-prefixed packages from public Maven Central**.
- **Private repo first** in resolution chain.

## Log4Shell Lessons

December 2021: a JNDI lookup in log strings allowed RCE on any Java service logging user input.

Lessons:

- Every backend with logging was vulnerable.
- Detection took weeks.
- Patches required full deployment.
- Mitigations needed multiple times.

What worked:

- Teams with SBOM knew exposure in minutes.
- Teams with patch pipelines deployed fixes in hours.
- Teams without — days to weeks.

Build the discipline; you'll thank yourself when next Log4Shell hits.

## CI/CD Hardening

Supply chain extends to your build pipeline.

- **GitHub Actions**: pin action versions to SHA (not tag); minimal permissions.
- **No secrets in build logs**.
- **Code signing**: enforce signed commits.
- **Branch protection**: require reviews.
- **Limit who can push** to release branches.
- **Use ephemeral runners** (no persistent state).

## SLSA (Supply-chain Levels for Software Artifacts)

[slsa.dev](https://slsa.dev/): graduated levels of supply-chain assurance.

- **L1**: build is documented + provenance attestable.
- **L2**: hosted build service + signed provenance.
- **L3**: hardened build + tamper-resistant provenance.
- **L4**: hermetic + reproducible + two-party review.

Aim for L2-L3 for production services.

## Reproducible Builds

Same source → identical bytes:

- Strip timestamps from JAR manifests.
- Pin every dep + plugin version.
- Pin JDK version.
- Use deterministic file ordering.

Maven plugins exist (`reproducible-build-maven-plugin`). Gradle requires care.

Reproducibility enables: detect tampering; reproduce historical builds for forensics.

## Common Pitfalls

> [!WARNING]
> **No dependency scanning.** CVEs land; you're unaware.

> [!WARNING]
> **No SBOM.** Can't answer "are we vulnerable?" quickly.

> [!WARNING]
> **`LATEST` / `+` version ranges.** Non-reproducible; risk.

> [!WARNING]
> **No update cadence.** Versions rot; CVEs accumulate.

> [!WARNING]
> **Trusting all public deps.** Typosquatting / confusion.

> [!WARNING]
> **Unsigned container images.** Tampering undetected.

> [!WARNING]
> **Long-lived secrets in CI.** Compromise = many secrets.

> [!WARNING]
> **GitHub Actions pinned to tag.** Tag can move; attacker controls.

> [!WARNING]
> **No commit signing.** Source provenance unclear.

## Practice

1. Add OWASP Dependency-Check to your build. Fix findings.
2. Generate CycloneDX SBOM; commit alongside release.
3. Sign container image with cosign; verify in deploy step.
4. Audit dep coords for typosquatting potential.
5. Configure Renovate / Dependabot; set merge rules.
6. Pin GitHub Actions to SHA.
7. Enable signed commits requirement on main branch.
8. Tabletop: a critical CVE drops — how fast can you ship a fix?

## Recap

You should now be able to:

- Scan dependencies in CI with OWASP Dependency-Check / Snyk / Dependabot.
- Generate SBOM (CycloneDX / SPDX) per release.
- Sign artifacts with Sigstore/cosign; verify before deploy.
- Lock dependency versions (Maven explicit; Gradle lock files).
- Apply update discipline: weekly minor; immediate security.
- Defend dependency confusion via scoped names + private repo first.
- Harden CI/CD: SHA-pinned actions, minimal secrets, signed commits.
- Aim for SLSA L2-L3.
- Build reproducibly when possible.
- Avoid the canonical pitfalls: no scanning, no SBOM, version ranges, unsigned images, unpinned actions.

## Next

Continue to [Security architecture & zero trust (intro)](./T16-security-architecture-and-zero-trust-intro.md) for the final C08 topic — designing security at the system level.
