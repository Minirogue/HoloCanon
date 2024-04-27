package plugin

import convention.configureJvm
import convention.configureKotlin
import org.gradle.api.Plugin
import org.gradle.api.Project

class KotlinJvmLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("org.jetbrains.kotlin.jvm")
                apply("java-library")
                apply("io.gitlab.arturbosch.detekt")
            }
            configureJvm()
            configureKotlin()
            // ???  detektPlugins "io.gitlab.arturbosch.detekt:detekt-formatting:$detektVersion"
        }
    }
}