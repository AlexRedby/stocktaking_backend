import ru.alexredby.convention.registerRunWithDotEnv

group = "ru.alexredby.db.migration"
version = "1.0-SNAPSHOT"

val codegen by sourceSets.creating {
    compileClasspath += sourceSets.main.get().output
    runtimeClasspath += sourceSets.main.get().output
}

plugins {
    ru.alexredby.convention.`kotlin-jvm`
    application
}

dependencies {
    implementation(projects.configuration)
    implementation(libs.liquibase)
    implementation(libs.liquibase.slf4j)
    runtimeOnly(libs.postgresql)
    testImplementation(libs.testcontainers.postregesql)

    add(codegen.implementationConfigurationName, libs.javassist)
    add(codegen.implementationConfigurationName, libs.jooq.codegen)
    add(codegen.implementationConfigurationName, libs.jooq.meta)
    add(codegen.implementationConfigurationName, libs.testcontainers.postregesql)
    add(codegen.implementationConfigurationName, libs.postgresql)
}

configurations[codegen.implementationConfigurationName]
    .extendsFrom(configurations.implementation.get())
configurations[codegen.runtimeOnlyConfigurationName]
    .extendsFrom(configurations.runtimeOnly.get())

application {
    mainClass.set("ru.alexredby.db.migration.LiquibaseMigrationMainKt")
}

registerRunWithDotEnv(application.mainClass)

tasks.register<JavaExec>("jooqCodegen") {
    group = "code generation"
    description = "Generates jOOQ Kotlin sources from the migrated schema"
    workingDir(rootProject.projectDir)
    classpath = codegen.runtimeClasspath
    mainClass.set("ru.alexredby.db.migration.JooqCodegenMainKt")
    jvmArgs("--add-opens=java.base/java.lang=ALL-UNNAMED")
}
