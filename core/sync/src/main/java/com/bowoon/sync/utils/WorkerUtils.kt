package com.bowoon.sync.utils

import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.ZonedDateTime

internal fun calculateInitialDelay(): Long {
    val currentDate = LocalDateTime.now()
    var dueDate = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0)
    val systemDefaultZoneOffset = ZoneOffset.from(ZonedDateTime.now())

    if (dueDate.isBefore(currentDate)) {
        dueDate = dueDate.plusDays(1)
    }

    return dueDate.toInstant(systemDefaultZoneOffset).toEpochMilli() - currentDate.toInstant(systemDefaultZoneOffset).toEpochMilli()
}