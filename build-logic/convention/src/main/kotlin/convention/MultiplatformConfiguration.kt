package convention

import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

internal fun Project.configureKotlinMultiplatform() {
    extensions.configure(KotlinMultiplatformExtension::class.java){
        androidTarget()
    }
}
