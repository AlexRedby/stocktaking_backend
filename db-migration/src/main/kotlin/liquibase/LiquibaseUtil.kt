package ru.alexredby.db.migration.liquibase

import liquibase.Contexts
import liquibase.Liquibase
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.DirectoryResourceAccessor
import java.nio.file.Paths
import java.sql.Connection

fun applyLiquibaseMigration(connection: Connection) = Liquibase(
    "/db-migration/src/main/resources/db/changelog-master.yaml",
    DirectoryResourceAccessor(Paths.get(".").toAbsolutePath()),
    JdbcConnection(connection),
).update(Contexts())
