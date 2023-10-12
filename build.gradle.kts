plugins {
    id("org.jetbrains.kotlin.jvm") version "1.6.10"
    id("io.papermc.paperweight.userdev") version "1.5.5"
    id("io.freefair.lombok") version "6.3.0"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("de.undercouch.download") version "5.0.1"
}

val version: String by project
val name: String by project

repositories {
    mavenLocal {
        content {
            includeGroup("org.bukkit")
            includeGroup("org.spigotmc")
        }
    }
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://arcanearts.jfrog.io/artifactory/archives")
    maven("https://mvn.lumine.io/repository/maven-public/")
    maven("https://jitpack.io")
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots")
    maven("https://mvn.lumine.io/repository/maven/")
    maven("https://repo.triumphteam.dev/snapshots")
    maven("https://repo.mineinabyss.com/releases")
    maven(url = "https://hub.jeff-media.com/nexus/repository/jeff-media-public/")
}

val main: String = "com.volmit.iris.Iris"
val apiVersion: String = "1.20.2"

dependencies {
    implementation("org.projectlombok:lombok:1.18.24")

    paperweight.paperDevBundle("1.20.2-R0.1-SNAPSHOT")

    compileOnly("com.ticxo.playeranimator:PlayerAnimator:R1.2.7")
    compileOnly("com.github.oraxen:oraxen:1.158.0")
    compileOnly("com.github.LoneDev6:api-itemsadder:3.4.1-r4")
    compileOnly("me.clip:placeholderapi:2.11.3")

    compileOnly("com.dfsek:Paralithic:0.4.0")
    compileOnly("io.papermc:paperlib:1.0.5")
    compileOnly("net.kyori:adventure-text-minimessage:4.13.1")
    compileOnly("net.kyori:adventure-platform-bukkit:4.3.0")
    compileOnly("net.kyori:adventure-api:4.13.1")
    compileOnly("io.lumine:Mythic-Dist:5.2.1")

    implementation("io.timeandspace:smoothie-map:2.0.2")
    implementation("it.unimi.dsi:fastutil:8.5.8")
    implementation("com.googlecode.concurrentlinkedhashmap:concurrentlinkedhashmap-lru:1.4.2")
    implementation("org.zeroturnaround:zt-zip:1.14")
    implementation("com.google.code.gson:gson:2.9.0")
    implementation("org.ow2.asm:asm:9.2")
    implementation("com.google.guava:guava:31.1-jre")
    implementation("bsf:bsf:2.4.0")
    implementation("rhino:js:1.7R2")
    implementation("com.github.ben-manes.caffeine:caffeine:3.0.6")
    implementation("org.apache.commons:commons-lang3:3.12.0")

    compileOnly("org.projectlombok:lombok:1.18.24")
    annotationProcessor("org.projectlombok:lombok:1.18.24")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-parameters")
}

tasks {
    shadowJar {
        dependencies {
            include(dependency("io.papermc:paperlib"))
            include(dependency("com.dfsek:Paralithic"))
            include(dependency("net.kyori:"))
        }
    }
}

configurations.all {
    resolutionStrategy.cacheChangingModulesFor(60, "minutes")
    resolutionStrategy.cacheDynamicVersionsFor(60, "minutes")
}
