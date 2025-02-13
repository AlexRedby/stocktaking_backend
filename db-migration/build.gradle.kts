group = "ru.alexredby.db.migration"
version = "1.0-SNAPSHOT"

plugins {
    ru.alexredby.convention.`kotlin-jvm`
}

dependencies {
    implementation(libs.javassist)

    implementation(libs.jooq.codegen)
    implementation(libs.jooq.meta)
    implementation(libs.jooq.meta.extension.liquibase)

    implementation(libs.liquibase)
    implementation(libs.liquibase.slf4j)

    runtimeOnly(libs.postgresql)
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xwhen-guards")
    }
}
