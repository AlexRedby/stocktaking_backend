package ru.alexredby.stocktaking.route

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.server.application.Application
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import org.jooq.DSLContext
import org.koin.ktor.ext.inject
import ru.alexredby.stocktaking.dto.ReactFlowNodeData
import ru.alexredby.stocktaking.model.tables.references.ITEM
import ru.alexredby.stocktaking.service.TarkovService
import kotlin.random.Random

private val logger = KotlinLogging.logger {}

fun Application.configureRouting() {
    routing {
        getTarkovRoutes()
    }
}

fun Route.getTarkovRoutes() {
    val tarkovService: TarkovService by inject()
    val jooq: DSLContext by inject()

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

        get("/test/insert-item") {
            jooq.insertInto(ITEM)
                .columns(ITEM.ID, ITEM.NAME, ITEM.SHORT_NAME, ITEM.IMAGE_LINK)
                .values(
                    Random.nextInt(),
                    "Item name № ${Random.nextInt()}",
                    "Item",
                    "https://upload.wikimedia.org/wikipedia/en/6/63/Feels_good_man.jpg"
                ).execute()
        }

        get("/test/read-all-items") {
            try {
                val items = jooq.selectFrom(ITEM).fetch().map {
                    ReactFlowNodeData(
                        it.shortName!!,
                        it.name!!,
                        it.shortName!!,
                        it.imageLink!!,
                        emptyList()
                    )
                }
                call.respond(items)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
