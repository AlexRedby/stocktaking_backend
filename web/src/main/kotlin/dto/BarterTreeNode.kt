package ru.alexredby.stocktaking.dto

import kotlinx.serialization.Serializable

@Serializable
data class BarterTreeNode(
    val id: String,
    val name: String,
    val shortName: String,
    val children: List<BarterTreeNode> = emptyList()
)
