package com.bowoon.data.model

import com.bowoon.database.model.NowPlayingMovieEntity
import com.bowoon.database.model.UpComingMovieEntity
import com.bowoon.model.Movie

fun Movie.asNowPlayingMovieEntity(): NowPlayingMovieEntity = NowPlayingMovieEntity(
    id = id ?: -1,
    posterPath = posterPath ?: "",
    title = title,
    releaseDate = releaseDate
)

fun Movie.asUpComingMovieEntity(): UpComingMovieEntity = UpComingMovieEntity(
    id = id ?: -1,
    posterPath = posterPath ?: "",
    title = title,
    releaseDate = releaseDate
)

fun NowPlayingMovieEntity.asExternalModel(): Movie = Movie(
    id = id,
    posterPath = posterPath,
    title = title,
    releaseDate = releaseDate
)

fun UpComingMovieEntity.asExternalModel(): Movie = Movie(
    id = id,
    posterPath = posterPath,
    title = title,
    releaseDate = releaseDate
)