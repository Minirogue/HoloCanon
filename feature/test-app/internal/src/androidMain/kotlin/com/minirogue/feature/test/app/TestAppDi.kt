package com.minirogue.feature.test.app

import com.holocanon.library.navigation.NavContributor
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesGraphExtension
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension

abstract class TestAppScope private constructor()

@GraphExtension(TestAppScope::class)
interface TestAppDi {

    val navContributors: Set<NavContributor>

    @ContributesTo(AppScope::class)
    @GraphExtension.Factory
    interface Factory {
        fun createTestAppDi(): TestAppDi
    }
    companion object {
        fun setInstance(newInstance: TestAppDi) { instance = newInstance }
        internal lateinit var instance: TestAppDi
    }
}
