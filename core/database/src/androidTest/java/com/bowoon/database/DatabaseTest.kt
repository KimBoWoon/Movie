/*
 * Copyright 2025 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bowoon.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.bowoon.database.dao.MovieDao
import com.bowoon.database.dao.PeopleDao
import org.junit.After
import org.junit.Before

internal abstract class DatabaseTest {
    private lateinit var db: MovieDatabase
    protected lateinit var movieDao: MovieDao
    protected lateinit var peopleDao: PeopleDao

    @Before
    fun setup() {
        db = run {
            val context = ApplicationProvider.getApplicationContext<Context>()
            Room.inMemoryDatabaseBuilder(
                context,
                MovieDatabase::class.java,
            ).build()
        }
        movieDao = db.movieDao()
        peopleDao = db.peopleDao()
    }

    @After
    fun teardown() = db.close()
}
