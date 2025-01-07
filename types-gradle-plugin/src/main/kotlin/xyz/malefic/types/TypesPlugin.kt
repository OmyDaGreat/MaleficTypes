package xyz.malefic.types

import org.gradle.api.Plugin
import org.gradle.api.Project

const val VERSION = "2.1.1"

class TypesPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.plugins.withId("org.jetbrains.kotlin.jvm") {
            project.dependencies.apply {
                add("implementation", "xyz.malefic:types:$VERSION")
                println("applied xyz.malefic:types:$VERSION")
            }
        }
        project.plugins.withId("com.google.devtools.ksp") {
            project.dependencies.apply {
                add("ksp", "xyz.malefic:types-processor:$VERSION")
                println("applied xyz.malefic:types-processor:$VERSION")
            }
        }
    }
}
