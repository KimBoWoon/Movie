package com.cheeke.surfy.domain

import com.cheeke.surfy.common.toEpochDayOrMax
import com.cheeke.surfy.data.repository.SeriesDetailRepository
import com.cheeke.surfy.model.ImageList
import com.cheeke.surfy.model.Series
import com.cheeke.surfy.model.SeriesPart
import io.reactivex.rxjava3.core.Flowable
import javax.inject.Inject

class GetSeriesDetailUseCase @Inject constructor(
    private val detailRepository: SeriesDetailRepository
) {
    operator fun invoke(id: Int): Flowable<SeriesWithImages> =
        Flowable.combineLatest(
            detailRepository.getData(id = id).map { series ->
                series.copy(
                    parts = series.parts?.sortedWith(
                        comparator = compareBy<SeriesPart> { it.releaseDate.toEpochDayOrMax() }
                            .thenBy { it.title.orEmpty() }
                    )
                )
            }.toFlowable(),
            detailRepository.getMovieSeriesImageList(collectionId = id).toFlowable()
        ) { series, imageList ->
            SeriesWithImages(series = series, imageList = imageList)
        }
}

data class SeriesWithImages(
    val series: Series,
    val imageList: ImageList
)