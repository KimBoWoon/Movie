package com.cheeke.surfy.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.cheeke.surfy.database.dao.MovieDao
import com.cheeke.surfy.database.dao.PeopleDao
import org.junit.After
import org.junit.Before

internal abstract class DatabaseTest {
    private lateinit var db: SurfyDatabase
    protected lateinit var movieDao: MovieDao
    protected lateinit var peopleDao: PeopleDao

    @Before
    fun setup() {
        db = run {
            val context = ApplicationProvider.getApplicationContext<Context>()
            Room.inMemoryDatabaseBuilder(
                context,
                SurfyDatabase::class.java,
            ).build()
        }
        movieDao = db.movieDao()
        peopleDao = db.peopleDao()
    }

    @After
    fun teardown() = db.close()
}
