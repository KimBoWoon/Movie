package com.cheeke.surfy.database.di

import android.content.Context
import androidx.room.Room
import com.cheeke.surfy.database.SurfyDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseModule {
    @Provides
    @Singleton
    fun providesMovieDatabase(
        @ApplicationContext context: Context
    ): SurfyDatabase = Room.databaseBuilder(
        context = context,
        klass = SurfyDatabase::class.java,
        name = "surfy-database.db"
    )/*.addCallback(
        callback = object : RoomDatabase.Callback() {
            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)

                context.assets.open("surfy-database_db-movies.sql").use { inputStream ->
                    val sql = inputStream.bufferedReader().use { it.readText() }

                    sql.split(";")
                        .map { it.trim() }
                        .filter { it.isNotEmpty() && it.startsWith(prefix = "INSERT") }
                        .forEach { statement ->
                            runCatching {
                                db.execSQL(sql = statement)
                            }.onFailure {
                                Log.e(statement)
                            }
                        }
                }

                context.assets.open("surfy-database_db-peoples.sql").use { inputStream ->
                    val sql = inputStream.bufferedReader().use { it.readText() }

                    sql.split(";")
                        .map { it.trim() }
                        .filter { it.isNotEmpty() && it.startsWith(prefix = "INSERT") }
                        .forEach { statement ->
                            runCatching {
                                db.execSQL(sql = statement)
                            }.onFailure {
                                Log.e(statement)
                            }
                        }
                }

                context.assets.open("surfy-database_db-tvs.sql").use { inputStream ->
                    val sql = inputStream.bufferedReader().use { it.readText() }

                    sql.split(";")
                        .map { it.trim() }
                        .filter { it.isNotEmpty() && it.startsWith(prefix = "INSERT") }
                        .forEach { statement ->
                            runCatching {
                                db.execSQL(sql = statement)
                            }.onFailure {
                                Log.e(statement)
                            }
                        }
                }
            }
        }
    )*/.build()
}