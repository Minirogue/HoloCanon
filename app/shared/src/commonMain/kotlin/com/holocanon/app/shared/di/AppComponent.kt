package com.holocanon.app.shared.di

import com.holocanon.library.navigation.NavContributor
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph

@DependencyGraph(scope = AppScope::class)
interface AppDependencyGraph {
    val navContributors: Set<NavContributor>
}
