# Kotlin For Java Developers

Run:

```powershell
.\gradlew.bat runKotlinSyntaxDemo
.\gradlew.bat runKotlinBasicsDemo
```

Open these files while reading:

| Kotlin feature | File |
|----------------|------|
| companion object, value class | `domain/Email.kt` |
| expression-bodied functions, trailing lambdas | `playground/KotlinSyntaxDemo.kt` |
| nullability and Elvis operator | `application/OrderService.kt` |
| data classes and init blocks | `domain/Money.kt`, `domain/Order.kt` |
| sealed interfaces | `application/OrderService.kt` |
| extension functions | `api/OrderDtos.kt`, `playground/KotlinSyntaxDemo.kt` |

## 1. Companion Object

Kotlin has no `static` keyword for methods on a class.

Java:

```java
public final class Email {
    private final String value;

    private Email(String value) {
        this.value = value;
    }

    public static Email parse(String raw) {
        String normalized = raw.trim().toLowerCase();
        if (!normalized.contains("@")) {
            throw new IllegalArgumentException("Email must contain @.");
        }
        return new Email(normalized);
    }
}
```

Kotlin:

```kotlin
@JvmInline
value class Email private constructor(val value: String) {
    companion object {
        fun parse(raw: String): Email {
            val normalized = raw.trim().lowercase()
            require("@" in normalized) { "Email must contain @." }
            return Email(normalized)
        }
    }
}
```

Equivalent call:

```java
Email.parse(raw)
```

```kotlin
Email.parse(raw)
```

`companion object` is the Kotlin place for factory functions and constants that
belong to a type.

## 2. Value Classes

Java needs a class or record to avoid passing raw strings everywhere.

Java:

```java
public record ProductId(String value) {}
```

Kotlin:

```kotlin
@JvmInline
value class ProductId(val value: String)
```

`@JvmInline value class` wraps one value. It gives the compiler a different type
from `String`, while avoiding wrapper allocation in many JVM call sites.

```kotlin
fun findById(id: ProductId): Product?

findById(ProductId("kotlin-book"))
```

This prevents accidental calls such as passing a `CustomerId` where a
`ProductId` is required.

## 3. Expression-Bodied Functions

Java methods always use a body.

Java:

```java
String applyStandardDiscount(long cents) {
    return cents - 500;
}
```

Kotlin can use the same style:

```kotlin
fun applyStandardDiscount(cents: Long): Long {
    return cents - 500
}
```

Or an expression body:

```kotlin
fun applyStandardDiscount(cents: Long): Long =
    cents - 500
```

When the return type is obvious, Kotlin can infer it:

```kotlin
fun applyStandardDiscount(cents: Long) =
    cents - 500
```

Project example: `playground/KotlinSyntaxDemo.kt`.

## 4. Functions As Values

Java has functional interfaces:

```java
long calculateTotal(String productId, int quantity, LongUnaryOperator discount) {
    long basePrice = 4590;
    return discount.applyAsLong(basePrice * quantity);
}
```

Kotlin has function types directly:

```kotlin
fun calculateTotal(
    productId: String,
    quantity: Int,
    discount: (Long) -> Long,
): Long {
    val basePrice = 4_590
    return discount(basePrice * quantity)
}
```

Normal call:

```kotlin
calculateTotal("kotlin-book", 2, ::applyStandardDiscount)
```

Lambda as argument:

```kotlin
calculateTotal("kotlin-book", 2, { cents ->
    cents - 500
})
```

## 5. Trailing Lambda Syntax

If the last argument of a function is a function, Kotlin allows the lambda to be
placed outside the parentheses.

Both calls are equivalent:

```kotlin
calculateTotal("ktor-course", 1, { cents ->
    cents - 1_000
})
```

```kotlin
calculateTotal("ktor-course", 1) { cents ->
    cents - 1_000
}
```

If the lambda is the only argument, the parentheses can disappear:

```kotlin
listOf(1, 2, 3).map({ number -> number * 2 })
```

```kotlin
listOf(1, 2, 3).map { number -> number * 2 }
```

This syntax is the reason Ktor can look like a DSL:

```kotlin
routing {
    get("/catalog") {
        call.respond(orderService.catalog())
    }
}
```

`routing`, `get`, and `respond` are function calls, not annotations.

## 6. Named And Default Arguments

Java needs overloads, builders, or telescoping constructors.

Java:

```java
new Money(4590, "EUR");
```

Kotlin:

```kotlin
data class Money(val cents: Long, val currency: String = "EUR")

Money(4590)
Money(cents = 4590, currency = "EUR")
Money(currency = "EUR", cents = 4590)
```

Named arguments make long constructor calls readable without a builder.

Project example:

```kotlin
val order = Order(
    id = OrderId.new(),
    customer = customer,
    lines = lines,
)
```

## 7. Nullability In The Type System

Java expresses absence through conventions, `null`, or `Optional`.

Java:

```java
Optional<Product> findById(ProductId id);
```

Kotlin:

```kotlin
fun findById(id: ProductId): Product?
```

Usage:

```kotlin
val product = catalogRepository.findById(ProductId(item.productId))
    ?: return PlaceOrderResult.Rejected("Unknown product: ${item.productId}")
```

Operators:

| Kotlin | Meaning |
|--------|---------|
| `Product` | cannot be null |
| `Product?` | can be null |
| `?.` | call only if not null |
| `?:` | fallback when null |
| `!!` | throw if null; avoid in normal code |

## 8. Data Classes

Java records:

```java
public record OrderItemCommand(String productId, int quantity) {}
```

Kotlin data class:

```kotlin
data class OrderItemCommand(
    val productId: String,
    val quantity: Int,
)
```

Generated:

```text
equals
hashCode
toString
copy
componentN functions
```

Kotlin data classes can also include validation and derived values:

```kotlin
data class OrderLine(
    val product: Product,
    val quantity: Int,
) {
    init {
        require(quantity > 0) { "Quantity must be positive." }
    }

    val subtotal: Money = product.price * quantity
}
```

## 9. Extension Functions

Java uses static helper methods:

```java
OrderResponse response = OrderMappers.toResponse(order);
```

Kotlin can keep the helper as a function and call it like a method:

```kotlin
fun Order.toResponse(): OrderResponse =
    OrderResponse(
        id = id.value,
        customerName = customer.name,
        customerEmail = customer.email.value,
        lines = lines.map { it.toResponse() },
        totalCents = total.cents,
        currency = total.currency,
    )

val response = order.toResponse()
```

An extension function does not modify the original class. It is resolved
statically by the compiler.

## 10. Sealed Interfaces

Java can model a closed hierarchy with sealed interfaces in recent versions, but
Spring code often uses exceptions or status flags for use-case outcomes.

Kotlin:

```kotlin
sealed interface PlaceOrderResult {
    data class Accepted(val order: Order) : PlaceOrderResult
    data class Rejected(val reason: String) : PlaceOrderResult
}
```

Usage:

```kotlin
when (val result = orderService.placeOrder(command)) {
    is PlaceOrderResult.Accepted -> result.order
    is PlaceOrderResult.Rejected -> error(result.reason)
}
```

The compiler checks that all known cases are handled.

## 11. Infix Functions

Kotlin allows a single-argument member or extension function to be called without
dot and parentheses when marked `infix`.

```kotlin
infix fun String.belongsTo(collection: String): Boolean =
    isNotBlank() && collection.isNotBlank()

"kotlin-book" belongsTo "catalog"
```

Use infix functions sparingly. They are useful when the call reads like a domain phrase.

## 12. Kotlin Features In The Project

| Feature | Project Example |
|---------|-----------------|
| `companion object` | `Email.parse(...)` |
| value class | `Email`, `ProductId`, `OrderId` |
| expression body | `applyStandardDiscount`, `toResponse` |
| trailing lambda | `calculateTotal(...) { ... }`, Ktor routes |
| named/default arguments | `Money(cents = 4590)` |
| null-safe return | `Product?` |
| Elvis operator | `?: return PlaceOrderResult.Rejected(...)` |
| data class | `Order`, `Product`, DTOs |
| extension function | `Order.toResponse()` |
| sealed interface | `PlaceOrderResult` |
