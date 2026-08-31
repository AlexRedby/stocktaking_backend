package ru.alexredby.db.migration

import liquibase.Contexts
import liquibase.Liquibase
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.ClassLoaderResourceAccessor
import ru.alexredby.stocktaking.configuration.DatabaseConfig
import java.sql.Connection
import java.sql.DriverManager

fun main() {
    migrateDatabase(createMigrationConfig().database)
}

internal fun migrateDatabase(database: DatabaseConfig) {
    DriverManager.getConnection(
        "jdbc:postgresql://${database.host}:${database.port}/${database.name}",
        database.user,
        database.password,
    ).use(::applyLiquibaseMigration)
}

fun applyLiquibaseMigration(connection: Connection) = Liquibase(
    "db/changelog-master.yaml",
    ClassLoaderResourceAccessor(),
    JdbcConnection(connection),
).update(Contexts())
