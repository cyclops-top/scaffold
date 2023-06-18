/*
 * Copyright 2022 The Android Open Source Project
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

import kx.build.implementation
import kx.build.kapt
import kx.build.kaptAndroidTest
import kx.build.library
import kx.build.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.plugin.KaptExtension

@Suppress("unused")
class AndroidHiltConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            val libs by libs()
            with(pluginManager) {
                apply("dagger.hilt.android.plugin")
                apply("org.jetbrains.kotlin.kapt")
            }

            dependencies {
                implementation(libs.library("hilt.android"))
                kapt(libs.library("hilt.compiler"))
                kaptAndroidTest(libs.library("hilt.compiler"))
            }
            extensions.configure<KotlinAndroidProjectExtension> {
                jvmToolchain(17)
            }
            extensions.configure<KaptExtension> {
                correctErrorTypes = true
            }
        }
    }

}