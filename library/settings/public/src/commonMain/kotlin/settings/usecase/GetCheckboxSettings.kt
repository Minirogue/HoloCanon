package settings.usecase

import kotlinx.coroutines.flow.Flow
import settings.model.CheckboxSettings

interface GetCheckboxSettings {
    operator fun invoke(): Flow<CheckboxSettings>
}
