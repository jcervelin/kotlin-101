# Kotlin + Ktor For Java/Spring Boot Developers

This repository contains a compact Kotlin + Ktor service for experienced
Java/Spring Boot developers.

- a small real-world order API built with Ktor
- domain classes written in idiomatic Kotlin
- application services and repositories without framework coupling
- executable command-line demos
- Markdown notes with Java/Spring Boot comparisons

## Documentation

| Topic | File |
|-------|------|
| Kotlin compared with Java | [docs/01-kotlin-for-java-developers.md](docs/01-kotlin-for-java-developers.md) |
| Ktor compared with Spring Boot | [docs/02-ktor-for-spring-developers.md](docs/02-ktor-for-spring-developers.md) |
| Side-by-side reference | [docs/03-java-spring-vs-kotlin-ktor-cheatsheet.md](docs/03-java-spring-vs-kotlin-ktor-cheatsheet.md) |
| Next steps | [docs/04-next-steps.md](docs/05-next-steps.md) |
| Consuming Spring WebFlux SSE with Ktor | [docs/05-consuming-spring-flux-sse-with-ktor.md](docs/04-consuming-spring-flux-sse-with-ktor.md) |

## Code Map

```text
src/main/kotlin/com/ada/training
    api             Ktor routes, DTOs, error handling
    application     use cases, commands, repository interfaces
    domain          Kotlin domain model
    infrastructure  in-memory adapters and sample data
    playground      executable interactive demos
```

The HTTP framework stays at the edge. Domain and application code remain plain
Kotlin.

## Executable Examples

| Command | Purpose |
|---------|---------|
| `./gradlew runKotlinBasicsDemo` | interactive Kotlin language demo |
| `./gradlew runKotlinSyntaxDemo` | Kotlin syntax features Java does not have |
| `./gradlew runOrderScenarioDemo` | interactive domain/service demo without HTTP |
| `./gradlew runSseClientDemo` | consumes a Spring WebFlux SSE endpoint with Ktor Client |
| `./gradlew runServer` | starts the Ktor API |
| `./gradlew test` | runs Ktor API tests |

On Windows PowerShell, use:

```powershell
.\gradlew.bat runKotlinBasicsDemo
.\gradlew.bat runKotlinSyntaxDemo
.\gradlew.bat runOrderScenarioDemo
.\gradlew.bat runSseClientDemo
.\gradlew.bat runServer
.\gradlew.bat test
```

## API Examples

After starting the server:

```powershell
Invoke-RestMethod http://localhost:8080/catalog
```

Create an order:

```powershell
Invoke-RestMethod `
  -Method Post `
  -Uri http://localhost:8080/orders `
  -ContentType "application/json" `
  -Body '{
    "customerId": "customer-1",
    "customerName": "Ada Lovelace",
    "customerEmail": "ada@example.com",
    "items": [
      { "productId": "kotlin-book", "quantity": 2 }
    ]
  }'
```

## Useful Links

 * [Ktor Documentation](https://ktor.io/docs/home.html)
 * [Ktor GitHub page](https://github.com/ktorio/ktor)
 * [Ktor Slack chat](https://app.slack.com/client/T09229ZC6/C0A974TJ9). [Request an invite](https://surveys.jetbrains.com/s3/kotlin-slack-sign-up).

## Building & Running
To build or run the project, use one of the following tasks:


| Task | Description |
|------|-------------|
| `./gradlew test`    | Run the tests     |
| `./gradlew build`   | Build the project |
| `./gradlew run`     | Run the server    |
| `./gradlew runServer` | Run the server |
| `./gradlew runKotlinBasicsDemo` | Run the language demo |
| `./gradlew runKotlinSyntaxDemo` | Run the syntax demo |
| `./gradlew runOrderScenarioDemo` | Run the service demo |
| `./gradlew runSseClientDemo` | Run the SSE client demo |

If the server starts successfully, you'll see the following output:
```
2024-12-04 14:32:45.584 [main] INFO  Application - Application started in 0.303 seconds.
2024-12-04 14:32:45.682 [main] INFO  Application - Responding at http://0.0.0.0:8080
```
