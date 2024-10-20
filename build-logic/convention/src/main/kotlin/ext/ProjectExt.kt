package ext

import org.gradle.api.Project

fun Project.isMultiplatform(): Boolean = plugins.hasPlugin("org.jetbrains.kotlin.multiplatform")