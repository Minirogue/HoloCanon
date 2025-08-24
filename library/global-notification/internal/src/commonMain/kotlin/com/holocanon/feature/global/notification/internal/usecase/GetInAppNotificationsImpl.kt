package com.holocanon.feature.global.notification.internal.usecase

import com.holocanon.feature.global.notification.internal.data.InAppNotificationRepository
import com.holocanon.feature.global.notification.usecase.GetInAppNotifications
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow

@Inject
@ContributesBinding(AppScope::class)
class GetInAppNotificationsImpl internal constructor(
    private val repository: InAppNotificationRepository,
) : GetInAppNotifications {
    override fun invoke(): Flow<String> {
        return repository.getNotifications()
    }
}
