package com.minirogue.starwarscanontracker.view.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.minirogue.starwarscanontracker.R
import com.minirogue.starwarscanontracker.core.model.FilterUpdater
import com.minirogue.starwarscanontracker.view.fragment.AboutFragment
import com.minirogue.starwarscanontracker.view.fragment.SettingsFragment
import com.minirogue.starwarscanontracker.view.fragment.TabbedListContainerFragment
import com.minirogue.usecase.UpdateMediaDatabaseUseCase
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var updateMediaDatabaseUseCase: UpdateMediaDatabaseUseCase

    @Inject
    lateinit var filterUpdater: FilterUpdater

    override fun onResume() {
        super.onResume()
        // Update filters based one current information
        filterUpdater.updateFilters()
        // check for update to room
        updateMediaDatabaseUseCase()
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
                replaceFragment(SETTINGS_TAG)
                true
            }
            R.id.toolbar_about -> {
                replaceFragment(ABOUT_TAG)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Replaces the displayed fragment with one associated to the given tag.
     *
     *
     * Checks to see if a Fragment associated to the tag already exists, then replaces the displayed
     * fragment with that one, if it exists, or a new Fragment of the appropriate type.
     *
     * @param tag a String associated to the type of Fragment that is to be displayed
     */
    private fun replaceFragment(tag: String) {
        // Check if an instance of the desired Fragment already exists somewhere on the backstack
        var frag = supportFragmentManager.findFragmentByTag(tag)
        // If the fragment doesn't already exist, create a new one
        if (frag == null) {
            frag = when (tag) {
                // CANON_LIST_TAG -> TabbedListContainerFragment()
                SETTINGS_TAG -> SettingsFragment()
                // HOME_TAG -> HomeFragment()
                ABOUT_TAG -> AboutFragment()
                // FILTERS_TAG -> FilterSelectionFragment()
                MAIN_TABBED -> TabbedListContainerFragment()
                else -> {
                    Toast.makeText(applicationContext, "Not yet implemented", Toast.LENGTH_SHORT).show()
                    return
                }
            }
        }
        // Call the function to actually replace the fragment
        replaceFragment(frag, tag)
    }

    /**
     * Performs the actual logic of replacing the active Fragment.
     *
     * @param newFrag the fragment to be displayed
     * @param tag     the tag to associate to newFrag
     */
    private fun replaceFragment(newFrag: Fragment, tag: String) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, newFrag, tag)
            .addToBackStack(null)
            .commit()
    }

    companion object {

        // the following are definitions for the tags associated to each of the main Fragments
        private const val SETTINGS_TAG = "settings"
        private const val ABOUT_TAG = "about"
        private const val MAIN_TABBED = "main_tabbed"
    }
}
