package com.minirogue.starwarsmediatracker;

/* TODO Sorting
*  TODO Update database
*  TODO revise media_list_item.xml
*  TODO properly implement "view media" pages
*  TODO add menus
 */

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import com.minirogue.starwarsmediatracker.database.CSVImporter;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if(prefs.getInt("lastLocalCSV", 0) != BuildConfig.VERSION_CODE) {
            CSVImporter importer = new CSVImporter(this);
            importer.execute(CSVImporter.SOURCE_RAW_RESOURCES);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("lastLocalCSV", BuildConfig.VERSION_CODE);
            editor.apply();
        }
    }

    public void openMediaByType(View view) {
        Intent intent = new Intent(this, ListMediaActivity.class);
        startActivity(intent);
    }

    public void updateDatabaseFromOnline(View view){
        CSVImporter importer = new CSVImporter(this);
        importer.execute(CSVImporter.SOURCE_ONLINE);
    }
}
