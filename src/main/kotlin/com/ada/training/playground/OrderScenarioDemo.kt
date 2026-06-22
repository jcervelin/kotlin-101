package com.ada.training.playground

import com.ada.training.application.OrderItemCommand
import com.ada.training.application.OrderService
import com.ada.training.application.PlaceOrderCommand
import com.ada.training.application.PlaceOrderResult
import com.ada.training.infrastructure.InMemoryCatalogRepository
import com.ada.training.infrastructure.InMemoryOrderRepository
import com.ada.training.infrastructure.sampleCatalog

fun main() {
    val service = OrderService(
        catalogRepository = InMemoryCatalogRepository(sampleCatalog()),
        orderRepository = InMemoryOrderRepository(),
    )

    println("Order scenario demo")
    println("-------------------")
    println("Catalog:")
    service.catalog().forEach { product ->
        println("- ${product.id.value}: ${product.name} (${product.price})")
    }

    println()
    println("Choose a product id:")
    val productId = readlnOrNull().orEmpty()

    println("Quantity:")
    val quantity = readlnOrNull()?.toIntOrNull() ?: 1

    val result = service.placeOrder(
        PlaceOrderCommand(
            customerId = "customer-1",
            customerName = "Ada Lovelace",
            customerEmail = "ada@example.com",
            items = listOf(OrderItemCommand(productId, quantity)),
        ),
    )

    when (result) {
        is PlaceOrderResult.Accepted -> {
            val order = result.order
            println("Accepted order ${order.id.value}")
            println("Total: ${order.total}")
        }

        is PlaceOrderResult.Rejected ->
            println("Rejected: ${result.reason}")
    }
}
