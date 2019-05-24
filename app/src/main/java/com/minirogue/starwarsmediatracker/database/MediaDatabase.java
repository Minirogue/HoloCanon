package com.minirogue.starwarsmediatracker.database;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

@Database(entities = {MediaItem.class, Character.class, MediaCharacterJoin.class, MediaNotes.class,
                        MediaType.class},
        version = 8)
public abstract class MediaDatabase extends RoomDatabase {

    private static MediaDatabase databaseInstance;

    public abstract DaoMedia getDaoMedia();
    public abstract DaoCharacter getDaoCharacter();
    public abstract DaoType getDaoType();


    public static MediaDatabase getMediaDataBase(Context ctx){
        if (databaseInstance == null) {
            databaseInstance =
                    Room.databaseBuilder(ctx.getApplicationContext(), MediaDatabase.class, "StarWars-database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return databaseInstance;
    }

    public static void destroyInstance(){
        databaseInstance = null;
    }
}
