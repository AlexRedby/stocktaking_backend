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
    companion object {
        const val THICC_ITEM_CASE_ID = "5c0a840b86f7742ffa4f2482"
    }

    suspend fun getAllToolNames(): Set<String> {
        val crafts = tarkovDevClient.getCrafts()

        return crafts.asSequence()
            .flatMap { b ->
                b.requiredItems.filterNotNull().filter { item ->
                    item.attributes?.any { it?.type == "tool" && it.value == "true" } == true
                }
            }.map { it.item.name }
            .filterNotNull()
            .sorted()
            .toSet()
    }

    suspend fun getReactFlowTree(): ReactFlowGraph {
        val crafts = tarkovDevClient.getCrafts()
        val barters = tarkovDevClient.getBarters()

        // TODO: build proper tree before processing it for ReactFlow or what else...

        var nodes = crafts.flatMap { b ->
            (b.requiredItems + b.rewardItems).filterNotNull()
                // Tools will be returned after craft finished, so we don't need to include them in tree
                .filter { item -> item.attributes?.none { it?.type == "tool" && it.value == "true" } == true }
                .map {
                    ReactFlowNode(
                        id = it.item.id,
                        data = ReactFlowNodeData(
                            label = it.item.shortName!!
                        ),
                    )
                }
        }.toSet()
        nodes += barters.flatMap { b ->
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
            b.rewardItems.filterNotNull().flatMap { source ->
                b.requiredItems
                    .filterNotNull()
                    // Tools will be returned after craft finished, so we don't need to include them in tree
                    .filter { item -> item.attributes?.none { it?.type == "tool" && it.value == "true" } == true }
                    .map { target ->
                        ReactFlowEdge(
                            id = source.item.id + target.item.id,
                            source = source.item.id,
                            target = target.item.id,
                        )
                    }
            }
        }.toSet()
        edges += barters.flatMap { b ->
            b.rewardItems.filterNotNull().flatMap { source ->
                b.requiredItems.filterNotNull().map { target ->
                    ReactFlowEdge(
                        id = source.item.id + target.item.id,
                        source = source.item.id,
                        target = target.item.id,
                    )
                }
            }
        }.toSet()

        // Exclude everything except selected root item
        val rootNodeIds = nodes.filter { it.id == THICC_ITEM_CASE_ID }.map { it.id }.toSet()
        val res = filterTree(nodes, edges, rootNodeIds, mutableSetOf())

        // Add root which needed for d3-hierarchy
        // val rootNode = ReactFlowNode("root", ReactFlowNodeData("Root"))
        // val allChildrenIds = edges.asSequence()
        //     .map { it.target }
        //     .toSet()
        // val rootNodeIds = nodes.asSequence()
        //     .filter { !allChildrenIds.contains(it.id) }
        //     .map { it.id }
        //     .toSet()
        // nodes += rootNode
        // edges += rootNodeIds.map {
        //     ReactFlowEdge(
        //         id = "root$it",
        //         source = "root",
        //         target = it,
        //     )
        // }
        val graph = ReactFlowGraph(res.first, res.second)
        // Need to remove all loops for d3-hierarchy
        // searchForLoop(rootNode, graph, mutableSetOf())

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
