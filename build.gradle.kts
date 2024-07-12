import net.minecrell.pluginyml.paper.PaperPluginDescription

plugins {
    alias(idofrontLibs.plugins.mia.kotlin.jvm)
    alias(idofrontLibs.plugins.kotlinx.serialization)
    alias(idofrontLibs.plugins.mia.papermc)
    alias(idofrontLibs.plugins.mia.nms)
    alias(idofrontLibs.plugins.mia.copyjar)
    alias(idofrontLibs.plugins.mia.publication)
    alias(idofrontLibs.plugins.mia.autoversion)
    id("net.minecrell.plugin-yml.paper") version "0.6.0"
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.mineinabyss.com/releases")
    maven("https://repo.mineinabyss.com/snapshots")
    maven("https://repo.triumphteam.dev/snapshots")
    mavenLocal()
}

dependencies {
    compileOnly(idofrontLibs.bundles.idofront.core)
    compileOnly(idofrontLibs.idofront.nms)
    compileOnly(idofrontLibs.kotlinx.serialization.json)
    compileOnly(idofrontLibs.kotlinx.serialization.kaml)
    compileOnly(idofrontLibs.kotlinx.coroutines)
    compileOnly(idofrontLibs.minecraft.mccoroutine)

    // Geary platform
    compileOnly(libs.geary.papermc)
    implementation(libs.triumph.gui)
}

tasks {
    shadowJar {
        //relocate("dev.triumphteam.gui", "com.mineinabyss.shaded.gui")
    }
}

paper {
    main = "com.mineinabyss.backpacky.Backpacky"
    name = "Backpacky"
    prefix = "Backpacky"
    val version: String by project
    this.version = version
    authors = listOf("boy0000")
    apiVersion = "1.20"

    serverDependencies {
        register("Geary") {
            required = true
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            joinClasspath = true
        }
    }
}