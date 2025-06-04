package com.holocanon.app.shared.di

import com.holocanon.app.shared.MainViewModel
import com.holocanon.library.navigation.NavContributor
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.SingleIn

interface AppDependencyGraph {
    val navContributors: Set<NavContributor>
    val mainViewModel: MainViewModel
}

expect fun getAppDependencyGraph(platformDependencies: PlatformDependencies): AppDependencyGraph
