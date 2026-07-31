package com.cheeke.surfy.database.impl

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.cheeke.surfy.database.impl.dao.KeywordDao
import com.cheeke.surfy.database.impl.dao.MovieDao
import com.cheeke.surfy.database.impl.dao.PeopleDao
import com.cheeke.surfy.database.impl.dao.TvDao
import org.junit.After
import org.junit.Before

internal abstract class DatabaseTest {
    private lateinit var db: SurfyDatabase
    protected lateinit var movieDao: MovieDao
    protected lateinit var peopleDao: PeopleDao
    protected lateinit var tvDao: TvDao
    protected lateinit var keywordDao: KeywordDao

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
        keywordDao = db.keywordDao()
    }

    @After
    fun teardown() = db.close()
}
