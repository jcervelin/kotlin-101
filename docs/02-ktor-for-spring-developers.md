# Ktor For Spring Boot Developers

Run:

```powershell
.\gradlew.bat runServer
Invoke-RestMethod http://localhost:8080/catalog
```

Open these files:

| Spring Boot concept | Ktor file |
|---------------------|-----------|
| application bootstrap | `Main.kt`, `Application.kt` |
| controller | `api/OrderRoutes.kt` |
| request/response DTO | `api/OrderDtos.kt` |
| exception handler | `api/StatusPages.kt` |
| service | `application/OrderService.kt` |
| repository | `infrastructure/InMemoryOrderRepository.kt` |
| integration test | `src/test/kotlin/com/ada/training/OrderApiTest.kt` |

## 1. Application Startup

Spring Boot:

```java
@SpringBootApplication
class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

Ktor:

```kotlin
fun main(args: Array<String>) {
    EngineMain.main(args)
}
```

Spring Boot hides much of the setup behind auto-configuration and component
scanning. Ktor usually makes the setup visible in an `Application` module:

```kotlin
fun Application.module() {
    val catalogRepository = InMemoryCatalogRepository(sampleCatalog())
    val orderRepository = InMemoryOrderRepository()
    val orderService = OrderService(catalogRepository, orderRepository)

    install(ContentNegotiation) {
        json(Json { prettyPrint = true })
    }

    configureStatusPages()
    configureOrderRoutes(orderService)
}
```

## 2. Routes Instead Of Controller Annotations

Spring MVC:

```java
@RestController
@RequestMapping("/orders")
class OrderController {
    @PostMapping
    ResponseEntity<OrderResponse> create(@RequestBody PlaceOrderRequest request) {
        ...
    }
}
```

Ktor:

```kotlin
route("/orders") {
    post {
        val request = call.receive<PlaceOrderRequest>()
        val command = request.toCommand()

        when (val result = orderService.placeOrder(command)) {
            is PlaceOrderResult.Accepted ->
                call.respond(HttpStatusCode.Created, result.order.toResponse())

            is PlaceOrderResult.Rejected ->
                call.respond(HttpStatusCode.UnprocessableEntity, ErrorResponse(result.reason))
        }
    }
}
```

The Ktor route works because of Kotlin trailing lambda syntax:

```kotlin
post({ /* handler */ })
```

```kotlin
post { /* handler */ }
```

Ktor APIs are normal functions designed to read like a routing DSL.

## 3. `ApplicationCall`

Spring MVC splits request data across annotations:

```java
@PathVariable String id
@RequestParam String q
@RequestBody PlaceOrderRequest request
```

Ktor uses `ApplicationCall`, available as `call` inside a route handler:

```kotlin
val id = call.parameters["id"]
val query = call.request.queryParameters["q"]
val request = call.receive<PlaceOrderRequest>()

call.respond(HttpStatusCode.Created, response)
```

Map:

| Spring MVC | Ktor |
|------------|------|
| `@PathVariable` | `call.parameters["id"]` |
| `@RequestParam` | `call.request.queryParameters["q"]` |
| `@RequestBody` | `call.receive<T>()` |
| `ResponseEntity.status(...).body(...)` | `call.respond(status, body)` |

## 4. `ContentNegotiation`

Spring Boot usually configures JSON automatically when Jackson is on the
classpath.

Ktor requires an installed plugin:

```kotlin
install(ContentNegotiation) {
    json(
        Json {
            prettyPrint = true
            isLenient = false
        },
    )
}
```

`ContentNegotiation` converts between HTTP bodies and Kotlin objects:

```text
JSON request body -> call.receive<PlaceOrderRequest>()
OrderResponse -> call.respond(response) -> JSON response body
```

DTO classes use kotlinx serialization:

```kotlin
@Serializable
data class PlaceOrderRequest(
    val customerId: String,
    val customerName: String,
    val customerEmail: String,
    val items: List<OrderItemRequest>,
)
```

## 5. `StatusPages`

Spring MVC:

```java
@ControllerAdvice
class ApiExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<ErrorResponse> handle(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(new ErrorResponse(ex.getMessage()));
    }
}
```

Ktor:

```kotlin
install(StatusPages) {
    exception<IllegalArgumentException> { call, cause ->
        call.respond(
            HttpStatusCode.BadRequest,
            ErrorResponse(cause.message ?: "Invalid request."),
        )
    }
}
```

`StatusPages` maps exceptions and status codes to responses. In this project it
turns domain validation failures from `require(...)` into a `400 Bad Request`
with a JSON body.

Without `StatusPages`, unexpected exceptions become generic server errors.

## 6. DTO Mapping At The Edge

Spring projects often share DTOs between controllers, services, and persistence.
This project keeps HTTP DTOs in `api`.

```kotlin
@Serializable
data class PlaceOrderRequest(...) {
    fun toCommand(): PlaceOrderCommand =
        PlaceOrderCommand(...)
}
```

Flow:

```text
HTTP JSON
    -> PlaceOrderRequest
    -> PlaceOrderCommand
    -> OrderService
    -> Order
    -> OrderResponse
    -> HTTP JSON
```

The service does not depend on Ktor.

## 7. Dependency Wiring

Spring Boot:

```java
@Service
class OrderService { ... }

@Repository
class OrderRepository { ... }
```

Ktor does not require a container:

```kotlin
val catalogRepository = InMemoryCatalogRepository(sampleCatalog())
val orderRepository = InMemoryOrderRepository()
val orderService = OrderService(catalogRepository, orderRepository)
```

For larger services, add DI deliberately:

| Option | Fit |
|--------|-----|
| manual wiring | small services |
| Koin | lightweight Kotlin DI |
| Dagger | compile-time DI |
| Spring integration | reuse Spring infrastructure |

## 8. Tests

Spring Boot:

```java
@SpringBootTest
@AutoConfigureMockMvc
class OrderApiTest { ... }
```

Ktor:

```kotlin
@Test
fun `order can be created through HTTP`() = testApplication {
    application {
        module()
    }

    val response = client.post("/orders") {
        contentType(ContentType.Application.Json)
        setBody("""{ ... }""")
    }

    assertEquals(HttpStatusCode.Created, response.status)
}
```

`testApplication` runs the Ktor pipeline in memory. It does not need an external
server process.

## 9. Spring Boot To Ktor Map

| Spring Boot | Ktor |
|-------------|------|
| `@SpringBootApplication` | `Application.module()` |
| auto-configuration | explicit `install(...)` calls |
| `@RestController` | `routing { ... }` |
| `@GetMapping` / `@PostMapping` | `get { ... }` / `post { ... }` |
| `@ControllerAdvice` | `StatusPages` |
| Jackson starter | `ContentNegotiation` + kotlinx JSON |
| Spring DI container | manual wiring or chosen DI library |
| MockMvc/WebTestClient | Ktor test client |
