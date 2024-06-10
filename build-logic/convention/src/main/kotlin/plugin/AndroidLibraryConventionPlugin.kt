package plugin

import convention.configureAndroidLibrary
import convention.configureCompose
import convention.configureHilt
import convention.configureKotlin
import org.gradle.api.Plugin
import org.gradle.api.Project

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
                apply("kotlin-android")
            }
            configureAndroidLibrary()
            configureKotlin()

            extensions.create(
                "holocanon",
                HolocanonAndroidLibraryExtension::class.java,
                target
            )
        }
    }
}

open class HolocanonAndroidLibraryExtension(private val project: Project) {
    fun hilt() = project.configureHilt()
    fun composeUi() = project.configureCompose()
}
