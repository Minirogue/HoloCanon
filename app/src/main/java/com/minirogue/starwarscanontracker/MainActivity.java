package com.minirogue.starwarscanontracker;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.minirogue.starwarscanontracker.database.CSVImporter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new EntryFragment()).commit();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()){
                case R.id.nav_media:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new MediaListFragment()).commit();
                    break;
                case R.id.nav_settings:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new SettingsFragment()).commit();
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
        new CheckForUpdatedDatabase(getApplication()).execute();
    }

    private class CheckForUpdatedDatabase extends AsyncTask<Void, Void, Void> {

        WeakReference<Application> appRef;

        CheckForUpdatedDatabase(Application application){
            appRef = new WeakReference<>(application);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                URL url = new URL("https://docs.google.com/spreadsheets/d/e/2PACX-1vRvJaZHf3HHC_-XhWM4zftX9G_vnePy2-qxQ-NlmBs8a_tdBSSBjuerie6AMWQWp4H6R__BK9Q_li2g/pub?gid=1842257512&single=true&output=csv");
                InputStream inputStream = url.openStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                long newVersionId = Long.valueOf(reader.readLine().split(",")[0]);
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(appRef.get());
                if (prefs.getLong(getString(R.string.current_database_version), 0) == newVersionId) {
                    cancel(true);
                }
            } catch (MalformedURLException ex) {
                //Log.e("MainActivity", ex.toString());
            } catch (IOException ex) {
                //Log.e("MainActivity", ex.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            CSVImporter importer = new CSVImporter(appRef.get());
            importer.execute(CSVImporter.SOURCE_ONLINE);
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
}
