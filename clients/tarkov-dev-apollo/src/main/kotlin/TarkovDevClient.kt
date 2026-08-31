package ru.alexredby.stocktaking.client.tarkov.dev

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Query
import ru.alexredby.stocktaking.client.tarkov.dev.FetchAllBartersQuery.Data.Barter
import ru.alexredby.stocktaking.client.tarkov.dev.FetchAllCraftsQuery.Data.Craft
import ru.alexredby.stocktaking.configuration.TarkovDevConfig
import kotlin.coroutines.cancellation.CancellationException

class TarkovDevClient(
    private val apolloClient: ApolloClient,
    private val config: TarkovDevConfig,
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
        var attempt = 0
        while (true) {
            val response = try {
                apolloClient.query(query).execute()
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                if (attempt >= config.retryCount) {
                    throw TarkovDevUnavailableException(exception)
                }
                attempt++
                continue
            }

            val transportFailure = response.exception
            if (transportFailure != null) {
                if (attempt >= config.retryCount) {
                    throw TarkovDevUnavailableException(transportFailure)
                }
                attempt++
                continue
            }
            if (response.hasErrors()) {
                throw TarkovDevResponseException("Tarkov.dev returned GraphQL errors")
            }

            return response.data
                ?: throw TarkovDevResponseException("Tarkov.dev response is missing data")
        }
    }
}

class TarkovDevUnavailableException(cause: Throwable) :
    RuntimeException("Tarkov.dev is unavailable", cause)

class TarkovDevResponseException(message: String) : RuntimeException(message)
