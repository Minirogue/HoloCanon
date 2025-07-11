package com.holocanon.library.settings.internal.usecase

import com.holocanon.library.settings.usecase.IsNetworkAllowed
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import settings.usecase.ShouldSyncViaWifiOnly

@Inject
@ContributesBinding(AppScope::class)
class IsNetworkAllowedImpl(
    private val shouldSyncViaWifiOnly: ShouldSyncViaWifiOnly,
) : IsNetworkAllowed {

    private fun unmeteredOnly(): Flow<Boolean> = shouldSyncViaWifiOnly()

    override fun invoke(): Flow<Boolean> = unmeteredOnly().map { true } // TODO check network on iOS
}
