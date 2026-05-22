package com.cheeke.surfy.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class Series(
    val backdropPath: String? = null,
    override val id: Int? = null,
    override val title: String? = null,
    val overview: String? = null,
    val parts: List<SeriesPart>? = null,
    override val originalTitle: String? = null,
    override val posterPath: String? = null,
    override val genres: List<Genre>? = null,
    override val releaseDate: String? = null,
    override val certification: String? = null,
    override val runtime: Int? = null,
    override val tagline: String? = null,
    override val voteAverage: Float? = null,
    override val mediaType: MediaType = MediaType.SERIES,
    override val firstAirDate: String? = null,
    override val lastAirDate: String? = null,
    override val isFavorite: Boolean = false
) : Parcelable, Media

@Serializable
@Parcelize
data class SeriesPart(
    val adult: Boolean? = null,
    val backdropPath: String? = null,
    val genreIds: List<Int>? = null,
    val id: Int? = null,
    val mediaType: String? = null,
    val originalLanguage: String? = null,
    val originalTitle: String? = null,
    val overview: String? = null,
    val popularity: Double? = null,
    val posterPath: String? = null,
    val releaseDate: String? = null,
    val title: String? = null,
    val video: Boolean? = null,
    val voteAverage: Float? = null,
    val voteCount: Int? = null
) : Parcelable