/*
package com.minirogue.starwarscanontracker.koin

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.preference.PreferenceManager
import com.minirogue.starwarscanontracker.R
import com.minirogue.starwarscanontracker.model.room.MediaDatabase
import com.minirogue.starwarscanontracker.model.SWMRepository
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module


//To replace modules in tests, use module(override=true)

val appModule = module {
    single { androidApplication().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager }
}

val roomModule = module {
    single { MediaDatabase.getMediaDataBase(androidApplication()) }
    single { get<MediaDatabase>().daoMedia }
    single { get<MediaDatabase>().daoType }
    single { get<MediaDatabase>().daoFilter}
    single { get<MediaDatabase>().daoSeries}
    single { SWMRepository() }
}

val preferencesModule = module {
    single { PreferenceManager.getDefaultSharedPreferences(androidApplication()) }
    factory(named("checkboxes")) {
        arrayOf(get<SharedPreferences>().getString(androidApplication().getString(R.string.checkbox1_default_text), androidApplication().getString(R.string.checkbox1_default_text)),
                get<SharedPreferences>().getString(androidApplication().getString(R.string.checkbox2_default_text), androidApplication().getString(R.string.checkbox2_default_text)),
                get<SharedPreferences>().getString(androidApplication().getString(R.string.checkbox3_default_text), androidApplication().getString(R.string.checkbox3_default_text)))
    }
    factory(named("unmetered_only")) { get<SharedPreferences>().getBoolean(androidContext().getString(R.string.setting_unmetered_sync_only), true) }
}*/
