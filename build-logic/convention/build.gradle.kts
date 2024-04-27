plugins {
    `kotlin-dsl`
}

group = "com.holocanon.buildlogic"

java {
    sourceCompatibility = JavaVersion.toVersion(libs.versions.java.get())
    targetCompatibility = JavaVersion.toVersion(libs.versions.java.get())
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("androidLibrary") {
            id = "holocanon.android.library"
            implementationClass = "plugin.AndroidLibraryConventionPlugin"
        }
        register("kotlinLibrary") {
            id = "holocanon.kotlin.library"
            implementationClass = "plugin.KotlinJvmLibraryConventionPlugin"
        }
        register("hilt") {
            id = "holocanon.hilt"
            implementationClass = "plugin.HiltConventionPlugin"
        }
        register("androidApp") {
            id = "holocanon.android.app"
            implementationClass = "plugin.AndroidAppConventionPlugin"
        }
    }
}
