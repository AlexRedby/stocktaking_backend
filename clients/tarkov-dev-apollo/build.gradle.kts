group = "ru.alexredby.stocktaking.client.tarkov.dev"
version = "1.0-SNAPSHOT"

plugins {
    ru.alexredby.convention.`kotlin-jvm`
    alias(libs.plugins.apollo)
}

dependencies {
    // Add this dependency to enable Kotlinx Serialization
    implementation(libs.kotlinx.serialization.json)
    // Extension over serialization for ktor
    implementation(libs.ktor.serialization.kotlinx.json)

    // DI in ktor 3.x
    implementation(libs.koin.ktor)
    implementation(libs.koin.logger.slf4j)

    implementation(libs.apollo.runtime)
}

apollo {
    service("tarkovdev") {
        packageName.set("ru.alexredby.stocktaking.client.tarkov.dev")

        // What codegen to use. One of "operationBased", "responseBased"
        codegenModels.set("operationBased")

        // This creates a downloadTarkovdevApolloSchemaFromIntrospection task which downloads *.graphqls file schema
        introspection {
            endpointUrl.set("https://api.tarkov.dev/graphql")
            // The path is interpreted relative to the current project
            schemaFile.set(file("src/main/graphql/tarkov-dev.graphqls"))
        }
    }
}
