package com.holocanon.library.settings.internal.usecase

import android.net.ConnectivityManager
import com.holocanon.library.settings.usecase.IsNetworkAllowed
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import settings.usecase.ShouldSyncViaWifiOnly
import javax.inject.Inject

internal class IsNetworkAllowedImpl @Inject constructor(
    private val connMgr: ConnectivityManager,
    private val shouldSyncViaWifiOnly: ShouldSyncViaWifiOnly,
) : IsNetworkAllowed {

    private fun unmeteredOnly(): Flow<Boolean> = shouldSyncViaWifiOnly()

    override fun invoke(): Flow<Boolean> = unmeteredOnly().map { !it || !connMgr.isActiveNetworkMetered }
}
