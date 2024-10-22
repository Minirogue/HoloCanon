package di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import settings.usecase.GetCheckboxText
import usecase.GetCheckboxTextImpl
import usecase.GetSettingsFragment
import usecase.GetSettingsFragmentImpl

@Module
@InstallIn(SingletonComponent::class)
internal interface SettingsModule {
    @Binds
    fun provideGetSettingsFragment(impl: GetSettingsFragmentImpl): GetSettingsFragment

    @Binds
    fun bindGetCheckboxText(impl: GetCheckboxTextImpl): GetCheckboxText
}
