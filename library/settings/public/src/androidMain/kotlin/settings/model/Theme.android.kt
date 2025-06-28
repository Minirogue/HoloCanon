package settings.model

import android.os.Build

actual fun Theme.isAvailableOnPlatform(): Boolean = when (this) {
    Theme.Force,
    Theme.Mace,
    -> true
    Theme.AndroidDynamic -> Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
}
