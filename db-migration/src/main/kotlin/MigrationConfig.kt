package ru.alexredby.db.migration

import ru.alexredby.stocktaking.configuration.DatabaseConfig
import ru.alexredby.stocktaking.configuration.loadConfig

data class MigrationConfig(
    val database: DatabaseConfig,
)

fun createMigrationConfig(resource: String = "/database.conf"): MigrationConfig = loadConfig(resource)
