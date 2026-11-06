package com.example.gustinemotiontracker.mobile.export

import android.content.Context
import com.example.gustinemotiontracker.core.model.MoodEntry
import java.io.File
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object MoodCsvExporter {

    private const val HEADER = "id,data_hora,timestamp_ms,quadrante,emocao,frequencia_cardiaca"

    private val csvDateTime = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

    fun createCsvFile(context: Context, entries: List<MoodEntry>): File {
        val dir = File(context.cacheDir, "exports").apply { mkdirs() }
        val file = File(dir, "mood_entries_${System.currentTimeMillis()}.csv")
        file.writeText(csvContent(entries), Charsets.UTF_8)
        return file
    }

    fun csvContent(
        entries: List<MoodEntry>,
        zone: ZoneId = ZoneId.systemDefault()
    ): String = buildString {
        appendLine(HEADER)
        entries.forEach { entry ->
            val dataHora = csvDateTime.format(
                Instant.ofEpochMilli(entry.timestamp).atZone(zone)
            )
            val fields = listOf(
                entry.id.toString(),
                dataHora,
                entry.timestamp.toString(),
                entry.quadrant.label,
                entry.emotion,
                entry.heartRate?.toString() ?: ""
            )
            appendLine(fields.joinToString(",") { escape(it) })
        }
    }

    private fun escape(field: String): String =
        if (field.any { it == ',' || it == '"' || it == '\n' || it == '\r' }) {
            "\"${field.replace("\"", "\"\"")}\""
        } else {
            field
        }
}
