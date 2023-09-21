plugins {
    id("idea")
    kotlin("jvm") version "1.9.10"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "io.github.tsgrissom.essentialskt"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.essentialsx.net/snapshots/")
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/central")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    implementation(group="com.uchuhimo", name="kotlinx-bimap", version="1.2")
    compileOnly(group="org.spigotmc", name="spigot-api", version="1.20.1-R0.1-SNAPSHOT")
    compileOnly(group="net.essentialsx", name="EssentialsX", version="2.20.1-SNAPSHOT")
    implementation(group="com.github.stefvanschie.inventoryframework", name="IF", version="0.10.11")
}

kotlin {
    jvmToolchain(17)
}

val localPluginsDir = file("${System.getProperty("user.home")}/Servers/Development/plugins")

tasks.register("copyJarToDir", Copy::class) {
    from(tasks.named("shadowJar"))
    into(localPluginsDir)
}

tasks.named("copyJarToDir") {
    dependsOn("shadowJar") // Task depends on shadowJar task
    description = "Copy shadowJar JAR to external directory"
}

tasks.named("build") {
    finalizedBy("copyJarToDir") // Execute after build task
    description = "Build a fat JAR, then copy it to external directory"
}