package ru.alexredby.stocktaking.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jooq.DSLContext
import kotlin.coroutines.cancellation.CancellationException

class HealthService(
    private val tarkovStorage: TarkovStorage,
    private val dslContext: DSLContext,
) {
    suspend fun checkReadiness() {
        try {
            val databaseReady = withContext(Dispatchers.IO) {
                dslContext.connectionResult { connection -> connection.isValid(DATABASE_VALIDATION_TIMEOUT_SECONDS) }
            }
            check(databaseReady) { "Database is not ready" }
            tarkovStorage.getFullCraftableTree()
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            throw ReadinessCheckException(exception)
        }
    }

    private companion object {
        const val DATABASE_VALIDATION_TIMEOUT_SECONDS = 2
    }
}

internal class ReadinessCheckException(cause: Throwable) : RuntimeException("Readiness check failed", cause)
