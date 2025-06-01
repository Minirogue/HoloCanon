package com.holocanon.feature.settings.internal.nav

import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.holocanon.feature.settings.SettingsNav
import com.holocanon.feature.settings.internal.view.SettingsScreen
import com.holocanon.library.navigation.AppBarConfig
import com.holocanon.library.navigation.NavContributor
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@ContributesBinding(AppScope::class, multibinding = true)
@SingleIn(AppScope::class)
class SettingsNavContributor : NavContributor() {
    override fun invoke(
        navGraphBuilder: NavGraphBuilder,
        navController: NavController,
        setAppBar: (AppBarConfig) -> Unit,
    ) = with(navGraphBuilder) {
        composable<SettingsNav> {
            LaunchedEffect(true) { setAppBar(AppBarConfig()) }
            SettingsScreen()
        }
    }
}
