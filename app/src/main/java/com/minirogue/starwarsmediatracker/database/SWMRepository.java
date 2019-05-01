package com.minirogue.starwarsmediatracker.database;

import android.app.Application;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.sqlite.db.SimpleSQLiteQuery;
import android.os.AsyncTask;
import androidx.annotation.Nullable;
import android.util.Log;

import com.minirogue.starwarsmediatracker.FilterObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class SWMRepository {
    private DaoCharacter daoCharacter;
    private DaoMedia daoMedia;
    private LiveData<List<MediaItem>> allMedia;
    private LiveDataContainer<List<MediaItem>> currentFilteredMedia;
    private MediatorLiveData<List<MediaItem>> filteredMedia;
    private List<FilterObject> filters;

    public SWMRepository(Application application){
        MediaDatabase db = MediaDatabase.getMediaDataBase(application);
        daoCharacter = db.getDaoCharacter();
        daoMedia = db.getDaoMedia();

        allMedia = daoMedia.getAll();
        filteredMedia = new MediatorLiveData<>();
        currentFilteredMedia = new LiveDataContainer<>();
        setFilters(new ArrayList<FilterObject>());
    }

    public LiveData<List<MediaItem>> getAllMedia(){
        return allMedia;
    }
    public LiveData<List<MediaItem>> getFilteredMedia(){ return filteredMedia; }
    public void setFilters(List<FilterObject> filters){
        this.filters = filters;
        filteredMedia.removeSource(currentFilteredMedia.getCurrentLiveData());
        new UpdateFilteredMedia(filteredMedia, currentFilteredMedia, this.filters, daoMedia).execute();
    }

    private static class UpdateFilteredMedia extends AsyncTask<Void, Void, LiveData<List<MediaItem>>>{
        WeakReference<MediatorLiveData<List<MediaItem>>> wrFilteredMedia;
        WeakReference<LiveDataContainer<List<MediaItem>>> wrCurrentFilteredMedia;
        WeakReference<List<FilterObject>> wrFilters;
        WeakReference<DaoMedia> wrDaoMedia;

        public UpdateFilteredMedia(MediatorLiveData<List<MediaItem>> filteredMedia, LiveDataContainer<List<MediaItem>> currentFilteredMedia, List<FilterObject> filters, DaoMedia daoMedia){
            wrFilteredMedia = new WeakReference<>(filteredMedia);
            wrCurrentFilteredMedia = new WeakReference<>(currentFilteredMedia);
            wrFilters = new WeakReference<>(filters);
            wrDaoMedia = new WeakReference<>(daoMedia);
        }

        @Override
        protected LiveData<List<MediaItem>> doInBackground(Void... voids) {
            StringBuilder queryBuild = new StringBuilder();
            StringBuilder joins = new StringBuilder();
            StringBuilder characterFilter = new StringBuilder();
            StringBuilder typeFilter = new StringBuilder();
            for (FilterObject filter : wrFilters.get()) {
                if (filter.column == FilterObject.FILTERCOLUMN_CHARACTER) {
                    if (characterFilter.length() == 0) {
                        joins.append(" INNER JOIN media_character_join ON media_items.id = media_character_join.mediaId ");
                    } else {//TODO implement filtertype here
                        characterFilter.append(" AND ");
                    }
                    characterFilter.append(" media_character_join.characterID = ");
                    characterFilter.append(filter.id);
                }
                else if (filter.column == FilterObject.FILTERCOLUMN_TYPE) {
                    if (typeFilter.length() == 0) {
                    } else {
                        typeFilter.append(" AND ");
                    }
                    typeFilter.append(" type = ");
                    typeFilter.append(filter.id);
                }
            }
            queryBuild.append("SELECT media_items.* FROM media_items ");
            queryBuild.append(joins);
            boolean whereClause = false;
            if (characterFilter.length() > 0){
                queryBuild.append(whereClause ? " AND " : " WHERE ");
                whereClause = true;
                queryBuild.append(characterFilter);
            }
            if (typeFilter.length() > 0){
                queryBuild.append(whereClause ? " AND " : " WHERE ");
                whereClause = true;
                queryBuild.append(typeFilter);
            }
            Log.d("ListAdapter", queryBuild.toString());
            return wrDaoMedia.get().getMediaFromRawQuery(new SimpleSQLiteQuery(queryBuild.toString()));
        }

        @Override
        protected void onPostExecute(LiveData<List<MediaItem>> newLiveDataInstance) {
            wrCurrentFilteredMedia.get().setCurrentLiveData(newLiveDataInstance);
            final MediatorLiveData<List<MediaItem>> mediator = wrFilteredMedia.get();
            mediator.addSource(newLiveDataInstance, new Observer<List<MediaItem>>() {
                @Override
                public void onChanged(@Nullable List<MediaItem> mediaItems) {
                    mediator.setValue(mediaItems);
                }
            });
        }
    }

    private class LiveDataContainer<T> {
        private LiveData<T> currentLiveData;

        public LiveData<T> getCurrentLiveData() {
            return currentLiveData;
        }

        public void setCurrentLiveData(LiveData<T> currentLiveData) {
            this.currentLiveData = currentLiveData;
        }
    }
}
