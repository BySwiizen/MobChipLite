import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("org.sonarqube") version "4.0.0.2929"
    id("io.github.goooler.shadow") version "8.1.7" apply false
    kotlin("jvm") version "2.0.0"

    java
    `maven-publish`
    `java-library`
    jacoco
}

val pGroup = "me.gamercoder215"
val pVersion = "1.10.1-SNAPSHOT"
val pAuthor = "datatags" // Originally "gmitch215"

val github = "$pAuthor/MobChipLite" // Originally "$pAuthor/MobChip"

val jdProjects = listOf(
    ":mobchip-bukkit",
    ":mobchip-base",
    ":mobchip-1_17_R1"
)

tasks {
    register("allJavadoc", Javadoc::class.java) {
        jdProjects.forEach { dependsOn(project(it).tasks["javadoc"]) }

        enabled = true
        title = "MobChip $version API"

        source = files(jdProjects.map { project(it).sourceSets["main"].allJava }).asFileTree
        classpath = files(jdProjects.map { project(it).sourceSets["main"].compileClasspath })

        options {
            require(this is StandardJavadocDocletOptions)

            overview = "bukkit/src/main/javadoc/overview.html"

            links("https://hub.spigotmc.org/javadocs/spigot/")
            links("https://javadoc.io/doc/org.jetbrains/annotations-java5/23.0.0/")

            addStringOption("tag", "apiNote:a:API Note:")
        }
    }
}

allprojects {
    group = pGroup
    version = pVersion
    description = "PathfinderGoal and Behavior NMS Wrapper"

    apply(plugin = "maven-publish")
    apply<JavaPlugin>()
    apply<JavaLibraryPlugin>()

    repositories {
        mavenCentral()
        mavenLocal()

        maven("https://jitpack.io")
        maven("https://plugins.gradle.org/m2/")
        maven("https://oss.sonatype.org/content/repositories/snapshots")
        maven("https://oss.sonatype.org/content/repositories/central")

        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven("https://repo.codemc.org/repository/nms/")
        maven("https://repo.papermc.io/repository/maven-public/")
    }

    publishing {
        publications {
            create<MavenPublication>("maven") {
                groupId = pGroup
                artifactId = project.name
                version = pVersion

                pom {
                    description.set(project.description)
                    url.set("https://github.com/$github")

                    licenses {
                        license {
                            name.set("GPL-3.0")
                            url.set("https://github.com/$github/blob/master/LICENSE")
                            distribution.set("repo")
                        }
                    }

                    developers {
                        developer {
                            id.set(pAuthor)
                            roles.add("Owner")
                        }
                    }

                    scm {
                        connection.set("scm:git:git://$github.git")
                        developerConnection.set("scm:git:ssh://$github.git")
                        url.set("https://github.com/$github")
                    }

                    inceptionYear.set("2022")

                    ciManagement {
                        system.set("Github Actions")
                        url.set("https://github.com/$github/actions")
                    }

                    issueManagement {
                        system.set("Github")
                        url.set("https://github.com/$github/issues/")
                    }
                }

                from(components["java"])
            }
        }

        repositories {
            maven {
                credentials {
                    username = System.getenv("JENKINS_USERNAME")
                    password = System.getenv("JENKINS_PASSWORD")
                }

                val releases = "https://repo.codemc.io/repository/maven-releases/"
                val snapshots = "https://repo.codemc.io/repository/maven-snapshots/"
                url = uri(if (version.toString().endsWith("SNAPSHOT")) snapshots else releases)
            }
        }
    }
}

subprojects {
    apply<JacocoPlugin>()
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.sonarqube")
    apply(plugin = "io.github.goooler.shadow")

    dependencies {
        testImplementation("org.mockito:mockito-core:5.12.0")
        testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")

        testImplementation("org.spigotmc:spigot-api:1.17.1-R0.1-SNAPSHOT")
        testImplementation("net.md-5:bungeecord-chat:1.20-R0.2")

        compileOnly("org.jetbrains:annotations:24.1.0")
        compileOnly("org.jetbrains.kotlin:kotlin-stdlib")
    }

    tasks {
        clean {
            delete("logs")
        }

        compileJava {
            options.encoding = "UTF-8"
            options.isDeprecation = false
            options.isWarnings = false
            options.compilerArgs.addAll(listOf("-Xlint:all", "-Xlint:-processing", "-Xlint:-options"))
        }

        jacocoTestReport {
            dependsOn(test)

            reports {
                xml.required.set(false)
                csv.required.set(false)

                html.required.set(true)
                html.outputLocation.set(layout.buildDirectory.dir("jacocoHtml"))
            }
        }

        test {
            useJUnitPlatform()
            testLogging {
                events("passed", "skipped", "failed")
            }
            finalizedBy(jacocoTestReport)
            testLogging { exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL }
        }

        javadoc {
            enabled = false

            options {
                require(this is StandardJavadocDocletOptions)

                encoding = "UTF-8"
                memberLevel = JavadocMemberLevel.PROTECTED

                addStringOption("tag", "apiNote:a:API Note:")
            }
        }

        jar.configure {
            dependsOn("shadowJar")
            archiveClassifier.set("dev")
        }

        withType<ShadowJar> {
            manifest {
                attributes(
                    "Implementation-Title" to project.name,
                    "Implementation-Version" to project.version,
                    "Implementation-Vendor" to pAuthor
                )
            }
            archiveClassifier.set("")
            archiveFileName.set("${project.name}-${project.version}.jar")
        }
    }

    artifacts {
        add("default", tasks.getByName<ShadowJar>("shadowJar"))
    }
}
