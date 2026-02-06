package ru.alexredby.db.migration

import org.jooq.codegen.GenerationTool
import org.jooq.codegen.KotlinGenerator
import org.jooq.meta.jaxb.Configuration
import org.jooq.meta.jaxb.Database
import org.jooq.meta.jaxb.Generate
import org.jooq.meta.jaxb.Generator
import org.jooq.meta.jaxb.Jdbc
import org.jooq.meta.jaxb.Target
import org.jooq.meta.postgres.PostgresDatabase
import org.postgresql.Driver
import org.testcontainers.postgresql.PostgreSQLContainer
import ru.alexredby.db.migration.jooq.overrideDefaultGeneratorStrategyForKotlin
import ru.alexredby.db.migration.liquibase.applyLiquibaseMigration

fun main() {
    overrideDefaultGeneratorStrategyForKotlin()

    PostgreSQLContainer("postgres:18-alpine").use { tc ->
        tc.withDatabaseName("stocktaking")
        tc.start()

        tc.createConnection("").use {
            applyLiquibaseMigration(it)
        }

        GenerationTool.generate(makeJooqCodegenConfig(tc))
    }
}

fun makeJooqCodegenConfig(tc: PostgreSQLContainer) = Configuration().apply {
    jdbc = Jdbc().apply {
        driver = Driver::class.qualifiedName
        url = tc.jdbcUrl
        username = tc.username
        password = tc.password
    }
    generator = Generator().apply {
        database = Database().apply {
            name = PostgresDatabase::class.qualifiedName
            inputSchema = "stocktaking"
        }
        name = KotlinGenerator::class.qualifiedName
        target = Target().apply {
            packageName = "ru.alexredby.stocktaking.model"
            directory = "model/src/main/kotlin"
        }
        generate = Generate().apply {
            isJooqVersionReference = false
        }
    }
}
