plugins {
    // Apply the application plugin to add support for building a CLI application in Java.
    application
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

dependencies {
    // This dependency is used by the application.
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.jetbrains:annotations:24.0.0")
}

application {
    // Define the main class for the application.
    mainClass.set("net.minecraft.client.Minecraft")
}

tasks.test {
    // Use JUnit Platform for unit tests.
    useJUnitPlatform()
}
