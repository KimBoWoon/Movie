package com.bowoon.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class MovieDetail(
    val adult: Boolean? = null,
    val alternativeTitles: TMDBMovieDetailAlternativeTitles? = null,
    val backdropPath: String? = null,
    val belongsToCollection: TMDBMovieDetailBelongsToCollection? = null,
    val budget: Long? = null,
    val changes: TMDBMovieDetailChanges? = null,
    val credits: TMDBMovieDetailCredits? = null,
    val genres: String? = null,
    val homepage: String? = null,
    val id: Int? = null,
    val images: TMDBMovieDetailImages? = null,
    val imdbId: String? = null,
    val keywords: TMDBMovieDetailKeywords? = null,
//    val lists: Lists? = null,
    val originCountry: List<String>? = null,
    val originalLanguage: String? = null,
    val originalTitle: String? = null,
    val overview: String? = null,
    val popularity: Double? = null,
    val posterPath: String? = null,
    val productionCompanies: List<TMDBMovieDetailProductionCompany>? = null,
    val productionCountries: List<TMDBMovieDetailProductionCountry>? = null,
    val releaseDate: String? = null,
    val releases: TMDBMovieDetailReleases? = null,
    val revenue: Long? = null,
//    val reviews: Reviews? = null,
    val runtime: Int? = null,
    val spokenLanguages: List<TMDBMovieDetailSpokenLanguage>? = null,
    val status: String? = null,
    val tagline: String? = null,
    val title: String? = null,
    val translations: TMDBMovieDetailTranslations? = null,
    val video: Boolean? = null,
    val videos: TMBDMovieDetailVideos? = null,
    val voteAverage: Double? = null,
    val voteCount: Int? = null,
    val similar: TMDBMovieDetailSimilar? = null,
    val certification: String? = null,
    val favoriteMovies: List<MovieDetail>? = null,
    val posterUrl: String? = null,
    val isFavorite: Boolean = false
) : Parcelable