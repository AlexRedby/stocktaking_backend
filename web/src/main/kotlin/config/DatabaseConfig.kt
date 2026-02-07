package ru.alexredby.stocktaking.config

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.config.getAs

import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.jooq.impl.DefaultConfiguration
import org.postgresql.Driver
import javax.sql.DataSource

fun createJooqDslContext(appConfig: ApplicationConfig): DSLContext =
    DSL.using(createJooqConfiguration(appConfig))

private fun createJooqConfiguration(appConfig: ApplicationConfig) = DefaultConfiguration().apply {
    setSQLDialect(SQLDialect.POSTGRES)
    setDataSource(createDataSource(appConfig))
}

private fun createDataSource(appConfig: ApplicationConfig): DataSource =
    HikariDataSource(createHikariConfig(appConfig))

private fun createHikariConfig(appConfig: ApplicationConfig) = HikariConfig().apply {
    // TODO: use some property wrapper
    val dbConfig = appConfig.config("database")
    val host: String = dbConfig.property("host").getAs()
    val port: Int = dbConfig.property("port").getAs()
    val name: String = dbConfig.property("name").getAs()
    val url = "jdbc:postgresql://$host:$port/$name"
    jdbcUrl = url
    username = dbConfig.property("user").getAs()
    password = dbConfig.property("password").getAs()
    driverClassName = Driver::class.qualifiedName

    val cpConfig = dbConfig.config("cp")
    maximumPoolSize = cpConfig.property("maximumPoolSize").getAs()
    minimumIdle = cpConfig.property("minimumIdle").getAs()
    connectionTimeout = cpConfig.property("connectionTimeout").getAs()
    idleTimeout = cpConfig.property("idleTimeout").getAs()
    maxLifetime = cpConfig.property("maxLifetime").getAs()
}
