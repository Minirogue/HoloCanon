package com.minirogue.starwarscanontracker.view.activity

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.holocanon.library.navigation.NavContributor
import com.minirogue.holocanon.feature.home.screen.HomeNav
import com.minirogue.holocanon.feature.media.item.usecase.GetMediaItemFragment
import com.minirogue.holocanon.feature.series.GetSeriesFragment
import com.minirogue.holoclient.usecase.MaybeUpdateMediaDatabase
import com.minirogue.starwarscanontracker.R
import com.minirogue.starwarscanontracker.core.usecase.UpdateFilters
import compose.theme.HolocanonTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import usecase.GetSettingsFragment
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var maybeUpdateMediaDatabase: MaybeUpdateMediaDatabase

    @Inject
    lateinit var updateFilters: UpdateFilters

    @Inject
    lateinit var getSettingsFragment: GetSettingsFragment

    @Inject
    lateinit var getMediaItemFragment: GetMediaItemFragment

    @Inject
    lateinit var getSeriesFragment: GetSeriesFragment

    @Inject
    lateinit var navContributors: Set<@JvmSuppressWildcards NavContributor>

    private val mainActivityViewModel: MainActivityViewModel by viewModels()

    private enum class TabInfo(val tabNameRes: Int, val navDestination: Any) {
        // The order here defines the order of the tabs
        HOME(R.string.nav_home, HomeNav),
        MEDIA_LIST(R.string.nav_media_list, HomeNav), // TODO add actual nav
        FILTERS(R.string.nav_filters, HomeNav), // TODO add actual nav
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
            HolocanonTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = { HolocanonAppBar(navController) },
                ) { padding ->
                    MainScreen(Modifier.padding(padding))
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
    private fun HolocanonAppBar(navController: NavController) = TopAppBar(
        title = { Text(stringResource(R.string.app_name)) },
        navigationIcon = {
            if (navController.currentBackStackEntry != null) {
                Icon(
                    Icons.AutoMirrored.Default.ArrowBack,
                    "Back", // TODO extract string
                )
            }
        },
        actions = {
            Icon(
                modifier = Modifier.clickable {
                    navController.navigate(
                        "settings",
                    )
                }, // TODO review navigation destinations
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings", // TODO extract string
            )
        },
    )

    @Composable
    private fun MainScreen(modifier: Modifier = Modifier) =
        Column(modifier = modifier.fillMaxSize()) {
            val mainScreenNavController = rememberNavController()
            val startTab = TabInfo.HOME
            val selectedTab = remember { mutableStateOf(startTab) }

            TabRow(selectedTab.value.ordinal) {
                TabInfo.entries.forEach { tabInfo ->
                    Tab(
                        selected = tabInfo == selectedTab.value,
                        onClick = { selectedTab.value = tabInfo },
                        text = { Text(stringResource(tabInfo.tabNameRes)) },
                    )
                }
            }
            NavHost(
                navController = mainScreenNavController,
                startTab.name,
            ) {
                navContributors.forEach { it.invoke(this, mainScreenNavController) }
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

    private fun navigateToMediaItem(itemId: Long) {
        val viewMediaItemFragment = getMediaItemFragment(itemId)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, viewMediaItemFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun navigateToSeries(seriesName: String) {
        val seriesFragment = getSeriesFragment(seriesName)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, seriesFragment)
            .addToBackStack(null)
            .commit()
    }

    private enum class ToolbarOption(val fragmentTag: String) {
        Settings(SETTINGS_TAG),
    }

    companion object {
        // The following are definitions for the tags associated to the fragments called from the
        // toolbar options.
        private const val SETTINGS_TAG = "settings"
    }
}
