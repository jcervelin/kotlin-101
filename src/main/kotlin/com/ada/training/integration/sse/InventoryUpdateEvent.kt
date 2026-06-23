package com.ada.training.integration.sse

import kotlinx.serialization.Serializable

@Serializable
data class InventoryUpdateEvent(
    val productId: String,
    val availableQuantity: Int,
    val status: String,
)
