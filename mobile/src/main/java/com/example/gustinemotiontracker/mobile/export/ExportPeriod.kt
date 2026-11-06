package com.example.gustinemotiontracker.mobile.export

import java.time.LocalDate
import java.time.ZoneId

sealed interface ExportPeriod {
    data object Last7Days : ExportPeriod
    data object Last30Days : ExportPeriod
    data class Custom(val startEpochDay: Long, val endEpochDay: Long) : ExportPeriod
}

fun ExportPeriod.toMillisRange(
    today: LocalDate = LocalDate.now(),
    zone: ZoneId = ZoneId.systemDefault()
): LongRange {
    val startDay: LocalDate
    val endDay: LocalDate
    when (this) {
        ExportPeriod.Last7Days -> {
            startDay = today.minusDays(6)
            endDay = today
        }

        ExportPeriod.Last30Days -> {
            startDay = today.minusDays(29)
            endDay = today
        }

        is ExportPeriod.Custom -> {
            startDay = LocalDate.ofEpochDay(startEpochDay)
            endDay = LocalDate.ofEpochDay(endEpochDay)
        }
    }
    val from = startDay.atStartOfDay(zone).toInstant().toEpochMilli()
    val to = endDay.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli() - 1
    return from..to
}
