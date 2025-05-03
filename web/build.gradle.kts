group = "ru.alexredby.stocktaking"
version = "1.0-SNAPSHOT"

plugins {
    ru.alexredby.convention.`kotlin-jvm`
    // TODO: Move version in libs.versions.toml
    kotlin("plugin.serialization") version "2.1.10"
    id("io.github.ermadmi78.kobby") version "4.1.1" // TODO: Check alternative - Apollo
}

dependencies {
    implementation(projects.model)

    implementation(libs.jooq.kotlin)

    // Add this dependency to enable Kotlinx Serialization
    implementation(libs.kotlinx.serialization.json)
    // Extension over serialization for ktor
    implementation(libs.ktor.serialization.kotlinx.json)

    // TODO: move client to separate module
    // Add this dependency to enable default Ktor adapters generation
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.negotiation)

    // DI in ktor 3.x
    implementation(libs.koin.ktor3)
    implementation(libs.koin.logger.slf4j)

    // Need for the embedded server (EngineMain)
    implementation(libs.ktor.server.netty)
    // Need to read application.yaml
    implementation(libs.ktor.server.config.yaml)
    // Need to say a server in which format communicate in API
    implementation(libs.ktor.server.content.negotiation)

    runtimeOnly(libs.postgresql)
}

kobby {
    kotlin {
        scalars = mapOf(
            "ID" to typeOf("kotlin", "String"),
        )
        // Is root package name for generated DSL
        // should be relative to GraphQL schema directory
        relativePackage = false

        // Root package name for generated DSL
        packageName = "ru.alexredby.stocktaking.tarkovdev"

        // Output directory for generated DSL
        // org.gradle.api.file.Directory
        outputDirectory = project.layout.buildDirectory
            .dir("generated/sources/kobby/main/kotlin")
            .get()

        // Configuration of DSL context generation (entry point to DSL)
        context {
            // Context package name relative to root package name
            // By default is empty
            packageName = null // String

            // Name of generated DSL context
            // By default is name of GraphQL schema file
            // or `graphql` if there are multiple schema files
            name = "graphql"
        }
    }
}
