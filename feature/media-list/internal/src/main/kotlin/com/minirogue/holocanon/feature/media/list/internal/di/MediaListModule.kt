package com.minirogue.holocanon.feature.media.list.internal.di

import com.minirogue.holocanon.feature.media.list.internal.usecase.GetMediaListFragmentImpl
import com.minirogue.holocanon.feature.media.list.usecase.GetMediaListFragment
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent

@Module
@InstallIn(FragmentComponent::class)
internal interface MediaListModule {
    @Binds
    fun bindGetMediaListFragment(impl: GetMediaListFragmentImpl): GetMediaListFragment
}

