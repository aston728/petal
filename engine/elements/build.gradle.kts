plugins {
    this.kotlin("jvm")
    this.`java-library`
}

dependencies {
    this.api(this.project(":engine:core"))
}
