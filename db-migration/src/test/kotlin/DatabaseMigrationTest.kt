package ru.alexredby.db.migration

import org.testcontainers.postgresql.PostgreSQLContainer
import ru.alexredby.stocktaking.configuration.DatabaseConfig
import java.sql.DriverManager
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DatabaseMigrationTest {
    @Test
    fun `migration creates the schema on a fresh database`() {
        PostgreSQLContainer("postgres:18-alpine").use { postgres ->
            postgres.start()

            migrateDatabase(postgres.databaseConfig())

            DriverManager.getConnection(postgres.jdbcUrl, postgres.username, postgres.password).use { connection ->
                connection.createStatement().use { statement ->
                    statement.executeQuery("select count(*) from stocktaking.item").use { result ->
                        assertTrue(result.next())
                        assertEquals(0, result.getInt(1))
                    }
                }
            }
        }
    }

    private fun PostgreSQLContainer.databaseConfig() = DatabaseConfig(
        host = host,
        port = firstMappedPort,
        name = databaseName,
        user = username,
        password = password,
    )
}
