package com.minirogue.holocanon.feature.home.screen.nav

import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.holocanon.library.navigation.AppBarConfig
import com.holocanon.library.navigation.NavContributor
import com.minirogue.holocanon.feature.home.screen.HomeNav
import com.minirogue.holocanon.feature.home.screen.HomeScreen
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@ContributesBinding(AppScope::class, multibinding = true)
@SingleIn(AppScope::class)
class HomeNavContributor : NavContributor() {
    override fun invoke(
        navGraphBuilder: NavGraphBuilder,
        navController: NavController,
        setAppBar: (AppBarConfig) -> Unit,
    ) = with(navGraphBuilder) {
        composable<HomeNav> { _ ->
            LaunchedEffect(true) { setAppBar(AppBarConfig()) }
            HomeScreen()
        }
    }
}
