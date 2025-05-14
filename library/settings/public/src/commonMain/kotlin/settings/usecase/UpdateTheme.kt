package settings.usecase

import settings.model.Theme

interface UpdateTheme {
    suspend operator fun invoke(newTheme: Theme)
}
