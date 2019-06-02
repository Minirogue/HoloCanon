package com.minirogue.starwarsmediatracker;


import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.preference.PreferenceManager;

import com.minirogue.starwarsmediatracker.database.MediaType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * POJO to define a filter for the media list
 */
public class FilterObject {
    public static final int FILTERTYPE_AND = 1;
    public static final int FILTERTYPE_OR = 2;
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
    private static MutableLiveData<List<FilterObject>> allFilters = new MutableLiveData<>();
    private static LiveData<List<MediaType>> allMediaTypes;

    private FilterObject(int id, int column, boolean positive, String displayText) {
        this.id = id;
        this.column = column;
        this.positive = positive;
        this.displayText = displayText;
        this.liveFilter = new MutableLiveData<>();
        this.liveFilter.postValue(this);
    }

    public static LiveData<List<FilterObject>> getAllFilters(Application application) {
        new Thread(() -> updateFilterList(application)).start();
        return allFilters;
    }
    static void updateFilterList(Application application){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (allFilters.getValue() == null) {
                    allFilters.postValue(createAllFilters(application));
                }else{
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(application);
                    boolean[] typeFilterActive = new boolean[9];
                    typeFilterActive[1] = (prefs.getBoolean(application.getString(R.string.preferences_filter_movie), true));
                    typeFilterActive[2] = (prefs.getBoolean(application.getString(R.string.preferences_filter_novelization), false));
                    typeFilterActive[3] = (prefs.getBoolean(application.getString(R.string.preferences_filter_original_novel), true));
                    typeFilterActive[4] = (prefs.getBoolean(application.getString(R.string.preferences_filter_video_game), false));
                    typeFilterActive[5] = (prefs.getBoolean(application.getString(R.string.preferences_filter_youth), true));
                    typeFilterActive[6] = (prefs.getBoolean(application.getString(R.string.preferences_filter_audiobook), true));
                    typeFilterActive[7] = (prefs.getBoolean(application.getString(R.string.preferences_filter_single_comics), false));
                    typeFilterActive[8] = (prefs.getBoolean(application.getString(R.string.preferences_filter_TPB), true));
                    boolean[] typeFilterChecked = new boolean[9];
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
                    for (int i = 1; i <=8; i++){
                        if (!typeFilterChecked[i]){
                            currentList.add(new FilterObject(i, FILTERCOLUMN_TYPE, true, getTextForType(i)));
                        }
                    }
                }
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
            default:
                return "MediaTypeNotFound";
        }
    }

    private static List<FilterObject> createAllFilters(Application application){
        List<FilterObject> newAllFilters = new ArrayList<>();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(application);
        if (prefs.getBoolean(application.getString(R.string.preferences_filter_movie), true)){
            newAllFilters.add(new FilterObject(1,FILTERCOLUMN_TYPE,true, "Movie"));
        }
        if (prefs.getBoolean(application.getString(R.string.preferences_filter_novelization), false)){
            newAllFilters.add(new FilterObject(2,FILTERCOLUMN_TYPE,true, "Novelization"));
        }
        if (prefs.getBoolean(application.getString(R.string.preferences_filter_original_novel), true)){
            newAllFilters.add(new FilterObject(3,FILTERCOLUMN_TYPE,true, "Original Novel"));
        }
        if (prefs.getBoolean(application.getString(R.string.preferences_filter_video_game), false)){
            newAllFilters.add(new FilterObject(4,FILTERCOLUMN_TYPE,true, "Video Game"));
        }
        if (prefs.getBoolean(application.getString(R.string.preferences_filter_youth), true)){
            newAllFilters.add(new FilterObject(5,FILTERCOLUMN_TYPE,true, "Youth"));
        }
        if (prefs.getBoolean(application.getString(R.string.preferences_filter_audiobook), true)){
            newAllFilters.add(new FilterObject(6,FILTERCOLUMN_TYPE,true, "Audiobook"));
        }
        if (prefs.getBoolean(application.getString(R.string.preferences_filter_single_comics), false)){
            newAllFilters.add(new FilterObject(7,FILTERCOLUMN_TYPE,true, "Comic Book"));
        }
        if (prefs.getBoolean(application.getString(R.string.preferences_filter_TPB), true)){
            newAllFilters.add(new FilterObject(8,FILTERCOLUMN_TYPE,true, "TPB"));
        }
        newAllFilters.add(new FilterObject(0, FILTERCOLUMN_OWNED, true, "Owned"));
        newAllFilters.add(new FilterObject(0, FILTERCOLUMN_HASREADWATCHED, true, "Read/Watched"));
        newAllFilters.add(new FilterObject(0, FILTERCOLUMN_WANTTOREADWATCH, true,"Want to Watch/Read"));
        return newAllFilters;
    }

    public boolean isPositive() {
        return positive;
    }

    public void setPositive(boolean positive) {
        this.positive = positive;
        this.liveFilter.postValue(this);
    }

    public LiveData<FilterObject> getLiveFilter(){
        return liveFilter;
    }
}
