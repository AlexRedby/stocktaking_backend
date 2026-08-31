package ru.alexredby.stocktaking.service

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.alexredby.stocktaking.client.tarkov.dev.TarkovDevClient
import ru.alexredby.stocktaking.dto.Craft
import ru.alexredby.stocktaking.dto.GraphItem
import ru.alexredby.stocktaking.util.toCraftComponents
import ru.alexredby.stocktaking.util.toGraphItem
import ru.alexredby.stocktaking.util.toStation
import ru.alexredby.stocktaking.util.toTools
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.TimeMark
import kotlin.time.TimeSource

class TarkovStorage(
    private val tarkovDevClient: TarkovDevClient,
    private val cacheTtl: Duration = 15.minutes,
    private val timeSource: TimeSource = TimeSource.Monotonic,
) {
    private val cacheMutex = Mutex()

    private var cache: CachedGraph? = null

    suspend fun getFullCraftableTree(): Map<String, GraphItem> = cacheMutex.withLock {
        cache?.takeIf { it.isFresh() }?.let { return@withLock it.graph }

        val graph = loadGraph()
        cache = CachedGraph(graph, timeSource.markNow())
        graph
    }

    private suspend fun loadGraph(): Map<String, GraphItem> {
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
                                id = b.id,
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
                                id = b.id,
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

        return idToItem
    }

    private fun CachedGraph.isFresh() =
        loadedAt.elapsedNow() < cacheTtl

    private data class CachedGraph(
        val graph: Map<String, GraphItem>,
        val loadedAt: TimeMark,
    )
}
