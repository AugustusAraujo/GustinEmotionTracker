package com.example.gustinemotiontracker.wear

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.gustinemotiontracker.core.model.MoodEntry
import com.example.gustinemotiontracker.core.model.Quadrant
import com.example.gustinemotiontracker.wear.data.MoodSender
import com.example.gustinemotiontracker.wear.ui.ConfirmationScreen
import com.example.gustinemotiontracker.wear.ui.EmotionListScreen
import com.example.gustinemotiontracker.wear.ui.QuadrantScreen
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.Vignette
import androidx.wear.compose.material.VignettePosition

sealed interface Screen {
    data object Quadrants : Screen
    data class Emotions(val quadrant: Quadrant) : Screen
    data class Confirmation(val entry: MoodEntry) : Screen
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {
            WearAppTheme {
                MoodTrackerApp()
            }
        }
    }
}

@Composable
fun WearAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(content = content)
}

@Composable
fun MoodTrackerApp() {
    var screen by remember { mutableStateOf<Screen>(Screen.Quadrants) }
    var moodSender by remember { mutableStateOf<MoodSender?>(null) }
    val context = androidx.compose.ui.platform.LocalContext.current
    if (moodSender == null) {
        moodSender = MoodSender(context)
    }
    val sender = moodSender ?: return

    Scaffold(
        timeText = { TimeText() },
        vignette = { Vignette(vignettePosition = VignettePosition.TopAndBottom) }
    ) {
        when (val current = screen) {
            Screen.Quadrants -> {
                QuadrantScreen(
                    onQuadrantSelected = { screen = Screen.Emotions(it) }
                )
            }

            is Screen.Emotions -> {
                BackHandler { screen = Screen.Quadrants }
                EmotionListScreen(
                    quadrant = current.quadrant,
                    onEmotionSelected = { emotion ->
                        val entry = MoodEntry(
                            timestamp = System.currentTimeMillis(),
                            quadrant = current.quadrant,
                            emotion = emotion
                        )
                        sender.send(entry)
                        screen = Screen.Confirmation(entry)
                    }
                )
            }

            is Screen.Confirmation -> {
                ConfirmationScreen(emotion = current.entry.emotion)
            }
        }
    }
}
