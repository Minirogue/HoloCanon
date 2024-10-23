package plugin

import convention.applyUniversalConfigurations
import convention.configureAndroidApp
import convention.configureGradleChecker
import convention.configureHilt
import org.gradle.api.Plugin
import org.gradle.api.Project

class AndroidAppConventionPlugin: Plugin<Project> {
        override fun apply(target: Project) {
            with(target) {
                with(pluginManager) {
                    apply("com.android.application")
                    apply("kotlin-android")
                }
                applyUniversalConfigurations(useGradleChecker = false)
                configureAndroidApp()

                extensions.create(
                    "holocanon",
                    HolocanonAndroidAppExtension::class.java,
                    target
                )
            }
        }
}

open class HolocanonAndroidAppExtension(private val project: Project) {
    fun hilt() = project.configureHilt()
}