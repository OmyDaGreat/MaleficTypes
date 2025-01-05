plugins {
    kotlin("jvm")
    alias(libs.plugins.ksp)
}

group = "xyz.malefic.types"
version = "1.1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.ksp)
    testImplementation(kotlin("test"))
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    withJavadocJar()
    withSourcesJar()
}

kotlin {
    jvmToolchain {
        this.languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks.test {
    useJUnitPlatform()
}
