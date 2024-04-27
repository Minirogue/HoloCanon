package convention

import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

public fun Project.configureJvm() {
    extensions.configure(JavaPluginExtension::class.java) {
        sourceCompatibility = JavaVersion.toVersion(javaLibVersion)
        targetCompatibility = JavaVersion.toVersion(javaLibVersion)
    }
}

internal fun Project.configureKotlin() {
    plugins.apply("io.gitlab.arturbosch.detekt")
    tasks.withType(KotlinCompile::class.java).configureEach {
        kotlinOptions {
            jvmTarget = javaLibVersion
        }
    }
}
