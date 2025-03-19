package com.bowoon.domain

import com.bowoon.model.Favorite
import com.bowoon.model.InternalData
import com.bowoon.testing.utils.MainDispatcherRule
import com.bowoon.testing.repository.TestDatabaseRepository
import com.bowoon.testing.repository.TestDetailRepository
import com.bowoon.testing.repository.TestUserDataRepository
import com.bowoon.testing.repository.favoriteMovieDetailTestData
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class GetMovieDetailUseCaseTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private val detailRepository = TestDetailRepository()
    private val databaseRepository = TestDatabaseRepository()
    private val userDataRepository = TestUserDataRepository()
    private val getMovieDetailUseCase = GetMovieDetailUseCase(
        userDataRepository = userDataRepository,
        detailRepository = detailRepository,
        databaseRepository = databaseRepository
    )

    @Test
    fun getMovieDetailTest() = runTest {
        detailRepository.setMovieDetail(favoriteMovieDetailTestData)
        userDataRepository.updateUserData(InternalData(), false)

        val result = getMovieDetailUseCase(0)

        assertEquals(result.first(), favoriteMovieDetailTestData.copy(isFavorite = false))
    }

    @Test
    fun getFavoriteMovieDetailTest() = runTest {
        detailRepository.setMovieDetail(favoriteMovieDetailTestData)
        userDataRepository.updateUserData(InternalData(), false)
        databaseRepository.insertMovie(Favorite(id = 0))

        val result = getMovieDetailUseCase(0)

        assertEquals(result.first(), favoriteMovieDetailTestData)
    }
}