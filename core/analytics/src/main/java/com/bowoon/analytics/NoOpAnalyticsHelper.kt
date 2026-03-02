package com.bowoon.analytics

class NoOpAnalyticsHelper : AnalyticsHelper {
    override fun logEvent(event: AnalyticsEvent) = Unit
}