package com.bowoon.analytics

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import com.bowoon.model.Media
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

fun AnalyticsHelper.logSelectContent(contentType: String, media: Media) {
    logEvent(
        event = AnalyticsEvent(
            type = FirebaseAnalytics.Event.SELECT_CONTENT,
            extras = listOf(
                AnalyticsEvent.Param(key = FirebaseAnalytics.Param.ITEM_ID, value = media.id.toString()),
                AnalyticsEvent.Param(key = FirebaseAnalytics.Param.CONTENT_TYPE, value = contentType),
                AnalyticsEvent.Param(key = "title", value = media.title ?: "")
            )
        )
    )
}

fun AnalyticsHelper.logSearch(query: String) {
    logEvent(
        event = AnalyticsEvent(
            type = FirebaseAnalytics.Event.SEARCH,
            extras = listOf(
                AnalyticsEvent.Param(key = FirebaseAnalytics.Param.SEARCH_TERM, value = query)
            )
        )
    )
}

fun AnalyticsHelper.logFavorite(
    isFavorite: Boolean,
    contentType: String,
    media: Media
) {
    when (isFavorite) {
        true -> {
            logEvent(
                event = AnalyticsEvent(
                    type = FirebaseAnalytics.Event.ADD_TO_WISHLIST,
                    extras = listOf(
                        AnalyticsEvent.Param(key = FirebaseAnalytics.Param.CONTENT_TYPE, value = contentType),
                        AnalyticsEvent.Param(key = FirebaseAnalytics.Param.ITEM_ID, value = media.id.toString()),
                        AnalyticsEvent.Param(key = "title", value = media.title ?: ""),
                    )
                )
            )
        }
        false -> {
            logEvent(
                event = AnalyticsEvent(
                    type = "remove_to_wishlist",
                    extras = listOf(
                        AnalyticsEvent.Param(key = FirebaseAnalytics.Param.CONTENT_TYPE, value = contentType),
                        AnalyticsEvent.Param(key = FirebaseAnalytics.Param.ITEM_ID, value = media.id.toString()),
                        AnalyticsEvent.Param(key = "title", value = media.title ?: ""),
                    )
                )
            )
        }
    }
}

fun AnalyticsHelper.logPlayTrailer(videoId: String) {
    logEvent(
        event = AnalyticsEvent(
            type = "play_trailer",
            extras = listOf(
                AnalyticsEvent.Param(key = FirebaseAnalytics.Param.ITEM_ID, value = videoId)
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
