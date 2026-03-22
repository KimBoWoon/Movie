package com.cheeke.surfy.detail

import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.cheeke.surfy.detail.tv.TvScreen
import com.cheeke.surfy.detail.tv.TvState
import com.cheeke.surfy.detail.tv.TvVM
import com.cheeke.surfy.domain.GetTvDetailUseCase
import com.cheeke.surfy.model.ReviewDataModel
import com.cheeke.surfy.model.Tv
import com.cheeke.surfy.model.TvEpisode
import com.cheeke.surfy.model.TvSeasons
import com.cheeke.surfy.testing.model.tvTestData
import com.cheeke.surfy.testing.repository.TestDatabaseRepository
import com.cheeke.surfy.testing.repository.TestPagingRepository
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class TvScreenTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()
    private lateinit var viewModel: TvVM
    private lateinit var getTvDetailUseCase: GetTvDetailUseCase
    private lateinit var databaseRepository: TestDatabaseRepository
    private lateinit var detailRepository: TestDetailRepository
    private lateinit var pagingRepository: TestPagingRepository

    @Before
    fun setup() {
        databaseRepository = TestDatabaseRepository()
        detailRepository = TestDetailRepository()
        pagingRepository = TestPagingRepository()
        getTvDetailUseCase = GetTvDetailUseCase(
            databaseRepository = databaseRepository,
            detailRepository = detailRepository
        )
        viewModel = TvVM(
            id = 0,
            initialTabIndex = 0,
            getTvDetailUseCase = getTvDetailUseCase,
            databaseRepository = databaseRepository,
            pagingRepository = pagingRepository
        )
    }

    @Test
    fun tvDetailLoadingTest() {
        composeTestRule.apply {
            setContent {
                val tvState by viewModel.tv.collectAsStateWithLifecycle()
                val similarTvs = viewModel.similarTvs.collectAsLazyPagingItems()
                val tvReviews = viewModel.tvReviews.collectAsLazyPagingItems()
                val selectedEpisode by viewModel.selectedEpisode.collectAsStateWithLifecycle()
                val tabIndex by viewModel.tabIndex.collectAsStateWithLifecycle()

                TvScreen(
                    tvState = tvState,
                    similarTvs = similarTvs,
                    tvReviews = tvReviews,
                    selectedEpisode = selectedEpisode,
                    tabIndex = tabIndex,
                    goToTv = {},
                    goToPeople = {},
                    goToBack = {},
                    showEpisodeDetail = viewModel::showEpisodeDetail,
                    hideEpisodeDetail = viewModel::hideEpisodeDetail,
                    onShowSnackbar = { _, _ -> true },
                    updateTabIndex = viewModel::updateTabIndex,
                    insertFavoriteTv = viewModel::insertTv,
                    deleteFavoriteTv = viewModel::deleteTv,
                    restart = viewModel::restart
                )
            }

            onNodeWithContentDescription(label = "tvDetailLoading").assertExists().assertIsDisplayed()
        }
    }

    @Test
    fun tvDetailSuccessTest() = runTest {
        composeTestRule.apply {
            setContent {
                val tvState by viewModel.tv.collectAsStateWithLifecycle()
                val similarTvs = viewModel.similarTvs.collectAsLazyPagingItems()
                val tvReviews = viewModel.tvReviews.collectAsLazyPagingItems()
                val selectedEpisode by viewModel.selectedEpisode.collectAsStateWithLifecycle()
                val tabIndex by viewModel.tabIndex.collectAsStateWithLifecycle()

                TvScreen(
                    tvState = tvState,
                    similarTvs = flowOf(value = PagingData.empty<Tv>()).collectAsLazyPagingItems(),
                    tvReviews = flowOf(value = PagingData.empty<ReviewDataModel>()).collectAsLazyPagingItems(),
                    selectedEpisode = selectedEpisode,
                    tabIndex = tabIndex,
                    goToTv = {},
                    goToPeople = {},
                    goToBack = {},
                    showEpisodeDetail = viewModel::showEpisodeDetail,
                    hideEpisodeDetail = viewModel::hideEpisodeDetail,
                    onShowSnackbar = { _, _ -> true },
                    updateTabIndex = viewModel::updateTabIndex,
                    insertFavoriteTv = viewModel::insertTv,
                    deleteFavoriteTv = viewModel::deleteTv,
                    restart = viewModel::restart
                )
            }

            onNodeWithContentDescription(label = "tvDetailLoading").assertExists().assertIsDisplayed()

            databaseRepository.insertTv(tv = Tv(id = 0, title = "tv_1", posterPath = "/tvImagePath.png"))
            detailRepository.setTvSeason(tvSeasons = TvSeasons())
            detailRepository.setTvEpisode(tvEpisode = TvEpisode())
            detailRepository.setTv(tv = tvTestData)

            onNodeWithContentDescription(label = "favorite").assertExists().assertIsDisplayed()
            onNodeWithTag(testTag = "titleComponent").assertTextEquals(tvTestData.title ?: "").assertIsDisplayed()
            onNodeWithText(text = tvTestData.originalTitle ?: "").assertExists().assertIsDisplayed()
            onNodeWithText(text = tvTestData.overview ?: "").assertExists().assertIsDisplayed()
        }
    }

    @Test
    fun tvDetailErrorTest() = runTest {
        composeTestRule.apply {
            setContent {
                val similarTvs = viewModel.similarTvs.collectAsLazyPagingItems()
                val tvReviews = viewModel.tvReviews.collectAsLazyPagingItems()
                val selectedEpisode by viewModel.selectedEpisode.collectAsStateWithLifecycle()
                val tabIndex by viewModel.tabIndex.collectAsStateWithLifecycle()

                TvScreen(
                    tvState = TvState.Error(throwable = Throwable("something wrong...")),
                    similarTvs = similarTvs,
                    tvReviews = tvReviews,
                    selectedEpisode = selectedEpisode,
                    tabIndex = tabIndex,
                    goToTv = {},
                    goToPeople = {},
                    goToBack = {},
                    showEpisodeDetail = viewModel::showEpisodeDetail,
                    hideEpisodeDetail = viewModel::hideEpisodeDetail,
                    onShowSnackbar = { _, _ -> true },
                    updateTabIndex = viewModel::updateTabIndex,
                    insertFavoriteTv = viewModel::insertTv,
                    deleteFavoriteTv = viewModel::deleteTv,
                    restart = viewModel::restart
                )
            }

            onNodeWithText(text = "something wrong...").assertExists().assertIsDisplayed()
        }
    }
}