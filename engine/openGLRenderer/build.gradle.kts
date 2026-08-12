plugins {
    this.kotlin("jvm")
    this.`java-library`
}

val lwjglVersion: String = parent!!.extra["lwjglVersion"] as String
val lwjglNatives: String = parent!!.extra["lwjglNatives"] as String

dependencies {
    this.api(this.project(":engine:core"))

    this.implementation(this.platform("org.lwjgl:lwjgl-bom:$lwjglVersion"))
    this.implementation("org.lwjgl:lwjgl-glfw")
    this.implementation("org.lwjgl:lwjgl-opengl")

    this.runtimeOnly("org.lwjgl:lwjgl-glfw::$lwjglNatives")
    this.runtimeOnly("org.lwjgl:lwjgl-opengl::$lwjglNatives")
}
