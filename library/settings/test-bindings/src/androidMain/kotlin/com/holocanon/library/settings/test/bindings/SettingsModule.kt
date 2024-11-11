package com.holocanon.library.settings.test.bindings

import com.holocanon.library.settings.test.bindings.fakes.FakeGetCheckboxSettings
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import settings.usecase.GetCheckboxSettings

@Module
@InstallIn(SingletonComponent::class)
interface SettingsModule {
    @Binds
    fun bindGetCheckboxSettings(impl: FakeGetCheckboxSettings): GetCheckboxSettings
}