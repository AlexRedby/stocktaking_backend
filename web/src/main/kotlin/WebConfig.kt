package ru.alexredby.stocktaking

import ru.alexredby.stocktaking.client.tarkov.dev.TarkovDevConfig
import ru.alexredby.stocktaking.configuration.DatabaseConfig
import ru.alexredby.stocktaking.configuration.loadConfig

fun createAppConfig(): AppConfig = loadConfig("/application.conf")

data class AppConfig(
    val server: ServerConfig,
    val database: DatabaseConfig,
    val tarkovDev: TarkovDevConfig,
)

data class ServerConfig(
    val host: String,
    val port: Int,
    val development: Boolean,
)
