package com.cheeke.surfy.firebase.api

import androidx.compose.runtime.staticCompositionLocalOf

const val FIREBASE_LOG_MESSAGE = "{name}{message}"

val LocalFirebaseLogHelper = staticCompositionLocalOf<LogHelper> {
    NoOpLogHelper()
}