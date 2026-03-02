package com.bowoon.analytics

import androidx.compose.runtime.staticCompositionLocalOf

val LocalAnalyticsHelper = staticCompositionLocalOf<AnalyticsHelper> {
    NoOpAnalyticsHelper()
}