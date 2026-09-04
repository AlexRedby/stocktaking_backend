package ru.alexredby.stocktaking.route

import io.kotest.matchers.shouldBe
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation as ClientContentNegotiation
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation as ServerContentNegotiation
import io.ktor.server.routing.routing
import io.ktor.server.testing.testApplication
import ru.alexredby.stocktaking.dto.HealthResponse
import ru.alexredby.stocktaking.service.ReadinessCheckException
import kotlin.test.Test

class HealthRoutesTest {
    @Test
    fun `liveness does not call dependencies`() = testApplication {
        application {
            install(ServerContentNegotiation) { json() }
            routing { getHealthRoutes() }
        }
        val client = createClient { install(ClientContentNegotiation) { json() } }

        val response = client.get("/health/live")

        response.status shouldBe HttpStatusCode.OK
        response.body<HealthResponse>() shouldBe HealthResponse("alive")
    }

    @Test
    fun `readiness succeeds when dependencies are available`() = testApplication {
        application {
            install(ServerContentNegotiation) { json() }
            routing { installHealthRoutes { } }
        }
        val client = createClient { install(ClientContentNegotiation) { json() } }

        val response = client.get("/health/ready")

        response.status shouldBe HttpStatusCode.OK
        response.body<HealthResponse>() shouldBe HealthResponse("ready")
    }

    @Test
    fun `readiness reports unavailable dependency`() = testApplication {
        application {
            install(ServerContentNegotiation) { json() }
            configureErrorHandling()
            routing {
                installHealthRoutes {
                    throw ReadinessCheckException(IllegalStateException("database unavailable"))
                }
            }
        }
        val client = createClient { install(ClientContentNegotiation) { json() } }

        val response = client.get("/health/ready")

        response.status shouldBe HttpStatusCode.ServiceUnavailable
        response.body<HealthResponse>() shouldBe HealthResponse("not_ready")
    }
}
