package com.minirogue.holocanon.feature.home.screen.nav

import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.holocanon.library.navigation.AppBarConfig
import com.holocanon.library.navigation.NavContributor
import com.minirogue.holocanon.feature.home.screen.HomeNav
import com.minirogue.holocanon.feature.home.screen.HomeScreen
import javax.inject.Inject

class HomeNavContributor @Inject constructor() : NavContributor() {
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
