package com.minirogue.starwarscanontracker.application

import android.app.Application
import com.holocanon.app.shared.di.AppDependencyGraph
import com.holocanon.app.shared.di.getAppDependencyGraph

class CanonTrackerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val platformDependencies = AndroidPlatformDependencies(this)
        dependencyGraph = getAppDependencyGraph(platformDependencies)
    }

    companion object {
        lateinit var dependencyGraph: AppDependencyGraph
    }
}
