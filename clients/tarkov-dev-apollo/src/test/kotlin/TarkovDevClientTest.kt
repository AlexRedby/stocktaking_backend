package ru.alexredby.stocktaking.client.tarkov.dev

import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.ApolloResponse
import com.apollographql.apollo.api.Error
import com.apollographql.apollo.api.Operation
import com.apollographql.apollo.exception.ApolloNetworkException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import java.net.SocketTimeoutException
import java.util.UUID
import kotlin.test.Test

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

        client.getCrafts() shouldBe listOf(craft)
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

        client.getBarters() shouldBe listOf(barter)
    }

    @Test
    fun `rejects a response with GraphQL errors`() = runCraftsClientTest { client, call ->
        coEvery { call.execute() } returns response(
            FetchAllCraftsQuery(),
            data = FetchAllCraftsQuery.Data(emptyList()),
            errors = listOf(Error.Builder("upstream error").build()),
        )

        val failure = shouldThrow<TarkovDevResponseException> { client.getCrafts() }
        failure.message shouldBe "Tarkov.dev returned GraphQL errors"
    }

    @Test
    fun `rejects a response without data`() = runCraftsClientTest { client, call ->
        coEvery { call.execute() } returns response(FetchAllCraftsQuery())

        val failure = shouldThrow<TarkovDevResponseException> { client.getCrafts() }
        failure.message shouldBe "Tarkov.dev response is missing data"
    }

    @Test
    fun `rejects a response without crafts`() = runCraftsClientTest { client, call ->
        coEvery { call.execute() } returns response(
            FetchAllCraftsQuery(),
            data = FetchAllCraftsQuery.Data(null),
        )

        val failure = shouldThrow<TarkovDevResponseException> { client.getCrafts() }
        failure.message shouldBe "Tarkov.dev response is missing crafts"
    }

    @Test
    fun `rejects a response without barters`() = runBartersClientTest { client, call ->
        coEvery { call.execute() } returns response(
            FetchAllBartersQuery(),
            data = FetchAllBartersQuery.Data(null),
        )

        val failure = shouldThrow<TarkovDevResponseException> { client.getBarters() }
        failure.message shouldBe "Tarkov.dev response is missing barters"
    }

    @Test
    fun `reports a timeout as upstream unavailability`() = runCraftsClientTest { client, call ->
        val timeout = SocketTimeoutException("timed out")
        coEvery { call.execute() } returns response(
            FetchAllCraftsQuery(),
            exception = ApolloNetworkException(platformCause = timeout),
        )

        val failure = shouldThrow<TarkovDevUnavailableException> { client.getCrafts() }
        failure.message shouldBe "Tarkov.dev is unavailable"
        val networkFailure = failure.cause.shouldBeInstanceOf<ApolloNetworkException>()
        networkFailure.platformCause shouldBe timeout
    }

    private fun runCraftsClientTest(
        block: suspend (TarkovDevClient, ApolloCall<FetchAllCraftsQuery.Data>) -> Unit,
    ) = runTest {
        val apolloClient = mockk<ApolloClient>()
        val call = mockk<ApolloCall<FetchAllCraftsQuery.Data>>()
        every { apolloClient.query(FetchAllCraftsQuery()) } returns call

        block(TarkovDevClient(apolloClient), call)
    }

    private fun runBartersClientTest(
        block: suspend (TarkovDevClient, ApolloCall<FetchAllBartersQuery.Data>) -> Unit,
    ) = runTest {
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
