package ru.alexredby.stocktaking.client.tarkov.dev

import com.apollographql.apollo.ApolloClient
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import ru.alexredby.stocktaking.client.tarkov.dev.FetchAllBartersQuery.Barter
import ru.alexredby.stocktaking.client.tarkov.dev.FetchAllCraftsQuery.Craft

val tarkovDevClientModule = module {
    includes(apolloClientModule)
    singleOf(::TarkovDevClient)
}

val apolloClientModule = module {
    single {
        ApolloClient.Builder()
            .serverUrl("https://api.tarkov.dev/graphql")
            .build()
    }
}

class TarkovDevClient(
    private val apolloClient: ApolloClient
) {
    suspend fun getBarters(): List<Barter> {
        return apolloClient.query(FetchAllBartersQuery()).execute()
            .data?.barters
            ?.filterNotNull()
            ?: emptyList()
    }

    suspend fun getCrafts(): List<Craft> {
        return apolloClient.query(FetchAllCraftsQuery()).execute()
            .data?.crafts
            ?.filterNotNull()
            ?: emptyList()
    }
}
