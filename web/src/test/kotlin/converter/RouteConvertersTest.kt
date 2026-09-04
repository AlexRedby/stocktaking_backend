package ru.alexredby.stocktaking.converter

import io.kotest.matchers.collections.shouldBeUnique
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json
import ru.alexredby.stocktaking.dto.Craft
import ru.alexredby.stocktaking.dto.CraftComponent
import ru.alexredby.stocktaking.dto.GraphItem
import ru.alexredby.stocktaking.dto.ReactFlowGraph
import ru.alexredby.stocktaking.dto.Station
import kotlin.test.Test

class RouteConvertersTest {
    @Test
    fun `alternative recipes preserve identity and quantities`() {
        val result = item("result", "Result")
        val component = item("component", "Component")
        val station = Station(
            id = "station",
            name = "Workbench",
            level = 2,
            image = "https://example.com/station.png",
        )

        result.crafts += recipe(
            id = "recipe-a",
            result = result,
            outputCount = 2.0,
            component = component,
            requiredItemCount = 3.0,
            station = station,
        )
        result.crafts += recipe(
            id = "recipe-b",
            result = result,
            outputCount = 4.0,
            component = component,
            requiredItemCount = 5.0,
            station = station,
        )

        val graph = ReactFlowGraph(
            nodes = setOf(result, component).toReactFlowNodes(),
            edges = setOf(result, component).toReactFlowEdges(),
        )
        val roundTripped = Json.decodeFromString<ReactFlowGraph>(Json.encodeToString(graph))

        val recipes = roundTripped.nodes.single { it.id == result.id }.data.recipes
        recipes.map { it.id }.shouldContainExactlyInAnyOrder("recipe-a", "recipe-b")
        recipes.map { it.outputCount }.shouldContainExactlyInAnyOrder(2.0, 4.0)
        recipes.map { it.station.id }.toSet() shouldBe setOf(station.id)

        roundTripped.edges.map { it.id }
            .shouldContainExactlyInAnyOrder("recipe-a:component", "recipe-b:component")
        roundTripped.edges.map { it.sourceHandle }.shouldContainExactlyInAnyOrder("recipe-a", "recipe-b")
        roundTripped.edges.map { it.requiredItemCount }.shouldContainExactlyInAnyOrder(3.0, 5.0)
        roundTripped.edges.map { it.id }.shouldBeUnique()
    }

    private fun item(id: String, name: String) = GraphItem(
        id = id,
        fullName = name,
        shortName = name,
        image = "https://example.com/$id.png",
    )

    private fun recipe(
        id: String,
        result: GraphItem,
        outputCount: Double,
        component: GraphItem,
        requiredItemCount: Double,
        station: Station,
    ) = Craft(
        id = id,
        result = result,
        count = outputCount,
        components = setOf(CraftComponent(component, requiredItemCount)),
        tools = emptySet(),
        station = station,
    )
}
