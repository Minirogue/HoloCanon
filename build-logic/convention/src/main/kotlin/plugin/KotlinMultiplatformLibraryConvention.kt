package plugin

import convention.configureKotlin
import convention.configureKotlinMultiplatform
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class KotlinMultiplatformLibraryConvention : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("org.jetbrains.kotlin.multiplatform")
            }
            configureKotlin()
            extensions.configure(
                KotlinMultiplatformExtension::class.java,
                ::configureKotlinMultiplatform
            )
        }
    }
}
