package convention

import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Project

fun Project.configureDetekt() {
    plugins.apply("io.gitlab.arturbosch.detekt")
    extensions.configure(DetektExtension::class.java) {
        source.setFrom("src/main", "src/androidMain", "src/commonMain", "src/jvmMain")
        config.setFrom(rootProject.file("config/detekt-config.yml"))
        buildUponDefaultConfig = true
    }
}