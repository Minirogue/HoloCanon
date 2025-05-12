package com.holocanon.library.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder

interface NavContributor {
    operator fun invoke(navGraphBuilder: NavGraphBuilder, navController: NavController)
}
