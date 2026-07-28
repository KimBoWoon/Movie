package com.cheeke.surfy.analytics.api

interface AnalyticsHelper {
    fun logEvent(event: AnalyticsEvent)
}