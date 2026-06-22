package com.ada.training.domain

data class Money(val cents: Long, val currency: String = "EUR") {
    init {
        require(cents >= 0) { "Money cannot be negative." }
        require(currency.length == 3) { "Currency must use ISO-4217 format." }
    }

    operator fun plus(other: Money): Money {
        require(currency == other.currency) { "Cannot add different currencies." }
        return copy(cents = cents + other.cents)
    }

    operator fun times(quantity: Int): Money {
        require(quantity > 0) { "Quantity must be positive." }
        return copy(cents = cents * quantity)
    }

    override fun toString(): String = "${cents / 100}.${(cents % 100).toString().padStart(2, '0')} $currency"
}
