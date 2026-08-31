package ru.alexredby.stocktaking.route

import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import org.koin.ktor.ext.inject
import ru.alexredby.stocktaking.dto.HealthResponse
import ru.alexredby.stocktaking.service.HealthService

fun Route.getHealthRoutes() {
    val healthService: HealthService by inject()
    installHealthRoutes { healthService.checkReadiness() }
}

internal fun Route.installHealthRoutes(readinessCheck: suspend () -> Unit) {
    route("/health") {
        get("/live") {
            call.respond(HealthResponse("alive"))
        }
        get("/ready") {
            readinessCheck()
            call.respond(HealthResponse("ready"))
        }
    }
}
