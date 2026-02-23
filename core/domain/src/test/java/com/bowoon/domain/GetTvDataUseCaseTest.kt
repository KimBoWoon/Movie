package com.bowoon.domain

import com.bowoon.model.Tv
import com.bowoon.model.TvEpisode
import com.bowoon.model.TvSeasons
import com.bowoon.testing.repository.TestDatabaseRepository
import com.bowoon.testing.repository.TestDetailRepository
import com.bowoon.testing.repository.TestUserDataRepository
import com.bowoon.testing.utils.MainDispatcherRule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class GetTvDataUseCaseTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private lateinit var detailRepository: TestDetailRepository
    private lateinit var databaseRepository: TestDatabaseRepository
    private lateinit var getTvDetailUseCase: GetTvDetailUseCase
    private lateinit var userDataRepository: TestUserDataRepository
    private val tv = Tv(id = 0)
    private val tvSeasons = TvSeasons(
        id = 0,
        seasonNumber = 0,
        episodes = emptyList()
    )
    private val tvEpisode = TvEpisode(
        id = 0,
        episodeNumber = 0
    )

    @Before
    fun setup() {
        detailRepository = TestDetailRepository()
        databaseRepository = TestDatabaseRepository()
        userDataRepository = TestUserDataRepository()
        getTvDetailUseCase = GetTvDetailUseCase(
            databaseRepository = databaseRepository,
            detailRepository = detailRepository,
            userDataRepository = userDataRepository
        )
    }

    @Test
    fun getTvDetailTest() = runTest {
        detailRepository.setTv(tv)
        detailRepository.setTvSeason(tvSeasons)
        detailRepository.setTvEpisode(tvEpisode)
        databaseRepository.insertTv(tv = tv)

        val result = getTvDetailUseCase(id = 0).first()

        assertEquals(
            expected = result.tv,
            actual = tv
        )
    }

    @Test
    fun getFavoriteTvTest() = runTest {
        detailRepository.setTv(tv)
        detailRepository.setTvSeason(tvSeasons)
        detailRepository.setTvEpisode(tvEpisode)
        databaseRepository.insertTv(tv = Tv(id = 123))

        assertEquals(
            expected = getTvDetailUseCase(id = 0).first().isFavorite,
            actual = false
        )
        databaseRepository.insertTv(tv)
        assertEquals(
            expected = getTvDetailUseCase(id = 0).first().isFavorite,
            actual = true
        )
    }
}