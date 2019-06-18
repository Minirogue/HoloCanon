package com.minirogue.starwarscanontracker;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.google.android.material.navigation.NavigationView;
import com.minirogue.starwarscanontracker.database.CSVImporter;


public class MainActivity extends AppCompatActivity {

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
            switch (menuItem.getItemId()){
                case R.id.nav_media:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new MediaListFragment())
                            .addToBackStack(null)
                            .commit();
                    break;
                case R.id.nav_settings:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new SettingsFragment())
                            .addToBackStack(null)
                            .commit();
                    break;
                case R.id.nav_about:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new EntryFragment())
                            .addToBackStack(null)
                            .commit();
                    break;
                default:
                    Toast.makeText(getApplicationContext(),"Not yet implemented", Toast.LENGTH_SHORT).show();
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
}
