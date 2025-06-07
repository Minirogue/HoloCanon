package com.holocanon.core.di

import android.app.Application
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.holocanon.core.data.database.MediaDatabase
import com.holocanon.core.data.database.MediaDatabase.Companion.DATABASE_NAME
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@ContributesTo(AppScope::class)
interface RoomAndroidDependencyGraph {
    @Provides
    @SingleIn(AppScope::class)
    fun provideRoomDatabase(application: Application): MediaDatabase {
        val dbFile = application.getDatabasePath(DATABASE_NAME)
        return Room.databaseBuilder<MediaDatabase>(
            context = application,
            name = dbFile.absolutePath,
        ).createFromAsset("database/$LATEST_PREPACKAGED_DATABASE")
            .addMigrations(
                MIGRATE_8_9,
                MIGRATE_9_10,
                MIGRATE_10_11,
                MIGRATE_11_12,
                MIGRATE_12_13,
                MIGRATE_13_14,
                MIGRATE_14_15,
                MIGRATE_15_16,
                MIGRATE_16_17,
            )
            .build()
    }

    @Suppress("MagicNumber")
    companion object {
        private const val LATEST_PREPACKAGED_DATABASE = "schema16_ver13.db"

        internal val MIGRATE_16_17: Migration = object : Migration(16, 17) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Add publisher column to media_items
                db.execSQL("ALTER TABLE 'media_items' ADD COLUMN 'publisher' INTEGER NOT NULL DEFAULT 0")
                // Create companies table
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `companies` (`id` INTEGER NOT NULL, `company_name` TEXT NOT NULL, PRIMARY KEY(`id`))",
                )
                // Change column names in media_notes
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `temp_table` (`media_id` INTEGER NOT NULL, `checkbox_1` INTEGER NOT NULL, `checkbox_2` INTEGER NOT NULL, `checkbox_3` INTEGER NOT NULL, PRIMARY KEY(`media_id`), FOREIGN KEY(`media_id`) REFERENCES `media_items`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )",
                )
                db.execSQL(
                    "INSERT INTO `temp_table` (media_id, checkbox_1, checkbox_2, checkbox_3) " +
                        "SELECT mediaId, watched_or_read, want_to_watch_or_read, owned FROM media_notes",
                )
                db.execSQL("DROP TABLE media_notes")
                db.execSQL("ALTER TABLE temp_table RENAME TO media_notes")
                // Change entries in media_items to non-null
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `temp_table` (`id` INTEGER NOT NULL, `title` TEXT NOT NULL DEFAULT '', `series` INTEGER NOT NULL, `author` TEXT NOT NULL DEFAULT '', `type` INTEGER NOT NULL, `description` TEXT NOT NULL DEFAULT '', `review` TEXT NOT NULL DEFAULT '', `image` TEXT NOT NULL DEFAULT '', `date` TEXT NOT NULL DEFAULT '99/99/9999', `timeline` REAL NOT NULL, `amazon_link` TEXT NOT NULL DEFAULT '', `amazon_stream` TEXT NOT NULL DEFAULT '', `publisher` INTEGER NOT NULL, PRIMARY KEY(`id`), FOREIGN KEY(`type`) REFERENCES `media_types`(`id`) ON UPDATE NO ACTION ON DELETE NO ACTION )",
                )
                db.execSQL(
                    "INSERT INTO `temp_table` (id, title, series, author, type, description, review, image, date, timeline, amazon_link, amazon_stream, publisher)" +
                        "SELECT id, IFNULL(title, ''), series, IFNULL(author, ''), type, IFNULL(description, ''), IFNULL(review, ''), IFNULL(image, ''), IFNULL(date, '99/99/9999'), timeline, IFNULL(amazon_link, ''), IFNULL(amazon_stream,''), publisher FROM media_items",
                )
                db.execSQL("DROP TABLE media_items")
                db.execSQL("ALTER TABLE temp_table RENAME TO media_items")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_media_items_type` ON `media_items` (`type`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_media_items_series` ON `media_items` (`series`)")
            }
        }

        internal val MIGRATE_15_16: Migration = object : Migration(15, 16) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE 'filter_type' ADD COLUMN 'text' TEXT NOT NULL DEFAULT ''")
            }
        }

        internal val MIGRATE_14_15: Migration = object : Migration(14, 15) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_media_items_type` ON `media_items` (`type`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_media_items_series` ON `media_items` (`series`)")
                // migrate primarykey of filter_object
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `temp_table` (`filter_id` INTEGER NOT NULL, `type_id` INTEGER NOT NULL, `is_active` INTEGER NOT NULL, `filter_text` TEXT, PRIMARY KEY(`filter_id`, `type_id`), FOREIGN KEY(`type_id`) REFERENCES `filter_type`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )",
                )
                db.execSQL(
                    "INSERT INTO `temp_table` (filter_id, type_id, is_active, filter_text) " +
                        "SELECT filter_id, type_id, is_active, filter_text FROM filter_object",
                )
                db.execSQL("DROP TABLE filter_object")
                db.execSQL("ALTER TABLE temp_table RENAME TO filter_object")
            }
        }

        internal val MIGRATE_13_14: Migration = object : Migration(13, 14) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `filter_object` (`filter_id` INTEGER NOT NULL, `type_id` INTEGER NOT NULL, `is_active` INTEGER NOT NULL, `filter_text` TEXT, PRIMARY KEY(`filter_id`), FOREIGN KEY(`type_id`) REFERENCES `filter_type`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )",
                )
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `filter_type` (`id` INTEGER NOT NULL, `is_positive` INTEGER NOT NULL, PRIMARY KEY(`id`))",
                )
                // Create temp table to migrate foreignkey
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `temp_table` (`id` INTEGER NOT NULL, `title` TEXT, `series` INTEGER NOT NULL, `author` TEXT, `type` INTEGER NOT NULL, `description` TEXT, `review` TEXT, `image` TEXT, `date` TEXT, `timeline` REAL NOT NULL, `amazon_link` TEXT, `amazon_stream` TEXT, PRIMARY KEY(`id`), FOREIGN KEY(`type`) REFERENCES `media_types`(`id`) ON UPDATE NO ACTION ON DELETE NO ACTION )",
                )
                db.execSQL(
                    "INSERT INTO `temp_table` (id, title, series, author, type, description, review,image, date,timeline,amazon_link,amazon_stream) " +
                        "SELECT id, title, series, author, type, description, review,image, date,timeline, amazon_link, amazon_stream FROM media_items",
                )
                db.execSQL("DROP TABLE media_items")
                db.execSQL("ALTER TABLE temp_table RENAME TO media_items")
            }
        }

        internal val MIGRATE_12_13: Migration = object : Migration(12, 13) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS 'series'('id' INTEGER NOT NULL, 'title' TEXT NOT NULL, 'description' TEXT NOT NULL, 'image' TEXT NOT NULL, PRIMARY KEY('id'))",
                )
                db.execSQL("ALTER TABLE 'media_items' ADD COLUMN 'series' INTEGER NOT NULL DEFAULT -1")
            }
        }

        internal val MIGRATE_11_12: Migration = object : Migration(11, 12) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE 'media_items' ADD COLUMN 'review' TEXT DEFAULT ''")
            }
        }

        internal val MIGRATE_10_11: Migration = object : Migration(10, 11) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE 'media_items' ADD COLUMN 'amazon_link' TEXT DEFAULT ''")
                db.execSQL("ALTER TABLE 'media_items' ADD COLUMN 'amazon_stream' TEXT DEFAULT ''")
            }
        }

        internal val MIGRATE_8_9: Migration = object : Migration(8, 9) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Example of adding a new column to a table. Make sure it matches the schema column
                db.execSQL("ALTER TABLE 'media_items' ADD COLUMN 'image' TEXT DEFAULT ''")
            }
        }

        internal val MIGRATE_9_10: Migration = object : Migration(9, 10) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Example of adding a new column to a table. Make sure it matches the schema column
                db.execSQL("ALTER TABLE 'media_items' ADD COLUMN 'date' TEXT DEFAULT ''")
                db.execSQL("ALTER TABLE 'media_items' ADD COLUMN 'timeline' REAL NOT NULL DEFAULT 10000")
            }
        }
    }
}
