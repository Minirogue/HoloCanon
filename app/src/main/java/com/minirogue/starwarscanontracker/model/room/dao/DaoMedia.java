package com.minirogue.starwarscanontracker.model.room.dao;

import androidx.lifecycle.LiveData;
import androidx.sqlite.db.SupportSQLiteQuery;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.room.Update;

import com.minirogue.starwarscanontracker.model.room.pojo.MediaAndNotes;
import com.minirogue.starwarscanontracker.model.room.entity.MediaItem;
import com.minirogue.starwarscanontracker.model.room.entity.MediaNotes;

import java.util.List;

@SuppressWarnings("UnusedReturnValue")
@Dao
public interface DaoMedia {
//This class defines the insert, query, update, and delete methods for the room


    //The following are used for MediaItems
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(MediaItem mediaItem);

    @Update
    void update(MediaItem mediaItem);

    @Query("SELECT * FROM media_notes WHERE media_id = :mediaId")
    LiveData<MediaNotes> getMediaNotesById(int mediaId);

    @Query("SELECT * FROM media_items WHERE id = :mediaID LIMIT 1")
    LiveData<MediaItem> getMediaItemById(int mediaID);


    //The following are for MediaNotes interactions
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(MediaNotes um);

    @Update
    void update(MediaNotes um);

    @Query("SELECT media_notes.* FROM media_items INNER JOIN media_notes ON media_items.id = media_notes.media_id WHERE media_items.series = :series")
    LiveData<List<MediaNotes>> getMediaNotesBySeries(int series);

    @Query("SELECT media_notes.* FROM media_items INNER JOIN media_notes ON media_items.id = media_notes.media_id WHERE media_items.series = :series")
    List<MediaNotes> getMediaNotesBySeriesNonLive(int series);

    //The following return MediaAndNotes objects
    @RawQuery(observedEntities = {MediaItem.class, MediaNotes.class})
    LiveData<List<MediaAndNotes>> getMediaAndNotesRawQuery(SupportSQLiteQuery query);

    @Query("SELECT media_items.*,media_notes.* FROM media_items INNER JOIN media_notes ON media_items.id = media_notes.media_id")
    List<MediaAndNotes> getAllMediaAndNotes();

}
