package plugin

import convention.configureAndroid
import convention.configureKotlin
import org.gradle.api.Plugin
import org.gradle.api.Project

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
                apply("kotlin-android")
                apply("io.gitlab.arturbosch.detekt")
            }
            configureAndroid()
            configureKotlin()
        }
    }
}
