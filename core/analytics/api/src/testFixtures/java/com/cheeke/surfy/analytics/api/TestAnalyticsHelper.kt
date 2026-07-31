package com.cheeke.surfy.analytics.api

class TestAnalyticsHelper : AnalyticsHelper {
    private val events = mutableListOf<AnalyticsEvent>()
    override fun logEvent(event: AnalyticsEvent) {
        events.add(event)
    }

    fun hasLogged(event: AnalyticsEvent) = event in events
}