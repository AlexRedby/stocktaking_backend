package ru.alexredby.stocktaking.dto

import kotlinx.serialization.Serializable

@Serializable
data class ApiErrorResponse(
    val code: String,
    val message: String,
    val parameter: String? = null,
)
