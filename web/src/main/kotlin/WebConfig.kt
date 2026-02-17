package ru.alexredby.stocktaking

import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.ExperimentalHoplite
import com.sksamuel.hoplite.addResourceSource

@OptIn(ExperimentalHoplite::class)
fun createAppConfig(): AppConfig {
    return ConfigLoaderBuilder.default()
        .addResourceSource("/application.conf")
        .withExplicitSealedTypes()
        .strict()
        .build()
        .loadConfigOrThrow<AppConfig>()
}

data class AppConfig(
    val server: ServerConfig,
    val database: DatabaseConfig
)

data class ServerConfig(
    val port: Int,
    val development: Boolean,
)

data class DatabaseConfig(
    val host: String,
    val port: Int,
    val name: String,
    val user: String,
    val password: String,

    val cp: ConnectionPoolConfig,
)

data class ConnectionPoolConfig(
    val maximumPoolSize: Int,
    val minimumIdle: Int,
    val connectionTimeout: Long,
    val idleTimeout: Long,
    val maxLifetime: Long,
)
