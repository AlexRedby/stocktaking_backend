package ru.alexredby.stocktaking.route

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.plugins.statuspages.exception
import io.ktor.server.response.respond
import ru.alexredby.stocktaking.client.tarkov.dev.TarkovDevResponseException
import ru.alexredby.stocktaking.client.tarkov.dev.TarkovDevUnavailableException
import ru.alexredby.stocktaking.dto.ApiErrorResponse
import ru.alexredby.stocktaking.dto.HealthResponse
import ru.alexredby.stocktaking.service.ReadinessCheckException
import ru.alexredby.stocktaking.service.TarkovItemNotFoundException
import kotlin.coroutines.cancellation.CancellationException

private val logger = KotlinLogging.logger {}

fun Application.configureErrorHandling() {
    install(StatusPages) {
        exception<TarkovItemNotFoundException> { call, _ ->
            call.respondApiError(
                status = HttpStatusCode.NotFound,
                code = "item_not_found",
                message = "No item exists for the requested ID",
                parameter = TARGET_ITEM_ID_PARAMETER,
            )
        }
        exception<TarkovDevUnavailableException> { call, _ ->
            call.respondApiError(
                status = HttpStatusCode.ServiceUnavailable,
                code = "upstream_unavailable",
                message = "Tarkov.dev is temporarily unavailable",
            )
        }
        exception<TarkovDevResponseException> { call, _ ->
            call.respondApiError(
                status = HttpStatusCode.BadGateway,
                code = "upstream_error",
                message = "Tarkov.dev returned an invalid response",
            )
        }
        exception<ReadinessCheckException> { call, cause ->
            logger.warn(cause) { "Readiness check failed" }
            call.respond(HttpStatusCode.ServiceUnavailable, HealthResponse("not_ready"))
        }
        exception<Throwable> { call, cause ->
            if (cause is CancellationException) {
                throw cause
            }

            logger.error(cause) { "Request failed" }
            call.respondApiError(
                status = HttpStatusCode.InternalServerError,
                code = "internal_error",
                message = "The request could not be completed",
            )
        }
    }
}

internal suspend fun ApplicationCall.respondApiError(
    status: HttpStatusCode,
    code: String,
    message: String,
    parameter: String? = null,
) {
    respond(status, ApiErrorResponse(code, message, parameter))
}
