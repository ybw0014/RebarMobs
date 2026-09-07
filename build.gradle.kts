import net.minecrell.pluginyml.bukkit.BukkitPluginDescription
import net.minecrell.pluginyml.paper.PaperPluginDescription
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

val pluginVersion = providers.gradleProperty("plugin.version").get()
val minecraftVersion = providers.gradleProperty("minecraft.version").get()
val rebarVersion = providers.gradleProperty("rebar.version").get()
val rebarSnapshot = providers.gradleProperty("rebar.snapshot").get().toBoolean()
val rebarSnapshotVersion = providers.gradleProperty("rebar.snapshot.version").getOrElse("1.0.0-SNAPSHOT")
val effectiveRebarVersion = if (rebarSnapshot) rebarSnapshotVersion else rebarVersion
val pylonVersion = providers.gradleProperty("pylon.version").get()

plugins {
    kotlin("jvm") version "2.4.20"
    id("com.gradleup.shadow") version "9.6.1"
    id("de.eldoria.plugin-yml.paper") version "0.9.0"
    id("xyz.jpenilla.run-paper") version "3.0.2"
    id("com.diffplug.spotless") version "8.10.0"
}

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://central.sonatype.com/repository/maven-snapshots/")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.alessiodp.com/releases/")
    maven("https://repo.xenondevs.xyz/releases")
    maven("https://jitpack.io")
}

dependencies {
    fun compileOnlyAndTestImpl(dependencyNotation: Any) {
        compileOnly(dependencyNotation)
        testImplementation(dependencyNotation)
    }

    compileOnly(kotlin("stdlib")) // loaded through library loader
    compileOnly(kotlin("reflect")) // loaded through library loader
    compileOnlyAndTestImpl("io.papermc.paper:paper-api:$minecraftVersion.build.+")
    compileOnlyAndTestImpl("io.github.pylonmc:rebar:$effectiveRebarVersion")
//    compileOnlyAndTestImpl("io.github.pylonmc:pylon:$pylonVersion")
    implementation("org.bstats:bstats-bukkit:3.2.1")
    implementation("net.guizhanss:guizhanlib-all:3.0.0-SNAPSHOT")
    implementation("net.guizhanss:guizhanlib-kt-all:0.3.0-SNAPSHOT")

    testImplementation(kotlin("test"))
//    testImplementation("com.github.MockBukkit:MockBukkit:v1.21-SNAPSHOT")
}

group = "net.guizhanss"
description = "RebarMobs"
version = pluginVersion

val mainPackage = "net.guizhanss.rebarmobs"

java {
    disableAutoTargetJvm()
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}

kotlin {
    compilerOptions {
        javaParameters = true
        jvmTarget = JvmTarget.JVM_25
        freeCompilerArgs.add("-Xexplicit-backing-fields")
    }
}

configure<com.diffplug.gradle.spotless.SpotlessExtension> {
    java {
        removeUnusedImports()
        expandWildcardImports()
        googleJavaFormat().aosp()
        formatAnnotations()
    }
    kotlin {
        ktlint().editorConfigOverride(
            mapOf(
                "ktlint_code_style" to "intellij_idea",
                "ktlint_standard_no-unused-imports" to "enabled"
            )
        )
    }
}

tasks.shadowJar {
    fun doRelocate(from: String, to: String? = null) {
        val last = to ?: from.split(".").last()
        relocate(from, "$mainPackage.libs.$last")
    }

    doRelocate("net.byteflux.libby")
    doRelocate("net.guizhanss.guizhanlib")
    doRelocate("org.bstats")
    minimize()
    archiveClassifier = ""
}

paper {
    main = "$mainPackage.RebarMobs"
    loader = "$mainPackage.RebarMobsLoader"
    bootstrapper = "$mainPackage.RebarMobsBootstrap"
    apiVersion = minecraftVersion
    authors = listOf("ybw0014")
    description = "Get mob heads, collect mob souls, capture mobs, or raise mob pets in your inventory."
    load = BukkitPluginDescription.PluginLoadOrder.STARTUP

    bootstrapDependencies {
        register("Rebar") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
    }

    serverDependencies {
        register("Rebar") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
        register("Pylon") {
            required = false
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
    }
}

tasks.runServer {
    doFirst {
        val langDir = file("run/plugins/Rebar/lang/rebarmobs/")
        if (langDir.exists()) {
            langDir.listFiles { file ->
                file.isFile && file.name.endsWith(".yml")
            }?.forEach { file ->
                file.delete()
            }
        }
    }
    downloadPlugins {
        // Rebar
        github("pylonmc", "rebar", rebarVersion, "rebar-$rebarVersion.jar")
        // Pylon
//        github("pylonmc", "pylon", pylonVersion, "pylon-$pylonVersion.jar")
    }
    jvmArgs("-Dcom.mojang.eula.agree=true")
    minecraftVersion(minecraftVersion)
}

tasks.test {
    useJUnitPlatform()
}
