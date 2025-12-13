package ru.alexredby.stocktaking.dto

import kotlinx.serialization.Serializable

@Serializable
data class ReactFlowGraph(
    val nodes: Set<ReactFlowNode>,
    var edges: Set<ReactFlowEdge>,
)

@Serializable
data class ReactFlowNode(
    val id: String,
    val data: ReactFlowNodeData,
)

@Serializable
data class ReactFlowNodeData(
    val label: String,
    val fullName: String,
    val shortName: String,
    val image: String,
)

@Serializable
data class ReactFlowEdge(
    val id: String,
    val source: String,
    val target: String,
)
