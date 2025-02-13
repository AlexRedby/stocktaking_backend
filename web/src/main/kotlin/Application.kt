package ru.alexredby.stocktaking

import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.netty.EngineMain
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import ru.alexredby.stocktaking.route.configureRouting

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json()
    }
}

fun Application.module() {
    configureSerialization()
    configureKoin()
    configureRouting()
}
