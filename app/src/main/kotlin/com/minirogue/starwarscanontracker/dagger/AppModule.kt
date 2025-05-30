package com.minirogue.starwarscanontracker.dagger

import android.app.Application
import android.content.Context
import android.content.res.Resources
import android.net.ConnectivityManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface AppModule {

    companion object {
        @Provides
        fun provideContext(application: Application): Context = application

        @Provides
        fun provideResources(application: Application): Resources = application.resources

        @Provides
        fun provideConnManager(app: Application): ConnectivityManager {
            return app.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        }
    }
}
