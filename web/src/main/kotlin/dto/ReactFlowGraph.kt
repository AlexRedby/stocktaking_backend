package ru.alexredby.stocktaking.dto

data class ReactFlowGraph(
    val nodes: List<ReactFlowNode>,
)

data class ReactFlowNode(
    val id: String,
    val data: ReactFlowNodeData,
)

data class ReactFlowNodeData(
    val label: String,
)
