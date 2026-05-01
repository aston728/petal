plugins {
    kotlin("jvm")
    application
}

dependencies {
    implementation(project(":engine"))
}

application {
    mainClass.set("com.aston728.app.MainKt")
}
