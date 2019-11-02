package com.minirogue.starwarscanontracker.application

import android.app.Application
import com.minirogue.starwarscanontracker.koin.appModule
import com.minirogue.starwarscanontracker.koin.preferencesModule
import com.minirogue.starwarscanontracker.koin.roomModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class CanonTrackerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        //initialize Koin for dependency injection
        startKoin {
            androidLogger()
            androidContext(this@CanonTrackerApplication)
            modules(listOf(appModule, roomModule, preferencesModule))
        }
    }
}