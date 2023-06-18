@file:Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("sc.android.library")
    id("sc.hilt")
    id("sc.navigation")
    id("sc.kotlin.serialization")
    alias(libs.plugins.dokka)
}

android {
    namespace = "scaffold"
}

dependencies {
    dokkaPlugin(libs.dokka.android.pluginz)
}

tasks.dokkaHtmlPartial{
    dependsOn(tasks.getByName("kaptReleaseKotlin"))
    dependsOn(tasks.getByName("kaptDebugKotlin"))
    mustRunAfter("kaptDebugKotlin")
    mustRunAfter("kaptReleaseKotlin")
}
tasks.dokkaGfmPartial{
    dependsOn(tasks.getByName("kaptReleaseKotlin"))
    dependsOn(tasks.getByName("kaptDebugKotlin"))
    mustRunAfter("kaptDebugKotlin")
    mustRunAfter("kaptReleaseKotlin")
}