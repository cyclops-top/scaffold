import com.android.build.api.dsl.ApplicationExtension
import kx.build.androidTestImplementation
import kx.build.bundle
import kx.build.configureKotlinAndroid
import kx.build.implementation
import kx.build.library
import kx.build.libs
import kx.build.testImplementation
import kx.build.version
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension


@Suppress("unused")
class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            val libs by libs()

            pluginManager.apply {
                apply("com.android.application")
                apply("org.jetbrains.kotlin.android")
                apply("sc.hilt")
                apply("sc.navigation")
                apply("kotlin-parcelize")
            }
            extensions.configure<ApplicationExtension> {
                defaultConfig.targetSdk = libs.version("android-target-sdk").toInt()
                configureKotlinAndroid(this)
            }
            extensions.configure<KotlinAndroidProjectExtension> {
                jvmToolchain(17)
            }
            dependencies {
                implementation(libs.bundle("androidx"))
                implementation(libs.library("paging"))
                androidTestImplementation(libs.bundle("androidx.test"))
                testImplementation(libs.library("junit"))
            }
        }
    }
}
