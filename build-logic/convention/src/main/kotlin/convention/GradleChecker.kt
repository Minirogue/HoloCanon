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
        val rootLines = gradleFile.readLines().filter { it.firstOrNull()?.isWhitespace() == false }
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
