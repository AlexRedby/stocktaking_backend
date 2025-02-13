package ru.alexredby.stocktaking.service

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import ru.alexredby.stocktaking.client.TarkovDevClient
import ru.alexredby.stocktaking.dto.BarterTreeNode

val tarkovServiceModule = module {
    singleOf(::TarkovService)
}

class TarkovService(
    private val tarkovDevClient: TarkovDevClient
) {
    suspend fun getBarterTree(): List<BarterTreeNode> {
        return tarkovDevClient.getBarters().flatMap { b ->
            val children = b.requiredItems.filterNotNull().map {
                BarterTreeNode(
                    id = it.item.id,
                    name = it.item.name!!,
                    shortName = it.item.shortName!!,
                )
            }
            b.rewardItems.filterNotNull().map {
                BarterTreeNode(
                    id = it.item.id,
                    name = it.item.name!!,
                    shortName = it.item.shortName!!,
                    children = children,
                )
            }
        }
    }
}
