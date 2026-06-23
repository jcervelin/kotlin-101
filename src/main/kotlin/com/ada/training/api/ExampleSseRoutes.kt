package com.ada.training.api

import io.ktor.http.ContentType
import io.ktor.server.application.Application
import io.ktor.server.response.respondTextWriter
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json

fun Application.configureExampleSseRoutes() {
    routing {
        get("/training/inventory/stream") {
            call.respondTextWriter(contentType = ContentType.Text.EventStream) {
                sampleInventoryUpdates().forEach { update ->
                    write("data: ${Json.encodeToString(update)}\n\n")
                    flush()
                    delay(1_000)
                }
            }
        }
    }
}

private fun sampleInventoryUpdates(): List<InventoryUpdateEventResponse> =
    listOf(
        InventoryUpdateEventResponse("kotlin-book", 10, "IN_STOCK"),
        InventoryUpdateEventResponse("ktor-guide", 0, "OUT_OF_STOCK"),
        InventoryUpdateEventResponse("coroutines-course", 3, "LOW_STOCK"),
    )
