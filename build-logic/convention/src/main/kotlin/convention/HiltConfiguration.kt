package convention

import org.gradle.api.Project

fun Project.configureHilt() {
    with(pluginManager) {
        apply("kotlin-kapt")
        apply("dagger.hilt.android.plugin")
    }
    with(dependencies) {
        addProvider("implementation", libs.findLibrary("hilt.android").get())
        addProvider("kapt", libs.findLibrary("hilt.compiler").get())
    }
}