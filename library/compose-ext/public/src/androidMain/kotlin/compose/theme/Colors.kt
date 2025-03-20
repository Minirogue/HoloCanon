package compose.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

private val PURE_DARK_SIDE = Color(0, 0, 0)
private val DARK_SIDE_GRAY = Color(50, 55, 58)
private val DURASTEEL_GRAY = Color(100, 105, 108)
private val DARK_SIDE_RED = Color(103, 0, 0)
private val IMPERIAL_RED = Color(188, 30, 34)
private val PURE_RED = Color(255, 0, 0)
private val CORUSCANT_BLUE = Color(57, 74, 89)
private val VALOR_GREEN = Color(85, 101, 103)

internal val DarkColors = darkColorScheme(
    primary = PURE_RED,
    onPrimary = DARK_SIDE_RED,
    secondary = IMPERIAL_RED,
    outline = PURE_RED,
    background = PURE_DARK_SIDE,
    surfaceVariant = DARK_SIDE_GRAY,
)
internal val LightColors = lightColorScheme()
