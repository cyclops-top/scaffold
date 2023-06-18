import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies


@Suppress("unused")
class AndroidDataConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {

            pluginManager.apply {
                apply("kx.android.library")
                apply("kx.hilt")
                apply("com.google.devtools.ksp")
            }

            dependencies {

            }
        }
    }
}
