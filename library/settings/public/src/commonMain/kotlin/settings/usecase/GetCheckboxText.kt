package settings.usecase

import kotlinx.coroutines.flow.Flow

interface GetCheckboxText {
    operator fun invoke(): Flow<Array<String>>
}
