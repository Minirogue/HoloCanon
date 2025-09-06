package com.holocanon.feature.select.filters.test.app

import android.app.Application
import com.minirogue.feature.test.app.TestAppDi
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.createGraph

class SelectFiltersTestApp : Application() {
    override fun onCreate() {
        super.onCreate()
        TestAppDi.setInstance(createGraph<SelectFiltersTestAppDi>().testAppDiFactory.createTestAppDi())
    }
}

@DependencyGraph(scope = AppScope::class)
interface SelectFiltersTestAppDi {
    val testAppDiFactory : TestAppDi.Factory
}
