package plugin

import convention.configureJvm
import convention.configureKotlin
import org.gradle.api.Plugin
import org.gradle.api.Project

class JvmAppConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply("application")
            apply("org.jetbrains.kotlin.jvm")
        }
        configureJvm()
        configureKotlin()
    }
}
