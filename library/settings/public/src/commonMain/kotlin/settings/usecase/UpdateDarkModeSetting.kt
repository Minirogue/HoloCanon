package settings.usecase

import settings.model.DarkModeSetting

interface UpdateDarkModeSetting {
    suspend operator fun invoke(newDarkModeSetting: DarkModeSetting)
}
