package kx.build


import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.provideDelegate
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

/**
 * Configure base Kotlin with Android options
 */
internal fun Project.configureKotlinAndroid(
    commonExtension: CommonExtension<*, *, *, *>,
) {
    val libs by libs()

    commonExtension.apply {
        compileSdk = libs.version("android-compile-sdk").toInt()
        defaultConfig {
            minSdk = libs.version("android-min-sdk").toInt()
        }
        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
            isCoreLibraryDesugaringEnabled = true
        }
        kotlinOptions {
            // Treat all Kotlin warnings as errors (disabled by default)
            // Override by setting warningsAsErrors=true in your ~/.gradle/gradle.properties
            val warningsAsErrors: String? by project
            allWarningsAsErrors = warningsAsErrors.toBoolean()

            freeCompilerArgs = freeCompilerArgs + listOf(
                "-Xcontext-receivers",
                "-opt-in=kotlin.RequiresOptIn",
                // Enable experimental coroutines APIs, including Flow
                "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
                "-opt-in=kotlinx.coroutines.FlowPreview",
                "-opt-in=kotlin.Experimental",
            )

            // Set JVM target to 11
            jvmTarget = JavaVersion.VERSION_17.toString()

        }
        extensions.configure<KotlinAndroidProjectExtension> {
            jvmToolchain(17)
        }
        dependencies {
            implementation(libs.library("kotlinx.coroutines.jvm"))
            implementation(libs.library("kotlinx.coroutines.android"))
            testImplementation(libs.library("kotlinx.coroutines.test"))
        }
    }


    dependencies {
        add("coreLibraryDesugaring", libs.library("android.desugarJdkLibs"))
    }
}
