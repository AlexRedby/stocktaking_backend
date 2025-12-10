package ru.alexredby.stocktaking

import io.ktor.server.application.Application
import io.ktor.server.application.install
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import ru.alexredby.stocktaking.client.tarkovDevClientModule
import ru.alexredby.stocktaking.client.tarkovDevContextModule
import ru.alexredby.stocktaking.service.tarkovServiceModule
import ru.alexredby.stocktaking.service.tarkovStorageModule

fun Application.configureKoin() {
    install(Koin) {
        slf4jLogger()
        modules(tarkovDevContextModule, tarkovDevClientModule, tarkovStorageModule, tarkovServiceModule)
    }
}
