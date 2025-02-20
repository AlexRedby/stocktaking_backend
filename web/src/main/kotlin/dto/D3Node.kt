package ru.alexredby.stocktaking.dto

import kotlinx.serialization.Serializable

@Serializable
data class D3Node(
    val name: String,
    val attributes: D3Attributes,
    var children: List<D3Node> = emptyList()
)

@Serializable
data class D3Attributes(
    val id: String,
    val fullName: String,
    var count: Int,
)
