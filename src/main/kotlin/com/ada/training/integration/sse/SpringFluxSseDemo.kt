package com.ada.training.integration.sse

import io.ktor.client.plugins.sse.SSEClientException
import kotlinx.coroutines.runBlocking

fun main(args: Array<String>) {
    val url = args.firstOrNull() ?: "http://localhost:8080/training/inventory/stream"
    val client = SpringFluxSseClient()

    println("Consuming Spring WebFlux SSE endpoint: $url")
    println("Stop the process with Ctrl+C when you are done.")

    try {
        runBlocking {
            client.consumeInventoryUpdates(
                url = url,
                maxEvents = 3,
                onUpdate = { update ->
                    println(
                        "Inventory update: productId=${update.productId}, " +
                            "availableQuantity=${update.availableQuantity}, status=${update.status}",
                    )
                },
            )
        }
    } catch (cause: SSEClientException) {
        println()
        println("Could not connect to the SSE endpoint.")
        println("Checked URL: $url")
        println(
            "Start the service that exposes this stream, or pass the correct endpoint URL " +
                "as the first program argument.",
        )
        println()
        println("For the local training example, run this in another terminal first:")
        println(".\\gradlew.bat runServer")
    } catch (cause: java.net.ConnectException) {
        println()
        println("Connection refused by the SSE endpoint.")
        println("Checked URL: $url")
        println("Make sure the server is running and the port/path are correct.")
    }
}
