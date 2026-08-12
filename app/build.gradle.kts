plugins {
    this.kotlin("jvm")
    this.application
}

dependencies {
    this.implementation(this.project(":engine"))
}

application {
    this.mainClass.set("com.aston728.app.MainKt")
}
