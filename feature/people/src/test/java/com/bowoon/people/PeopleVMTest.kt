package com.bowoon.people

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.testing.invoke
import com.bowoon.domain.GetPeopleDetailUseCase
import com.bowoon.model.Movie
import com.bowoon.model.People
import com.bowoon.people.navigation.PeopleRoute
import com.bowoon.testing.model.combineCreditsTestData
import com.bowoon.testing.model.externalIdsTestData
import com.bowoon.testing.model.peopleDetailTestData
import com.bowoon.testing.repository.TestDatabaseRepository
import com.bowoon.testing.repository.TestDetailRepository
import com.bowoon.testing.repository.TestMovieAppDataRepository
import com.bowoon.testing.utils.MainDispatcherRule
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PeopleVMTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private lateinit var viewModel: PeopleVM
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var testDatabaseRepository: TestDatabaseRepository
    private lateinit var testDetailRepository: TestDetailRepository
    private lateinit var getPeopleDetailUseCase: GetPeopleDetailUseCase
    private lateinit var testMovieAppDataRepository: TestMovieAppDataRepository

    @Before
    fun setup() {
        savedStateHandle = SavedStateHandle(route = PeopleRoute(id = 0))
        testDatabaseRepository = TestDatabaseRepository()
        testDetailRepository = TestDetailRepository()
        testMovieAppDataRepository = TestMovieAppDataRepository()
        getPeopleDetailUseCase = GetPeopleDetailUseCase(
            detailRepository = testDetailRepository,
            databaseRepository = testDatabaseRepository
        )
        viewModel = PeopleVM(
            savedStateHandle = savedStateHandle,
            getPeopleDetail = getPeopleDetailUseCase,
            databaseRepository =testDatabaseRepository
        )
        runBlocking {
            testDatabaseRepository.insertPeople(people = People(id = 0, name = "people_1", profilePath = "/peopleImagePath.png"))
        }
    }

    @Test
    fun peopleDetailLoadingTest() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.people.collect() }

        assertEquals(viewModel.people.value, PeopleState.Loading)
    }

    @Test
    fun peopleDetailSuccessTest() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.people.collect() }

        assertEquals(viewModel.people.value, PeopleState.Loading)

        testDetailRepository.setPeopleDetail(peopleDetailTestData)
        testDetailRepository.setCombineCredits(combineCreditsTestData)
        testDetailRepository.setExternalIds(externalIdsTestData)

        assertEquals(
            viewModel.people.value,
            PeopleState.Success(peopleDetailTestData)
        )
    }

    @Test
    fun insertPeopleTest() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.people.collect() }

        val people = People(id = 124, combineCredits = combineCreditsTestData, externalIds = externalIdsTestData, isFavorite = false)

        testDetailRepository.setPeopleDetail(people)
        testDetailRepository.setCombineCredits(combineCreditsTestData)
        testDetailRepository.setExternalIds(externalIdsTestData)
        testDatabaseRepository.insertMovie(movie = Movie(id = 124, title = "people_124", posterPath = "/peopleImagePath.png"))

        assertEquals(
            viewModel.people.value,
            PeopleState.Success(people)
        )
    }

    @Test
    fun deletePeopleTest() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.people.collect() }

        val people = People(id = 124, combineCredits = combineCreditsTestData, externalIds = externalIdsTestData, isFavorite = true)

        testDetailRepository.setPeopleDetail(people)
        testDetailRepository.setCombineCredits(combineCreditsTestData)
        testDetailRepository.setExternalIds(externalIdsTestData)
        testDatabaseRepository.deletePeople(people = People(id = 124, name = "people_124", profilePath = "/peopleImagePath.png"))

        assertEquals(
            viewModel.people.value,
            PeopleState.Success(
                people.copy(isFavorite = false)
            )
        )
    }

//    @Test
//    fun restartFlowTest() = runTest {
//        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.people.collect() }
//
//        val people = PeopleDetail(id = 124, combineCredits = combineCreditsTestData, externalIds = externalIdsTestData, isFavorite = false)
//
//        assertEquals(viewModel.people.value, PeopleState.Loading)
//
//        testDetailRepository.setPeopleDetail(people)
//        testDetailRepository.setCombineCredits(combineCreditsTestData)
//        testDetailRepository.setExternalIds(externalIdsTestData)
//
//        assertEquals(viewModel.people.value, PeopleState.Success(people))
//
//        viewModel.restart()
//
//        assertEquals(viewModel.people.value, PeopleState.Loading)
//    }
}