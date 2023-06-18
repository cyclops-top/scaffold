@file:Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("sc.android")
//    alias(libs.plugins.dokka)
}

android {
    namespace = "scaffold.simple"

}

dependencies {
    implementation(project(":scaffold"))
}
