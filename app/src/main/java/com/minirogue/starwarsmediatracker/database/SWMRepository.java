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
import android.util.Log;
import android.util.SparseArray;

import com.minirogue.starwarsmediatracker.FilterObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class SWMRepository {
    private final String TAG = "Repo";
//    private DaoCharacter daoCharacter;
    private DaoMedia daoMedia;
//    private LiveDataContainer<List<MediaAndNotes>> currentFilteredMediaAndNotes;
    private LiveData<List<MediaAndNotes>> filteredMediaAndNotes;
    private MediatorLiveData<List<FilterObject>> filters;
    private LiveData<List<MediaType>> allMediaTypes;
    private LiveData<SparseArray<String>> typeConverter;

    public SWMRepository(Application application){
        MediaDatabase db = MediaDatabase.getMediaDataBase(application);
//        daoCharacter = db.getDaoCharacter();
        daoMedia = db.getDaoMedia();
        allMediaTypes = db.getDaoType().getAllMediaTypes();
        Log.d(TAG, "AllMediaTypes set");
        /*typeConverter = Transformations.map(allMediaTypes, new Function<List<MediaType>, SparseArray<String>>() {
                    @Override
                    public SparseArray<String> apply(List<MediaType> input) {
                        SparseArray<String> converter = new SparseArray<>();
                        for (MediaType mediaType : input){
                            converter.append(mediaType.getId(), mediaType.getText());
                        }
                        Log.d(TAG, "TypeConverter set");
                        return converter;
                    }
                });*/
        /*typeConverter = new SparseArray<>();
        for (MediaType mediaType : allMediaTypes){
            typeConverter.append(mediaType.getId(), mediaType.getText());
        }*/
        //allMedia = daoMedia.getAll();
        filteredMediaAndNotes = new MediatorLiveData<>();
//        currentFilteredMediaAndNotes = new LiveDataContainer<>();
        filters = new MediatorLiveData<>();
        filters.setValue(new ArrayList<FilterObject>());
        Log.d(TAG, "Filters set");
        filteredMediaAndNotes = Transformations.switchMap(filters, new Function<List<FilterObject>, LiveData<List<MediaAndNotes>>>() {
            @Override
            public LiveData<List<MediaAndNotes>> apply(List<FilterObject> input) {
                return daoMedia.getMediaAndNotesRawQuery(convertFiltersToQuery(input));
            }
        });
    }
    public void removeFilter(FilterObject filter){
        List<FilterObject> tempList = filters.getValue();
        tempList.remove(filter);
        filters.removeSource(filter.getLiveFilter());
        filters.setValue(tempList);
    }
    public void addFilter(FilterObject filter){
        List<FilterObject> tempList = filters.getValue();
        tempList.add(filter);
        filters.addSource(filter.getLiveFilter(), new Observer<FilterObject>() {
            @Override
            public void onChanged(FilterObject filterObject) {
                filters.postValue(filters.getValue());
            }
        });
        filters.setValue(tempList);
    }

    public boolean isCurrentFilter(FilterObject filter){
        return filters.getValue().contains(filter);
    }

    public String convertTypeToString(final int typeId){
        List<MediaType> typeList = allMediaTypes.getValue();
        if (typeList != null)
            for (MediaType mediatype: typeList) {
                if (mediatype.getId() == typeId) {
                    return mediatype.getText();
                }
            }
        return "MediaTypeNotfound";
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
    public LiveData<List<FilterObject>> getFilters(){ return filters; }

    public LiveData<List<FilterObject>> getAllFilters(){
        return FilterObject.getAllFilters(allMediaTypes);
    }

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

}
