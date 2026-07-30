package com.cheeke.surfy.firebase.api

import com.cheeke.surfy.common.Log

internal class NoOpLogHelper : LogHelper {
    override fun sendLog(name: String?, message: String) {
        Log.d(tag = name ?: this::class.java.simpleName, msg = message)
    }
}