---
title: "GraphQL"
slug: graphql
level: L4
module: "Backend Engineering"
section: "APIs — Advanced"
type: concept
difficulty: senior
order: 5
tags: [graphql, schema-first, query, mutation, subscription, type-system, resolver, data-loader, n-plus-1-resolver, batching, apollo, graphql-java, spring-for-graphql, graphqls, sdl, schema-definition-language, type-extension, union-type, interface-type, scalar, custom-scalar, directive, deprecation, federation, persisted-queries, query-complexity, depth-limit, graphql-vs-rest, over-fetching, under-fetching]
prerequisites: [openapi-swagger-documentation, the-n-plus-1-problem-and-fixes]
status: complete
estimated_minutes: 60
last_updated: 2026-06-08
---

# GraphQL

REST exposes **fixed endpoints** returning fixed shapes — clients often over-fetch (download fields they ignore) or under-fetch (need a second request for related data). **GraphQL** (Facebook, 2015 → CNCF 2019) inverts this: a **single endpoint** accepts queries that specify *exactly* the fields the client wants from a graph of types. The server returns matching JSON. One request can traverse many resources in one round trip. Strongly typed; schema-first; introspectable. The trade-offs: server complexity grows (every type needs resolvers; N+1 is a real risk); caching is harder (one URL with infinite query shapes); learning curve for both producers and consumers.

In 2026, GraphQL has a stable niche: **BFFs (T11)**, **mobile apps** with bandwidth concerns, **complex nested data**, **federated graphs across many microservices** (Apollo Federation). It hasn't replaced REST as predicted — for simple CRUD APIs, REST + OpenAPI is more straightforward. **Spring for GraphQL** (2022) is the canonical Java GraphQL framework, replacing the older `graphql-java-spring-boot-starter`.

A senior engineer reaches for GraphQL when (a) clients need flexible projections of a complex domain; (b) over-fetching / multi-request cost matters; (c) the team can invest in resolver design. For most internal APIs, REST suffices.

This topic covers: the GraphQL type system; queries / mutations / subscriptions; schema-first design with SDL; resolvers in Spring for GraphQL; the DataLoader pattern (the N+1 fix); query complexity / depth limiting; persisted queries; federation; the trade-offs vs REST.

> [!NOTE]
> Prerequisites: [OpenAPI (T04)](./T04-openapi-swagger-documentation.md), [N+1 (L4/C02/T07)](../C02-persistence-jpa-hibernate/T07-the-n-plus-1-problem-and-fixes.md), Spring MVC.

## The Type System

A schema in SDL (Schema Definition Language):

```graphql
type Query {
  user(id: ID!): User
  orders(status: OrderStatus): [Order!]!
}

type Mutation {
  placeOrder(input: PlaceOrderInput!): Order!
  cancelOrder(id: ID!): Order
}

type Subscription {
  orderStatusChanged(orderId: ID!): Order!
}

type User {
  id: ID!
  name: String!
  email: String
  orders: [Order!]!
}

type Order {
  id: ID!
  status: OrderStatus!
  total: Float!
  customer: User!
  items: [OrderItem!]!
}

type OrderItem {
  product: Product!
  quantity: Int!
  unitPrice: Float!
}

enum OrderStatus { NEW, PROCESSING, SHIPPED, DELIVERED, CANCELLED }

input PlaceOrderInput {
  customerId: ID!
  items: [OrderItemInput!]!
}

input OrderItemInput {
  productId: ID!
  quantity: Int!
}
```

Strong typing: every field declares its type; non-null marked `!`; lists `[T!]!`. Tools generate clients with full type safety.

## Queries

```graphql
query {
  user(id: "42") {
    name
    email
    orders {
      id
      status
      total
    }
  }
}
```

Server returns:

```json
{
  "data": {
    "user": {
      "name": "Alice",
      "email": "alice@example.com",
      "orders": [
        { "id": "100", "status": "DELIVERED", "total": 99.95 },
        { "id": "101", "status": "NEW", "total": 49.50 }
      ]
    }
  }
}
```

Only the requested fields. The client controls projection.

## Mutations

```graphql
mutation {
  placeOrder(input: { customerId: "42", items: [{ productId: "p1", quantity: 2 }] }) {
    id
    status
    total
  }
}
```

Mutations look like queries but POST and have side effects. Convention: name with verb (placeOrder, cancelOrder).

## Subscriptions

```graphql
subscription {
  orderStatusChanged(orderId: "100") {
    status
  }
}
```

WebSocket-based. Server pushes updates when the matched event occurs. Useful for live dashboards, chat, notifications.

## Spring For GraphQL

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-graphql</artifactId>
</dependency>
```

Place schema at `src/main/resources/graphql/schema.graphqls`.

Endpoint: `/graphql` (POST) by default. `graphiql` UI at `/graphiql` if `spring.graphql.graphiql.enabled=true`.

### Resolvers

```java
@Controller
public class UserController {

    private final UserService userService;
    private final OrderService orderService;

    @QueryMapping
    public User user(@Argument String id) {
        return userService.find(Long.parseLong(id));
    }

    @SchemaMapping
    public List<Order> orders(User user) {   // resolves User.orders
        return orderService.findByUser(user.getId());
    }
}

@Controller
public class OrderController {

    @QueryMapping
    public List<Order> orders(@Argument OrderStatus status) {
        return orderService.list(status);
    }

    @MutationMapping
    public Order placeOrder(@Argument PlaceOrderInput input) {
        return orderService.place(input);
    }

    @SchemaMapping
    public User customer(Order order) {
        return userService.find(order.getCustomerId());
    }
}
```

Method names match GraphQL field names; `@QueryMapping` / `@MutationMapping` / `@SubscriptionMapping` for root operations; `@SchemaMapping` for nested fields.

### Subscriptions With WebFlux

```java
@SubscriptionMapping
public Flux<Order> orderStatusChanged(@Argument String orderId) {
    return orderEventBus.events()
        .filter(e -> e.orderId().equals(orderId))
        .map(e -> e.order());
}
```

Returns a `Flux<T>`; Spring wires WebSocket-based subscription delivery.

## DataLoader — The N+1 Fix

The infamous GraphQL N+1: a query selects `users { name, orders { id } }` for 100 users; the naïve implementation calls `orderService.findByUser` 100 times.

DataLoader batches and dedupes:

```java
@Bean
public BatchLoaderRegistry registry() {
    return new DefaultBatchLoaderRegistry();
}

@Component
public class OrderDataLoader {

    public OrderDataLoader(BatchLoaderRegistry registry, OrderRepository repo) {
        registry.forTypePair(Long.class, List.class)
            .registerMappedBatchLoader((customerIds, env) ->
                Mono.fromCallable(() -> repo.findByCustomerIdIn(customerIds).stream()
                    .collect(Collectors.groupingBy(Order::getCustomerId)))
            );
    }
}

@SchemaMapping
public CompletableFuture<List<Order>> orders(User user, DataLoader<Long, List<Order>> loader) {
    return loader.load(user.getId());
}
```

The framework collects all `loader.load(id)` calls in one execution tick; calls the batch loader once with all keys; distributes results. **The N+1 is gone**: 1 query for users + 1 batched query for orders.

DataLoader is **mandatory** for non-trivial GraphQL services. Without it, every query risks N+1.

## Query Complexity / Depth Limiting

A client can issue a query of unbounded depth or scope: `user { friends { friends { friends { ... } } } }`. DoS risk.

```yaml
spring:
  graphql:
    schema:
      printer:
        enabled: true
    websocket:
      path: /graphql
```

Implement limits:

```java
@Bean
public RuntimeWiringConfigurer wiringConfigurer() {
    return wiring -> wiring.directive("...", ...);
}

@Bean
public GraphQlSourceBuilderCustomizer instrumentation() {
    return builder -> builder.configureGraphQl(b ->
        b.instrumentation(List.of(
            new MaxQueryComplexityInstrumentation(1000),
            new MaxQueryDepthInstrumentation(10)
        )));
}
```

Reject queries exceeding limits with an error.

## Persisted Queries

For public APIs, allow only pre-registered queries:

- Client sends a hash; server looks up the corresponding query.
- Smaller request size; query allowlisting.

Apollo's persisted-query extension is the standard.

## Apollo Federation

Multiple GraphQL services compose into one supergraph. Each service ("subgraph") owns part of the schema; Apollo Router resolves queries across them:

```graphql
# users subgraph
type User @key(fields: "id") {
  id: ID!
  name: String!
}

# orders subgraph
extend type User @key(fields: "id") {
  id: ID! @external
  orders: [Order!]!   # this subgraph extends User
}

type Order { id: ID!  total: Float!  ... }
```

A query `user { name, orders { total } }` is routed to both subgraphs; results stitched. Powerful for big teams with many services; complex operationally.

## GraphQL Vs REST

| Aspect | GraphQL | REST |
|--------|---------|------|
| Endpoints | one | many |
| Over-fetching | client decides | full payload |
| Under-fetching | one query enough | often multi-request |
| Caching | hard (CDN-unfriendly per query) | easy (per URL) |
| Tooling maturity | good but more complex | excellent |
| Mobile bandwidth | wins | loses if many requests |
| Backend complexity | resolvers + DataLoader | simpler controllers |
| Schema evolution | strict (need versioned types) | easier (additive) |
| Learning curve | moderate | low |

**Use GraphQL when**: mobile / single-page app with tight bandwidth; complex domain queries; federated graph across many services; BFF aggregating multiple backends.

**Use REST when**: simple CRUD; cacheable resources; tooling familiarity matters; small team.

## Common Pitfalls

> [!WARNING]
> **No DataLoader.** N+1 on every nested field. Cluster-killer.

> [!WARNING]
> **Exposing every entity directly.** Schema bloat; security surface. Define DTOs at the schema layer.

> [!WARNING]
> **No query depth / complexity limit.** Public-facing DoS vulnerability.

> [!WARNING]
> **Caching as if it were REST.** GraphQL needs different strategy — persisted queries; per-field cache.

> [!WARNING]
> **Mutations returning void / boolean.** Convention is to return updated entity.

> [!WARNING]
> **Mixing REST and GraphQL for same data.** Two truth sources to maintain.

> [!WARNING]
> **Subscriptions on long-lived connections without back-pressure.** Server overwhelmed by slow clients.

> [!WARNING]
> **Federation without strict subgraph ownership.** Schema churn.

## Practice

1. Define a small GraphQL schema. Implement resolvers with Spring for GraphQL. Test in GraphiQL.
2. Add a nested resolver (User → orders) without DataLoader; observe N+1. Add DataLoader; verify single batched query.
3. Add `@QueryMapping` returning `Optional<T>`; verify null handling.
4. Add a mutation with input type. Test.
5. Implement a subscription; connect via WebSocket; verify push.
6. Add max-depth and max-complexity instrumentation; test rejection.
7. Compare GraphQL query for "user + orders + items + products" to equivalent REST (multiple endpoints). Measure round trips and bytes.
8. Try Apollo Federation across two Spring services; verify resolved query.

## Recap

You should now be able to:

- Design a GraphQL schema with types, queries, mutations, subscriptions, inputs.
- Implement resolvers with Spring for GraphQL (`@QueryMapping`, `@MutationMapping`, `@SchemaMapping`, `@SubscriptionMapping`).
- Use DataLoader to batch nested loads; eliminate N+1.
- Limit query depth / complexity for DoS protection.
- Implement persisted queries for public APIs.
- Use Apollo Federation for cross-service supergraphs.
- Choose GraphQL vs REST per use case (GraphQL for flexible projections, mobile, BFF; REST for simple CRUD and caching).
- Avoid the canonical pitfalls: no DataLoader, no depth limit, mismatched caching, mixing with REST without discipline.

## Next

Continue to [gRPC & Protocol Buffers](./T06-grpc-and-protocol-buffers.md) for binary RPC over HTTP/2 — schema-first .proto files, code generation, streaming, the dominant choice for inter-service communication at scale.
