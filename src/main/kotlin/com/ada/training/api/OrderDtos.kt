package com.ada.training.api

import com.ada.training.application.OrderItemCommand
import com.ada.training.application.PlaceOrderCommand
import com.ada.training.domain.Order
import com.ada.training.domain.Product
import kotlinx.serialization.Serializable

@Serializable
data class ProductResponse(
    val id: String,
    val name: String,
    val priceCents: Long,
    val currency: String,
)

@Serializable
data class PlaceOrderRequest(
    val customerId: String,
    val customerName: String,
    val customerEmail: String,
    val items: List<OrderItemRequest>,
) {
    fun toCommand(): PlaceOrderCommand =
        PlaceOrderCommand(
            customerId = customerId,
            customerName = customerName,
            customerEmail = customerEmail,
            items = items.map { it.toCommand() },
        )
}

@Serializable
data class OrderItemRequest(
    val productId: String,
    val quantity: Int,
) {
    fun toCommand(): OrderItemCommand =
        OrderItemCommand(productId, quantity)
}

@Serializable
data class OrderResponse(
    val id: String,
    val customerName: String,
    val customerEmail: String,
    val lines: List<OrderLineResponse>,
    val totalCents: Long,
    val currency: String,
)

@Serializable
data class OrderLineResponse(
    val productId: String,
    val productName: String,
    val quantity: Int,
    val subtotalCents: Long,
)

@Serializable
data class ErrorResponse(val message: String)

fun Product.toResponse(): ProductResponse =
    ProductResponse(
        id = id.value,
        name = name,
        priceCents = price.cents,
        currency = price.currency,
    )

fun Order.toResponse(): OrderResponse =
    OrderResponse(
        id = id.value,
        customerName = customer.name,
        customerEmail = customer.email.value,
        lines = lines.map {
            OrderLineResponse(
                productId = it.product.id.value,
                productName = it.product.name,
                quantity = it.quantity,
                subtotalCents = it.subtotal.cents,
            )
        },
        totalCents = total.cents,
        currency = total.currency,
    )
