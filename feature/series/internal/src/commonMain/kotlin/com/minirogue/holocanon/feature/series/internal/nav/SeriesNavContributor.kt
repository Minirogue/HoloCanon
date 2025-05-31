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
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@ContributesBinding(AppScope::class, multibinding = true)
@SingleIn(AppScope::class)
class SeriesNavContributor : NavContributor() {
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
