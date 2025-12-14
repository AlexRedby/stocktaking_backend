package ru.alexredby.stocktaking.dto

data class GraphItem(
    val id: String,
    val fullName: String,
    val shortName: String,
    val image: String,
) {
    val crafts: MutableSet<Craft> = mutableSetOf()
    val usedIn: MutableSet<Craft> = mutableSetOf()
}

data class Craft(
    val result: GraphItem,
    val count: Double,
    val components: Set<CraftComponent>,
    val tools: Set<GraphItem>,
    val station: Station,
)

data class CraftComponent(
    val item: GraphItem,
    val count: Double,
)

data class Station(
    val id: String,
    val name: String,
    val level: Int,
    val image: String,
)

