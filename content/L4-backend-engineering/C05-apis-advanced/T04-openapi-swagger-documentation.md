---
title: "OpenAPI / Swagger documentation"
slug: openapi-swagger-documentation
level: L4
module: "Backend Engineering"
section: "APIs — Advanced"
type: concept
difficulty: senior
order: 4
tags: [openapi, swagger, openapi-3, openapi-3-1, spec-first, code-first, springdoc-openapi, springfox-deprecated, swagger-ui, redoc, openapi-generator, contract-first, contract-testing, schema-first, api-design, json-schema, components, parameter, request-body, response-schema, security-scheme, openapi-yaml, openapi-json, api-versioning, api-discovery, generated-clients, documentation-as-code]
prerequisites: [richardson-maturity-model-and-hateoas]
status: complete
estimated_minutes: 45
last_updated: 2026-06-08
---

# OpenAPI / Swagger documentation

OpenAPI (formerly Swagger) is the **dominant API description language** in 2026. A YAML or JSON document describes every endpoint, parameter, request/response schema, security scheme, and example. From the spec, you can generate: **interactive documentation** (Swagger UI, Redoc), **client SDKs in 50+ languages** (openapi-generator), **mock servers** (Prism), **contract tests** (Pact, Spring Cloud Contract), **gateway configurations**. OpenAPI 3.0 came out in 2017; 3.1 (2021) aligned with JSON Schema 2020-12. Almost every public REST API ships an OpenAPI spec.

A senior engineer chooses between **code-first** (annotations on Spring controllers; spec generated) and **spec-first** (write the YAML; generate controller interfaces) per project. Code-first wins for fast-evolving internal APIs; spec-first wins for public APIs where the contract is the priority.

This topic covers: the spec structure (paths, components, schemas, security); springdoc-openapi for code-first generation; spec-first with openapi-generator; Swagger UI + Redoc rendering; contract testing integration; versioning strategies; gateway-level uses (Gloo, Kong); the OpenAPI vs HATEOAS trade-off.

> [!NOTE]
> Prerequisites: [HATEOAS (T02)](./T02-richardson-maturity-model-and-hateoas.md), [Spring MVC (L4/C01/T10)](../C01-spring-framework/T10-spring-mvc-rest-controllers.md), basic YAML / JSON Schema.

## The OpenAPI Spec

A minimal spec:

```yaml
openapi: 3.1.0
info:
  title: Orders API
  version: 1.0.0
  description: Order management
servers:
  - url: https://api.example.com
paths:
  /orders/{id}:
    get:
      operationId: getOrder
      parameters:
        - name: id
          in: path
          required: true
          schema: { type: integer, format: int64 }
      responses:
        '200':
          description: order details
          content:
            application/json:
              schema: { $ref: '#/components/schemas/OrderResponse' }
        '404':
          description: not found
components:
  schemas:
    OrderResponse:
      type: object
      properties:
        id: { type: integer, format: int64 }
        status: { type: string, enum: [NEW, PROCESSING, SHIPPED, DELIVERED] }
        total: { type: number, format: double }
      required: [id, status, total]
  securitySchemes:
    bearerAuth:
      type: http
      scheme: bearer
      bearerFormat: JWT
security:
  - bearerAuth: []
```

This is JSON Schema for the data shapes + path / response / security wiring around them. **The spec is the source of truth** for API consumers — they generate clients from it.

## Code-First With springdoc-openapi

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.5.0</version>
</dependency>
```

Spring scans controllers and DTOs, generating the spec at runtime. Access:

- `/v3/api-docs` — JSON spec.
- `/v3/api-docs.yaml` — YAML spec.
- `/swagger-ui.html` — interactive Swagger UI.

Annotations enrich the generated spec:

```java
@RestController
@RequestMapping("/api/orders")
@Tag(name = "Orders", description = "Order management")
public class OrderController {

    @GetMapping("/{id}")
    @Operation(summary = "Get an order by id",
               description = "Returns the order if it exists, else 404")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "found",
            content = @Content(schema = @Schema(implementation = OrderResponse.class))),
        @ApiResponse(responseCode = "404", description = "not found",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public OrderResponse get(@PathVariable @Parameter(description = "Order id") long id) {
        return service.find(id);
    }
}

@Schema(description = "Order response")
public record OrderResponse(
    @Schema(description = "Identifier", example = "42") Long id,
    @Schema(description = "Current status", example = "NEW") OrderStatus status,
    @Schema(description = "Total amount", example = "99.95") BigDecimal total
) {}
```

Most fields are auto-derived; annotations add descriptions, examples, edge cases.

## Spec-First With openapi-generator

Write the YAML first; generate code:

```bash
openapi-generator-cli generate \
    -i orders-api.yaml \
    -g spring \
    --library spring-boot \
    -o generated/ \
    --additional-properties=interfaceOnly=true,useTags=true
```

Produces a `OrdersApi` interface with method signatures matching the spec. You implement it:

```java
@RestController
public class OrderController implements OrdersApi {
    @Override
    public ResponseEntity<OrderResponse> getOrder(Long id) {
        return ResponseEntity.ok(service.find(id));
    }
}
```

The spec is the contract; spec changes propagate to the interface; implementation has to follow. Strong consistency at the cost of a build step.

## Swagger UI vs Redoc

- **Swagger UI** — interactive; "Try it out" button hits real API; default in springdoc.
- **Redoc** — read-only; three-column layout; nicer for public docs.

Both consume the same OpenAPI JSON. Often: Swagger UI for development; Redoc for public docs.

## Client Generation

```bash
openapi-generator-cli generate -i spec.yaml -g typescript-axios -o ./client
openapi-generator-cli generate -i spec.yaml -g java -o ./java-client
openapi-generator-cli generate -i spec.yaml -g python -o ./python-client
```

Generated clients are typed; method signatures match operations. Frontend teams consume API changes via regenerated SDKs.

## Versioning

Two common strategies:

- **URL-based**: `/v1/orders`, `/v2/orders`. Explicit; URL versioned in spec.
- **Header-based**: `Accept: application/vnd.example.v2+json`. Cleaner URLs; less common.

Spec versioning:

```yaml
info:
  version: 2.0.0
```

Either bump `info.version` (semver) for spec-level changes, or split into `/v1.yaml` and `/v2.yaml`. The former is simpler; the latter cleaner for breaking changes.

## Contract Testing Integration

OpenAPI + **Spring Cloud Contract** or **Pact**: tests verify the implementation matches the spec.

```java
@AutoConfigureMockMvc
class OrderApiSpecTest {

    @Test void apiMatchesSpec() throws Exception {
        String spec = mvc.perform(get("/v3/api-docs")).andReturn()
            .getResponse().getContentAsString();
        // assert against committed spec.yaml
        // or: run OpenAPI Diff against committed baseline
    }
}
```

Or, in CI, run `openapi-diff` to detect breaking changes between commits.

## Common Patterns

### Reusable Components

```yaml
components:
  parameters:
    PageParam: { name: page, in: query, schema: { type: integer, default: 0 } }
    SizeParam: { name: size, in: query, schema: { type: integer, default: 20 } }
  schemas:
    Page:
      type: object
      properties:
        content: { type: array, items: { type: object } }
        totalElements: { type: integer }
        totalPages: { type: integer }
```

Reuse across endpoints; DRY.

### Discriminator For Polymorphic Responses

```yaml
PaymentResponse:
  oneOf:
    - $ref: '#/components/schemas/CardPayment'
    - $ref: '#/components/schemas/BankPayment'
  discriminator:
    propertyName: type
    mapping:
      CARD: '#/components/schemas/CardPayment'
      BANK: '#/components/schemas/BankPayment'
```

Generated clients produce sealed/discriminated unions.

### Examples

```yaml
responses:
  '200':
    content:
      application/json:
        schema: { $ref: '#/components/schemas/Order' }
        examples:
          basic: { value: { id: 1, status: NEW } }
          shipped: { value: { id: 2, status: SHIPPED, trackingNumber: "ZZ12345" } }
```

Multiple examples per response; Swagger UI lets users pick.

## Gateway Integration

API gateways (Kong, Apigee, Gloo) consume OpenAPI to:

- Wire authentication.
- Apply rate limiting.
- Validate request/response shapes.
- Generate developer portals.

OpenAPI is the **standard interchange** between application and gateway.

## OpenAPI 3.0 vs 3.1

- 3.0 (2017): subset of JSON Schema; widely supported.
- 3.1 (2021): full JSON Schema 2020-12 alignment; better union types.

springdoc supports both. Most tools are catching up to 3.1; default to 3.0 if tool compatibility matters.

## Code-First vs Spec-First

| Code-first | Spec-first |
|------------|-------------|
| Fast iteration; spec follows code | Slower; design upfront |
| Internal APIs; rapid evolution | Public APIs; stable contracts |
| Annotations + dependencies | Codegen + extra build step |
| Drift risk (spec stale vs code) | Source-of-truth-clarity |

For most Spring services: **code-first with springdoc-openapi**, plus CI checks for breaking changes.

## Common Pitfalls

> [!WARNING]
> **Spec not in version control.** Generated at runtime; can't diff. Commit a snapshot.

> [!WARNING]
> **Spec doesn't match runtime.** Annotations forgotten; runtime behavior diverges. Test.

> [!WARNING]
> **Springfox in 2026.** Springfox is unmaintained; use springdoc-openapi.

> [!WARNING]
> **Swagger UI on public endpoint.** Exposes admin endpoints. Secure or disable in prod.

> [!WARNING]
> **No spec versioning.** Breaking changes hidden. Version + diff in CI.

> [!WARNING]
> **Generated clients in main repo.** Bloat. Publish to separate package registry.

> [!WARNING]
> **OAS 3.1 features used but consumers parse 3.0.** Test compatibility.

> [!WARNING]
> **Overly verbose annotations.** Schema bloated; hard to maintain. Use defaults; annotate sparingly.

## Practice

1. Add springdoc-openapi. Visit `/swagger-ui.html`; verify your endpoints render.
2. Add `@Operation`, `@Schema` annotations to enrich the spec.
3. Generate a TypeScript client; consume from a frontend app.
4. Set up Redoc for public docs; compare to Swagger UI.
5. Commit a snapshot spec to git; run openapi-diff in CI; introduce a breaking change; observe CI fail.
6. Try spec-first with openapi-generator + the spring generator; implement the interface.
7. Define a polymorphic response with `oneOf` + discriminator; verify generated client.
8. Wire generated examples into Swagger UI; confirm UI lets users pick.

## Recap

You should now be able to:

- Read and write OpenAPI 3.x specs covering paths, components, schemas, security.
- Use springdoc-openapi for code-first generation; enrich with `@Operation`, `@Schema`, `@ApiResponses`.
- Use openapi-generator for spec-first with Spring server interface.
- Generate clients (TypeScript, Java, Python, ...) from the spec.
- Render docs via Swagger UI (interactive) or Redoc (public-friendly).
- Integrate with contract testing (Spring Cloud Contract, Pact) and CI diffing.
- Choose code-first vs spec-first per project priority.
- Avoid the canonical pitfalls: Springfox in 2026, no version control, no diff CI, leaked Swagger UI in prod.

## Next

Continue to [GraphQL](./T05-graphql.md) for the alternative API paradigm — single endpoint, client-shaped queries, schema-first design, Spring for GraphQL, and the trade-offs vs REST.
