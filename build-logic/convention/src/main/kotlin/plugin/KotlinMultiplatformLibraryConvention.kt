package plugin

import convention.configureAndroidLibrary
import convention.configureCompose
import convention.configureHilt
import convention.configureKotlin
import convention.configureKotlinMultiplatformAndroid
import convention.configureKotlinMultiplatformJvm
import convention.configureRoom
import convention.configureSerialization
import convention.configureViewBinding
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project

class KotlinMultiplatformLibraryConvention : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("org.jetbrains.kotlin.multiplatform")
            }
            configureKotlin()

            extensions.create(
                "holocanon",
                HolocanonMultiplatformLibraryExtension::class.java,
                target
            )
        }
    }
}

open class HolocanonMultiplatformLibraryExtension(private val project: Project) {
    fun serialization() = project.configureSerialization()

    fun jvm() = jvm(Action {})
    fun jvm(jvmActions: Action<JvmConfig>) {
        project.configureKotlinMultiplatformJvm()
        jvmActions.execute(JvmConfig((project)))
    }

    fun android() = android(Action {})
    fun android(androidActions: Action<AndroidConfig>) {
        project.configureKotlinMultiplatformAndroid()
        androidActions.execute(AndroidConfig(project))
    }
}

class AndroidConfig(val project: Project) {
    fun room() = project.configureRoom()
    fun hilt() = project.configureHilt()
    fun viewBinding() = project.configureViewBinding()
    fun composeUi() = project.configureCompose()
}

class JvmConfig(val project: Project) {

}
