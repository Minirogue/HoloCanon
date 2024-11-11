package plugin

import com.android.build.api.dsl.ApplicationExtension
import convention.applyUniversalConfigurations
import convention.configureAndroidApp
import convention.configureHilt
import convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.plugin.sources.android.findAndroidSourceSet

class TestAppPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.application")
                apply("kotlin-android")
            }
            applyUniversalConfigurations(useGradleChecker = false)
            configureAndroidApp()
            configureHilt()

            extensions.configure(ApplicationExtension::class.java) {
                defaultConfig {
                    applicationId = "com.holocanon.test.app"
                }
                namespace = "com.holocanon.test.app"
            }

          //  target.dependencies.add("implementation", ":feature:test-app:internal")

            extensions.create(
                "holocanon",
                HolocanonTestAppExtension::class.java,
                target
            )
        }
    }
}

open class HolocanonTestAppExtension(private val project: Project) {
}
