package convention

import org.gradle.api.plugins.PluginManager

fun PluginManager.applyKsp() {
    apply("com.google.devtools.ksp")
}