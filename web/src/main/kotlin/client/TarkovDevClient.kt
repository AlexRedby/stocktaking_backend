package ru.alexredby.stocktaking.client

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import ru.alexredby.stocktaking.tarkovdev.GraphqlContext
import ru.alexredby.stocktaking.tarkovdev.dto.GameMode
import ru.alexredby.stocktaking.tarkovdev.entity.Barter
import ru.alexredby.stocktaking.tarkovdev.entity.Craft

val tarkovDevClientModule = module {
    singleOf(::TarkovDevClient)
}

class TarkovDevClient(
    private val apiContext: GraphqlContext
) {
    suspend fun getBarters(): List<Barter> =
        apiContext
            .query {
                barters(gameMode = GameMode.pve) {
                    id()
                    requiredItems {
                        count()
                        attributes {
                            type()
                            name()
                            value()
                        }
                        item {
                            id()
                            name()
                            shortName()
                        }
                    }
                    rewardItems {
                        count()
                        attributes {
                            type()
                            name()
                            value()
                        }
                        item {
                            id()
                            name()
                            shortName()
                        }
                    }
                }
            }.barters
            ?.filterNotNull()
            ?: emptyList()

    suspend fun getCrafts(limit: Int? = null): List<Craft> =
        apiContext
            .query {
                crafts(gameMode = GameMode.pve, limit = limit) {
                    id()
                    requiredItems {
                        count()
                        attributes {
                            type()
                            name()
                            value()
                        }
                        item {
                            id()
                            name()
                            shortName()
                        }
                    }
                    rewardItems {
                        count()
                        attributes {
                            type()
                            name()
                            value()
                        }
                        item {
                            id()
                            name()
                            shortName()
                        }
                    }
                }
            }.crafts
            ?.filterNotNull()
            ?: emptyList()
}
