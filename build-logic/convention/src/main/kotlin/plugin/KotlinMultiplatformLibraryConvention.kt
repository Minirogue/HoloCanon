package plugin

import convention.configureAndroidLibrary
import convention.configureHilt
import convention.configureKotlin
import convention.configureKotlinMultiplatform
import convention.configureSerialization
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

            extensions.create(
                "holocanon",
                HolocanonMultiplatformLibraryExtension::class.java,
                target
            )
        }
    }
}

open class HolocanonMultiplatformLibraryExtension(private val project: Project) {
    fun hilt() = project.configureHilt()
    fun serialization() = project.configureSerialization()
}
