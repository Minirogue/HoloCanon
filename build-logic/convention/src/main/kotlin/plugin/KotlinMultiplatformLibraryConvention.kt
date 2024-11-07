package plugin

import convention.applyUniversalConfigurations
import convention.configureAndroidLibrary
import convention.configureCompose
import convention.configureGradleChecker
import convention.configureHilt
import convention.configureKotlinMultiplatformAndroid
import convention.configureKotlinMultiplatformJvm
import convention.configureRoom
import convention.configureSerialization
import convention.configureViewBinding
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension

class KotlinMultiplatformLibraryConvention : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("org.jetbrains.kotlin.multiplatform")
            }
            applyUniversalConfigurations()

            extensions.create(
                "holocanon",
                HolocanonMultiplatformLibraryExtension::class.java,
                target
            )
        }
    }
}

open class HolocanonMultiplatformLibraryExtension(private val project: Project) {
    fun android() = android(Action {})
    fun android(androidActions: Action<AndroidConfig>) {
        project.configureKotlinMultiplatformAndroid()
        androidActions.execute(AndroidConfig(project))
    }


    fun jvm() = jvm(Action {})
    fun jvm(jvmActions: Action<JvmConfig>) {
        project.configureKotlinMultiplatformJvm()
        jvmActions.execute(JvmConfig((project)))
    }

    fun serialization() = project.configureSerialization()
    fun explicitBackingFields() = project.extensions.configure(KotlinProjectExtension::class.java) {
        sourceSets.all {
            languageSettings.enableLanguageFeature("ExplicitBackingFields")
        }
    }

}

class AndroidConfig(val project: Project) {
    fun composeUi() = project.configureCompose()
    fun hilt() = project.configureHilt()
    fun room() = project.configureRoom()
    fun viewBinding() = project.configureViewBinding()
}

class JvmConfig(val project: Project) {

}
