import kx.build.implementation
import kx.build.library
import kx.build.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies


@Suppress("unused")
class AndroidDataConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            val libs by libs()
            pluginManager.apply {
                apply("kx.android.library")
                apply("kx.hilt")
                apply("com.google.devtools.ksp")
            }

            dependencies {
                implementation(libs.library("paging"))
            }
        }
    }
}
