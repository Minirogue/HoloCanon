package com.holocanon.app.shared

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.holocanon.app.shared.di.create
import com.holocanon.feature.settings.SettingsNav
import com.holocanon.library.navigation.AppBarConfig
import com.holocanon.library.navigation.NavContributor
import compose.theme.HolocanonTheme
import holocanon.app.shared.generated.resources.Res
import holocanon.app.shared.generated.resources.app_name
import holocanon.app.shared.generated.resources.content_description_back_button
import holocanon.app.shared.generated.resources.content_description_navigate_to_settings
import org.jetbrains.compose.resources.stringResource
import settings.model.DarkModeSetting
import settings.model.Theme

@Composable
fun App(
    navContributors: Set<NavContributor> = create().navContributors,
    viewModel: MainActivityViewModel = hiltViewModel(),
) {
    // Essentially treating this as Application.onCreate()
    LaunchedEffect(true) { viewModel.onAppStart() }

    // Compose components
    val navController = rememberNavController()
    val appBarConfig = remember { mutableStateOf(AppBarConfig()) }
    val snackbarHostState = remember { SnackbarHostState() }

    // top-level state
    val themeSettings: State<Pair<DarkModeSetting, Theme>> =
        viewModel.themeSettings.collectAsStateWithLifecycle(
            Pair(DarkModeSetting.SYSTEM, Theme.Force),
        )
    val notification = viewModel.globalToasts.collectAsStateWithLifecycle(null)

    LaunchedEffect(notification.value) {
        notification.value?.also { snackbarHostState.showSnackbar(it) }
    }

    HolocanonTheme(themeSettings.value.first, themeSettings.value.second) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = { HolocanonAppBar(navController, appBarConfig.value) },
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
        ) { padding ->
            MainScreen(
                Modifier.padding(padding),
                navController,
                navContributors,
            ) { appBarConfig.value = it }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HolocanonAppBar(navController: NavController, appBarConfig: AppBarConfig) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    CenterAlignedTopAppBar(
        title = {
            Text(
                appBarConfig.title.takeIf { it.isNotBlank() }
                    ?: stringResource(Res.string.app_name),
            )
        },
        navigationIcon = {
            if (navBackStackEntry?.destination?.let { TabInfo.fromNavDestination(it) } == null) {
                IconButton(
                    onClick = { navController.popBackStack() },
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = stringResource(Res.string.content_description_back_button),
                    )
                }
            }
        },
        actions = {
            appBarConfig.actions.forEach { it() }
            IconButton(onClick = { navController.navigate(SettingsNav) }) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = stringResource(Res.string.content_description_navigate_to_settings),
                )
            }
        },
    )
}

@Composable
private fun MainScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    navContributors: Set<NavContributor>,
    onAppBarConfig: (AppBarConfig) -> Unit,
) = Column(modifier = modifier.fillMaxSize()) {
    val currentBackStackEntry = navController.currentBackStackEntryAsState()
    val selectedTab = remember {
        derivedStateOf {
            currentBackStackEntry.value?.destination?.let { TabInfo.fromNavDestination(it) }
        }
    }

    selectedTab.value?.also {
        TabRow(it.ordinal) {
            TabInfo.entries.forEach { tabInfo ->
                Tab(
                    selected = tabInfo == selectedTab.value,
                    onClick = { navController.navigate(route = tabInfo.navDestination) },
                    text = { Text(stringResource(tabInfo.tabNameRes)) },
                )
            }
        }
    }
    NavHost(
        navController = navController,
        startDestination = TabInfo.HOME.navDestination,
    ) {
        navContributors.forEach {
            it.invoke(this, navController, onAppBarConfig)
        }
    }
}
