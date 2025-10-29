package com.bowoon.sync.utils

import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneOffset

internal fun calculateInitialDelay(): Long {
    val currentDate = LocalDateTime.now()
    val dueDate = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0)

    if (dueDate.isBefore(currentDate)) {
        dueDate.plusDays(1)
    }

    return dueDate.toEpochSecond(ZoneOffset.UTC) - currentDate.toEpochSecond(ZoneOffset.UTC)
}