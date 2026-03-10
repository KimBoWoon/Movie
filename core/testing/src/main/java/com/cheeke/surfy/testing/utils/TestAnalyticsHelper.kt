package com.cheeke.surfy.testing.utils

import com.bowoon.analytics.AnalyticsEvent
import com.bowoon.analytics.AnalyticsHelper

class TestAnalyticsHelper : AnalyticsHelper {
    private val events = mutableListOf<AnalyticsEvent>()
    override fun logEvent(event: AnalyticsEvent) {
        events.add(event)
    }

    fun hasLogged(event: AnalyticsEvent) = event in events
}