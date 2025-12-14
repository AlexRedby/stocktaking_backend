package ru.alexredby.stocktaking.converter

import ru.alexredby.stocktaking.dto.GraphItem
import ru.alexredby.stocktaking.dto.ItemForComboBox
import ru.alexredby.stocktaking.dto.ReactFlowEdge
import ru.alexredby.stocktaking.dto.ReactFlowNode
import ru.alexredby.stocktaking.dto.ReactFlowNodeData
import ru.alexredby.stocktaking.dto.ReactFlowStation
import ru.alexredby.stocktaking.dto.Station

fun Iterable<GraphItem>.toReactFlowNodes() = this.asSequence()
    .map { item ->
        ReactFlowNode(
            id = item.id,
            data = ReactFlowNodeData(
                label = item.shortName,
                fullName = item.fullName,
                shortName = item.shortName,
                image = item.image,
                stations = item.crafts.map {
                    it.station.toReactFlowStation()
                },
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

fun Station.toReactFlowStation() = ReactFlowStation(
    name = name,
    level = level,
    image = image,
)
