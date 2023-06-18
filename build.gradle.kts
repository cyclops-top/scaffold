// Top-level build file where you can add configuration options common to all sub-projects/modules.
@file:Suppress("DSL_SCOPE_VIOLATION")

buildscript {
    dependencies {
        classpath(libs.kotlin.gradle.pluginz)
        classpath(libs.kotlinx.serialization.pluginz)
        classpath(libs.navigation.args.pluginz)
    }
}

plugins {
    alias(libs.plugins.com.android.application) apply false
    alias(libs.plugins.org.jetbrains.kotlin.android) apply false
    alias(libs.plugins.android.hilt) apply false
    alias(libs.plugins.kotlin.ksp) apply false
    alias(libs.plugins.dokka)
}

tasks.dokkaHtmlMultiModule.configure {
    outputDirectory.set(projectDir.resolve("docs/html"))
}
tasks.dokkaGfmMultiModule.configure {
    outputDirectory.set(projectDir.resolve("docs/md"))
}

tasks.dokkaJavadoc.configure {
    outputDirectory.set(projectDir.resolve("docs/java"))
}

tasks.dokkaJekyllMultiModule.configure {
    outputDirectory.set(projectDir.resolve("docs/jeky"))
}