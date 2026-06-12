package com.cheeke.surfy.common

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant
import kotlin.test.assertEquals

class RelativeTimeTest {
    private class FakeClock(private val fixedInstant: Instant) : RelativeClock {
        constructor(nowSeconds: Long) : this(fixedInstant = Instant.ofEpochSecond(nowSeconds))
        override fun nowInstant(): Instant = fixedInstant
    }

    private fun labelOf(secondsAgo: Long): String {
        val baseEpochSecond = 100_000L
        val nowInstant = Instant.ofEpochSecond(baseEpochSecond)
        val targetInstant = Instant.ofEpochSecond(baseEpochSecond - secondsAgo)
        return targetInstant.toRelativeTimeLabel(now = nowInstant)
    }

    @Test
    fun `0초 전은 방금 전이다`() {
        assertEquals("방금 전", labelOf(secondsAgo = 0))
    }

    @Test
    fun `10초 전은 방금 전이다`() {
        assertEquals("방금 전", labelOf(secondsAgo = 10))
    }

    @Test
    fun `11초 전은 초 단위로 표시된다`() {
        assertEquals("11초 전", labelOf(secondsAgo = 11))
    }

    @Test
    fun `59초 전은 초 단위로 표시된다`() {
        assertEquals("59초 전", labelOf(secondsAgo = 59))
    }

    @Test
    fun `60초 전은 1분 전이다`() {
        assertEquals("1분 전", labelOf(secondsAgo = 60))
    }

    @Test
    fun `3599초 전은 59분 전이다`() {
        assertEquals("59분 전", labelOf(secondsAgo = 3_599))
    }

    @Test
    fun `3600초 전은 1시간 전이다`() {
        assertEquals("1시간 전", labelOf(secondsAgo = 3_600))
    }

    @Test
    fun `86399초 전은 23시간 전이다`() {
        assertEquals("23시간 전", labelOf(secondsAgo = 86_399))
    }

    @Test
    fun `86400초 전은 1일 전이다`() {
        assertEquals("1일 전", labelOf(secondsAgo = 86_400))
    }

    @Test
    fun `604799초 전은 6일 전이다`() {
        assertEquals("6일 전", labelOf(secondsAgo = 604_799))
    }

    @Test
    fun `604800초 전은 1주 전이다`() {
        assertEquals("1주 전", labelOf(secondsAgo = 604_800))
    }

    @Test
    fun `2591999초 전은 4주 전이다`() {
        assertEquals("4주 전", labelOf(secondsAgo = 2_591_999))
    }

    @Test
    fun `1개월 전은 1개월 전이다`() {
        val nowInstant = Instant.parse("2024-02-01T00:00:00Z")
        val targetInstant = Instant.parse("2024-01-01T00:00:00Z")
        assertEquals("1개월 전", targetInstant.toRelativeTimeLabel(now = nowInstant))
    }

    @Test
    fun `11개월 전은 11개월 전이다`() {
        val nowInstant = Instant.parse("2024-12-01T00:00:00Z")
        val targetInstant = Instant.parse("2024-01-01T00:00:00Z")
        assertEquals("11개월 전", targetInstant.toRelativeTimeLabel(now = nowInstant))
    }

    @Test
    fun `1년 전은 1년 전이다`() {
        val nowInstant = Instant.parse("2024-01-01T00:00:00Z")
        val targetInstant = Instant.parse("2023-01-01T00:00:00Z")
        assertEquals("1년 전", targetInstant.toRelativeTimeLabel(now = nowInstant))
    }

    @Test
    fun `2년 전은 2년 전이다`() {
        val nowInstant = Instant.parse("2024-01-01T00:00:00Z")
        val targetInstant = Instant.parse("2022-01-01T00:00:00Z")
        assertEquals("2년 전", targetInstant.toRelativeTimeLabel(now = nowInstant))
    }

    @Test
    fun `secondsToRelativeTime - 방금 전`() {
        val clock = FakeClock(nowSeconds = 1_000L)
        assertEquals("방금 전", 995L.secondsToRelativeTime(clock = clock))
    }

    @Test
    fun `secondsToRelativeTime - 분 단위`() {
        val clock = FakeClock(nowSeconds = 1_000L)
        val targetSeconds = 1_000L - 120L
        assertEquals("2분 전", targetSeconds.secondsToRelativeTime(clock = clock))
    }

    @Test
    fun `secondsToRelativeTime - 시간 단위`() {
        val clock = FakeClock(nowSeconds = 10_000L)
        val targetSeconds = 10_000L - 3_600L
        assertEquals("1시간 전", targetSeconds.secondsToRelativeTime(clock = clock))
    }

    @Test
    fun `secondsToRelativeTime - 일 단위`() {
        val clock = FakeClock(nowSeconds = 100_000L)
        val targetSeconds = 100_000L - 86_400L
        assertEquals("1일 전", targetSeconds.secondsToRelativeTime(clock = clock))
    }

    @Test
    fun `secondsToRelativeTime - 주 단위`() {
        val clock = FakeClock(nowSeconds = 700_000L)
        val targetSeconds = 700_000L - 604_800L
        assertEquals("1주 전", targetSeconds.secondsToRelativeTime(clock = clock))
    }

    @Test
    fun `secondsToRelativeTime - 개월 단위`() {
        val nowInstant = Instant.parse("2024-02-01T00:00:00Z")
        val targetInstant = Instant.parse("2024-01-01T00:00:00Z")
        val clock = FakeClock(fixedInstant = nowInstant)
        assertEquals("1개월 전", targetInstant.epochSecond.secondsToRelativeTime(clock = clock))
    }

    @Test
    fun `secondsToRelativeTime - 년 단위`() {
        val nowInstant = Instant.parse("2024-01-01T00:00:00Z")
        val targetInstant = Instant.parse("2023-01-01T00:00:00Z")
        val clock = FakeClock(fixedInstant = nowInstant)
        assertEquals("1년 전", targetInstant.epochSecond.secondsToRelativeTime(clock = clock))
    }

    @Test
    fun `millisToRelativeTime - 밀리초를 초로 변환해서 계산한다`() {
        val clock = FakeClock(nowSeconds = 1_000L)
        val epochMillis = 900_000L  // 900초, diff = 100초 → 1분 전
        assertEquals("1분 전", epochMillis.millisToRelativeTime(clock = clock))
    }

    @Test
    fun `millisToRelativeTime - 방금 전`() {
        val clock = FakeClock(nowSeconds = 1_000L)
        val epochMillis = 999_000L
        assertEquals("방금 전", epochMillis.millisToRelativeTime(clock = clock))
    }

    @Test
    fun `millisToRelativeTime - 시간 단위`() {
        val clock = FakeClock(nowSeconds = 10_000L)
        val epochMillis = (10_000L - 3_600L) * 1_000L
        assertEquals("1시간 전", epochMillis.millisToRelativeTime(clock = clock))
    }

    @Test
    fun `toRelativeTime - 1분 전`() {
        val clock = FakeClock(nowSeconds = 1_704_067_260L)
        assertEquals("1분 전", "2024-01-01 00:00:00".toRelativeTime(clock = clock))
    }

    @Test
    fun `toRelativeTime - 1시간 전`() {
        val clock = FakeClock(nowSeconds = 1_704_070_800L)
        assertEquals("1시간 전", "2024-01-01 00:00:00".toRelativeTime(clock = clock))
    }

    @Test
    fun `toRelativeTime - 1일 전`() {
        val clock = FakeClock(nowSeconds = 1_704_153_600L)
        assertEquals("1일 전", "2024-01-01 00:00:00".toRelativeTime(clock = clock))
    }

    @Test
    fun `toRelativeTime - 잘못된 패턴이면 빈 문자열을 반환한다`() {
        val clock = FakeClock(nowSeconds = 1_000L)
        assertEquals("", "not-a-date".toRelativeTime(clock = clock))
    }

    @Test
    fun `toRelativeTime - 커스텀 패턴`() {
        val clock = FakeClock(nowSeconds = 1_704_067_260L)
        // "2024-01-01" → 00:00:00 UTC = 1704067200, diff = 60초 → 1분 전
        assertEquals(
            expected = "1분 전",
            actual = "2024-01-01".toRelativeTime(
                pattern = "uuuu-MM-dd",
                clock = clock
            )
        )
    }

    @Test
    fun `toRelativeTime - 1개월 전`() {
        val clock = FakeClock(fixedInstant = Instant.parse("2024-02-01T00:00:00Z"))
        assertEquals("1개월 전", "2024-01-01 00:00:00".toRelativeTime(clock = clock))
    }

    @Test
    fun `toRelativeTime - 1년 전`() {
        val clock = FakeClock(fixedInstant = Instant.parse("2024-01-01T00:00:00Z"))
        assertEquals("1년 전", "2023-01-01 00:00:00".toRelativeTime(clock = clock))
    }
}
//package com.cheeke.surfy.common
//
//import org.junit.Assert.assertEquals
//import org.junit.Test
//
//class RelativeTimeTest {
//
//    // ─────────────────────────────────────────
//    // FakeClock
//    // ─────────────────────────────────────────
//
//    private class FakeClock(var nowSeconds: Long) : RelativeClock {
//        override fun nowSeconds() = nowSeconds
//    }
//
//    // ─────────────────────────────────────────
//    // toRelativeTimeLabel — 경계값 전체 검증
//    // ─────────────────────────────────────────
//
//    @Test
//    fun `0초 전은 방금 전이다`() {
//        assertEquals("방금 전", 0L.toRelativeTimeLabel())
//    }
//
//    @Test
//    fun `10초 전은 방금 전이다`() {
//        assertEquals("방금 전", 10L.toRelativeTimeLabel())
//    }
//
//    @Test
//    fun `11초 전은 초 단위로 표시된다`() {
//        assertEquals("11초 전", 11L.toRelativeTimeLabel())
//    }
//
//    @Test
//    fun `59초 전은 초 단위로 표시된다`() {
//        assertEquals("59초 전", 59L.toRelativeTimeLabel())
//    }
//
//    @Test
//    fun `60초 전은 1분 전이다`() {
//        assertEquals("1분 전", 60L.toRelativeTimeLabel())
//    }
//
//    @Test
//    fun `3599초 전은 59분 전이다`() {
//        assertEquals("59분 전", 3_599L.toRelativeTimeLabel())
//    }
//
//    @Test
//    fun `3600초 전은 1시간 전이다`() {
//        assertEquals("1시간 전", 3_600L.toRelativeTimeLabel())
//    }
//
//    @Test
//    fun `86399초 전은 23시간 전이다`() {
//        assertEquals("23시간 전", 86_399L.toRelativeTimeLabel())
//    }
//
//    @Test
//    fun `86400초 전은 1일 전이다`() {
//        assertEquals("1일 전", 86_400L.toRelativeTimeLabel())
//    }
//
//    @Test
//    fun `604799초 전은 6일 전이다`() {
//        assertEquals("6일 전", 604_799L.toRelativeTimeLabel())
//    }
//
//    @Test
//    fun `604800초 전은 1주 전이다`() {
//        assertEquals("1주 전", 604_800L.toRelativeTimeLabel())
//    }
//
//    @Test
//    fun `2591999초 전은 4주 전이다`() {
//        assertEquals("4주 전", 2_591_999L.toRelativeTimeLabel())
//    }
//
//    @Test
//    fun `2592000초 전은 1개월 전이다`() {
//        assertEquals("1개월 전", 2_592_000L.toRelativeTimeLabel())
//    }
//
//    @Test
//    fun `31535999초 전은 11개월 전이다`() {
//        assertEquals("11개월 전", 31_535_999L.toRelativeTimeLabel())
//    }
//
//    @Test
//    fun `31536000초 전은 1년 전이다`() {
//        assertEquals("1년 전", 31_536_000L.toRelativeTimeLabel())
//    }
//
//    @Test
//    fun `63072000초 전은 2년 전이다`() {
//        assertEquals("2년 전", 63_072_000L.toRelativeTimeLabel())
//    }
//
//    // ─────────────────────────────────────────
//    // Long.secondsToRelativeTime
//    // ─────────────────────────────────────────
//
//    @Test
//    fun `secondsToRelativeTime - 방금 전`() {
//        val clock = FakeClock(nowSeconds = 1_000L)
//        assertEquals("방금 전", 995L.secondsToRelativeTime(clock))
//    }
//
//    @Test
//    fun `secondsToRelativeTime - 분 단위`() {
//        val clock = FakeClock(nowSeconds = 1_000L)
//        assertEquals("2분 전", (1_000L - 120L).secondsToRelativeTime(clock))
//    }
//
//    @Test
//    fun `secondsToRelativeTime - 시간 단위`() {
//        val clock = FakeClock(nowSeconds = 10_000L)
//        assertEquals("1시간 전", (10_000L - 3_600L).secondsToRelativeTime(clock))
//    }
//
//    @Test
//    fun `secondsToRelativeTime - 일 단위`() {
//        val clock = FakeClock(nowSeconds = 100_000L)
//        assertEquals("1일 전", (100_000L - 86_400L).secondsToRelativeTime(clock))
//    }
//
//    @Test
//    fun `secondsToRelativeTime - 주 단위`() {
//        val clock = FakeClock(nowSeconds = 700_000L)
//        assertEquals("1주 전", (700_000L - 604_800L).secondsToRelativeTime(clock))
//    }
//
//    @Test
//    fun `secondsToRelativeTime - 개월 단위`() {
//        val clock = FakeClock(nowSeconds = 3_000_000L)
//        assertEquals("1개월 전", (3_000_000L - 2_592_000L).secondsToRelativeTime(clock))
//    }
//
//    @Test
//    fun `secondsToRelativeTime - 년 단위`() {
//        val clock = FakeClock(nowSeconds = 40_000_000L)
//        assertEquals("1년 전", (40_000_000L - 31_536_000L).secondsToRelativeTime(clock))
//    }
//
//    // ─────────────────────────────────────────
//    // Long.millisToRelativeTime
//    // ─────────────────────────────────────────
//
//    @Test
//    fun `millisToRelativeTime - 밀리초를 초로 변환해서 계산한다`() {
//        val clock = FakeClock(nowSeconds = 1_000L)
//        // 900초 = 900_000ms, diff = 100초 → 1분 전
//        assertEquals("1분 전", 900_000L.millisToRelativeTime(clock))
//    }
//
//    @Test
//    fun `millisToRelativeTime - 방금 전`() {
//        val clock = FakeClock(nowSeconds = 1_000L)
//        assertEquals("방금 전", 999_000L.millisToRelativeTime(clock))
//    }
//
//    @Test
//    fun `millisToRelativeTime - 시간 단위`() {
//        val clock = FakeClock(nowSeconds = 10_000L)
//        val epochMillis = (10_000L - 3_600L) * 1_000L
//        assertEquals("1시간 전", epochMillis.millisToRelativeTime(clock))
//    }
//
//    // ─────────────────────────────────────────
//    // String.toRelativeTime
//    // ─────────────────────────────────────────
//
//    @Test
//    fun `toRelativeTime - 1분 전`() {
//        // now = 2024-01-01 00:01:00 UTC = 1704067260
//        val clock = FakeClock(nowSeconds = 1_704_067_260L)
//        // 2024-01-01 00:00:00 UTC = 1704067200
//        assertEquals("1분 전", "2024-01-01 00:00:00".toRelativeTime(clock = clock))
//    }
//
//    @Test
//    fun `toRelativeTime - 1시간 전`() {
//        // now = 2024-01-01 01:00:00 UTC = 1704070800
//        val clock = FakeClock(nowSeconds = 1_704_070_800L)
//        // 2024-01-01 00:00:00 UTC = 1704067200
//        assertEquals("1시간 전", "2024-01-01 00:00:00".toRelativeTime(clock = clock))
//    }
//
//    @Test
//    fun `toRelativeTime - 1일 전`() {
//        // now = 2024-01-02 00:00:00 UTC = 1704153600
//        val clock = FakeClock(nowSeconds = 1_704_153_600L)
//        // 2024-01-01 00:00:00 UTC = 1704067200
//        assertEquals("1일 전", "2024-01-01 00:00:00".toRelativeTime(clock = clock))
//    }
//
//    @Test
//    fun `toRelativeTime - 잘못된 패턴이면 빈 문자열을 반환한다`() {
//        val clock = FakeClock(nowSeconds = 1_000L)
//        assertEquals("", "not-a-date".toRelativeTime(clock = clock))
//    }
//
//    @Test
//    fun `toRelativeTime - 커스텀 패턴`() {
//        // now = 2024-01-01 00:01:00 UTC
//        val clock = FakeClock(nowSeconds = 1_704_067_260L)
//        assertEquals("1분 전", "2024-01-01".toRelativeTime(pattern = "uuuu-MM-dd", clock = clock))
//    }
//}