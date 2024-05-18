package convention

import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

internal fun Project.configureKotlinMultiplatform(extension: KotlinMultiplatformExtension) {
    extension.apply {
        jvm()
    }
}
