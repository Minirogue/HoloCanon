package com.holocanon.library.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder

abstract class NavContributor {
    abstract operator fun invoke(
        navGraphBuilder: NavGraphBuilder,
        navController: NavController,
        setAppBar: (AppBarConfig) -> Unit,
    )
}
