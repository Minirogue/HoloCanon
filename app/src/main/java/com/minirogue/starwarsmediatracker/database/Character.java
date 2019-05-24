package com.minirogue.starwarsmediatracker.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "characters")
public class Character {

    @PrimaryKey
    private int id;
    @ColumnInfo
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString(){
        return (name==null ? "" : name);
    }

}

