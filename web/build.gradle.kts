group = "ru.alexredby.stocktaking"
version = "1.0-SNAPSHOT"

plugins {
    ru.alexredby.convention.`kotlin-jvm`
    alias(libs.plugins.kotlin.serialization)
}

dependencies {
    implementation(projects.model)
    implementation(projects.clients.tarkovDevKobby)

    implementation(libs.jooq.kotlin)

    // Add this dependency to enable Kotlinx Serialization
    implementation(libs.kotlinx.serialization.json)
    // Extension over serialization for ktor
    implementation(libs.ktor.serialization.kotlinx.json)

    // DI in ktor 3.x
    implementation(libs.koin.ktor)
    implementation(libs.koin.logger.slf4j)

    // Need for the embedded server (EngineMain)
    implementation(libs.ktor.server.netty)
    // Need to read application.yaml
    implementation(libs.ktor.server.config.yaml)
    // Need to say a server in which format communicate in API
    implementation(libs.ktor.server.content.negotiation)

    runtimeOnly(libs.postgresql)
}
