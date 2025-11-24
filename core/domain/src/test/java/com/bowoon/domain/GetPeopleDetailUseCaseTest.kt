package com.bowoon.domain

import com.bowoon.model.People
import com.bowoon.testing.model.combineCreditsTestData
import com.bowoon.testing.model.externalIdsTestData
import com.bowoon.testing.model.peopleDetailTestData
import com.bowoon.testing.repository.TestDatabaseRepository
import com.bowoon.testing.repository.TestDetailRepository
import com.bowoon.testing.utils.MainDispatcherRule
import com.bowoon.testing.utils.TestMovieAppDataManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class GetPeopleDetailUseCaseTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private lateinit var detailRepository: TestDetailRepository
    private lateinit var databaseRepository: TestDatabaseRepository
    private lateinit var movieAppDataRepository: TestMovieAppDataManager
    private lateinit var getPeopleDetailUseCase: GetPeopleDetailUseCase

    @Before
    fun setup() {
        detailRepository = TestDetailRepository()
        databaseRepository = TestDatabaseRepository()
        movieAppDataRepository = TestMovieAppDataManager()
        getPeopleDetailUseCase = GetPeopleDetailUseCase(
            detailRepository = detailRepository,
            databaseRepository = databaseRepository
        )

        runBlocking { databaseRepository.insertPeople(people = People(id = 489)) }
    }

    @Test
    fun getPeopleDetailTest() = runTest {
        detailRepository.setPeopleDetail(peopleDetailTestData)
        detailRepository.setCombineCredits(combineCreditsTestData)
        detailRepository.setExternalIds(externalIdsTestData)

        val result = getPeopleDetailUseCase(0)

        assertEquals(
            result.first(),
            peopleDetailTestData.copy(isFavorite = false)
        )
    }

    @Test
    fun getFavoritePeopleDetailTest() = runTest {
        detailRepository.setPeopleDetail(peopleDetailTestData)
        detailRepository.setCombineCredits(combineCreditsTestData)
        detailRepository.setExternalIds(externalIdsTestData)
        databaseRepository.insertPeople(people = People(id = 0))

        val result = getPeopleDetailUseCase(0)

        assertEquals(
            result.first(),
            peopleDetailTestData
        )
    }
}