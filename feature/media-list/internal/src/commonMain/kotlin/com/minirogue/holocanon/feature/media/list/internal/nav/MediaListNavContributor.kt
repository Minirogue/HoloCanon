package com.minirogue.holocanon.feature.media.list.internal.nav

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.holocanon.library.navigation.AppBarConfig
import com.holocanon.library.navigation.NavContributor
import com.minirogue.holocanon.feature.media.list.usecase.MediaListNav
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.Provider
import internal.view.MediaListScreen
import internal.viewmodel.MediaListViewModel

@Inject
@ContributesIntoSet(AppScope::class)
class MediaListNavContributor internal constructor(
    private val viewModelProvider: Provider<MediaListViewModel>,
) : NavContributor() {
    override fun invoke(
        navGraphBuilder: NavGraphBuilder,
        navController: NavController,
        setAppBar: (AppBarConfig) -> Unit,
    ) = with(navGraphBuilder) {
        composable<MediaListNav> {
            MediaListScreen(viewModelProvider = viewModelProvider, navController = navController, setAppBar = setAppBar)
        }
    }
}
