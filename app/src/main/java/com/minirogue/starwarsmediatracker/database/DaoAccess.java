package com.minirogue.starwarsmediatracker.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface DaoAccess {
//This class defines the insert, query, update, and delete methods for the database

    @Insert
    void insertSingleMediaItem (MediaItem mediaItem);
    @Insert
    void insertMultipleMediaItems (List<MediaItem> mediaItemList);
    @Query("SELECT * FROM media_items WHERE mediaID = :mediaID")
    MediaItem getMediaItemById (int mediaID);
    @Query("SELECT * FROM media_items WHERE type = :type")
    List<MediaItem> getMediaByType(int type);
    @Query("SELECT * FROM media_items")
    List<MediaItem> getAll();
    @Update
    void updateMedia (MediaItem mediaItem);
    @Delete
    void deleteMedia (MediaItem mediaItem);
}
