package com.minirogue.starwarsmediatracker;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.minirogue.starwarsmediatracker.database.MediaAndNotes;
import com.minirogue.starwarsmediatracker.database.MediaItem;
import com.minirogue.starwarsmediatracker.database.MediaNotes;
import com.minirogue.starwarsmediatracker.database.SWMRepository;

import java.util.ArrayList;
import java.util.List;

public class ListMediaViewModel extends AndroidViewModel {
    private SWMRepository repository;
    private MutableLiveData<List<FilterObject>> filters;
    private List<FilterObject> allFilters;

    public ListMediaViewModel(@NonNull Application application) {
        super(application);
        repository = new SWMRepository(application);
        filters = repository.getFilters();
        allFilters = FilterObject.getAllFilters();
    }

    public List<FilterObject> getAllFilters(){
        return allFilters;
    }

    public void removeFilter(FilterObject filter){
        List<FilterObject> tempList = filters.getValue();
        tempList.remove(filter);
        filters.setValue(tempList);
    }

    public void setFilters(List<FilterObject> newFilters){
        filters.setValue(newFilters);
    }

    public LiveData<List<FilterObject>> getFilters() {
        return filters;
    }

    public LiveData<List<MediaAndNotes>> getFilteredMediaAndNotes() {
        return repository.getFilteredMediaAndNotes();
    }
    public void update(MediaNotes mediaNotes){
        repository.update(mediaNotes);
    }

}
