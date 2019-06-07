package com.minirogue.starwarscanontracker;


import android.app.Application;
import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.preference.PreferenceManager;

import com.minirogue.starwarscanontracker.database.MediaDatabase;
import com.minirogue.starwarscanontracker.database.MediaType;

import java.util.ArrayList;
import java.util.List;

/**
 * POJO to define a filter for the media list
 */
public class FilterObject {
    // --Commented out by Inspection (6/6/19 8:33 PM// --Commented out by Inspection (6/6/19 8:33 PM):):public static final int FILTERTYPE_AND = 1;
// --Commented out by Inspection (6/6/19 8:33 PM): // --Commented out by Inspection (6/6/19 8:33 PM):   public static final int FILTERTYPE_OR = 2;
    public static final int FILTERTYPE_NOT = 3;

    public static final String TAG = "FilterObject";

    public static final int FILTERCOLUMN_TYPE = 1;
    public static final int FILTERCOLUMN_CHARACTER = 2;
    public static final int FILTERCOLUMN_OWNED = 3;
    public static final int FILTERCOLUMN_WANTTOREADWATCH = 4;
    public static final int FILTERCOLUMN_HASREADWATCHED = 5;

    public int id;
    public int column;
    private boolean positive;
    String displayText;
    private MutableLiveData<FilterObject> liveFilter;
    private static MediatorLiveData<List<FilterObject>> allFilters = new MediatorLiveData<>();
    // --Commented out by Inspection (6/6/19 8:33 PM):private static LiveData<List<MediaType>> allMediaTypes;
    private static MutableLiveData<List<FilterObject>> notesFilters = new MutableLiveData<>();
    private static MutableLiveData<List<FilterObject>> typeFilters = new MutableLiveData<>();

    private FilterObject(int id, int column, String displayText) {
        this.id = id;
        this.column = column;
        this.positive = true;//May want to add a constructor with this boolean if we want to make a filter with false
        this.displayText = displayText;
        this.liveFilter = new MutableLiveData<>();
        this.liveFilter.postValue(this);
    }

    public static LiveData<List<FilterObject>> getAllFilters(Application application) {
        new Thread(() -> updateFilterList(application)).start();
        return allFilters;
    }
    private static void updateFilterList(Application application){
        new Thread(() -> {
            if (allFilters.getValue() == null) {
                makeNotesFilters(application);
                makeTypeFilters(application);
                allFilters.addSource(typeFilters, value -> combineAllFilters());
                allFilters.addSource(notesFilters, value -> combineAllFilters());
            }else{
                makeTypeFilters(application);
                makeNotesFilters(application);
/*
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(application);
                List<MediaType> allMediaTypes = MediaDatabase.getMediaDataBase(application).getDaoType().getAllNonLive();
                List
                for (MediaType type : allMediaTypes){
                    typeFilterActive[type.getId()] = prefs.getBoolean(type.getText(), true);
                }
                */
/*typeFilterActive[1] = (prefs.getBoolean(application.getString(R.string.preferences_filter_movie), true));
                typeFilterActive[2] = (prefs.getBoolean(application.getString(R.string.preferences_filter_novelization), false));
                typeFilterActive[3] = (prefs.getBoolean(application.getString(R.string.preferences_filter_original_novel), true));
                typeFilterActive[4] = (prefs.getBoolean(application.getString(R.string.preferences_filter_video_game), false));
                typeFilterActive[5] = (prefs.getBoolean(application.getString(R.string.preferences_filter_youth), true));
                typeFilterActive[6] = (prefs.getBoolean(application.getString(R.string.preferences_filter_audiobook), true));
                typeFilterActive[7] = (prefs.getBoolean(application.getString(R.string.preferences_filter_single_comics), false));
                typeFilterActive[8] = (prefs.getBoolean(application.getString(R.string.preferences_filter_TPB), true));
                typeFilterActive[9] = (prefs.getBoolean(application.getString(R.string.preferences_filter_TV_Season), true));
                typeFilterActive[10] = (prefs.getBoolean(application.getString(R.string.preferences_filter_TV_Episode), false));*//*

                boolean[] typeFilterChecked = new boolean[allMediaTypes.size()+1];
                List<FilterObject> currentList = allFilters.getValue();
                Iterator<FilterObject> itr = currentList.iterator();
                while (itr.hasNext()){
                    FilterObject filter = itr.next();
                    if (filter.column == FILTERCOLUMN_TYPE) {
                        if (!typeFilterActive[filter.id]){
                            itr.remove();
                        }
                        typeFilterChecked[filter.id] = true;
                    }
                }
                for (int i = 1; i <=10; i++){
                    if (!typeFilterChecked[i]){
                        currentList.add(new FilterObject(i, FILTERCOLUMN_TYPE, getTextForType(i)));
                    }
                }
*/
            }
        }).start();
    }

    public static String getTextForType(int i){//TODO want to have this automatically generated from database
        switch(i){
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
            default:
                return "MediaTypeNotFound";
        }
    }

    private static void makeNotesFilters(Application application){
        List<FilterObject> newNotesFilters = new ArrayList<>();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(application);
        newNotesFilters.add(new FilterObject(0, FILTERCOLUMN_OWNED, prefs.getString(application.getString(R.string.owned),application.getString(R.string.owned))));
        newNotesFilters.add(new FilterObject(0, FILTERCOLUMN_HASREADWATCHED, prefs.getString(application.getString(R.string.watched_read),application.getString(R.string.watched_read))));
        newNotesFilters.add(new FilterObject(0, FILTERCOLUMN_WANTTOREADWATCH, prefs.getString(application.getString(R.string.want_to_watch_read),application.getString(R.string.want_to_watch_read))));
        notesFilters.postValue(newNotesFilters);
    }

    private static void combineAllFilters(){
        List<FilterObject> newAllFilters = new ArrayList<>();
        newAllFilters.addAll(notesFilters.getValue());
        newAllFilters.addAll(typeFilters.getValue());
        allFilters.postValue(newAllFilters);
    }

    private static void makeTypeFilters(Application application){
        List<FilterObject> newTypeFilters = new ArrayList<>();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(application);
        List<MediaType> allMediaTypes = MediaDatabase.getMediaDataBase(application).getDaoType().getAllNonLive();
        for (MediaType mediaType : allMediaTypes){
            if (prefs.getBoolean(mediaType.getText(), true)){
                newTypeFilters.add(new FilterObject(mediaType.getId(),FILTERCOLUMN_TYPE,mediaType.getText()));
            }
        }
        typeFilters.postValue(newTypeFilters);
    }

    /*private static List<FilterObject> createAllFilters(Application application){
        List<FilterObject> newAllFilters = new ArrayList<>();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(application);
        List<MediaType> allMediaTypes = MediaDatabase.getMediaDataBase(application).getDaoType().getAllNonLive();
        for (MediaType mediaType : allMediaTypes){
            if (prefs.getBoolean(mediaType.getText(), true)){
                newAllFilters.add(new FilterObject(mediaType.getId(),FILTERCOLUMN_TYPE,mediaType.getText()));
            }
        }
        newAllFilters.add(new FilterObject(0, FILTERCOLUMN_OWNED, prefs.getString(application.getString(R.string.owned),application.getString(R.string.owned))));
        newAllFilters.add(new FilterObject(0, FILTERCOLUMN_HASREADWATCHED, prefs.getString(application.getString(R.string.watched_read),application.getString(R.string.watched_read))));
        newAllFilters.add(new FilterObject(0, FILTERCOLUMN_WANTTOREADWATCH, prefs.getString(application.getString(R.string.want_to_watch_read),application.getString(R.string.want_to_watch_read))));
        return newAllFilters;
    }*/

    public boolean isPositive() {
        return positive;
    }

    void setPositive(boolean positive) {
        this.positive = positive;
        this.liveFilter.postValue(this);
    }

    public LiveData<FilterObject> getLiveFilter(){
        return liveFilter;
    }
}
