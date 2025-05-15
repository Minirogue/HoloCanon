package compose.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
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
        Theme.Dynamic -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (darkTheme) {
                    dynamicDarkColorScheme(LocalContext.current)
                } else {
                    dynamicLightColorScheme(LocalContext.current)
                }
            } else if (darkTheme) ForceDarkColors else ForceLightColors
        }
    }
    MaterialTheme(
        colorScheme = colors,
        content = content,
    )
}
