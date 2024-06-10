package convention

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskExecutionException
import java.io.File

fun Project.configureGradleChecker() {
    tasks.register("checkGradleConfig", GradleChecker::class.java)
}

open class GradleChecker : DefaultTask() {
    @InputFile
    val gradleFile: File = project.buildFile

    @TaskAction
    fun checkGradle() { // TODO make compatible with multiplatform modules too
        // filter out lines that start with whitespaces
        val allLines = gradleFile.readLines()
        checkRootLines(allLines.filter { it.firstOrNull()?.isWhitespace() == false })
        checkPluginsLines(allLines.filter { it.contains("id ") })
    }

    private fun checkPluginsLines(pluginsLines: List<String>) {
        pluginsLines.forEach { pluginLine ->
            if (!pluginLine.contains("holocanon.")) throw TaskExecutionException(
                this,
                AssertionError("Only holocanon plugins are allowed in the plugin block: ${pluginLine.trim()}")
            )
        }
    }

    private fun checkRootLines(rootLines: List<String>) {
        rootLines.forEach { root ->
            var isRootAcceptable = false
            acceptableRoots.forEach {
                // root is acceptable if it is in our allowlist
                isRootAcceptable = isRootAcceptable || root.startsWith(it)
            }
            if (!isRootAcceptable) throw TaskExecutionException(
                this,
                AssertionError("All build.gradle root lines must start with one of $acceptableRoots")
            )
        }
    }

    companion object {
        private val acceptableRoots = listOf("plugins", "dependencies", "holocanon", "}")
    }
}
