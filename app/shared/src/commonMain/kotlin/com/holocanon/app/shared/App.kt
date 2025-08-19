package com.holocanon.app.shared

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.holocanon.app.shared.di.AppDependencyGraph
import com.holocanon.feature.main.screen.internal.TopLevelScreen
import com.holocanon.library.navigation.AppBarConfig
import compose.theme.HolocanonTheme
import compose.theme.collectAsStateSafely
import setImageLoader
import settings.model.DarkModeSetting
import settings.model.Theme

@Composable
fun App(
    appDependencyGraph: AppDependencyGraph,
    viewModel: MainViewModel = viewModel { appDependencyGraph.mainViewModel },
) {
    // Essentially treating this as Application.onCreate()
    LaunchedEffect(true) { viewModel.onAppStart() }
    setImageLoader()

    // Compose components
    val navController = rememberNavController()
    val navContributors = remember { appDependencyGraph.navContributors }
    val appBarConfig = remember { mutableStateOf(AppBarConfig.DEFAULT) }
    val snackbarHostState = remember { SnackbarHostState() }

    // top-level state
    val themeSettings: State<Pair<DarkModeSetting, Theme>> =
        viewModel.themeSettings.collectAsStateSafely(
            Pair(DarkModeSetting.SYSTEM, Theme.Force),
        )
    val notification = viewModel.inAppNotifications.collectAsStateSafely(null)

    LaunchedEffect(notification.value) {
        notification.value?.also { snackbarHostState.showSnackbar(it) }
    }

    HolocanonTheme(themeSettings.value.first, themeSettings.value.second) {
        TopLevelScreen(
            navHostController = navController,
            appBarConfig = appBarConfig,
            snackbarHostState = snackbarHostState,
            navContributors = navContributors,
        )
    }
}
