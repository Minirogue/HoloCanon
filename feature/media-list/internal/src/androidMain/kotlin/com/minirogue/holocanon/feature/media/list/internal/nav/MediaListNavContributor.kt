package com.minirogue.holocanon.feature.media.list.internal.nav

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.holocanon.library.navigation.AppBarConfig
import com.holocanon.library.navigation.NavContributor
import com.minirogue.holocanon.feature.media.list.internal.view.MediaListScreen
import com.minirogue.holocanon.feature.media.list.usecase.MediaListNav
import javax.inject.Inject

class MediaListNavContributor @Inject constructor() : NavContributor() {
    override fun invoke(
        navGraphBuilder: NavGraphBuilder,
        navController: NavController,
        setAppBar: (AppBarConfig) -> Unit,
    ) = with(navGraphBuilder) {
        composable<MediaListNav> {
            MediaListScreen(navController = navController, setAppBar = setAppBar)
        }
    }
}
