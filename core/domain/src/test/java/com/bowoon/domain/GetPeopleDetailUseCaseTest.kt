package com.bowoon.domain

import com.bowoon.model.Favorite
import com.bowoon.testing.utils.MainDispatcherRule
import com.bowoon.testing.repository.TestDatabaseRepository
import com.bowoon.testing.repository.TestDetailRepository
import com.bowoon.testing.repository.peopleDetailTestData
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class GetPeopleDetailUseCaseTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private val detailRepository = TestDetailRepository()
    private val databaseRepository = TestDatabaseRepository()
    private val getPeopleDetailUseCase = GetPeopleDetailUseCase(
        detailRepository = detailRepository,
        databaseRepository = databaseRepository
    )

    @Test
    fun getPeopleDetailTest() = runTest {
        detailRepository.setPeopleDetail()
        detailRepository.setCombineCredits()
        detailRepository.setExternalIds()

        val result = getPeopleDetailUseCase(0)

        assertEquals(result.first(), peopleDetailTestData.copy(isFavorite = false))
    }

    @Test
    fun getFavoritePeopleDetailTest() = runTest {
        detailRepository.setPeopleDetail()
        detailRepository.setCombineCredits()
        detailRepository.setExternalIds()
        databaseRepository.insertPeople(Favorite(id = 0))

        val result = getPeopleDetailUseCase(0)

        assertEquals(result.first(), peopleDetailTestData)
    }
}