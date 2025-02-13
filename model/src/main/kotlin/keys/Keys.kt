/*
 * This file is generated by jOOQ.
 */
package ru.alexredby.stocktaking.model.keys


import org.jooq.UniqueKey
import org.jooq.impl.DSL
import org.jooq.impl.Internal

import ru.alexredby.stocktaking.model.tables.Item
import ru.alexredby.stocktaking.model.tables.records.ItemRecord



// -------------------------------------------------------------------------
// UNIQUE and PRIMARY KEY definitions
// -------------------------------------------------------------------------

val CONSTRAINT_2: UniqueKey<ItemRecord> = Internal.createUniqueKey(Item.ITEM, DSL.name("CONSTRAINT_2"), arrayOf(Item.ITEM.ID), true)
