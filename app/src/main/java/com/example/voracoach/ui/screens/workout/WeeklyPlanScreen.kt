package com.example.voracoach.ui.screens.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.voracoach.R
import com.example.voracoach.model.WorkoutLevel
import com.example.voracoach.model.WorkoutSummary
import com.example.voracoach.ui.components.AppCard
import com.example.voracoach.ui.components.AppCardStyle
import com.example.voracoach.ui.components.AppChip
import com.example.voracoach.ui.components.AppChipStyle
import com.example.voracoach.ui.components.ScreenHeader
import com.example.voracoach.ui.components.StatusBadge
import com.example.voracoach.ui.components.StatusBadgeStyle
import com.example.voracoach.ui.components.VoraOrb
import com.example.voracoach.ui.theme.VoraCoachTheme
import com.example.voracoach.viewmodel.WorkoutCatalogUiState
import com.example.voracoach.viewmodel.WorkoutCatalogViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

// Un día de la tira semanal
private data class WeekDay(val shortName: String, val dayOfMonth: Int, val isToday: Boolean)

// Pantalla "Workout / 02 — Weekly Plan"
@Composable
fun WeeklyPlanRoute(
    onBack: () -> Unit,
    onWorkoutClick: (workoutId: String) -> Unit,
    viewModel: WorkoutCatalogViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    WeeklyPlanScreen(uiState = uiState, onBack = onBack, onWorkoutClick = onWorkoutClick)
}

@Composable
fun WeeklyPlanScreen(
    uiState: WorkoutCatalogUiState,
    onBack: () -> Unit,
    onWorkoutClick: (workoutId: String) -> Unit
) {
    // Las sugerencias para reajustar la semana son de demostración (no hay coach real)
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val demoMessage = stringResource(R.string.demo_only_message)
    val showDemo: () -> Unit = { scope.launch { snackbarHostState.showSnackbar(demoMessage) } }

    Scaffold(
        topBar = {
            ScreenHeader(title = "", onBackClick = onBack, modifier = Modifier.statusBarsPadding())
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column {
                Text(stringResource(R.string.weekly_title), style = MaterialTheme.typography.headlineMedium)
                Text(
                    text = stringResource(R.string.weekly_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            WeekStrip(days = remember { currentWeek() })

            when (uiState) {
                is WorkoutCatalogUiState.Content -> PlanCards(uiState.workouts, onWorkoutClick)
                WorkoutCatalogUiState.Loading -> Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                // Sin entrenamientos (vacío o error sin caché): el resto del plan se sigue viendo
                else -> Text(
                    text = stringResource(R.string.workout_catalog_empty),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            AdjustWeekCard(onSuggestionClick = showDemo)
        }
    }
}

// Lunes a domingo de la semana actual. Se usa Calendar (y no java.time) porque la app
// soporta Android 7 (API 24), donde java.time no está disponible sin configuración extra.
private fun currentWeek(): List<WeekDay> {
    val today = Calendar.getInstance()
    val day = today.clone() as Calendar
    // DAY_OF_WEEK va de domingo (1) a sábado (7); se retrocede hasta el lunes
    val daysSinceMonday = (day.get(Calendar.DAY_OF_WEEK) + 5) % 7
    day.add(Calendar.DAY_OF_MONTH, -daysSinceMonday)
    val nameFormat = SimpleDateFormat("EEE", Locale.ENGLISH)
    return List(7) {
        val weekDay = WeekDay(
            shortName = nameFormat.format(day.time).uppercase(Locale.ENGLISH),
            dayOfMonth = day.get(Calendar.DAY_OF_MONTH),
            isToday = day.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR) &&
                day.get(Calendar.YEAR) == today.get(Calendar.YEAR)
        )
        day.add(Calendar.DAY_OF_MONTH, 1)
        weekDay
    }
}

// Nombre del día dentro de N días ("WEDNESDAY"), para las tarjetas de los próximos entrenamientos
private fun dayNameInDays(days: Int): String {
    val day = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, days) }
    return SimpleDateFormat("EEEE", Locale.ENGLISH).format(day.time).uppercase(Locale.ENGLISH)
}

@Composable
private fun WeekStrip(days: List<WeekDay>) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        days.forEach { day ->
            val textColor = if (day.isToday) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .background(
                        color = if (day.isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background,
                        shape = RoundedCornerShape(24.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 10.dp)
            ) {
                Text(
                    text = day.shortName,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (day.isToday) MaterialTheme.colorScheme.primaryContainer else textColor
                )
                Text(
                    text = day.dayOfMonth.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    color = if (day.isToday) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun PlanCards(workouts: List<WorkoutSummary>, onWorkoutClick: (String) -> Unit) {
    if (workouts.isEmpty()) return
    val today = workouts.first()

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        AppCard(modifier = Modifier.fillMaxWidth(), onClick = { onWorkoutClick(today.id) }) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(R.string.weekly_today_label),
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.weight(1f)
                )
                StatusBadge(text = stringResource(R.string.weekly_ready), style = StatusBadgeStyle.Mint)
            }
            Spacer(Modifier.height(8.dp))
            Text(today.name, style = MaterialTheme.typography.titleMedium)
            Text(
                text = stringResource(R.string.weekly_today_meta, today.durationMinutes, today.exerciseCount, today.level.label()),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Los demás entrenamientos del catálogo, uno por día a partir de mañana
        workouts.drop(1).forEachIndexed { index, workout ->
            AppCard(modifier = Modifier.fillMaxWidth(), onClick = { onWorkoutClick(workout.id) }) {
                Text(
                    text = dayNameInDays(index + 1),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(4.dp))
                Text(workout.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = stringResource(R.string.weekly_upcoming_meta, workout.durationMinutes, workout.exerciseCount),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AdjustWeekCard(onSuggestionClick: () -> Unit) {
    AppCard(modifier = Modifier.fillMaxWidth(), style = AppCardStyle.Dark) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            VoraOrb(diameter = 24.dp)
            Spacer(Modifier.width(10.dp))
            Text(
                text = stringResource(R.string.weekly_adjust_title),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.secondary
            )
        }
        Spacer(Modifier.height(10.dp))
        Text(stringResource(R.string.weekly_adjust_body), style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(12.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AppChip(text = stringResource(R.string.weekly_adjust_tired), style = AppChipStyle.OnDark, onClick = onSuggestionClick)
            AppChip(text = stringResource(R.string.weekly_adjust_move), style = AppChipStyle.OnDark, onClick = onSuggestionClick)
        }
    }
}

@Preview(showBackground = true, heightDp = 900)
@Composable
private fun WeeklyPlanPreview() {
    VoraCoachTheme {
        WeeklyPlanScreen(
            uiState = WorkoutCatalogUiState.Content(
                workouts = listOf(
                    WorkoutSummary("w01", "Fuerza inicial", WorkoutLevel.BEGINNER, 25, 3),
                    WorkoutSummary("w02", "Entrenamiento completo", WorkoutLevel.INTERMEDIATE, 35, 3)
                ),
                isRefreshing = false,
                refreshError = null
            ),
            onBack = {},
            onWorkoutClick = {}
        )
    }
}
