package convention

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.DefaultTask
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.io.FileInputStream
import java.util.Properties

private const val MIN_SDK = 21
private const val COMPILE_SDK = 34
private const val TARGET_SDK = 34

internal fun Project.configureAndroidLibrary() {
    extensions.configure(LibraryExtension::class.java) {
        configureAndroidCommon(this)
    }
}

internal fun Project.configureAndroidApp() {
    registerBumpVersionCode()
    extensions.configure(ApplicationExtension::class.java) {
        configureAndroidCommon(this)
        defaultConfig {
            targetSdk = TARGET_SDK
            versionCode = getVersionCodeFromProperties(rootProject.file("version.properties"))
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
        }
        compileOptions {
            sourceCompatibility = JavaVersion.toVersion(javaLibVersion)
            targetCompatibility = JavaVersion.toVersion(javaLibVersion)
        }
        lint {
            baseline = file("lint-baseline.xml")
        }
    }

private fun Project.registerBumpVersionCode() {
    tasks.register("bumpVersionCode", BumpVersionCodeTask::class.java) {
        description = "bump the app's version code"
    }
}

abstract class BumpVersionCodeTask : DefaultTask() {
    @InputFile
    val versionPropertiesInputFile: File = project.rootProject.file("version.properties")

    @OutputFile
    val versionPropertiesOutputFile: File = project.rootProject.file("version.properties")

    @TaskAction
    fun run() {
        val versionProperties = Properties()
        versionProperties.load(FileInputStream(versionPropertiesInputFile))

        val code = versionProperties.getProperty("VERSION_CODE").toInt() + 1

        versionProperties["VERSION_CODE"] = code.toString()
        versionProperties.store(versionPropertiesOutputFile.bufferedWriter(), null)
    }
}

