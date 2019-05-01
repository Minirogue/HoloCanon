package com.minirogue.starwarsmediatracker;

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

    public int id;
    public int column;
    public int filterType;
    public String displayText;

    public FilterObject(int id, int column, int filterType, String displayText) {
        this.id = id;
        this.column = column;
        this.filterType = filterType;
        this.displayText = displayText;
    }
}
