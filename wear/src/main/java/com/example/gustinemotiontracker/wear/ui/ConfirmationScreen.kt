package com.example.gustinemotiontracker.wear.ui

import androidx.activity.ComponentActivity
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import kotlinx.coroutines.delay

@Composable
fun ConfirmationScreen(
    emotion: String,
    modifier: Modifier = Modifier
) {
    val activity = LocalContext.current as? ComponentActivity
    val scale = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        scale.animateTo(1f, animationSpec = spring(dampingRatio = 0.45f, stiffness = 380f))
        delay(1200)
        activity?.finishAndRemoveTask()
    }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Check,
            contentDescription = null,
            tint = Color(0xFF43A047),
            modifier = Modifier
                .size(56.dp)
                .scale(scale.value)
        )
        Text(
            text = emotion,
            color = MaterialTheme.colors.primary,
            style = MaterialTheme.typography.title3
        )
    }
}
