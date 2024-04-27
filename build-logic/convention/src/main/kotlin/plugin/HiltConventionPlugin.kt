package plugin

import convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project

class HiltConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("kotlin-kapt")
                apply("dagger.hilt.android.plugin")
            }
            with(dependencies){
                addProvider("implementation", libs.findLibrary("hilt.android").get())
                addProvider("kapt", libs.findLibrary("hilt.compiler").get())
            }
        }
    }
}