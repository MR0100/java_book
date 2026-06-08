---
title: "Configuration and secrets management"
slug: configuration-and-secrets-management
level: L4
module: "Backend Engineering"
section: "DevOps, Cloud & Observability"
type: concept
difficulty: senior
order: 9
tags: [configuration, secrets, 12-factor, spring-config, vault, aws-secrets-manager, sealed-secrets, external-secrets, environment-variables, configmaps, encryption-at-rest, kms]
prerequisites: [kubernetes-basics]
status: complete
estimated_minutes: 45
last_updated: 2026-06-08
---

# Configuration and secrets management

Application configuration and secrets are different things with different lifecycles, but the boundary blurs in practice. Configuration is "what differs across environments" — database URLs, feature flags, log levels. Secrets are configuration that's *also* sensitive — database passwords, API keys, TLS certs, JWT signing keys. Both must be loadable per environment without code changes (the "12-Factor" principle from Heroku, 2011), but secrets need additional protection at rest, in transit, and in process memory.

This topic covers the canonical patterns for both: Spring Boot's `application.yml`/profiles, Kubernetes ConfigMaps and Secrets, HashiCorp Vault, AWS Secrets Manager, Sealed Secrets, External Secrets Operator, and the operational practices that keep credentials out of git, logs, and disk dumps.

> [!NOTE]
> Prerequisites: [Kubernetes basics (L4/C10/T03)](./T03-kubernetes-basics.md).

## The 12-Factor Principle

From Heroku's "Twelve-Factor App" (2011, by Adam Wiggins):

> **III. Config — Store config in the environment.**
> 
> An app's config is everything that is likely to vary between deploys (staging, production, developer environments). Configuration should be strictly separated from code. The twelve-factor app stores config in environment variables.

Why:
- Config varies per environment; code doesn't.
- Environment variables are language-agnostic.
- Easy to change without rebuild.

The challenge: secrets in env vars can leak (in process listings, dump files, logs). Modern practice has evolved — env vars for non-secret config, dedicated secret stores for secrets.

## Configuration Sources

Spring Boot's config hierarchy (highest priority first):
1. Command-line arguments (`--server.port=8080`).
2. Environment variables (`SERVER_PORT=8080`).
3. Profile-specific `application-{profile}.yml`.
4. `application.yml` in classpath.
5. Default values in code.

Example `application.yml`:
```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/myapp
    username: appuser
    password: ${DB_PASSWORD}      # from env var

logging:
  level:
    com.example: INFO
```

`application-production.yml`:
```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:postgresql://prod-db.internal:5432/myapp
    username: appuser

logging:
  level:
    com.example: WARN
```

Run with: `java -jar app.jar --spring.profiles.active=production`.

## Spring Cloud Config

Centralized config for many services:

```yaml
# config-server application.yml
server:
  port: 8888
spring:
  cloud:
    config:
      server:
        git:
          uri: https://github.com/myorg/config-repo
```

Services point at config-server:
```yaml
# client application.yml
spring:
  config:
    import: optional:configserver:http://config-server:8888
  application:
    name: user-service
```

Config-server fetches `user-service.yml` from git.

Use cases: shared org config, dynamic refresh, audit log of config changes.

## Kubernetes ConfigMaps

For non-secret config:

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: myapp-config
data:
  application.yml: |
    server:
      port: 8080
    logging:
      level:
        com.example: INFO
  feature.flags: |
    NEW_CHECKOUT_ENABLED=true
    LEGACY_API_ENABLED=false
```

Use as files:
```yaml
spec:
  containers:
  - name: myapp
    image: myapp:1.0
    volumeMounts:
    - name: config
      mountPath: /config
  volumes:
  - name: config
    configMap:
      name: myapp-config
```

Or as env vars:
```yaml
spec:
  containers:
  - name: myapp
    envFrom:
    - configMapRef:
        name: myapp-config
```

## Kubernetes Secrets

Same as ConfigMaps but for sensitive data. **Base64-encoded, NOT encrypted by default.**

```yaml
apiVersion: v1
kind: Secret
metadata:
  name: db-credentials
type: Opaque
data:
  username: YXBwdXNlcg==      # base64("appuser")
  password: c2VjcmV0MTIz      # base64("secret123")
```

Use:
```yaml
spec:
  containers:
  - name: myapp
    env:
    - name: DB_PASSWORD
      valueFrom:
        secretKeyRef:
          name: db-credentials
          key: password
```

> [!WARNING]
> **Base64 is encoding, not encryption.** Anyone with read access to Secrets can read the values. Enable etcd encryption at rest (KMS provider) and tightly control RBAC.

## HashiCorp Vault

Vault is the canonical secret management tool. Features:
- Encrypted secret storage.
- Dynamic secrets (e.g., short-lived DB credentials).
- Secret rotation.
- Detailed audit logging.
- Multiple authentication methods.
- PKI: generate certs.

### Vault Concepts

- **Seal/unseal**: Vault must be unsealed (using Shamir-split keys) to operate.
- **Auth methods**: Kubernetes, AWS IAM, GitHub, LDAP, etc.
- **Secret engines**: KV (key-value), database (dynamic creds), PKI, transit (encryption-as-a-service).
- **Policies**: who can read/write what paths.

### Reading Secrets From Java

```java
@Configuration
public class VaultConfig {
    @Value("${vault.token}")
    private String token;
    
    @Bean
    public VaultTemplate vaultTemplate() {
        VaultEndpoint endpoint = VaultEndpoint.create("vault.example.com", 8200);
        return new VaultTemplate(endpoint, new TokenAuthentication(token));
    }
}

@Service
public class CredentialsService {
    @Autowired
    private VaultTemplate vaultTemplate;
    
    public String getDbPassword() {
        VaultResponseSupport<Map<String, Object>> response = 
            vaultTemplate.read("secret/myapp", Map.class);
        return (String) response.getData().get("db_password");
    }
}
```

Or use Spring Cloud Vault — automatic injection:

```yaml
spring:
  cloud:
    vault:
      host: vault.example.com
      port: 8200
      authentication: kubernetes
      kubernetes:
        role: myapp
      kv:
        enabled: true
        backend: secret
        application-name: myapp
```

Now `${secret.db_password}` references Vault.

### Dynamic Database Credentials

Vault generates per-session DB credentials:

```bash
$ vault read database/creds/myapp
Key                Value
---                -----
lease_id           database/creds/myapp/abc123
lease_duration     1h
lease_renewable    true
password           A1a-supersecret-pw
username           v-myapp-1234567890
```

The credentials expire after 1 hour. Vault auto-rotates.

## AWS Secrets Manager / Parameter Store

AWS native alternatives:

- **Secrets Manager**: managed secret storage. Auto-rotation. $0.40/secret/month.
- **Parameter Store**: simpler key-value, free.

Java access:
```java
SecretsManagerClient client = SecretsManagerClient.create();

GetSecretValueResponse response = client.getSecretValue(
    GetSecretValueRequest.builder()
        .secretId("prod/myapp/db")
        .build()
);

JsonNode secret = new ObjectMapper().readTree(response.secretString());
String password = secret.get("password").asText();
```

Spring Cloud AWS provides direct injection.

## Sealed Secrets

Bitnami's Sealed Secrets lets you commit *encrypted* secrets to git:

```bash
# Encrypt
echo -n "secret123" | kubectl create secret generic db-pass \
    --dry-run=client -o yaml --from-file=password=/dev/stdin |
    kubeseal -o yaml > sealed-secret.yaml

# Commit sealed-secret.yaml to git (safe)
git add sealed-secret.yaml
git commit -m "Add encrypted DB password"
```

The Sealed Secrets controller running in K8s decrypts at apply time:

```yaml
apiVersion: bitnami.com/v1alpha1
kind: SealedSecret
metadata:
  name: db-pass
spec:
  encryptedData:
    password: AgB7zJ9KrL8...   # encrypted
```

Only the controller's private key can decrypt. Lose the key → secrets unrecoverable.

## External Secrets Operator

ESO syncs from external secret stores (Vault, AWS Secrets Manager) to K8s Secrets:

```yaml
apiVersion: external-secrets.io/v1beta1
kind: ExternalSecret
metadata:
  name: db-credentials
spec:
  refreshInterval: 1h
  secretStoreRef:
    name: aws-secrets-manager
    kind: SecretStore
  target:
    name: db-credentials
  data:
  - secretKey: password
    remoteRef:
      key: prod/myapp/db
      property: password
```

ESO reads from AWS Secrets Manager every hour, populates the K8s Secret.

Many teams prefer ESO + Secrets Manager over Sealed Secrets:
- No need to manage encryption keys.
- Secrets exist in a real secret store with audit logs.
- Rotation is automatic.

## Secret Rotation

Best practice: rotate secrets regularly (every 90 days minimum, more often for sensitive). Methods:

- **Manual rotation**: create new secret, deploy new version, delete old.
- **Auto-rotation**: Secrets Manager rotates and updates apps via callbacks.
- **Vault dynamic credentials**: built-in rotation per session.

The challenge: apps must handle rotation gracefully (reconnect with new credentials, no downtime).

## Configuration Hot-Reload

Sometimes you want config changes to take effect without restart:

- **Spring Cloud Config + Spring Cloud Bus**: refresh endpoint reloads beans.
- **Kubernetes ConfigMap mount**: file changes propagate; app must watch files.
- **Feature flags** (next topic): designed for runtime change.

But: not all changes can hot-reload safely. Database connection strings? Probably need restart. Log levels? Easy hot-reload.

## Encryption At Rest

- **etcd encryption**: K8s Secrets in etcd are encrypted using a KMS provider.
- **EBS encryption**: enable on all EBS volumes.
- **RDS encryption**: enable at creation (can't change later).
- **S3 encryption**: SSE-S3 or SSE-KMS.

Most clouds make this trivial. *Use them*.

## Encryption In Transit

- **TLS everywhere**: between services, to databases, to caches.
- **mTLS for service-to-service**: cert-based identity.
- **Service mesh (Istio, Linkerd)**: automatic mTLS.

## Java Secret Handling

In code:

```java
// BAD: secret in source
String password = "supersecret123";

// BAD: secret in log
log.info("Connecting with password: {}", password);

// BAD: secret in toString
public String toString() {
    return "DatabaseConfig{password=" + password + "}";
}

// GOOD: load from env
String password = System.getenv("DB_PASSWORD");

// GOOD: use char[] for sensitive data, zero after use
char[] password = loadPassword();
try {
    authenticate(password);
} finally {
    Arrays.fill(password, '0');  // overwrite
}
```

The `char[]` trick exists because `String` is immutable — once a password is in a `String`, it sits in heap memory until GC; GC is non-deterministic; heap dump exposes it.

## Anti-Patterns

> [!WARNING]
> **Hardcoded secrets.** API keys in source code, even private repos.

> [!WARNING]
> **Secrets in git.** Even encrypted. Commit history is forever.

> [!WARNING]
> **Secrets in CI logs.** Mask outputs.

> [!WARNING]
> **Secrets in image layers.** Docker images are world-readable in registries.

> [!WARNING]
> **Same secret across environments.** Compromised dev key shouldn't compromise prod.

> [!WARNING]
> **No rotation.** Secrets known for years.

> [!WARNING]
> **Shared service accounts.** Per-service identities are auditable.

> [!WARNING]
> **Secrets in environment variables of public processes.** Process listings expose them.

> [!WARNING]
> **No audit log.** Who accessed which secret when?

## Common Misconceptions

> [!WARNING]
> **"Kubernetes Secrets are encrypted."** Not by default. Enable etcd encryption.

> [!WARNING]
> **"Base64 is secure."** It's encoding, not encryption.

> [!WARNING]
> **"Private git repos protect secrets."** Anyone with read access reads forever.

> [!WARNING]
> **"Environment variables are safe."** Process listings, dumps, child processes expose them.

> [!WARNING]
> **"One secret manager fits all."** Different tools for different scales — Parameter Store vs Secrets Manager vs Vault.

## Practice

1. **Spring profiles**: configure dev/staging/prod profiles for a Spring Boot app.
2. **ConfigMap + Secret**: deploy a Spring Boot app to K8s with both.
3. **Vault**: install Vault dev mode. Store/retrieve a secret.
4. **Sealed Secrets**: install Sealed Secrets controller. Encrypt and commit a secret.
5. **External Secrets**: set up ESO with AWS Secrets Manager.
6. **Secret rotation**: rotate a DB password without downtime.
7. **etcd encryption**: enable KMS-based encryption for K8s Secrets.
8. **Audit**: scan your codebase for hardcoded secrets (use `git-secrets` or `trufflehog`).

## Recap

You should now be able to:

- Apply the 12-Factor config principles.
- Use Spring Boot profiles and `application.yml`.
- Manage K8s ConfigMaps and Secrets.
- Use Vault for centralized secret management.
- Use Sealed Secrets or External Secrets Operator.
- Rotate secrets and handle rotation in apps.
- Encrypt secrets at rest and in transit.
- Avoid common secret-management anti-patterns.

## Next

Continue to [Feature flags](./T10-feature-flags.md) — the technique for separating deploy from release and enabling progressive rollout of new behavior.
