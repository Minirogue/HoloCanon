package com.minirogue.starwarscanontracker.application

import android.net.ConnectivityManager
import com.minirogue.starwarscanontracker.core.usecase.IsNetworkAllowed
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import settings.usecase.ShouldSyncViaWifiOnly
import javax.inject.Inject

class IsNetworkAllowedImpl @Inject constructor(
        private val connMgr: ConnectivityManager,
        private val shouldSyncViaWifiOnly: ShouldSyncViaWifiOnly,
) : IsNetworkAllowed {

    private fun unmeteredOnly(): Flow<Boolean> = shouldSyncViaWifiOnly()

    override fun invoke(): Flow<Boolean> = unmeteredOnly().map { !it || !connMgr.isActiveNetworkMetered }
}
