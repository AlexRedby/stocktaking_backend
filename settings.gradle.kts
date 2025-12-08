rootProject.name = "stocktaking"

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

include("web", "db-migration", "model")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
