import ru.alexredby.convention.registerRunWithDotEnv

group = "ru.alexredby.db.migration"
version = "1.0-SNAPSHOT"

plugins {
    ru.alexredby.convention.`kotlin-jvm`
}

dependencies {
    implementation(projects.configuration)

    implementation(libs.javassist)

    implementation(libs.jooq.codegen)
    implementation(libs.jooq.meta)

    implementation(libs.liquibase)
    implementation(libs.liquibase.slf4j)

    implementation(libs.testcontainers.postregesql)
    implementation(libs.postgresql)
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xwhen-guards")
    }
}

registerRunWithDotEnv("ru.alexredby.db.migration.LiquibaseMigrationMainKt")
