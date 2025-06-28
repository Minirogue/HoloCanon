package com.holocanon.feature.main.screen.internal

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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import com.holocanon.feature.settings.SettingsNav
import com.holocanon.library.navigation.AppBarConfig
import com.holocanon.library.navigation.NavContributor
import holocanon.feature.main_screen.internal.generated.resources.Res
import holocanon.feature.main_screen.internal.generated.resources.app_name
import holocanon.feature.main_screen.internal.generated.resources.content_description_back_button
import holocanon.feature.main_screen.internal.generated.resources.content_description_navigate_to_settings
import org.jetbrains.compose.resources.stringResource

@Composable
fun TopLevelScreen(
    navHostController: NavHostController,
    appBarConfig: MutableState<AppBarConfig>,
    snackbarHostState: SnackbarHostState,
    navContributors: Set<NavContributor>,
) = Scaffold(
    modifier = Modifier.fillMaxSize(),
    topBar = { HolocanonAppBar(navHostController, appBarConfig.value) },
    snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
) { padding ->
    MainScreen(
        Modifier.padding(padding),
        navHostController,
        navContributors,
    ) { appBarConfig.value = it }
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
