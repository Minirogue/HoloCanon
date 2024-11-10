package convention

import com.android.build.gradle.internal.utils.configureKotlinCompileTasks
import org.gradle.api.Project
import org.gradle.kotlin.dsl.support.KotlinCompilerOptions
import org.gradle.kotlin.dsl.support.kotlinCompilerOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

fun Project.applyUniversalConfigurations(useGradleChecker: Boolean = true) {
    if (useGradleChecker) configureGradleChecker()
    configureDetekt()
    tasks.withType(KotlinCompile::class.java).all {
        compilerOptions.freeCompilerArgs.add("-Xcontext-receivers")
    }
}