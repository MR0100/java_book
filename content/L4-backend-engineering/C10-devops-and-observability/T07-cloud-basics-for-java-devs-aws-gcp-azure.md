---
title: "Cloud basics for Java devs (AWS/GCP/Azure)"
slug: cloud-basics-for-java-devs-aws-gcp-azure
level: L4
module: "Backend Engineering"
section: "DevOps, Cloud & Observability"
type: concept
difficulty: senior
order: 7
tags: [cloud, aws, gcp, azure, ec2, eks, lambda, rds, s3, cloudwatch, gke, azure-aks, iam, vpc, managed-services, serverless, compute, storage, networking, java-cloud]
prerequisites: []
status: complete
estimated_minutes: 50
last_updated: 2026-06-08
---

# Cloud basics for Java devs (AWS/GCP/Azure)

The "cloud" has been the dominant deployment target for Java backends since roughly 2015. AWS, GCP, and Azure together hold ~65% of cloud market share; a senior Java engineer in 2026 should be conversant in at least one (typically AWS) and familiar with the categories of services across all three. You don't need cloud-architect expertise — that's a separate role — but you do need to know what each major service does, what it costs, and what trade-offs it implies for your Java code.

This topic surveys the cloud landscape from a Java developer's perspective. Equivalent services across AWS, GCP, and Azure for compute, storage, networking, databases, messaging, observability, and identity. The senior point: cloud services aren't interchangeable; specific choices have specific operational implications.

> [!NOTE]
> Prerequisites: basic networking concepts (IP, DNS). [Docker](./T01-docker-and-containerization-for-java.md) helps for context.

## Why The Cloud Won

The cloud era started with **AWS EC2 in 2006**. Before then, deploying Java apps meant buying servers, racking them in data centers, paying for power, network, and ops staff. AWS made compute *rentable* by the hour with a credit card. The economic shift was profound:

- **Capital expense → operating expense**: no upfront server purchases.
- **Elastic capacity**: scale up for traffic spikes; scale down at night.
- **Global reach**: deploy to multiple regions without owning data centers.
- **Managed services**: databases, queues, ML, etc. — no ops needed.

By 2015, Netflix had completed migrating to AWS. By 2020, the cloud was the *default* for new projects. By 2024, most enterprise Java workloads run in the cloud.

The market: AWS (~32%), Azure (~23%), GCP (~10%), others (~35% combined). For new Java projects in 2024, AWS is most common, Azure is strong in enterprise (Microsoft shops), GCP is strong in data/ML.

## The Service Categories

Each cloud has dozens of services. The categories that matter for most Java devs:

| Category | Purpose |
|----------|---------|
| **Compute** | Run code (VMs, containers, functions) |
| **Storage** | Store files, objects, blocks |
| **Networking** | Connect things, route traffic |
| **Databases** | Managed SQL and NoSQL |
| **Messaging** | Queues, pub-sub |
| **Identity** | Authentication, authorization |
| **Observability** | Logs, metrics, traces |
| **CI/CD** | Build and deploy pipelines |
| **Edge/CDN** | Content delivery |

Each cloud has equivalents in each category. The names differ.

## The Big Three — Compute

### AWS

- **EC2**: virtual machines. The fundamental unit.
- **ECS**: container service (AWS-native).
- **EKS**: managed Kubernetes.
- **Fargate**: serverless containers (no EC2 to manage).
- **Lambda**: serverless functions.
- **Beanstalk**: PaaS for Java apps (legacy).

For Java services in 2024:
- **EKS + Fargate** for new Kubernetes deployments.
- **ECS + Fargate** for simpler container needs.
- **Lambda** for event-driven, short-lived functions.
- **EC2** for legacy or specific performance needs.

### GCP

- **Compute Engine**: VMs.
- **GKE**: managed Kubernetes (Google invented K8s).
- **Cloud Run**: serverless containers (Knative-based).
- **App Engine**: PaaS (oldest GCP service).
- **Cloud Functions**: serverless functions.

GKE is widely considered the best managed K8s (Google's heritage).

### Azure

- **Virtual Machines**: VMs.
- **AKS**: managed Kubernetes.
- **Container Apps**: serverless containers.
- **App Service**: PaaS.
- **Functions**: serverless functions.

Azure's strength: tight integration with Microsoft ecosystem (Active Directory, Visual Studio, .NET, but Java is well-supported too).

## Storage

### AWS

- **S3**: object storage. Foundation of AWS. Stores virtually anything.
- **EBS**: block storage (attached to EC2).
- **EFS**: network file system.
- **Glacier**: archival storage.

S3 is *the* object store. Java SDK has `software.amazon.awssdk:s3` for access.

### GCP

- **Cloud Storage**: object storage (equivalent to S3).
- **Persistent Disk**: block storage.
- **Filestore**: NFS.

### Azure

- **Blob Storage**: object storage.
- **Managed Disks**: block storage.
- **Files**: SMB/NFS.

For Java apps, you'll work with object storage (uploads, backups, logs) and block storage (databases, persistent volumes).

## Networking

### AWS

- **VPC**: virtual private cloud. Network isolation.
- **Subnets**: divisions of VPC.
- **Route 53**: DNS.
- **CloudFront**: CDN.
- **ALB**: application load balancer (L7).
- **NLB**: network load balancer (L4).
- **API Gateway**: API management.
- **Direct Connect**: dedicated network connection.

VPC is foundational. Every AWS resource lives in a VPC.

### GCP

- **VPC**: virtual private cloud.
- **Cloud DNS**: DNS.
- **Cloud CDN**: CDN.
- **Cloud Load Balancing**: load balancers.
- **API Gateway**: API management.

GCP's networking is famously good; global VPCs (vs AWS's regional).

### Azure

- **Virtual Network**: VPC equivalent.
- **DNS Zones**: DNS.
- **Front Door**: CDN + WAF.
- **Load Balancer / Application Gateway**: load balancers.

## Databases

### AWS

- **RDS**: managed relational. PostgreSQL, MySQL, MariaDB, Oracle, SQL Server, Aurora.
- **Aurora**: AWS's cloud-native database (PostgreSQL/MySQL compatible).
- **DynamoDB**: managed NoSQL key-value. Massive scale.
- **DocumentDB**: managed MongoDB-compatible.
- **ElastiCache**: managed Redis/Memcached.
- **Redshift**: data warehouse.
- **Neptune**: graph database.

### GCP

- **Cloud SQL**: managed PostgreSQL/MySQL/SQL Server.
- **Spanner**: globally distributed, strongly consistent SQL.
- **Bigtable**: managed Bigtable (NoSQL).
- **Firestore**: document database.
- **BigQuery**: data warehouse.

Spanner is unique — globally distributed strong consistency.

### Azure

- **Azure SQL Database**: managed SQL Server.
- **Cosmos DB**: multi-model database (document, key-value, graph, column-family).
- **Azure Database for PostgreSQL/MySQL**: managed Postgres/MySQL.
- **Synapse Analytics**: data warehouse.

For Java apps, RDS/Cloud SQL/Azure SQL Database for relational; DynamoDB/Bigtable/Cosmos DB for NoSQL.

## Messaging

### AWS

- **SQS**: managed queue.
- **SNS**: pub/sub.
- **MSK**: managed Kafka.
- **EventBridge**: event bus.
- **Kinesis**: data streams (Kafka alternative).

### GCP

- **Pub/Sub**: pub/sub messaging.
- **Cloud Tasks**: deferred execution.
- **Dataflow**: stream processing.

### Azure

- **Service Bus**: message queue.
- **Event Grid**: event routing.
- **Event Hubs**: Kafka-equivalent.

## Identity

### AWS

- **IAM**: identity and access management. Roles, policies.
- **Cognito**: user pools for application authentication.
- **STS**: temporary credentials.

IAM is critical. Every resource access goes through IAM.

### GCP

- **Cloud IAM**: identity.
- **Identity Platform**: user authentication.

### Azure

- **Entra ID** (formerly Azure AD): identity.
- **AD B2C**: customer identity.

## Observability

### AWS

- **CloudWatch**: metrics, logs, alarms.
- **X-Ray**: distributed tracing.
- **CloudTrail**: audit logs.

### GCP

- **Cloud Logging**: logs.
- **Cloud Monitoring**: metrics.
- **Cloud Trace**: tracing.

### Azure

- **Azure Monitor**: unified observability.
- **Application Insights**: APM.

These cover further in T11-T13. The cloud-native observability is increasingly replaced by third parties (Datadog, New Relic) for cross-cloud consistency.

## Java SDK Across Clouds

Each cloud provides Java SDKs:

```xml
<!-- AWS -->
<dependency>
  <groupId>software.amazon.awssdk</groupId>
  <artifactId>s3</artifactId>
</dependency>

<!-- GCP -->
<dependency>
  <groupId>com.google.cloud</groupId>
  <artifactId>google-cloud-storage</artifactId>
</dependency>

<!-- Azure -->
<dependency>
  <groupId>com.azure</groupId>
  <artifactId>azure-storage-blob</artifactId>
</dependency>
```

Spring Boot has starters for each cloud:
- **Spring Cloud AWS**.
- **Spring Cloud GCP**.
- **Spring Cloud Azure**.

These abstract some differences but each cloud's SDK remains the canonical interface.

## The Lock-In Question

Using cloud-specific services creates lock-in:

| Service | Lock-In Level |
|---------|---------------|
| EC2/Compute Engine/VMs | Low (VMs are portable) |
| EKS/GKE/AKS | Low (Kubernetes is portable) |
| S3/Cloud Storage/Blob | Medium (S3 API common) |
| RDS (Postgres) | Low (Postgres works anywhere) |
| Aurora | High (AWS-specific) |
| Lambda/Functions | High (function code portable, deployment isn't) |
| Spanner | Very high (no equivalent elsewhere) |
| DynamoDB | High (AWS-specific) |

The pragmatic answer:
- **Use the best service for the job**.
- **Document the lock-in**.
- **Don't engineer for portability** unless it's a specific requirement.

The "multi-cloud" goal is often theoretical. Most companies pick one cloud and accept the lock-in.

## Cost Considerations

Cloud costs add up. Major drivers:

- **Compute**: EC2, EKS instances. The biggest line item usually.
- **Egress**: data transfer out is expensive ($0.09/GB on AWS).
- **NAT gateway**: $0.045/hour + $0.045/GB. Surprising at scale.
- **Logs**: CloudWatch logs charged per GB ingested.
- **Snapshots**: EBS snapshots accumulate.
- **Idle resources**: forgotten dev environments.

The senior practice: monitor costs continuously. Use cost allocation tags. Right-size regularly.

## Choosing A Cloud

Factors:

1. **Existing investment**: hardest to change. If your company is on AWS, use AWS.
2. **Specific service needs**: Spanner for global SQL → GCP. Strong AD → Azure.
3. **Talent availability**: AWS-skilled engineers are most plentiful.
4. **Compliance**: Azure is strong in regulated industries (HIPAA, FedRAMP).
5. **Cost**: vary by workload; price calculators help.

For new projects with no existing investment: AWS is the safe default.

## Anti-Patterns

> [!WARNING]
> **No cost monitoring.** Bills surprise teams. Set up budgets and alerts.

> [!WARNING]
> **Ignoring data transfer costs.** Cross-region traffic is expensive.

> [!WARNING]
> **Lifting and shifting without re-architecting.** VM-on-cloud is more expensive than properly cloud-native architectures.

> [!WARNING]
> **Hardcoded cloud-specific values.** Move to config.

> [!WARNING]
> **No backup/DR plan.** "AWS is reliable" isn't a strategy.

> [!WARNING]
> **Overly broad IAM permissions.** Grant least privilege.

> [!WARNING]
> **Using root account.** Create IAM users; never log in as root.

> [!WARNING]
> **Secrets in environment variables.** Use secrets manager.

## Common Misconceptions

> [!WARNING]
> **"Cloud is cheaper than on-prem."** Sometimes; depends on workload. Steady-state compute can be expensive vs owned hardware.

> [!WARNING]
> **"Cloud is more reliable than on-prem."** Generally yes, but not magical. Multi-AZ, multi-region for real reliability.

> [!WARNING]
> **"Multi-cloud avoids lock-in."** Sometimes adds it (operational complexity).

> [!WARNING]
> **"Serverless is always cheaper."** For high-traffic workloads, EC2/containers are often cheaper than Lambda.

## Practice

1. **AWS account**: create a free-tier account. Launch a t2.micro EC2 instance. Stop it.
2. **S3 from Java**: write a Java program that uploads/downloads from S3.
3. **RDS database**: create a managed Postgres instance. Connect from Java.
4. **EKS cluster**: create a small EKS cluster. Deploy a Spring Boot pod.
5. **Lambda function**: write a Java Lambda. Trigger via API Gateway.
6. **Cost calculator**: estimate cost for your typical service (compute, storage, network).
7. **IAM policy**: create least-privilege IAM policy for your service.
8. **CloudWatch logs**: log from your app to CloudWatch. View logs.

## Recap

You should now be able to:

- Compare AWS, GCP, Azure at a high level.
- Identify equivalent services across clouds (compute, storage, database, etc.).
- Choose appropriate cloud services for typical Java workloads.
- Use cloud-specific Java SDKs.
- Understand the lock-in trade-offs of cloud-specific services.
- Monitor and control cloud costs.
- Avoid common cloud anti-patterns.

## Next

Continue to [Infrastructure as Code (Terraform, intro)](./T08-infrastructure-as-code-terraform-intro.md) — the practice of defining cloud infrastructure as version-controlled code instead of clicking through GUIs.
