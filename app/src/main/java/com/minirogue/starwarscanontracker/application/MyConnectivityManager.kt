package com.minirogue.starwarscanontracker.application

import android.net.ConnectivityManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import settings.usecase.ShouldSyncViaWifiOnly
import javax.inject.Inject

class MyConnectivityManager @Inject constructor(
    private val connMgr: ConnectivityManager,
    private val shouldSyncViaWifiOnly: ShouldSyncViaWifiOnly,
) {

    private fun unmeteredOnly(): Flow<Boolean> = shouldSyncViaWifiOnly()

    fun isNetworkAllowed(): Flow<Boolean> = unmeteredOnly().map { !it || !connMgr.isActiveNetworkMetered }
}
