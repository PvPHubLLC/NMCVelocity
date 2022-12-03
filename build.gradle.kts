import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
    kotlin("kapt") version "1.7.10"
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "co.pvphub"
version = "-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        url = uri("https://nexus.velocitypowered.com/repository/maven-public/")
    }
    maven {
        url = uri("https://maven.pvphub.me/releases")
    }
    maven {
        url = uri("https://jitpack.io")
    }
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.7.10")
    compileOnly("com.velocitypowered:velocity-api:3.1.0")
    kapt("com.velocitypowered:velocity-api:3.1.0")
    implementation("co.pvphub:VelocityUtils:-SNAPSHOT")
    implementation("me.carleslc.Simple-YAML:Simple-Yaml:1.7.2")
    compileOnly("net.luckperms:api:5.4")
}

sourceSets["main"].resources.srcDir("src/resources/")

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks {
    build {
        dependsOn(shadowJar)
    }
}
tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    archiveBaseName.set("namemc")
    mergeServiceFiles()
}