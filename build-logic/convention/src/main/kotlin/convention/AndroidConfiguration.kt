package convention

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.DefaultTask
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.io.FileInputStream
import java.lang.ProcessBuilder.Redirect
import java.util.Properties
import java.util.concurrent.TimeUnit

private const val MIN_SDK = 21
private const val COMPILE_SDK = 35
private const val TARGET_SDK = 35

internal fun Project.configureAndroidLibrary() {
    with(pluginManager) { apply("com.android.library") }
    extensions.configure(LibraryExtension::class.java) {
        val modulePath = path.split(":").drop(1).filter { it != "public" }
        namespace = "com.minirogue.holocanon.${modulePath.joinToString(".").replace("-", ".")}"
        resourcePrefix =
            modulePath.first { it != "feature" && it != "library" }.replace("-", "_") + "_"
        configureAndroidCommon(this)
    }
}

internal fun Project.configureAndroidApp() {
    extensions.configure(ApplicationExtension::class.java) {
        configureAndroidCommon(this)
        defaultConfig {
            targetSdk = TARGET_SDK
            versionCode = getVersionCodeFromGitHistory()
            versionName = getDateAsVersionName()
        }
        buildTypes {
            release {
                isDebuggable = false
                isMinifyEnabled = true
                proguardFiles.add(getDefaultProguardFile("proguard-android-optimize.txt"))
                proguardFiles.add(project.file("proguard-rules.pro"))
            }
            debug {
                applicationIdSuffix = ".debug"
            }
        }
    }
}

private fun Project.configureAndroidCommon(commonExtension: CommonExtension<*, *, *, *, *, *>) =
    with(commonExtension) {
        compileSdk = COMPILE_SDK
        defaultConfig {
            minSdk = MIN_SDK
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
        compileOptions {
            sourceCompatibility = JavaVersion.toVersion(javaLibVersion)
            targetCompatibility = JavaVersion.toVersion(javaLibVersion)
        }
        lint {
            baseline = file("lint-baseline.xml")
        }
    }

private fun Project.getVersionCodeFromGitHistory(): Int {
    return providers.exec {
        commandLine("git","rev-list", "--count", "HEAD")
    }.standardOutput.asText.get().trim().toInt().also {println("version code: $it")}
}
