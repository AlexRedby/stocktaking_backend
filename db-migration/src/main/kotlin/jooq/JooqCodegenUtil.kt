package ru.alexredby.db.migration.jooq

import javassist.ClassPool

/**
 * Preventing jOOQ default code generation strategy to generate excessive directories in Kotlin due
 * [official guidelines](https://kotlinlang.org/docs/coding-conventions.html#directory-structure).
 * Overrides bytecode of [org.jooq.codegen.AbstractGeneratorStrategy.patchPackageName] method with usage of javassist.
 *
 * **WARNING:** This method, if needed, should be launched before any jOOQ codegen classes is touched.
 */
fun overrideDefaultGeneratorStrategyForKotlin() {
    val classPool = ClassPool.getDefault()
    val stringClass = classPool.get("java.lang.String")
    val generatorStrategy = classPool.get("org.jooq.codegen.AbstractGeneratorStrategy")
    val patchPackageName = generatorStrategy.getDeclaredMethod("patchPackageName", arrayOf(stringClass))
    patchPackageName.insertAfter("\$_ = \$_.substring(getTargetPackage().length());")
    generatorStrategy.toClass()
}
