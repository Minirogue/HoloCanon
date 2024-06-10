package plugin

import convention.applyKsp
import convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project

class HiltConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                applyKsp()
                apply("dagger.hilt.android.plugin")
            }
            with(dependencies){
                addProvider("implementation", libs.findLibrary("hilt.android").get())
                addProvider("ksp", libs.findLibrary("hilt.compiler").get())
            }
        }
    }
}
