plugins {
    // Lets Gradle download the Java 25 toolchain automatically, so the build works
    // on CI, JitPack and contributor machines without a preinstalled JDK 25.
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "UplineGuiApi"
