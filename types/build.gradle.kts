plugins {
    kotlin("jvm") version libs.versions.kotlin
    alias(libs.plugins.dokka)
    alias(libs.plugins.kotlinx.kover)
    alias(libs.plugins.gradle.ktlint)
    alias(libs.plugins.maven.publish)
}

kotlin.explicitApi()

dependencies {
    ktlintRuleset(libs.rulebook.ktlint)
    ktlintRuleset(libs.rulebook.ktlint.compose)

    implementation(libs.kotlinx.coroutines)

    testImplementation(kotlin("test-junit", libs.versions.kotlin.get()))
    testImplementation(libs.truth)
}
