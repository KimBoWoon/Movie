package com.bowoon.domain

import androidx.paging.testing.asSnapshot
import com.bowoon.model.InternalData
import com.bowoon.model.Movie
import com.bowoon.model.MovieAppData
import com.bowoon.model.People
import com.bowoon.model.SearchPeopleKnownFor
import com.bowoon.model.SearchType
import com.bowoon.model.Series
import com.bowoon.testing.repository.TestMovieAppDataRepository
import com.bowoon.testing.repository.TestPagingRepository
import com.bowoon.testing.repository.TestUserDataRepository
import com.bowoon.testing.utils.MainDispatcherRule
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class GetSearchUseCaseTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private lateinit var getSearchUseCase: GetSearchUseCase
    private lateinit var testPagingRepository: TestPagingRepository
    private lateinit var testUserDataRepository: TestUserDataRepository
    private lateinit var testMovieAppDataRepository: TestMovieAppDataRepository

    @Before
    fun setup() {
        testPagingRepository = TestPagingRepository()
        testUserDataRepository = TestUserDataRepository()
        testMovieAppDataRepository = TestMovieAppDataRepository()
        getSearchUseCase = GetSearchUseCase(
            pagingRepository = testPagingRepository,
            userDataRepository = testUserDataRepository,
            movieAppDataRepository =testMovieAppDataRepository
        )

        runBlocking {
            testUserDataRepository.updateUserData(InternalData(), false)
            testMovieAppDataRepository.setMovieAppData(MovieAppData())
        }
    }

    @Test
    fun getMovieSearchResult() = runTest {
        val result = getSearchUseCase(searchType = SearchType.MOVIE, query = "mission", selectedGenre = flowOf(null))

        assertEquals(
            result.asSnapshot(),
            (0..6).map {
                Movie(
                    backdropPath = "backdropPath_$it",
                    genreIds = listOf(it),
                    originalLanguage = "originalLanguage_$it",
                    originalTitle = "originalTitle_$it",
                    overview = "overview_$it",
                    posterPath = "/posterPath_$it.png",
                    releaseDate = "releaseDate_$it",
                    title = "title_$it",
                    video = true,
                    voteAverage = it.toDouble(),
                    voteCount = it,
                    adult = true,
                    id = it,
                    name = "name_$it",
                    imagePath = "${testMovieAppDataRepository.movieAppData.value.getImageUrl()}/imagePath_$it.png",
                    originalName = "originalName_$it",
                    popularity = it.toDouble()
                )
            }
        )
    }

    @Test
    fun getPeopleSearchResult() = runTest {
        val result = getSearchUseCase(searchType = SearchType.PEOPLE, query = "mission", selectedGenre = flowOf(null))

        assertEquals(
            result.asSnapshot(),
            (0..6).map {
                People(
                    gender = 1,
                    knownFor = listOf(SearchPeopleKnownFor()),
                    knownForDepartment = "knownForDepartment_$it",
                    profilePath = "profilePath_$it",
                    adult = true,
                    id = it,
                    name = "name_$it",
                    imagePath = "${testMovieAppDataRepository.movieAppData.value.getImageUrl()}imagePath_$it",
                    originalName = "originalName_$it",
                    popularity = it.toDouble()
                )
            }
        )
    }

    @Test
    fun getSeriesSearchResult() = runTest {
        val result = getSearchUseCase(searchType = SearchType.SERIES, query = "mission", selectedGenre = flowOf(null))

        assertEquals(
            result.asSnapshot(),
            (0..6).map {
                Series(
                    backdropPath = "backdropPath_$it",
                    originalLanguage = "originalLanguage_$it",
                    overview = "overview_$it",
                    posterPath = "posterPath_$it",
                    adult = true,
                    id = it,
                    name = "name_$it",
                    imagePath = "${testMovieAppDataRepository.movieAppData.value.getImageUrl()}imagePath_$it",
                    originalName = "originalName_$it",
                    popularity = it.toDouble()
                )
            }
        )
    }
}