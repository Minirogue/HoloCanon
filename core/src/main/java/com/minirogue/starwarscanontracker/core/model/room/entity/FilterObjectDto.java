package com.minirogue.starwarscanontracker.core.model.room.entity;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;

/**
 * defines a filter for the media list
 */
@Entity(tableName = "filter_object",
        primaryKeys = {"filter_id", "type_id"},
        foreignKeys = @ForeignKey(entity = FilterTypeDto.class,
                parentColumns = "id",
                childColumns = "type_id",
                onDelete = ForeignKey.CASCADE))
public class FilterObjectDto {
    @ColumnInfo(name = "filter_id")
    public int id;
    @ColumnInfo(name = "type_id")
    public int filterType;
    @ColumnInfo(name = "is_active")
    public boolean active;
    @ColumnInfo(name = "filter_text")
    public String displayText;

    public FilterObjectDto(int id, int filterType, boolean active, String displayText) {
        this.id = id;
        this.filterType = filterType;
        this.active = active;
        this.displayText = displayText;
    }

    @NonNull
    @Override
    public String toString() {
        return ("id: " + id + " filterType: " + filterType + " active: " + active + " displayText" + displayText);
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
            FilterObjectDto otherObj = (FilterObjectDto) obj;
            return otherObj.id == this.id && otherObj.filterType == this.filterType;
        }
    }
}
