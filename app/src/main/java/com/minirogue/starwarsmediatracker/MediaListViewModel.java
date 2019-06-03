package com.minirogue.starwarsmediatracker;

import android.app.Application;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ImageView;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.annotation.NonNull;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.minirogue.starwarsmediatracker.database.MediaAndNotes;
import com.minirogue.starwarsmediatracker.database.MediaNotes;
import com.minirogue.starwarsmediatracker.database.SWMRepository;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

class MediaListViewModel extends AndroidViewModel {
    private static final String TAG = "MediaListViewModel";

    private SWMRepository repository;
    private LiveData<List<FilterObject>> filters;
    private LiveData<List<FilterObject>> allFilters;
    private LiveData<List<MediaAndNotes>> data;
    private MediatorLiveData<List<MediaAndNotes>> sortedData = new MediatorLiveData<>();
    private String[] checkboxText = new String[4];
    private MutableLiveData<Comparator<MediaAndNotes>> comparator = new MutableLiveData<>();
    private int compareType;

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
        comparator.setValue((mediaAndNotes, t1) -> mediaAndNotes.mediaItem.getTitle().compareTo(t1.mediaItem.getTitle()));
        compareType = 0;
        sortedData.addSource(data, dat -> sort());
        sortedData.addSource(comparator, comp -> sort());
    }

    LiveData<List<FilterObject>> getAllFilters(){
        return allFilters;
    }
    void toggleSort(){
        switch(compareType){
            case 0:
                comparator.postValue((t1, t2) -> t2.mediaItem.getTitle().compareTo(t1.mediaItem.getTitle()));
                compareType += 1;
                break;
            case 1:
                comparator.postValue((t1, t2) -> FilterObject.getTextForType(t1.mediaItem.getType()).compareTo(FilterObject.getTextForType(t2.mediaItem.getType())));
                compareType += 1;
                break;
            case 2:
                comparator.postValue((t1, t2) -> FilterObject.getTextForType(t2.mediaItem.getType()).compareTo(FilterObject.getTextForType(t1.mediaItem.getType())));
                compareType += 1;
                break;
            default:
                comparator.postValue((t1, t2) -> t1.mediaItem.getTitle().compareTo(t2.mediaItem.getTitle()));
                compareType = 0;
                break;
        }
    }

    private void sort(){
        Log.d(TAG, "Sort called");
        List<MediaAndNotes> toBeSorted = data.getValue();
        if(toBeSorted != null) {
            Collections.sort(toBeSorted, comparator.getValue());
            sortedData.postValue(toBeSorted);
        }
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
