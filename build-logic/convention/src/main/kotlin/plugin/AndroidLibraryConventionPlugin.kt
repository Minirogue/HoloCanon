package plugin

import convention.configureAndroidLibrary
import convention.configureCompose
import convention.configureGradleChecker
import convention.configureHilt
import convention.configureKotlin
import convention.configureRoom
import convention.configureSerialization
import convention.configureViewBinding
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
            configureGradleChecker()

            extensions.create(
                "holocanon",
                HolocanonAndroidLibraryExtension::class.java,
                target
            )
        }
    }
}

open class HolocanonAndroidLibraryExtension(private val project: Project) {
    fun composeUi() = project.configureCompose()
    fun hilt() = project.configureHilt()
    fun room() = project.configureRoom()
    fun serialization() = project.configureSerialization()
    fun viewBinding() = project.configureViewBinding()
}
