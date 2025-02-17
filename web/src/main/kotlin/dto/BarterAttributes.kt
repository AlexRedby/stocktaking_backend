package ru.alexredby.stocktaking.dto

import kotlinx.serialization.Serializable

@Serializable
data class BarterAttributes(
    val id: String,
    val fullName: String,
    val count: Int,
)
