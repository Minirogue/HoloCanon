package settings.usecase

import kotlinx.coroutines.flow.Flow

interface ShouldSyncViaWifiOnly {
    operator fun invoke(): Flow<Boolean>
}
