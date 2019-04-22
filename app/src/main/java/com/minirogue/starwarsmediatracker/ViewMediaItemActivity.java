package com.minirogue.starwarsmediatracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.minirogue.starwarsmediatracker.database.MediaDatabase;
import com.minirogue.starwarsmediatracker.database.MediaItem;

public class ViewMediaItemActivity extends AppCompatActivity {

    private MediaItem thisMediaItem;

    private TextView titleTextView;
    private TextView authorTextView;
    private TextView typeTextView;
    private TextView descriptionTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_media_item);

        Intent intent = getIntent();
        int mediaID = intent.getIntExtra(getString(R.string.intentMediaID),-1);
        thisMediaItem = MediaDatabase.getMediaDataBase(this).getDaoMedia().getMediaItemById(mediaID);
        titleTextView.setText(thisMediaItem.getTitle());
        typeTextView.setText(MediaItem.convertTypeToString(thisMediaItem.getType()));
        descriptionTextView.setText(thisMediaItem.getDescription());
    }
}
