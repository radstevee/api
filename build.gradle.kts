plugins {
    kotlin("jvm") version "1.9.22"
}

group = "net.mcbrawls"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.fabricmc.net/")
    maven("https://mvn.lumine.io/repository/maven-public/")
    maven("https://maven.andante.dev/releases/")
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    implementation("com.mojang:datafixerupper:6.0.6")
    implementation("dev.andante:codex:1.4.0")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}
