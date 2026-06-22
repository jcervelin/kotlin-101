package com.ada.training.playground

fun main() {
    println("Expression body: ${formatCustomerName("ada", "lovelace")}")

    val explicitCall = calculateTotal("kotlin-book", 2, ::applyStandardDiscount)
    val trailingLambdaCall = calculateTotal("ktor-course", 1) { cents ->
        cents - 1_000
    }

    println("Function argument: $explicitCall cents")
    println("Trailing lambda: $trailingLambdaCall cents")

    val message = "ada@example.com".maskEmail()
    println("Extension function: $message")

    println(createLabel(text = "paid", prefix = "ORDER"))
    println("kotlin-book" belongsTo "catalog")
}

fun formatCustomerName(firstName: String, lastName: String): String =
    "${firstName.trim()} ${lastName.trim()}".lowercase().replaceFirstChar { it.titlecase() }

fun calculateTotal(
    productId: String,
    quantity: Int,
    discount: (Long) -> Long,
): Long {
    val basePrice = when (productId) {
        "kotlin-book" -> 4_590L
        "ktor-course" -> 12_900L
        else -> 7_900L
    }
    return discount(basePrice * quantity)
}

fun applyStandardDiscount(cents: Long): Long =
    cents - 500

fun String.maskEmail(): String =
    replaceBefore("@", "***")

fun createLabel(prefix: String = "ITEM", text: String): String =
    "$prefix-${text.uppercase()}"

infix fun String.belongsTo(collection: String): Boolean =
    isNotBlank() && collection.isNotBlank()
