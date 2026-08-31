package ru.alexredby.stocktaking.route

import io.mockk.coEvery
import io.mockk.mockk
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation as ClientContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation as ServerContentNegotiation
import io.ktor.server.routing.routing
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import ru.alexredby.stocktaking.client.tarkov.dev.TarkovDevResponseException
import ru.alexredby.stocktaking.client.tarkov.dev.TarkovDevUnavailableException
import ru.alexredby.stocktaking.dto.ApiErrorResponse
import ru.alexredby.stocktaking.dto.Craft
import ru.alexredby.stocktaking.dto.GraphItem
import ru.alexredby.stocktaking.dto.ItemForComboBox
import ru.alexredby.stocktaking.dto.ReactFlowGraph
import ru.alexredby.stocktaking.dto.Station
import ru.alexredby.stocktaking.service.TarkovService
import ru.alexredby.stocktaking.service.TarkovStorage
import kotlin.test.Test
import kotlin.test.assertEquals

class TarkovRoutesTest {
    @Test
    fun `craftable items accepts a blank filter`() = testApplication {
        installApi()
        val jsonClient = jsonClient()

        val response = jsonClient.get("/api/craftable-items") {
            parameter("filter", "")
        }

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(
            listOf(
                ItemForComboBox(ROOT_ID, "Аптечка [Alpha]", "AFAK"),
                ItemForComboBox(SECOND_ID, "Набор (Beta)", "KIT"),
            ),
            response.body(),
        )
    }

    @Test
    fun `craftable items safely handles punctuation and Unicode`() = testApplication {
        installApi()
        val jsonClient = jsonClient()

        listOf("[", "(", "*", "\"").forEach { filter ->
            val response = jsonClient.get("/api/craftable-items") {
                parameter("filter", filter)
            }
            assertEquals(HttpStatusCode.OK, response.status, "filter=$filter")
        }

        val unicodeResponse = jsonClient.get("/api/craftable-items") {
            parameter("filter", "аптечка")
        }
        assertEquals(HttpStatusCode.OK, unicodeResponse.status)
        assertEquals(listOf(ROOT_ID), unicodeResponse.body<List<ItemForComboBox>>().map { it.id })
    }

    @Test
    fun `craftable items excludes items without recipes`() = testApplication {
        installApi()
        val jsonClient = jsonClient()

        val allItems = jsonClient.get("/api/craftable-items")
        val leafSearch = jsonClient.get("/api/craftable-items") { parameter("filter", "Leaf") }
        val toolSearch = jsonClient.get("/api/craftable-items") { parameter("filter", "Tool") }

        assertEquals(listOf(ROOT_ID, SECOND_ID), allItems.body<List<ItemForComboBox>>().map { it.id })
        assertEquals(emptyList(), leafSearch.body<List<ItemForComboBox>>())
        assertEquals(emptyList(), toolSearch.body<List<ItemForComboBox>>())
    }

    @Test
    fun `crafting tree requires target item ID`() = testApplication {
        installApi()
        val response = jsonClient().get("/api/crafting-tree")

        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals(
            ApiErrorResponse(
                code = "missing_parameter",
                message = "Query parameter 'target_item_id' is required",
                parameter = "target_item_id",
            ),
            response.body(),
        )
    }

    @Test
    fun `crafting tree rejects malformed target item ID`() = testApplication {
        installApi()
        val response = jsonClient().get("/api/crafting-tree") {
            parameter("target_item_id", "not-an-item-id")
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals(
            ApiErrorResponse(
                code = "invalid_parameter",
                message = "Query parameter 'target_item_id' must be a 24-character hexadecimal ID",
                parameter = "target_item_id",
            ),
            response.body(),
        )
    }

    @Test
    fun `crafting tree returns not found for an unknown item`() = testApplication {
        installApi()
        val response = jsonClient().get("/api/crafting-tree") {
            parameter("target_item_id", UNKNOWN_ID)
        }

        assertEquals(HttpStatusCode.NotFound, response.status)
        assertEquals(
            ApiErrorResponse(
                code = "item_not_found",
                message = "No item exists for the requested ID",
                parameter = "target_item_id",
            ),
            response.body(),
        )
    }

    @Test
    fun `crafting tree accepts a hexadecimal ID and returns its graph`() = testApplication {
        installApi()
        val response = jsonClient().get("/api/crafting-tree") {
            parameter("target_item_id", ROOT_ID.uppercase())
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val graph = response.body<ReactFlowGraph>()
        assertEquals(setOf(ROOT_ID), graph.nodes.map { it.id }.toSet())
        assertEquals(emptySet(), graph.edges)
    }

    @Test
    fun `tool names returns sorted unique names`() = testApplication {
        installApi()

        val response = jsonClient().get("/api/tool-names")

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(listOf("Awl", "Reusable tool"), response.body())
    }

    @Test
    fun `supported routes return a stable error for an unexpected failure`() = testApplication {
        installApi(failure = IllegalStateException("upstream failed"))
        val jsonClient = jsonClient()

        val itemsResponse = jsonClient.get("/api/craftable-items")
        val treeResponse = jsonClient.get("/api/crafting-tree") {
            parameter("target_item_id", ROOT_ID)
        }
        val toolsResponse = jsonClient.get("/api/tool-names")

        listOf(itemsResponse, treeResponse, toolsResponse).forEach { response ->
            assertEquals(HttpStatusCode.InternalServerError, response.status)
            assertEquals(
                ApiErrorResponse(
                    code = "internal_error",
                    message = "The request could not be completed",
                ),
                response.body(),
            )
        }
    }

    @Test
    fun `transport failure returns service unavailable`() = testApplication {
        installApi(failure = TarkovDevUnavailableException(IllegalStateException("offline")))

        val response = jsonClient().get("/api/craftable-items")

        assertEquals(HttpStatusCode.ServiceUnavailable, response.status)
        assertEquals(
            ApiErrorResponse(
                code = "upstream_unavailable",
                message = "Tarkov.dev is temporarily unavailable",
            ),
            response.body(),
        )
    }

    @Test
    fun `invalid GraphQL response returns bad gateway`() = testApplication {
        installApi(failure = TarkovDevResponseException("GraphQL error"))

        val response = jsonClient().get("/api/crafting-tree") {
            parameter("target_item_id", ROOT_ID)
        }

        assertEquals(HttpStatusCode.BadGateway, response.status)
        assertEquals(
            ApiErrorResponse(
                code = "upstream_error",
                message = "Tarkov.dev returned an invalid response",
            ),
            response.body(),
        )
    }

    private fun ApplicationTestBuilder.installApi(failure: Exception? = null) {
        val storage = mockk<TarkovStorage>()
        if (failure == null) {
            coEvery { storage.getFullCraftableTree() } returns TEST_GRAPH
        } else {
            coEvery { storage.getFullCraftableTree() } throws failure
        }

        application {
            install(ServerContentNegotiation) { json() }
            configureErrorHandling()
            routing {
                installTarkovApiRoutes(TarkovService(storage))
            }
        }
    }

    private fun ApplicationTestBuilder.jsonClient() = createClient {
        install(ClientContentNegotiation) { json() }
    }

    private companion object {
        const val ROOT_ID = "aaaaaaaaaaaaaaaaaaaaaaaa"
        const val SECOND_ID = "bbbbbbbbbbbbbbbbbbbbbbbb"
        const val UNKNOWN_ID = "cccccccccccccccccccccccc"

        val LEAF = GraphItem("dddddddddddddddddddddddd", "Leaf component", "Leaf", "https://example.com/leaf.png")
        val TOOL = GraphItem("eeeeeeeeeeeeeeeeeeeeeeee", "Reusable tool", "Tool", "https://example.com/tool.png")
        val SECOND_TOOL = GraphItem("ffffffffffffffffffffffff", "Awl", "Awl", "https://example.com/awl.png")
        val STATION = Station("station", "Workbench", 1, "https://example.com/station.png")
        val ROOT = GraphItem(ROOT_ID, "Аптечка [Alpha]", "AFAK", "https://example.com/afak.png").apply {
            crafts += Craft("root-recipe", this, 1.0, emptySet(), setOf(TOOL), STATION)
            crafts += Craft("second-root-recipe", this, 1.0, emptySet(), setOf(TOOL, SECOND_TOOL), STATION)
        }
        val SECOND = GraphItem(SECOND_ID, "Набор (Beta)", "KIT", "https://example.com/kit.png").apply {
            crafts += Craft("second-recipe", this, 1.0, emptySet(), emptySet(), STATION)
        }
        val TEST_GRAPH = linkedMapOf(
            ROOT_ID to ROOT,
            SECOND_ID to SECOND,
            LEAF.id to LEAF,
            TOOL.id to TOOL,
        )
    }
}
