/*
 * This file is generated by jOOQ.
 */
package ru.alexredby.stocktaking.model.tables


import java.time.LocalDateTime

import kotlin.collections.Collection

import org.jooq.Condition
import org.jooq.Field
import org.jooq.ForeignKey
import org.jooq.InverseForeignKey
import org.jooq.Name
import org.jooq.PlainSQL
import org.jooq.QueryPart
import org.jooq.Record
import org.jooq.SQL
import org.jooq.Schema
import org.jooq.Select
import org.jooq.Stringly
import org.jooq.Table
import org.jooq.TableField
import org.jooq.TableOptions
import org.jooq.UniqueKey
import org.jooq.impl.DSL
import org.jooq.impl.SQLDataType
import org.jooq.impl.TableImpl

import ru.alexredby.stocktaking.model.DefaultSchema
import ru.alexredby.stocktaking.model.keys.CONSTRAINT_2
import ru.alexredby.stocktaking.model.tables.records.ItemRecord


/**
 * This class is generated by jOOQ.
 */
@Suppress("UNCHECKED_CAST")
open class Item(
    alias: Name,
    path: Table<out Record>?,
    childPath: ForeignKey<out Record, ItemRecord>?,
    parentPath: InverseForeignKey<out Record, ItemRecord>?,
    aliased: Table<ItemRecord>?,
    parameters: Array<Field<*>?>?,
    where: Condition?
): TableImpl<ItemRecord>(
    alias,
    DefaultSchema.DEFAULT_SCHEMA,
    path,
    childPath,
    parentPath,
    aliased,
    parameters,
    DSL.comment(""),
    TableOptions.table(),
    where,
) {
    companion object {

        /**
         * The reference instance of <code>ITEM</code>
         */
        val ITEM: Item = Item()
    }

    /**
     * The class holding records for this type
     */
    override fun getRecordType(): Class<ItemRecord> = ItemRecord::class.java

    /**
     * The column <code>ITEM.ID</code>.
     */
    val ID: TableField<ItemRecord, Int?> = createField(DSL.name("ID"), SQLDataType.INTEGER.nullable(false), this, "")

    /**
     * The column <code>ITEM.NAME</code>.
     */
    val NAME: TableField<ItemRecord, String?> = createField(DSL.name("NAME"), SQLDataType.VARCHAR(1000000000), this, "")

    /**
     * The column <code>ITEM.SHORT_NAME</code>.
     */
    val SHORT_NAME: TableField<ItemRecord, String?> = createField(DSL.name("SHORT_NAME"), SQLDataType.VARCHAR(1000000000), this, "")

    /**
     * The column <code>ITEM.BASE_PRICE</code>.
     */
    val BASE_PRICE: TableField<ItemRecord, Int?> = createField(DSL.name("BASE_PRICE"), SQLDataType.INTEGER, this, "")

    /**
     * The column <code>ITEM.WIKI_LINK</code>.
     */
    val WIKI_LINK: TableField<ItemRecord, String?> = createField(DSL.name("WIKI_LINK"), SQLDataType.VARCHAR(1000000000), this, "")

    /**
     * The column <code>ITEM.UPDATED</code>.
     */
    val UPDATED: TableField<ItemRecord, LocalDateTime?> = createField(DSL.name("UPDATED"), SQLDataType.LOCALDATETIME(6), this, "")

    private constructor(alias: Name, aliased: Table<ItemRecord>?): this(alias, null, null, null, aliased, null, null)
    private constructor(alias: Name, aliased: Table<ItemRecord>?, parameters: Array<Field<*>?>?): this(alias, null, null, null, aliased, parameters, null)
    private constructor(alias: Name, aliased: Table<ItemRecord>?, where: Condition?): this(alias, null, null, null, aliased, null, where)

    /**
     * Create an aliased <code>ITEM</code> table reference
     */
    constructor(alias: String): this(DSL.name(alias))

    /**
     * Create an aliased <code>ITEM</code> table reference
     */
    constructor(alias: Name): this(alias, null)

    /**
     * Create a <code>ITEM</code> table reference
     */
    constructor(): this(DSL.name("ITEM"), null)
    override fun getSchema(): Schema? = if (aliased()) null else DefaultSchema.DEFAULT_SCHEMA
    override fun getPrimaryKey(): UniqueKey<ItemRecord> = CONSTRAINT_2
    override fun `as`(alias: String): Item = Item(DSL.name(alias), this)
    override fun `as`(alias: Name): Item = Item(alias, this)
    override fun `as`(alias: Table<*>): Item = Item(alias.qualifiedName, this)

    /**
     * Rename this table
     */
    override fun rename(name: String): Item = Item(DSL.name(name), null)

    /**
     * Rename this table
     */
    override fun rename(name: Name): Item = Item(name, null)

    /**
     * Rename this table
     */
    override fun rename(name: Table<*>): Item = Item(name.qualifiedName, null)

    /**
     * Create an inline derived table from this table
     */
    override fun where(condition: Condition?): Item = Item(qualifiedName, if (aliased()) this else null, condition)

    /**
     * Create an inline derived table from this table
     */
    override fun where(conditions: Collection<Condition>): Item = where(DSL.and(conditions))

    /**
     * Create an inline derived table from this table
     */
    override fun where(vararg conditions: Condition?): Item = where(DSL.and(*conditions))

    /**
     * Create an inline derived table from this table
     */
    override fun where(condition: Field<Boolean?>?): Item = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(condition: SQL): Item = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String): Item = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String, vararg binds: Any?): Item = where(DSL.condition(condition, *binds))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String, vararg parts: QueryPart): Item = where(DSL.condition(condition, *parts))

    /**
     * Create an inline derived table from this table
     */
    override fun whereExists(select: Select<*>): Item = where(DSL.exists(select))

    /**
     * Create an inline derived table from this table
     */
    override fun whereNotExists(select: Select<*>): Item = where(DSL.notExists(select))
}
