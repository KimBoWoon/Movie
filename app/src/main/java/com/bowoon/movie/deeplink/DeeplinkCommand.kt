package com.bowoon.movie.deeplink

sealed interface DeeplinkCommand {
    data object GoToHome : DeeplinkCommand
    data class GoToFavorite(val tabIndex: Int = 0) : DeeplinkCommand
    data object GoToSetting : DeeplinkCommand
    data class OpenFavoritePeople(val id: Int) : DeeplinkCommand
    data class OpenFavoriteMovie(val id: Int) : DeeplinkCommand
    data class GoToMovie(val id: Int, val tabIndex: Int = 0) : DeeplinkCommand
    data class GoToPeople(val id: Int) : DeeplinkCommand
    data class GoToSearch(val query: String, val searchType: String) : DeeplinkCommand
    data class SearchToMovie(val query: String, val searchType: String, val id: Int, val tabIndex: Int = 0) : DeeplinkCommand
    data class SearchToPeople(val query: String, val searchType: String, val id: Int) : DeeplinkCommand
}