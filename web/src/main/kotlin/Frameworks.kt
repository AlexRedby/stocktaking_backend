package ru.alexredby.stocktaking

import io.ktor.server.application.Application
import io.ktor.server.application.install
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import ru.alexredby.stocktaking.client.tarkov.dev.di.tarkovDevClientModule
import ru.alexredby.stocktaking.di.appModule

fun Application.configureKoin() {
    install(Koin) {
        slf4jLogger()
        modules(
            module {
                single { environment.config }
            },
            appModule,
            tarkovDevClientModule,
        )
    }
}
