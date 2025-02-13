package ru.alexredby.db.migration

import org.jooq.codegen.GenerationTool
import org.jooq.codegen.KotlinGenerator
import org.jooq.meta.extensions.liquibase.LiquibaseDatabase
import org.jooq.meta.jaxb.Configuration
import org.jooq.meta.jaxb.Database
import org.jooq.meta.jaxb.Generator
import org.jooq.meta.jaxb.Property
import org.jooq.meta.jaxb.Target
import ru.alexredby.db.migration.jooq.overrideDefaultGeneratorStrategyForKotlin

fun main() {
    overrideDefaultGeneratorStrategyForKotlin()

    val jooqConfig = Configuration().apply {
        generator = Generator().apply {
            name = KotlinGenerator::class.qualifiedName
            database = Database().apply {
                // TODO: here should be used same database as for prod
                //       https://blog.jooq.org/using-testcontainers-to-generate-jooq-code/
                name = LiquibaseDatabase::class.qualifiedName
                properties = listOf(
                    Property().apply {
                        key = "rootPath"
                        value = "db-migration/src/main/resources/db"
                    },
                    Property().apply {
                        key = "scripts"
                        value = "changelog-master.yaml"
                    },
                )
            }
            target = Target().apply {
                packageName = "ru.alexredby.stocktaking.model"
                directory = "model/src/main/kotlin"
            }
        }
    }

    GenerationTool.generate(jooqConfig)
}
