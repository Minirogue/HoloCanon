package compose.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable

@Composable
actual fun getDynamicColors(isDarkMode: Boolean): ColorScheme {
    // No dynamic colors on iOS
    return if (isDarkMode) ForceDarkColors else ForceLightColors
}
