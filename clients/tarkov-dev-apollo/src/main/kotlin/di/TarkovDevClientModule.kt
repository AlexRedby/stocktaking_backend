package ru.alexredby.stocktaking.client.tarkov.dev.di

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.exception.ApolloNetworkException
import com.apollographql.apollo.interceptor.RetryOnErrorInterceptor
import com.apollographql.apollo.interceptor.RetryStrategy
import com.apollographql.apollo.network.http.DefaultHttpEngine
import kotlinx.coroutines.delay
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import ru.alexredby.stocktaking.client.tarkov.dev.TarkovDevClient
import ru.alexredby.stocktaking.client.tarkov.dev.TarkovDevConfig
import kotlin.math.pow
import kotlin.time.Duration.Companion.seconds

fun tarkovDevClientModule(config: TarkovDevConfig) = module {
    single {
        ApolloClient.Builder()
            .serverUrl(config.endpoint)
            .httpEngine(DefaultHttpEngine(config.connectTimeoutMillis, config.readTimeoutMillis))
            .retryOnError(true)
            .retryOnErrorInterceptor(
                RetryOnErrorInterceptor(retryStrategy = tarkovDevRetryStrategy(config.retryCount)),
            )
            .build()
    }
    singleOf(::TarkovDevClient)
}

internal fun tarkovDevRetryStrategy(retryCount: Int) = RetryStrategy { context ->
    if (context.attempt >= retryCount || context.response.exception !is ApolloNetworkException) {
        return@RetryStrategy false
    }

    delay(2.0.pow(context.attempt).coerceAtMost(60.0).seconds)
    true
}
