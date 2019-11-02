package com.minirogue.starwarscanontracker.model.room.entity;


import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

/**
 * defines a filter for the media list
 */
@Entity(tableName = "filter_object",
        foreignKeys = @ForeignKey(entity = FilterType.class,
                parentColumns = "id",
                childColumns = "type_id",
                onDelete = CASCADE))
public class FilterObject {
    public static final String TAG = "FilterObject";

    @PrimaryKey
    @ColumnInfo(name = "filter_id")
    public int id;
    @ColumnInfo(name = "type_id")
    public int filterType;
    @ColumnInfo(name = "is_active")
    public boolean active;
    @ColumnInfo(name = "filter_text")
    public String displayText;

    public FilterObject(int id, int filterType, boolean active, String displayText) {
        this.id = id;
        this.filterType = filterType;
        this.active = active;
        this.displayText = displayText;
    }

    /**
     * Check if this FilterObject is equal to another object.
     * <p>
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
            FilterObject otherObj = (FilterObject) obj;
            return otherObj.id == this.id && otherObj.filterType == this.filterType;
        }
    }

    public static String getTextForType(int i) {//TODO want to have this automatically generated from room
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
}
