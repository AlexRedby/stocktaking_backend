package ru.alexredby.stocktaking.client.tarkov.dev

import com.apollographql.apollo.ApolloClient
import ru.alexredby.stocktaking.client.tarkov.dev.FetchAllBartersQuery.Data.Barter
import ru.alexredby.stocktaking.client.tarkov.dev.FetchAllCraftsQuery.Data.Craft

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
