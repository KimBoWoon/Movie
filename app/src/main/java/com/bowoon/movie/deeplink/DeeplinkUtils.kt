package com.bowoon.movie.deeplink

import com.bowoon.common.Log

fun List<String>.parseIntPath(index: Int, defaultValue: Int = -1): Int =
    runCatching {
        this[index].toInt()
    }.getOrElse { e ->
        Log.printStackTrace(tr = e)
        defaultValue
    }

fun List<String>.parseStringPath(index: Int, defaultValue: String = ""): String =
    runCatching {
        this[index]
    }.getOrElse { e ->
        Log.printStackTrace(tr = e)
        defaultValue
    }