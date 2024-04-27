package convention

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import java.io.File

private const val MIN_SDK = 21
private const val COMPILE_SDK = 34
private const val TARGET_SDK = 34

internal fun Project.configureAndroidLibrary() {
    extensions.configure(LibraryExtension::class.java) {
        configureAndroidCommon(this)
    }
}

internal fun Project.configureAndroidApp() {
    extensions.configure(ApplicationExtension::class.java) {
        configureAndroidCommon(this)
        defaultConfig {
            targetSdk = TARGET_SDK
        }
        buildTypes {
            release {
                isDebuggable = false
                isMinifyEnabled = true
                proguardFiles.add(getDefaultProguardFile("proguard-android-optimize.txt"))
                proguardFiles.add(File("proguard-rules.pro"))
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
