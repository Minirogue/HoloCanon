package settings.model

actual fun Theme.isAvailableOnPlatform(): Boolean = when(this){
    Theme.Force,
    Theme.Mace -> true
    Theme.AndroidDynamic -> false
}