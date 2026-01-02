package ru.alexredby.stocktaking.route

import io.github.oshai.kotlinlogging.KotlinLogging
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

    route("/api") {
        get("/craftable-items") {
            // TODO: Use delegated property here
            val filter = call.queryParameters["filter"]
            val items = tarkovService.getItems(filter)
            call.respond(items)
        }

        get("/crafting-tree") {
            val targetItemId = call.queryParameters["target_item_id"]
            val res = tarkovService.getReactFlowTree(targetItemId)
            logger.info { "Successfully created tree for react-flow!" }
            call.respond(res)
        }

        get("/tool-names") {
            call.respond(tarkovService.getAllToolNames())
        }
    }
}
