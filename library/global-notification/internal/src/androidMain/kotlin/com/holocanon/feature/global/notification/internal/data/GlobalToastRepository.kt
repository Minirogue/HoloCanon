package com.holocanon.feature.global.notification.internal.data

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

@Inject
@SingleIn(AppScope::class)
internal class GlobalToastRepository {
    private val toastEventChannel = Channel<String>(Channel.BUFFERED)
    fun enqueueToast(message: String) {
        toastEventChannel.trySend(message)
    }

    fun getToasts(): Flow<String> {
        return toastEventChannel.receiveAsFlow()
    }
}
