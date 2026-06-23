package com.ada.training.integration.sse

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.sse.SSE
import io.ktor.client.plugins.sse.sse
import kotlinx.coroutines.flow.collect
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlin.time.Duration.Companion.seconds

class SpringFluxSseClient(
    private val httpClient: HttpClient = defaultHttpClient(),
    private val json: Json = defaultJson,
) {
    suspend fun consumeInventoryUpdates(
        url: String,
        onUpdate: suspend (InventoryUpdateEvent) -> Unit,
        onInvalidPayload: suspend (String, Throwable) -> Unit = { payload, cause ->
            println("Skipping invalid SSE payload: $payload")
            println("Reason: ${cause.message}")
        },
    ) {
        httpClient.sse(urlString = url) {
            incoming.collect { event ->
                val payload = event.data ?: return@collect
                val update = decodeInventoryUpdate(payload, onInvalidPayload) ?: return@collect

                onUpdate(update)
            }
        }
    }

    private suspend fun decodeInventoryUpdate(
        payload: String,
        onInvalidPayload: suspend (String, Throwable) -> Unit,
    ): InventoryUpdateEvent? =
        try {
            json.decodeFromString<InventoryUpdateEvent>(payload)
        } catch (cause: SerializationException) {
            onInvalidPayload(payload, cause)
            null
        } catch (cause: IllegalArgumentException) {
            onInvalidPayload(payload, cause)
            null
        }

    companion object {
        val defaultJson: Json =
            Json {
                ignoreUnknownKeys = true
                isLenient = true
            }

        fun defaultHttpClient(): HttpClient =
            HttpClient(CIO) {
                install(SSE) {
                    maxReconnectionAttempts = 3
                    reconnectionTime = 2.seconds
                }
            }
    }
}
