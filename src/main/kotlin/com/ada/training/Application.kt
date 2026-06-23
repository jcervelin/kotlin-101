package com.ada.training

import com.ada.training.api.configureOrderRoutes
import com.ada.training.api.configureExampleSseRoutes
import com.ada.training.api.configureStatusPages
import com.ada.training.application.OrderService
import com.ada.training.infrastructure.InMemoryCatalogRepository
import com.ada.training.infrastructure.InMemoryOrderRepository
import com.ada.training.infrastructure.sampleCatalog
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import kotlinx.serialization.json.Json

/**
 * Ktor application module.
 *
 * Spring Boot equivalent: the part normally hidden behind `@SpringBootApplication`,
 * auto-configuration, and component scanning.
 *
 * Ktor keeps this explicit: create dependencies, install HTTP plugins, then
 * register routes.
 */
fun Application.module() {
    val catalogRepository = InMemoryCatalogRepository(sampleCatalog())
    val orderRepository = InMemoryOrderRepository()
    val orderService = OrderService(catalogRepository, orderRepository)

    install(ContentNegotiation) {
        json(
            Json {
                prettyPrint = true
                isLenient = false
            },
        )
    }

    configureStatusPages()
    configureOrderRoutes(orderService)
    configureExampleSseRoutes()
}
