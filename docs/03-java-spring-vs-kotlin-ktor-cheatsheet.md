# Java/Spring vs Kotlin/Ktor Reference

## Kotlin Syntax Java Does Not Have

| Kotlin | Java Equivalent |
|--------|-----------------|
| `fun f(x: Int) = x * 2` | method with `return` |
| `Product?` | `Optional<Product>` or nullable convention |
| `value class ProductId(val value: String)` | record/class wrapper |
| `companion object` | static members/factory methods |
| `data class` | record plus extra generated `copy` |
| `fun Order.toResponse()` | static helper method |
| `calculateTotal(...) { ... }` | lambda passed as last argument |
| `Money(cents = 4590)` | builder or named constructor pattern |
| `sealed interface` + `when` | sealed hierarchy + switch/pattern matching |
| `infix fun` | method call with normal dot syntax |

## Spring Boot To Ktor

| Spring Boot | Ktor |
|-------------|------|
| `@SpringBootApplication` | `Application.module()` |
| component scan | explicit object creation or chosen DI |
| starter auto-configuration | explicit plugin installation |
| `@RestController` | `routing { ... }` |
| `@GetMapping` | `get { ... }` |
| `@PostMapping` | `post { ... }` |
| `@RequestBody` | `call.receive<T>()` |
| `ResponseEntity` | `call.respond(status, body)` |
| `@ControllerAdvice` | `StatusPages` |
| Jackson starter | `ContentNegotiation` + kotlinx JSON |
| `@SpringBootTest` | `testApplication` |

## Project Commands

| Command | Purpose |
|---------|---------|
| `.\gradlew.bat runKotlinSyntaxDemo` | Kotlin syntax differences |
| `.\gradlew.bat runKotlinBasicsDemo` | null-safety and value parsing |
| `.\gradlew.bat runOrderScenarioDemo` | application service without HTTP |
| `.\gradlew.bat runServer` | Ktor server |
| `.\gradlew.bat test` | API tests |

## Request Flow

Spring Boot:

```text
HTTP request
    -> DispatcherServlet
    -> controller method
    -> service bean
    -> repository bean
    -> ResponseEntity
```

Ktor:

```text
HTTP request
    -> engine
    -> installed plugins
    -> route lambda
    -> service
    -> repository
    -> call.respond(...)
```

## Main Boundary

```text
HTTP JSON
    -> PlaceOrderRequest
    -> PlaceOrderCommand
    -> OrderService
    -> Order
    -> OrderResponse
    -> HTTP JSON
```

The route owns HTTP. The service owns the use case. The domain owns invariants.
