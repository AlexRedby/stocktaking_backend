package ru.alexredby.stocktaking.client.tarkov.dev

data class TarkovDevConfig(
    val endpoint: String = "https://api.tarkov.dev/graphql",
    val connectTimeoutMillis: Long = 5_000,
    val readTimeoutMillis: Long = 15_000,
    val retryCount: Int = 2,
)
