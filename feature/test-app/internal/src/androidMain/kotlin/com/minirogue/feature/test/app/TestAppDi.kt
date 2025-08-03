package com.minirogue.feature.test.app

import com.holocanon.library.navigation.NavContributor
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesGraphExtension

abstract class TestAppScope private constructor()

@ContributesGraphExtension(TestAppScope::class)
interface TestAppDi {

    val navContributors: Set<NavContributor>

    @ContributesGraphExtension.Factory(AppScope::class)
    interface Factory {
        fun createTestAppDi(): TestAppDi
    }
    companion object {
        fun setInstance(newInstance: TestAppDi) { instance = newInstance }
        internal lateinit var instance: TestAppDi
    }
}
