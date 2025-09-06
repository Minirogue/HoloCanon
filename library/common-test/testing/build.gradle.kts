plugins {
    id("minirogue.multiplatform.library")
}

minirogue {
    platforms {
        android()
        jvm()
        ios()
    }
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(libs.kotlin.test.common)
            api(libs.kotlin.test.annotations.common)
        }
        jvmMain.dependencies {
            api(libs.kotlin.test.junit)
        }
        androidMain.dependencies {
            api(libs.kotlin.test.junit)
        }
        // TODO ios?
    }
}