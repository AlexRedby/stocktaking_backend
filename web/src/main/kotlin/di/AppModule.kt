package ru.alexredby.stocktaking.di

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import ru.alexredby.stocktaking.config.createJooqDslContext
import ru.alexredby.stocktaking.readConfig
import ru.alexredby.stocktaking.service.TarkovService
import ru.alexredby.stocktaking.service.TarkovStorage

val appModule = module {
    single { readConfig() }
    single { createJooqDslContext(get()) }

    singleOf(::TarkovStorage)
    singleOf(::TarkovService)
}
