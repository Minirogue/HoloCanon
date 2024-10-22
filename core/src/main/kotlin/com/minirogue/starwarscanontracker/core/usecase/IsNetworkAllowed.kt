package com.minirogue.starwarscanontracker.core.usecase

import kotlinx.coroutines.flow.Flow

interface IsNetworkAllowed {
    operator fun invoke(): Flow<Boolean>
}

