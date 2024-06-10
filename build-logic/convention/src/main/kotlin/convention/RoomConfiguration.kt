package convention

import androidx.room.gradle.RoomExtension
import org.gradle.api.Project

fun Project.configureRoom() {
    with(pluginManager) {
        applyKsp()
        apply("androidx.room")
    }

    extensions.configure(RoomExtension::class.java) {
        schemaDirectory("$projectDir/schemas")
    }

    with(dependencies) {
        add("implementation", libs.findLibrary("room.ktx").get())
        add("implementation", libs.findLibrary("room.runtime").get())
        add("ksp", libs.findLibrary("room.compiler").get())
    }
}
