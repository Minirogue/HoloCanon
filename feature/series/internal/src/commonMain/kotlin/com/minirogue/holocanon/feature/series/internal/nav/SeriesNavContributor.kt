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
import com.minirogue.holocanon.feature.series.internal.view.SeriesViewModel
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.Inject

@Inject
@ContributesIntoSet(AppScope::class)
class SeriesNavContributor internal constructor(
    private val viewModelFactory: SeriesViewModel.Factory,
) : NavContributor() {
    override fun invoke(
        navGraphBuilder: NavGraphBuilder,
        navController: NavController,
        setAppBar: (AppBarConfig) -> Unit,
    ) = with(navGraphBuilder) {
        composable<SeriesNav> { backStackEntry ->
            val seriesNav: SeriesNav = backStackEntry.toRoute()
            LaunchedEffect(true) { setAppBar(AppBarConfig()) }
            SeriesScreen(
                seriesName = seriesNav.seriesName,
                viewModelFactory = viewModelFactory,
                navController = navController,
            )
        }
    }
}
