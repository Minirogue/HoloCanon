package com.holocanon.feature.global.notification.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull

class FakeGetInAppNotifications : GetInAppNotifications {
    private val stateFlow = MutableStateFlow<String?>(null)

    fun emit(message: String) {
        stateFlow.value = message
    }
    override fun invoke(): Flow<String> {
        return stateFlow.filterNotNull()
    }
}
