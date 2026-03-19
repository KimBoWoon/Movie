package com.cheeke.surfy.data.model

import com.cheeke.surfy.database.model.NowPlayingMovieEntity
import com.cheeke.surfy.database.model.UpComingMovieEntity
import com.cheeke.surfy.model.Movie

fun Movie.asNowPlayingMovieEntity(): NowPlayingMovieEntity = NowPlayingMovieEntity(
    id = id ?: -1,
    posterPath = posterPath ?: "",
    title = title,
    releaseDate = releaseDate,
    voteAverage = voteAverage,
    voteCount = voteCount
)

fun Movie.asUpComingMovieEntity(): UpComingMovieEntity = UpComingMovieEntity(
    id = id ?: -1,
    posterPath = posterPath ?: "",
    title = title,
    releaseDate = releaseDate,
    voteAverage = voteAverage,
    voteCount = voteCount
)

fun NowPlayingMovieEntity.asExternalModel(): Movie = Movie(
    id = id,
    posterPath = posterPath,
    title = title,
    releaseDate = releaseDate,
    voteAverage = voteAverage,
    voteCount = voteCount
)

fun UpComingMovieEntity.asExternalModel(): Movie = Movie(
    id = id,
    posterPath = posterPath,
    title = title,
    releaseDate = releaseDate,
    voteAverage = voteAverage,
    voteCount = voteCount
)