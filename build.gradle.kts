@file:Suppress("VulnerableLibrariesLocal", "LocalVariableName")

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
    val ktor_version = property("ktor_version")

    testImplementation("org.jetbrains.kotlin:kotlin-test")

    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("io.ktor:ktor-server-auth:$ktor_version")

    implementation ("com.mysql:mysql-connector-j:8.2.+")

    implementation("com.mojang:datafixerupper:6.0.6")
    implementation("dev.andante:codex:1.4.0")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)

    compilerOptions {
        freeCompilerArgs.addAll(
            "-Xopt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-Xopt-in=kotlinx.coroutines.DelicateCoroutinesApi",
        )
    }
}
