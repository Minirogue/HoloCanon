package convention

import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree

internal fun Project.configureKotlinMultiplatformAndroid() {
    configureAndroidLibrary()
    extensions.configure(KotlinMultiplatformExtension::class.java){
        androidTarget {
            @OptIn(ExperimentalKotlinGradlePluginApi::class)
            instrumentedTestVariant.sourceSetTree.set(KotlinSourceSetTree.test)
        }
    }
}

internal fun Project.configureKotlinMultiplatformJvm() {
    configureJvm()
    extensions.configure(KotlinMultiplatformExtension::class.java){
        jvm()
    }
}

