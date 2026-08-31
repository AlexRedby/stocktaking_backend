package ru.alexredby.stocktaking

import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine
import io.ktor.server.plugins.compression.Compression
import io.ktor.server.plugins.compression.gzip
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.IgnoreTrailingSlash
import ru.alexredby.stocktaking.route.configureErrorHandling
import ru.alexredby.stocktaking.route.configureRouting

fun main() {
    val appConfig = createAppConfig()

    createKtorServer(appConfig)
        .start(wait = true)
}

fun createKtorServer(appConfig: AppConfig): EmbeddedServer<NettyApplicationEngine, NettyApplicationEngine.Configuration> {
    if (appConfig.server.development) {
        System.setProperty("io.ktor.development", "true")
    }

    return embeddedServer(Netty, port = appConfig.server.port) {
        // allows foo/ and foo to be treated the same
        install(IgnoreTrailingSlash)

        // enables gzip compression
        install(Compression) {
            gzip()
        }

        // setup json marshalling - provide your own jackson mapper if you have custom jackson modules
        install(ContentNegotiation) { json() }

        configureKoin(appConfig)
        configureErrorHandling()
        configureRouting()
    }
}
