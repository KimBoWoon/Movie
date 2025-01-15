package com.bowoon.model.tmdb

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

data class TMDBMovieDetail(
    val adult: Boolean? = null,
    val alternativeTitles: TMDBMovieDetailAlternativeTitles? = null,
    val backdropPath: String? = null,
    val belongsToCollection: TMDBMovieDetailBelongsToCollection? = null,
    val budget: Long? = null,
    val changes: TMDBMovieDetailChanges? = null,
    val credits: TMDBMovieDetailCredits? = null,
    val genres: List<TMDBMovieDetailGenre>? = null,
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
    val similar: TMDBMovieDetailSimilar? = null
)

@Serializable
@Parcelize
data class TMDBMovieDetailAlternativeTitles(
    val titles: List<TMDBMovieDetailTitle>? = null
) : Parcelable

@Serializable
@Parcelize
data class TMDBMovieDetailTitle(
    val iso31661: String? = null,
    val title: String? = null,
    val type: String? = null
) : Parcelable

@Serializable
@Parcelize
data class TMDBMovieDetailBelongsToCollection(
    val backdropPath: String? = null,
    val id: Int? = null,
    val name: String? = null,
    val posterPath: String? = null
) : Parcelable

@Serializable
@Parcelize
data class TMDBMovieDetailChanges(
    val changes: List<TMDBMovieDetailChange>? = null
) : Parcelable

@Serializable
@Parcelize
data class TMDBMovieDetailChange(
    val items: List<TMDBMovieDetailItem>? = null,
    val key: String? = null
) : Parcelable

@Serializable
@Parcelize
data class TMDBMovieDetailItem(
    val action: String? = null,
    val id: String? = null,
    val iso31661: String? = null,
    val iso6391: String? = null,
    val originalValue: List<String>? = null,
    val time: String? = null,
    val value: List<String>? = null
) : Parcelable

@Serializable
@Parcelize
data class TMDBMovieDetailCredits(
    val cast: List<TMDBMovieDetailCast>? = null,
    val crew: List<TMDBMovieDetailCrew>? = null
) : Parcelable

@Serializable
@Parcelize
data class TMDBMovieDetailCast(
    val adult: Boolean? = null,
    val castId: Int? = null,
    val character: String? = null,
    val creditId: String? = null,
    val gender: Int? = null,
    val id: Int? = null,
    val knownForDepartment: String? = null,
    val name: String? = null,
    val order: Int? = null,
    val originalName: String? = null,
    val popularity: Double? = null,
    val profilePath: String? = null
) : Parcelable

@Serializable
@Parcelize
data class TMDBMovieDetailCrew(
    val adult: Boolean? = null,
    val creditId: String? = null,
    val department: String? = null,
    val gender: Int? = null,
    val id: Int? = null,
    val job: String? = null,
    val knownForDepartment: String? = null,
    val name: String? = null,
    val originalName: String? = null,
    val popularity: Double? = null,
    val profilePath: String? = null
) : Parcelable

@Serializable
@Parcelize
data class TMDBMovieDetailGenre(
    val id: Int? = null,
    val name: String? = null
) : Parcelable

@Serializable
@Parcelize
data class TMDBMovieDetailImages(
    val backdrops: List<TMDBMovieDetailImage>? = null,
    val logos: List<TMDBMovieDetailImage>? = null,
    val posters: List<TMDBMovieDetailImage>? = null
) : Parcelable

@Serializable
@Parcelize
data class TMDBMovieDetailImage(
    val aspectRatio: Double? = null,
    val filePath: String? = null,
    val height: Int? = null,
    val iso6391: String? = null,
    val voteAverage: Double? = null,
    val voteCount: Int? = null,
    val width: Int? = null
) : Parcelable

@Serializable
@Parcelize
data class TMDBMovieDetailBackdrop(
    val aspectRatio: Double? = null,
    val filePath: String? = null,
    val height: Int? = null,
    val iso6391: String? = null,
    val voteAverage: Double? = null,
    val voteCount: Int? = null,
    val width: Int? = null
) : Parcelable

@Serializable
@Parcelize
data class TMDBMovieDetailLogo(
    val aspectRatio: Double? = null,
    val filePath: String? = null,
    val height: Int? = null,
    val iso6391: String? = null,
    val voteAverage: Double? = null,
    val voteCount: Int? = null,
    val width: Int? = null
) : Parcelable

@Serializable
@Parcelize
data class TMDBMovieDetailPoster(
    val aspectRatio: Double? = null,
    val filePath: String? = null,
    val height: Int? = null,
    val iso6391: String? = null,
    val voteAverage: Double? = null,
    val voteCount: Int? = null,
    val width: Int? = null
) : Parcelable

@Serializable
@Parcelize
data class TMDBMovieDetailKeywords(
    val keywords: List<TMDBMovieDetailKeyword>? = null
) : Parcelable

@Serializable
@Parcelize
data class TMDBMovieDetailKeyword(
    val id: Int? = null,
    val name: String? = null
) : Parcelable

//    data class Lists(
//        val page: Int? = null,
//        val results: List<Any>? = null,
//        val totalPages: Int? = null,
//        val totalResults: Int? = null
//    )

@Serializable
@Parcelize
data class TMDBMovieDetailProductionCompany(
    val id: Int? = null,
    val logoPath: String? = null,
    val name: String? = null,
    val originCountry: String? = null
) : Parcelable

@Serializable
@Parcelize
data class TMDBMovieDetailProductionCountry(
    val iso31661: String? = null,
    val name: String? = null
) : Parcelable

@Serializable
@Parcelize
data class TMDBMovieDetailReleases(
    val countries: List<TMDBMovieDetailCountry>? = null
) : Parcelable

@Serializable
@Parcelize
data class TMDBMovieDetailCountry(
    val certification: String? = null,
    val descriptors: List<String>? = null,
    val iso31661: String? = null,
    val primary: Boolean? = null,
    val releaseDate: String? = null
) : Parcelable

//    data class Reviews(
//        val page: Int? = null,
//        val results: List<Any>? = null,
//        val totalPages: Int? = null,
//        val totalResults: Int? = null
//    )

@Serializable
@Parcelize
data class TMDBMovieDetailSpokenLanguage(
    val englishName: String? = null,
    val iso6391: String? = null,
    val name: String? = null
) : Parcelable

@Serializable
@Parcelize
data class TMDBMovieDetailTranslations(
    val translations: List<TMDBMovieDetailTranslation>? = null
) : Parcelable

@Serializable
@Parcelize
data class TMDBMovieDetailTranslation(
    val `data`: TMDBMovieDetailData? = null,
    val englishName: String? = null,
    val iso31661: String? = null,
    val iso6391: String? = null,
    val name: String? = null
) : Parcelable

@Serializable
@Parcelize
data class TMDBMovieDetailData(
    val homepage: String? = null,
    val overview: String? = null,
    val runtime: Int? = null,
    val tagline: String? = null,
    val title: String? = null
) : Parcelable

@Serializable
@Parcelize
data class TMBDMovieDetailVideos(
    val results: List<TMDBMovieDetailVideoResult>? = null
) : Parcelable

@Serializable
@Parcelize
data class TMDBMovieDetailVideoResult(
    val id: String? = null,
    val iso31661: String? = null,
    val iso6391: String? = null,
    val key: String? = null,
    val name: String? = null,
    val official: Boolean? = null,
    val publishedAt: String? = null,
    val site: String? = null,
    val size: Int? = null,
    val type: String? = null
) : Parcelable

@Serializable
@Parcelize
data class TMDBMovieDetailSimilar(
    val page: Int? = null,
    val results: List<TMDBMovieDetailSimilarResult>? = null,
    val totalPages: Int? = null,
    val totalResults: Int? = null
) : Parcelable

@Serializable
@Parcelize
data class TMDBMovieDetailSimilarResult(
    val adult: Boolean? = null,
    val backdropPath: String? = null,
    val genreIds: List<Int>? = null,
    val id: Int? = null,
    val originalLanguage: String? = null,
    val originalTitle: String? = null,
    val overview: String? = null,
    val popularity: Double? = null,
    val posterPath: String? = null,
    val releaseDate: String? = null,
    val title: String? = null,
    val video: Boolean? = null,
    val voteAverage: Double? = null,
    val voteCount: Int? = null
) : Parcelable