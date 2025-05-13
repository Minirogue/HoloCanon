package com.minirogue.holocanon.feature.series.internal.nav

import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.holocanon.library.navigation.AppBarConfig
import com.holocanon.library.navigation.NavContributor
import com.minirogue.holocanon.feature.series.SeriesNav
import com.minirogue.holocanon.feature.series.internal.view.SeriesScreen
import javax.inject.Inject

internal class SeriesNavContributor @Inject constructor() : NavContributor() {
    override fun invoke(
        navGraphBuilder: NavGraphBuilder,
        navController: NavController,
        setAppBar: (AppBarConfig) -> Unit,
    ) = with(navGraphBuilder) {
        composable<SeriesNav> { backStackEntry ->
            val seriesNav: SeriesNav = backStackEntry.toRoute()
            LaunchedEffect(true) { setAppBar(AppBarConfig()) }
            SeriesScreen(seriesName = seriesNav.seriesName, navController = navController)
        }
    }
}
