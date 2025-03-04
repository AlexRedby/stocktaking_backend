package ru.alexredby.stocktaking.util

import ru.alexredby.stocktaking.dto.CraftComponent
import ru.alexredby.stocktaking.dto.GraphItem
import ru.alexredby.stocktaking.tarkovdev.entity.ContainedItem
import ru.alexredby.stocktaking.tarkovdev.entity.Item
import kotlin.collections.asSequence

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
    )
}

fun ContainedItem.isTool() =
    this.attributes?.any { it?.type == "tool" && it.value == "true" } == true
