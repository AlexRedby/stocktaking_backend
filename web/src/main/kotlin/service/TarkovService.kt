package ru.alexredby.stocktaking.service

import io.github.oshai.kotlinlogging.KotlinLogging
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import ru.alexredby.stocktaking.converter.toItemForComboBox
import ru.alexredby.stocktaking.converter.toReactFlowEdges
import ru.alexredby.stocktaking.converter.toReactFlowNodes
import ru.alexredby.stocktaking.dto.GraphItem
import ru.alexredby.stocktaking.dto.ItemForComboBox
import ru.alexredby.stocktaking.dto.ReactFlowEdge
import ru.alexredby.stocktaking.dto.ReactFlowGraph
import ru.alexredby.stocktaking.dto.ReactFlowNode

val logger = KotlinLogging.logger { }

val tarkovServiceModule = module {
    singleOf(::TarkovService)
}

class TarkovService(
    private val tarkovStorage: TarkovStorage
) {
    companion object {
        const val THICC_ITEM_CASE_ID = "5c0a840b86f7742ffa4f2482"
        val wordDelimiters = "([/\",.]|\\s)+".toRegex()
    }

    suspend fun getItems(filter: String?): List<ItemForComboBox> = tarkovStorage
        .getFullCraftableTree()
        .values.asSequence()
        .filter { filter.isNullOrBlank() || phraseSearch(filter, it.shortName, it.fullName) }
        .map { it.toItemForComboBox() }
        .sortedBy { it.fullName }
        .toList()

    suspend fun getAllToolNames(): Set<String> {
        val crafts = tarkovStorage.getFullCraftableTree()

        return crafts.values.asSequence()
            .flatMap { it.crafts }
            .flatMap { it.tools }
            .map { it.fullName }
            .sorted()
            .toSet()
    }

    suspend fun getReactFlowTree(): ReactFlowGraph {
        val idToItem = tarkovStorage.getFullCraftableTree()

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

    private fun phraseSearch(phrase: String, vararg targets: String): Boolean {
        val phraseRegex = phrase.split(wordDelimiters)
            .joinToString(separator = "") { "\\b$it.*" }
            .toRegex(RegexOption.IGNORE_CASE)

        return targets.any { it.contains(phraseRegex) }
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
        allNodes: Set<ReactFlowNode>,
        edges: Set<ReactFlowEdge>,
        neededIds: Set<String>,
        visitedIds: MutableSet<String>,
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
