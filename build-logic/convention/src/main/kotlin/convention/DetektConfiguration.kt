package convention

import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Project

fun Project.configureDetekt() {
    plugins.apply("io.gitlab.arturbosch.detekt")
    extensions.configure(DetektExtension::class.java) {
        config.setFrom(rootProject.file("config/detekt-config.yml"))
        buildUponDefaultConfig = true
    }
}