package com.minirogue.starwarscanontracker.view.activity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.google.android.material.navigation.NavigationView;
import com.minirogue.starwarscanontracker.R;
import com.minirogue.starwarscanontracker.model.CSVImporter;
import com.minirogue.starwarscanontracker.model.FilterUpdater;
import com.minirogue.starwarscanontracker.view.fragment.EntryFragment;
import com.minirogue.starwarscanontracker.view.fragment.FilterSelectionFragment;
import com.minirogue.starwarscanontracker.view.fragment.MediaListFragment;
import com.minirogue.starwarscanontracker.view.fragment.SettingsFragment;


public class MainActivity extends AppCompatActivity {

    //private static final String TAG = "MainActivity";

    //the following are definitions for the tags associated to each of the main Fragments
    private static final String CANON_LIST_TAG = "canon_list";
    private static final String SETTINGS_TAG = "settings";
    private static final String ENTRY_TAG = "entry";
    public static final String FILTERS_TAG = "filters";

    private DrawerLayout drawer;
    private NavigationView navigationView;

    @Override
    protected void onResume() {
        super.onResume();
        //Update filters based one current information
        (new FilterUpdater()).updateFilters();
        //check for update to room
        new CSVImporter(getApplication(), false).execute(CSVImporter.SOURCE_ONLINE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        //When the user opens a fresh instance of the app
        if (savedInstanceState == null) {
            //initialize the fragment to the entry fragment
            replaceFragment(ENTRY_TAG);
            //initialize Fresco
            ImagePipelineConfig config = ImagePipelineConfig.newBuilder(getApplication())
                    .setDownsampleEnabled(true)
                    .build();
            Fresco.initialize(getApplication(), config);
        }

        //Set up the toolbar and navigation drawer
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.nav_media:
                    replaceFragment(CANON_LIST_TAG);
                    break;
                case R.id.nav_settings:
                    replaceFragment(SETTINGS_TAG);
                    break;
                case R.id.nav_about:
                    replaceFragment(ENTRY_TAG);
                    break;
                case R.id.nav_filters:
                    replaceFragment(FILTERS_TAG);
                    break;
                default:
                    Toast.makeText(getApplicationContext(), "Not yet implemented", Toast.LENGTH_SHORT).show();
                    break;
            }
            drawer.closeDrawer(GravityCompat.START);
            return true;
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    /**
     * Replaces the displayed fragment with one associated to the given tag.
     * <p>
     * Checks to see if a Fragment associated to the tag already exists, then replaces the displayed
     * fragment with that one, if it exists, or a new Fragment of the appropriate type.
     *
     * @param tag a String associated to the type of Fragment that is to be displayed
     */
    public void replaceFragment(String tag) {
        //Check if an instance of the desired Fragment already exists somewhere on the backstack
        Fragment frag = getSupportFragmentManager().findFragmentByTag(tag);
        //If the fragment doesn't already exist, create a new one
        if (frag == null) {
            switch (tag) {
                case CANON_LIST_TAG:
                    frag = new MediaListFragment();
                    break;
                case SETTINGS_TAG:
                    frag = new SettingsFragment();
                    break;
                case ENTRY_TAG:
                    frag = new EntryFragment();
                    break;
                case FILTERS_TAG:
                    frag = new FilterSelectionFragment();
                    break;
                default:
                    Toast.makeText(getApplicationContext(), "Not yet implemented", Toast.LENGTH_SHORT).show();
                    return;
            }
        }
        //Call the function to actually replace the fragment
        replaceFragment(frag, tag);
    }

    /**
     * Performs the actual logic of replacing the active Fragment.
     *
     * @param newFrag the fragment to be displayed
     * @param tag     the tag to associate to newFrag
     */
    private void replaceFragment(Fragment newFrag, String tag) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, newFrag, tag)
                .addToBackStack(null)
                .commit();
    }


}

