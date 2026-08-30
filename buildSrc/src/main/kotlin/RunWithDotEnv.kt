package ru.alexredby.convention

import io.github.klahap.dotenv.DotEnvBuilder
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.register

fun Project.registerRunWithDotEnv(mainClass: String): TaskProvider<JavaExec> =
    registerRunWithDotEnv(providers.provider { mainClass })

fun Project.registerRunWithDotEnv(mainClass: Provider<String>): TaskProvider<JavaExec> {
    val sourceSets = extensions.getByType<SourceSetContainer>()

    return tasks.register<JavaExec>("runWithDotEnv") {
        group = "application"
        description = "Runs the application with the repository-root .env file"

        this.mainClass.set(mainClass)
        classpath = sourceSets.getByName("main").runtimeClasspath

        val dotenv = DotEnvBuilder.dotEnv {
            addFileIfExists("$rootDir/.env")
        }
        environment(dotenv)
    }
}
