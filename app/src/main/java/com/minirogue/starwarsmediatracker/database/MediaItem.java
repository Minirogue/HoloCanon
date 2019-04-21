package com.minirogue.starwarsmediatracker.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "media_items")
public class MediaItem {
    //This class represents the database entries
    public static final int MEDIATYPE_ANY = 0;
    public static final int MEDIATYPE_NONE = -1;
    public static final int MEDIATYPE_MOVIE = 1;
    public static final int MEDIATYPE_BOOK = 2;


    @PrimaryKey
    private int mediaID;
    @ColumnInfo(name = "title")
    private String title;
    @ColumnInfo(name = "type")
    private int type;
    @ColumnInfo(name = "description")
    private String description;

    public int getMediaID() {
        return mediaID;
    }

    public void setMediaID(int newID){
        this.mediaID = newID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static String convertTypeToString(int type){
        switch (type){
            case MEDIATYPE_ANY:
                return "Any";
            case MEDIATYPE_NONE:
                return "None";
            case MEDIATYPE_MOVIE:
                return "Movie";
            case MEDIATYPE_BOOK:
                return "Book";
            default:
                return "Media type not found";
        }
    }

    @Override
    public String toString() {
        return title;
    }
}