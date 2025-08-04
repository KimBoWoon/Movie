package com.bowoon.people

import androidx.activity.ComponentActivity
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bowoon.model.Favorite
import com.bowoon.model.MovieAppData
import com.bowoon.model.PosterSize
import com.bowoon.model.getRelatedMovie
import com.bowoon.testing.model.configurationTestData
import com.bowoon.testing.model.genreListTestData
import com.bowoon.testing.model.languageListTestData
import com.bowoon.testing.model.peopleDetailTestData
import com.bowoon.testing.model.regionTestData
import com.bowoon.testing.repository.TestDatabaseRepository
import com.bowoon.testing.repository.TestMovieAppDataRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class PeopleScreenTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()
    private lateinit var testMovieAppDataRepository: TestMovieAppDataRepository

    @Before
    fun setup() {
        testMovieAppDataRepository = TestMovieAppDataRepository()
        val movieAppData = MovieAppData(
            secureBaseUrl = configurationTestData.images?.secureBaseUrl ?: "",
            genres = genreListTestData.genres ?: emptyList(),
            region = regionTestData.results ?: emptyList(),
            language = languageListTestData,
            posterSize = configurationTestData.images?.posterSizes?.map {
                PosterSize(size = it, isSelected = it == "original")
            } ?: emptyList()
        )

        testMovieAppDataRepository.setMovieAppData(movieAppData)
    }

    @Test
    fun peopleScreenLoadingTest() {
        composeTestRule.apply {
            setContent {
                PeopleScreen(
                    peopleState = PeopleState.Loading,
                    goToBack = {},
                    insertFavoritePeople = {},
                    deleteFavoritePeople = {},
                    goToMovie = {},
                    onShowSnackbar = { _, _ -> true },
                    restart = {}
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
                    insertFavoritePeople = {},
                    deleteFavoritePeople = {},
                    goToMovie = {},
                    onShowSnackbar = { _, _ -> true },
                    restart = {}
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
            var posterPath = ""

            setContent {
                posterPath = testMovieAppDataRepository.movieAppData.collectAsStateWithLifecycle().value.getImageUrl()

                PeopleScreen(
                    peopleState = PeopleState.Success(peopleDetailTestData),
                    goToBack = {},
                    insertFavoritePeople = {},
                    deleteFavoritePeople = {},
                    goToMovie = {},
                    onShowSnackbar = { _, _ -> true },
                    restart = {}
                )
            }

            onNodeWithTag(testTag = "titleComponent").assertExists().assertTextEquals(peopleDetailTestData.name!!).assertIsDisplayed()
            onNodeWithContentDescription(label = "PeopleImage").assertExists().assertIsDisplayed()
            onNodeWithContentDescription(label = "peopleName").assertExists().assertTextEquals(peopleDetailTestData.name!!).assertIsDisplayed()
            onNodeWithContentDescription(label = "peoplePlaceOfBirth").assertExists().assertTextEquals(peopleDetailTestData.placeOfBirth!!).assertIsDisplayed()
            onNodeWithContentDescription(label = "facebookId").assertExists().assertIsDisplayed()
            onNodeWithContentDescription(label = "instagramId").assertExists().assertIsDisplayed()
            onNodeWithContentDescription(label = "youtubeId").assertExists().assertIsDisplayed()
            onNodeWithContentDescription(label = "peopleBiography").assertExists().assertTextEquals(peopleDetailTestData.biography!!).assertIsDisplayed()
            peopleDetailTestData.combineCredits?.getRelatedMovie()?.forEach {
                onNodeWithTag(testTag = "$posterPath${it.posterPath}").assertExists().assertIsDisplayed()
            }
        }
    }

    @Test
    fun insertPeopleTest() = runTest {
        composeTestRule.apply {
            var posterPath = ""
            val testDatabaseRepository = TestDatabaseRepository()

            setContent {
                posterPath = testMovieAppDataRepository.movieAppData.collectAsStateWithLifecycle().value.getImageUrl()
                var people by remember { mutableStateOf(peopleDetailTestData.copy(isFavorite = false)) }

                PeopleScreen(
                    peopleState = PeopleState.Success(people),
                    goToBack = {},
                    insertFavoritePeople = {
                        backgroundScope.launch(UnconfinedTestDispatcher()) { testDatabaseRepository.insertPeople(it) }
                        people = people.copy(isFavorite = true)
                    },
                    deleteFavoritePeople = {
                        backgroundScope.launch(UnconfinedTestDispatcher()) { testDatabaseRepository.deletePeople(it) }
                        people = people.copy(isFavorite = false)
                    },
                    goToMovie = {},
                    onShowSnackbar = { _, _ -> true },
                    restart = {}
                )
            }

            onNodeWithTag(testTag = "titleComponent").assertExists().assertTextEquals(peopleDetailTestData.name!!).assertIsDisplayed()
            onNodeWithContentDescription(label = "PeopleImage").assertExists().assertIsDisplayed()
            onNodeWithContentDescription(label = "peopleName").assertExists().assertTextEquals(peopleDetailTestData.name!!).assertIsDisplayed()
            onNodeWithContentDescription(label = "peoplePlaceOfBirth").assertExists().assertTextEquals(peopleDetailTestData.placeOfBirth!!).assertIsDisplayed()
            onNodeWithContentDescription(label = "facebookId").assertExists().assertIsDisplayed()
            onNodeWithContentDescription(label = "instagramId").assertExists().assertIsDisplayed()
            onNodeWithContentDescription(label = "youtubeId").assertExists().assertIsDisplayed()
            onNodeWithContentDescription(label = "peopleBiography").assertExists().assertTextEquals(peopleDetailTestData.biography!!).assertIsDisplayed()
            peopleDetailTestData.combineCredits?.getRelatedMovie()?.forEach {
                onNodeWithTag(testTag = "$posterPath${it.posterPath}").assertExists().assertIsDisplayed()
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
            var posterPath = ""
            val testDatabaseRepository = TestDatabaseRepository()

            setContent {
                posterPath = testMovieAppDataRepository.movieAppData.collectAsStateWithLifecycle().value.getImageUrl()
                var people by remember { mutableStateOf(peopleDetailTestData) }

                LaunchedEffect("deleteFavorite") {
                    backgroundScope.launch(UnconfinedTestDispatcher()) {
                        testDatabaseRepository.insertPeople(
                            Favorite(
                                id = peopleDetailTestData.id,
                                title = peopleDetailTestData.name,
                                imagePath = peopleDetailTestData.profilePath
                            )
                        )
                    }
                }

                PeopleScreen(
                    peopleState = PeopleState.Success(people),
                    goToBack = {},
                    insertFavoritePeople = {
                        backgroundScope.launch(UnconfinedTestDispatcher()) { testDatabaseRepository.insertPeople(it) }
                        people = people.copy(isFavorite = true)
                    },
                    deleteFavoritePeople = {
                        backgroundScope.launch(UnconfinedTestDispatcher()) { testDatabaseRepository.deletePeople(it) }
                        people = people.copy(isFavorite = false)
                    },
                    goToMovie = {},
                    onShowSnackbar = { _, _ -> true },
                    restart = {}
                )
            }

            onNodeWithTag(testTag = "titleComponent").assertExists().assertTextEquals(peopleDetailTestData.name!!).assertIsDisplayed()
            onNodeWithContentDescription(label = "PeopleImage").assertExists().assertIsDisplayed()
            onNodeWithContentDescription(label = "peopleName").assertExists().assertTextEquals(peopleDetailTestData.name!!).assertIsDisplayed()
            onNodeWithContentDescription(label = "peoplePlaceOfBirth").assertExists().assertTextEquals(peopleDetailTestData.placeOfBirth!!).assertIsDisplayed()
            onNodeWithContentDescription(label = "facebookId").assertExists().assertIsDisplayed()
            onNodeWithContentDescription(label = "instagramId").assertExists().assertIsDisplayed()
            onNodeWithContentDescription(label = "youtubeId").assertExists().assertIsDisplayed()
            onNodeWithContentDescription(label = "peopleBiography").assertExists().assertTextEquals(peopleDetailTestData.biography!!).assertIsDisplayed()
            peopleDetailTestData.combineCredits?.getRelatedMovie()?.forEach {
                onNodeWithTag(testTag = "$posterPath${it.posterPath}").assertExists().assertIsDisplayed()
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