package com.bowoon.firebase

interface LogHelper {
    fun sendLog(name: String? = null, message: String)
}

class NoOpLogHelper : LogHelper {
    override fun sendLog(name: String?, message: String) {}
}