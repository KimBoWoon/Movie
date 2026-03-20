package com.cheeke.surfy.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.cheeke.surfy.database.dao.MovieDao
import com.cheeke.surfy.database.dao.PeopleDao
import com.cheeke.surfy.database.dao.TvDao
import org.junit.After
import org.junit.Before

internal abstract class DatabaseTest {
    private lateinit var db: SurfyDatabase
    protected lateinit var movieDao: MovieDao
    protected lateinit var peopleDao: PeopleDao
    protected lateinit var tvDao: TvDao

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
        tvDao = db.tvDao()
    }

    @After
    fun teardown() = db.close()
}
