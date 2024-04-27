package convention

import com.android.build.api.dsl.LibraryExtension
import com.android.build.gradle.AppExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project

private const val minSdkVersion = 21
private const val compileSdkVersion = 34
private const val targetSdkVersion = 34

internal fun Project.configureAndroid() {
    extensions.configure(LibraryExtension::class.java) {
        compileSdk = compileSdkVersion
        defaultConfig {
            minSdk = minSdkVersion
        }
        compileOptions {
            sourceCompatibility = JavaVersion.toVersion(javaLibVersion)
            targetCompatibility = JavaVersion.toVersion(javaLibVersion)
        }
        lint {
            baseline = file("lint-baseline.xml")
        }
    }
}

internal fun Project.androidAppConfiguration(appExtension: AppExtension) = with(appExtension) {

}
