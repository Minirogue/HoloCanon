package com.holocanon.feature.select.filters.test.app

import com.holocanon.feature.select.filters.FilterSelectionNav
import com.minirogue.feature.test.app.model.TestScreen
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
        fun provideSelectFilterScreen(): TestScreen =
            TestScreen.compose("select filters", FilterSelectionNav)

        @Provides
        fun provideTestScreens(screens: Set<@JvmSuppressWildcards TestScreen>): List<@JvmSuppressWildcards TestScreen> =
            screens.sortedBy { it.screenName }
    }
}
