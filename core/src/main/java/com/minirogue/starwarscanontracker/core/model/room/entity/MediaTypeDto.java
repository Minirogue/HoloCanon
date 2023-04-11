package com.minirogue.starwarscanontracker.core.model.room.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "media_types")
public class MediaTypeDto {
    @PrimaryKey
    @ColumnInfo
    private int id;
    @ColumnInfo
    private String text;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
