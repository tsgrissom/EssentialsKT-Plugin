plugins {
    id("idea")
    kotlin("jvm") version "1.9.0"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    application
}

group = "io.github.tsgrissom.testpluginkt"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

dependencies {
    testImplementation(kotlin("test"))
    compileOnly(group="org.spigotmc", name="plugin-annotations", version="1.2.3-SNAPSHOT")
    annotationProcessor(group="org.spigotmc", name="plugin-annotations", version="1.2.3-SNAPSHOT")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}

application {
    mainClass.set("MainKt")
}