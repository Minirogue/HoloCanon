package com.minirogue.holocanon.feature.media.item.internal.nav

import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.holocanon.library.navigation.AppBarConfig
import com.holocanon.library.navigation.NavContributor
import com.minirogue.holocanon.feature.media.item.internal.fragment.MediaItemScreen
import com.minirogue.holocanon.feature.media.item.usecase.MediaItemNav
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.Inject

@Inject
@ContributesIntoSet(AppScope::class)
class MediaItemNavContributor : NavContributor() {
    override fun invoke(
        navGraphBuilder: NavGraphBuilder,
        navController: NavController,
        setAppBar: (AppBarConfig) -> Unit,
    ) =
        with(navGraphBuilder) {
            composable<MediaItemNav> { backStackEntry ->
                LaunchedEffect(true) { setAppBar(AppBarConfig()) }
                val mediaItemNav: MediaItemNav = backStackEntry.toRoute()
                MediaItemScreen(itemId = mediaItemNav.itemId, navController = navController)
            }
        }
}
