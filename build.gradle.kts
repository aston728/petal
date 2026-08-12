plugins {
    this.kotlin("jvm") version "2.2.0" apply false
}

allprojects {
    this.repositories {
        this.mavenCentral()
    }
}
subprojects {
    this.plugins.withType<org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper> {
        this@subprojects.extensions.configure<org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension> {
            this.jvmToolchain(21)
        }
    }
}
