package com.ada.training.domain

data class Product(
    val id: ProductId,
    val name: String,
    val price: Money,
)

@JvmInline
value class ProductId(val value: String)
