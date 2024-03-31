package com.minirogue.holocanon.feature.home.screen

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@Module
@InstallIn(ActivityComponent::class)
interface HomeScreenModule {
    @Binds
    fun bindGetHomeFragment(impl: GetHomeFragmentImpl): GetHomeFragment
}
