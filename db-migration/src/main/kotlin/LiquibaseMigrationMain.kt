package ru.alexredby.db.migration

import ru.alexredby.db.migration.liquibase.applyLiquibaseMigration
import java.sql.DriverManager

fun main() {
    // TODO: move strings to properties
    DriverManager.getConnection(
        "jdbc:postgresql://localhost:5432/stocktaking",
        "stocktaking",
        "QLGcuAAxT2zg",
    ).use { applyLiquibaseMigration(it) }
}
