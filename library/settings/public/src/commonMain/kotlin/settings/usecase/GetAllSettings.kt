package settings.usecase

import kotlinx.coroutines.flow.Flow
import settings.model.AllSettings

interface GetAllSettings {
    operator fun invoke(): Flow<AllSettings>
}
