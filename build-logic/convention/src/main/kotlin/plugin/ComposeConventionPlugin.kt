package plugin

import com.android.build.api.dsl.CommonExtension
import convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project

class ComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply(libs.findPlugin("compose.compiler").get().get().pluginId)
            extensions.configure(CommonExtension::class.java) {
                buildFeatures {
                    compose = true
                }
                with(dependencies) {
                    add("implementation", platform(libs.findLibrary("compose.bom").get()))
                    add("implementation", libs.findLibrary("compose.activity").get())
                    add("implementation", libs.findLibrary("compose.material").get())
                    add("implementation", libs.findLibrary("compose.uiToolingPreview").get())
                    add("debugImplementation", libs.findLibrary("compose.uiTooling").get())
                }
            }
        }
    }
}
