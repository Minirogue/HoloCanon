package com.minirogue.starwarsmediatracker;

/* TODO Sorting
*  TODO Update database
*  TODO revise media_list_item.xml
*  TODO properly implement "view media" pages
*  TODO add menus
*  TODO add options to permanently filter out certain types of media (junior, etc.)
*  TODO add options to view collected comics vs individual comics
*  TODO add options for filtering out redundant material (audiobooks, comic adaptations (e.g. Thrawn), novelizations)
 */

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.minirogue.starwarsmediatracker.database.CSVImporter;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.nav_media:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                new MediaListFragment()).commit();
                        break;
                    default:
                        Toast.makeText(getApplicationContext(),"Not yet implemented", Toast.LENGTH_SHORT).show();
                        break;
                }
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if(prefs.getInt("lastLocalCSV", 0) != BuildConfig.VERSION_CODE) {
            CSVImporter importer = new CSVImporter(this);
            importer.execute(CSVImporter.SOURCE_RAW_RESOURCES);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("lastLocalCSV", BuildConfig.VERSION_CODE);
            editor.apply();
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }
    }


    public void updateDatabaseFromOnline(View view){
        CSVImporter importer = new CSVImporter(this);
        importer.execute(CSVImporter.SOURCE_ONLINE);
    }
}
