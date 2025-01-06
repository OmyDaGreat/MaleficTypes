import cn.lalaki.pub.BaseCentralPortalPlusExtension.PublishingType

val user: String by properties
val repo: String by properties
val g: String by properties
val artifact: String by properties
val v: String by properties
val a = "$artifact-processor"
val localMavenRepo = uri(layout.buildDirectory.dir("repo").get())

plugins {
    kotlin("jvm")
    alias(libs.plugins.central)
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
    testImplementation(kotlin("test"))
}

publishing {
    publications {
        create<MavenPublication>("mavenProcessor") {
            groupId = g
            artifactId = a
            version = v

            from(components["java"])

            pom {
                name.set(repo)
                description.set("A processor to support the MaleficTypes UnionOverload annotation")
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
