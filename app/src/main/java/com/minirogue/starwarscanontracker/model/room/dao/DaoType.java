package com.minirogue.starwarscanontracker.model.room.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.minirogue.starwarscanontracker.model.room.entity.MediaType;

import java.util.List;

@SuppressWarnings("UnusedReturnValue")
@Dao
public interface DaoType {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(MediaType mediaType);

    @Update
    void update(MediaType mediaType);

    @Query("SELECT * FROM media_types")
    LiveData<List<MediaType>> getAllMediaTypes();

    @Query("SELECT * FROM media_types")
    List<MediaType> getAllNonLive();

    @Query("SELECT * FROM media_types WHERE id=:typeId")
    LiveData<MediaType> getLiveMediaType(int typeId);

}
