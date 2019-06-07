package com.minirogue.starwarscanontracker.database;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import android.content.Context;

@Database(entities = {MediaItem.class, Character.class, MediaCharacterJoin.class, MediaNotes.class,
                        MediaType.class},
        version = 10)
public abstract class MediaDatabase extends RoomDatabase {

    private static MediaDatabase databaseInstance;

    public abstract DaoMedia getDaoMedia();
    // --Commented out by Inspection (6/6/19 8:33 PM):public abstract DaoCharacter getDaoCharacter();
    public abstract DaoType getDaoType();


    public static MediaDatabase getMediaDataBase(Context ctx){
        if (databaseInstance == null) {
            databaseInstance =
                    Room.databaseBuilder(ctx.getApplicationContext(), MediaDatabase.class, "StarWars-database")
                            .addMigrations(MIGRATE_8_9,MIGRATE_9_10)
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

    private final static Migration MIGRATE_9_10 = new Migration(9,10) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            //Example of adding a new column to a table. Make sure it matches the schema column
            database.execSQL("ALTER TABLE 'media_items' ADD COLUMN 'date' TEXT DEFAULT ''");
            database.execSQL("ALTER TABLE 'media_items' ADD COLUMN 'timeline' REAL NOT NULL DEFAULT 10000");
        }
    };


// --Commented out by Inspection START (6/6/19 8:33 PM):
//    public static void destroyInstance(){
//        databaseInstance = null;
//    }
// --Commented out by Inspection STOP (6/6/19 8:33 PM)
}
