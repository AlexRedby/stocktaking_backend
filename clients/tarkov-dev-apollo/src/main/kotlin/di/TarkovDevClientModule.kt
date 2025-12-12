package ru.alexredby.stocktaking.client.tarkov.dev.di

import com.apollographql.apollo.ApolloClient
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import ru.alexredby.stocktaking.client.tarkov.dev.TarkovDevClient

val tarkovDevClientModule = module {
    single {
        ApolloClient.Builder()
            .serverUrl("https://api.tarkov.dev/graphql")
            .build()
    }
    singleOf(::TarkovDevClient)
}
