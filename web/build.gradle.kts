group = "ru.alexredby.stocktaking"
version = "1.0-SNAPSHOT"

plugins {
    ru.alexredby.convention.`kotlin-jvm`
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kobby) // TODO: Check alternative - Apollo
    id("com.apollographql.apollo") version "4.3.3"
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
    implementation(libs.koin.ktor)
    implementation(libs.koin.logger.slf4j)

    // Need for the embedded server (EngineMain)
    implementation(libs.ktor.server.netty)
    // Need to read application.yaml
    implementation(libs.ktor.server.config.yaml)
    // Need to say a server in which format communicate in API
    implementation(libs.ktor.server.content.negotiation)

    implementation("com.apollographql.apollo:apollo-runtime:4.3.3")

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

apollo {
    service("tarkovdev") {
        packageName.set("ru.alexredby.stocktaking.tarkovdev.apollo")

        // TODO: how to build all graphql types without making query
        //  and do i need this?

        // Adds the given directory as a GraphQL source root
        srcDir("src/main/graphql")

        // This creates a downloadTarkovdevApolloSchemaFromIntrospection task which downloads *.graphqls file schema
        introspection {
            endpointUrl.set("https://api.tarkov.dev/graphql")
            // The path is interpreted relative to the current project
            schemaFile.set(file("src/main/graphql/tarkovdev.graphqls"))
        }
    }
}
