package ru.alexredby.stocktaking.config

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.jooq.impl.DefaultConfiguration
import org.postgresql.Driver
import ru.alexredby.stocktaking.AppConfig
import javax.sql.DataSource

fun createJooqDslContext(appConfig: AppConfig): DSLContext =
    DSL.using(createJooqConfiguration(appConfig))

private fun createJooqConfiguration(appConfig: AppConfig) = DefaultConfiguration().apply {
    setSQLDialect(SQLDialect.POSTGRES)
    setDataSource(createDataSource(appConfig))
}

private fun createDataSource(appConfig: AppConfig): DataSource =
    HikariDataSource(createHikariConfig(appConfig))

private fun createHikariConfig(appConfig: AppConfig) = HikariConfig().apply {
    val dbConfig = appConfig.database
    jdbcUrl = "jdbc:postgresql://${dbConfig.host}:${dbConfig.port}/${dbConfig.name}"
    username = dbConfig.user
    password = dbConfig.password
    driverClassName = Driver::class.qualifiedName

    val cpConfig = dbConfig.cp
    maximumPoolSize = cpConfig.maximumPoolSize
    minimumIdle = cpConfig.minimumIdle
    connectionTimeout = cpConfig.connectionTimeout
    idleTimeout = cpConfig.idleTimeout
    maxLifetime = cpConfig.maxLifetime
}
