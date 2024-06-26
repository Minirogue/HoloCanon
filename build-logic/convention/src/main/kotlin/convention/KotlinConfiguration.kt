package convention

import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

public fun Project.configureJvm() {
    extensions.configure(JavaPluginExtension::class.java) {
        sourceCompatibility = JavaVersion.toVersion(javaLibVersion)
        targetCompatibility = JavaVersion.toVersion(javaLibVersion)
    }
}

internal fun Project.configureKotlin() {
    configureDetekt()
    tasks.withType(KotlinJvmCompile::class.java).configureEach {
        compilerOptions {
            jvmTarget.set(JvmTarget.fromTarget(javaLibVersion))
        }
    }
}

private fun Project.configureDetekt() {
    plugins.apply("io.gitlab.arturbosch.detekt")
    extensions.configure(DetektExtension::class.java) {
        config.setFrom(rootProject.file("config/detekt-config.yml"))
        buildUponDefaultConfig = true
    }
}
