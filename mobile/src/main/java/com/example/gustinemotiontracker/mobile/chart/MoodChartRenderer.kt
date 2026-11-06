package com.example.gustinemotiontracker.mobile.chart

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import com.example.gustinemotiontracker.core.model.MoodEntry
import com.example.gustinemotiontracker.core.model.Quadrant
import java.io.File

object MoodChartRenderer {

    private const val WIDTH = 1080
    private const val PADDING = 72f
    private const val BAR_RADIUS = 22f
    private const val MAX_EMOTIONS = 5

    private val backgroundColor = Color.parseColor("#1C1B1F")
    private val textColor = Color.WHITE
    private val mutedColor = Color.argb(179, 255, 255, 255)
    private val dividerColor = Color.argb(51, 255, 255, 255)

    fun createChartFile(context: Context, entries: List<MoodEntry>, periodLabel: String): File {
        val bitmap = render(entries, periodLabel)
        val dir = File(context.cacheDir, "exports").apply { mkdirs() }
        val file = File(dir, "mood_chart_${System.currentTimeMillis()}.png")
        file.outputStream().use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        return file
    }

    fun render(entries: List<MoodEntry>, periodLabel: String): Bitmap {
        val quadrantCounts = Quadrant.entries.associateWith { q ->
            entries.count { it.quadrant == q }
        }
        val topEmotions = entries
            .groupingBy { it.emotion }
            .eachCount()
            .entries
            .sortedWith(compareByDescending<Map.Entry<String, Int>> { it.value }.thenBy { it.key })
            .take(MAX_EMOTIONS)

        val height = computeHeight(topEmotions.size)
        val bitmap = Bitmap.createBitmap(WIDTH, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(backgroundColor)

        val titlePaint = textPaint(bold = true, size = 64f, color = textColor)
        val subtitlePaint = textPaint(size = 40f, color = mutedColor)
        val sectionPaint = textPaint(bold = true, size = 44f, color = textColor)
        val labelPaint = textPaint(size = 34f, color = textColor)
        val countPaint = textPaint(size = 34f, color = mutedColor, alignRight = true)
        val emotionPaint = textPaint(size = 38f, color = textColor)
        val emotionCountPaint = textPaint(size = 38f, color = mutedColor, alignRight = true)
        val footerPaint = textPaint(size = 30f, color = mutedColor, alignCenter = true)
        val barPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        val dividerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = dividerColor }

        val contentWidth = WIDTH - 2 * PADDING

        canvas.drawText("Como você esteve", PADDING, 150f, titlePaint)
        canvas.drawText(
            "$periodLabel · ${entries.size} registro(s)",
            PADDING,
            220f,
            subtitlePaint
        )
        var y = 280f
        canvas.drawLine(PADDING, y, WIDTH - PADDING, y, dividerPaint)

        y = 380f
        canvas.drawText("Frequência por quadrante", PADDING, y, sectionPaint)
        y += 40f
        val maxCount = (quadrantCounts.values.maxOrNull() ?: 0).coerceAtLeast(1)
        val total = entries.size.coerceAtLeast(1)
        Quadrant.entries.forEach { quadrant ->
            val count = quadrantCounts[quadrant] ?: 0
            val rowTop = y
            val labelBaseline = rowTop + 36f
            canvas.drawText(quadrant.label, PADDING, labelBaseline, labelPaint)
            val percent = count * 100 / total
            canvas.drawText(
                "$count ($percent%)",
                WIDTH - PADDING,
                labelBaseline,
                countPaint
            )
            val barTop = rowTop + 60f
            val barWidth = contentWidth * count / maxCount
            barPaint.color = quadrant.colorArgb.toInt()
            canvas.drawRoundRect(
                RectF(PADDING, barTop, PADDING + barWidth, barTop + 44f),
                BAR_RADIUS,
                BAR_RADIUS,
                barPaint
            )
            y = rowTop + 150f
        }

        y += 40f
        canvas.drawLine(PADDING, y, WIDTH - PADDING, y, dividerPaint)
        y += 100f
        canvas.drawText("Top emoções", PADDING, y, sectionPaint)
        y += 60f
        topEmotions.forEach { (emotion, count) ->
            val quadrant = entries.firstOrNull { it.emotion == emotion }?.quadrant
            val dotColor = quadrant?.colorArgb?.toInt() ?: mutedColor
            dotPaint.color = dotColor
            canvas.drawCircle(PADDING + 20f, y, 20f, dotPaint)
            canvas.drawText(emotion, PADDING + 64f, y + 13f, emotionPaint)
            canvas.drawText("$count", WIDTH - PADDING, y + 13f, emotionCountPaint)
            y += 120f
        }

        canvas.drawText(
            "Gustin Emotion Tracker",
            WIDTH / 2f,
            height - 60f,
            footerPaint
        )
        return bitmap
    }

    private fun computeHeight(topEmotionCount: Int): Int {
        var y = 280f
        y += 100f
        y += 4 * 150f
        y += 40f
        y += 100f
        y += 60f
        y += topEmotionCount * 120f
        y += 140f
        return y.toInt().coerceAtLeast(900)
    }

    private fun textPaint(
        bold: Boolean = false,
        size: Float,
        color: Int,
        alignRight: Boolean = false,
        alignCenter: Boolean = false
    ): Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        this.color = color
        this.textSize = size
        this.isFakeBoldText = bold
        textAlign = when {
            alignRight -> Paint.Align.RIGHT
            alignCenter -> Paint.Align.CENTER
            else -> Paint.Align.LEFT
        }
    }
}
