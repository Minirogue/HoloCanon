package com.minirogue.holocanon.feature.home.screen.di

import com.holocanon.library.navigation.NavContributor
import com.minirogue.holocanon.feature.home.screen.nav.HomeNavContributor
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(ActivityComponent::class)
interface HomeScreenModule {
    @Binds
    @IntoSet
    fun bindHomeNavContributor(impl: HomeNavContributor): NavContributor
}
