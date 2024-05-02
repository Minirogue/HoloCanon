package plugin

import convention.configureAndroidApp
import convention.configureKotlin
import org.gradle.api.Plugin
import org.gradle.api.Project

class AndroidAppConventionPlugin: Plugin<Project> {
        override fun apply(target: Project) {
            with(target) {
                with(pluginManager) {
                    apply("com.android.application")
                    apply("kotlin-android")
                }
                configureAndroidApp()
                configureKotlin()
            }
        }
}
