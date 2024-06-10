package convention

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.get

fun Project.configureRoom() {
    pluginManager.applyKsp()

    extensions.configure(CommonExtension::class.java) {
        defaultConfig {
            javaCompileOptions {
                annotationProcessorOptions {
                    arguments += "room.schemaLocation" to "$projectDir/schemas"
                }
            }
            sourceSets.get("androidTest").assets.srcDirs(files("$projectDir/schemas"))
        }
    }
    with(dependencies) {
        add("implementation", libs.findLibrary("room.ktx").get())
        add("implementation", libs.findLibrary("room.runtime").get())
        add("ksp", libs.findLibrary("room.compiler").get())
    }
}