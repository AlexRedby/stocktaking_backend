package ru.alexredby.db.migration

import ru.alexredby.db.migration.liquibase.applyLiquibaseMigration
import java.sql.DriverManager

fun main() {
    val database = createMigrationConfig().database

    DriverManager.getConnection(
        "jdbc:postgresql://${database.host}:${database.port}/${database.name}",
        database.user,
        database.password,
    ).use { applyLiquibaseMigration(it) }
}
