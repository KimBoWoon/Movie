package com.cheeke.surfy.domain

import com.cheeke.surfy.common.Result
import com.cheeke.surfy.common.asResult
import com.cheeke.surfy.data.repository.TvDetailRepository
import com.cheeke.surfy.model.Tv
import com.cheeke.surfy.model.TvEpisode
import com.cheeke.surfy.model.TvSeason
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class GetTvDetailUseCase @Inject constructor(
    private val detailRepository: TvDetailRepository
) {
    private val episodesCache = MutableStateFlow<Map<String, List<TvEpisode>>>(value = emptyMap())

    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(
        id: Int,
        selectedSeason: Flow<TvSeason?> = flowOf(value = null)
    ): Flow<TvInfo> {
        val seasonState = selectedSeason
            .flatMapLatest { season ->
                if (season == null) {
                    flowOf(value = TvSeasonLoadState.Idle)
                } else {
                    val cached = episodesCache.value[season.name]
                    if (cached != null) {
                        flowOf(value = TvSeasonLoadState.Idle)
                    } else {
                        detailRepository.getTvSeasons(seriesId = id, seasonNumber = season.seasonNumber ?: -1)
                            .asResult()
                            .map { result ->
                                when (result) {
                                    is Result.Loading -> TvSeasonLoadState.Loading(message = "${season.name}을 불러오고 있습니다.")
                                    is Result.Success -> {
                                        episodesCache.update {
                                            it + ((result.data.name ?: "") to (result.data.episodes ?: emptyList()))
                                        }
                                        TvSeasonLoadState.Idle
                                    }
                                    is Result.Error -> TvSeasonLoadState.Error(message = "${season.name}을 불러오지 못했습니다.")
                                }
                            }
                    }
                }
            }

        return combine(
            detailRepository.getData(id = id),
            episodesCache,
            seasonState
        ) { tv, episodesBySeason, currentSeasonState ->
            TvInfo(
                tv = tv,
                episodesBySeason = episodesBySeason,
                seasonLoadState = currentSeasonState
            )
        }
    }
}

data class TvInfo(
    val tv: Tv,
    val episodesBySeason: Map<String, List<TvEpisode>>,
    val seasonLoadState: TvSeasonLoadState
)

sealed interface TvSeasonLoadState {
    data object Idle : TvSeasonLoadState
    data class Loading(val message: String) : TvSeasonLoadState
    data class Error(val message: String) : TvSeasonLoadState
}