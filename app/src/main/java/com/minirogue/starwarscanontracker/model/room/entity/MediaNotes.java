package com.minirogue.starwarscanontracker.model.room.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "media_notes",
        foreignKeys = {@ForeignKey(entity = MediaItem.class,
                parentColumns = "id",
                childColumns = "mediaId",
                onDelete = ForeignKey.CASCADE)})
public class MediaNotes {

    @PrimaryKey
    private int mediaId;
    @ColumnInfo(name = "want_to_watch_or_read")
    private boolean userChecked2;
    @ColumnInfo(name = "watched_or_read")
    private boolean userChecked1;
    @ColumnInfo(name = "owned")
    private boolean userChecked3;

    public MediaNotes(int mediaId) {
        this.mediaId = mediaId;
        userChecked2 = false;
        userChecked1 = false;
        userChecked3 = false;
    }

    public int getMediaId() {
        return mediaId;
    }


    public boolean isUserChecked2() {
        return userChecked2;
    }

    public void setUserChecked2(boolean userChecked2) {
        this.userChecked2 = userChecked2;
    }

    public void flipCheck2() {
        userChecked2 = !userChecked2;
    }

    public boolean isUserChecked1() {
        return userChecked1;
    }

    public void setUserChecked1(boolean userChecked1) {
        this.userChecked1 = userChecked1;
    }

    public void flipCheck1() {
        userChecked1 = !userChecked1;
    }

    public boolean isUserChecked3() {
        return userChecked3;
    }

    public void setUserChecked3(boolean userChecked3) {
        this.userChecked3 = userChecked3;
    }

    public void flipCheck3() {
        userChecked3 = !userChecked3;
    }
}
