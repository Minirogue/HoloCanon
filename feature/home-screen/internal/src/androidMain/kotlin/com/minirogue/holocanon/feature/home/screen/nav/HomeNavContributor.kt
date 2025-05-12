package com.minirogue.holocanon.feature.home.screen.nav

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.holocanon.library.navigation.NavContributor
import com.minirogue.holocanon.feature.home.screen.HomeNav
import com.minirogue.holocanon.feature.home.screen.HomeScreen

class HomeNavContributor : NavContributor {
    override fun invoke(navGraphBuilder: NavGraphBuilder, navController: NavController) =
        with(navGraphBuilder) {
            composable<HomeNav> { _ -> HomeScreen() }
        }
}
