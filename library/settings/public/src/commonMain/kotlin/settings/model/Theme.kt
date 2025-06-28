package settings.model

enum class Theme {
    Force, Mace, AndroidDynamic
}

expect fun Theme.isAvailableOnPlatform(): Boolean
