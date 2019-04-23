package com.minirogue.starwarsmediatracker.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
class UserMedia {

    @PrimaryKey
    private int mediaId;
    @ColumnInfo
    private boolean wantToWatchRead;
    @ColumnInfo
    private boolean watchedRead;
    @ColumnInfo
    private boolean owned;

    public UserMedia(int mediaId){
        this.mediaId = mediaId;
        wantToWatchRead = false;
        watchedRead = false;
        owned = false;
    }

    public int getMediaId() {
        return mediaId;
    }

    public void setMediaId(int mediaId) {
        this.mediaId = mediaId;
    }

    public boolean isWantToWatchRead() {
        return wantToWatchRead;
    }

    public void setWantToWatchRead(boolean wantToWatchRead) {
        this.wantToWatchRead = wantToWatchRead;
    }
    public boolean isWatchedRead() {
        return watchedRead;
    }

    public void setWatchedRead(boolean watchedRead) {
        this.watchedRead = watchedRead;
    }

    public boolean isOwned() {
        return owned;
    }

    public void setOwned(boolean owned) {
        this.owned = owned;
    }
}
