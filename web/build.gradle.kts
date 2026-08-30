import ru.alexredby.convention.registerRunWithDotEnv

group = "ru.alexredby.stocktaking"
version = "1.0-SNAPSHOT"

plugins {
    ru.alexredby.convention.`kotlin-jvm`
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ktor)
}

dependencies {
    implementation(projects.configuration)
    implementation(projects.model)
    implementation(projects.clients.tarkovDevApollo)

    implementation(libs.jooq.kotlin)

    // Add this dependency to enable Kotlinx Serialization
    implementation(libs.kotlinx.serialization.json)
    // Extension over serialization for ktor
    implementation(libs.ktor.serialization.kotlinx.json)

    // DI in ktor
    implementation(libs.koin.ktor)
    implementation(libs.koin.logger.slf4j)

    implementation(libs.ktor.server.netty) {
        because("Need for the embedded server")
    }
    implementation(libs.ktor.server.compression) {
        because("Need for HTTP package compression")
    }
    implementation(libs.ktor.server.content.negotiation) {
        because("Need to say a server in which format communicate in API")
    }

    implementation(libs.postgresql) {
        because("Driver for postgresql connection")
    }
    implementation(libs.hikari.cp) {
        because("Need connection pool for DB")
    }
    implementation(libs.jooq) {
        because("Need for DB requests")
    }
    implementation(libs.jooq.kotlin) {
        because("Need to get kotlin native feel when working with jOOQ")
    }
}

application {
    mainClass.set("ru.alexredby.stocktaking.ApplicationKt")
}

////////////////////////////////////////////////////////////////////////////////
// Tasks
////////////////////////////////////////////////////////////////////////////////

registerRunWithDotEnv(application.mainClass)
