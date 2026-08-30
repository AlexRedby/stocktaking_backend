package ru.alexredby.stocktaking.configuration

data class DatabaseConfig(
    val host: String,
    val port: Int,
    val name: String,
    val user: String,
    val password: String,
    val cp: ConnectionPoolConfig = ConnectionPoolConfig(),
)

data class ConnectionPoolConfig(
    val maximumPoolSize: Int = 10,
    val minimumIdle: Int = 2,
    val connectionTimeout: Long = 10_000,
    val idleTimeout: Long = 600_000,
    val maxLifetime: Long = 1_800_000,
)
