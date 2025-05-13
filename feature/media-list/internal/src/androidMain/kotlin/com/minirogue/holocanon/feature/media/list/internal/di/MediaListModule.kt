package com.minirogue.holocanon.feature.media.list.internal.di

import com.holocanon.library.navigation.NavContributor
import com.minirogue.holocanon.feature.media.list.internal.nav.MediaListNavContributor
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(ActivityComponent::class)
internal interface MediaListModule {
    @Binds
    @IntoSet
    fun bindMediaListNavContributor(navContributor: MediaListNavContributor): NavContributor
}
