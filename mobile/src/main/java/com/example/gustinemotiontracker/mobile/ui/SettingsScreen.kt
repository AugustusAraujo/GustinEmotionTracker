package com.example.gustinemotiontracker.mobile.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

private val INTERVAL_OPTIONS = listOf(1, 2, 3, 4, 6)
private val DAY_LABELS = listOf("Seg", "Ter", "Qua", "Qui", "Sex", "Sáb", "Dom")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MoodTrackerViewModel = viewModel()
) {
    val config by viewModel.promptConfig.collectAsStateWithLifecycle()
    val syncState by viewModel.promptSyncState.collectAsStateWithLifecycle()
    val entries by viewModel.entries.collectAsStateWithLifecycle()

    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = "Lembretes") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Lembretes no relógio",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Pergunta “Como você está?” de tempos em tempos",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = config.enabled,
                    onCheckedChange = { checked ->
                        viewModel.updatePromptConfig { it.copy(enabled = checked) }
                    }
                )
            }

            if (config.enabled) {
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Frequência",
                    style = MaterialTheme.typography.titleSmall
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    INTERVAL_OPTIONS.forEach { hours ->
                        FilterChip(
                            selected = config.intervalHours == hours,
                            onClick = {
                                viewModel.updatePromptConfig { it.copy(intervalHours = hours) }
                            },
                            label = { Text(text = "${hours}h") }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Janela do dia",
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = "Os lembretes só tocam dentro deste horário",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    OutlinedButton(onClick = { showStartPicker = true }) {
                        Text(text = "Início: ${formatMinutes(config.windowStartMinute)}")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedButton(onClick = { showEndPicker = true }) {
                        Text(text = "Fim: ${formatMinutes(config.windowEndMinute)}")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Dias da semana",
                    style = MaterialTheme.typography.titleSmall
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DAY_LABELS.forEachIndexed { index, label ->
                        val isoDay = index + 1
                        FilterChip(
                            selected = isoDay in config.daysOfWeek,
                            onClick = {
                                viewModel.updatePromptConfig { current ->
                                    val days = current.daysOfWeek.toMutableSet()
                                    if (isoDay in days) days.remove(isoDay) else days.add(isoDay)
                                    if (days.isNotEmpty()) current.copy(daysOfWeek = days) else current
                                }
                            },
                            label = { Text(text = label) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = when (syncState) {
                    true -> "Sincronizado com o relógio"
                    false -> "Relógio offline — sincroniza ao reconectar"
                    null -> if (config.enabled) "Sincronizando…" else ""
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Zona de perigo",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(
                onClick = { showDeleteDialog = true },
                enabled = entries.isNotEmpty(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Apagar histórico")
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(text = "Apagar histórico?") },
            text = {
                Text(
                    text = "Todos os ${entries.size} registro(s) serão removidos " +
                        "permanentemente. Isso não pode ser desfeito."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteAll()
                        showDeleteDialog = false
                    }
                ) {
                    Text(
                        text = "Apagar",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(text = "Cancelar")
                }
            }
        )
    }

    if (showStartPicker) {
        TimePickerField(
            initialMinute = config.windowStartMinute,
            onDismiss = { showStartPicker = false },
            onConfirm = { minute ->
                viewModel.updatePromptConfig { current ->
                    current.copy(
                        windowStartMinute = minute
                            .coerceAtMost((current.windowEndMinute - 60).coerceAtLeast(0))
                    )
                }
                showStartPicker = false
            }
        )
    }
    if (showEndPicker) {
        TimePickerField(
            initialMinute = config.windowEndMinute,
            onDismiss = { showEndPicker = false },
            onConfirm = { minute ->
                viewModel.updatePromptConfig { current ->
                    current.copy(
                        windowEndMinute = minute
                            .coerceAtLeast((current.windowStartMinute + 60).coerceAtMost(23 * 60 + 59))
                    )
                }
                showEndPicker = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerField(
    initialMinute: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    val state = rememberTimePickerState(
        initialHour = initialMinute / 60,
        initialMinute = initialMinute % 60,
        is24Hour = true
    )
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { onConfirm(state.hour * 60 + state.minute) }) {
                Text(text = "OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(text = "Cancelar") }
        },
        text = { TimePicker(state = state) }
    )
}

private fun formatMinutes(totalMinutes: Int): String =
    "%02d:%02d".format(totalMinutes / 60, totalMinutes % 60)
