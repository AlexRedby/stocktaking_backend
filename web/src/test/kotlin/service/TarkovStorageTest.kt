package ru.alexredby.stocktaking.service

import io.kotest.assertions.throwables.shouldThrow
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import ru.alexredby.stocktaking.client.tarkov.dev.TarkovDevClient
import kotlin.test.Test
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.TestTimeSource

class TarkovStorageTest {
    @Test
    fun `concurrent first requests share one upstream load including an empty graph`() = runTest {
        val client = emptyClient(craftsDelayMillis = 50)
        val storage = TarkovStorage(client)

        List(10) { async { storage.getFullCraftableTree() } }.awaitAll()

        coVerify(exactly = 1) { client.getCrafts() }
        coVerify(exactly = 1) { client.getBarters() }
    }

    @Test
    fun `expired graph refreshes and recovers after a failed refresh`() = runTest {
        val client = mockk<TarkovDevClient>()
        val timeSource = TestTimeSource()
        val refreshFailure = IllegalStateException("refresh failed")
        var craftsRequest = 0
        coEvery { client.getCrafts() } answers {
            craftsRequest++
            if (craftsRequest == 2) throw refreshFailure
            emptyList()
        }
        coEvery { client.getBarters() } returns emptyList()
        val storage = TarkovStorage(client, 100.milliseconds, timeSource)

        storage.getFullCraftableTree()
        timeSource += 100.milliseconds
        shouldThrow<IllegalStateException> { storage.getFullCraftableTree() }
        storage.getFullCraftableTree()

        coVerify(exactly = 3) { client.getCrafts() }
        coVerify(exactly = 2) { client.getBarters() }
    }

    private fun emptyClient(craftsDelayMillis: Long = 0): TarkovDevClient = mockk<TarkovDevClient>().also { client ->
        coEvery { client.getCrafts() } coAnswers {
            delay(craftsDelayMillis)
            emptyList()
        }
        coEvery { client.getBarters() } returns emptyList()
    }
}
