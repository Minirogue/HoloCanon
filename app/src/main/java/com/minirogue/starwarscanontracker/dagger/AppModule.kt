package com.minirogue.starwarscanontracker.dagger

import ApplicationScope
import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import com.minirogue.starwarscanontracker.application.CanonTrackerApplication
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideContext(application: Application): Context = application

    @Provides
    fun provideAppScope(application: Application): ApplicationScope = (application as CanonTrackerApplication).appScope

    @Provides
    fun provideConnManager(app: Application): ConnectivityManager {
        return app.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }
}
