package ru.alexredby.stocktaking.client.tarkov.dev

import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.ApolloResponse
import com.apollographql.apollo.api.Error
import com.apollographql.apollo.api.Operation
import com.apollographql.apollo.exception.ApolloNetworkException
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import java.net.SocketTimeoutException
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

class TarkovDevClientTest {
    @Test
    fun `returns crafts from a successful response`() = runCraftsClientTest { client, call ->
        val craft = FetchAllCraftsQuery.Data.Craft(
            id = "craft-id",
            requiredItems = emptyList(),
            rewardItems = emptyList(),
            station = FetchAllCraftsQuery.Data.Craft.Station("station-id", "Workbench", null),
            level = 1,
        )
        coEvery { call.execute() } returns response(
            FetchAllCraftsQuery(),
            data = FetchAllCraftsQuery.Data(listOf(craft)),
        )

        assertEquals(listOf(craft), client.getCrafts())
    }

    @Test
    fun `returns barters from a successful response`() = runBartersClientTest { client, call ->
        val barter = FetchAllBartersQuery.Data.Barter(
            id = "barter-id",
            requiredItems = emptyList(),
            rewardItems = emptyList(),
            trader = FetchAllBartersQuery.Data.Barter.Trader("trader-id", "Mechanic", null),
            level = 2,
        )
        coEvery { call.execute() } returns response(
            FetchAllBartersQuery(),
            data = FetchAllBartersQuery.Data(listOf(barter)),
        )

        assertEquals(listOf(barter), client.getBarters())
    }

    @Test
    fun `rejects a response with GraphQL errors`() = runCraftsClientTest { client, call ->
        coEvery { call.execute() } returns response(
            FetchAllCraftsQuery(),
            data = FetchAllCraftsQuery.Data(emptyList()),
            errors = listOf(Error.Builder("upstream error").build()),
        )

        val failure = assertFailsWith<TarkovDevResponseException> { client.getCrafts() }
        assertEquals("Tarkov.dev returned GraphQL errors", failure.message)
    }

    @Test
    fun `rejects a response without data`() = runCraftsClientTest { client, call ->
        coEvery { call.execute() } returns response(FetchAllCraftsQuery())

        val failure = assertFailsWith<TarkovDevResponseException> { client.getCrafts() }
        assertEquals("Tarkov.dev response is missing data", failure.message)
    }

    @Test
    fun `rejects a response without crafts`() = runCraftsClientTest { client, call ->
        coEvery { call.execute() } returns response(
            FetchAllCraftsQuery(),
            data = FetchAllCraftsQuery.Data(null),
        )

        val failure = assertFailsWith<TarkovDevResponseException> { client.getCrafts() }
        assertEquals("Tarkov.dev response is missing crafts", failure.message)
    }

    @Test
    fun `rejects a response without barters`() = runBartersClientTest { client, call ->
        coEvery { call.execute() } returns response(
            FetchAllBartersQuery(),
            data = FetchAllBartersQuery.Data(null),
        )

        val failure = assertFailsWith<TarkovDevResponseException> { client.getBarters() }
        assertEquals("Tarkov.dev response is missing barters", failure.message)
    }

    @Test
    fun `reports a timeout as upstream unavailability`() = runCraftsClientTest { client, call ->
        val timeout = SocketTimeoutException("timed out")
        coEvery { call.execute() } returns response(
            FetchAllCraftsQuery(),
            exception = ApolloNetworkException(platformCause = timeout),
        )

        val failure = assertFailsWith<TarkovDevUnavailableException> { client.getCrafts() }
        assertEquals("Tarkov.dev is unavailable", failure.message)
        val networkFailure = assertIs<ApolloNetworkException>(failure.cause)
        assertEquals(timeout, networkFailure.platformCause)
    }

    private fun runCraftsClientTest(
        block: suspend (TarkovDevClient, ApolloCall<FetchAllCraftsQuery.Data>) -> Unit,
    ) = runBlocking {
        val apolloClient = mockk<ApolloClient>()
        val call = mockk<ApolloCall<FetchAllCraftsQuery.Data>>()
        every { apolloClient.query(FetchAllCraftsQuery()) } returns call

        block(TarkovDevClient(apolloClient), call)
    }

    private fun runBartersClientTest(
        block: suspend (TarkovDevClient, ApolloCall<FetchAllBartersQuery.Data>) -> Unit,
    ) = runBlocking {
        val apolloClient = mockk<ApolloClient>()
        val call = mockk<ApolloCall<FetchAllBartersQuery.Data>>()
        every { apolloClient.query(FetchAllBartersQuery()) } returns call

        block(TarkovDevClient(apolloClient), call)
    }

    private fun <D : Operation.Data> response(
        operation: Operation<D>,
        data: D? = null,
        errors: List<Error>? = null,
        exception: ApolloNetworkException? = null,
    ): ApolloResponse<D> = ApolloResponse.Builder(
        operation = operation,
        requestUuid = UUID.randomUUID(),
    ).apply {
        data?.let(::data)
        errors?.let(::errors)
        exception?.let(::exception)
    }.build()
}
