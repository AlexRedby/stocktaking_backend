package ru.alexredby.stocktaking.service

import ru.alexredby.stocktaking.client.tarkov.dev.TarkovDevClient
import ru.alexredby.stocktaking.dto.Craft
import ru.alexredby.stocktaking.dto.GraphItem
import ru.alexredby.stocktaking.util.toCraftComponents
import ru.alexredby.stocktaking.util.toGraphItem
import ru.alexredby.stocktaking.util.toStation
import ru.alexredby.stocktaking.util.toTools

class TarkovStorage(
    private val tarkovDevClient: TarkovDevClient
) {
    var cachedGraph: Map<String, GraphItem> = HashMap()

    suspend fun getFullCraftableTree(): Map<String, GraphItem> {
        if (cachedGraph.isNotEmpty()) {
            return cachedGraph
        }

        val crafts = tarkovDevClient.getCrafts()
        val barters = tarkovDevClient.getBarters()

        val idToItem: Map<String, GraphItem> = buildMap {
            crafts.forEach { b ->
                val components = b.requiredItems.toCraftComponents(this)
                val tools = b.requiredItems.toTools(this)
                val station = b.toStation()

                b.rewardItems.filterNotNull()
                    .forEach {
                        it.item.toGraphItem(this).apply {
                            val craft = Craft(
                                result = this,
                                count = it.count,
                                components = components,
                                tools = tools,
                                station = station
                            )
                            this.crafts.add(craft)
                            components.forEach { c -> c.item.usedIn.add(craft) }
                        }
                    }
            }
            barters.forEach { b ->
                val components = b.requiredItems.toCraftComponents(this)
                val station = b.toStation()

                b.rewardItems.asSequence()
                    .filterNotNull()
                    .forEach {
                        it.item.toGraphItem(this).apply {
                            val craft = Craft(
                                result = this,
                                count = it.count,
                                components = components,
                                tools = emptySet(),
                                station = station
                            )
                            this.crafts.add(craft)
                            components.forEach { c -> c.item.usedIn.add(craft) }
                        }
                    }
            }
        }

        cachedGraph = idToItem

        return idToItem
    }
}
