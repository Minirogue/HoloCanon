package com.holocanon.app.shared.di

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.createGraphFactory
import io.ktor.http.ContentType

@DependencyGraph(AppScope::class)
interface IosDependencyGraph : AppDependencyGraph {

    @DependencyGraph.Factory
    fun interface Factory {
        fun create(@Provides platformDependencies: PlatformDependencies): IosDependencyGraph
    }
}

actual fun getAppDependencyGraph(platformDependencies: PlatformDependencies): AppDependencyGraph =
    createGraphFactory<IosDependencyGraph.Factory>().create(platformDependencies)