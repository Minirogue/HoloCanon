package com.holocanon.feature.global.notification.usecase

import kotlinx.coroutines.flow.Flow

interface GetInAppNotifications {
    operator fun invoke(): Flow<String>
}
