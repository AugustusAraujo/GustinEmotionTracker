package com.example.gustinemotiontracker.mobile.ui

import android.content.Context
import android.content.Intent
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
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gustinemotiontracker.mobile.chart.MoodChartRenderer
import com.example.gustinemotiontracker.mobile.export.ExportPeriod
import com.example.gustinemotiontracker.mobile.export.MoodCsvExporter
import com.example.gustinemotiontracker.mobile.export.toMillisRange
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val dayFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MoodTrackerViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var period by remember { mutableStateOf<ExportPeriod>(ExportPeriod.Last7Days) }
    var count by remember { mutableStateOf<Int?>(null) }
    var exporting by remember { mutableStateOf(false) }

    LaunchedEffect(period) {
        count = viewModel.countIn(period)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = "Exportar CSV") },
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
            Text(
                text = "Período",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))

            PeriodOption(
                label = "Últimos 7 dias",
                selected = period == ExportPeriod.Last7Days,
                onSelect = { period = ExportPeriod.Last7Days }
            )
            PeriodOption(
                label = "Últimos 30 dias",
                selected = period == ExportPeriod.Last30Days,
                onSelect = { period = ExportPeriod.Last30Days }
            )
            PeriodOption(
                label = "Intervalo personalizado",
                selected = period is ExportPeriod.Custom,
                onSelect = {
                    val today = LocalDate.now().toEpochDay()
                    period = ExportPeriod.Custom(
                        startEpochDay = today - 6,
                        endEpochDay = today
                    )
                }
            )

            val custom = period as? ExportPeriod.Custom
            if (custom != null) {
                CustomRangeFields(
                    startEpochDay = custom.startEpochDay,
                    endEpochDay = custom.endEpochDay,
                    onStartSelected = { start ->
                        period = ExportPeriod.Custom(
                            startEpochDay = minOf(start, custom.endEpochDay),
                            endEpochDay = custom.endEpochDay
                        )
                    },
                    onEndSelected = { end ->
                        period = ExportPeriod.Custom(
                            startEpochDay = custom.startEpochDay,
                            endEpochDay = maxOf(end, custom.startEpochDay)
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = when (val c = count) {
                    null -> "Calculando registros…"
                    0 -> "Nenhum registro no período selecionado."
                    else -> "$c registro(s) no período selecionado."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    exporting = true
                    scope.launch {
                        val entries = viewModel.entriesIn(period)
                        val file = MoodCsvExporter.createCsvFile(context, entries)
                        shareFile(context, file, "text/csv", entryCount = entries.size)
                        exporting = false
                    }
                },
                enabled = (count ?: 0) > 0 && !exporting,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = if (exporting) "Exportando…" else "Exportar CSV")
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = {
                    exporting = true
                    scope.launch {
                        val entries = viewModel.entriesIn(period)
                        val file = MoodChartRenderer.createChartFile(
                            context,
                            entries,
                            periodLabel(period)
                        )
                        shareFile(context, file, "image/png", entryCount = entries.size)
                        exporting = false
                    }
                },
                enabled = (count ?: 0) > 0 && !exporting,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Compartilhar gráfico")
            }
        }
    }
}

@Composable
private fun PeriodOption(
    label: String,
    selected: Boolean,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = onSelect)
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun CustomRangeFields(
    startEpochDay: Long,
    endEpochDay: Long,
    onStartSelected: (Long) -> Unit,
    onEndSelected: (Long) -> Unit
) {
    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp)
    ) {
        OutlinedButton(onClick = { showStartPicker = true }) {
            Text(text = "Início: ${formatEpochDay(startEpochDay)}")
        }
        Spacer(modifier = Modifier.width(8.dp))
        OutlinedButton(onClick = { showEndPicker = true }) {
            Text(text = "Fim: ${formatEpochDay(endEpochDay)}")
        }
    }

    if (showStartPicker) {
        DatePickerField(
            label = "Data inicial",
            selectedEpochDay = startEpochDay,
            onDismiss = { showStartPicker = false },
            onConfirm = { day ->
                onStartSelected(day)
                showStartPicker = false
            }
        )
    }
    if (showEndPicker) {
        DatePickerField(
            label = "Data final",
            selectedEpochDay = endEpochDay,
            onDismiss = { showEndPicker = false },
            onConfirm = { day ->
                onEndSelected(day)
                showEndPicker = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerField(
    label: String,
    selectedEpochDay: Long,
    onDismiss: () -> Unit,
    onConfirm: (Long) -> Unit
) {
    val state = rememberDatePickerState(
        initialSelectedDateMillis = LocalDate.ofEpochDay(selectedEpochDay)
            .atStartOfDay(ZoneId.of("UTC")).toInstant().toEpochMilli()
    )
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val millis = state.selectedDateMillis
                    if (millis != null) {
                        onConfirm(millis / 86_400_000L)
                    }
                }
            ) { Text(text = "OK") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(text = "Cancelar") }
        }
    ) {
        DatePicker(state = state, title = { Text(text = label) })
    }
}

private fun formatEpochDay(epochDay: Long): String =
    dayFormat.format(LocalDate.ofEpochDay(epochDay))

private fun periodLabel(period: ExportPeriod): String {
    val range = period.toMillisRange()
    val zone = ZoneId.systemDefault()
    val fmt = DateTimeFormatter.ofPattern("dd/MM")
    val start = fmt.format(Instant.ofEpochMilli(range.first).atZone(zone))
    val end = fmt.format(Instant.ofEpochMilli(range.last).atZone(zone))
    return "$start – $end"
}

private fun shareFile(context: Context, file: java.io.File, mimeType: String, entryCount: Int) {
    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
    )
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = mimeType
        putExtra(Intent.EXTRA_STREAM, uri)
        putExtra(
            Intent.EXTRA_SUBJECT,
            "Gustin Emotion Tracker — Registros de humor"
        )
        putExtra(
            Intent.EXTRA_TEXT,
            "Exportação com $entryCount registro(s) de humor."
        )
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, "Compartilhar"))
}
