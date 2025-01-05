import cn.lalaki.pub.BaseCentralPortalPlusExtension.PublishingType

val user = "OmyDaGreat"
val repo = "MaleficTypes"
val g = "xyz.malefic"
val artifact = "types"
val v = "2.0.0-BETA"
val localMavenRepo = uri(layout.buildDirectory.dir("repo").get())

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.spotless)
    alias(libs.plugins.central)
    alias(libs.plugins.dokka)
    alias(libs.plugins.ksp)
    `maven-publish`
    signing
}

group = g
version = v

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.ksp)
    implementation(project(":processor"))
    ksp(project(":processor"))
    testImplementation(kotlin("test"))
}

spotless {
    kotlin {
        ktlint()
    }
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

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = g
            artifactId = artifact
            version = v

            from(components["java"])

            pom {
                name.set(repo)
                description.set("A union types framework for Kotlin/JVM")
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

tasks.apply {
    build {
        dependsOn(dokkaGenerate)
    }
    test {
        useJUnitPlatform()
    }
}

dokka {
    dokkaPublications.html {
        outputDirectory.set(layout.buildDirectory.dir("dokka"))
    }
}
