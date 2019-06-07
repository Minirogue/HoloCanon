package com.minirogue.starwarscanontracker;

import android.app.Application;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.annotation.NonNull;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.minirogue.starwarscanontracker.database.MediaAndNotes;
import com.minirogue.starwarscanontracker.database.MediaNotes;
import com.minirogue.starwarscanontracker.database.SWMRepository;

import java.util.Collections;
import java.util.List;

class MediaListViewModel extends AndroidViewModel {
    private static final String TAG = "MediaListViewModel";

    private SWMRepository repository;
    private LiveData<List<FilterObject>> filters;
    private LiveData<List<FilterObject>> allFilters;
    private LiveData<List<MediaAndNotes>> data;
    private MediatorLiveData<List<MediaAndNotes>> sortedData = new MediatorLiveData<>();
    private String[] checkboxText = new String[4];
    private MutableLiveData<SortStyle> sortStyle = new MutableLiveData<>();

    public MediaListViewModel(@NonNull Application application) {
        super(application);
        repository = new SWMRepository(application);
        filters = repository.getFilters();
        allFilters = repository.getAllFilters();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(application);
        checkboxText[1] = prefs.getString(application.getString(R.string.watched_read),application.getString(R.string.watched_read));
        checkboxText[2] = prefs.getString(application.getString(R.string.want_to_watch_read),application.getString(R.string.want_to_watch_read));
        checkboxText[3] = prefs.getString(application.getString(R.string.owned),application.getString(R.string.owned));
        data = repository.getFilteredMediaAndNotes();
        setSort(SortStyle.SORT_TITLE);
        sortedData.addSource(data, dat -> sort());
        sortedData.addSource(sortStyle, comp -> sort());
    }

    LiveData<List<FilterObject>> getAllFilters(){
        return allFilters;
    }

    void setSort(int newCompareType){
        sortStyle.postValue(new SortStyle(newCompareType, true));
    }
    void reverseSort(){
        sortStyle.postValue(sortStyle.getValue().reversed());
    }

    private void sort(){
        //Log.d(TAG, "Sort called");
        List<MediaAndNotes> toBeSorted = data.getValue();
        if(toBeSorted != null) {
            Collections.sort(toBeSorted, sortStyle.getValue());
            sortedData.postValue(toBeSorted);
        }
    }
    LiveData<SortStyle> getSortStyle(){
        return sortStyle;
    }

    void removeFilter(FilterObject filter){
        repository.removeFilter(filter);
    }
    void addFilter(FilterObject filter){
        repository.addFilter(filter);
    }

    boolean isCurrentFilter(FilterObject filter){
        return repository.isCurrentFilter(filter);
    }

    LiveData<List<FilterObject>> getFilters() {
        return filters;
    }

    LiveData<List<MediaAndNotes>> getFilteredMediaAndNotes() {
        return sortedData;
    }
    public void update(MediaNotes mediaNotes){
        repository.update(mediaNotes);
    }
    String convertTypeToString(int typeId){
        return repository.convertTypeToString(typeId);
    }
    Drawable getCoverImageFromURL(String url){
        return repository.getCoverImageFromURL(url);
    }
    String getCheckboxText(int boxNumber){
        return checkboxText[boxNumber];
    }
}
