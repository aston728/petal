plugins {
    this.kotlin("jvm")
    this.`java-library`
}

val lwjglVersion: String = parent!!.extra["lwjglVersion"] as String
val lwjglNatives: String = parent!!.extra["lwjglNatives"] as String

dependencies {
    this.implementation(this.platform("org.lwjgl:lwjgl-bom:$lwjglVersion"))
    this.implementation("org.lwjgl:lwjgl-glfw")
    this.implementation("org.lwjgl:lwjgl-openal")
    this.implementation("org.lwjgl:lwjgl-stb")
    this.implementation("org.lwjgl:lwjgl-freetype")

    this.runtimeOnly("org.lwjgl:lwjgl::$lwjglNatives")
    this.runtimeOnly("org.lwjgl:lwjgl-glfw::$lwjglNatives")
    this.runtimeOnly("org.lwjgl:lwjgl-openal::$lwjglNatives")
    this.runtimeOnly("org.lwjgl:lwjgl-stb::$lwjglNatives")
    this.runtimeOnly("org.lwjgl:lwjgl-freetype::$lwjglNatives")
}
