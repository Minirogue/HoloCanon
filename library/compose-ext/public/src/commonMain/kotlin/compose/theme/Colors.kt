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

private val LIGHT_PURPLE = Color(200, 20, 240)
private val DARK_PURPLE = Color(100, 0, 100)
private val ELECTRUM = Color(180, 180, 180)

private val FORCE_BLUE = Color(0, 0, 200)
private val FORCE_GREEN = Color(0, 200, 0)

internal val ForceDarkColors = darkColorScheme(
    primary = PURE_RED,
    onPrimary = DARK_SIDE_RED,
    secondary = IMPERIAL_RED,
)
internal val ForceLightColors = lightColorScheme(
    primary = FORCE_BLUE,
    secondary = FORCE_GREEN,
)

internal val MaceDarkColors = darkColorScheme(
    primary = LIGHT_PURPLE,
    secondary = ELECTRUM,
)
internal val MaceLightColors = lightColorScheme(
    primary = DARK_PURPLE,
    secondary = DURASTEEL_GRAY,
)
