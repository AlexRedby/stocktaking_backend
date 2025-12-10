group = "ru.alexredby.stocktaking"
version = "1.0-SNAPSHOT"

plugins {
    ru.alexredby.convention.`kotlin-jvm`
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kobby)
}

dependencies {
    // Add this dependency to enable Kotlinx Serialization
    implementation(libs.kotlinx.serialization.json)
    // Extension over serialization for ktor
    implementation(libs.ktor.serialization.kotlinx.json)

    // Add this dependency to enable default Ktor adapters generation
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.negotiation)

    // DI in ktor 3.x
    implementation(libs.koin.ktor)
    implementation(libs.koin.logger.slf4j)
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
