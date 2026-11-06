package com.example.gustinemotiontracker.wear.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.rememberScalingLazyListState
import com.example.gustinemotiontracker.core.model.Quadrant

@Composable
fun QuadrantScreen(
    onQuadrantSelected: (Quadrant) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberScalingLazyListState()
    ScalingLazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            Text(
                text = "Como você está?",
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium
            )
        }
        items(Quadrant.entries.size) { index ->
            val quadrant = Quadrant.entries[index]
            QuadrantButton(
                quadrant = quadrant,
                onClick = { onQuadrantSelected(quadrant) }
            )
        }
    }
}

@Composable
private fun QuadrantButton(
    quadrant: Quadrant,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val background = Color(quadrant.colorArgb)
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth(0.82f)
            .height(48.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor = background),
        content = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = quadrant.label,
                    textAlign = TextAlign.Center,
                    color = background.contentColor()
                )
            }
        }
    )
}

fun Color.contentColor(): Color =
    if (luminance() > 0.6f) Color.Black else Color.White

private fun Color.luminance(): Float =
    0.299f * red + 0.587f * green + 0.114f * blue
