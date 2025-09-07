plugins {
    id("minirogue.multiplatform.library")
}

minirogue {
    platforms {
        android()
        ios()
    }
    kotlinCompose()
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.library.commonResources.public)
            implementation(projects.library.composeExt.public)
        }
    }
}