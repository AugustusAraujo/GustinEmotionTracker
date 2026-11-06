package com.example.gustinemotiontracker.wear.scheduler

import com.example.gustinemotiontracker.core.model.PromptConfig
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.ZoneId

class PromptSchedulerTest {

    private val zone = ZoneId.systemDefault()

    private fun at(day: String, hour: Int, minute: Int = 0): Long =
        java.time.LocalDate.parse(day).atTime(hour, minute).atZone(zone)
            .toInstant().toEpochMilli()

    private fun millisAt(day: String, hour: Int, minute: Int = 0): Long =
        java.time.LocalDate.parse(day).atTime(hour, minute).atZone(zone)
            .toInstant().toEpochMilli()

    @Test
    fun `antes da janela agenda inicio da janela hoje`() {
        val config = PromptConfig(enabled = true, intervalHours = 2, windowStartMinute = 8 * 60, windowEndMinute = 22 * 60)
        val now = at("2026-11-09", 6)
        assertEquals(millisAt("2026-11-09", 8), nextTriggerMillis(config, now))
    }

    @Test
    fun `dentro da janela agenda proximo ponto da grade`() {
        val config = PromptConfig(enabled = true, intervalHours = 2, windowStartMinute = 8 * 60, windowEndMinute = 22 * 60)
        val now = at("2026-11-09", 9, 30)
        assertEquals(millisAt("2026-11-09", 10), nextTriggerMillis(config, now))
    }

    @Test
    fun `apos a janela agenda inicio da janela amanha`() {
        val config = PromptConfig(enabled = true, intervalHours = 2, windowStartMinute = 8 * 60, windowEndMinute = 22 * 60)
        val now = at("2026-11-09", 23)
        assertEquals(millisAt("2026-11-10", 8), nextTriggerMillis(config, now))
    }

    @Test
    fun `dia nao permitido pula para proximo dia valido`() {
        val config = PromptConfig(
            enabled = true,
            intervalHours = 2,
            windowStartMinute = 8 * 60,
            windowEndMinute = 22 * 60,
            daysOfWeek = setOf(2)
        )
        val now = at("2026-11-09", 9)
        assertEquals(millisAt("2026-11-10", 8), nextTriggerMillis(config, now))
    }

    @Test
    fun `ultimo ponto da janela do dia respeita fim`() {
        val config = PromptConfig(enabled = true, intervalHours = 4, windowStartMinute = 8 * 60, windowEndMinute = 20 * 60)
        val now = at("2026-11-09", 19)
        assertEquals(millisAt("2026-11-09", 20), nextTriggerMillis(config, now))
    }

    @Test
    fun `sem dias validos retorna null`() {
        val config = PromptConfig(
            enabled = true,
            intervalHours = 2,
            windowStartMinute = 8 * 60,
            windowEndMinute = 22 * 60,
            daysOfWeek = emptySet()
        )
        val now = at("2026-11-09", 9)
        assertNull(nextTriggerMillis(config, now))
    }

    @Test
    fun `exatamente no ponto da grade agenda o proximo`() {
        val config = PromptConfig(enabled = true, intervalHours = 2, windowStartMinute = 8 * 60, windowEndMinute = 22 * 60)
        val now = at("2026-11-09", 10)
        assertEquals(millisAt("2026-11-09", 12), nextTriggerMillis(config, now))
    }
}
