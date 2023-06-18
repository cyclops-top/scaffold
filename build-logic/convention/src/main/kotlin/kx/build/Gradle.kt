@file:Suppress("unused")

package kx.build

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions

fun CommonExtension<*, *, *, *>.kotlinOptions(block: KotlinJvmOptions.() -> Unit) {
    (this as ExtensionAware).extensions.configure("kotlinOptions", block)
}

fun DependencyHandlerScope.implementation(dependency: Any) {
    add("implementation", dependency)
}

fun DependencyHandlerScope.kapt(dependency: Any) {
    add("kapt", dependency)
}

fun DependencyHandlerScope.ksp(dependency: Any) {
    add("ksp", dependency)
}

fun DependencyHandlerScope.testImplementation(dependency: Any) {
    add("testImplementation", dependency)
}

fun DependencyHandlerScope.androidTestImplementation(dependency: Any) {
    add("androidTestImplementation", dependency)
}

fun DependencyHandlerScope.debugImplementation(dependency: Any) {
    add("debugImplementation", dependency)
}

fun DependencyHandlerScope.kaptAndroidTest(dependencyNotation: Any) {
    add("kaptAndroidTest", dependencyNotation)
}