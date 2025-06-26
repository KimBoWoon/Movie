package com.bowoon.domain

import com.bowoon.model.Favorite
import com.bowoon.model.MovieAppData
import com.bowoon.testing.repository.TestDatabaseRepository
import com.bowoon.testing.repository.TestMovieAppDataRepository
import com.bowoon.testing.utils.MainDispatcherRule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class GetFavoritePeopleUseCaseTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private lateinit var getFavoritePeopleListUseCase: GetFavoritePeopleListUseCase
    private lateinit var testDatabaseRepository: TestDatabaseRepository
    private lateinit var testMovieAppDataRepository: TestMovieAppDataRepository
    private val testPeople = Favorite(id = 0, title = "people_0", imagePath = "/asdf.png")

    @Before
    fun setup() {
        testDatabaseRepository = TestDatabaseRepository()
        testMovieAppDataRepository = TestMovieAppDataRepository()
        getFavoritePeopleListUseCase = GetFavoritePeopleListUseCase(
            databaseRepository = testDatabaseRepository,
            movieAppDataRepository = testMovieAppDataRepository
        )

        runBlocking {
            testMovieAppDataRepository.setMovieAppData(MovieAppData())
        }
    }

    @Test
    fun getFavoriteMovieList() = runTest {
        testDatabaseRepository.peopleDatabase.emit(emptyList())

        assertEquals(
            getFavoritePeopleListUseCase().first(),
            emptyList()
        )

        testDatabaseRepository.insertPeople(testPeople)

        assertEquals(
            getFavoritePeopleListUseCase().first(),
            listOf(
                testPeople.copy(imagePath = "${testMovieAppDataRepository.movieAppData.value.getImageUrl()}${testPeople.imagePath}")
            )
        )
    }
}