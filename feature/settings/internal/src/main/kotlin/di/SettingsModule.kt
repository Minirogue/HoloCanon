package di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import usecase.GetSettingsFragment
import usecase.GetSettingsFragmentImpl

@Module
@InstallIn(ActivityComponent::class)
internal interface SettingsModule {
    @Binds
    fun provideGetSettingsFragment(getSettingsFragmentImpl: GetSettingsFragmentImpl): GetSettingsFragment
}