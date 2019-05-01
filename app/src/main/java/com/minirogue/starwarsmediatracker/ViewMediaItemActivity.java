package com.minirogue.starwarsmediatracker;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.minirogue.starwarsmediatracker.database.MediaDatabase;
import com.minirogue.starwarsmediatracker.database.MediaItem;

import java.lang.ref.WeakReference;

public class ViewMediaItemActivity extends AppCompatActivity {

    private MediaItem thisMediaItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        int mediaID = intent.getIntExtra(getString(R.string.intentMediaID),-1);
        Log.d("ViewMediaItem", String.valueOf(mediaID));
        new loadMedia(this).execute(mediaID);
    }

    private void inflateMovieView(){
        setContentView(R.layout.activity_view_movie);
        ((TextView)findViewById(R.id.title_textview)).setText(thisMediaItem.getTitle());
        ((TextView)findViewById(R.id.description_textview)).setText(thisMediaItem.getDescription());
        ((TextView)findViewById(R.id.type_textview)).setText("Movie");
    }

    private void inflateBookView(){
        setContentView(R.layout.activity_view_book);
        ((TextView)findViewById(R.id.title_textview)).setText(thisMediaItem.getTitle());
        ((TextView)findViewById(R.id.author_textview)).setText(thisMediaItem.getAuthor());
        ((TextView)findViewById(R.id.description_textview)).setText(thisMediaItem.getDescription());
        ((TextView)findViewById(R.id.type_textview)).setText("Book");
    }

    private class loadMedia extends AsyncTask<Integer,Void,Void>{//TODO inflate a view depending on media type

        WeakReference<Context> ctxRef;

        private loadMedia(Context ctx){
            ctxRef = new WeakReference<>(ctx);
        }

        @Override
        protected Void doInBackground(Integer... ids) {
            thisMediaItem = MediaDatabase.getMediaDataBase(ctxRef.get()).getDaoMedia().getMediaItemById(ids[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void voids) {
            switch (thisMediaItem.getType()){
                case MediaItem.MEDIATYPE_MOVIE:
                    inflateMovieView();
                    break;
                case MediaItem.MEDIATYPE_BOOK:
                    inflateBookView();
                    break;
            }
        }
    }
}
