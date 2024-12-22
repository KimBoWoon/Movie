package com.bowoon.domain

import com.bowoon.data.repository.KmdbRepository
import com.bowoon.data.repository.KobisRepository
import com.bowoon.data.repository.UserDataRepository
import com.bowoon.model.MovieDetail
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import javax.inject.Inject

class GetMovieDetailUseCase @Inject constructor(
    private val kobisRepository: KobisRepository,
    private val kmdbRepository: KmdbRepository,
    private val userDataRepository: UserDataRepository
) {
    operator fun invoke(
        kobisApiKey: String,
        movieCd: String,
        kmdbUrl: String
    ): Flow<MovieDetail> = combine(
        kobisRepository.getMovieInfo(kobisApiKey, movieCd),
        kmdbRepository.getMovieInfo(kmdbUrl),
        userDataRepository.userData.map { it.favoriteMovies }
    ) { kobisMovieInfo, kmdbMovieInfo, favoriteMovies ->
        MovieDetail(
            title = kobisMovieInfo.movieInfoResult?.movieInfo?.movieNm,
            titleEng = kobisMovieInfo.movieInfoResult?.movieInfo?.movieNmEn,
            genre = kmdbMovieInfo.data?.firstOrNull()?.result?.firstOrNull()?.genre,
            plots = kmdbMovieInfo.data?.firstOrNull()?.result?.firstOrNull()?.plots,
            actors = kmdbMovieInfo.data?.firstOrNull()?.result?.firstOrNull()?.actors,
            directors = kmdbMovieInfo.data?.firstOrNull()?.result?.firstOrNull()?.directors,
            rating = kmdbMovieInfo.data?.firstOrNull()?.result?.firstOrNull()?.rating,
            posters = kmdbMovieInfo.data?.firstOrNull()?.result?.firstOrNull()?.getPosterList(),
            stlls = kmdbMovieInfo.data?.firstOrNull()?.result?.firstOrNull()?.getStllList(),
            vods = kmdbMovieInfo.data?.firstOrNull()?.result?.firstOrNull()?.vods,
            staffs = kmdbMovieInfo.data?.firstOrNull()?.result?.firstOrNull()?.staffs,
            openDt = LocalDate.parse(kobisMovieInfo.movieInfoResult?.movieInfo?.openDt, DateTimeFormatter.BASIC_ISO_DATE).toString(),
            runtime = kmdbMovieInfo.data?.firstOrNull()?.result?.firstOrNull()?.runtime,
            salesAcc = kmdbMovieInfo.data?.firstOrNull()?.result?.firstOrNull()?.salesAcc,
            audiAcc = kmdbMovieInfo.data?.firstOrNull()?.result?.firstOrNull()?.audiAcc,
            kobisMovieCd = kobisMovieInfo.movieInfoResult?.movieInfo?.movieCd,
            isFavorite = favoriteMovies.find {
                it.title == kobisMovieInfo.movieInfoResult?.movieInfo?.movieNm &&
                        it.titleEng == kobisMovieInfo.movieInfoResult?.movieInfo?.movieNmEn &&
                        it.openDt == LocalDate.parse(kobisMovieInfo.movieInfoResult?.movieInfo?.openDt, DateTimeFormatter.BASIC_ISO_DATE).toString()
            } != null
        )
    }
}