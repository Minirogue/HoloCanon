package com.minirogue.starwarsmediatracker.database;

import android.arch.persistence.db.SupportSQLiteQuery;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.RawQuery;
import android.arch.persistence.room.Transaction;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface DaoMedia {
//This class defines the insert, query, update, and delete methods for the database

    @RawQuery
    List<MediaItem> getMediaFromRawQuery(SupportSQLiteQuery query);
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert (MediaItem mediaItem);
    @Insert
    void insertMultipleMediaItems (List<MediaItem> mediaItemList);
    @Query("SELECT * FROM media_items WHERE id = :mediaID LIMIT 1")
    MediaItem getMediaItemById (int mediaID);
    @Query("SELECT * FROM media_items WHERE type = :type")
    List<MediaItem> getMediaByType(int type);
    @Query("SELECT * FROM media_items")
    List<MediaItem> getAll();
    @Update
    void update (MediaItem mediaItem);
    @Delete
    void delete (MediaItem mediaItem);


    //The following are for UserMedia interactions
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(UserMedia um);
    @Update
    void update(UserMedia um);
    @Delete
    void delete(UserMedia um);


}
