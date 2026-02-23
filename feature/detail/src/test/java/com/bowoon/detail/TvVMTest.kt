package com.bowoon.detail

import com.bowoon.detail.tv.EpisodesLoadState
import com.bowoon.detail.tv.TvState
import com.bowoon.detail.tv.TvUiState
import com.bowoon.detail.tv.TvVM
import com.bowoon.domain.GetTvDetailUseCase
import com.bowoon.model.Tv
import com.bowoon.model.TvEpisode
import com.bowoon.model.TvSeason
import com.bowoon.model.TvSeasons
import com.bowoon.testing.repository.TestDatabaseRepository
import com.bowoon.testing.repository.TestDetailRepository
import com.bowoon.testing.repository.TestPagingRepository
import com.bowoon.testing.repository.TestUserDataRepository
import com.bowoon.testing.utils.MainDispatcherRule
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class TvVMTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private val testDataBaseRepository = TestDatabaseRepository()
    private val testPagingRepository = TestPagingRepository()
    private val testDetailRepository = TestDetailRepository()
    private val testUserDataRepository = TestUserDataRepository()
    private val getTvDetailUseCase = GetTvDetailUseCase(
        detailRepository = testDetailRepository,
        databaseRepository = testDataBaseRepository,
        userDataRepository = testUserDataRepository
    )
    private lateinit var viewModel: TvVM
    private val tv = Tv(id = 0)
    private val tvEpisode = TvEpisode(
        id = 0,
        episodeNumber = 0
    )
    private val tvSeason = TvSeason(
        id = 0,
        seasonNumber = 1,
        name = "Season1",
        posterPath = "/tvSeason.png"
    )
    private val tvSeasons = TvSeasons(
        id = 0,
        seasonNumber = 0,
        name = "Season1",
        episodes = listOf(tvEpisode)
    )

    @Before
    fun setup() {
        viewModel = TvVM(
            id = 0,
            databaseRepository = testDataBaseRepository,
            pagingRepository = testPagingRepository,
            getTvDetailUseCase = getTvDetailUseCase,
            detailRepository = testDetailRepository
        )
    }

    @Test
    fun loadingTest() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

        assertEquals(
            expected = viewModel.uiState.value,
            actual = TvState.Loading
        )
    }

    @Test
    fun successTest() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

        assertEquals(
            expected = viewModel.uiState.value,
            actual = TvState.Loading
        )
        testDetailRepository.setTv(tv)
        testDetailRepository.setTvSeason(tvSeasons)
        testDetailRepository.setTvEpisode(tvEpisode)
        testDataBaseRepository.insertTv(tv = Tv(id = 124))
        assertEquals(
            expected = viewModel.uiState.value,
            actual = TvState.Success(
                TvUiState(
                    tv = tv,
                    isFavorite = false,
                    autoPlayTrailer = true,
                    seasons = tv.seasons.orEmpty(),
                    episodeState = EpisodesLoadState.Idle,
                    episodesBySeason = emptyMap()
                )
            )
        )
    }

    @Test
    fun insertTvTest() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

        assertEquals(
            expected = viewModel.uiState.value,
            actual = TvState.Loading
        )
        testDetailRepository.setTv(tv)
        testDetailRepository.setTvSeason(tvSeasons)
        testDetailRepository.setTvEpisode(tvEpisode)
        testDataBaseRepository.insertTv(tv = Tv(id = 124))
        assertEquals(
            expected = viewModel.uiState.value,
            actual = TvState.Success(
                TvUiState(
                    tv = tv,
                    isFavorite = false,
                    autoPlayTrailer = true,
                    seasons = tv.seasons.orEmpty(),
                    episodeState = EpisodesLoadState.Idle,
                    episodesBySeason = emptyMap()
                )
            )
        )
        viewModel.insertTv(tv)
        assertEquals(
            expected = viewModel.uiState.value,
            actual = TvState.Success(
                TvUiState(
                    tv = tv,
                    isFavorite = true,
                    autoPlayTrailer = true,
                    seasons = tv.seasons.orEmpty(),
                    episodeState = EpisodesLoadState.Idle,
                    episodesBySeason = emptyMap()
                )
            )
        )
    }

    @Test
    fun deleteTvTest() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

        assertEquals(
            expected = viewModel.uiState.value,
            actual = TvState.Loading
        )
        testDetailRepository.setTv(tv)
        testDetailRepository.setTvSeason(tvSeasons)
        testDetailRepository.setTvEpisode(tvEpisode)
        testDataBaseRepository.insertTv(tv = tv)
        assertEquals(
            expected = viewModel.uiState.value,
            actual = TvState.Success(
                TvUiState(
                    tv = tv,
                    isFavorite = true,
                    autoPlayTrailer = true,
                    seasons = tv.seasons.orEmpty(),
                    episodeState = EpisodesLoadState.Idle,
                    episodesBySeason = emptyMap()
                )
            )
        )
        viewModel.deleteTv(tv)
        assertEquals(
            expected = viewModel.uiState.value,
            actual = TvState.Success(
                TvUiState(
                    tv = tv,
                    isFavorite = false,
                    autoPlayTrailer = true,
                    seasons = tv.seasons.orEmpty(),
                    episodeState = EpisodesLoadState.Idle,
                    episodesBySeason = emptyMap()
                )
            )
        )
    }

    @Test
    fun showEpisodeDetailTest() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.selectedEpisode.collect() }

        assertEquals(
            expected = viewModel.selectedEpisode.value,
            actual = null
        )
        viewModel.showEpisodeDetail(episode = tvEpisode)
        assertEquals(
            expected = viewModel.selectedEpisode.value,
            actual = tvEpisode
        )
    }

    @Test
    fun hideEpisodeDetailTest() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.selectedEpisode.collect() }

        assertEquals(
            expected = viewModel.selectedEpisode.value,
            actual = null
        )
        viewModel.showEpisodeDetail(episode = tvEpisode)
        assertEquals(
            expected = viewModel.selectedEpisode.value,
            actual = tvEpisode
        )
        viewModel.hideEpisodeDetail()
        assertEquals(
            expected = viewModel.selectedEpisode.value,
            actual = null
        )
    }

    @Test
    fun selectedSeasonTest() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

        assertEquals(
            expected = viewModel.uiState.value,
            actual = TvState.Loading
        )
        testDetailRepository.setTv(tv)
        testDetailRepository.setTvSeason(tvSeasons)
        testDetailRepository.setTvEpisode(tvEpisode)
        testDataBaseRepository.insertTv(tv = Tv(id = 124))
        viewModel.onSelectSeason(season = tvSeason)
        assertEquals(
            expected = viewModel.uiState.value,
            actual = TvState.Success(
                TvUiState(
                    tv = tv,
                    isFavorite = false,
                    autoPlayTrailer = true,
                    seasons = tv.seasons.orEmpty(),
                    episodeState = EpisodesLoadState.Idle,
                    episodesBySeason = mapOf("Season1" to listOf(tvEpisode))
                )
            )
        )
    }
}