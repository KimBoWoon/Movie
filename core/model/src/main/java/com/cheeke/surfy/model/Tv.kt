package com.cheeke.surfy.model

data class Tv(
    val adult: Boolean? = null,
    val alternativeTitles: AlternativeTitles? = null,
    val backdropPath: String? = null,
    val createdBy: List<TvCreatedBy>? = null,
    val credits: Credits? = null,
    val episodeRunTime: List<Int>? = null,
    override val firstAirDate: String? = null,
    override val genres: List<Genre>? = null,
    val homepage: String? = null,
    override val id: Int? = null,
    val images: Images? = null,
    val inProduction: Boolean? = null,
    val keywords: TvKeywords? = null,
    val languages: List<String>? = null,
    override val lastAirDate: String? = null,
    val lastEpisodeToAir: TvLastEpisodeToAir? = null,
    override val title: String? = null,
    val networks: List<TvNetwork>? = null,
    val nextEpisodeToAir: TvNextEpisodeToAir? = null,
    val numberOfEpisodes: Int? = null,
    val numberOfSeasons: Int? = null,
    val originCountry: List<String>? = null,
    val originalLanguage: String? = null,
    override val originalTitle: String? = null,
    val overview: String? = null,
    val popularity: Double? = null,
    override val posterPath: String? = null,
    val productionCompanies: List<ProductionCompany>? = null,
    val productionCountries: List<ProductionCountry>? = null,
    val seasons: List<TvSeason>? = null,
    val spokenLanguages: List<SpokenLanguage>? = null,
    val status: String? = null,
    override val tagline: String? = null,
    val type: String? = null,
    val videos: Videos? = null,
    override val voteAverage: Float? = null,
    val voteCount: Int? = null,
    val episode: TvSeasons? = null,
    val seasonList: Map<String, TvSeasons>? = null,
    override val releaseDate: String? = null,
    override val certification: String? = null,
    override val runtime: Int? = null,
    override val mediaType: MediaType = MediaType.TV,
    override val isFavorite: Boolean = false
) : Media

data class TvAlternativeTitles(
    val results: List<AlternativeTitle>? = null
)

data class TvCreatedBy(
    val creditId: String? = null,
    val gender: Int? = null,
    val id: Int? = null,
    val name: String? = null,
    val originalName: String? = null,
    val profilePath: String? = null
)

data class TvKeywords(
    val results: List<Keyword>? = null
)

data class TvLastEpisodeToAir(
    val airDate: String? = null,
    val episodeNumber: Int? = null,
    val episodeType: String? = null,
    val id: Int? = null,
    val name: String? = null,
    val overview: String? = null,
    val productionCode: String? = null,
    val runtime: Int? = null,
    val seasonNumber: Int? = null,
    val showId: Int? = null,
    val stillPath: String? = null,
    val voteAverage: Float? = null,
    val voteCount: Int? = null
)

data class TvNetwork(
    val id: Int? = null,
    val logoPath: String? = null,
    val name: String? = null,
    val originCountry: String? = null
)

data class TvNextEpisodeToAir(
    val id: Int? = null,
    val name: String? = null,
    val overview: String? = null,
    val voteAverage: Float? = null,
    val voteCount: Int? = null,
    val airDate: String? = null,
    val episodeNumber: Int? = null,
    val episodeType: String? = null,
    val productionCode: String? = null,
    val runtime: Int? = null,
    val seasonNumber: Int? = null,
    val showId: Int? = null,
    val stillPath: String? = null
)

data class TvSeason(
    val airDate: String? = null,
    val episodeCount: Int? = null,
    val id: Int? = null,
    val name: String? = null,
    val overview: String? = null,
    val posterPath: String? = null,
    val seasonNumber: Int? = null,
    val voteAverage: Float? = null
)