package com.minirogue.starwarscanontracker.application

import android.app.Application
import com.minirogue.starwarscanontracker.dagger.AppComponent
import com.minirogue.starwarscanontracker.dagger.AppModule
import com.minirogue.starwarscanontracker.dagger.DaggerAppComponent
import com.minirogue.starwarscanontracker.dagger.RoomModule


class CanonTrackerApplication : Application() {

    lateinit var appComponent: AppComponent


    override fun onCreate() {
        super.onCreate()
        appComponent = initDagger(this)

    }

    //Initialize Dagger2 for dependency injection
    private fun initDagger(app: CanonTrackerApplication): AppComponent =
            DaggerAppComponent.builder()
                    .appModule(AppModule(app))
                    .roomModule(RoomModule(app))
                    .build()
}