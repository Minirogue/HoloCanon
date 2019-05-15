package com.minirogue.starwarsmediatracker;

import android.content.Context;


import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;

import com.minirogue.starwarsmediatracker.database.MediaType;

import java.util.ArrayList;
import java.util.List;

/**
 * POJO to define a filter for the media list
 */
public class FilterObject {
    public static final int FILTERTYPE_AND = 1;
    public static final int FILTERTYPE_OR = 2;
    public static final int FILTERTYPE_NOT = 3;

    public static final int FILTERCOLUMN_TYPE = 1;
    public static final int FILTERCOLUMN_CHARACTER = 2;
    public static final int FILTERCOLUMN_OWNED = 3;
    public static final int FILTERCOLUMN_WANTTOREADWATCH = 4;
    public static final int FILTERCOLUMN_HASREADWATCHED = 5;

    public int id;
    public int column;
    public int filterType;
    public String displayText;
    private static MediatorLiveData<List<FilterObject>> allFilters;
    private static LiveData<List<MediaType>> allMediaTypes;

    private FilterObject(int id, int column, int filterType, String displayText) {
        this.id = id;
        this.column = column;
        this.filterType = filterType;
        this.displayText = displayText;
    }

    public static LiveData<List<FilterObject>> getAllFilters(LiveData<List<MediaType>> mediaTypes) {
        if (allFilters == null){
            allMediaTypes = mediaTypes;
            allFilters = new MediatorLiveData<>();
            allFilters.addSource(allMediaTypes, new Observer<List<MediaType>>() {
                @Override
                public void onChanged(List<MediaType> mediaTypes) {
                    allFilters.setValue(createAllFilters());
                }
            });
/*
            allFilters = new MutableLiveData<List<FilterObject>>();
            allFilters.setValue(createAllFilters(mediaTypes));
*/
        }
        return allFilters;
    }
    public static LiveData<List<FilterObject>> getAllFilters(List<MediaType> mediaTypes) {
        if (allFilters == null){
            allMediaTypes = new MutableLiveData<>();
            ((MutableLiveData<List<MediaType>>)allMediaTypes).setValue(mediaTypes);
            allFilters = new MediatorLiveData<>();
            allFilters.addSource(allMediaTypes, new Observer<List<MediaType>>() {
                @Override
                public void onChanged(List<MediaType> mediaTypes) {
                    allFilters.setValue(createAllFilters());
                }
            });
/*
            allFilters = new MutableLiveData<List<FilterObject>>();
            allFilters.setValue(createAllFilters(mediaTypes));
*/
        }
        return allFilters;
    }

    private static List<FilterObject> createAllFilters(){
        List<FilterObject> newAllFilters = new ArrayList<>();
        if (allMediaTypes.getValue() != null) {
            for (MediaType mediaType : allMediaTypes.getValue()) {
                newAllFilters.add(new FilterObject(mediaType.getId(), FILTERCOLUMN_TYPE, 0, mediaType.getText()));
            }
        }
        //allFilters.add(new FilterObject(2, FILTERCOLUMN_CHARACTER,0,"Luke Skywalker"));
        //allFilters.add(new FilterObject(3, FILTERCOLUMN_CHARACTER, 0, "Anakin Skywalker"));
        newAllFilters.add(new FilterObject(0, FILTERCOLUMN_OWNED, 0, "Owned"));
        newAllFilters.add(new FilterObject(0, FILTERCOLUMN_HASREADWATCHED, 0, "Read/Watched"));
        newAllFilters.add(new FilterObject(0, FILTERCOLUMN_WANTTOREADWATCH, 0,"Want to Watch/Read"));
        return newAllFilters;
    }
}
