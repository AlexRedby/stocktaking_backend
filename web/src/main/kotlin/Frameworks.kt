package ru.alexredby.stocktaking

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import ru.alexredby.stocktaking.client.tarkovDevClientModule
import ru.alexredby.stocktaking.service.tarkovServiceModule
import ru.alexredby.stocktaking.service.tarkovStorageModule
import ru.alexredby.stocktaking.tarkovdev.adapter.ktor.GraphqlSimpleKtorAdapter
import ru.alexredby.stocktaking.tarkovdev.graphqlContextOf
import ru.alexredby.stocktaking.tarkovdev.graphqlJson

fun Application.configureKoin() {
    install(Koin) {
        slf4jLogger()
        modules(tarkovDevContextModule, tarkovDevClientModule, tarkovStorageModule, tarkovServiceModule)
    }
}

val tarkovDevContextModule = module {
    single {
        val client = HttpClient(CIO) {
            install(ContentNegotiation) {
                json(graphqlJson)
            }
        }
        graphqlContextOf(GraphqlSimpleKtorAdapter(client, "https://api.tarkov.dev/graphql"))
    }
}
