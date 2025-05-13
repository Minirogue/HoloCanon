package com.minirogue.holocanon.feature.select.filters.internal.nav

import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.holocanon.feature.select.filters.FilterSelectionNav
import com.holocanon.library.navigation.AppBarConfig
import com.holocanon.library.navigation.NavContributor
import com.minirogue.holocanon.feature.select.filters.internal.view.FilterSelectionScreen
import javax.inject.Inject

class FilterSelectionNavContributor @Inject constructor() : NavContributor() {
    override fun invoke(
        navGraphBuilder: NavGraphBuilder,
        navController: NavController,
        setAppBar: (AppBarConfig) -> Unit,
    ) = with(navGraphBuilder) {
        composable<FilterSelectionNav> {
            LaunchedEffect(true) { setAppBar(AppBarConfig()) }
            FilterSelectionScreen()
        }
    }
}
