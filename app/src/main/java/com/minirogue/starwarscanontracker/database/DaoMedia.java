package com.minirogue.starwarscanontracker.database;

import androidx.lifecycle.LiveData;
import androidx.sqlite.db.SupportSQLiteQuery;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.room.Update;

import java.util.List;

@Dao
public interface DaoMedia {
//This class defines the insert, query, update, and delete methods for the database

// --Commented out by Inspection START (6/6/19 7:11 PM):
//    @RawQuery(observedEntities = MediaItem.class)
//    LiveData<List<MediaItem>> getMediaFromRawQuery(SupportSQLiteQuery query);
// --Commented out by Inspection STOP (6/6/19 7:11 PM)
    @RawQuery(observedEntities = {MediaItem.class, MediaNotes.class})
    LiveData<List<MediaAndNotes>> getMediaAndNotesRawQuery(SupportSQLiteQuery query);
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert (MediaItem mediaItem);

    @Query("SELECT * FROM media_notes WHERE mediaId = :mediaId")
    LiveData<MediaNotes> getMediaNotesById(int mediaId);
    @Query("SELECT * FROM media_items WHERE id = :mediaID LIMIT 1")
    LiveData<MediaItem> getMediaItemById (int mediaID);
    @Query("SELECT * FROM media_items WHERE series = :series")
    LiveData<MediaItem> getMediaItemsBySeries(int series);
    @Query("SELECT media_notes.* FROM media_items INNER JOIN media_notes ON media_items.id = media_notes.mediaId WHERE media_items.series = :series")
    LiveData<List<MediaNotes>> getMediaNotesBySeries(int series);

    @Query("SELECT media_notes.* FROM media_items INNER JOIN media_notes ON media_items.id = media_notes.mediaId WHERE media_items.series = :series")
    List<MediaNotes> getMediaNotesBySeriesNonLive(int series);


    // --Commented out by Inspection START (6/6/19 7:11 PM):
//    @Query("SELECT * FROM media_items WHERE type = :type")
//    List<MediaItem> getMediaByType(int type);
// --Commented out by Inspection STOP (6/6/19 7:11 PM)
// --Commented out by Inspection START (6/6/19 7:11 PM):
//    @Query("SELECT * FROM media_items")
//    LiveData<List<MediaItem>> getAll();
// --Commented out by Inspection STOP (6/6/19 7:11 PM)
    @Update
    void update (MediaItem mediaItem);
// --Commented out by Inspection START (6/6/19 7:11 PM):
//    @Delete
//    void delete (MediaItem mediaItem);
// --Commented out by Inspection STOP (6/6/19 7:11 PM)


    //The following are for MediaNotes interactions
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(MediaNotes um);

    @Update
    void update(MediaNotes um);
// --Commented out by Inspection START (6/6/19 8:33 PM):
//    @Delete
//    void delete(MediaNotes um);
// --Commented out by Inspection STOP (6/6/19 8:33 PM)


}
