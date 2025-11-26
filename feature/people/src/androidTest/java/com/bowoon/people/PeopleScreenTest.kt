package com.bowoon.people

import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bowoon.domain.GetPeopleDetailUseCase
import com.bowoon.model.MovieAppData
import com.bowoon.model.People
import com.bowoon.model.PosterSize
import com.bowoon.model.getRelatedMovie
import com.bowoon.testing.model.combineCreditsTestData
import com.bowoon.testing.model.configurationTestData
import com.bowoon.testing.model.externalIdsTestData
import com.bowoon.testing.model.genreListTestData
import com.bowoon.testing.model.languageListTestData
import com.bowoon.testing.model.peopleDetailTestData
import com.bowoon.testing.model.regionTestData
import com.bowoon.testing.repository.TestDatabaseRepository
import com.bowoon.testing.repository.TestDetailRepository
import com.bowoon.testing.utils.TestMovieAppDataManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class PeopleScreenTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()
    private lateinit var viewModel: PeopleVM
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var getPeopleDetail: GetPeopleDetailUseCase
    private lateinit var testDatabaseRepository: TestDatabaseRepository
    private lateinit var testDetailRepository: TestDetailRepository
    private lateinit var testMovieAppDataManager: TestMovieAppDataManager
    private val movieAppData = MovieAppData(
        secureBaseUrl = configurationTestData.images?.secureBaseUrl ?: "",
        genres = genreListTestData.genres ?: emptyList(),
        region = regionTestData.results ?: emptyList(),
        language = languageListTestData,
        posterSize = configurationTestData.images?.posterSizes?.map {
            PosterSize(size = it, isSelected = it == "original")
        } ?: emptyList()
    )

    @Before
    fun setup() {
        savedStateHandle = SavedStateHandle(initialState = mapOf("id" to 0))
        testMovieAppDataManager = TestMovieAppDataManager()
        testDatabaseRepository = TestDatabaseRepository()
        testDetailRepository = TestDetailRepository()
        getPeopleDetail = GetPeopleDetailUseCase(
            detailRepository = testDetailRepository,
            databaseRepository = testDatabaseRepository
        )
        viewModel = PeopleVM(
            savedStateHandle = savedStateHandle,
            getPeopleDetail = getPeopleDetail,
            databaseRepository = testDatabaseRepository
        )
        testMovieAppDataManager.setMovieAppData(movieAppData)
    }

    @Test
    fun peopleScreenLoadingTest() {
        composeTestRule.apply {
            setContent {
                val people by viewModel.people.collectAsStateWithLifecycle()

                PeopleScreen(
                    peopleState = people,
                    goToBack = {},
                    insertFavoritePeople = viewModel::insertPeople,
                    deleteFavoritePeople = viewModel::deletePeople,
                    goToMovie = {},
                    onShowSnackbar = { _, _ -> true },
                    restart = viewModel::restart
                )
            }

            onNodeWithContentDescription(label = "peopleDetailLoading").assertExists().assertIsDisplayed()
        }
    }

    @Test
    fun peopleScreenErrorTest() {
        composeTestRule.apply {
            setContent {
                PeopleScreen(
                    peopleState = PeopleState.Error(Throwable("something wrong...")),
                    goToBack = {},
                    insertFavoritePeople = viewModel::insertPeople,
                    deleteFavoritePeople = viewModel::deletePeople,
                    goToMovie = {},
                    onShowSnackbar = { _, _ -> true },
                    restart = viewModel::restart
                )
            }

            onNodeWithText(text = "통신 실패").assertExists().assertIsDisplayed()
            onNodeWithText(text = "재시도").assertExists().assertIsDisplayed()
            onNodeWithText(text = "돌아가기").assertExists().assertIsDisplayed()
        }
    }

    @Test
    fun peopleScreenSuccessTest() {
        composeTestRule.apply {
            setContent {
                val people by viewModel.people.collectAsStateWithLifecycle()

                PeopleScreen(
                    peopleState = people,
                    goToBack = {},
                    insertFavoritePeople = viewModel::insertPeople,
                    deleteFavoritePeople = viewModel::deletePeople,
                    goToMovie = {},
                    onShowSnackbar = { _, _ -> true },
                    restart = viewModel::restart
                )
            }

            runBlocking {
                testDetailRepository.setPeopleDetail(people = peopleDetailTestData)
                testDetailRepository.setCombineCredits(credits = combineCreditsTestData)
                testDetailRepository.setExternalIds(ids = externalIdsTestData)
                testDatabaseRepository.insertPeople(people = peopleDetailTestData)
            }

            onNodeWithTag(testTag = "titleComponent").assertExists().assertTextEquals(peopleDetailTestData.name!!).assertIsDisplayed()
            onNodeWithContentDescription(label = "peopleImageHorizontalPager").assertExists().assertIsDisplayed()
            onNodeWithContentDescription(label = "peopleName").assertExists().assertTextEquals(peopleDetailTestData.name!!).assertIsDisplayed()
            onNodeWithContentDescription(label = "peoplePlaceOfBirth").assertExists().assertTextEquals(peopleDetailTestData.placeOfBirth!!).assertIsDisplayed()
            onNodeWithContentDescription(label = "facebookId").assertExists().assertIsDisplayed()
            onNodeWithContentDescription(label = "instagramId").assertExists().assertIsDisplayed()
            onNodeWithContentDescription(label = "youtubeId").assertExists().assertIsDisplayed()
            onNodeWithContentDescription(label = "peopleBiography").assertExists().assertTextEquals(peopleDetailTestData.biography!!).assertIsDisplayed()
            peopleDetailTestData.combineCredits?.getRelatedMovie()?.forEach {
                onNodeWithTag(testTag = "${it.posterPath}").assertExists().assertIsDisplayed()
            }
        }
    }

    @Test
    fun insertPeopleTest() = runTest {
        composeTestRule.apply {
            setContent {
                val people by viewModel.people.collectAsStateWithLifecycle()

                PeopleScreen(
                    peopleState = people,
                    goToBack = {},
                    insertFavoritePeople = viewModel::insertPeople,
                    deleteFavoritePeople = viewModel::deletePeople,
                    goToMovie = {},
                    onShowSnackbar = { _, _ -> true },
                    restart = viewModel::restart
                )
            }

            runBlocking {
                testDetailRepository.setPeopleDetail(people = peopleDetailTestData.copy(isFavorite = false))
                testDetailRepository.setCombineCredits(credits = combineCreditsTestData)
                testDetailRepository.setExternalIds(ids = externalIdsTestData)
                testDatabaseRepository.insertPeople(people = People(id = 123))
            }

            onNodeWithTag(testTag = "titleComponent").assertExists().assertTextEquals(peopleDetailTestData.name!!).assertIsDisplayed()
            onNodeWithContentDescription(label = "peopleImageHorizontalPager").assertExists().assertIsDisplayed()
            onNodeWithContentDescription(label = "peopleName").assertExists().assertTextEquals(peopleDetailTestData.name!!).assertIsDisplayed()
            onNodeWithContentDescription(label = "peoplePlaceOfBirth").assertExists().assertTextEquals(peopleDetailTestData.placeOfBirth!!).assertIsDisplayed()
            onNodeWithContentDescription(label = "facebookId").assertExists().assertIsDisplayed()
            onNodeWithContentDescription(label = "instagramId").assertExists().assertIsDisplayed()
            onNodeWithContentDescription(label = "youtubeId").assertExists().assertIsDisplayed()
            onNodeWithContentDescription(label = "peopleBiography").assertExists().assertTextEquals(peopleDetailTestData.biography!!).assertIsDisplayed()
            peopleDetailTestData.combineCredits?.getRelatedMovie()?.forEach {
                onNodeWithTag(testTag = "${it.posterPath}").assertExists().assertIsDisplayed()
            }
            onNodeWithContentDescription(label = "unFavorite").assertIsDisplayed()
            onNodeWithContentDescription(label = "favorite").assertIsNotDisplayed()
            onNodeWithContentDescription(label = "unFavorite").performClick()
            onNodeWithContentDescription(label = "unFavorite").assertIsNotDisplayed()
            onNodeWithContentDescription(label = "favorite").assertIsDisplayed()
            assertEquals(
                testDatabaseRepository.getPeople().first().find { it.id == peopleDetailTestData.id }?.id,
                peopleDetailTestData.id
            )
        }
    }

    @Test
    fun deletePeopleTest() = runTest {
        composeTestRule.apply {
            setContent {
                val people by viewModel.people.collectAsStateWithLifecycle()

                PeopleScreen(
                    peopleState = people,
                    goToBack = {},
                    insertFavoritePeople = viewModel::insertPeople,
                    deleteFavoritePeople = viewModel::deletePeople,
                    goToMovie = {},
                    onShowSnackbar = { _, _ -> true },
                    restart = viewModel::restart
                )
            }

            runBlocking {
                testDetailRepository.setPeopleDetail(people = peopleDetailTestData)
                testDetailRepository.setCombineCredits(credits = combineCreditsTestData)
                testDetailRepository.setExternalIds(ids = externalIdsTestData)
                testDatabaseRepository.insertPeople(people = peopleDetailTestData)
            }

            onNodeWithTag(testTag = "titleComponent").assertExists().assertTextEquals(peopleDetailTestData.name!!).assertIsDisplayed()
            onNodeWithContentDescription(label = "peopleImageHorizontalPager").assertExists().assertIsDisplayed()
            onNodeWithContentDescription(label = "peopleName").assertExists().assertTextEquals(peopleDetailTestData.name!!).assertIsDisplayed()
            onNodeWithContentDescription(label = "peoplePlaceOfBirth").assertExists().assertTextEquals(peopleDetailTestData.placeOfBirth!!).assertIsDisplayed()
            onNodeWithContentDescription(label = "facebookId").assertExists().assertIsDisplayed()
            onNodeWithContentDescription(label = "instagramId").assertExists().assertIsDisplayed()
            onNodeWithContentDescription(label = "youtubeId").assertExists().assertIsDisplayed()
            onNodeWithContentDescription(label = "peopleBiography").assertExists().assertTextEquals(peopleDetailTestData.biography!!).assertIsDisplayed()
            peopleDetailTestData.combineCredits?.getRelatedMovie()?.forEach {
                onNodeWithTag(testTag = "${it.posterPath}").assertExists().assertIsDisplayed()
            }
            onNodeWithContentDescription(label = "unFavorite").assertIsNotDisplayed()
            onNodeWithContentDescription(label = "favorite").assertIsDisplayed()
            onNodeWithContentDescription(label = "favorite").performClick()
            onNodeWithContentDescription(label = "unFavorite").assertIsDisplayed()
            onNodeWithContentDescription(label = "favorite").assertIsNotDisplayed()
            assertEquals(
                testDatabaseRepository.getPeople().first().find { it.id == peopleDetailTestData.id }?.id,
                null
            )
        }
    }
}