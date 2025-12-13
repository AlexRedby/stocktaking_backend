package ru.alexredby.stocktaking.converter

import ru.alexredby.stocktaking.dto.GraphItem
import ru.alexredby.stocktaking.dto.ItemForComboBox
import ru.alexredby.stocktaking.dto.ReactFlowEdge
import ru.alexredby.stocktaking.dto.ReactFlowNode
import ru.alexredby.stocktaking.dto.ReactFlowNodeData

fun Iterable<GraphItem>.toReactFlowNodes() = this.asSequence()
    .map {
        ReactFlowNode(
            id = it.id,
            data = ReactFlowNodeData(
                label = it.shortName,
                fullName = it.fullName,
                shortName = it.shortName,
                image = it.image,
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

fun GraphItem.toItemForComboBox() = ItemForComboBox(
    id = id,
    fullName = fullName,
    shortName = shortName,
)
