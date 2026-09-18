plugins {
    kotlin("jvm") version "2.3.20"
    `java-library`
    id("com.gradleup.shadow") version "9.6.1"
    id("xyz.jpenilla.run-paper") version "3.0.2"
    id("org.jetbrains.dokka") version "2.0.0"
    `maven-publish`
}

group = "com.uplineservers"
version = "1.4"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:26.2.build.+")
    api("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
}

tasks {
    runServer {
        minecraftVersion("26.2")
    }
}

val targetJavaVersion = 25
kotlin {
    jvmToolchain(targetJavaVersion)
}

java {
    // Ship sources so consumers get navigation and docs in their IDE.
    withSourcesJar()
}

tasks.build {
    dependsOn("shadowJar")
}

tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = "uplineguiapi"

            // The plain API jar: consumers add it as compileOnly and run the
            // shaded plugin jar on the server, so the Kotlin stdlib is not shaded in here.
            from(components["java"])

            pom {
                name = "UplineGuiApi"
                description = "Advanced custom GUI API for Minecraft plugins"
                url = "https://github.com/UplineServers/UplineGuiApi"

                developers {
                    developer {
                        id = "emanuelscura"
                        name = "Emanuel Scura"
                        url = "https://emanuelscura.me"
                    }
                }

                scm {
                    url = "https://github.com/UplineServers/UplineGuiApi"
                    connection = "scm:git:https://github.com/UplineServers/UplineGuiApi.git"
                    developerConnection = "scm:git:git@github.com:UplineServers/UplineGuiApi.git"
                }
            }
        }
    }

    repositories {
        // Publishes to GitHub Packages when credentials are present:
        //   GITHUB_ACTOR / GITHUB_TOKEN  (set automatically inside GitHub Actions)
        // Locally, `./gradlew publishToMavenLocal` needs no credentials at all.
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/UplineServers/UplineGuiApi")
            credentials {
                username = System.getenv("GITHUB_ACTOR") ?: providers.gradleProperty("gpr.user").orNull
                password = System.getenv("GITHUB_TOKEN") ?: providers.gradleProperty("gpr.key").orNull
            }
        }
    }
}

dokka {
    moduleName = "UplineGuiApi"

    dokkaSourceSets.main {
        includes.from("docs/module.md")

        // "View source" links from the generated pages back to GitHub.
        sourceLink {
            localDirectory = file("src/main/kotlin")
            remoteUrl("https://github.com/UplineServers/UplineGuiApi/blob/main/src/main/kotlin")
            remoteLineSuffix = "#L"
        }

        // Link Paper/Bukkit types to their own javadocs instead of rendering them as plain text.
        externalDocumentationLinks.register("paper") {
            url("https://jd.papermc.io/paper/1.21/")
            packageListUrl("https://jd.papermc.io/paper/1.21/element-list")
        }
    }

    dokkaPublications.html {
        outputDirectory = layout.buildDirectory.dir("docs/api")
    }
}
