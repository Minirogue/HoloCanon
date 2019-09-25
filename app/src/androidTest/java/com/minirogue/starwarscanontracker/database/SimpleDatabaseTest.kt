package com.minirogue.starwarscanontracker.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class SimpleDatabaseTest {
    private lateinit var userDao: DaoSeries
    private lateinit var db: MediaDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
                context, MediaDatabase::class.java).build()
        userDao = db.daoSeries
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    /*@Test
    @Throws(Exception::class)
    fun writeUserAndReadInList() {
        val series: Series = TestUtil.createUser(3).apply {
            setName("george")
        }
        userDao.insert(user)
        val byName = userDao.findUsersByName("george")
        assertThat(byName.get(0), equalTo(user))
    }*/
}