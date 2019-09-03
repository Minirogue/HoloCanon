package com.minirogue.starwarscanontracker;


import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

/**
 * defines a filter for the media list
 */
public class FilterObject {

    //public static final String TAG = "FilterObject";

    public static final int FILTERCOLUMN_TYPE = 1;
    public static final int FILTERCOLUMN_CHARACTER = 2;
    public static final int FILTERCOLUMN_OWNED = 3;
    public static final int FILTERCOLUMN_WANTTOREADWATCH = 4;
    public static final int FILTERCOLUMN_HASREADWATCHED = 5;

    public int id;
    public int column;
    private boolean positive;
    public String displayText;
    private MutableLiveData<FilterObject> liveFilter;
    private static MediatorLiveData<List<FilterObject>> allFilters = new MediatorLiveData<>();
    private static MutableLiveData<List<FilterObject>> notesFilters = new MutableLiveData<>();
    private static MutableLiveData<List<FilterObject>> typeFilters = new MutableLiveData<>();

    public FilterObject(int id, int column, String displayText) {
        this.id = id;
        this.column = column;
        this.positive = true;
        this.displayText = displayText;
        this.liveFilter = new MutableLiveData<>();
        this.liveFilter.postValue(this);
    }

    /**
     * Check if this FilterObject is equal to another object.
     *
     * Returns True if they have the same id, column, and positive values,
     * but liveFilter and displayText are ignored
     *
     * @param obj the object being compared to this one
     */
    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        } else {
            FilterObject otherObj = (FilterObject)obj;
            return otherObj.id == this.id && otherObj.column == this.column;
        }
    }

    /*public static LiveData<List<FilterObject>> getAllFilters(Application application) {
        new Thread(() -> updateFilterList(application)).start();
        return allFilters;
    }

    private static void updateFilterList(Application application) {
        new Thread(() -> {
            if (allFilters.getValue() == null) {
                makeNotesFilters(application);
                makeTypeFilters(application);
                allFilters.addSource(typeFilters, value -> combineAllFilters());
                allFilters.addSource(notesFilters, value -> combineAllFilters());
            } else {
                makeTypeFilters(application);
                makeNotesFilters(application);
            }
        }).start();
    }

     */

    public static String getTextForType(int i) {//TODO want to have this automatically generated from database
        switch (i) {
            case 1:
                return "Movie";
            case 2:
                return "Novelization";
            case 3:
                return "Original Novel";
            case 4:
                return "Video Game";
            case 5:
                return "Youth";
            case 6:
                return "Audiobook";
            case 7:
                return "Comic Book";
            case 8:
                return "TPB";
            case 9:
                return "TV Season";
            case 10:
                return "TV Episode";
            case 11:
                return "Comic Adaptation";
            case 12:
                return "TPB Adaptation";
            default:
                return "MediaTypeNotFound";
        }
    }

    /*private static void makeNotesFilters(Application application) {
        List<FilterObject> newNotesFilters = new ArrayList<>();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(application);
        newNotesFilters.add(new FilterObject(0, FILTERCOLUMN_OWNED, prefs.getString(application.getString(R.string.owned), application.getString(R.string.owned))));
        newNotesFilters.add(new FilterObject(0, FILTERCOLUMN_HASREADWATCHED, prefs.getString(application.getString(R.string.watched_read), application.getString(R.string.watched_read))));
        newNotesFilters.add(new FilterObject(0, FILTERCOLUMN_WANTTOREADWATCH, prefs.getString(application.getString(R.string.want_to_watch_read), application.getString(R.string.want_to_watch_read))));
        notesFilters.postValue(newNotesFilters);
    }

    private static void combineAllFilters() {
        List<FilterObject> newAllFilters = new ArrayList<>();
        List<FilterObject> someFilters = notesFilters.getValue();
        if (someFilters != null) {
            newAllFilters.addAll(someFilters);
        }
        List<FilterObject> someOtherFilters = typeFilters.getValue();
        if (someOtherFilters != null) {
            newAllFilters.addAll(someOtherFilters);
        }
        allFilters.postValue(newAllFilters);
    }

    private static void makeTypeFilters(Application application) {
        List<FilterObject> newTypeFilters = new ArrayList<>();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(application);
        List<MediaType> allMediaTypes = MediaDatabase.getMediaDataBase(application).getDaoType().getAllNonLive();
        for (MediaType mediaType : allMediaTypes) {
            if (prefs.getBoolean(mediaType.getText(), true)) {
                newTypeFilters.add(new FilterObject(mediaType.getId(), FILTERCOLUMN_TYPE, mediaType.getText()));
            }
        }
        typeFilters.postValue(newTypeFilters);
    }*/

    public boolean isPositive() {
        return positive;
    }

    public void setPositive(boolean positive) {
        this.positive = positive;
        this.liveFilter.postValue(this);
    }

    LiveData<FilterObject> getLiveFilter() {
        return liveFilter;
    }
}
