package com.minirogue.starwarscanontracker.core.model.room

import androidx.room.testing.MigrationTestHelper
import androidx.test.platform.app.InstrumentationRegistry
import com.holocanon.core.data.database.MediaDatabase
import com.holocanon.core.di.RoomAndroidDependencyGraph.Companion.MIGRATE_11_12
import com.holocanon.core.di.RoomAndroidDependencyGraph.Companion.MIGRATE_12_13
import com.holocanon.core.di.RoomAndroidDependencyGraph.Companion.MIGRATE_13_14
import com.holocanon.core.di.RoomAndroidDependencyGraph.Companion.MIGRATE_14_15
import com.holocanon.core.di.RoomAndroidDependencyGraph.Companion.MIGRATE_15_16
import com.holocanon.core.di.RoomAndroidDependencyGraph.Companion.MIGRATE_16_17
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.io.IOException

// @RunWith(AndroidJUnit4::class)
class MigrationTest {

    @Rule
    @JvmField
    val testHelper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        MediaDatabase::class.java,
//        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    @Throws(IOException::class)
    fun migrate11ToCurrent() {
        var db = testHelper.createDatabase(TEST_DB, 11).apply {
            // use execSQL() to populate room

            close()
        }

        db = testHelper.runMigrationsAndValidate(
            TEST_DB,
            MediaDatabase.SCHEMA_VERSION,
            true,
            MIGRATE_11_12,
            MIGRATE_12_13,
            MIGRATE_13_14,
            MIGRATE_14_15,
            MIGRATE_15_16,
            MIGRATE_16_17,
        )
    }

    @Test
    @Throws(IOException::class)
    fun migrate11To12() {
        var db = testHelper.createDatabase(TEST_DB, 11).apply {
            // use execSQL() to populate room

            close()
        }

        db = testHelper.runMigrationsAndValidate(TEST_DB, 12, true, MIGRATE_11_12)
    }

    @Test
    @Throws(IOException::class)
    fun migrate12To13() {
        var db = testHelper.createDatabase(TEST_DB, 12).apply {
            // use execSQL() to populate room

            close()
        }

        db = testHelper.runMigrationsAndValidate(TEST_DB, 13, true, MIGRATE_12_13)
    }

    @Test
    @Throws(IOException::class)
    fun migrate14To15() {
        var db = testHelper.createDatabase(TEST_DB, 14).apply {
            // use execSQL() to populate room

            close()
        }

        db = testHelper.runMigrationsAndValidate(TEST_DB, 15, true, MIGRATE_14_15)
    }

    @Test
    @Throws(IOException::class)
    fun migrate15To16() {
        var db = testHelper.createDatabase(TEST_DB, 15).apply {
            // use execSQL() to populate room

            close()
        }

        db = testHelper.runMigrationsAndValidate(TEST_DB, 16, true, MIGRATE_15_16)
    }

    @Test
    @Throws(IOException::class)
    fun migrate16To17() {
        var db = testHelper.createDatabase(TEST_DB, 16).apply {
            close()
        }

        db = testHelper.runMigrationsAndValidate(TEST_DB, 17, true, MIGRATE_16_17)
    }

    @Test
    @Throws(IOException::class)
    fun migrate13To14() {
        var db = testHelper.createDatabase(TEST_DB, 13)

        // Insert series entry
        val seriesId = 2
        val seriesTitle = "a series far far away"
        val seriesDescription = "a series from long long ago"
        val seriesImage = "url of series image"
        db.execSQL(
            "INSERT INTO series (id, title, description, image) " +
                "VALUES ($seriesId, '$seriesTitle', '$seriesDescription', '$seriesImage')",
        )

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
            "INSERT INTO media_items (id, title, series, author, type, description, review, image," +
                " date, timeline, amazon_link, amazon_stream) " +
                "VALUES ($itemId, '$title', $seriesId, '$author', $type, '$description', '$review', '$image'," +
                " '$date', $timeline, '$amazonLink', '$amazonStream')",
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

        db = testHelper.runMigrationsAndValidate(TEST_DB, 14, true, MIGRATE_13_14)

        var cursor = db.query("SELECT * FROM media_items WHERE id=$itemId")
        cursor.moveToPosition(0)
        assertEquals(cursor.getInt(cursor.getColumnIndex("id")), itemId)
        assertEquals(cursor.getString(cursor.getColumnIndex("title")), title)
        assertEquals(cursor.getInt(cursor.getColumnIndex("series")), seriesId)
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

    companion object {
        private const val TEST_DB = "test_database"
    }
}
