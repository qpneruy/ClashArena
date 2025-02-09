import org.gradle.api.JavaVersion

plugins {
    id("xyz.jpenilla.run-paper") version "2.3.1"
    java
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "org.qpneruy"
version = "1.0"

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
    maven("https://oss.sonatype.org/content/groups/public/") {
        name = "sonatype"
    }
    maven("https://jitpack.io") {
        name = "jitpack"
    }
    maven("https://repo.alessiodp.com/releases/") {
        name = "alessiodp-repo"
    }
    maven("https://libraries.minecraft.net/") {
        name = "minecraft-repo"
    }
}

dependencies {
    implementation("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.projectlombok:lombok:1.18.34")
    implementation("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
    compileOnly("com.alessiodp.parties:parties-api:3.2.15")
    compileOnly("com.mojang:authlib:1.5.25")
    implementation ("org.junit.jupiter:junit-jupiter-api:5.8.1")
    implementation ("org.junit.jupiter:junit-jupiter-engine:5.8.1")

}

val targetJavaVersion = 21
java {
    val javaVersion = JavaVersion.toVersion(targetJavaVersion)
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
    if (JavaVersion.current() < javaVersion) {
        toolchain.languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    if (targetJavaVersion >= 10 || JavaVersion.current().isJava10Compatible) {
        options.release.set(targetJavaVersion)
    }
}

tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }
}

tasks.named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
    archiveBaseName.set("ClashArena")
    archiveVersion.set(version.toString())
    archiveClassifier.set("")
//    destinationDirectory.set(file("X:/plugins"))
}

tasks.register<Copy>("copyToServerPlugins") {
    from(tasks.named("shadowJar"))
    into("X:/plugins")
}

tasks.named("build") {
    finalizedBy("copyToServerPlugins")
}
