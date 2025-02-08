package com.bowoon.firebase

import androidx.compose.runtime.staticCompositionLocalOf

const val FIREBASE_LOG_MESSAGE = "{name} -> {message}"

data class FirebaseLog(
    val name: String? = null,
    val message: String
)

val LocalFirebaseLogHelper = staticCompositionLocalOf<LogHelper> {
    NoOpLogHelper()
}