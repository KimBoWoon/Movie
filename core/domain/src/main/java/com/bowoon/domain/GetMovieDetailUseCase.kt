package com.bowoon.domain

import com.bowoon.data.repository.KmdbRepository
import com.bowoon.data.repository.KobisRepository
import com.bowoon.model.MovieDetail
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetMovieDetailUseCase @Inject constructor(
    private val kobisRepository: KobisRepository,
    private val kmdbRepository: KmdbRepository
) {
    operator fun invoke(
        kobisApiKey: String,
        movieCd: String,
        kmdbUrl: String
    ): Flow<MovieDetail> = combine(
        kobisRepository.getMovieInfo(kobisApiKey, movieCd),
        kmdbRepository.getMovieInfo(kmdbUrl)
    ) { kobisMovieInfo, kmdbMovieInfo ->
        MovieDetail(
            title = kobisMovieInfo.movieInfoResult?.movieInfo?.movieNm,
            titleEng = kobisMovieInfo.movieInfoResult?.movieInfo?.movieNmEn,
            genre = kmdbMovieInfo.data?.firstOrNull()?.result?.firstOrNull()?.genre,
            plots = kmdbMovieInfo.data?.firstOrNull()?.result?.firstOrNull()?.plots,
            rating = kmdbMovieInfo.data?.firstOrNull()?.result?.firstOrNull()?.rating,
            posters = kmdbMovieInfo.data?.firstOrNull()?.result?.firstOrNull()?.getPosterList(),
            stlls = kmdbMovieInfo.data?.firstOrNull()?.result?.firstOrNull()?.getStllList(),
            vods = kmdbMovieInfo.data?.firstOrNull()?.result?.firstOrNull()?.vods,
            staffs = kmdbMovieInfo.data?.firstOrNull()?.result?.firstOrNull()?.staffs,
            repRlsDate = kmdbMovieInfo.data?.firstOrNull()?.result?.firstOrNull()?.repRlsDate,
            runtime = kmdbMovieInfo.data?.firstOrNull()?.result?.firstOrNull()?.runtime,
            salesAcc = kmdbMovieInfo.data?.firstOrNull()?.result?.firstOrNull()?.salesAcc,
            audiAcc = kmdbMovieInfo.data?.firstOrNull()?.result?.firstOrNull()?.audiAcc
        )
    }
}