val mcVersion = "1.17.1"

dependencies {
    api(project(":mobchip-base"))
    api(project(":mobchip-abstraction"))

    compileOnly("org.spigotmc:spigot-api:$mcVersion-R0.1-SNAPSHOT")
    compileOnly("org.spigotmc:spigot:$mcVersion-R0.1-SNAPSHOT")
    testImplementation("org.spigotmc:spigot-api:$mcVersion-R0.1-SNAPSHOT")
    testImplementation("org.spigotmc:spigot:$mcVersion-R0.1-SNAPSHOT")
}

java {
    sourceCompatibility = JavaVersion.VERSION_16
    targetCompatibility = JavaVersion.VERSION_16
}

tasks {
    javadoc {
        enabled = true

        source.removeAll { it.path.contains("abstraction") }

        options {
            require(this is StandardJavadocDocletOptions)

            links("https://hub.spigotmc.org/javadocs/spigot/")
            links("https://javadoc.io/doc/org.jetbrains/annotations-java5/23.0.0/")
        }
    }
}
