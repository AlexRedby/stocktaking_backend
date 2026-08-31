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
    val recipes: List<ReactFlowRecipe>,
)

@Serializable
data class ReactFlowRecipe(
    val id: String,
    val outputCount: Double,
    val station: ReactFlowStation,
)

@Serializable
data class ReactFlowStation(
    val id: String,
    val name: String,
    val level: Int,
    val image: String,
)

@Serializable
data class ReactFlowEdge(
    val id: String,
    val source: String,
    val sourceHandle: String,
    val target: String,
    val requiredItemCount: Double,
)
