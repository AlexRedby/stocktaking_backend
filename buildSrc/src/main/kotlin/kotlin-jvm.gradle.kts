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

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    // FIXME: Need gradle 9.4.x to upgrade this to 25
    jvmToolchain(21)
}
