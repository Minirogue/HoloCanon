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
            }
            configureJvm()
            configureKotlin()
        }
    }
}
