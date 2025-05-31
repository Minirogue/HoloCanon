package com.holocanon.app.shared.di

import com.holocanon.library.navigation.NavContributor
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.MergeComponent
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@MergeComponent(AppScope::class)
@SingleIn(AppScope::class)
abstract class AppComponent {
    abstract val navContributors: Set<NavContributor>
}

@MergeComponent.CreateComponent
expect fun create(): AppComponent
