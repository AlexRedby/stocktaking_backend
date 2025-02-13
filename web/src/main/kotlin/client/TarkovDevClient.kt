package ru.alexredby.stocktaking.client

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import ru.alexredby.stocktaking.tarkovdev.GraphqlContext
import ru.alexredby.stocktaking.tarkovdev.dto.GameMode
import ru.alexredby.stocktaking.tarkovdev.entity.Barter

val tarkovDevClientModule = module {
    singleOf(::TarkovDevClient)
}

class TarkovDevClient(
    private val apiContext: GraphqlContext
) {
    suspend fun getBarters(): List<Barter> =
        apiContext
            .query {
                barters(gameMode = GameMode.pve, limit = 5) {
                    id()
                    requiredItems {
                        count()
                        item {
                            id()
                            name()
                            shortName()
                        }
                    }
                    rewardItems {
                        count()
                        item {
                            id()
                            name()
                            shortName()
                        }
                    }
                    source()
                }
            }.barters
            ?.filterNotNull()
            ?: emptyList()
}
