package compose.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable

@Composable
expect fun getDynamicColors(isDarkMode: Boolean): ColorScheme
