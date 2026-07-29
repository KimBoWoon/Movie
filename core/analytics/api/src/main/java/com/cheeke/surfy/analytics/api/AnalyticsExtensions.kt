package com.cheeke.surfy.analytics.api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import com.google.firebase.analytics.FirebaseAnalytics

fun AnalyticsHelper.logScreenView(screenName: String) {
    logEvent(
        AnalyticsEvent(
            type = AnalyticsEvent.Types.SCREEN_VIEW,
            extras = listOf(
                AnalyticsEvent.Param(key = AnalyticsEvent.ParamKeys.SCREEN_NAME, value = screenName)
            )
        )
    )
}

fun AnalyticsHelper.logSelectContent(contentType: String, id: Int, title: String) {
    logEvent(
        event = AnalyticsEvent(
            type = FirebaseAnalytics.Event.SELECT_CONTENT,
            extras = listOf(
                AnalyticsEvent.Param(
                    key = FirebaseAnalytics.Param.ITEM_ID,
                    value = id.toString()
                ),
                AnalyticsEvent.Param(
                    key = FirebaseAnalytics.Param.CONTENT_TYPE,
                    value = contentType
                ),
                AnalyticsEvent.Param(key = "title", value = title)
            )
        )
    )
}

fun AnalyticsHelper.logSearch(searchType: String, query: String) {
    logEvent(
        event = AnalyticsEvent(
            type = FirebaseAnalytics.Event.SEARCH,
            extras = listOf(
                AnalyticsEvent.Param(key = FirebaseAnalytics.Param.SEARCH_TERM, value = query),
                AnalyticsEvent.Param(key = "search_type", value = searchType)
            )
        )
    )
}

fun AnalyticsHelper.logFavorite(
    isFavorite: Boolean,
    contentType: String,
    id: Int,
    title: String
) {
    when (isFavorite) {
        true -> {
            logEvent(
                event = AnalyticsEvent(
                    type = FirebaseAnalytics.Event.ADD_TO_WISHLIST,
                    extras = listOf(
                        AnalyticsEvent.Param(
                            key = FirebaseAnalytics.Param.CONTENT_TYPE,
                            value = contentType
                        ),
                        AnalyticsEvent.Param(
                            key = FirebaseAnalytics.Param.ITEM_ID,
                            value = id.toString()
                        ),
                        AnalyticsEvent.Param(key = "title", value = title),
                    )
                )
            )
        }
        false -> {
            logEvent(
                event = AnalyticsEvent(
                    type = "remove_to_wishlist",
                    extras = listOf(
                        AnalyticsEvent.Param(
                            key = FirebaseAnalytics.Param.CONTENT_TYPE,
                            value = contentType
                        ),
                        AnalyticsEvent.Param(
                            key = FirebaseAnalytics.Param.ITEM_ID,
                            value = id.toString()
                        ),
                        AnalyticsEvent.Param(key = "title", value = title),
                    )
                )
            )
        }
    }
}

fun AnalyticsHelper.logPlayTrailer(contentType: String, videoId: String) {
    logEvent(
        event = AnalyticsEvent(
            type = "play_trailer",
            extras = listOf(
                AnalyticsEvent.Param(
                    key = FirebaseAnalytics.Param.CONTENT_TYPE,
                    value = contentType
                ),
                AnalyticsEvent.Param(key = FirebaseAnalytics.Param.ITEM_ID, value = videoId)
            )
        )
    )
}

fun AnalyticsHelper.logSelectSeason(tvId: String, tvTitle: String, seasonName: String, seasonNumber: String) {
    logEvent(
        event = AnalyticsEvent(
            type = "select_tv_season",
            extras = listOf(
                AnalyticsEvent.Param(key = FirebaseAnalytics.Param.ITEM_ID, value = tvId),
                AnalyticsEvent.Param(key = "title", value = tvTitle),
                AnalyticsEvent.Param(key = "season_name", value = seasonName),
                AnalyticsEvent.Param(key = "season_number", value = seasonNumber)
            )
        )
    )
}

fun AnalyticsHelper.logSelectEpisode(tvId: String, tvTitle: String, seasonName: String, seasonNumber: String, episodeName: String, episodeNumber: String) {
    logEvent(
        event = AnalyticsEvent(
            type = "select_tv_episode",
            extras = listOf(
                AnalyticsEvent.Param(key = FirebaseAnalytics.Param.ITEM_ID, value = tvId),
                AnalyticsEvent.Param(key = "title", value = tvTitle),
                AnalyticsEvent.Param(key = "season_name", value = seasonName),
                AnalyticsEvent.Param(key = "season_number", value = seasonNumber),
                AnalyticsEvent.Param(key = "episode_name", value = episodeName),
                AnalyticsEvent.Param(key = "episode_number", value = episodeNumber)
            )
        )
    )
}

@Composable
fun TrackScreenViewEvent(
    screenName: String,
    analyticsHelper: AnalyticsHelper = LocalAnalyticsHelper.current,
) = DisposableEffect(key1 = Unit) {
    analyticsHelper.logScreenView(screenName)
    onDispose {}
}
