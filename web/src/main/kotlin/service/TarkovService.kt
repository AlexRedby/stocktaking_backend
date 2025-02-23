package ru.alexredby.stocktaking.service

import io.github.oshai.kotlinlogging.KotlinLogging
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import ru.alexredby.stocktaking.client.TarkovDevClient
import ru.alexredby.stocktaking.dto.D3Attributes
import ru.alexredby.stocktaking.dto.D3Node
import ru.alexredby.stocktaking.dto.ReactFlowEdge
import ru.alexredby.stocktaking.dto.ReactFlowGraph
import ru.alexredby.stocktaking.dto.ReactFlowNode
import ru.alexredby.stocktaking.dto.ReactFlowNodeData

val logger = KotlinLogging.logger { }

val tarkovServiceModule = module {
    singleOf(::TarkovService)
}

class TarkovService(
    private val tarkovDevClient: TarkovDevClient
) {
    suspend fun getReactFlowTree(): ReactFlowGraph {
        val crafts = tarkovDevClient.getCrafts()

        var nodes = crafts.flatMap { b ->
            (b.requiredItems + b.rewardItems).filterNotNull().map {
                ReactFlowNode(
                    id = it.item.id,
                    data = ReactFlowNodeData(
                        label = it.item.shortName!!
                    ),
                )
            }
        }.toSet()

        var edges = crafts.flatMap { b ->
            b.rewardItems.filterNotNull().flatMap { target ->
                b.requiredItems.filterNotNull().map { source ->
                    ReactFlowEdge(
                        id = source.item.id + target.item.id,
                        source = source.item.id,
                        target = target.item.id,
                    )
                }
            }
        }.toSet()

        // Add root which needed for d3-hierarchy
        val rootNode = ReactFlowNode("root", ReactFlowNodeData("Root"))
        val allChildrenIds = edges.asSequence()
            .map { it.target }
            .toSet()
        val rootNodeIds = nodes.asSequence()
            .filter { !allChildrenIds.contains(it.id) }
            .map { it.id }
            .toSet()
        nodes += rootNode
        edges += rootNodeIds.map {
            ReactFlowEdge(
                id = "root$it",
                source = "root",
                target = it,
            )
        }
        val graph = ReactFlowGraph(nodes, edges)
        // Need to remove all loops for d3-hierarchy
        searchForLoop(rootNode, graph, mutableSetOf())

        return graph
    }

    suspend fun getCraftingTree(): D3Node {
        val barters = tarkovDevClient.getBarters()
        val crafts = tarkovDevClient.getCrafts()

        val items = buildMap<String, D3Node> {
            barters.forEach { b ->
                (b.requiredItems + b.rewardItems).filterNotNull().forEach {
                    val existingNode = get(it.item.id)
                    if (existingNode == null) {
                        put(
                            it.item.id,
                            D3Node(
                                name = it.item.shortName!!,
                                attributes = D3Attributes(
                                    id = it.item.id,
                                    fullName = it.item.name!!,
                                    count = it.count.toInt()
                                ),
                            ),
                        )
                    } else {
                        existingNode.attributes.count += it.count.toInt()
                    }
                }
            }
            crafts.forEach { b ->
                (b.requiredItems + b.rewardItems).filterNotNull().forEach {
                    val existingNode = get(it.item.id)
                    if (existingNode == null) {
                        put(
                            it.item.id,
                            D3Node(
                                name = it.item.shortName!!,
                                attributes = D3Attributes(
                                    id = it.item.id,
                                    fullName = it.item.name!!,
                                    count = it.count.toInt()
                                ),
                            ),
                        )
                    } else {
                        existingNode.attributes.count += it.count.toInt()
                    }
                }
            }
        }

        barters.forEach { b ->
            val children = b.requiredItems.filterNotNull().map {
                val id = it.item.id
                items[id]!!
            }
            b.rewardItems.filterNotNull().forEach { items[it.item.id]!!.children += children }
        }
        crafts.forEach { b ->
            val children = b.requiredItems.filterNotNull().map {
                val id = it.item.id
                items[id]!!
            }
            b.rewardItems.filterNotNull().forEach { items[it.item.id]!!.children += children }
        }

        // FIXME: This is temporary solution because we have tree but not graph and we cannot have loops
        // Detect loops and destroy them
        items.values.forEach {
            searchForLoop(it, mutableSetOf())
        }

        val allChildrenIds = items.values.asSequence()
            .flatMap { it.children }
            .map { it.attributes.id }
            .toSet()
        val rootNodes = items.asSequence()
            .filter { !allChildrenIds.contains(it.key) }
            .map { it.value }
            .toList()

        return createRootNode(rootNodes)
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

    private fun searchForLoop(node: D3Node, visitedIds: MutableSet<String>) {
        visitedIds.add(node.attributes.id)

        val itemsToDelete = node.children.filter { visitedIds.contains(it.attributes.id) }
        if (itemsToDelete.isNotEmpty()) {
            logger.warn {
                "This items will be deleted from ${node.attributes.fullName} craft: ${
                    itemsToDelete.map { it.attributes.fullName }
                } }"
            }
        }
        node.children -= itemsToDelete
        node.children.forEach { searchForLoop(it, visitedIds) }

        visitedIds.remove(node.attributes.id)
    }

    private fun createRootNode(children: List<D3Node>) = D3Node(
        name = "Root",
        attributes = D3Attributes(
            id = "0",
            fullName = "Root",
            count = 0
        ),
        children = children,
    )
}
