package ru.alexredby.stocktaking.di

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import ru.alexredby.stocktaking.service.TarkovService
import ru.alexredby.stocktaking.service.TarkovStorage

val appModule = module {
    singleOf(::TarkovStorage)
    singleOf(::TarkovService)
}
