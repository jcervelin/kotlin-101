package com.ada.training.infrastructure

import com.ada.training.domain.Money
import com.ada.training.domain.Product
import com.ada.training.domain.ProductId

fun sampleCatalog(): List<Product> =
    listOf(
        Product(ProductId("kotlin-book"), "Kotlin in Action", Money(4590)),
        Product(ProductId("ktor-course"), "Ktor Production Course", Money(12900)),
        Product(ProductId("spring-migration"), "Spring to Ktor Migration Guide", Money(7900)),
    )
