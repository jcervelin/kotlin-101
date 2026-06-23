package com.ada.training.integration.sse

import kotlinx.coroutines.runBlocking

fun main(args: Array<String>) {
    val url = args.firstOrNull() ?: "http://localhost:8081/inventory/stream"
    val client = SpringFluxSseClient()

    println("Consuming Spring WebFlux SSE endpoint: $url")
    println("Stop the process with Ctrl+C when you are done.")

    runBlocking {
        client.consumeInventoryUpdates(
            url = url,
            onUpdate = { update ->
                println(
                    "Inventory update: productId=${update.productId}, " +
                        "availableQuantity=${update.availableQuantity}, status=${update.status}",
                )
            },
        )
    }
}
