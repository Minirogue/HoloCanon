package com.minirogue.starwarsmediatracker.database;

import android.app.Application;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;
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
    //private LiveData<List<MediaItem>> allMedia;
    private LiveDataContainer<List<MediaAndNotes>> currentFilteredMediaAndNotes;
    private LiveData<List<MediaAndNotes>> filteredMediaAndNotes;
    private MutableLiveData<List<FilterObject>> filters;

    public SWMRepository(Application application){
        MediaDatabase db = MediaDatabase.getMediaDataBase(application);
        daoCharacter = db.getDaoCharacter();
        daoMedia = db.getDaoMedia();

        //allMedia = daoMedia.getAll();
        filteredMediaAndNotes = new MediatorLiveData<>();
        currentFilteredMediaAndNotes = new LiveDataContainer<>();
        filters = new MutableLiveData<>();
        filters.setValue(new ArrayList<FilterObject>());
        filteredMediaAndNotes = Transformations.switchMap(filters, new Function<List<FilterObject>, LiveData<List<MediaAndNotes>>>() {
            @Override
            public LiveData<List<MediaAndNotes>> apply(List<FilterObject> input) {
                return daoMedia.getMediaAndNotesRawQuery(convertFiltersToQuery(input));
            }
        });
    }

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
                characterFilter.append(" media_character_join.characterID = ");
                characterFilter.append(filter.id);
            }
            else if (filter.column == FilterObject.FILTERCOLUMN_TYPE) {
                if (typeFilter.length() == 0) {
                } else {
                    typeFilter.append(" OR ");
                }
                typeFilter.append(" type = ");
                typeFilter.append(filter.id);
            }
            else if (filter.column == FilterObject.FILTERCOLUMN_OWNED){
                if (notesFilter.length() == 0){
                } else{
                    notesFilter.append(" AND ");
                }
                notesFilter.append(" media_notes.owned = 1 ");
            }
            else if (filter.column == FilterObject.FILTERCOLUMN_HASREADWATCHED){
                if (notesFilter.length() == 0){
                } else{
                    notesFilter.append(" AND ");
                }
                notesFilter.append(" media_notes.watched_or_read = 1 ");
            }
            else if (filter.column == FilterObject.FILTERCOLUMN_WANTTOREADWATCH){
                if (notesFilter.length() == 0){
                } else{
                    notesFilter.append(" AND ");
                }
                notesFilter.append(" media_notes.want_to_watch_or_read = 1 ");
            }
        }
        queryBuild.append("SELECT media_items.*,media_notes.* FROM media_items INNER JOIN media_notes ON media_items.id = media_notes.mediaId ");
        queryBuild.append(joins);
        boolean whereClause = false;
        if (characterFilter.length() > 0){
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
        Log.d("ListAdapter", queryBuild.toString());
        return new SimpleSQLiteQuery(queryBuild.toString());
    }

    /*public LiveData<List<MediaItem>> getAllMedia(){
        return allMedia;
    }*/
    public LiveData<List<MediaAndNotes>> getFilteredMediaAndNotes(){ return filteredMediaAndNotes; }
    public MutableLiveData<List<FilterObject>> getFilters(){ return filters; }

    public void update(MediaNotes mediaNotes){
        new UpdateMediaNotes(daoMedia).execute(mediaNotes);
    }
    private static class UpdateMediaNotes extends AsyncTask<MediaNotes, Void, Void>{
        WeakReference<DaoMedia> wrDaoMedia;

        public UpdateMediaNotes(DaoMedia daoMedia) {
            wrDaoMedia  = new WeakReference<>(daoMedia);
        }

        @Override
        protected Void doInBackground(MediaNotes... mediaNotes) {
            wrDaoMedia.get().update(mediaNotes[0]);
            return null;
        }
    }

    private static class UpdateFilteredMedia extends AsyncTask<Void, Void, LiveData<List<MediaAndNotes>>>{
        WeakReference<MediatorLiveData<List<MediaAndNotes>>> wrFilteredMedia;
        WeakReference<LiveDataContainer<List<MediaAndNotes>>> wrCurrentFilteredMedia;
        WeakReference<List<FilterObject>> wrFilters;
        WeakReference<DaoMedia> wrDaoMedia;

        public UpdateFilteredMedia(MediatorLiveData<List<MediaAndNotes>> filteredMedia, LiveDataContainer<List<MediaAndNotes>> currentFilteredMedia, List<FilterObject> filters, DaoMedia daoMedia){
            wrFilteredMedia = new WeakReference<>(filteredMedia);
            wrCurrentFilteredMedia = new WeakReference<>(currentFilteredMedia);
            wrFilters = new WeakReference<>(filters);
            wrDaoMedia = new WeakReference<>(daoMedia);
        }

        @Override
        protected LiveData<List<MediaAndNotes>> doInBackground(Void... voids) {
            StringBuilder queryBuild = new StringBuilder();
            StringBuilder joins = new StringBuilder();
            StringBuilder characterFilter = new StringBuilder();
            StringBuilder typeFilter = new StringBuilder();
            for (FilterObject filter : wrFilters.get()) {
                if (filter.column == FilterObject.FILTERCOLUMN_CHARACTER) {
                    if (characterFilter.length() == 0) {
                        joins.append(" INNER JOIN media_character_join ON media_items.id = media_character_join.mediaId ");
                    } else {
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
            queryBuild.append("SELECT media_items.*,media_notes.* FROM media_items INNER JOIN media_notes ON media_items.id = media_notes.mediaId ");
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
            return wrDaoMedia.get().getMediaAndNotesRawQuery(new SimpleSQLiteQuery(queryBuild.toString()));
        }

        @Override
        protected void onPostExecute(LiveData<List<MediaAndNotes>> newLiveDataInstance) {
            wrCurrentFilteredMedia.get().setCurrentLiveData(newLiveDataInstance);
            final MediatorLiveData<List<MediaAndNotes>> mediator = wrFilteredMedia.get();
            mediator.addSource(newLiveDataInstance, new Observer<List<MediaAndNotes>>() {
                @Override
                public void onChanged(@Nullable List<MediaAndNotes> mediaAndNotes) {
                    mediator.setValue(mediaAndNotes);
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
