import cn.lalaki.pub.BaseCentralPortalPlusExtension.PublishingType

val user: String by properties
val repo: String by properties
val g: String by properties
val artifact: String by properties
val v: String by properties
val localMavenRepo = uri(layout.buildDirectory.dir("repo").get())

plugins {
    alias(libs.plugins.gradle.publish)
    alias(libs.plugins.central)
    alias(libs.plugins.ksp)
    `java-gradle-plugin`
    `maven-publish`
    `kotlin-dsl`
    signing
}

group = g
version = v

repositories {
    mavenCentral()
}

dependencies {
    implementation(gradleApi())
    implementation(libs.ksp)
}

gradlePlugin {
    website = "https://$user/$repo"
    vcsUrl = "https://$user/$repo.git"
    plugins {
        create("typesPlugin") {
            id = "$g.$artifact"
            displayName = "Plugin for libraries using MaleficTypes"
            description =
                "A plugin that provides an annotation for Unions, allowing functions using them to be called without the MaleficTypes library"
            tags = listOf("malefic", "kotlin")
            implementationClass = "xyz.malefic.types.TypesPlugin"
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("pluginMaven") {
            pom {
                name.set(repo)
                description.set(
                    "A plugin that provides an annotation for Unions, allowing functions using them to be called without the MaleficTypes library",
                )
                url.set("https://github.com/$user/$repo")
                developers {
                    developer {
                        name.set("Om Gupta")
                        email.set("ogupta4242@gmail.com")
                    }
                }
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/$user/$repo.git")
                    developerConnection.set("scm:git:ssh://github.com/$user/$repo.git")
                    url.set("https://github.com/$user/$repo")
                }
            }
        }
        repositories {
            maven {
                url = localMavenRepo
            }
        }
    }
}

afterEvaluate {
    tasks.withType(GenerateMavenPom::class.java) {
        doFirst {
            pom.name = repo
            pom.url = "https://github.com/$user/$repo"
            pom.description =
                "A plugin that provides an annotation for Unions, allowing functions using them to be called without the MaleficTypes library"

            pom.developers {
                developer {
                    name.set("Om Gupta")
                    email.set("ogupta4242@gmail.com")
                }
            }
            pom.licenses {
                license {
                    name.set("MIT License")
                    url.set("https://opensource.org/licenses/MIT")
                }
            }
            pom.scm {
                connection.set("scm:git:git://github.com/$user/$repo.git")
                developerConnection.set("scm:git:ssh://github.com/$user/$repo.git")
                url.set("https://github.com/$user/$repo")
            }
        }
    }
}

signing {
    useGpgCmd()
    sign(publishing.publications)
}

centralPortalPlus {
    url = localMavenRepo
    username = System.getenv("centralPortalUsername") ?: ""
    password = System.getenv("centralPortalPassword") ?: ""
    publishingType = PublishingType.AUTOMATIC
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

tasks.apply {
    test {
        useJUnitPlatform()
    }
}
