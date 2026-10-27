package com.example.voracoach.ui.screens.workout

import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.voracoach.R
import com.example.voracoach.model.Exercise
import com.example.voracoach.model.Workout
import com.example.voracoach.model.WorkoutLevel
import com.example.voracoach.ui.components.SecondaryButton
import com.example.voracoach.ui.theme.VoraCoachTheme
import com.example.voracoach.viewmodel.WorkoutDetailUiState
import com.example.voracoach.viewmodel.WorkoutDetailViewModel

// Opciones para valorar la dificultad. Es un enum (y no una lista de textos) para poder
// guardar la elección con rememberSaveable y comparar sin depender del idioma.
private enum class DifficultyRating(@StringRes val label: Int) {
    GREAT(R.string.complete_rate_great),
    GOOD(R.string.complete_rate_good),
    TOUGH(R.string.complete_rate_tough),
    TOO_EASY(R.string.complete_rate_too_easy)
}

// Pantalla "Workout / 07 — Workout Complete" (demostración navegable).
// Duración y cantidad de ejercicios son las del entrenamiento real; volumen e insight son de ejemplo.
@Composable
fun WorkoutCompleteRoute(
    onDone: () -> Unit,
    viewModel: WorkoutDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    WorkoutCompleteScreen(uiState = uiState, onDone = onDone)
}

@Composable
fun WorkoutCompleteScreen(
    uiState: WorkoutDetailUiState,
    onDone: () -> Unit
) {
    // El botón atrás del sistema hace lo mismo que "Done": volver al catálogo.
    // Sin esto, atrás llevaría de nuevo a Active Workout, un entrenamiento que ya terminó (NAV02).
    BackHandler(onBack = onDone)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when (uiState) {
            is WorkoutDetailUiState.Content -> CompleteContent(uiState.workout, onDone)
            WorkoutDetailUiState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
            // Para llegar acá hay que haber abierto el entrenamiento; si igual falla, se puede salir
            else -> CompleteContent(workout = null, onDone = onDone)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CompleteContent(workout: Workout?, onDone: () -> Unit) {
    var rating by rememberSaveable { mutableStateOf<DifficultyRating?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.complete_label),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.secondary
        )
        Spacer(Modifier.height(32.dp))

        // Orbe con check (versión simple hasta que exista VoraOrb en ui/components)
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(120.dp)
                .background(MaterialTheme.colorScheme.secondary, CircleShape)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(48.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
            ) {
                Icon(Icons.Filled.Check, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
            }
        }
        Spacer(Modifier.height(32.dp))

        Text(text = stringResource(R.string.complete_title), style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.complete_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(24.dp))

        if (workout != null) {
            StatsCard(workout)
            Spacer(Modifier.height(16.dp))
            workout.exercises.firstOrNull()?.let { InsightBox(exerciseName = it.name) }
            Spacer(Modifier.height(24.dp))
        }

        Text(
            text = stringResource(R.string.complete_rate_title),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(12.dp))
        // FlowRow: si los chips no entran en una línea (pantalla angosta), pasan a la siguiente
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            DifficultyRating.entries.forEach { option ->
                RatingChip(
                    text = stringResource(option.label),
                    selected = rating == option,
                    onClick = { rating = option }
                )
            }
        }
        Spacer(Modifier.height(32.dp))

        SecondaryButton(
            text = stringResource(R.string.complete_done),
            onClick = onDone,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        )
    }
}

@Composable
private fun StatsCard(workout: Workout) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(vertical = 20.dp, horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            val total = workout.exercises.size
            Stat(stringResource(R.string.complete_duration_value, workout.durationMinutes), stringResource(R.string.complete_duration_label))
            Stat(stringResource(R.string.complete_exercises_value, total, total), stringResource(R.string.complete_exercises_label))
            Stat(stringResource(R.string.complete_volume_value), stringResource(R.string.complete_volume_label))
        }
    }
}

@Composable
private fun Stat(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, style = MaterialTheme.typography.titleLarge)
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

// Caja menta del insight (cuando exista InsightBanner en ui/components, se reemplaza)
@Composable
private fun InsightBox(exerciseName: String) {
    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.complete_insight_label),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = stringResource(R.string.complete_insight_body, exerciseName),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

// Chip de valoración: borde gris sin elegir, relleno verde oscuro cuando está elegido
@Composable
private fun RatingChip(text: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
        contentColor = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
        border = if (selected) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
        )
    }
}

@Preview(showBackground = true, heightDp = 850)
@Composable
private fun WorkoutCompletePreview() {
    VoraCoachTheme {
        WorkoutCompleteScreen(
            uiState = WorkoutDetailUiState.Content(
                workout = Workout(
                    id = "w01",
                    name = "Fuerza inicial",
                    level = WorkoutLevel.BEGINNER,
                    durationMinutes = 25,
                    description = "",
                    exercises = listOf(Exercise("e01", "Sentadilla", "Piernas", 3, 10, 60, "", ""))
                ),
                isRefreshing = false,
                refreshError = null
            ),
            onDone = {}
        )
    }
}
