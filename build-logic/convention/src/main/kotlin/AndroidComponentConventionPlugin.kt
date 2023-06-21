import kx.build.implementation
import kx.build.library
import kx.build.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies


@Suppress("unused")
class AndroidComponentConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            val libs by libs()
            pluginManager.apply {
                apply("sc.android.library")
                apply("sc.navigation")
            }

            dependencies {
                implementation(libs.library("material"))
                implementation(libs.library("paging"))
            }
        }
    }
}
