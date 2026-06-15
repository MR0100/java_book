# Spring Boot 3 + GraalVM Native Image — starter template

> Backs: **L4/C01/T25 Spring Native / GraalVM** + **L3/C02/T05 AOT & GraalVM native image**

A deliberately tiny Spring Boot 3.3 web app whose *point is the build*: compiling a JVM
application ahead-of-time (AOT) into a single, self-contained **native executable** with GraalVM
`native-image`. The app itself is one endpoint — `GET /ping` → `{"pong":true,"startupNote":"..."}` —
so nothing distracts from the native-image workflow.

---

## What is a native image (and why bother)?

Normally a Java app ships as bytecode that the **JVM** loads, verifies, JIT-compiles, and runs.
Startup pays for class loading, classpath scanning, reflection-driven wiring, and JIT warm-up — for
a Spring Boot service that is roughly **0.8–2 s** before the first request, and a baseline heap of
tens to hundreds of MB.

GraalVM `native-image` instead performs **ahead-of-time (AOT) compilation**: it does whole-program
static analysis under a *closed-world assumption* (everything reachable must be known at build
time), then emits a native OS executable with the needed runtime and a snapshot of the initialized
heap baked in. Spring Boot 3's **AOT engine** runs first — it evaluates your `@Configuration` at
build time and freezes the bean definitions plus GraalVM *reachability metadata* (reflection,
resources, proxies) so the static analysis knows what to keep.

The payoff:

| | JVM (`java -jar`) | Native image |
|---|---|---|
| **Cold start** | ~0.8–2 s | **tens of ms** (often < 100 ms) |
| **Memory at idle** | tens–hundreds of MB | **a fraction of that** |
| **Distribution** | JAR + a JRE | **single binary, no JVM needed** |
| **Peak throughput** | usually higher (JIT) | slightly lower (no JIT) |
| **Build time** | seconds | **minutes** (slow!) |

This makes native images a strong fit for **serverless / scale-to-zero, CLIs, and short-lived or
densely-packed containers**, where fast cold start and low memory beat peak throughput.

---

## Prerequisites

- **Java 21** — an LTS release. For the JVM run, any JDK 21 works.
- **Maven** (the `mvn` on your `PATH`; this repo uses Maven 3.9+).
- **A GraalVM JDK with `native-image` installed** — required *only* for the native build.

The easiest way to get a GraalVM JDK 21 is [SDKMAN](https://sdkman.io/):

```bash
sdk install java 21-graalce      # GraalVM Community Edition, JDK 21
sdk use java 21-graalce          # point this shell at it
java -version                    # should mention "GraalVM"
native-image --version           # ships inside recent GraalVM JDKs; if missing: `gu install native-image`
```

> Verify `JAVA_HOME` points at the GraalVM JDK before a native build — `native-image` is discovered
> via that JDK, not via Maven.

---

## Commands

All commands run from this directory.

### 1. Run on the JVM (fast, for development)

```bash
mvn spring-boot:run
```

Then in another terminal:

```bash
curl http://localhost:8080/ping
# {"pong":true,"startupNote":"process has been up for 1023 ms"}
```

### 2. Run the tests (a `@SpringBootTest` smoke test)

```bash
mvn test
```

### 3. Build the native image (needs GraalVM; slow — minutes)

```bash
mvn -Pnative native:compile
```

`-Pnative` activates the **`native` profile that Spring Boot's parent POM already defines** (it
wires in `org.graalvm.buildtools:native-maven-plugin` and runs Spring AOT). The `native:compile`
goal first runs AOT processing, then invokes `native-image`. Expect this to take **several minutes**
and a lot of RAM/CPU — that is normal for native-image, not a hang.

> Note: the very first time, AOT/native may need to download the GraalVM reachability-metadata
> repository. Keep the machine online for the first build.

### 4. Run the native binary

```bash
./target/spring-boot-3-native-image
```

(The binary name comes from `<imageName>${project.artifactId}</imageName>` in `pom.xml`.) It serves
the same `GET /ping` on port 8080 — but watch the log line "Started Application in **0.0xx**
seconds".

### 5. Compare startup: JVM vs native

The app logs its own startup time, and `/ping` reports process uptime. A quick comparison:

```bash
# JVM: look for "Started Application in N seconds" — typically ~1 s
mvn spring-boot:run            # Ctrl-C after it logs the startup line

# Native: the same log line, typically ~0.0xx s
time ./target/spring-boot-3-native-image &   # the "Started ... in" line shows the cold start
sleep 1 && curl -s localhost:8080/ping ; kill %1
```

You should see the native process reach "Started" **one to two orders of magnitude faster** than the
JVM run. (Memory is the other win: compare `RES` in `top`/`htop` for the two processes.)

---

## Caveats: reflection & AOT hints

Native-image's closed-world analysis only keeps what it can *prove* reachable. Anything reached
**only via reflection, dynamic proxies, resource loading, or JNI** is invisible to that analysis and
must be declared up front, or it fails at runtime (e.g. `ClassNotFoundException`,
`MissingReflectionRegistrationError`).

- **Most Spring/Jackson cases are handled for you.** Spring Boot's AOT engine generates the hints
  for beans, controllers, and the DTOs they (de)serialize — which is why the `PingResponse` record
  in this template needs *zero* manual config.
- **Your own reflection needs hints.** For types Spring cannot see, register them with
  `@RegisterReflectionForBinding`, a `RuntimeHintsRegistrar` + `@ImportRuntimeHints`, or (last
  resort) hand-written `reflect-config.json` under `META-INF/native-image/`.
- **Tracing agent fallback.** When you cannot enumerate hints by hand, run the app on the JVM under
  the GraalVM tracing agent
  (`java -agentlib:native-image-agent=config-output-dir=...`) to record the metadata, then feed it
  to the build.
- Build is **slow and memory-hungry** by design; iterate on the JVM and only compile native when you
  need the artifact.

### Don't need a separate compiler? Consider CRaC instead

If your goal is just **fast cold start** but you'd rather keep the JVM (full reflection, normal build
times, JIT peak throughput), the alternative covered in **L3/C02/T05** is **CRaC (Coordinated
Restore at Checkpoint)**: you snapshot a warmed-up running JVM and restore it in milliseconds.
CRaC keeps you on a regular JVM (no closed-world constraints, no native-image build) at the cost of
managing checkpoint images and a CRaC-enabled JDK. Native image vs CRaC is the core trade-off to
weigh: *smaller/standalone binary and lowest memory* (native) vs *full JVM dynamism with fast
restore* (CRaC).

---

## Project layout

```
spring-boot-3-native-image/
├── pom.xml                          # Boot 3.3.5 parent; deps: web + test; `native` profile
├── README.md
└── src
    ├── main
    │   ├── java/com/javamastery/nativeapp/
    │   │   ├── Application.java      # @SpringBootApplication entry point
    │   │   ├── PingController.java   # GET /ping
    │   │   └── PingResponse.java     # record DTO -> {"pong":..,"startupNote":..}
    │   └── resources/
    │       └── application.properties
    └── test/java/com/javamastery/nativeapp/
        └── PingControllerSmokeTest.java   # @SpringBootTest smoke test (runs on the JVM)
```
