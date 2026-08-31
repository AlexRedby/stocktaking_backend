package ru.alexredby.stocktaking.route

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import org.koin.ktor.ext.inject
import ru.alexredby.stocktaking.service.TarkovService

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
            call.respond(tarkovService.getItems(filter))
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

            val res = tarkovService.getReactFlowTree(targetItemId.lowercase())

            logger.info { "Successfully created tree for react-flow!" }
            call.respond(res)
        }

        get("/tool-names") {
            call.respond(tarkovService.getAllToolNames())
        }
    }
}

internal const val TARGET_ITEM_ID_PARAMETER = "target_item_id"
private val TARKOV_ITEM_ID = Regex("^[0-9a-fA-F]{24}$")
