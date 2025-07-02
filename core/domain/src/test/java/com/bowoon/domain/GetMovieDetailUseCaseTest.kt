package com.bowoon.domain

import com.bowoon.model.Favorite
import com.bowoon.model.InternalData
import com.bowoon.model.MovieAppData
import com.bowoon.model.PosterSize
import com.bowoon.testing.model.configurationTestData
import com.bowoon.testing.model.genreListTestData
import com.bowoon.testing.model.languageListTestData
import com.bowoon.testing.model.movieSeriesTestData
import com.bowoon.testing.model.regionTestData
import com.bowoon.testing.repository.TestDatabaseRepository
import com.bowoon.testing.repository.TestDetailRepository
import com.bowoon.testing.repository.TestMovieAppDataRepository
import com.bowoon.testing.repository.TestPagingRepository
import com.bowoon.testing.repository.TestUserDataRepository
import com.bowoon.testing.repository.favoriteMovieDetailTestData
import com.bowoon.testing.repository.unFavoriteMovieDetailTestData
import com.bowoon.testing.utils.MainDispatcherRule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class GetMovieDetailUseCaseTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private lateinit var detailRepository: TestDetailRepository
    private lateinit var databaseRepository: TestDatabaseRepository
    private lateinit var userDataRepository: TestUserDataRepository
    private lateinit var getMovieDetailUseCase: GetMovieDetailUseCase
    private lateinit var movieAppDataRepository: TestMovieAppDataRepository
    private lateinit var testPagingRepository: TestPagingRepository

    @Before
    fun setup() {
        detailRepository = TestDetailRepository()
        databaseRepository = TestDatabaseRepository()
        userDataRepository = TestUserDataRepository()
        movieAppDataRepository = TestMovieAppDataRepository()
        testPagingRepository = TestPagingRepository()
        getMovieDetailUseCase = GetMovieDetailUseCase(
            userDataRepository = userDataRepository,
            detailRepository = detailRepository,
            databaseRepository = databaseRepository,
            movieAppDataRepository = movieAppDataRepository,
            pagingRepository = testPagingRepository
        )
        runBlocking {
            databaseRepository.insertMovie(Favorite(id = 23))
            userDataRepository.updateUserData(InternalData(), false)
            movieAppDataRepository.setMovieAppData(
                MovieAppData(
                    secureBaseUrl = configurationTestData.images?.secureBaseUrl,
                    genres = genreListTestData.genres ?: emptyList(),
                    region = regionTestData.results,
                    language = languageListTestData,
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
            result.detail,
            favoriteMovieDetailTestData.copy(
                backdropPath = "${movieAppDataRepository.movieAppData.value.getImageUrl()}${favoriteMovieDetailTestData.backdropPath}",
                belongsToCollection = favoriteMovieDetailTestData.belongsToCollection?.copy(
                    backdropPath = "${movieAppDataRepository.movieAppData.value.getImageUrl()}${favoriteMovieDetailTestData.belongsToCollection?.backdropPath}",
                    posterPath = "${movieAppDataRepository.movieAppData.value.getImageUrl()}${favoriteMovieDetailTestData.belongsToCollection?.posterPath}"
                ),
                images = favoriteMovieDetailTestData.images?.copy(
                    backdrops = favoriteMovieDetailTestData.images?.backdrops?.map { it.copy(filePath = "${movieAppDataRepository.movieAppData.value.getImageUrl()}${it.filePath}") },
                    logos = favoriteMovieDetailTestData.images?.logos?.map { it.copy(filePath = "${movieAppDataRepository.movieAppData.value.getImageUrl()}${it.filePath}") },
                    posters = favoriteMovieDetailTestData.images?.posters?.map { it.copy(filePath = "${movieAppDataRepository.movieAppData.value.getImageUrl()}${it.filePath}") }
                ),
                posterPath = "${movieAppDataRepository.movieAppData.value.getImageUrl()}${favoriteMovieDetailTestData.posterPath}",
                credits = favoriteMovieDetailTestData.credits?.copy(
                    cast = favoriteMovieDetailTestData.credits?.cast?.map { it.copy(profilePath = "${movieAppDataRepository.movieAppData.value.getImageUrl()}${it.profilePath}") },
                    crew = favoriteMovieDetailTestData.credits?.crew?.map { it.copy(profilePath = "${movieAppDataRepository.movieAppData.value.getImageUrl()}${it.profilePath}") }
                ),
                productionCompanies = favoriteMovieDetailTestData.productionCompanies?.map { it.copy(logoPath = "${movieAppDataRepository.movieAppData.value.getImageUrl()}${it.logoPath}") },
                isFavorite = false
            )
        )

        assertEquals(
            result.series,
            movieSeriesTestData.copy(
                backdropPath = "${movieAppDataRepository.movieAppData.value.getImageUrl()}${movieSeriesTestData.backdropPath}",
                posterPath = "${movieAppDataRepository.movieAppData.value.getImageUrl()}${movieSeriesTestData.posterPath}",
                parts = movieSeriesTestData.parts?.map {
                    it.copy(
                        backdropPath = "${movieAppDataRepository.movieAppData.value.getImageUrl()}${it.backdropPath}",
                        posterPath = "${movieAppDataRepository.movieAppData.value.getImageUrl()}${it.posterPath}"
                    )
                }
            )
        )
    }

    @Test
    fun getFavoriteMovieDetailTest() = runTest {
        detailRepository.setMovie(unFavoriteMovieDetailTestData)
        detailRepository.setMovieSeries(movieSeriesTestData)
        databaseRepository.insertMovie(
            Favorite(
                id = unFavoriteMovieDetailTestData.id,
                title = unFavoriteMovieDetailTestData.title,
                imagePath = unFavoriteMovieDetailTestData.posterPath
            )
        )

        val result = getMovieDetailUseCase(id = 324).first()

        assertEquals(
            result.detail,
            unFavoriteMovieDetailTestData.copy(
                backdropPath = "${movieAppDataRepository.movieAppData.value.getImageUrl()}${unFavoriteMovieDetailTestData.backdropPath}",
                belongsToCollection = unFavoriteMovieDetailTestData.belongsToCollection?.copy(
                    backdropPath = "${movieAppDataRepository.movieAppData.value.getImageUrl()}${unFavoriteMovieDetailTestData.belongsToCollection?.backdropPath}",
                    posterPath = "${movieAppDataRepository.movieAppData.value.getImageUrl()}${unFavoriteMovieDetailTestData.belongsToCollection?.posterPath}"
                ),
                images = unFavoriteMovieDetailTestData.images?.copy(
                    backdrops = unFavoriteMovieDetailTestData.images?.backdrops?.map { it.copy(filePath = "${movieAppDataRepository.movieAppData.value.getImageUrl()}${it.filePath}") },
                    logos = unFavoriteMovieDetailTestData.images?.logos?.map { it.copy(filePath = "${movieAppDataRepository.movieAppData.value.getImageUrl()}${it.filePath}") },
                    posters = unFavoriteMovieDetailTestData.images?.posters?.map { it.copy(filePath = "${movieAppDataRepository.movieAppData.value.getImageUrl()}${it.filePath}") }
                ),
                posterPath = "${movieAppDataRepository.movieAppData.value.getImageUrl()}${unFavoriteMovieDetailTestData.posterPath}",
                credits = unFavoriteMovieDetailTestData.credits?.copy(
                    cast = unFavoriteMovieDetailTestData.credits?.cast?.map { it.copy(profilePath = "${movieAppDataRepository.movieAppData.value.getImageUrl()}${it.profilePath}") },
                    crew = unFavoriteMovieDetailTestData.credits?.crew?.map { it.copy(profilePath = "${movieAppDataRepository.movieAppData.value.getImageUrl()}${it.profilePath}") }
                ),
                productionCompanies = unFavoriteMovieDetailTestData.productionCompanies?.map { it.copy(logoPath = "${movieAppDataRepository.movieAppData.value.getImageUrl()}${it.logoPath}") },
                isFavorite = true
            )
        )

        assertEquals(
            result.series,
            movieSeriesTestData.copy(
                backdropPath = "${movieAppDataRepository.movieAppData.value.getImageUrl()}${movieSeriesTestData.backdropPath}",
                posterPath = "${movieAppDataRepository.movieAppData.value.getImageUrl()}${movieSeriesTestData.posterPath}",
                parts = movieSeriesTestData.parts?.map {
                    it.copy(
                        backdropPath = "${movieAppDataRepository.movieAppData.value.getImageUrl()}${it.backdropPath}",
                        posterPath = "${movieAppDataRepository.movieAppData.value.getImageUrl()}${it.posterPath}"
                    )
                }
            )
        )
    }
}