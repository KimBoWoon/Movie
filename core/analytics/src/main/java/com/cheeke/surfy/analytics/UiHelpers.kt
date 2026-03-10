package com.cheeke.surfy.analytics

import androidx.compose.runtime.staticCompositionLocalOf

val LocalAnalyticsHelper = staticCompositionLocalOf<AnalyticsHelper> {
    NoOpAnalyticsHelper()
}