package plugin

import convention.configureAndroidLibrary
import convention.configureKotlin
import convention.configureKotlinMultiplatform
import org.gradle.api.Plugin
import org.gradle.api.Project

class KotlinMultiplatformLibraryConvention : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("org.jetbrains.kotlin.multiplatform")
                apply("com.android.library")
            }
            configureKotlin()
            configureKotlinMultiplatform()
            configureAndroidLibrary()
        }
    }
}
