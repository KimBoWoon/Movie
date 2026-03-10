package com.cheeke.surfy.analytics

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import javax.inject.Inject

internal class FirebaseAnalyticsHelper @Inject constructor(
    private val firebaseAnalytics: FirebaseAnalytics
) : AnalyticsHelper {
    override fun logEvent(event: AnalyticsEvent) {
        firebaseAnalytics.logEvent(name = event.type) {
            for (extra in event.extras) {
                // Truncate parameter keys and values according to firebase maximum length values.
                param(
                    key = extra.key.take(n = 40),
                    value = extra.value.take(n = 100),
                )
            }
        }
    }
}