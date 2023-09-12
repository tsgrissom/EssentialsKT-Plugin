plugins {
    id("idea")
    kotlin("jvm") version "1.9.0"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "io.github.tsgrissom.essentialskt"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/central")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.20.1-R0.1-SNAPSHOT")
}

kotlin {
    jvmToolchain(8)
}

val devServerPluginsDir = file("${System.getProperty("user.home")}/Servers/Development/plugins")

tasks.register("copyJarToDir", Copy::class) {
    from(tasks.named("shadowJar"))
    into(devServerPluginsDir)
}

tasks.named("copyJarToDir") {
    dependsOn("shadowJar") // Task depends on shadowJar task
    description = "Copy shadowJar JAR to external directory"
}

tasks.named("build") {
    finalizedBy("copyJarToDir") // Execute after build task
    description = "Build a fat JAR, then copy it to external directory"
}