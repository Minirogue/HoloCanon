package com.minirogue.starwarscanontracker.model.room;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.minirogue.starwarscanontracker.model.room.dao.DaoFilter;
import com.minirogue.starwarscanontracker.model.room.dao.DaoMedia;
import com.minirogue.starwarscanontracker.model.room.dao.DaoSeries;
import com.minirogue.starwarscanontracker.model.room.dao.DaoType;
import com.minirogue.starwarscanontracker.model.room.entity.FilterObject;
import com.minirogue.starwarscanontracker.model.room.entity.FilterType;
import com.minirogue.starwarscanontracker.model.room.entity.MediaItem;
import com.minirogue.starwarscanontracker.model.room.entity.MediaNotes;
import com.minirogue.starwarscanontracker.model.room.entity.MediaType;
import com.minirogue.starwarscanontracker.model.room.entity.Series;
import com.minirogue.starwarscanontracker.model.room.join.MediaCharacterJoin;

@Database(entities = {MediaItem.class, com.minirogue.starwarscanontracker.model.room.entity.Character.class, MediaCharacterJoin.class, MediaNotes.class,
        MediaType.class, Series.class, FilterObject.class, FilterType.class},
        version = 16)

public abstract class MediaDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "StarWars-database";
    private static final String LATEST_PREPACKAGED_DATABASE = "schema16_ver13.db";

    private static MediaDatabase databaseInstance;

    public abstract DaoMedia getDaoMedia();
    public abstract DaoType getDaoType();
    public abstract DaoSeries getDaoSeries();
    public abstract DaoFilter getDaoFilter();


    public static MediaDatabase getMediaDataBase(Context ctx) {
        if (databaseInstance == null) {
            databaseInstance =
                    Room.databaseBuilder(ctx.getApplicationContext(), MediaDatabase.class, DATABASE_NAME)
                            .createFromAsset("database/" + LATEST_PREPACKAGED_DATABASE)
                            .addMigrations(MIGRATE_8_9, MIGRATE_9_10, MIGRATE_10_11, MIGRATE_11_12, MIGRATE_12_13,
                                    MIGRATE_13_14, MIGRATE_14_15, MIGRATE_15_16)
                            .build();
        }
        return databaseInstance;
    }

    /**
     * Due to an error while refactoring, a small portion of devices started using a database by the name "StarWars-room" instead of "StarWars-database".
     * This method exists as a way to obtain the incorrect database to put its data into the correct database.
     *
     * @param name The name of the database to be obtained.
     * @param ctx  Context
     * @return The database that is saved under the given name.
     */
    public static MediaDatabase getDatabaseByName(String name, Context ctx) {
        return Room.databaseBuilder(ctx.getApplicationContext(), MediaDatabase.class, name)
                .addMigrations(MIGRATE_8_9, MIGRATE_9_10, MIGRATE_10_11, MIGRATE_11_12, MIGRATE_12_13,
                        MIGRATE_13_14, MIGRATE_14_15, MIGRATE_15_16)
                .build();
    }

    final static Migration MIGRATE_15_16 = new Migration(15, 16) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE 'filter_type' ADD COLUMN 'text' TEXT NOT NULL DEFAULT ''");
        }
    };

    final static Migration MIGRATE_14_15 = new Migration(14, 15) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_media_items_type` ON `media_items` (`type`)");
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_media_items_series` ON `media_items` (`series`)");
            //migrate primarykey of filter_object
            database.execSQL("CREATE TABLE IF NOT EXISTS `temp_table` (`filter_id` INTEGER NOT NULL, `type_id` INTEGER NOT NULL, `is_active` INTEGER NOT NULL, `filter_text` TEXT, PRIMARY KEY(`filter_id`, `type_id`), FOREIGN KEY(`type_id`) REFERENCES `filter_type`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
            database.execSQL("INSERT INTO `temp_table` (filter_id, type_id, is_active, filter_text) " +
                    "SELECT filter_id, type_id, is_active, filter_text FROM filter_object");
            database.execSQL("DROP TABLE filter_object");
            database.execSQL("ALTER TABLE temp_table RENAME TO filter_object");
        }
    };

    final static Migration MIGRATE_13_14 = new Migration(13, 14) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `filter_object` (`filter_id` INTEGER NOT NULL, `type_id` INTEGER NOT NULL, `is_active` INTEGER NOT NULL, `filter_text` TEXT, PRIMARY KEY(`filter_id`), FOREIGN KEY(`type_id`) REFERENCES `filter_type`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
            database.execSQL("CREATE TABLE IF NOT EXISTS `filter_type` (`id` INTEGER NOT NULL, `is_positive` INTEGER NOT NULL, PRIMARY KEY(`id`))");
            //Create temp table to migrate foreignkey
            database.execSQL("CREATE TABLE IF NOT EXISTS `temp_table` (`id` INTEGER NOT NULL, `title` TEXT, `series` INTEGER NOT NULL, `author` TEXT, `type` INTEGER NOT NULL, `description` TEXT, `review` TEXT, `image` TEXT, `date` TEXT, `timeline` REAL NOT NULL, `amazon_link` TEXT, `amazon_stream` TEXT, PRIMARY KEY(`id`), FOREIGN KEY(`type`) REFERENCES `media_types`(`id`) ON UPDATE NO ACTION ON DELETE NO ACTION )");
            database.execSQL("INSERT INTO `temp_table` (id, title, series, author, type, description, review,image, date,timeline,amazon_link,amazon_stream) " +
                    "SELECT id, title, series, author, type, description, review,image, date,timeline, amazon_link, amazon_stream FROM media_items");
            database.execSQL("DROP TABLE media_items");
            database.execSQL("ALTER TABLE temp_table RENAME TO media_items");

        }
    };

    final static Migration MIGRATE_12_13 = new Migration(12, 13) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS 'series'('id' INTEGER NOT NULL, 'title' TEXT NOT NULL, 'description' TEXT NOT NULL, 'image' TEXT NOT NULL, PRIMARY KEY('id'))");
            database.execSQL("ALTER TABLE 'media_items' ADD COLUMN 'series' INTEGER NOT NULL DEFAULT -1");
        }
    };

    final static Migration MIGRATE_11_12 = new Migration(11, 12) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE 'media_items' ADD COLUMN 'review' TEXT DEFAULT ''");
        }
    };

    private final static Migration MIGRATE_10_11 = new Migration(10, 11) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE 'media_items' ADD COLUMN 'amazon_link' TEXT DEFAULT ''");
            database.execSQL("ALTER TABLE 'media_items' ADD COLUMN 'amazon_stream' TEXT DEFAULT ''");
        }
    };

    private final static Migration MIGRATE_8_9 = new Migration(8, 9) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            //Example of adding a new column to a table. Make sure it matches the schema column
            database.execSQL("ALTER TABLE 'media_items' ADD COLUMN 'image' TEXT DEFAULT ''");
        }
    };

    private final static Migration MIGRATE_9_10 = new Migration(9, 10) {
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
