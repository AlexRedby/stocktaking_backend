package ru.alexredby.stocktaking

import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.addResourceSource

fun readConfig(): WebConfig = ConfigLoaderBuilder.default()
    .addResourceSource("/application.yaml")
    .strict()
    .build()
    .loadConfigOrThrow<WebConfig>()

data class WebConfig(
    val database: DatabaseConfig
)

data class DatabaseConfig(
    val host: String,
    val port: String,
    val name: String,
    val user: String,
    val password: String,
)
