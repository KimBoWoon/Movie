package com.bowoon.common.extension

import com.bowoon.common.Log
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneOffset
import org.threeten.bp.format.DateTimeFormatter

fun formatStringTime(time: String): String = runCatching {
    val minSeconds = 10
    val seconds = 59
    val minute = 60
    val hour = minute * 60
    val day = hour * 24
    val month = day * 30
    val year = month * 12
    val now = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
    val longTime = LocalDateTime.parse(time, DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss")).toEpochSecond(ZoneOffset.UTC)
    val afterTime = now - longTime

    when {
        afterTime <= minSeconds -> "금방"
        afterTime < seconds -> "${afterTime}초 전..."
        afterTime < hour -> "${afterTime / minute}분 전..."
        afterTime < day -> "${afterTime / hour}시간 전..."
        afterTime < month -> "${afterTime / day}일 전..."
        afterTime < year -> "${afterTime / month}개월 전..."
        else -> "${afterTime / year}년 전..."
    }
}.getOrElse { e ->
    Log.printStackTrace(e)
    ""
}

fun formatStringTime(time: Long): String {
    val minSeconds = 10
    val seconds = 59
    val minute = 60
    val hour = minute * 60
    val day = hour * 24
    val month = day * 30
    val year = month * 12
    val now = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
    val longTime = Instant.ofEpochMilli(time).toEpochMilli()
    val afterTime = now - longTime

    return when {
        afterTime <= minSeconds -> "금방"
        afterTime < seconds -> "${afterTime}초 전..."
        afterTime < hour -> "${afterTime / minute}분 전..."
        afterTime < day -> "${afterTime / hour}시간 전..."
        afterTime < month -> "${afterTime / day}일 전..."
        afterTime < year -> "${afterTime / month}개월 전..."
        else -> "${afterTime / year}년 전..."
    }
}

fun String.timeDifference(time: String): String = formatStringTime(time)
fun String.timeDifference(time: Long): String = formatStringTime(time)
fun Long.timeDifference(time: String): String = formatStringTime(time)
fun Long.timeDifference(time: Long): String = formatStringTime(time)