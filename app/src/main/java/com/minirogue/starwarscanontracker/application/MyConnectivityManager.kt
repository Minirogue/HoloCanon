package com.minirogue.starwarscanontracker.application

import android.app.Application
import android.content.SharedPreferences
import android.net.ConnectivityManager
import com.minirogue.starwarscanontracker.R
import javax.inject.Inject

class MyConnectivityManager @Inject constructor(private val connMgr: ConnectivityManager, private val prefs: SharedPreferences, private val app: Application) {

    private fun unmeteredOnly(): Boolean = prefs.getBoolean(app.getString(R.string.setting_unmetered_sync_only), true)

    fun isNetworkAllowed(): Boolean = !connMgr.isActiveNetworkMetered || !unmeteredOnly()

}