package ru.alexredby.stocktaking.dto

import kotlinx.serialization.Serializable

@Serializable
data class ItemForComboBox(
    val id: String,
    val fullName: String,
    val shortName: String,
)
