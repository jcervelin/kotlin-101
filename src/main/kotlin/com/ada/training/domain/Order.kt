package com.ada.training.domain

import java.util.UUID

data class Order(
    val id: OrderId,
    val customer: Customer,
    val lines: List<OrderLine>,
    val status: OrderStatus = OrderStatus.Accepted,
) {
    init {
        require(lines.isNotEmpty()) { "An order must contain at least one line." }
    }

    val total: Money =
        lines.map { it.subtotal }.reduce(Money::plus)
}

data class OrderLine(
    val product: Product,
    val quantity: Int,
) {
    init {
        require(quantity > 0) { "Quantity must be positive." }
    }

    val subtotal: Money = product.price * quantity
}

@JvmInline
value class OrderId(val value: String) {
    companion object {
        fun new(): OrderId = OrderId(UUID.randomUUID().toString())
    }
}

sealed interface OrderStatus {
    data object Accepted : OrderStatus
    data class Rejected(val reason: String) : OrderStatus
}
