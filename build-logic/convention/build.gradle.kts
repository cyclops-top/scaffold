/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

plugins {
    `kotlin-dsl`
}

group = "sc.build"
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
dependencies {
    compileOnly(libs.android.gradle.pluginz)
    compileOnly(libs.kotlin.gradle.pluginz)
}


gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "sc.android"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = "sc.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("AndroidData") {
            id = "sc.data"
            implementationClass = "AndroidDataConventionPlugin"
        }
        register("AndroidComponent") {
            id = "sc.component"
            implementationClass = "AndroidComponentConventionPlugin"
        }
        register("AndroidFeature") {
            id = "sc.feature"
            implementationClass = "AndroidFeatureConventionPlugin"
        }

        register("androidHilt") {
            id = "sc.hilt"
            implementationClass = "AndroidHiltConventionPlugin"
        }
        register("androidNavigation") {
            id = "sc.navigation"
            implementationClass = "AndroidNavigationConventionPlugin"
        }
        register("kotlinSerialization") {
            id = "sc.kotlin.serialization"
            implementationClass = "KotlinSerializationConventionPlugin"
        }
    }
}