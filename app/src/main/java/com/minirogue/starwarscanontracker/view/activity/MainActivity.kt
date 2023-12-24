package com.minirogue.starwarscanontracker.view.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import com.minirogue.starwarscanontracker.R
import com.minirogue.starwarscanontracker.core.model.FilterUpdater
import com.minirogue.starwarscanontracker.view.fragment.AboutFragment
import com.minirogue.starwarscanontracker.view.fragment.TabbedListContainerFragment
import com.minirogue.usecase.UpdateMediaDatabaseUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import usecase.GetSettingsFragment
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var updateMediaDatabaseUseCase: UpdateMediaDatabaseUseCase

    @Inject
    lateinit var filterUpdater: FilterUpdater

    @Inject
    lateinit var getSettingsFragment: GetSettingsFragment

    override fun onResume() {
        super.onResume()
        // Update filters based on current information
        filterUpdater.updateFilters()
        // Update media database if needed.
        lifecycleScope.launch { updateMediaDatabaseUseCase() }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // When the user opens a fresh instance of the app
        if (savedInstanceState == null) {
            // initialize the fragment to the entry fragment
            supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, TabbedListContainerFragment())
                    .commit()
        }

        // Set up the toolbar and navigation drawer
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
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

            R.id.toolbar_about -> {
                navigateToToolbarOption(ToolbarOption.About)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
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
                ToolbarOption.About -> AboutFragment()
            }
        }
        // Replace the fragment
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, frag, toolbarOption.fragmentTag)
                .addToBackStack(null)
                .commit()
    }

    private enum class ToolbarOption(val fragmentTag: String) {
        Settings(SETTINGS_TAG), About(ABOUT_TAG);
    }

    companion object {
        // The following are definitions for the tags associated to the fragments called from the
        // toolbar options.
        private const val SETTINGS_TAG = "settings"
        private const val ABOUT_TAG = "about"
    }
}
