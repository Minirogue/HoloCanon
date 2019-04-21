package com.minirogue.starwarsmediatracker;

/* TODO OUTLINE
 * MediaItem is the object type for each item.
 * There should be an activity to view all items and sort them
 * -Within that activity, a long press on an item should give users an option to add their own tags
 *  such as "reading", "want to read", "own", etc.
 *  There needs to be a set of stored user tags. Media items' ids will be stored in an arraylist for these tags
 *  Filtering methods: Characters, author, media type (book, film, etc.), user tags, age range
 *  Sorting methods: Title, Author
 *  Add option in settings to permanently filter things (namely media type)
 */

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import com.minirogue.starwarsmediatracker.database.CSVImporter;

public class MainActivity extends AppCompatActivity {
    //TODO check online repo for current database version and download
    private final static String DB_NAME = "mediadatabase";
    private final static String DB_PATH = "data/starwarsmediatracker/databases/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CSVImporter.importCSVToDatabase(this);
    }

    public void openMediaByType(View view) {
        Intent intent = new Intent(this, ListMediaActivity.class);
        startActivity(intent);
    }
}
