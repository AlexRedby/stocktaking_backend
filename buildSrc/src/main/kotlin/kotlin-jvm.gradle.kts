package ru.alexredby.convention

import org.gradle.accessors.dm.LibrariesForLibs

val libs = the<LibrariesForLibs>()

plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.kotlin.logging) {
        because("Planing to use kotlin idiomatic logging library")
    }
    runtimeOnly(libs.slf4j.api) {
        because("kotlin-logging-jvm requires this to work")
    }
    runtimeOnly(libs.logback) {
        because("Project choice for slf4j logging provider")
    }

    // TODO: Do i need it if i have ktor configuration?
    //  Just need find out how to make it type-safe like here - https://github.com/perracodex/ktor-config (not on maven)
    implementation(libs.hoplite.core) {
        because("Core library to load configuration properties")
    }
    implementation(libs.hoplite.yaml) {
        because("Extension library for YAML configuration file support")
    }

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    // FIXME: Need gradle 9.4.x to upgrade this to 25
    jvmToolchain(21)
}
