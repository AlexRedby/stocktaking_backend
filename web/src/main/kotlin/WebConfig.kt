package ru.alexredby.stocktaking

import ru.alexredby.stocktaking.configuration.DatabaseConfig
import ru.alexredby.stocktaking.configuration.loadConfig

fun createAppConfig(): AppConfig = loadConfig("/application.conf")

data class AppConfig(
    val server: ServerConfig,
    val database: DatabaseConfig
)

data class ServerConfig(
    val port: Int,
    val development: Boolean,
)
