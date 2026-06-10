package com.cheeke.surfy.firebase

import com.cheeke.surfy.common.Log

interface LogHelper {
    fun sendLog(name: String? = null, message: String)
}

class NoOpLogHelper : LogHelper {
    override fun sendLog(name: String?, message: String) {
        Log.d(tag = name ?: this::class.java.simpleName, msg = message)
    }
}