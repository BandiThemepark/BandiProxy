plugins {
    kotlin("jvm") version "1.6.10"
    kotlin("kapt") version "1.6.10"
    kotlin("plugin.serialization") version "1.6.10"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "net.bandithemepark"
version = "1.0.0"

repositories {
    mavenCentral()

    maven("https://repo.velocitypowered.com/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://repo.eclipse.org/content/repositories/paho-releases/")
}

dependencies {
    implementation(kotlin("reflect"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("net.kyori:adventure-extra-kotlin:4.7.0")

    compileOnly("com.velocitypowered:velocity-api:3.3.0-SNAPSHOT")
    annotationProcessor("com.velocitypowered:velocity-annotation-processor:4.0.0-SNAPSHOT")

    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("org.eclipse.paho:org.eclipse.paho.client.mqttv3:1.2.5")
    implementation("net.kyori:adventure-text-minimessage:4.17.0")
}

tasks.build {
    dependsOn(tasks.shadowJar.get())
}