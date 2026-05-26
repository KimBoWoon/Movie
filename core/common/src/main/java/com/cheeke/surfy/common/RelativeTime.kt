package com.cheeke.surfy.common

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

interface RelativeClock {
    fun nowInstant(): Instant
}

object SystemRelativeClock : RelativeClock {
    override fun nowInstant(): Instant = Instant.now()
}

// Long (밀리초) → 상대 시간
fun Long.millisToRelativeTime(
    clock: RelativeClock = SystemRelativeClock
): String = runCatching {
    val target = Instant.ofEpochMilli(this)
    target.toRelativeTimeLabel(clock.nowInstant())
}.getOrElse { e ->
    Log.printStackTrace(e)
    ""
}

// Long (초) → 상대 시간
fun Long.secondsToRelativeTime(
    clock: RelativeClock = SystemRelativeClock
): String = runCatching {
    val target = Instant.ofEpochSecond(this)
    target.toRelativeTimeLabel(clock.nowInstant())
}.getOrElse { e ->
    Log.printStackTrace(e)
    ""
}

// String (날짜 문자열) → 상대 시간
fun String.toRelativeTime(
    pattern: String = "uuuu-MM-dd HH:mm:ss",
    clock: RelativeClock = SystemRelativeClock
): String = runCatching {
    val formatter = DateTimeFormatter.ofPattern(pattern)
    val target = runCatching {
        LocalDateTime.parse(this, formatter).toInstant(ZoneOffset.UTC)
    }.getOrElse { e ->
        Log.e("exception -> $e, message -> ${e.message}")
        LocalDate.parse(this, formatter).atStartOfDay().toInstant(ZoneOffset.UTC)
    }

    target.toRelativeTimeLabel(clock.nowInstant())
}.getOrElse { e ->
    Log.printStackTrace(e)
    ""
}

internal fun Instant.toRelativeTimeLabel(now: Instant = SystemRelativeClock.nowInstant()): String {
    val seconds = ChronoUnit.SECONDS.between(this, now)
    val minutes = ChronoUnit.MINUTES.between(this, now)
    val hours = ChronoUnit.HOURS.between(this, now)
    val days = ChronoUnit.DAYS.between(this, now)
    val nowDate = now.atZone(ZoneOffset.UTC).toLocalDate()
    val targetDate = this.atZone(ZoneOffset.UTC).toLocalDate()
    val months = ChronoUnit.MONTHS.between(targetDate, nowDate)
    val years = ChronoUnit.YEARS.between(targetDate, nowDate)

    return when {
        seconds <= 10 -> "방금 전"
        seconds < 60 -> "${seconds}초 전"
        minutes < 60 -> "${minutes}분 전"
        hours < 24 -> "${hours}시간 전"
        days < 7 -> "${days}일 전"
        days < 30 -> "${days / 7}주 전"
        months < 12 -> "${months}개월 전"
        else -> "${years}년 전"
    }
}
//package com.cheeke.surfy.common
//
//import java.time.Instant
//import java.time.LocalDateTime
//import java.time.ZoneOffset
//import java.time.format.DateTimeFormatter
//
//// ─────────────────────────────────────────────
//// Clock
//// ─────────────────────────────────────────────
//
//interface RelativeClock {
//    fun nowSeconds(): Long
//}
//
//object SystemRelativeClock : RelativeClock {
//    override fun nowSeconds(): Long = Instant.now().epochSecond
//}
//
//// ─────────────────────────────────────────────
//// 상수
//// ─────────────────────────────────────────────
//
//private const val MIN_SECONDS = 10L
//private const val MINUTE = 60L
//private const val HOUR = MINUTE * 60    // 3,600
//private const val DAY = HOUR * 24       // 86,400
//private const val WEEK = DAY * 7        // 604,800
//private const val MONTH = DAY * 30      // 2,592,000
//private const val YEAR = MONTH * 12     // 31,536,000
//
//// ─────────────────────────────────────────────
//// Long (밀리초) → 상대 시간
//// ─────────────────────────────────────────────
//
//fun Long.millisToRelativeTime(
//    clock: RelativeClock = SystemRelativeClock
//): String = runCatching {
//    val diff = clock.nowSeconds() - (this / 1000L)
//    diff.toRelativeTimeLabel()
//}.getOrElse { e ->
//    Log.printStackTrace(e)
//    ""
//}
//
//// ─────────────────────────────────────────────
//// Long (초) → 상대 시간
//// ─────────────────────────────────────────────
//
//fun Long.secondsToRelativeTime(
//    clock: RelativeClock = SystemRelativeClock
//): String = runCatching {
//    val diff = clock.nowSeconds() - this
//    diff.toRelativeTimeLabel()
//}.getOrElse { e ->
//    Log.printStackTrace(e)
//    ""
//}
//
//// ─────────────────────────────────────────────
//// String (날짜 문자열) → 상대 시간
//// ─────────────────────────────────────────────
//
//fun String.toRelativeTime(
//    pattern: String = "uuuu-MM-dd HH:mm:ss",
//    clock: RelativeClock = SystemRelativeClock
//): String = runCatching {
//    val epochMilli = LocalDateTime
//        .parse(this, DateTimeFormatter.ofPattern(pattern))
//        .toInstant(ZoneOffset.UTC)
//        .toEpochMilli()
//
//    epochMilli.millisToRelativeTime(clock)
//}.getOrElse { e ->
//    Log.printStackTrace(e)
//    ""
//}
//
//// ─────────────────────────────────────────────
//// 초 단위 diff → 레이블 변환
//// ─────────────────────────────────────────────
//
//internal fun Long.toRelativeTimeLabel(): String = when {
//    this <= MIN_SECONDS -> "방금 전"
//    this < MINUTE -> "${this}초 전"
//    this < HOUR -> "${this / MINUTE}분 전"
//    this < DAY -> "${this / HOUR}시간 전"
//    this < WEEK -> "${this / DAY}일 전"
//    this < MONTH -> "${this / WEEK}주 전"
//    this < YEAR -> "${this / MONTH}개월 전"
//    else -> "${this / YEAR}년 전"
//}
//package com.cheeke.surfy.common.extension
//
//import com.cheeke.surfy.common.Log
//import java.time.Instant
//import java.time.LocalDateTime
//import java.time.ZoneOffset
//import java.time.format.DateTimeFormatter
//
//// ✅ 상수 top-level 분리
//private const val MIN_SECONDS = 10L
//private const val MINUTE = 60L
//private const val HOUR = MINUTE * 60   // 3,600
//private const val DAY = HOUR * 24     // 86,400
//private const val WEEK = DAY * 7       // 604,800
//private const val MONTH = DAY * 30      // 2,592,000
//private const val YEAR = MONTH * 12    // 31,536,000
//
//// ✅ Long (밀리초) → 상대 시간
//fun Long.millisToRelativeTime(): String = runCatching {
//    val now = Instant.now().epochSecond
//    val diff = now - (this / 1000L)  // 기준 초를 밀리초 → 초 변환
//
//    diff.toRelativeTimeLabel()
//}.getOrElse { e ->
//    Log.printStackTrace(e)
//    ""
//}
//
//// ✅ Long (초) → 상대 시간
//fun Long.secondsToRelativeTime(): String = runCatching {
//    val now = Instant.now().epochSecond
//    val diff = now - this
//
//    diff.toRelativeTimeLabel()
//}.getOrElse { e ->
//    Log.printStackTrace(e)
//    ""
//}
//
//// ✅ String (날짜 문자열) → 상대 시간
//fun String.toRelativeTime(
//    pattern: String = "uuuu-MM-dd HH:mm:ss"
//): String = runCatching {
//    val epochMilli = LocalDateTime
//        .parse(this, DateTimeFormatter.ofPattern(pattern))
//        .toInstant(ZoneOffset.UTC)
//        .toEpochMilli()
//
//    epochMilli.millisToRelativeTime()
//}.getOrElse { e ->
//    Log.printStackTrace(e)
//    ""
//}
//
//// ✅ 초 단위 diff → 레이블 변환
//private fun Long.toRelativeTimeLabel(): String = when {
//    this <= MIN_SECONDS -> "방금 전"
//    this < MINUTE -> "${this}초 전"
//    this < HOUR -> "${this / MINUTE}분 전"
//    this < DAY -> "${this / HOUR}시간 전"
//    this < WEEK -> "${this / DAY}일 전"
//    this < MONTH -> "${this / WEEK}주 전"
//    this < YEAR -> "${this / MONTH}개월 전"
//    else -> "${this / YEAR}년 전"
//}