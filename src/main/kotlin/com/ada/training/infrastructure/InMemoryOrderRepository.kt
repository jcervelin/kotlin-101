package com.ada.training.infrastructure

import com.ada.training.application.OrderRepository
import com.ada.training.domain.Order
import com.ada.training.domain.OrderId
import java.util.concurrent.ConcurrentHashMap

class InMemoryOrderRepository : OrderRepository {
    private val orders = ConcurrentHashMap<OrderId, Order>()

    override fun save(order: Order): Order {
        orders[order.id] = order
        return order
    }

    override fun findById(id: OrderId): Order? =
        orders[id]

    override fun findAll(): List<Order> =
        orders.values.sortedBy { it.customer.name }
}
