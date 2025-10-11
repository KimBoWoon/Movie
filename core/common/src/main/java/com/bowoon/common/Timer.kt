package com.bowoon.common

import com.bowoon.common.Timer.TimerStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter

class Timer(
    private val startAt: Long,
    private val endAt: Long,
    private val period: Long = 0
) {
    private var current = startAt
    private var nextTickTock = startAt + period
    private val scope = CoroutineScope(context = Dispatchers.IO)
    val timer = MutableSharedFlow<TimerStatus>()

    enum class TimerStatus {
        INIT, START, TICK_TOCK, END
    }

    init {
        check(value = startAt < endAt, lazyMessage = { "start > end" })

        timer.tryEmit(value = TimerStatus.INIT)
    }

    fun start() {
        scope.launch {
            timer.emit(value = TimerStatus.START)

            while (true) {
                if (current >= endAt) {
                    timer.emit(value = TimerStatus.END)
                    break
                } else if (period != 0L && current >= nextTickTock) {
                    nextTickTock += period
                    timer.emit(value = TimerStatus.TICK_TOCK)
                }
                delay(timeMillis = 1000)
                current += 1
            }
        }
    }

    fun reset() {
        timer.tryEmit(value = TimerStatus.INIT)
    }
}

class TimerTemp(
    private val startAt: LocalDateTime,
    private val endAt: LocalDateTime,
    private val period: Long = 0
) {
    private val format = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss")
    private var current = startAt
    private var nextTickTock = startAt.plusSeconds(period)
    private val scope = CoroutineScope(context = Dispatchers.IO)
    val timer = MutableSharedFlow<TimerStatus>()

    init {
        startAt.format(format)
        endAt.format(format)
        check(value = startAt < endAt, lazyMessage = { "start > end" })

        timer.tryEmit(value = TimerStatus.INIT)
    }

    fun start() {
        scope.launch {
            while (true) {
                if (current == endAt) {
                    timer.emit(value = TimerStatus.END)
                    break
                } else if (period != 0L && current == nextTickTock) {
                    nextTickTock = nextTickTock.plusSeconds(period)
                    timer.emit(value = TimerStatus.TICK_TOCK)
                }
                delay(timeMillis = 1000)
                current = current.plusSeconds(1)
            }
        }
    }

    fun reset() {
        timer.tryEmit(value = TimerStatus.INIT)
    }
}