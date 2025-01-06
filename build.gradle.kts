import cn.lalaki.pub.BaseCentralPortalPlusExtension.PublishingType

val user: String by properties
val repo: String by properties
val g: String by properties
val artifact: String by properties
val v: String by properties
val localMavenRepo = uri(layout.buildDirectory.dir("repo").get())

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.central)
    alias(libs.plugins.dokka)
    `maven-publish`
    signing
}

group = g
version = v

repositories {
    mavenCentral()
}

dependencies {
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

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = g
            artifactId = artifact
            version = v

            from(components["java"])

            pom {
                name.set(repo)
                description.set("A union types framework for Kotlin")
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
    register("publishAllArtifacts") {
        group = "publishing"
        description = "Publish all artifacts to Maven Central"
        dependsOn("publishToCentralPortal")
        dependsOn(":types-gradle-plugin:publishToCentralPortal")
        dependsOn(":processor:publishToCentralPortal")
    }
    register("publishAllArtifactsToMavenLocal") {
        group = "publishing"
        description = "Publish all artifacts to Maven Local"
        dependsOn("publishToMavenLocal")
        dependsOn(":types-gradle-plugin:publishToMavenLocal")
        dependsOn(":processor:publishToMavenLocal")
    }
}

dokka {
    dokkaPublications.html {
        outputDirectory.set(layout.buildDirectory.dir("dokka"))
    }
}
