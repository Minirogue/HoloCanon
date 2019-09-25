package com.minirogue.starwarscanontracker.database
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import java.io.IOException


//@RunWith(AndroidJUnit4::class)
class MigrationTest {
    val TEST_DB = "test_database"

    @get:Rule
    val testHelper: MigrationTestHelper = MigrationTestHelper(
            InstrumentationRegistry.getInstrumentation(),
            MediaDatabase::class.java.canonicalName,
            FrameworkSQLiteOpenHelperFactory())

    @Test
    @Throws(IOException::class)
    fun migrate11To12() {
        var db = testHelper.createDatabase(TEST_DB, 11).apply {
            //use execSQL() to populate database

            close()
        }

        db = testHelper.runMigrationsAndValidate(TEST_DB, 12, true, MediaDatabase.MIGRATE_11_12)

    }

    @Test
    @Throws(IOException::class)
    fun migrate12To13() {
        var db = testHelper.createDatabase(TEST_DB, 12).apply {
            //use execSQL() to populate database

            close()
        }

        db = testHelper.runMigrationsAndValidate(TEST_DB, 13, true, MediaDatabase.MIGRATE_12_13)

    }
}