package com.minirogue.starwarscanontracker.model.room;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.minirogue.starwarscanontracker.model.room.entity.FilterObject;
import com.minirogue.starwarscanontracker.model.room.dao.DaoFilter;
import com.minirogue.starwarscanontracker.model.room.dao.DaoMedia;
import com.minirogue.starwarscanontracker.model.room.dao.DaoSeries;
import com.minirogue.starwarscanontracker.model.room.dao.DaoType;
import com.minirogue.starwarscanontracker.model.room.entity.FilterType;
import com.minirogue.starwarscanontracker.model.room.entity.MediaItem;
import com.minirogue.starwarscanontracker.model.room.entity.MediaNotes;
import com.minirogue.starwarscanontracker.model.room.entity.MediaType;
import com.minirogue.starwarscanontracker.model.room.entity.Series;
import com.minirogue.starwarscanontracker.model.room.join.MediaCharacterJoin;

@Database(entities = {MediaItem.class, com.minirogue.starwarscanontracker.model.room.entity.Character.class, MediaCharacterJoin.class, MediaNotes.class,
                        MediaType.class, Series.class, FilterObject.class, FilterType.class},
        version = 14)

public abstract class MediaDatabase extends RoomDatabase {

    private static MediaDatabase databaseInstance;

    public abstract DaoMedia getDaoMedia();
    public abstract DaoType getDaoType();
    public abstract DaoSeries getDaoSeries();
    public abstract DaoFilter getDaoFilter();


    public static MediaDatabase getMediaDataBase(Context ctx){
        if (databaseInstance == null) {
            databaseInstance =
                    Room.databaseBuilder(ctx.getApplicationContext(), MediaDatabase.class, "StarWars-room")
                            .addMigrations(MIGRATE_8_9,MIGRATE_9_10,MIGRATE_10_11,MIGRATE_11_12,MIGRATE_12_13,MIGRATE_13_14)
                            .build();
        }
        return databaseInstance;
    }

    final static Migration MIGRATE_13_14 = new Migration(13,14) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `filter_object` (`filter_id` INTEGER NOT NULL, `type_id` INTEGER NOT NULL, `is_active` INTEGER NOT NULL, `filter_text` TEXT, PRIMARY KEY(`filter_id`), FOREIGN KEY(`type_id`) REFERENCES `filter_type`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
            database.execSQL("CREATE TABLE IF NOT EXISTS `filter_type` (`id` INTEGER NOT NULL, `is_positive` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        }
    };

    final static Migration MIGRATE_12_13 = new Migration(12,13) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS 'series'('id' INTEGER NOT NULL, 'title' TEXT NOT NULL, 'description' TEXT NOT NULL, 'image' TEXT NOT NULL, PRIMARY KEY('id'))");
            database.execSQL("ALTER TABLE 'media_items' ADD COLUMN 'series' INTEGER NOT NULL DEFAULT -1");
        }
    };

    final static Migration MIGRATE_11_12 = new Migration(11,12) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE 'media_items' ADD COLUMN 'review' TEXT DEFAULT ''");
        }
    };

    private final static Migration MIGRATE_10_11 = new Migration(10,11){
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE 'media_items' ADD COLUMN 'amazon_link' TEXT DEFAULT ''");
            database.execSQL("ALTER TABLE 'media_items' ADD COLUMN 'amazon_stream' TEXT DEFAULT ''");
        }
    };

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
