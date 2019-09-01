package com.minirogue.starwarscanontracker;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.google.android.material.navigation.NavigationView;
import com.minirogue.starwarscanontracker.database.CSVImporter;


public class MainActivity extends AppCompatActivity {

    public static final String CANON_LIST_TAG = "canon_list";
    public static final String SETTINGS_TAG = "settings";
    public static final String ENTRY_TAG = "entry";
    private DrawerLayout drawer;

    @Override
    protected void onResume() {
        super.onResume();
        new CSVImporter(getApplication(), false).execute(CSVImporter.SOURCE_ONLINE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new EntryFragment()).commit();
            ImagePipelineConfig config = ImagePipelineConfig.newBuilder(getApplication())
                    .setDownsampleEnabled(true)
                    .build();
            Fresco.initialize(getApplication(), config);
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
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

    private static final String TAG = "MainActivity";

    public void replaceFragment(String tag) {
        Fragment frag = getSupportFragmentManager().findFragmentByTag(tag);
        if (frag == null){
            switch (tag){
                case CANON_LIST_TAG:
                    frag = new MediaListFragment();
                    break;
                case SETTINGS_TAG:
                    frag = new SettingsFragment();
                    break;
                case ENTRY_TAG:
                    frag = new EntryFragment();
                    break;
                default:
                    Toast.makeText(getApplicationContext(), "Not yet implemented", Toast.LENGTH_SHORT).show();
                    return;
            }
        }
        replaceFragment(frag, tag);
    }

    void replaceFragment(Fragment newFrag, String tag) {
        //mainViewModel.addToBackStack(newFrag);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, newFrag, tag)
                .addToBackStack(null)
                .commit();
    }

    /*@Override
    public void onBackPressed() {
        Fragment backFrag = mainViewModel.popBackStack();
        if (backFrag == null) {
            finish();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, backFrag)
                    .commit();
        }
    }*/

/*
    public void resetListFragment(){
        mainViewModel.resetListFragment();
    }
*/
}
