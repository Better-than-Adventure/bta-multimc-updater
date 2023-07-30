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
    implementation("com.google.guava:guava:30.1.1-jre")
    implementation("org.kohsuke:github-api:1.315")
    implementation("com.google.code.gson:gson:2.10.1")
}

application {
    // Define the main class for the application.
    mainClass.set("net.minecraft.client.Minecraft")
}

tasks.test {
    // Use JUnit Platform for unit tests.
    useJUnitPlatform()
}
