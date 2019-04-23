package com.minirogue.starwarsmediatracker.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.provider.MediaStore;

import java.util.List;

@Dao
public interface DaoCharacter {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(Character character);
    @Update
    void update(Character character);
    @Query("SELECT * FROM characters WHERE id = :id LIMIT 1")
    Character getCharacterById(int id);
    @Query("SELECT * FROM characters")
    List<Character> getAllCharacters();
    @Query("SELECT name FROM characters ORDER BY name")
    List<String> getAllCharacterNames();
    @Query("SELECT * FROM characters WHERE name = :name LIMIT 1")
    Character getCharacterByName(String name);


    //The following is for MediaCharacterJoin
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(MediaCharacterJoin mediaCharacterJoin);
    @Query("SELECT * FROM characters INNER JOIN media_character_join ON id=media_character_join.characterId WHERE media_character_join.mediaId=:mediaId")
    List<Character> getCharactersFromMedia(final int mediaId);
    @Query("SELECT * FROM media_items INNER JOIN media_character_join ON id=media_character_join.mediaId WHERE media_character_join.characterId=:characterID")
    List<MediaItem> getMediaFromCharacter(final int characterID);
    @Query("DELETE FROM media_character_join")
    void clearMediaCharacterJoin();
    @Query("SELECT * FROM media_character_join")
    List<MediaCharacterJoin> getAllMCJoin();



}
