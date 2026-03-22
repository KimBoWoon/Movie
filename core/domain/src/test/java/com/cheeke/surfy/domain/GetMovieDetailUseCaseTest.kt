package com.cheeke.surfy.domain

import com.cheeke.surfy.model.LocaleOption
import com.cheeke.surfy.model.Movie
import com.cheeke.surfy.model.PosterSize
import com.cheeke.surfy.model.SurfyAppData
import com.cheeke.surfy.testing.model.configurationTestData
import com.cheeke.surfy.testing.model.favoriteMovieDetailTestData
import com.cheeke.surfy.testing.model.genreListTestData
import com.cheeke.surfy.testing.model.languageListTestData
import com.cheeke.surfy.testing.model.movieSeriesTestData
import com.cheeke.surfy.testing.model.regionTestData
import com.cheeke.surfy.testing.model.unFavoriteMovieDetailTestData
import com.cheeke.surfy.testing.repository.TestDatabaseRepository
import com.cheeke.surfy.testing.repository.TestMovieDetailRepository
import com.cheeke.surfy.testing.repository.TestPagingRepository
import com.cheeke.surfy.testing.repository.TestUserDataRepository
import com.cheeke.surfy.testing.utils.MainDispatcherRule
import com.cheeke.surfy.testing.utils.TestMovieAppDataManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class GetMovieDetailUseCaseTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private lateinit var detailRepository: TestMovieDetailRepository
    private lateinit var databaseRepository: TestDatabaseRepository
    private lateinit var userDataRepository: TestUserDataRepository
    private lateinit var getMovieDetailUseCase: GetMovieDetailUseCase
    private lateinit var movieAppDataRepository: TestMovieAppDataManager
    private lateinit var testPagingRepository: TestPagingRepository

    @Before
    fun setup() {
        detailRepository = TestMovieDetailRepository()
        databaseRepository = TestDatabaseRepository()
        userDataRepository = TestUserDataRepository()
        movieAppDataRepository = TestMovieAppDataManager()
        testPagingRepository = TestPagingRepository()
        getMovieDetailUseCase = GetMovieDetailUseCase(
            userDataRepository = userDataRepository,
            detailRepository = detailRepository,
            databaseRepository = databaseRepository
        )
        runBlocking {
            databaseRepository.insertMovie(movie = Movie(id = 23))
            movieAppDataRepository.setMovieAppData(
                SurfyAppData(
                    secureBaseUrl = configurationTestData.images?.secureBaseUrl ?: "",
                    movieGenres = genreListTestData.genres ?: emptyList(),
                    region = regionTestData.results?.map { LocaleOption(code = it.iso31661.orEmpty(), label = it.englishName.orEmpty(), isSelected = it.isSelected) } ?: emptyList(),
                    language = languageListTestData.map { LocaleOption(code = it.iso6391.orEmpty(), label = it.englishName.orEmpty(), isSelected = it.isSelected) },
                    posterSize = configurationTestData.images?.posterSizes?.map {
                        PosterSize(size = it, isSelected = it == "original")
                    } ?: emptyList()
                )
            )
        }
    }

    @Test
    fun getMovieDetailTest() = runTest {
        detailRepository.setMovie(favoriteMovieDetailTestData)
        detailRepository.setMovieSeries(movieSeriesTestData)

        val result = getMovieDetailUseCase(id = 0).first()

        assertEquals(
            expected = result.movie,
            actual = favoriteMovieDetailTestData
        )

        assertEquals(
            expected = result.movie.series,
            actual = movieSeriesTestData
        )

        assertEquals(
            expected = result.autoPlayTrailer,
            actual = userDataRepository.internalData.map { it.isAutoPlayTrailer }.first()
        )

        assertEquals(
            expected = result.isFavorite,
            actual = databaseRepository.isFavoriteMovie(id = 0).first()
        )
    }

    @Test
    fun getFavoriteMovieDetailTest() = runTest {
        detailRepository.setMovie(unFavoriteMovieDetailTestData)
        detailRepository.setMovieSeries(movieSeriesTestData)
        databaseRepository.insertMovie(
            movie = Movie(
                id = unFavoriteMovieDetailTestData.id,
                title = unFavoriteMovieDetailTestData.title,
                posterPath = unFavoriteMovieDetailTestData.posterPath
            )
        )

        val result = getMovieDetailUseCase(id = 324).first()

        assertEquals(
            expected = result.movie,
            actual = unFavoriteMovieDetailTestData
        )

        assertEquals(
            expected = result.movie.series,
            actual = movieSeriesTestData
        )

        assertEquals(
            expected = result.autoPlayTrailer,
            actual = userDataRepository.internalData.map { it.isAutoPlayTrailer }.first()
        )

        assertEquals(
            expected = result.isFavorite,
            actual = databaseRepository.isFavoriteMovie(id = 324).first()
        )
    }
}