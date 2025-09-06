plugins {
    id("minirogue.multiplatform.library")
}

minirogue {
    platforms {
        jvm()
        ios()
    }
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.library.mediaNotes.public)

            implementation(projects.library.commonTest.testing)
        }
    }
}