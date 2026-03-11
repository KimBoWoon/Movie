package com.cheeke.surfy.deeplink

enum class DeeplinkAction(val value: String) {
    GO_TO_HOME(value = "go_to_home"),
    GO_TO_FAVORITE(value = "go_to_favorite"),
    GO_TO_SETTING(value = "go_to_setting"),
    OPEN_FAVORITE_PERSON(value = "open_favorite_person"),
    OPEN_FAVORITE_MOVIE(value = "open_favorite_movie"),
    OPEN_FAVORITE_TV(value = "open_favorite_tv"),
    GO_TO_MOVIE(value = "go_to_movie"),
    GO_TO_PEOPLE(value = "go_to_people"),
    GO_TO_SERIES(value = "go_to_series"),
    GO_TO_SEARCH(value = "go_to_search"),
    SEARCH_TO_MOVIE(value = "search_to_movie"),
    SEARCH_TO_PEOPLE(value = "search_to_people"),
    SEARCH_TO_SERIES(value = "search_to_series"),
    SEARCH_TO_TV(value = "search_to_tv")
}