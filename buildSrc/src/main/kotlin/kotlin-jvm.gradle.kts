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

    implementation(libs.hoplite.core) {
        because("Core library to load configuration properties")
    }
    implementation(libs.hoplite.hocon) {
        because("Extension library for HOCON configuration file support")
    }

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(25)
}
