package com.holocanon.library.settings.usecase

import kotlinx.coroutines.flow.Flow

interface IsNetworkAllowed {
    operator fun invoke(): Flow<Boolean>
}
