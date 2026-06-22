package com.ada.training.application

import com.ada.training.domain.Order
import com.ada.training.domain.OrderId
import com.ada.training.domain.Product
import com.ada.training.domain.ProductId

interface CatalogRepository {
    fun findById(id: ProductId): Product?
    fun findAll(): List<Product>
}

interface OrderRepository {
    fun save(order: Order): Order
    fun findById(id: OrderId): Order?
    fun findAll(): List<Order>
}
