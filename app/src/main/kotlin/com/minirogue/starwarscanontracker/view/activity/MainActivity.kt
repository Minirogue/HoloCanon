package com.minirogue.starwarscanontracker.view.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.minirogue.holocanon.feature.media.item.usecase.GetMediaItemFragment
import com.minirogue.holocanon.feature.series.GetSeriesFragment
import com.minirogue.holoclient.usecase.MaybeUpdateMediaDatabase
import com.minirogue.starwarscanontracker.R
import com.minirogue.starwarscanontracker.core.nav.NavigationDestination
import com.minirogue.starwarscanontracker.core.nav.NavigationViewModel
import com.minirogue.starwarscanontracker.core.usecase.UpdateFilters
import com.minirogue.starwarscanontracker.view.fragment.TabbedListContainerFragment
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

    private val navigationViewModel: NavigationViewModel by viewModels()
    private val mainActivityViewModel: MainActivityViewModel by viewModels()

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
            HolocanonTheme {
                MainScreen()
            }
        }
        setContentView(R.layout.activity_main)

        // When the user opens a fresh instance of the app
        if (savedInstanceState == null) {
            // initialize the fragment to the entry fragment
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, TabbedListContainerFragment())
                .commit()
        }

        // Set up the toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportFragmentManager.apply {
            addOnBackStackChangedListener {
                supportActionBar?.setDisplayHomeAsUpEnabled(backStackEntryCount > 0)
            }
        }

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                navigationViewModel.navigationDestination.collect { destination ->
                    when (destination) {
                        is NavigationDestination.MediaItemScreen -> navigateToMediaItem(destination.itemId)
                        is NavigationDestination.SeriesScreen -> navigateToSeries(destination.seriesName)
                    }
                }
            }
        }
        mainActivityViewModel.globalToasts
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { Toast.makeText(this, it, Toast.LENGTH_SHORT).show() }
            .launchIn(lifecycleScope)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.action_bar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.toolbar_settings -> {
                navigateToToolbarOption(ToolbarOption.Settings)
                true
            }

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

    /**
     * Replaces the displayed fragment with one associated to a [ToolbarOption].
     *
     * Checks to see if a Fragment associated to [toolbarOption] exists, then replaces the displayed
     * fragment with that one, or a new Fragment if necessary.
     */
    private fun navigateToToolbarOption(toolbarOption: ToolbarOption) {
        // Check if an instance of the desired Fragment already exists somewhere on the backstack
        var frag = supportFragmentManager.findFragmentByTag(toolbarOption.fragmentTag)
        // If the fragment doesn't already exist, create a new one
        if (frag == null) {
            frag = when (toolbarOption) {
                ToolbarOption.Settings -> getSettingsFragment()
            }
        }
        // Replace the fragment
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, frag, toolbarOption.fragmentTag)
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
