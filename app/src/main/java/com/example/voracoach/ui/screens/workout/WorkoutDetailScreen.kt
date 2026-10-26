package com.example.voracoach.ui.screens.workout

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.voracoach.R
import com.example.voracoach.data.repository.DataError
import com.example.voracoach.model.Exercise
import com.example.voracoach.model.Workout
import com.example.voracoach.model.WorkoutLevel
import com.example.voracoach.ui.components.InsightBanner
import com.example.voracoach.ui.components.ScreenHeader
import com.example.voracoach.ui.components.VoraOrb
import com.example.voracoach.ui.components.SecondaryButton
import com.example.voracoach.ui.components.SecondaryButtonStyle
import com.example.voracoach.ui.theme.VoraCoachTheme
import com.example.voracoach.viewmodel.WorkoutDetailUiState
import com.example.voracoach.viewmodel.WorkoutDetailViewModel
import kotlinx.coroutines.launch

// Pantalla "Workout / 03 — Workout Details". Igual que el catálogo, está partida en
// Route (obtiene el ViewModel y su estado) y Screen (solo dibuja, previsualizable).
@Composable
fun WorkoutDetailRoute(
    onBack: () -> Unit,
    onStartWorkout: (workoutId: String) -> Unit,
    onExerciseClick: (workoutId: String, exerciseId: String) -> Unit,
    viewModel: WorkoutDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    WorkoutDetailScreen(
        uiState = uiState,
        onBack = onBack,
        onRetry = viewModel::retry,
        onStartWorkout = onStartWorkout,
        onExerciseClick = onExerciseClick
    )
}

@Composable
fun WorkoutDetailScreen(
    uiState: WorkoutDetailUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onStartWorkout: (workoutId: String) -> Unit,
    onExerciseClick: (workoutId: String, exerciseId: String) -> Unit = { _, _ -> }
) {
    // "Edit workout" no tiene destino, avisa que es de demostración

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val demoMessage = stringResource(R.string.demo_only_message)

    Scaffold(
        topBar = {
            ScreenHeader(
                title = stringResource(R.string.workout_detail_header),
                onBackClick = onBack,
                modifier = Modifier.statusBarsPadding()
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (uiState) {
                WorkoutDetailUiState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))

                WorkoutDetailUiState.NotFound -> MessageWithRetry(
                    message = stringResource(R.string.workout_detail_not_found),
                    onRetry = null
                )

                is WorkoutDetailUiState.Unavailable -> MessageWithRetry(
                    message = stringResource(R.string.workout_detail_unavailable),
                    detail = uiState.error.message(),
                    onRetry = onRetry
                )

                is WorkoutDetailUiState.Content -> DetailContent(
                    state = uiState,
                    onRetry = onRetry,
                    onStartWorkout = { onStartWorkout(uiState.workout.id) },
                    onExerciseClick = { exerciseId -> onExerciseClick(uiState.workout.id, exerciseId) },
                    onEditWorkout = { scope.launch { snackbarHostState.showSnackbar(demoMessage) } }
                )
            }
        }
    }
}

@Composable
private fun DetailContent(
    state: WorkoutDetailUiState.Content,
    onRetry: () -> Unit,
    onStartWorkout: () -> Unit,
    onExerciseClick: (exerciseId: String) -> Unit,
    onEditWorkout: () -> Unit
) {
    val workout = state.workout
    // "Focus" no viene de la API, se arma con los grupos musculares de los ejercicios, sin repetir
    val focus = workout.exercises.map { it.muscleGroup }.filter { it.isNotBlank() }.distinct()

    // Todo en una LazyColumn
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (state.isRefreshing) {
            item { LinearProgressIndicator(Modifier.fillMaxWidth()) }
        }

        if (state.refreshError != null) {
            item { RefreshErrorBanner(onRetry) }
        }

        item {
            Column {
                Text(text = workout.name, style = MaterialTheme.typography.headlineMedium)
                Spacer(Modifier.height(8.dp))
                Text(
                    text = stringResource(
                        R.string.workout_detail_meta,
                        workout.durationMinutes,
                        workout.level.label(),
                        workout.exercises.size
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (focus.isNotEmpty()) {
                    Text(
                        text = stringResource(R.string.workout_detail_focus, focus.joinToString(" • ")),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        if (workout.description.isNotBlank()) {
            item {
                InsightBanner(
                    title = stringResource(R.string.workout_detail_coach_note),
                    message = workout.description,
                    leading = { VoraOrb(diameter = 16.dp) }
                )
            }
        }

        itemsIndexed(workout.exercises, key = { _, exercise -> exercise.id }) { index, exercise ->
            ExerciseRow(number = index + 1, exercise = exercise, onClick = { onExerciseClick(exercise.id) })
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Spacer(Modifier.height(4.dp))
                SecondaryButton(
                    text = stringResource(R.string.workout_detail_start),
                    onClick = onStartWorkout,
                    modifier = Modifier.fillMaxWidth()
                )
                SecondaryButton(
                    text = stringResource(R.string.workout_detail_edit),
                    onClick = onEditWorkout,
                    style = SecondaryButtonStyle.Outlined,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

// Fila de un ejercicio: número, nombre, series × repeticiones, descanso e instrucciones (RF01).
// Es propia de Workout, por eso vive en esta pantalla y no en ui/components.
@Composable
private fun ExerciseRow(number: Int, exercise: Exercise, onClick: () -> Unit) {
    // Tocar el ejercicio abre su guía (Exercise Details)
    Surface(
        onClick = onClick,
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(36.dp)
                    .background(MaterialTheme.colorScheme.background, RoundedCornerShape(10.dp))
            ) {
                // "%02d" muestra 01, 02... como en Figma
                Text(
                    text = "%02d".format(number),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(text = exercise.name, style = MaterialTheme.typography.titleSmall)
                Text(
                    text = stringResource(
                        R.string.workout_exercise_sets,
                        exercise.sets,
                        exercise.reps,
                        exercise.restSeconds
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (exercise.instructions.isNotBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = exercise.instructions,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun RefreshErrorBanner(onRetry: () -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.errorContainer,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(start = 16.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.workout_detail_refresh_failed),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.weight(1f)
            )
            TextButton(onClick = onRetry) { Text(stringResource(R.string.action_retry)) }
        }
    }
}

// onRetry null: no tiene sentido reintentar (por ejemplo, un 404)
@Composable
private fun MessageWithRetry(message: String, onRetry: (() -> Unit)?, detail: String? = null) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = message, style = MaterialTheme.typography.titleSmall, textAlign = TextAlign.Center)
        if (detail != null) {
            Spacer(Modifier.height(4.dp))
            Text(
                text = detail,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
        if (onRetry != null) {
            Spacer(Modifier.height(12.dp))
            Button(onClick = onRetry) { Text(stringResource(R.string.action_retry)) }
        }
    }
}

private val sampleWorkout = Workout(
    id = "w01",
    name = "Fuerza inicial",
    level = WorkoutLevel.BEGINNER,
    durationMinutes = 25,
    description = "Rutina de ejemplo para el trabajo práctico.",
    exercises = listOf(
        Exercise("e01", "Sentadilla", "Piernas", 3, 10, 60, "Separá los pies y flexioná las rodillas con control.", ""),
        Exercise("e02", "Remo con mancuerna", "Espalda", 3, 10, 60, "Apoyá una mano y llevá la mancuerna hacia el torso.", "")
    )
)

@Preview(showBackground = true, heightDp = 800)
@Composable
private fun WorkoutDetailContentPreview() {
    VoraCoachTheme {
        WorkoutDetailScreen(
            uiState = WorkoutDetailUiState.Content(sampleWorkout, isRefreshing = false, refreshError = null),
            onBack = {}, onRetry = {}, onStartWorkout = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun WorkoutDetailUnavailablePreview() {
    VoraCoachTheme {
        WorkoutDetailScreen(
            uiState = WorkoutDetailUiState.Unavailable(DataError.NoConnection),
            onBack = {}, onRetry = {}, onStartWorkout = {}
        )
    }
}
