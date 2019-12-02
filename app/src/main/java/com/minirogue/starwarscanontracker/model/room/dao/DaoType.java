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
// --Commented out by Inspection START (6/6/19 8:33 PM):
//// --Commented out by Inspection START (6/6/19 8:33 PM):
////    @Delete
////    void delete(MediaType mediaType);
//// --Commented out by Inspection STOP (6/6/19 8:33 PM)
// --Commented out by Inspection STOP (6/6/19 8:33 PM)
    @Query("SELECT * FROM media_types")
    LiveData<List<MediaType>> getAllMediaTypes();
    @Query("SELECT * FROM media_types")
    List<MediaType> getAllNonLive();
}
