package com.minirogue.starwarsmediatracker.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {MediaItem.class, Character.class, MediaCharacterJoin.class}, version = 3, exportSchema = false)//TODO examine schema when getting ready to ship app
public abstract class MediaDatabase extends RoomDatabase {

    private static MediaDatabase databaseInstance;

    public abstract DaoMedia getDaoMedia();
    public abstract DaoCharacter getDaoCharacter();


    public static MediaDatabase getMediaDataBase(Context ctx){
        if (databaseInstance == null) {
            databaseInstance =
                    Room.databaseBuilder(ctx.getApplicationContext(), MediaDatabase.class, "StarWars-database")
                    //.allowMainThreadQueries()//TODO this is a bad idea, but the tutorial said to do it for now
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return databaseInstance;
    }

    public static void destroyInstance(){
        databaseInstance = null;
    }
}
