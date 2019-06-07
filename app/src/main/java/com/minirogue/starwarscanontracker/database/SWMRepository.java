package com.minirogue.starwarscanontracker.database;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.sqlite.db.SimpleSQLiteQuery;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import androidx.preference.PreferenceManager;
import android.util.Log;

import com.minirogue.starwarscanontracker.FilterObject;
import com.minirogue.starwarscanontracker.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SWMRepository {
    private final String TAG = "Repo";
    private DaoMedia daoMedia;
    private DaoType daoType;
    private LiveData<List<MediaAndNotes>> filteredMediaAndNotes;
    private MediatorLiveData<List<FilterObject>> filters;
    private MutableLiveData<StringBuilder> permanentFilters;
    private LiveData<List<FilterObject>> allFilters;
    private Application application;

    public SWMRepository(final Application application){
        this.application = application;
        MediaDatabase db = MediaDatabase.getMediaDataBase(application);
        daoMedia = db.getDaoMedia();
        daoType = db.getDaoType();
        allFilters = FilterObject.getAllFilters(application);
        filters = new MediatorLiveData<>();
        filters.setValue(new ArrayList<>());
        permanentFilters = new MutableLiveData<>();
        permanentFilters.setValue(new StringBuilder());
        MediatorLiveData<List<FilterObject>> filterTracker = new MediatorLiveData<>();
        filterTracker.addSource(filters, filterTracker::setValue);
        filterTracker.addSource(permanentFilters, input ->  filterTracker.setValue(filters.getValue()));
        filteredMediaAndNotes = Transformations.switchMap(filterTracker, input -> daoMedia.getMediaAndNotesRawQuery(convertFiltersToQuery(input)));
        new Thread(this::buildPermanentFilters).start();
    }
    public void removeFilter(FilterObject filter){
        try {
            List<FilterObject> tempList = filters.getValue();
            tempList.remove(filter);
            filters.removeSource(filter.getLiveFilter());
            filters.setValue(tempList);
        } catch (NullPointerException e){
            Log.e(TAG, "removeFilter",e);
        }
    }
    public void addFilter(FilterObject filter){
        try {
            List<FilterObject> tempList = filters.getValue();
            tempList.add(filter);
            filters.addSource(filter.getLiveFilter(), filterObject -> filters.postValue(filters.getValue()));
            filters.setValue(tempList);
        } catch (NullPointerException e){
            Log.e(TAG, "addFilter",e);
        }
    }

    public boolean isCurrentFilter(FilterObject filter){
        try {
            return filters.getValue().contains(filter);
        } catch (NullPointerException e){
            Log.e(TAG, "isCurrentFilter",e);
            return false;
        }
    }

    public String convertTypeToString(final int typeId){
        return FilterObject.getTextForType(typeId);
    }
    public LiveData<MediaItem> getLiveMediaItem(int itemId){
        return daoMedia.getMediaItemById(itemId);
    }
    public LiveData<MediaNotes> getLiveMediaNotes(int itemId){ return daoMedia.getMediaNotesById(itemId);}

    @SuppressWarnings("StatementWithEmptyBody")
    private SimpleSQLiteQuery convertFiltersToQuery(List<FilterObject> filterList){
        StringBuilder queryBuild = new StringBuilder();
        StringBuilder joins = new StringBuilder();
        StringBuilder characterFilter = new StringBuilder();
        StringBuilder typeFilter = new StringBuilder();
        StringBuilder notesFilter = new StringBuilder();
        for (FilterObject filter : filterList) {
            if (filter.column == FilterObject.FILTERCOLUMN_CHARACTER) {
                if (characterFilter.length() == 0) {
                    joins.append(" INNER JOIN media_character_join ON media_items.id = media_character_join.mediaId ");
                } else {
                    characterFilter.append(" AND ");
                }
                if (!filter.isPositive()){
                    characterFilter.append(" NOT ");
                }
                characterFilter.append(" media_character_join.characterID = ");
                characterFilter.append(filter.id);
            }
            else if (filter.column == FilterObject.FILTERCOLUMN_TYPE) {
                if (typeFilter.length() == 0) {
                    if (!filter.isPositive()){
                        typeFilter.append(" NOT ");
                    }
                } else {
                    if (!filter.isPositive()){
                        typeFilter.append(" AND NOT ");
                    } else {
                        typeFilter.append(" OR ");
                    }
                }
                typeFilter.append(" type = ");
                typeFilter.append(filter.id);
            }
            else if (filter.column == FilterObject.FILTERCOLUMN_OWNED){
                if (notesFilter.length() == 0){
                } else{
                    notesFilter.append(" AND ");
                }
                if (!filter.isPositive()){
                    notesFilter.append(" NOT ");
                }
                notesFilter.append(" media_notes.owned = 1 ");
            }
            else if (filter.column == FilterObject.FILTERCOLUMN_HASREADWATCHED){
                if (notesFilter.length() == 0){
                } else{
                    notesFilter.append(" AND ");
                }
                if (!filter.isPositive()){
                    notesFilter.append(" NOT ");
                }
                notesFilter.append(" media_notes.watched_or_read = 1 ");
            }
            else if (filter.column == FilterObject.FILTERCOLUMN_WANTTOREADWATCH){
                if (notesFilter.length() == 0){
                } else{
                    notesFilter.append(" AND ");
                }
                if (!filter.isPositive()){
                    notesFilter.append(" NOT ");
                }
                notesFilter.append(" media_notes.want_to_watch_or_read = 1 ");
            }
        }
        queryBuild.append("SELECT media_items.*,media_notes.* FROM media_items INNER JOIN media_notes ON media_items.id = media_notes.mediaId ");
        queryBuild.append(joins);
        boolean whereClause = false;
        if (characterFilter.length() > 0){
            //noinspection ConstantConditions
            queryBuild.append(whereClause ? " AND (" : " WHERE (");
            whereClause = true;
            queryBuild.append(characterFilter);
            queryBuild.append(")");
        }
        if (typeFilter.length() > 0){
            queryBuild.append(whereClause ? " AND (" : " WHERE (");
            whereClause = true;
            queryBuild.append(typeFilter);
            queryBuild.append(")");
        }
        if (notesFilter.length() > 0){
            queryBuild.append(whereClause ? " AND (" : " WHERE (");
            whereClause = true;
            queryBuild.append(notesFilter);
            queryBuild.append(")");
        }
        if (permanentFilters.getValue() != null && permanentFilters.getValue().length() > 0){
            queryBuild.append(whereClause ? " AND (" : " WHERE (");
            //noinspection UnusedAssignment
            whereClause = true;
            queryBuild.append(permanentFilters.getValue());
            queryBuild.append(")");
        }
        Log.d("ListAdapter", queryBuild.toString());
        return new SimpleSQLiteQuery(queryBuild.toString());
    }

    @SuppressWarnings("StatementWithEmptyBody")
    private void buildPermanentFilters(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(application);
        StringBuilder permFiltersBuilder = new StringBuilder();
        for (MediaType type : daoType.getAllNonLive()) {
            if (!prefs.getBoolean(type.getText(), true)) {
                if (permFiltersBuilder.length() == 0) {
                } else {
                    permFiltersBuilder.append(" AND ");
                }
                permFiltersBuilder.append(" NOT type = ");
                permFiltersBuilder.append(type.getId());
            }
        }
        permanentFilters.postValue(permFiltersBuilder);
    }


    public LiveData<List<MediaAndNotes>> getFilteredMediaAndNotes(){ return filteredMediaAndNotes; }
    public LiveData<List<FilterObject>> getFilters(){ return filters; }

    public LiveData<List<FilterObject>> getAllFilters(){
        return allFilters;
    }

    public Drawable getCoverImageFromURL(String url){
        if (url == null || url.equals("")){
            return application.getDrawable(R.mipmap.ic_launcher);
        }
        Bitmap bitmap = null;
        String filename = url.hashCode()+".PNG";
        Log.d(TAG,filename);
        try {
            Log.d(TAG, "loading image from file");
            FileInputStream fiStream = application.openFileInput(filename);
            bitmap = BitmapFactory.decodeStream(fiStream);
            fiStream.close();
        } catch (Exception e) {
            Log.d("GetCoverImage", "loading from file didn't work, trying from internet");
            try {
                Log.d(TAG, url);
                InputStream inputStream = new URL(url).openStream();   // Download Image from URL
                bitmap = BitmapFactory.decodeStream(inputStream);       // Decode Bitmap
                inputStream.close();
                FileOutputStream foStream = application.openFileOutput(filename, Context.MODE_PRIVATE);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, foStream);
                foStream.close();
                Log.d(TAG, String.valueOf(new File(filename).exists()));
            } catch (Exception e2) {
                Log.d("GetCoverImage", "Exception 1, Something went wrong!");
                e2.printStackTrace();
            }
        }
        if (bitmap == null){
            return application.getDrawable(R.mipmap.ic_launcher);
        }
        return new BitmapDrawable(application.getResources(), bitmap);
    }

    public void update(MediaNotes mediaNotes){
        new UpdateMediaNotes(daoMedia).execute(mediaNotes);
    }
    private static class UpdateMediaNotes extends AsyncTask<MediaNotes, Void, Void>{
        WeakReference<DaoMedia> wrDaoMedia;

        UpdateMediaNotes(DaoMedia daoMedia) {
            wrDaoMedia  = new WeakReference<>(daoMedia);
        }

        @Override
        protected Void doInBackground(MediaNotes... mediaNotes) {
            wrDaoMedia.get().update(mediaNotes[0]);
            return null;
        }
    }

}
