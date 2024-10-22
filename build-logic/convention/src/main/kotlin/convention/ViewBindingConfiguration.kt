package convention

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project

fun Project.configureViewBinding() {
    extensions.configure(CommonExtension::class.java) {
        buildFeatures.viewBinding = true
    }
}