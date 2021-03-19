package com.minirogue.starwarscanontracker.core.model.room.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "characters")
public class Character {

    @PrimaryKey
    public int id;
    @ColumnInfo
    public String name;


    @NonNull
    @Override
    public String toString() {
        return (name == null ? "" : name);
    }

}

