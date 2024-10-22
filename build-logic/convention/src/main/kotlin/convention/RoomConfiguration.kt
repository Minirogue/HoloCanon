package convention

import androidx.room.gradle.RoomExtension
import com.android.build.api.dsl.CommonExtension
import ext.isMultiplatform
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension

fun Project.configureRoom() {
    with(pluginManager) {
        applyKsp()
        apply("androidx.room")
    }

    extensions.configure(RoomExtension::class.java) {
        schemaDirectory("$projectDir/schemas")
    }
    if (isMultiplatform()) {
        kotlinExtension.sourceSets.named("androidMain") {
            with(dependencies) {
                add("implementation", libs.findLibrary("room.ktx").get())
                add("implementation", libs.findLibrary("room.runtime").get())
                add("kspAndroid", libs.findLibrary("room.compiler").get())
            }
        }
    } else {
        with(dependencies) {
            add("implementation", libs.findLibrary("room.ktx").get())
            add("implementation", libs.findLibrary("room.runtime").get())
            add("ksp", libs.findLibrary("room.compiler").get())
        }
    }
}
