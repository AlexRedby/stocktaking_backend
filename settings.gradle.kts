rootProject.name = "stocktaking"

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0"
}

include("web", "db-migration", "model")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
