package convention

import org.gradle.api.Project

fun Project.configureHilt() {
    with(pluginManager) {
        applyKsp()
        apply("dagger.hilt.android.plugin")
    }
    with(dependencies) {
        addProvider("implementation", libs.findLibrary("hilt.android").get())
        addProvider("ksp", libs.findLibrary("hilt.compiler").get())
    }
}