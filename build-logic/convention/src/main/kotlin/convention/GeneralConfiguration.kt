package convention

import org.gradle.api.Project

fun Project.applyUniversalConfigurations(useGradleChecker: Boolean = true) {
    if (useGradleChecker) configureGradleChecker()
    configureDetekt()
}