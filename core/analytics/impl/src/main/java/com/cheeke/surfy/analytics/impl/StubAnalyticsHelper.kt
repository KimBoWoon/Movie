package com.cheeke.surfy.analytics.impl

import android.util.Log
import com.cheeke.surfy.analytics.api.AnalyticsEvent
import com.cheeke.surfy.analytics.api.AnalyticsHelper
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "StubAnalyticsHelper"

@Singleton
internal class StubAnalyticsHelper @Inject constructor() : AnalyticsHelper {
    override fun logEvent(event: AnalyticsEvent) {
        Log.d(TAG, "Received analytics event: $event")
    }
}