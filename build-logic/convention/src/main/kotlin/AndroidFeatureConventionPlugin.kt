import kx.build.bundle
import kx.build.implementation
import kx.build.library
import kx.build.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies


@Suppress("unused")
class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            val libs by libs()
            pluginManager.apply {
                apply("sc.android.library")
                apply("sc.hilt")
                apply("sc.navigation")
            }

            dependencies {
                implementation(libs.bundle("androidx"))
                implementation(libs.library("material"))
            }
        }
    }
}
