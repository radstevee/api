@file:Suppress("VulnerableLibrariesLocal", "LocalVariableName")

plugins {
    kotlin("jvm") version "1.9.22"
    `maven-publish`
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

    implementation ("org.slf4j:slf4j-simple:2.0.12")

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
            "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-opt-in=kotlinx.coroutines.DelicateCoroutinesApi",
        )
    }
}

java {
    withSourcesJar()
    withJavadocJar()
}

tasks.withType<Jar>() {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    manifest {
        attributes["Main-Class"] = "net.mcbrawls.api.MainKt"
    }

    configurations["compileClasspath"].forEach { file: File ->
        from(zipTree(file.absoluteFile))
    }
}

publishing {
    publications {
        register("mavenJava", MavenPublication::class) {
            from(components["java"])
        }
    }

    repositories {
        val env = System.getenv()
        val envUsername = env["MAVEN_USERNAME_ANDANTE"]
        val envPassword = env["MAVEN_PASSWORD_ANDANTE"]
        if (envUsername != null && envPassword != null) {
            maven {
                url = uri("https://maven.andante.dev/releases/")
                credentials {
                    username = envUsername
                    password = envPassword
                }
            }
        }
    }
}
