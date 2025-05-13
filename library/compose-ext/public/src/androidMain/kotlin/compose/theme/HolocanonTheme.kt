package compose.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import settings.model.DarkModeSetting

@Composable
fun HolocanonTheme(
    darkModeSetting: DarkModeSetting,
    content: @Composable () -> Unit,
) {
    val darkTheme = when (darkModeSetting) {
        DarkModeSetting.SYSTEM -> isSystemInDarkTheme()
        DarkModeSetting.LIGHT -> false
        DarkModeSetting.DARK -> true
    }
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content,
    )
}
