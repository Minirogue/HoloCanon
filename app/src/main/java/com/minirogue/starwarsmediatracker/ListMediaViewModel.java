package com.minirogue.starwarsmediatracker;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.annotation.NonNull;

import com.minirogue.starwarsmediatracker.database.MediaItem;
import com.minirogue.starwarsmediatracker.database.SWMRepository;

import java.util.ArrayList;
import java.util.List;

public class ListMediaViewModel extends AndroidViewModel {
    private SWMRepository repository;
    private List<FilterObject> filters;
    private List<FilterObject> allFilters;

    public ListMediaViewModel(@NonNull Application application) {
        super(application);
        repository = new SWMRepository(application);
        filters = new ArrayList<>();
        allFilters = new ArrayList<>();
        allFilters.add(new FilterObject(MediaItem.MEDIATYPE_MOVIE,FilterObject.FILTERCOLUMN_TYPE,0,"Movies"));
        allFilters.add(new FilterObject(MediaItem.MEDIATYPE_BOOK,FilterObject.FILTERCOLUMN_TYPE,0,"Books"));
        allFilters.add(new FilterObject(2,FilterObject.FILTERCOLUMN_CHARACTER,0,"Luke Skywalker"));
        allFilters.add(new FilterObject(3,FilterObject.FILTERCOLUMN_CHARACTER, 0, "Anakin Skywalker"));
    }

    public List<FilterObject> getAllFilters(){
        return allFilters;
    }

    public void removeFilter(FilterObject filter){
        filters.remove(filter);
        repository.setFilters(filters);
    }

    public void setFilters(List<FilterObject> filters){
        this.filters = filters;
        repository.setFilters(filters);
    }

    public List<FilterObject> getFilters() {
        return filters;
    }

    public LiveData<List<MediaItem>> getFilteredMedia() {
        repository.setFilters(filters);
        return repository.getFilteredMedia();
    }

}
