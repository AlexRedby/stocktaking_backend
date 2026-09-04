package ru.alexredby.stocktaking.configuration

import com.sksamuel.hoplite.ConfigException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class ConfigLoaderTest {
    data class TestConfig(
        val database: DatabaseConfig,
    )

    @Test
    fun `loads shared database config with application overrides`() {
        val database = loadConfig<TestConfig>("/valid-config.conf").database

        database.host shouldBe "test-db"
        database.port shouldBe 6432
        database.name shouldBe "test-stocktaking"
        database.user shouldBe "test-user"
        database.password shouldBe "test-password"
        database.cp.maximumPoolSize shouldBe 10
    }

    @Test
    fun `rejects missing required value`() {
        shouldThrow<ConfigException> {
            loadConfig<TestConfig>("/missing-password-config.conf")
        }
    }

    @Test
    fun `rejects invalid typed value`() {
        shouldThrow<ConfigException> {
            loadConfig<TestConfig>("/invalid-port-config.conf")
        }
    }
}
