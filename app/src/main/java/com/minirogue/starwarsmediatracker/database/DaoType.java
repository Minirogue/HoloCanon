package com.minirogue.starwarsmediatracker.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface DaoType {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(MediaType mediaType);
    @Update
    void update(MediaType mediaType);
    @Delete
    void delete(MediaType mediaType);
    @Query("SELECT * FROM media_types")
    LiveData<List<MediaType>> getAllMediaTypes();
    @Query("SELECT * FROM media_types")
    List<MediaType> getAllNonLive();
}
