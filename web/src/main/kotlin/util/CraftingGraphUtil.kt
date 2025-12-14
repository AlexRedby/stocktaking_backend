package ru.alexredby.stocktaking.util

import ru.alexredby.stocktaking.client.tarkov.dev.FetchAllBartersQuery.Data.Barter
import ru.alexredby.stocktaking.client.tarkov.dev.FetchAllCraftsQuery.Data.Craft
import ru.alexredby.stocktaking.client.tarkov.dev.fragment.ContainedItem
import ru.alexredby.stocktaking.client.tarkov.dev.fragment.ContainedItem.Item
import ru.alexredby.stocktaking.dto.CraftComponent
import ru.alexredby.stocktaking.dto.GraphItem
import ru.alexredby.stocktaking.dto.Station

fun List<ContainedItem?>.toCraftComponents(buffer: MutableMap<String, GraphItem>) = this.asSequence()
    .filterNotNull()
    .filter { !it.isTool() }
    .map { it.toCraftComponent(buffer) }
    .toSet()

fun List<ContainedItem?>.toTools(buffer: MutableMap<String, GraphItem>) = this.asSequence()
    .filterNotNull()
    .filter { it.isTool() }
    .map { it.item.toGraphItem(buffer) }
    .toSet()

fun ContainedItem.toCraftComponent(buffer: MutableMap<String, GraphItem>) = CraftComponent(
    item = item.toGraphItem(buffer),
    count = count,
)

fun Item.toGraphItem(buffer: MutableMap<String, GraphItem>) = buffer.getOrPut(id) {
    GraphItem(
        id = id,
        fullName = name!!,
        shortName = shortName!!,
        image = iconLink!!,
    )
}

fun ContainedItem.isTool() = this.attributes?.any { it?.type == "tool" && it.value == "true" } == true

fun Barter.toStation() = Station(
    name = trader.name,
    level = level,
    image = trader.imageLink!!,
)

fun Craft.toStation() = Station(
    name = station.name,
    level = level,
    image = station.imageLink!!,
)
