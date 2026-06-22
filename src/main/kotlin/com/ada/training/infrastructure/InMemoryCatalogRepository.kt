package com.ada.training.infrastructure

import com.ada.training.application.CatalogRepository
import com.ada.training.domain.Product
import com.ada.training.domain.ProductId

class InMemoryCatalogRepository(products: List<Product>) : CatalogRepository {
    private val productsById = products.associateBy { it.id }

    override fun findById(id: ProductId): Product? =
        productsById[id]

    override fun findAll(): List<Product> =
        productsById.values.sortedBy { it.name }
}
