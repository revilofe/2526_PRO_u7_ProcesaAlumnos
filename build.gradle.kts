plugins {
    application
    kotlin("jvm") version "2.3.0"
}

group = "org.iesra"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(21)
}

application {
    mainClass = "org.iesra.procesaalumnos.MainKt"
}

tasks.test {
    useJUnitPlatform()
}
