package com.example.gustinemotiontracker.wear.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.rememberScalingLazyListState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import com.example.gustinemotiontracker.core.model.EmotionCatalog
import com.example.gustinemotiontracker.core.model.Quadrant

@Composable
fun EmotionListScreen(
    quadrant: Quadrant,
    onEmotionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberScalingLazyListState()
    val accent = Color(quadrant.colorArgb)
    val emotions = EmotionCatalog.emotionsFor(quadrant)

    ScalingLazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        item {
            Text(
                text = quadrant.label,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium,
                color = accent
            )
        }
        items(emotions.size) { index ->
            val emotion = emotions[index]
            Chip(
                onClick = { onEmotionSelected(emotion) },
                label = {
                    Text(
                        text = emotion,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                colors = ChipDefaults.chipColors(
                    backgroundColor = accent.copy(alpha = 0.25f),
                    contentColor = Color.White
                ),
                icon = {
                    Box(
                        modifier = Modifier
                            .size(ChipDefaults.IconSize)
                            .background(accent, CircleShape)
                    )
                }
            )
        }
    }
}
