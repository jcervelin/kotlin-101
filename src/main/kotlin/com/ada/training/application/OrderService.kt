package com.ada.training.application

import com.ada.training.domain.Customer
import com.ada.training.domain.CustomerId
import com.ada.training.domain.Email
import com.ada.training.domain.Order
import com.ada.training.domain.OrderId
import com.ada.training.domain.OrderLine
import com.ada.training.domain.ProductId

class OrderService(
    private val catalogRepository: CatalogRepository,
    private val orderRepository: OrderRepository,
) {
    fun catalog() = catalogRepository.findAll()

    fun placeOrder(command: PlaceOrderCommand): PlaceOrderResult {
        val customer = Customer(
            id = CustomerId(command.customerId),
            name = command.customerName.trim(),
            email = Email.parse(command.customerEmail),
        )

        val lines = command.items.map { item ->
            val product = catalogRepository.findById(ProductId(item.productId))
                ?: return PlaceOrderResult.Rejected("Unknown product: ${item.productId}")
            OrderLine(product, item.quantity)
        }

        val order = Order(
            id = OrderId.new(),
            customer = customer,
            lines = lines,
        )

        return PlaceOrderResult.Accepted(orderRepository.save(order))
    }

    fun findOrder(id: String): Order? =
        orderRepository.findById(OrderId(id))

    fun listOrders(): List<Order> =
        orderRepository.findAll()
}

data class PlaceOrderCommand(
    val customerId: String,
    val customerName: String,
    val customerEmail: String,
    val items: List<OrderItemCommand>,
)

data class OrderItemCommand(
    val productId: String,
    val quantity: Int,
)

sealed interface PlaceOrderResult {
    data class Accepted(val order: Order) : PlaceOrderResult
    data class Rejected(val reason: String) : PlaceOrderResult
}
