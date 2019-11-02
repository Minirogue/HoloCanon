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
    private boolean wantToWatchRead;
    @ColumnInfo(name = "watched_or_read")
    private boolean watchedRead;
    @ColumnInfo(name = "owned")
    private boolean owned;

    public MediaNotes(int mediaId){
        this.mediaId = mediaId;
        wantToWatchRead = false;
        watchedRead = false;
        owned = false;
    }

    public int getMediaId() {
        return mediaId;
    }

// --Commented out by Inspection START (6/6/19 8:33 PM):
//    public void setMediaId(int mediaId) {
//        this.mediaId = mediaId;
//    }
// --Commented out by Inspection STOP (6/6/19 8:33 PM)

    public boolean isWantToWatchRead() {
        return wantToWatchRead;
    }

    public void setWantToWatchRead(boolean wantToWatchRead) {
        this.wantToWatchRead = wantToWatchRead;
    }
    public void flipWantToWatchRead(){
        wantToWatchRead = !wantToWatchRead;
    }

    public boolean isWatchedRead() {
        return watchedRead;
    }

    public void setWatchedRead(boolean watchedRead) {
        this.watchedRead = watchedRead;
    }
    public void flipWatchedRead(){
        watchedRead = !watchedRead;
    }

    public boolean isOwned() {
        return owned;
    }

    public void setOwned(boolean owned) {
        this.owned = owned;
    }
    public void flipOwned(){
        owned = !owned;
    }
}
