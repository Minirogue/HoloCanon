package com.minirogue.starwarsmediatracker.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "media_items")
public class MediaItem {
    //This class represents the database entries
    public static final int MEDIATYPE_ANY = 0;
    public static final int MEDIATYPE_NONE = -1;
    public static final int MEDIATYPE_MOVIE = 1;
    public static final int MEDIATYPE_BOOK = 2;


    @PrimaryKey
    private int id;
    @ColumnInfo(name = "title")
    private String title;
    @ColumnInfo(name = "author")
    private String author;
    @ColumnInfo(name = "type")
    private int type;
    @ColumnInfo(name = "description")
    private String description;

    public int getId() {
        return id;
    }

    public void setId(int newID){
        this.id = newID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
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