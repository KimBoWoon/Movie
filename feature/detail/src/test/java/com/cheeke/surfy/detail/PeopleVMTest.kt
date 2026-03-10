package com.cheeke.surfy.detail

import com.cheeke.surfy.detail.people.PeopleState
import com.cheeke.surfy.detail.people.PeopleVM
import com.cheeke.surfy.domain.GetPeopleDetailUseCase
import com.cheeke.surfy.domain.PeopleWithFavorite
import com.cheeke.surfy.model.Movie
import com.cheeke.surfy.model.People
import com.cheeke.surfy.testing.model.combineCreditsTestData
import com.cheeke.surfy.testing.model.externalIdsTestData
import com.cheeke.surfy.testing.model.peopleDetailTestData
import com.cheeke.surfy.testing.repository.TestDatabaseRepository
import com.cheeke.surfy.testing.repository.TestDetailRepository
import com.cheeke.surfy.testing.utils.MainDispatcherRule
import com.cheeke.surfy.testing.utils.TestAnalyticsHelper
import com.cheeke.surfy.testing.utils.TestMovieAppDataManager
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
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
    private lateinit var testDatabaseRepository: TestDatabaseRepository
    private lateinit var testDetailRepository: TestDetailRepository
    private lateinit var getPeopleDetailUseCase: GetPeopleDetailUseCase
    private lateinit var testMovieAppDataManager: TestMovieAppDataManager
    private lateinit var testAnalyticsHelper: TestAnalyticsHelper

    @Before
    fun setup() {
        testDatabaseRepository = TestDatabaseRepository()
        testDetailRepository = TestDetailRepository()
        testMovieAppDataManager = TestMovieAppDataManager()
        testAnalyticsHelper = TestAnalyticsHelper()
        getPeopleDetailUseCase = GetPeopleDetailUseCase(
            detailRepository = testDetailRepository,
            databaseRepository = testDatabaseRepository
        )
        viewModel = PeopleVM(
            id = 0,
            getPeopleDetail = getPeopleDetailUseCase,
            databaseRepository = testDatabaseRepository,
            analyticsHelper = testAnalyticsHelper
        )
        runBlocking {
            testDatabaseRepository.insertPeople(people = People(id = 0, title = "people_1", posterPath = "/peopleImagePath.png"))
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
            PeopleState.Success(
                PeopleWithFavorite(
                    people = peopleDetailTestData,
                    isFavorite = testDatabaseRepository.isFavoritePeople(id = 0).first()
                )
            )
        )
    }

    @Test
    fun insertPeopleTest() = runTest {
        viewModel = PeopleVM(
            id = 124,
            getPeopleDetail = getPeopleDetailUseCase,
            databaseRepository = testDatabaseRepository,
            analyticsHelper = testAnalyticsHelper
        )
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.people.collect() }

        val people = People(id = 124, combineCredits = combineCreditsTestData, externalIds = externalIdsTestData)

        testDetailRepository.setPeopleDetail(people)
        testDetailRepository.setCombineCredits(combineCreditsTestData)
        testDetailRepository.setExternalIds(externalIdsTestData)
        testDatabaseRepository.insertMovie(movie = Movie(id = 124, title = "people_124", posterPath = "/peopleImagePath.png"))

        assertEquals(
            viewModel.people.value,
            PeopleState.Success(
                data = PeopleWithFavorite(
                    people = people,
                    isFavorite = testDatabaseRepository.isFavoritePeople(id = 124).first()
                )
            )
        )
    }

    @Test
    fun deletePeopleTest() = runTest {
        viewModel = PeopleVM(
            id = 124,
            getPeopleDetail = getPeopleDetailUseCase,
            databaseRepository = testDatabaseRepository,
            analyticsHelper = testAnalyticsHelper
        )
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.people.collect() }

        testDetailRepository.setPeopleDetail(peopleDetailTestData)
        testDetailRepository.setCombineCredits(combineCreditsTestData)
        testDetailRepository.setExternalIds(externalIdsTestData)
        testDatabaseRepository.deletePeople(people = People(id = 124, title = "people_124", posterPath = "/peopleImagePath.png"))

        assertEquals(
            viewModel.people.value,
            PeopleState.Success(
                PeopleWithFavorite(
                    people = peopleDetailTestData,
                    isFavorite = testDatabaseRepository.isFavoritePeople(id = 124).first()
                )
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