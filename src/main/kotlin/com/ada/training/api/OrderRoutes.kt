package com.ada.training.api

import com.ada.training.application.OrderService
import com.ada.training.application.PlaceOrderResult
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

/**
 * Registers HTTP endpoints for the order use case.
 *
 * Spring MVC equivalent: a `@RestController` with `@GetMapping` and
 * `@PostMapping` methods.
 *
 * Ktor routes are executable Kotlin code. `routing`, `route`, `get`, and `post`
 * are functions that receive lambdas; because the lambda is the last argument,
 * Kotlin allows the block to be written outside the parentheses.
 */
fun Application.configureOrderRoutes(orderService: OrderService) {
    routing {
        get("/health") {
            call.respond(mapOf("status" to "UP"))
        }

        route("/catalog") {
            get {
                call.respond(orderService.catalog().map { it.toResponse() })
            }
        }

        route("/orders") {
            get {
                call.respond(orderService.listOrders().map { it.toResponse() })
            }

            get("/{id}") {
                val id = call.parameters["id"]
                    ?: return@get call.respond(HttpStatusCode.BadRequest, ErrorResponse("Missing order id."))

                val order = orderService.findOrder(id)
                    ?: return@get call.respond(HttpStatusCode.NotFound, ErrorResponse("Order not found."))

                call.respond(order.toResponse())
            }

            post {
                when (val result = orderService.placeOrder(call.receive<PlaceOrderRequest>().toCommand())) {
                    is PlaceOrderResult.Accepted ->
                        call.respond(HttpStatusCode.Created, result.order.toResponse())

                    is PlaceOrderResult.Rejected ->
                        call.respond(HttpStatusCode.UnprocessableEntity, ErrorResponse(result.reason))
                }
            }
        }
    }
}
