package com.holocanon.library.settings.internal.usecase

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.holocanon.library.settings.usecase.IsNetworkAllowed
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import settings.usecase.ShouldSyncViaWifiOnly

@Inject
@ContributesBinding(AppScope::class)
class IsNetworkAllowedImpl(
    private val connMgr: ConnectivityManager,
    shouldSyncViaWifiOnly: ShouldSyncViaWifiOnly,
) : IsNetworkAllowed {

    private val unmeteredNetworkConnectedFlow: Flow<Boolean> = callbackFlow {
        val networkCallback = object : ConnectivityManager.NetworkCallback() {

            // Network capabilities have changed for the network
            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities,
            ) {
                super.onCapabilitiesChanged(network, networkCapabilities)
                val unmetered =
                    networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)
                trySend(unmetered)
            }
        }
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connMgr.registerNetworkCallback(networkRequest, networkCallback)
        awaitClose {
            connMgr.unregisterNetworkCallback(networkCallback)
        }
    }

    private val unmeteredOnlyFlow: Flow<Boolean> = shouldSyncViaWifiOnly()

    override fun invoke(): Flow<Boolean> =
        combine(
            unmeteredOnlyFlow,
            unmeteredNetworkConnectedFlow,
        ) { unmeteredOnly, unmeteredConnected ->
            (unmeteredOnly && unmeteredConnected) || !unmeteredOnly
        }
}
