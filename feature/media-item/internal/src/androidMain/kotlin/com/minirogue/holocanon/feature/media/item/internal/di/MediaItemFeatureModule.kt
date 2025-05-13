package com.minirogue.holocanon.feature.media.item.internal.di

import com.holocanon.library.navigation.NavContributor
import com.minirogue.holocanon.feature.media.item.internal.nav.MediaItemNavContributor
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(ActivityComponent::class)
internal interface MediaItemFeatureModule {
    @Binds
    @IntoSet
    fun bindsNavContributor(contributor: MediaItemNavContributor): NavContributor
}
