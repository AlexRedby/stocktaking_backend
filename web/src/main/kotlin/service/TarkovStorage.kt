package ru.alexredby.stocktaking.service

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import ru.alexredby.stocktaking.client.TarkovDevClient
import ru.alexredby.stocktaking.dto.Craft
import ru.alexredby.stocktaking.dto.GraphItem
import ru.alexredby.stocktaking.util.toCraftComponents
import ru.alexredby.stocktaking.util.toGraphItem
import ru.alexredby.stocktaking.util.toTools
import kotlin.sequences.forEach

val tarkovStorageModule = module {
    singleOf(::TarkovStorage)
}

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

                b.rewardItems.filterNotNull()
                    .forEach {
                        it.item.toGraphItem(this).apply {
                            val craft = Craft(
                                result = this,
                                count = it.count,
                                components = components,
                                tools = tools,
                            )
                            this.crafts.add(craft)
                            components.forEach { c -> c.item.usedIn.add(craft) }
                        }
                    }
            }
            barters.forEach { b ->
                val components = b.requiredItems.toCraftComponents(this)

                b.rewardItems.asSequence()
                    .filterNotNull()
                    .forEach {
                        it.item.toGraphItem(this).apply {
                            val craft = Craft(
                                result = this,
                                count = it.count,
                                components = components,
                                tools = emptySet(),
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
