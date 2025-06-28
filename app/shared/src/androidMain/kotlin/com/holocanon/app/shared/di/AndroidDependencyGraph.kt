package com.holocanon.app.shared.di

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import com.holocanon.library.platform.Platform
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.createGraphFactory

@DependencyGraph(AppScope::class)
interface AndroidDependencyGraph : AppDependencyGraph {

    @Provides
    fun providePlatform(): Platform = Platform.Android

    @Provides
    fun provideApplication(
        platformDependencies: PlatformDependencies,
    ): Application = platformDependencies.application

    @Provides
    fun provideConnManager(app: Application): ConnectivityManager {
        return app.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    @DependencyGraph.Factory
    fun interface Factory {
        fun create(@Provides platformDependencies: PlatformDependencies): AndroidDependencyGraph
    }
}

actual fun getAppDependencyGraph(platformDependencies: PlatformDependencies): AppDependencyGraph =
    createGraphFactory<AndroidDependencyGraph.Factory>().create(platformDependencies)
