package com.bowoon.domain

import com.bowoon.model.Favorite
import com.bowoon.model.InternalData
import com.bowoon.testing.repository.TestDatabaseRepository
import com.bowoon.testing.repository.TestDetailRepository
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

    @Before
    fun setup() {
        detailRepository = TestDetailRepository()
        databaseRepository = TestDatabaseRepository()
        userDataRepository = TestUserDataRepository()
        getMovieDetailUseCase = GetMovieDetailUseCase(
            userDataRepository = userDataRepository,
            detailRepository = detailRepository,
            databaseRepository = databaseRepository
        )
        runBlocking {
            databaseRepository.insertMovie(Favorite(id = 23))
            userDataRepository.updateUserData(InternalData(), false)
        }
    }

    @Test
    fun getMovieDetailTest() = runTest {
        detailRepository.setMovieDetail(favoriteMovieDetailTestData)

        val result = getMovieDetailUseCase(0)

        assertEquals(result.first(), favoriteMovieDetailTestData.copy(isFavorite = false))
    }

    @Test
    fun getFavoriteMovieDetailTest() = runTest {
        detailRepository.setMovieDetail(unFavoriteMovieDetailTestData)
        databaseRepository.insertMovie(
            Favorite(
                id = unFavoriteMovieDetailTestData.id,
                title = unFavoriteMovieDetailTestData.title,
                imagePath = unFavoriteMovieDetailTestData.posterPath
            )
        )

        val result = getMovieDetailUseCase(324)

        assertEquals(result.first(), unFavoriteMovieDetailTestData.copy(isFavorite = true))
    }
}