import java.io.FileInputStream
import java.util.Properties

plugins {
    id("minirogue.android.app")
}

minirogue {
    hilt()
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
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.preference)
    implementation(libs.compose.navigation)
    implementation(libs.coil.ktor)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.room.ktx)
    implementation(projects.app.shared)
    implementation(projects.core)
    implementation(projects.feature.homeScreen.internal)
    implementation(projects.feature.mediaItem.internal)
    implementation(projects.feature.mediaList.internal)
    implementation(projects.feature.series.internal)
    implementation(projects.feature.settings.internal)
    implementation(projects.feature.selectFilters.internal)
    implementation(projects.library.commonResources.public)
    implementation(projects.library.composeExt.public)
    implementation(projects.library.coroutineExt.internal)
    implementation(projects.library.coroutineExt.public)
    implementation(projects.library.filters.internal)
    implementation(projects.library.globalNotification.internal)
    implementation(projects.library.holoclient.internal)
    implementation(projects.library.mediaItem.internal)
    implementation(projects.library.mediaNotes.internal)
    implementation(projects.library.navigation.public)
    implementation(projects.library.serializationExt.internal)
    implementation(projects.library.series.internal)
    implementation(projects.library.settings.internal)
    implementation(projects.library.sorting.internal)
}
