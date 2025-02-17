package ru.alexredby.stocktaking.dto

import kotlinx.serialization.Serializable

@Serializable
data class D3Node<T>(
    val name: String,
    val attributes: T,
    val children: List<D3Node<T>> = emptyList()
)
