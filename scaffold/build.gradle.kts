@file:Suppress("DSL_SCOPE_VIOLATION")

import kx.build.implementation
import kx.build.library

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
    implementation(libs.paging)
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