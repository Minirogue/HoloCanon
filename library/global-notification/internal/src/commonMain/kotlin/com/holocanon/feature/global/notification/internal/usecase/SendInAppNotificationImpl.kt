package com.holocanon.feature.global.notification.internal.usecase

import com.holocanon.feature.global.notification.internal.data.InAppNotificationRepository
import com.holocanon.feature.global.notification.usecase.SendInAppNotification
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

@Inject
@ContributesBinding(AppScope::class)
class SendInAppNotificationImpl internal constructor(
    private val repository: InAppNotificationRepository,
) : SendInAppNotification {
    override fun invoke(message: String) {
        repository.enqueueNotification(message)
    }
}
