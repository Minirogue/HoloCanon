package convention

import ext.isMultiplatform
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension

fun Project.configureSerialization() {
    with(pluginManager) {
        apply("org.jetbrains.kotlin.plugin.serialization")
    }
    if (isMultiplatform()) {
        kotlinExtension.sourceSets.named("commonMain") {
            dependencies {
                implementation(libs.findLibrary("kotlinx.serialization.core").get())
                implementation(libs.findLibrary("kotlinx.serialization.json").get())
            }
        }
    } else {
        with(dependencies) {
            add(
                "implementation",
                libs.findLibrary("kotlinx.serialization.core").get()
            )
            add(
                "implementation",
                libs.findLibrary("kotlinx.serialization.json").get()
            )
        }
    }
}
