package com.minirogue.starwarscanontracker.core.model.room

import androidx.room.testing.MigrationTestHelper
import androidx.test.platform.app.InstrumentationRegistry
import com.holocanon.core.data.database.MediaDatabase
import com.holocanon.core.di.RoomAndroidDependencyGraph.Companion.MIGRATE_12_13
import com.holocanon.core.di.RoomAndroidDependencyGraph.Companion.MIGRATE_13_14
import com.holocanon.core.di.RoomAndroidDependencyGraph.Companion.MIGRATE_14_15
import com.holocanon.core.di.RoomAndroidDependencyGraph.Companion.MIGRATE_15_16
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.io.IOException

class Migrate12to16Test {

    @Rule
    @JvmField
    val testHelper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        MediaDatabase::class.java,
    )

    @Test
    @Throws(IOException::class)
    fun migrate12To16ValidateData() {
        var db = testHelper.createDatabase(TEST_DB, 12)

        // Insert series entry
        /* val seriesId = 2
         val seriesTitle = "a series far far away"
         val seriesDescription = "a series from long long ago"
         val seriesImage = "url of series image"
         db.execSQL("INSERT INTO series (id, title, description, image) " +
                 "VALUES ($seriesId, '$seriesTitle', '$seriesDescription', '$seriesImage')")*/

        // Insert media_type entry
        val type = 5
        val typeText = "books or something"
        db.execSQL(
            "INSERT INTO media_types (id, text) " +
                "VALUES ($type, '$typeText')",
        )

        // Insert media_items entry
        val itemId = 20
        val title = "an interesting title"
        val author = "a person"
        val description = "an interesting description"
        val review = "it was bad"
        val image = "http://www.url.com/path/to/image"
        val date = "11/11/1111"
        val timeline = -1.25
        val amazonLink = "url to buy"
        val amazonStream = "url to stream"
        db.execSQL(
            "INSERT INTO media_items (id, title, author, type, description, review, image, " +
                "date, timeline, amazon_link, amazon_stream) " +
                "VALUES ($itemId, '$title', '$author', $type, '$description', '$review', '$image', " +
                "'$date', $timeline, '$amazonLink', '$amazonStream')",
        )

        // Insert media_notes entry
        val wantToWatchRead = 1
        val watchedOrRead = 0
        val owned = 1
        db.execSQL(
            "INSERT INTO media_notes (mediaId, want_to_watch_or_read, watched_or_read, owned) " +
                "VALUES ($itemId, $wantToWatchRead, $watchedOrRead, $owned)",
        )

        db.close()

        db = testHelper.runMigrationsAndValidate(
            TEST_DB,
            16,
            true,
            MIGRATE_12_13,
            MIGRATE_13_14,
            MIGRATE_14_15,
            MIGRATE_15_16,
        )

        var cursor = db.query("SELECT * FROM media_items WHERE id=$itemId")
        cursor.moveToPosition(0)
        assertEquals(cursor.getInt(cursor.getColumnIndex("id")), itemId)
        assertEquals(cursor.getString(cursor.getColumnIndex("title")), title)
        // assertEquals(cursor.getInt(cursor.getColumnIndex("series")), seriesId)
        assertEquals(cursor.getString(cursor.getColumnIndex("author")), author)
        assertEquals(cursor.getInt(cursor.getColumnIndex("type")), type)
        assertEquals(cursor.getString(cursor.getColumnIndex("description")), description)
        assertEquals(cursor.getString(cursor.getColumnIndex("review")), review)
        assertEquals(cursor.getString(cursor.getColumnIndex("image")), image)
        assertEquals(cursor.getString(cursor.getColumnIndex("date")), date)
        assertEquals(cursor.getDouble(cursor.getColumnIndex("timeline")), timeline, .0001)
        assertEquals(cursor.getString(cursor.getColumnIndex("amazon_link")), amazonLink)
        assertEquals(cursor.getString(cursor.getColumnIndex("amazon_stream")), amazonStream)

        cursor = db.query("SELECT * FROM media_notes WHERE mediaId=$itemId")
        cursor.moveToPosition(0)
        assertEquals(cursor.getInt(cursor.getColumnIndex("mediaId")), itemId)
        assertEquals(cursor.getInt(cursor.getColumnIndex("want_to_watch_or_read")), wantToWatchRead)
        assertEquals(cursor.getInt(cursor.getColumnIndex("watched_or_read")), watchedOrRead)
        assertEquals(cursor.getInt(cursor.getColumnIndex("owned")), owned)
    }

    @Test
    @Throws(IOException::class)
    fun migrate12To16ValidateJoinQuery() {
        var db = testHelper.createDatabase(TEST_DB, 12)

        // Insert series entry
        /*val seriesId = 2
        val seriesTitle = "a series far far away"
        val seriesDescription = "a series from long long ago"
        val seriesImage = "url of series image"
        db.execSQL("INSERT INTO series (id, title, description, image) " +
                "VALUES ($seriesId, '$seriesTitle', '$seriesDescription', '$seriesImage')")*/

        // Insert media_type entry
        val type = 5
        val typeText = "books or something"
        db.execSQL(
            "INSERT INTO media_types (id, text) " +
                "VALUES ($type, '$typeText')",
        )

        // Insert media_items entry
        val itemId = 20
        val title = "an interesting title"
        val author = "a person"
        val description = "an interesting description"
        val review = "it was bad"
        val image = "http://www.url.com/path/to/image"
        val date = "11/11/1111"
        val timeline = -1.25
        val amazonLink = "url to buy"
        val amazonStream = "url to stream"
        db.execSQL(
            "INSERT INTO media_items (id, title, author, type, description, review, image, date," +
                " timeline, amazon_link, amazon_stream) " +
                "VALUES ($itemId, '$title', '$author', $type, '$description', '$review', '$image', '$date'," +
                " $timeline, '$amazonLink', '$amazonStream')",
        )

        // Insert media_notes entry
        val wantToWatchRead = 1
        val watchedOrRead = 0
        val owned = 1
        db.execSQL(
            "INSERT INTO media_notes (mediaId, want_to_watch_or_read, watched_or_read, owned) " +
                "VALUES ($itemId, $wantToWatchRead, $watchedOrRead, $owned)",
        )

        db.close()

        db = testHelper.runMigrationsAndValidate(
            TEST_DB,
            16,
            true,
            MIGRATE_12_13,
            MIGRATE_13_14,
            MIGRATE_14_15,
            MIGRATE_15_16,
        )

        val cursor = db.query(
            "SELECT media_items.*,media_notes.* FROM media_items " +
                "INNER JOIN media_notes ON media_items.id = media_notes.mediaId",
        )
        cursor.moveToPosition(0)
        assertEquals(cursor.getInt(cursor.getColumnIndex("id")), itemId)
        assertEquals(cursor.getString(cursor.getColumnIndex("title")), title)
        // assertEquals(cursor.getInt(cursor.getColumnIndex("series")), seriesId)
        assertEquals(cursor.getString(cursor.getColumnIndex("author")), author)
        assertEquals(cursor.getInt(cursor.getColumnIndex("type")), type)
        assertEquals(cursor.getString(cursor.getColumnIndex("description")), description)
        assertEquals(cursor.getString(cursor.getColumnIndex("review")), review)
        assertEquals(cursor.getString(cursor.getColumnIndex("image")), image)
        assertEquals(cursor.getString(cursor.getColumnIndex("date")), date)
        assertEquals(cursor.getDouble(cursor.getColumnIndex("timeline")), timeline, .0001)
        assertEquals(cursor.getString(cursor.getColumnIndex("amazon_link")), amazonLink)
        assertEquals(cursor.getString(cursor.getColumnIndex("amazon_stream")), amazonStream)
        assertEquals(cursor.getInt(cursor.getColumnIndex("mediaId")), itemId)
        assertEquals(cursor.getInt(cursor.getColumnIndex("want_to_watch_or_read")), wantToWatchRead)
        assertEquals(cursor.getInt(cursor.getColumnIndex("watched_or_read")), watchedOrRead)
        assertEquals(cursor.getInt(cursor.getColumnIndex("owned")), owned)
    }

    companion object {
        private const val TEST_DB = "test_database"
    }
}
