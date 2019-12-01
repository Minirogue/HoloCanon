package com.minirogue.starwarscanontracker.model.room
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
            //use execSQL() to populate room

            close()
        }

        db = testHelper.runMigrationsAndValidate(TEST_DB, 12, true, MediaDatabase.MIGRATE_11_12)

    }

    @Test
    @Throws(IOException::class)
    fun migrate12To13() {
        var db = testHelper.createDatabase(TEST_DB, 12).apply {
            //use execSQL() to populate room

            close()
        }

        db = testHelper.runMigrationsAndValidate(TEST_DB, 13, true, MediaDatabase.MIGRATE_12_13)

    }

    @Test
    @Throws(IOException::class)
    fun migrate13To14() {
        var db = testHelper.createDatabase(TEST_DB, 13).apply {
            //use execSQL() to populate room

            close()
        }

        db = testHelper.runMigrationsAndValidate(TEST_DB, 14, true, MediaDatabase.MIGRATE_13_14)

    }

    @Test
    @Throws(IOException::class)
    fun dataValidation13to14(){

    }

    @Test
    @Throws(IOException::class)
    fun migrate14To15() {
        var db = testHelper.createDatabase(TEST_DB, 14).apply {
            //use execSQL() to populate room

            close()
        }

        db = testHelper.runMigrationsAndValidate(TEST_DB, 15, true, MediaDatabase.MIGRATE_14_15)

    }
    @Test
    @Throws(IOException::class)
    fun migrate15To16() {
        var db = testHelper.createDatabase(TEST_DB, 15).apply {
            //use execSQL() to populate room

            close()
        }

        db = testHelper.runMigrationsAndValidate(TEST_DB, 16, true, MediaDatabase.MIGRATE_15_16)

    }
}