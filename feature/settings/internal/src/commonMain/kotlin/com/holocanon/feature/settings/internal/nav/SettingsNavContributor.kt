package com.holocanon.feature.settings.internal.nav

import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.holocanon.feature.settings.SettingsNav
import com.holocanon.feature.settings.internal.view.SettingsScreen
import com.holocanon.library.navigation.AppBarConfig
import com.holocanon.library.navigation.NavContributor
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.Provider
import view.SettingsScreen
import viewmodel.SettingsViewModel

@Inject
@ContributesIntoSet(AppScope::class)
class SettingsNavContributor(private val viewModelProvider: Provider<SettingsViewModel>) : NavContributor() {
    override fun invoke(
        navGraphBuilder: NavGraphBuilder,
        navController: NavController,
        setAppBar: (AppBarConfig) -> Unit,
    ) = with(navGraphBuilder) {
        composable<SettingsNav> {
            LaunchedEffect(true) { setAppBar(AppBarConfig()) }
            SettingsScreen(viewModelProvider)
        }
    }
}
