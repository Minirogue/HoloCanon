package com.holocanon.app.shared.di

import com.holocanon.app.shared.MainViewModel
import com.holocanon.library.navigation.NavContributor

interface AppDependencyGraph {
    val navContributors: Set<NavContributor>
    val mainViewModel: MainViewModel
}

expect fun getAppDependencyGraph(platformDependencies: PlatformDependencies): AppDependencyGraph
