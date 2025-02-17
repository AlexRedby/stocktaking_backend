package ru.alexredby.stocktaking.service

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import ru.alexredby.stocktaking.client.TarkovDevClient
import ru.alexredby.stocktaking.dto.BarterAttributes
import ru.alexredby.stocktaking.dto.D3Node

val tarkovServiceModule = module {
    singleOf(::TarkovService)
}

class TarkovService(
    private val tarkovDevClient: TarkovDevClient
) {
    suspend fun getBarterTree(): D3Node<BarterAttributes> {
        return createRootNode(tarkovDevClient.getBarters().flatMap { b ->
            val children = b.requiredItems.filterNotNull().map {
                D3Node(
                    name = it.item.shortName!!,
                    attributes = BarterAttributes(
                        id = it.item.id,
                        fullName = it.item.name!!,
                        count = it.count.toInt()
                    ),
                )
            }
            b.rewardItems.filterNotNull().map {
                D3Node(
                    name = it.item.shortName!!,
                    attributes = BarterAttributes(
                        id = it.item.id,
                        fullName = it.item.name!!,
                        count = it.count.toInt()
                    ),
                    children = children,
                )
            }
        })
    }

    private fun createRootNode(children: List<D3Node<BarterAttributes>>) = D3Node(
        name = "Root",
        attributes = BarterAttributes(
            id = "0",
            fullName = "Root",
            count = 0
        ),
        children = children,
    )
}
