package settings.usecase

import settings.model.DarkModeSetting

interface UpdateTheme {
   suspend operator fun invoke(newTheme: DarkModeSetting)
}