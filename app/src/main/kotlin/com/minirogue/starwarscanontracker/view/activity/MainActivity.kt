package com.minirogue.starwarscanontracker.view.activity

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
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
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.holocanon.feature.select.filters.FilterSelectionNav
import com.holocanon.feature.settings.SettingsNav
import com.holocanon.library.navigation.AppBarConfig
import com.holocanon.library.navigation.NavContributor
import com.minirogue.holocanon.feature.home.screen.HomeNav
import com.minirogue.holocanon.feature.media.list.usecase.MediaListNav
import com.minirogue.holoclient.usecase.MaybeUpdateMediaDatabase
import com.minirogue.starwarscanontracker.R
import com.minirogue.starwarscanontracker.core.usecase.UpdateFilters
import compose.theme.HolocanonTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import settings.model.DarkModeSetting
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var maybeUpdateMediaDatabase: MaybeUpdateMediaDatabase

    @Inject
    lateinit var updateFilters: UpdateFilters

    @Inject
    lateinit var navContributors: Set<@JvmSuppressWildcards NavContributor>

    private val mainActivityViewModel: MainActivityViewModel by viewModels()

    private enum class TabInfo(val tabNameRes: Int, val navDestination: Any) {
        // The order here defines the order of the tabs
        HOME(R.string.nav_home, HomeNav),
        MEDIA_LIST(R.string.nav_media_list, MediaListNav),
        FILTERS(R.string.nav_filters, FilterSelectionNav),
        ;

        companion object {
            fun fromNavDestination(navDestination: NavDestination): TabInfo? {
                return entries.find { navDestination.hasRoute(it.navDestination::class) }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch { // TODO this should be done elsewhere
            // Update filters based on current information
            updateFilters()
            // Update media database if needed.
            maybeUpdateMediaDatabase()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val appBarConfig = remember { mutableStateOf(AppBarConfig()) }
            val darkModeSetting by mainActivityViewModel.darkModeSetting.collectAsStateWithLifecycle(
                DarkModeSetting.SYSTEM
            )
            HolocanonTheme(darkModeSetting) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = { HolocanonAppBar(navController, appBarConfig.value) },
                ) { padding ->
                    MainScreen(Modifier.padding(padding), navController) { appBarConfig.value = it }
                }
            }
        }

        mainActivityViewModel.globalToasts
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { Toast.makeText(this, it, Toast.LENGTH_SHORT).show() }
            .launchIn(lifecycleScope) // TODO move to snackbar?
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun HolocanonAppBar(navController: NavController, appBarConfig: AppBarConfig) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        CenterAlignedTopAppBar(
            title = {
                Text(
                    appBarConfig.title.takeIf { it.isNotBlank() }
                        ?: stringResource(R.string.app_name),
                )
            },
            navigationIcon = {
                if (navBackStackEntry?.destination?.let { TabInfo.fromNavDestination(it) } == null) {
                    IconButton(
                        onClick = { navController.popBackStack() },
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = stringResource(R.string.content_description_back_button),
                        )
                    }
                }
            },
            actions = {
                appBarConfig.actions.forEach { it() }
                IconButton(onClick = { navController.navigate(SettingsNav) }) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = stringResource(R.string.content_description_navigate_to_settings),
                    )
                }
            },
        )
    }

    @Composable
    private fun MainScreen(
        modifier: Modifier = Modifier,
        navController: NavHostController,
        onAppBarConfig: (AppBarConfig) -> Unit,
    ) =
        Column(modifier = modifier.fillMaxSize()) {
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
                    println("Contributing nav graph: ${it.javaClass.name}")
                    it.invoke(this, navController, onAppBarConfig)
                }
            }
        }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}
