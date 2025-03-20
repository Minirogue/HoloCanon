package com.holocanon.feature.global.notification.internal.data

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class GlobalToastRepository @Inject constructor() {
    private val toastEventChannel = Channel<String>(Channel.BUFFERED)
    fun enqueueToast(message: String) {
        toastEventChannel.trySend(message)
    }

    fun getToasts(): Flow<String> {
        return toastEventChannel.receiveAsFlow()
    }
}
