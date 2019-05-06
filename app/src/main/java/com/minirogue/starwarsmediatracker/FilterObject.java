package com.minirogue.starwarsmediatracker;

import com.minirogue.starwarsmediatracker.database.MediaItem;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Filter;

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
    private static List<FilterObject> allFilters;

    public FilterObject(int id, int column, int filterType, String displayText) {
        this.id = id;
        this.column = column;
        this.filterType = filterType;
        this.displayText = displayText;
    }

    public static List<FilterObject> getAllFilters() {
        if (allFilters == null){
            createAllFilters();
        }
        return allFilters;
    }
    private static void createAllFilters(){
        allFilters = new ArrayList<>();
        allFilters.add(new FilterObject(MediaItem.MEDIATYPE_MOVIE, FILTERCOLUMN_TYPE,0,"Movies"));
        allFilters.add(new FilterObject(MediaItem.MEDIATYPE_BOOK, FILTERCOLUMN_TYPE,0,"Books"));
        allFilters.add(new FilterObject(2, FILTERCOLUMN_CHARACTER,0,"Luke Skywalker"));
        allFilters.add(new FilterObject(3, FILTERCOLUMN_CHARACTER, 0, "Anakin Skywalker"));
        allFilters.add(new FilterObject(0, FILTERCOLUMN_OWNED, 0, "Owned"));
        allFilters.add(new FilterObject(0, FILTERCOLUMN_HASREADWATCHED, 0, "Read/Watched"));
        allFilters.add(new FilterObject(0, FILTERCOLUMN_WANTTOREADWATCH, 0,"Want to Watch/Read"));
    }
}
