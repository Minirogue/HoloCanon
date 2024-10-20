package convention

import com.android.build.gradle.internal.utils.isKspPluginApplied
import ext.isMultiplatform
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension

fun Project.configureHilt() {
    with(pluginManager) {
        applyKsp()
        apply("dagger.hilt.android.plugin")
    }

    if (isMultiplatform()) {
        kotlinExtension.sourceSets.named("androidMain") {
            dependencies. addProvider("implementation", libs.findLibrary("hilt.android").get())
            dependencies.addProvider("kspAndroid", libs.findLibrary("hilt.compiler").get())
        }
    } else {
        with(dependencies) {
            addProvider("implementation", libs.findLibrary("hilt.android").get())
            addProvider("ksp", libs.findLibrary("hilt.compiler").get())
        }
    }
}