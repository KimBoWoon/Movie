package com.bowoon.testing.repository

import androidx.annotation.VisibleForTesting
import com.bowoon.data.repository.MovieAppDataRepository
import com.bowoon.model.Configuration
import com.bowoon.model.Genres
import com.bowoon.model.Language
import com.bowoon.model.MovieAppData
import com.bowoon.model.Regions
import com.bowoon.testing.model.configurationTestData
import com.bowoon.testing.model.genreListTestData
import com.bowoon.testing.model.languageListTestData
import com.bowoon.testing.model.regionTestData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf

class TestMovieAppDataRepository : MovieAppDataRepository {
    override val movieAppData = MutableStateFlow<MovieAppData>(MovieAppData())

    override fun getConfiguration(): Flow<Configuration> = flowOf(configurationTestData)
    override fun getAvailableLanguage(): Flow<List<Language>> = flowOf(languageListTestData)
    override fun getAvailableRegion(): Flow<Regions> = flowOf(regionTestData)
    override fun getGenres(language: String): Flow<Genres> = flowOf(genreListTestData)

    @VisibleForTesting
    fun setMovieAppData(movieAppData: MovieAppData) {
        this@TestMovieAppDataRepository.movieAppData.tryEmit(movieAppData)
    }
}