package compose.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import settings.model.DarkModeSetting
import settings.model.Theme

@Composable
fun HolocanonTheme(
    darkModeSetting: DarkModeSetting,
    theme: Theme,
    content: @Composable () -> Unit,
) {
    val darkTheme = when (darkModeSetting) {
        DarkModeSetting.SYSTEM -> isSystemInDarkTheme()
        DarkModeSetting.LIGHT -> false
        DarkModeSetting.DARK -> true
    }
    val colors = when (theme) {
        Theme.Force -> if (darkTheme) ForceDarkColors else ForceLightColors
        Theme.Mace -> if (darkTheme) MaceDarkColors else MaceLightColors
        Theme.AndroidDynamic -> { getDynamicColors(darkTheme) }
    }
    MaterialTheme(
        colorScheme = colors,
        content = content,
    )
}
