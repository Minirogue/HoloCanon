package com.holocanon.feature.select.filters.test.app

import com.minirogue.feature.test.app.model.TestScreen
import com.minirogue.holocanon.feature.select.filters.usecase.GetSelectFiltersFragment
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
interface SelectFiltersTestModule {

    companion object {
        @Provides
        @IntoSet
        fun provideSelectFilterScreen(getSelectFiltersFragment: GetSelectFiltersFragment): TestScreen =
            TestScreen.fragment("select filters") {
                getSelectFiltersFragment()
            }

        @Provides
        fun provideTestScreens(screens: Set<@JvmSuppressWildcards TestScreen>): List<@JvmSuppressWildcards TestScreen> =
            screens.sortedBy { it.screenName }
    }
}
