package ru.alexredby.stocktaking.client.tarkov.dev

import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.ApolloResponse
import com.apollographql.apollo.api.Error
import com.apollographql.apollo.exception.ApolloNetworkException
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import ru.alexredby.stocktaking.configuration.TarkovDevConfig
import java.net.SocketTimeoutException
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlin.test.assertFailsWith

class TarkovDevClientTest {
    @Test
    fun `valid empty crafts response stays empty`() = runClientTest { client, call ->
        coEvery { call.execute() } returns response(data = FetchAllCraftsQuery.Data(emptyList()))

        assertTrue(client.getCrafts().isEmpty())
    }

    @Test
    fun `partial GraphQL response is rejected`() = runClientTest { client, call ->
        coEvery { call.execute() } returns response(
            data = FetchAllCraftsQuery.Data(emptyList()),
            errors = listOf(Error.Builder("upstream error").build()),
        )

        assertFailsWith<TarkovDevResponseException> { client.getCrafts() }
        coVerify(exactly = 1) { call.execute() }
    }

    @Test
    fun `transport failures are retried up to the configured limit`() = runClientTest(retryCount = 2) { client, call ->
        coEvery { call.execute() } returns response(exception = ApolloNetworkException())

        assertFailsWith<TarkovDevUnavailableException> { client.getCrafts() }
        coVerify(exactly = 3) { call.execute() }
    }

    @Test
    fun `timeout is reported as upstream unavailability`() = runClientTest { client, call ->
        coEvery { call.execute() } throws SocketTimeoutException("timed out")

        val failure = assertFailsWith<TarkovDevUnavailableException> { client.getCrafts() }
        assertIs<SocketTimeoutException>(failure.cause)
    }

    private fun runClientTest(
        retryCount: Int = 0,
        block: suspend (TarkovDevClient, ApolloCall<FetchAllCraftsQuery.Data>) -> Unit,
    ) = runBlocking {
        val apolloClient = mockk<ApolloClient>()
        val call = mockk<ApolloCall<FetchAllCraftsQuery.Data>>()
        every { apolloClient.query(FetchAllCraftsQuery()) } returns call

        block(TarkovDevClient(apolloClient, TarkovDevConfig(retryCount = retryCount)), call)
    }

    private fun response(
        data: FetchAllCraftsQuery.Data? = null,
        errors: List<Error>? = null,
        exception: ApolloNetworkException? = null,
    ): ApolloResponse<FetchAllCraftsQuery.Data> = ApolloResponse.Builder(
        operation = FetchAllCraftsQuery(),
        requestUuid = UUID.randomUUID(),
    ).apply {
        data?.let(::data)
        errors?.let(::errors)
        exception?.let(::exception)
    }.build()
}
