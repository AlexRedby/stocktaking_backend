group = "ru.alexredby.stocktaking"
version = "1.0-SNAPSHOT"

plugins {
    ru.alexredby.convention.`kotlin-jvm`
    kotlin("plugin.serialization") version "2.1.10"
    id("io.github.ermadmi78.kobby") version "4.1.1" // TODO: Check alternative - Apollo
}

dependencies {
    implementation(projects.model)

    implementation(libs.jooq.kotlin)

    // TODO: move to libs.versions.toml
    // Add this dependency to enable Kotlinx Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")

    // Add this dependency to enable default Ktor adapters generation
    implementation("io.ktor:ktor-client-cio:3.0.3")
    implementation("io.ktor:ktor-client-content-negotiation:3.0.3")
    implementation("io.ktor:ktor-serialization-kotlinx-json:3.0.3")

    implementation("io.insert-koin:koin-ktor:4.0.2")
    implementation("io.insert-koin:koin-logger-slf4j:4.0.2")

    // Needed for making web application
    implementation("io.ktor:ktor-server-core:3.0.3")
    implementation("io.ktor:ktor-server-host-common:3.0.3")
    implementation("io.ktor:ktor-server-netty:3.0.3")
    implementation("io.ktor:ktor-server-config-yaml:3.0.3")
    implementation("io.ktor:ktor-server-content-negotiation:3.0.3")

    runtimeOnly(libs.postgresql)

    testImplementation("io.ktor:ktor-server-test-host:3.0.3")
    testImplementation("io.insert-koin:koin-test:4.0.2")
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
