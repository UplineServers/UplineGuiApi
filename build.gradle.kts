plugins {
    kotlin("jvm") version "2.2.20-Beta1"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("xyz.jpenilla.run-paper") version "2.3.1"
}

group = "com.uplineservers"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
    maven("https://oss.sonatype.org/content/groups/public/") {
        name = "sonatype"
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.8-R0.1-SNAPSHOT")

    // Adventure API — only include what you use
    compileOnly("net.kyori:adventure-api:4.14.0")
    compileOnly("net.kyori:adventure-text-serializer-gson:4.14.0")

    // Kotlin stdlib
    compileOnly("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
}

kotlin {
    jvmToolchain(21)
}

tasks {
    runServer {
        minecraftVersion("1.21")
    }

    test {
        useJUnitPlatform()
    }

    processResources {
        val props = mapOf("version" to version)
        inputs.properties(props)
        filteringCharset = "UTF-8"
        filesMatching("plugin.yml") {
            expand(props)
        }
    }

    // Use shadowJar if you *must* bundle dependencies
    named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
        minimize() // remove unused classes
        archiveClassifier.set("") // makes the final jar name clean: CustomGUI.jar

        // Optional: relocate if needed
        // relocate("net.kyori", "com.uplineservers.libs.kyori")

        // Optional: exclude if server provides them
        exclude("META-INF/*.kotlin_module")
        exclude("kotlin/**")
    }

    build {
        dependsOn(shadowJar)
    }
}