package com.example.gustinemotiontracker.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gustinemotiontracker.mobile.ui.ExportScreen
import com.example.gustinemotiontracker.mobile.ui.HistoryScreen
import com.example.gustinemotiontracker.mobile.ui.MoodTrackerViewModel
import com.example.gustinemotiontracker.mobile.ui.theme.GustinEmotionTrackerTheme

sealed interface Dest {
    data object History : Dest
    data object Export : Dest
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GustinEmotionTrackerTheme {
                MobileApp()
            }
        }
    }
}

@Composable
fun MobileApp(viewModel: MoodTrackerViewModel = viewModel()) {
    var dest by remember { mutableStateOf<Dest>(Dest.History) }
    val entries by viewModel.entries.collectAsStateWithLifecycle()

    when (dest) {
        Dest.History -> {
            HistoryScreen(
                entries = entries,
                onExportClick = { dest = Dest.Export }
            )
        }

        Dest.Export -> {
            BackHandler { dest = Dest.History }
            ExportScreen(onBack = { dest = Dest.History })
        }
    }
}
