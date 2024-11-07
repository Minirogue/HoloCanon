package com.holocanon.feature.global.notification.usecase

import kotlinx.coroutines.flow.Flow

interface GetGlobalToasts {
    operator fun invoke(): Flow<String>
}
