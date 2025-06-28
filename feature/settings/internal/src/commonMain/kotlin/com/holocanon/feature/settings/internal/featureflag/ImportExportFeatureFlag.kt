package com.holocanon.feature.settings.internal.featureflag

import com.holocanon.library.platform.Platform
import dev.zacsweers.metro.Inject

@Inject
class ImportExportFeatureFlag(val currentPlatform: Platform) {
    fun isAvailable() = when (currentPlatform) {
        Platform.Android -> true
        Platform.IOS -> false
    }
}
