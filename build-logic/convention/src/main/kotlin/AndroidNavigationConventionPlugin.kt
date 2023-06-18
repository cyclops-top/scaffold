import kx.build.bundle
import kx.build.implementation
import kx.build.library
import kx.build.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

@Suppress("unused")
class AndroidNavigationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            val libs by libs()
            with(pluginManager) {
                apply("androidx.navigation.safeargs.kotlin")
            }
            dependencies {
                implementation(libs.bundle("navigation"))
                implementation(libs.library("material"))
            }
        }
    }

}