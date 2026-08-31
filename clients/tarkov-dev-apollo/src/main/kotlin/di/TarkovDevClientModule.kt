package ru.alexredby.stocktaking.client.tarkov.dev.di

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.network.http.DefaultHttpEngine
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import ru.alexredby.stocktaking.client.tarkov.dev.TarkovDevClient
import ru.alexredby.stocktaking.configuration.TarkovDevConfig

val tarkovDevClientModule = module {
    single {
        val config = get<TarkovDevConfig>()
        ApolloClient.Builder()
            .serverUrl(config.endpoint)
            .httpEngine(DefaultHttpEngine(config.connectTimeoutMillis, config.readTimeoutMillis))
            .build()
    }
    singleOf(::TarkovDevClient)
}
