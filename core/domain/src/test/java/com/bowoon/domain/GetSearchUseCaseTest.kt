package com.bowoon.domain

import androidx.paging.testing.asSnapshot
import com.bowoon.model.DisplayItem
import com.bowoon.model.InternalData
import com.bowoon.model.MovieAppData
import com.bowoon.model.SearchType
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
            userDataRepository = testUserDataRepository
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
                DisplayItem(
                    genreIds = listOf(it),
                    releaseDate = "releaseDate_$it",
                    title = "title_$it",
                    adult = true,
                    id = it,
                    imagePath = "/imagePath_$it.png",
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
                DisplayItem(
                    genreIds = listOf(it),
                    releaseDate = "releaseDate_$it",
                    title = "title_$it",
                    adult = true,
                    id = it,
                    imagePath = "/imagePath_$it.png"
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
                DisplayItem(
                    genreIds = listOf(it),
                    releaseDate = "releaseDate_$it",
                    title = "title_$it",
                    adult = true,
                    id = it,
                    imagePath = "/imagePath_$it.png",
                )
            }
        )
    }
}