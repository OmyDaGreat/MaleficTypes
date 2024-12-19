pluginManagement.repositories {
    gradlePluginPortal()
    mavenCentral()
}
dependencyResolutionManagement.repositories.mavenCentral()

rootProject.name = "MaleficTypes"

include("types", "types-extension")
include("sample")
include("website")
