package com.minirogue.starwarscanontracker.database;

import androidx.room.Entity;
import androidx.room.ForeignKey;

import org.jetbrains.annotations.NotNull;

@Entity(tableName = "media_character_join",
        primaryKeys = { "mediaId", "characterId" },
        foreignKeys = {
                @ForeignKey(entity = MediaItem.class,
                        parentColumns = "id",
                        childColumns = "mediaId"),
                @ForeignKey(entity = Character.class,
                        parentColumns = "id",
                        childColumns = "characterId")})
class MediaCharacterJoin {

    private final int mediaId;
    private final int characterId;

    public MediaCharacterJoin(int mediaId, int characterId){
        this.mediaId = mediaId;
        this.characterId = characterId;
    }

    public int getMediaId() {
        return mediaId;
    }

    public int getCharacterId() {
        return characterId;
    }

    @NotNull
    @Override
    public String toString() {
        return "("+mediaId+", "+characterId+")";
    }
}