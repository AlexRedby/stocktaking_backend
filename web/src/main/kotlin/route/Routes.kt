package ru.alexredby.stocktaking.route

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import org.koin.ktor.ext.inject
import ru.alexredby.stocktaking.dto.ApiErrorResponse
import ru.alexredby.stocktaking.service.TarkovService
import ru.alexredby.stocktaking.service.TarkovItemNotFoundException
import kotlin.coroutines.cancellation.CancellationException

private val logger = KotlinLogging.logger {}

fun Application.configureRouting() {
    routing {
        getTarkovRoutes()
    }
}

fun Route.getTarkovRoutes() {
    val tarkovService: TarkovService by inject()
    installTarkovApiRoutes(tarkovService)
}

internal fun Route.installTarkovApiRoutes(tarkovService: TarkovService) {
    route("/api") {
        get("/craftable-items") {
            // TODO: Use delegated property here
            val filter = call.queryParameters["filter"]
            val items = try {
                tarkovService.getItems(filter)
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                logger.error(exception) { "Failed to search craftable items" }
                call.respondInternalError()
                return@get
            }
            call.respond(items)
        }

        get("/crafting-tree") {
            val targetItemId = call.queryParameters[TARGET_ITEM_ID_PARAMETER]
            if (targetItemId == null) {
                call.respondApiError(
                    status = HttpStatusCode.BadRequest,
                    code = "missing_parameter",
                    message = "Query parameter '$TARGET_ITEM_ID_PARAMETER' is required",
                    parameter = TARGET_ITEM_ID_PARAMETER,
                )
                return@get
            }
            if (!TARKOV_ITEM_ID.matches(targetItemId)) {
                call.respondApiError(
                    status = HttpStatusCode.BadRequest,
                    code = "invalid_parameter",
                    message = "Query parameter '$TARGET_ITEM_ID_PARAMETER' must be a 24-character hexadecimal ID",
                    parameter = TARGET_ITEM_ID_PARAMETER,
                )
                return@get
            }

            val res = try {
                tarkovService.getReactFlowTree(targetItemId.lowercase())
            } catch (exception: TarkovItemNotFoundException) {
                call.respondApiError(
                    status = HttpStatusCode.NotFound,
                    code = "item_not_found",
                    message = "No item exists for the requested ID",
                    parameter = TARGET_ITEM_ID_PARAMETER,
                )
                return@get
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                logger.error(exception) { "Failed to build the crafting tree" }
                call.respondInternalError()
                return@get
            }

            logger.info { "Successfully created tree for react-flow!" }
            call.respond(res)
        }

        get("/tool-names") {
            call.respond(tarkovService.getAllToolNames())
        }
    }
}

private suspend fun ApplicationCall.respondInternalError() {
    respondApiError(
        status = HttpStatusCode.InternalServerError,
        code = "internal_error",
        message = "The request could not be completed",
    )
}

private suspend fun ApplicationCall.respondApiError(
    status: HttpStatusCode,
    code: String,
    message: String,
    parameter: String? = null,
) {
    respond(status, ApiErrorResponse(code, message, parameter))
}

private const val TARGET_ITEM_ID_PARAMETER = "target_item_id"
private val TARKOV_ITEM_ID = Regex("^[0-9a-fA-F]{24}$")
