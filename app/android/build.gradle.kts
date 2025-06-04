import java.io.FileInputStream
import java.util.Properties

plugins {
    id("minirogue.android.app")
}

minirogue {
    kotlinCompose()
}

val keystorePropsFile = rootProject.file("keystore.properties")

android {
    if (keystorePropsFile.exists()) {
        val keystoreProps = Properties()
        keystoreProps.load(FileInputStream(keystorePropsFile))
        signingConfigs {
            create("release") {
                storeFile = file(keystoreProps["storeFile"]!!)
                storePassword = keystoreProps["storePassword"] as String
                keyAlias = keystoreProps["keyAlias"] as String
                keyPassword = keystoreProps["keyPassword"] as String
            }
        }
    }

    defaultConfig {
        applicationId = "com.minirogue.starwarscanontracker"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    namespace = "com.minirogue.starwarscanontracker"
}

dependencies {
    implementation(libs.android.material)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.appcompat)
    implementation(projects.app.shared)
}
