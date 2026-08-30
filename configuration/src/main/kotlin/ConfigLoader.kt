package ru.alexredby.stocktaking.configuration

import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.ExperimentalHoplite
import com.sksamuel.hoplite.addResourceSource

@OptIn(ExperimentalHoplite::class)
inline fun <reified T : Any> loadConfig(resource: String): T = ConfigLoaderBuilder.default()
    .addResourceSource(resource)
    .withExplicitSealedTypes()
    .strict()
    .build()
    .loadConfigOrThrow<T>()
