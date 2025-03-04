package ru.alexredby.stocktaking.util

import ru.alexredby.stocktaking.dto.GraphItem
import ru.alexredby.stocktaking.dto.ReactFlowEdge
import ru.alexredby.stocktaking.dto.ReactFlowNode
import ru.alexredby.stocktaking.dto.ReactFlowNodeData
import kotlin.collections.flatMap

fun Iterable<GraphItem>.toReactFlowNodes() = this.asSequence()
    .map {
        ReactFlowNode(
            id = it.id,
            data = ReactFlowNodeData(
                label = it.shortName
            ),
        )
    }.toSet()

fun Iterable<GraphItem>.toReactFlowEdges() = this.asSequence()
    .flatMap { item ->
        item.crafts.flatMap { craft ->
            craft.components.map {
                ReactFlowEdge(
                    id = item.id + it.item.id,
                    source = item.id,
                    target = it.item.id,
                )
            }
        }
    }.toSet()
