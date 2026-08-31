package ru.alexredby.stocktaking.configuration

import com.sksamuel.hoplite.ConfigException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ConfigLoaderTest {
    data class TestConfig(
        val database: DatabaseConfig,
    )

    data class TarkovTestConfig(
        val tarkovDev: TarkovDevConfig,
    )

    @Test
    fun `loads shared database config with application overrides`() {
        val database = loadConfig<TestConfig>("/valid-config.conf").database

        assertEquals("test-db", database.host)
        assertEquals(6432, database.port)
        assertEquals("test-stocktaking", database.name)
        assertEquals("test-user", database.user)
        assertEquals("test-password", database.password)
        assertEquals(10, database.cp.maximumPoolSize)
    }

    @Test
    fun `rejects missing required value`() {
        assertFailsWith<ConfigException> {
            loadConfig<TestConfig>("/missing-password-config.conf")
        }
    }

    @Test
    fun `rejects invalid typed value`() {
        assertFailsWith<ConfigException> {
            loadConfig<TestConfig>("/invalid-port-config.conf")
        }
    }

    @Test
    fun `loads Tarkov dev client configuration`() {
        val tarkovDev = loadConfig<TarkovTestConfig>("/valid-tarkov-dev-config.conf").tarkovDev

        assertEquals("https://example.com/graphql", tarkovDev.endpoint)
        assertEquals(1_000, tarkovDev.connectTimeoutMillis)
        assertEquals(2_000, tarkovDev.readTimeoutMillis)
        assertEquals(3, tarkovDev.retryCount)
    }
}
