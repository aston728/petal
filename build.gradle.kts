plugins {
    kotlin("jvm") version "2.2.0" apply false
}

allprojects {
    repositories {
        mavenCentral()
    }
}
subprojects {
    plugins.withType<org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper> {
        extensions.configure<org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension> {
            jvmToolchain(21)
        }
    }
}
