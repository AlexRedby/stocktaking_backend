package ru.alexredby.stocktaking.service

import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import ru.alexredby.stocktaking.dto.Craft
import ru.alexredby.stocktaking.dto.CraftComponent
import ru.alexredby.stocktaking.dto.GraphItem
import ru.alexredby.stocktaking.dto.ItemForComboBox
import ru.alexredby.stocktaking.dto.Station
import kotlin.test.Test

class TarkovServiceTest {
    @Test
    fun `phrase search matches ordered literal terms`() = runTest {
        val craftable = item("craftable", "Ratnik Alpha[Beta] Helmet", "Ratnik")
        craftable.addCraft("craft")
        val service = serviceFor(craftable)
        val expected = listOf(ItemForComboBox(craftable.id, craftable.fullName, craftable.shortName))

        service.getItems("rat/helmet") shouldBe expected
        service.getItems("alpha[beta]") shouldBe expected
        service.getItems("helmet/rat").shouldBeEmpty()
    }

    @Test
    fun `crafting tree keeps diamond paths and removes the closing cycle edge`() = runTest {
        val root = item("root")
        val left = item("left")
        val right = item("right")
        val shared = item("shared")
        root.addCraft("root-craft", left, right)
        left.addCraft("left-craft", shared)
        right.addCraft("right-craft", shared)
        shared.addCraft("cycle-craft", root)

        val graph = serviceFor(root, left, right, shared).getReactFlowTree(root.id)

        graph.nodes.map { it.id }.shouldContainExactlyInAnyOrder("root", "left", "right", "shared")
        graph.edges.map { it.id }.shouldContainExactlyInAnyOrder(
            "root-craft:left",
            "root-craft:right",
            "left-craft:shared",
            "right-craft:shared",
        )
    }

    private fun serviceFor(vararg items: GraphItem): TarkovService {
        val storage = mockk<TarkovStorage>()
        coEvery { storage.getFullCraftableTree() } returns items.associateBy { it.id }
        return TarkovService(storage)
    }

    private fun item(id: String, fullName: String = id, shortName: String = id) = GraphItem(
        id = id,
        fullName = fullName,
        shortName = shortName,
        image = "https://example.com/$id.png",
    )

    private fun GraphItem.addCraft(id: String, vararg components: GraphItem) {
        crafts += Craft(
            id = id,
            result = this,
            count = 1.0,
            components = components.map { CraftComponent(it, 1.0) }.toSet(),
            tools = emptySet(),
            station = STATION,
        )
    }

    private companion object {
        val STATION = Station("station", "Workbench", 1, "https://example.com/station.png")
    }
}
