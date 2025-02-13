package ru.alexredby.db.migration

import liquibase.Contexts
import liquibase.Liquibase
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.DirectoryResourceAccessor
import java.nio.file.Paths
import java.sql.DriverManager

fun main() {
    // TODO: move strings to properties
    DriverManager
        .getConnection(
            "jdbc:postgresql://localhost:5432/stocktaking",
            "stocktaking",
            "QLGcuAAxT2zg",
        ).use { conn ->
            Liquibase(
                "/db-migration/src/main/resources/db/changelog-master.yaml",
                DirectoryResourceAccessor(Paths.get(".").toAbsolutePath()),
                JdbcConnection(conn),
            ).use { lb ->
                lb.update(Contexts())
            }
        }
}
