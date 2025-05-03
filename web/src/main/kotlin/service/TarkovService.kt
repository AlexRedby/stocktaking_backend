package ru.alexredby.stocktaking.service

import io.github.oshai.kotlinlogging.KotlinLogging
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import ru.alexredby.stocktaking.client.TarkovDevClient
import ru.alexredby.stocktaking.dto.Craft
import ru.alexredby.stocktaking.dto.GraphItem
import ru.alexredby.stocktaking.dto.ReactFlowEdge
import ru.alexredby.stocktaking.dto.ReactFlowGraph
import ru.alexredby.stocktaking.dto.ReactFlowNode
import ru.alexredby.stocktaking.util.isTool
import ru.alexredby.stocktaking.util.toCraftComponents
import ru.alexredby.stocktaking.util.toGraphItem
import ru.alexredby.stocktaking.util.toReactFlowEdges
import ru.alexredby.stocktaking.util.toReactFlowNodes
import ru.alexredby.stocktaking.util.toTools

val logger = KotlinLogging.logger { }

val tarkovServiceModule = module {
    singleOf(::TarkovService)
}

class TarkovService(
    private val tarkovDevClient: TarkovDevClient
) {
    companion object {
        const val THICC_ITEM_CASE_ID = "5c0a840b86f7742ffa4f2482"
    }

    suspend fun getAllToolNames(): Set<String> {
        val crafts = tarkovDevClient.getCrafts()

        return crafts.asSequence()
            .flatMap { b ->
                b.requiredItems.filterNotNull().filter { it.isTool() }
            }.map { it.item.name }
            .filterNotNull()
            .sorted()
            .toSet()
    }

    suspend fun getReactFlowTree(): ReactFlowGraph {
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
                                tools = tools
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
                                tools = emptySet()
                            )
                            this.crafts.add(craft)
                            components.forEach { c -> c.item.usedIn.add(craft) }
                        }
                    }
            }
        }

        // Exclude everything except selected item and its subtree
        val rootNode = idToItem[THICC_ITEM_CASE_ID]!!
        val neededItems = findAllItemsInSubTreeFor(rootNode, mutableSetOf())

        val nodes = neededItems.toReactFlowNodes()
        val edges = neededItems.toReactFlowEdges()
        val graph = ReactFlowGraph(nodes, edges)

        // Remove all loops for better graph readability
        searchForLoop(nodes.find { it.id == rootNode.id }!!, graph, mutableSetOf())

        return graph
    }

    private fun findAllItemsInSubTreeFor(root: GraphItem, visitedIds: MutableSet<String>): Set<GraphItem> {
        visitedIds.add(root.id)

        val res = root.crafts.flatMap { craft ->
            craft.components
                .filter { !visitedIds.contains(it.item.id) }
                .flatMap {
                    findAllItemsInSubTreeFor(it.item, visitedIds)
                }
        }.toSet() + root

        visitedIds.remove(root.id)

        return res
    }

    private fun filterTree(
        allNodes: Set<ReactFlowNode>, edges: Set<ReactFlowEdge>, neededIds: Set<String>, visitedIds: MutableSet<String>
    ): Pair<Set<ReactFlowNode>, Set<ReactFlowEdge>> {
        visitedIds.addAll(neededIds)

        val foundEdges = edges.filter { neededIds.contains(it.source) }.toSet()
        val foundNodes = allNodes.filter { neededIds.contains(it.id) }.toSet()

        if (foundEdges.isNotEmpty()) {
            val newIds = foundEdges.map { it.target }
                .filter { !visitedIds.contains(it) }
                .toSet()
            val res = filterTree(allNodes, edges, newIds, visitedIds)
            return foundNodes + res.first to foundEdges + res.second
        }
        return foundNodes to foundEdges
    }

    private fun searchForLoop(node: ReactFlowNode, graph: ReactFlowGraph, visitedIds: MutableSet<String>) {
        visitedIds.add(node.id)

        val nodeEdges = graph.edges.filter { it.source == node.id }
        val edgesToDelete = nodeEdges.filter { visitedIds.contains(it.target) }
        if (edgesToDelete.isNotEmpty()) {
            logger.warn { "There is a loop for ${node.data.label} craft" }
        }
        graph.edges -= edgesToDelete
        graph.nodes
            .filter { n -> (nodeEdges - edgesToDelete).any { it.target == n.id } }
            .forEach { searchForLoop(it, graph, visitedIds) }

        visitedIds.remove(node.id)
    }
}
