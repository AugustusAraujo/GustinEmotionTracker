package com.example.gustinemotiontracker.core.model

data class PromptConfig(
    val enabled: Boolean = false,
    val intervalHours: Int = 2,
    val windowStartMinute: Int = 8 * 60,
    val windowEndMinute: Int = 22 * 60,
    val daysOfWeek: Set<Int> = setOf(1, 2, 3, 4, 5, 6, 7),
    val updatedAtMillis: Long = 0L
) {
    companion object {
        const val MIN_INTERVAL_HOURS = 1
        const val MAX_INTERVAL_HOURS = 12
    }
}
