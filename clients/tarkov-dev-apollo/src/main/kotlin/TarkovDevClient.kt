package ru.alexredby.stocktaking.client.tarkov.dev

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Query
import ru.alexredby.stocktaking.client.tarkov.dev.FetchAllBartersQuery.Data.Barter
import ru.alexredby.stocktaking.client.tarkov.dev.FetchAllCraftsQuery.Data.Craft

class TarkovDevClient(
    private val apolloClient: ApolloClient,
) {
    suspend fun getBarters(): List<Barter> {
        return execute(FetchAllBartersQuery()).barters
            ?.filterNotNull()
            ?: throw TarkovDevResponseException("Tarkov.dev response is missing barters")
    }

    suspend fun getCrafts(): List<Craft> {
        return execute(FetchAllCraftsQuery()).crafts
            ?.filterNotNull()
            ?: throw TarkovDevResponseException("Tarkov.dev response is missing crafts")
    }

    private suspend fun <D : Query.Data> execute(query: Query<D>): D {
        val response = apolloClient.query(query).execute()
        response.exception?.let {
            throw TarkovDevUnavailableException(it)
        }
        if (response.hasErrors()) {
            throw TarkovDevResponseException("Tarkov.dev returned GraphQL errors")
        }

        return response.data
            ?: throw TarkovDevResponseException("Tarkov.dev response is missing data")
    }
}

class TarkovDevUnavailableException(cause: Throwable) :
    RuntimeException("Tarkov.dev is unavailable", cause)

class TarkovDevResponseException(message: String) : RuntimeException(message)
