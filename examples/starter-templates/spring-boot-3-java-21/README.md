# Spring Boot 3 + Java 21 — Starter Template

Backs: L4/C01 Spring Framework (starter template)

A minimal, **runnable** baseline for a modern Java backend: Spring Boot 3.3 on Java 21,
built with Maven, tested with JUnit 5. Copy this directory as the seed for any new service.

## What it demonstrates

- A modern **Spring Boot 3.3 + Java 21** project laid out the conventional Maven way.
- A thin **`@RestController`** (`GreetingController`) delegating to a **`@Service`**
  (`GreetingService`) — the standard controller/service separation.
- A JSON DTO modeled as a Java **`record`** (`GreetingResponse`), serialized by Jackson.
- Java 21 idioms used where natural: `record`, `var`, and **`switch` pattern matching with
  `when` guards** (lightly commented as teaching points in `GreetingService`).
- **Constructor injection** (preferred over field injection).
- **Spring Boot Actuator** health endpoint, with exposure configured in `application.yml`.
- Two flavours of test: a fast **plain unit test** of the service, and a
  **`@SpringBootTest` + `@AutoConfigureMockMvc`** web-layer test that asserts on the JSON body.

## Prerequisites

- **Java 21** (an LTS release). Check with `java -version` — it should report `21`.
- **Maven 3.9+**. Check with `mvn -version`.

## Run it

From this directory (`examples/starter-templates/spring-boot-3-java-21/`):

```bash
# 1. Run the tests (unit + MockMvc web test)
mvn test

# 2. Start the app (embedded Tomcat on port 8080)
mvn spring-boot:run
```

Then, in another terminal:

```bash
# Call the greeting endpoint
curl "localhost:8080/api/greeting?name=Ada"

# Check the actuator health endpoint
curl localhost:8080/actuator/health
```

### Expected output

`curl "localhost:8080/api/greeting?name=Ada"`:

```json
{"message":"Hello, Ada!","language":"en"}
```

Calling it with no name (`curl localhost:8080/api/greeting`) falls back to:

```json
{"message":"Hello, World!","language":"en"}
```

`curl localhost:8080/actuator/health`:

```json
{"status":"UP"}
```

(With `show-details: always` set in `application.yml`, the health response also includes
per-component details such as `diskSpace` and `ping`.)

`mvn test` should finish with `BUILD SUCCESS` and all tests passing
(`GreetingServiceTest`, `GreetingControllerTest`).

## Files to read first

1. **`pom.xml`** — the Boot parent, the three starters, and `<java.version>21</java.version>`.
2. **`src/main/java/com/javamastery/starter/Application.java`** — the entry point and what
   `@SpringBootApplication` bundles.
3. **`src/main/java/com/javamastery/starter/GreetingController.java`** — the REST endpoint and
   constructor injection.
4. **`src/main/java/com/javamastery/starter/GreetingService.java`** — the Java 21 idioms
   (`record` usage, `var`, `switch` pattern matching).
5. **`src/main/java/com/javamastery/starter/GreetingResponse.java`** — the record DTO.
6. **`src/main/resources/application.yml`** — port and actuator configuration.
7. **`src/test/java/com/javamastery/starter/`** — the two test styles.

## Upgrading to Spring Boot 4 / Java 25

This template targets the current LTS baseline (Boot 3.3, Java 21) so it runs everywhere today.
When you move to **Spring Boot 4** (which raises the Java floor and adopts Jakarta/JSpecify and
newer language features) and **Java 25**, see **L4/C01/T26** for the migration walkthrough:
bumping the parent version, the changed minimum JDK, dependency/BOM shifts, and the deprecations
to clear before upgrading.
