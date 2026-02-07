package ru.alexredby.stocktaking

import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.addResourceSource

// TODO: reuse ktor configs
fun readConfig(): WebConfig = ConfigLoaderBuilder.default()
    // TODO: use hocon instead of yaml
    .addResourceSource("/application.yaml")
    .strict()
    .build()
    .loadConfigOrThrow<WebConfig>()

data class WebConfig(
    val database: DatabaseConfig
)

data class DatabaseConfig(
    val host: String,
    val port: Int,
    val name: String,
    val user: String,
    val password: String,
)
