package com.ada.training.domain

data class Customer(
    val id: CustomerId,
    val name: String,
    val email: Email,
)

@JvmInline
value class CustomerId(val value: String)
