---
title: "Behavior-Driven Development (BDD, Cucumber)"
slug: behavior-driven-development-bdd-cucumber
level: L4
module: "Backend Engineering"
section: "Testing — Advanced"
type: concept
difficulty: senior
order: 4
tags: [bdd, behavior-driven-development, cucumber, gherkin, given-when-then, feature-files, step-definitions, dan-north, jbehave, karate, executable-specifications, ubiquitous-language]
prerequisites: [integration-testing]
status: complete
estimated_minutes: 45
last_updated: 2026-06-08
---

# Behavior-Driven Development (BDD, Cucumber)

Behavior-Driven Development (BDD) is the practice — coined by Dan North in 2003 — of expressing tests as plain-language *scenarios* that describe behavior from the user's perspective. The flagship tool is Cucumber: features written in Gherkin (`Given/When/Then`), each step bound to executable Java code. The goal is *executable specifications* that double as documentation and that non-engineers (product managers, QA, customers) can read and validate. Done well, BDD aligns the whole team around behavior; done poorly, it adds layers of ceremony over tests that would have been clearer as plain JUnit.

This topic covers Cucumber's structure, Gherkin syntax, step definition binding, the pragmatic decision tree (when BDD pays off vs when it's overkill), the JBehave/Karate alternatives, and how BDD sits inside the broader testing strategy.

> [!NOTE]
> Prerequisites: [Integration testing (L4/C09/T01)](./T01-integration-testing.md).

## Origins — Dan North, 2003

Dan North was teaching TDD and noticed engineers stumbled on:
- "What should I test first?"
- "What should I call this test?"
- "How do I know I'm done?"

He noticed when tests were named like *behaviors* (`shouldRefundExpiredOrders`) instead of *implementation* (`testRefundLogic`), people understood. He extended this to a whole methodology: focus on behavior, use natural language, involve non-engineers.

The original article: "Introducing BDD" (2006). Cucumber (Ruby, by Aslak Hellesøy) came in 2008 and spread to Java/JVM.

## The Three Practices Of BDD

1. **Discovery**: collaborative workshops ("Three Amigos": dev, QA, product) to explore requirements.
2. **Formulation**: capture agreed examples as Gherkin scenarios.
3. **Automation**: implement step definitions; scenarios become regression tests.

Many teams skip #1 and #2 and only do #3 — which is "Cucumber as a JUnit alternative" and misses the point.

## Gherkin — The Language

A feature file:

```gherkin
Feature: Order checkout
  As a customer
  I want to check out my cart
  So that I can receive my items

  Background:
    Given the catalog has product "ABC-123" priced at 49.99 USD

  Scenario: Single-item checkout
    Given customer "user-42" has 1 of "ABC-123" in cart
    When customer "user-42" checks out
    Then a receipt is issued with total 49.99 USD
    And inventory for "ABC-123" decreases by 1

  Scenario: Out-of-stock item
    Given catalog item "OUT-OF-STOCK" has 0 inventory
    And customer "user-42" has 1 of "OUT-OF-STOCK" in cart
    When customer "user-42" checks out
    Then checkout fails with message "Item out of stock"

  Scenario Outline: Tax calculation by state
    Given customer "user-42" is in state "<state>"
    And customer "user-42" has 1 of "ABC-123" in cart
    When customer "user-42" checks out
    Then receipt tax amount is "<tax>"

    Examples:
      | state | tax  |
      | CA    | 4.13 |
      | OR    | 0.00 |
      | NY    | 4.37 |
```

Keywords:
- `Feature`: the capability being tested.
- `Scenario`: one example of behavior.
- `Background`: setup steps run before each scenario.
- `Given`: precondition (state).
- `When`: action.
- `Then`: expected outcome.
- `And`/`But`: continuation.
- `Scenario Outline`/`Examples`: parameterized scenarios.

## Cucumber Project Setup

```xml
<dependency>
  <groupId>io.cucumber</groupId>
  <artifactId>cucumber-java</artifactId>
  <version>7.20.1</version>
  <scope>test</scope>
</dependency>
<dependency>
  <groupId>io.cucumber</groupId>
  <artifactId>cucumber-junit-platform-engine</artifactId>
  <version>7.20.1</version>
  <scope>test</scope>
</dependency>
<dependency>
  <groupId>io.cucumber</groupId>
  <artifactId>cucumber-spring</artifactId>
  <version>7.20.1</version>
  <scope>test</scope>
</dependency>
```

Project layout:
```
src/test/
├── java/
│   └── com/example/steps/
│       └── CheckoutStepDefinitions.java
└── resources/
    ├── junit-platform.properties
    └── features/
        └── checkout.feature
```

`junit-platform.properties`:
```properties
cucumber.junit-platform.naming-strategy=long
cucumber.glue=com.example.steps
cucumber.features=src/test/resources/features
```

Runner:
```java
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.example.steps")
class CucumberRunnerIT { }
```

## Step Definitions

Each Gherkin step matches a Java method via regex/Cucumber Expression:

```java
@CucumberContextConfiguration
@SpringBootTest
public class CheckoutStepDefinitions {

    @Autowired CartService cartService;
    @Autowired CheckoutService checkoutService;
    @Autowired CatalogService catalogService;

    private Receipt lastReceipt;
    private Exception lastError;

    @Given("the catalog has product {string} priced at {double} {word}")
    public void catalogPriced(String sku, double price, String currency) {
        catalogService.add(new Product(sku, BigDecimal.valueOf(price), currency));
    }

    @Given("customer {string} has {int} of {string} in cart")
    public void customerHas(String userId, int qty, String sku) {
        cartService.add(userId, sku, qty);
    }

    @When("customer {string} checks out")
    public void checksOut(String userId) {
        try {
            lastReceipt = checkoutService.checkout(userId);
            lastError = null;
        } catch (Exception e) {
            lastError = e;
            lastReceipt = null;
        }
    }

    @Then("a receipt is issued with total {double} {word}")
    public void receiptHasTotal(double expected, String currency) {
        assertThat(lastError).isNull();
        assertThat(lastReceipt.getTotal()).isEqualByComparingTo(BigDecimal.valueOf(expected));
        assertThat(lastReceipt.getCurrency()).isEqualTo(currency);
    }

    @Then("checkout fails with message {string}")
    public void checkoutFails(String message) {
        assertThat(lastError).isNotNull();
        assertThat(lastError.getMessage()).contains(message);
    }
}
```

`@CucumberContextConfiguration` + `@SpringBootTest` boots the Spring context for Cucumber steps. Steps can `@Autowired` Spring beans.

## Cucumber Expressions vs Regex

```java
@Given("the catalog has product {string} priced at {double} {word}")  // Expression
@Given("^the catalog has product \"(.+)\" priced at ([0-9.]+) (\\w+)$")  // Regex
```

Cucumber Expressions are simpler and type-safe (`{int}`, `{string}`, `{double}`, `{word}`). Use them unless you need regex flexibility.

Custom parameter types:
```java
@ParameterType("USD|EUR|GBP")
public Currency currency(String code) {
    return Currency.valueOf(code);
}

@Given("the price is {double} {currency}")
public void price(double amount, Currency currency) { ... }
```

## Scenario Lifecycle

By default, a fresh world (state) per scenario. State across steps in one scenario goes in step-definition fields (`lastReceipt`, `lastError`).

For shared state across steps in different classes:
```java
public class TestContext {
    public String userId;
    public Receipt receipt;
}

// Inject in step classes via Spring or PicoContainer (Cucumber DI).
```

`@Before` / `@After` hooks run before/after every scenario:

```java
@Before
public void setUp() {
    cartService.clear();
}

@After("@cleanup-db")
public void clearDb() {
    db.execute("TRUNCATE TABLE orders");
}
```

## Tags For Filtering

```gherkin
@checkout @slow
Scenario: Multi-currency checkout
  ...
```

Run only tagged scenarios:
```bash
mvn test -Dcucumber.filter.tags="@checkout and not @slow"
```

Useful for splitting fast/slow suites or excluding flaky tests.

## When BDD Pays Off

BDD is worth the ceremony when:
- **Cross-functional collaboration**: PMs/QA validate scenarios before code.
- **Complex business rules**: many examples make rules concrete.
- **Living documentation**: scenarios always match current behavior (they execute).
- **Regulated domains**: auditable specs.

BDD is *not* worth it when:
- Engineer-only audience.
- Pure technical tests (parsing, persistence).
- High-churn requirements that make scenarios stale.
- Team treats Cucumber as "tests in English" without collaboration.

The senior trap: introducing Cucumber for tests no non-engineer ever reads. The ceremony then is pure overhead.

## Alternatives

### JBehave

The original Java BDD framework. Older but still maintained. More verbose than Cucumber.

### Karate

Newer (intuit, 2017). DSL aimed at API testing:

```gherkin
Feature: User API

  Scenario: Get user
    Given url 'http://localhost:8080/api/users/42'
    When method get
    Then status 200
    And match response == { id: 42, name: 'Alice' }
```

No step definitions! Karate has built-in HTTP, JSON, assertions. Great for API contract tests; less useful for non-HTTP behavior.

### Plain JUnit With Descriptive Names

Many teams use plain JUnit with `@DisplayName`:

```java
@Test
@DisplayName("checkout fails when item is out of stock")
void outOfStockCheckoutFails() { ... }
```

Same intent, less ceremony. Fine when there's no cross-functional reader.

## Living Documentation

Cucumber generates HTML reports with all scenarios. Hook up via plugin:

```bash
mvn test -Dcucumber.plugin="pretty,html:target/cucumber-reports.html"
```

These reports double as documentation: business stakeholders see what the system does today.

## CI Integration

Cucumber runs as JUnit. Failures surface like any test. Reports can be published as build artifacts. With Allure or ExtentReports plugins, the output becomes rich.

## Anti-Patterns

> [!WARNING]
> **Imperative Gherkin.** "When I click the button, then I see a div." Avoid UI details in features.

> [!WARNING]
> **Too many steps per scenario.** > 7 steps is hard to read.

> [!WARNING]
> **Sharing scenarios across features.** Features should stand alone.

> [!WARNING]
> **Mutable shared state via static fields.** Race conditions.

> [!WARNING]
> **No Three Amigos meetings.** BDD without collaboration is JUnit-with-extra-files.

> [!WARNING]
> **Feature files for technical tests.** Use plain JUnit for tech-only concerns.

> [!WARNING]
> **One huge step that does everything.** Steps should be reusable building blocks.

> [!WARNING]
> **Tightly coupled scenarios.** Run-order dependence breaks parallelism.

## Common Misconceptions

> [!WARNING]
> **"BDD is the same as TDD."** TDD is process. BDD is collaboration + language.

> [!WARNING]
> **"Cucumber is for QA."** It's for the team, including engineers.

> [!WARNING]
> **"All tests should be Cucumber."** Use for behavior; plain JUnit for code-level tests.

> [!WARNING]
> **"Gherkin is for non-engineers to write."** Engineers and non-engineers write it together.

> [!WARNING]
> **"BDD makes tests faster."** It doesn't. Often slower due to ceremony.

## Practice

1. **First Cucumber project**: set up Cucumber with Spring Boot. One feature, one scenario.
2. **Step definitions**: implement steps with Cucumber Expressions.
3. **Scenario outline**: parameterize a scenario with `Examples`.
4. **`@Before`/`@After`**: cleanup state per scenario.
5. **Tags**: filter scenarios by tag.
6. **Live docs**: generate HTML report.
7. **Three Amigos exercise**: write a feature collaboratively with a PM and QA (simulate if necessary).
8. **Compare**: rewrite a Cucumber scenario as plain JUnit. Which is clearer?
9. **Karate**: try Karate for an HTTP API test.

## Recap

You should now be able to:

- Write Gherkin features and scenarios.
- Bind steps to Java code via Cucumber Expressions.
- Integrate Cucumber with Spring Boot.
- Use tags, hooks, scenario outlines.
- Decide when BDD is worth the ceremony.
- Compare Cucumber, JBehave, Karate, plain JUnit.

## Next

Continue to [Contract testing (Spring Cloud Contract, Pact)](./T05-contract-testing-spring-cloud-contract-pact.md) — keeping producer and consumer of an API in sync without expensive E2E tests.
