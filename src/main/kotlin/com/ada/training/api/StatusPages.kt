package com.ada.training.api

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond

/**
 * Registers Ktor's StatusPages plugin.
 *
 * Spring MVC equivalent: `@ControllerAdvice` plus `@ExceptionHandler`.
 *
 * StatusPages intercepts exceptions thrown while processing a request and turns
 * them into HTTP responses. Without it, clients would receive generic server
 * errors instead of a structured API response.
 */
fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<IllegalArgumentException> { call, cause ->
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse(cause.message ?: "Invalid request."),
            )
        }
    }
}
