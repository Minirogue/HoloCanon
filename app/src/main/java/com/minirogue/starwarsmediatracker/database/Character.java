package com.minirogue.starwarsmediatracker.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "characters")
public class Character {

    @PrimaryKey
    private int id;
    @ColumnInfo
    private String name;

    public static Character getBlank(){
        Character newChar = new Character();
        newChar.setId(-1);
        newChar.setName("");
        return newChar;
    }

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
        return name;
    }

}

