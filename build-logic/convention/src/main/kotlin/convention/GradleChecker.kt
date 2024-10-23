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
    fun checkGradle() {
        val allLines = gradleFile.readLines()
        checkRootLines(allLines.filter { it.firstOrNull()?.isWhitespace() == false })
        checkPluginsLines(allLines.take(3))
    }

    private fun checkPluginsLines(pluginsLines: List<String>) {
        assert(pluginsLines[0] == "plugins {") { "first line of build.gradle must be \"plugins {\": ${pluginsLines[0]}" }
        assert(pluginsLines[1].contains("holocanon.")) { "Only holocanon plugins are allowed in the plugin block: ${pluginsLines[1].trim()}" }
        assert(pluginsLines[2] == "}") { "plugins block should only contain one (holocanon plugin) and end with \"}\": ${pluginsLines[2]}" }
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
                AssertionError(
                    "All build.gradle lines must start with whitespace or one of $acceptableRoots\n" +
                            "\"$root\" does not fit this paradigm"
                )
            )
        }
    }

    companion object {
        private val acceptableRoots = listOf(
            "plugins",
            "holocanon",
            "kotlin.sourceSets.androidMain.dependencies",
            "kotlin.sourceSets.androidUnitTest.dependencies",
            "kotlin.sourceSets.androidInstrumentedTest.dependencies",
            "kotlin.sourceSets.commonMain.dependencies",
            "dependencies",
            "}"
        )
    }
}
