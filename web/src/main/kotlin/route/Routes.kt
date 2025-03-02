package ru.alexredby.stocktaking.route

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.server.application.Application
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.application
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
    // TODO: why i need application here?
    val tarkovService: TarkovService by application.inject()

    get("/barter-tree") {
        val res = tarkovService.getCraftingTree()
        logger.info { "Successfully created tree!" }
        call.respond(res)
    }

    get("/crafting-tree") {
        val res = tarkovService.getReactFlowTree()
        logger.info { "Successfully created tree for react-flow!" }
        call.respond(res)
    }

    get("/tool-names") {
        call.respond(tarkovService.getAllToolNames())
    }
}
