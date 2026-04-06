package com.cheeke.surfy.common

import android.util.Log
import com.cheeke.surfy.core.common.BuildConfig

object Log {
    private val IS_SHOWING = BuildConfig.DEBUG
    private const val PREFIX = "surfy_"

    fun i(tag: String, msg: String) {
        if (IS_SHOWING) Log.i("$PREFIX$tag", getMessageWithLineNumber(msg))
    }

    fun i(msg: String) {
        if (IS_SHOWING) Log.i("$PREFIX${tag()}", getMessageWithLineNumber(msg))
    }

    fun v(tag: String, msg: String) {
        if (IS_SHOWING) Log.v("$PREFIX$tag", getMessageWithLineNumber(msg))
    }

    fun v(msg: String) {
        if (IS_SHOWING) Log.v("$PREFIX${tag()}", getMessageWithLineNumber(msg))
    }

    fun d(tag: String, msg: String) {
        if (IS_SHOWING) Log.d("$PREFIX$tag", getMessageWithLineNumber(msg))
    }

    fun d(msg: String) {
        if (IS_SHOWING) Log.d("$PREFIX${tag()}", getMessageWithLineNumber(msg))
    }

    fun w(tag: String, msg: String) {
        if (IS_SHOWING) Log.d("$PREFIX$tag", getMessageWithLineNumber(msg))
    }

    fun w(msg: String) {
        if (IS_SHOWING) Log.w("$PREFIX${tag()}", getMessageWithLineNumber(msg))
    }

    fun e(tag: String, msg: String) {
        if (IS_SHOWING) Log.d("$PREFIX$tag", getMessageWithLineNumber(msg))
    }

    fun e(msg: String) {
        if (IS_SHOWING) Log.e("$PREFIX${tag()}", getMessageWithLineNumber(msg))
    }

    fun printStackTrace(tr: Throwable? = null) {
        if (IS_SHOWING) tr?.printStackTrace()
    }

    private fun getMessageWithLineNumber(msg: String): String =
        Thread.currentThread().stackTrace.let { trace ->
            val firstMatchIndex = trace.indexOfFirst { it.className.equals("com.cheeke.common.Log") && it.fileName.equals("Log.kt") }
            val index = if (firstMatchIndex == -1) return msg else firstMatchIndex + 2

            if (index in trace.indices) {
                "(${trace[index].fileName}:${trace[index].lineNumber}) $msg"
            } else {
                msg
            }
        }

    private fun tag(): String =
        Thread.currentThread().stackTrace.let { trace ->
            val firstMatchIndex = trace.indexOfFirst { it.className.equals("com.cheeke.common.Log") && it.fileName.equals("Log.kt") }
            val index = if (firstMatchIndex == -1) return "LinkNotFound" else firstMatchIndex + 2

            if (index in trace.indices) {
                "(${trace[index].fileName}:${trace[index].lineNumber})"
            } else {
                "LinkNotFound"
            }
        }
}