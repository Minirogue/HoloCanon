package com.holocanon.feature.global.notification.internal.data

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

@Inject
@SingleIn(AppScope::class)
internal class InAppNotificationRepository {
    private val inAppNotificationChannel = Channel<String>(Channel.BUFFERED)
    fun enqueueNotification(message: String) {
        inAppNotificationChannel.trySend(message)
    }

    fun getNotifications(): Flow<String> {
        return inAppNotificationChannel.receiveAsFlow()
    }
}
