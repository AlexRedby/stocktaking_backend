package ru.alexredby.stocktaking.route

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.http.content.staticResources
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.application
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import org.koin.ktor.ext.inject
import ru.alexredby.stocktaking.client.TarkovDevClient
import ru.alexredby.stocktaking.service.TarkovService
import ru.alexredby.stocktaking.tarkovdev.GraphqlContext
import ru.alexredby.stocktaking.tarkovdev.dto.BarterDto
import ru.alexredby.stocktaking.tarkovdev.dto.ContainedItemDto
import ru.alexredby.stocktaking.tarkovdev.dto.GameMode
import ru.alexredby.stocktaking.tarkovdev.dto.ItemDto

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
        logger.info { "/barter-tree request was made..." }
        val res = tarkovService.getBarterTree()
        logger.info { "/barter-tree res is ready ${res.size}" }

        call.respond(res)
    }
}
