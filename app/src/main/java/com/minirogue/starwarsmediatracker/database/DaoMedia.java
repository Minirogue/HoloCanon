package com.minirogue.starwarsmediatracker.database;

import androidx.lifecycle.LiveData;
import androidx.sqlite.db.SupportSQLiteQuery;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.room.Update;

import java.util.List;

@Dao
public interface DaoMedia {
//This class defines the insert, query, update, and delete methods for the database

    @RawQuery(observedEntities = MediaItem.class)
    LiveData<List<MediaItem>> getMediaFromRawQuery(SupportSQLiteQuery query);
    @RawQuery(observedEntities = {MediaItem.class, MediaNotes.class})
    LiveData<List<MediaAndNotes>> getMediaAndNotesRawQuery(SupportSQLiteQuery query);
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert (MediaItem mediaItem);
    @Insert
    void insertMultipleMediaItems (List<MediaItem> mediaItemList);
    @Query("SELECT * FROM media_items WHERE id = :mediaID LIMIT 1")
    MediaItem getMediaItemById (int mediaID);
    @Query("SELECT * FROM media_items WHERE type = :type")
    List<MediaItem> getMediaByType(int type);
    @Query("SELECT * FROM media_items")
    LiveData<List<MediaItem>> getAll();
    @Update
    void update (MediaItem mediaItem);
    @Delete
    void delete (MediaItem mediaItem);


    //The following are for MediaNotes interactions
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(MediaNotes um);
    @Update
    void update(MediaNotes um);
    @Delete
    void delete(MediaNotes um);


}
