package com.minirogue.starwarsmediatracker.database;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "media_character_join",
        primaryKeys = { "mediaId", "characterId" },
        foreignKeys = {
                @ForeignKey(entity = MediaItem.class,
                        parentColumns = "id",
                        childColumns = "mediaId"),
                @ForeignKey(entity = Character.class,
                        parentColumns = "id",
                        childColumns = "characterId")})
public class MediaCharacterJoin {

    public final int mediaId;
    public final int characterId;

    public MediaCharacterJoin(int mediaId, int characterId){
        this.mediaId = mediaId;
        this.characterId = characterId;
    }

    @Override
    public String toString() {
        return "("+mediaId+", "+characterId+")";
    }
}