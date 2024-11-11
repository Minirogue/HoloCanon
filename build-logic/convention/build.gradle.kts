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
    compileOnly(libs.detekt.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.room.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("multiplatformLibrary") {
            id = "holocanon.multiplatform.library"
            implementationClass = "plugin.KotlinMultiplatformLibraryConvention"
        }
        register("testApp") {
            id = "holocanon.test.app"
            implementationClass = "plugin.TestAppPlugin"
        }
        register("androidApp") {
            id = "holocanon.android.app"
            implementationClass = "plugin.AndroidAppConventionPlugin"
        }
        register("jvmApp") {
            id = "holocanon.jvm.app"
            implementationClass = "plugin.JvmAppConventionPlugin"
        }
    }
}
