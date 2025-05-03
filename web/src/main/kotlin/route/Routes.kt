package ru.alexredby.stocktaking.route

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.server.application.Application
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import org.koin.ktor.ext.inject
import ru.alexredby.stocktaking.service.TarkovService

private val logger = KotlinLogging.logger {}

fun Application.configureRouting() {
    routing {
        getBarterTree()
    }
}

fun Route.getBarterTree() {
    val tarkovService: TarkovService by inject()

    get("/crafting-tree") {
        val res = tarkovService.getReactFlowTree()
        logger.info { "Successfully created tree for react-flow!" }
        call.respond(res)
    }

    get("/tool-names") {
        call.respond(tarkovService.getAllToolNames())
    }
}
