rootProject.name = "stocktaking"

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

include(
    "clients:tarkov-dev-apollo",
    "clients:tarkov-dev-kobby",
    "db-migration",
    "model",
    "web"
)

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
