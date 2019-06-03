package com.minirogue.starwarsmediatracker.database;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import android.content.Context;

@Database(entities = {MediaItem.class, Character.class, MediaCharacterJoin.class, MediaNotes.class,
                        MediaType.class},
        version = 9)
public abstract class MediaDatabase extends RoomDatabase {

    private static MediaDatabase databaseInstance;

    public abstract DaoMedia getDaoMedia();
    public abstract DaoCharacter getDaoCharacter();
    public abstract DaoType getDaoType();


    public static MediaDatabase getMediaDataBase(Context ctx){
        if (databaseInstance == null) {
            databaseInstance =
                    Room.databaseBuilder(ctx.getApplicationContext(), MediaDatabase.class, "StarWars-database")
                            .addMigrations(MIGRATE_8_9)
                            .build();
        }
        return databaseInstance;
    }

    private final static Migration MIGRATE_8_9 = new Migration(8,9) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            //Example of adding a new column to a table. Make sure it matches the schema column
            database.execSQL("ALTER TABLE 'media_items' ADD COLUMN 'image' TEXT DEFAULT ''");
        }
    };

    public static void destroyInstance(){
        databaseInstance = null;
    }
}
