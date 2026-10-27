package com.example.voracoach.ui.screens.workout

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.example.voracoach.ui.theme.VoraCoachTheme
import com.example.voracoach.ui.theme.White12
import com.example.voracoach.ui.theme.White60
import com.example.voracoach.viewmodel.WorkoutDetailUiState
import com.example.voracoach.viewmodel.WorkoutDetailViewModel
import kotlinx.coroutines.launch

// Pantalla "Workout / 04 — Pre Workout" (demostración navegable).
// Reutiliza WorkoutDetailViewModel: ya lee el id de la ruta y observa el entrenamiento en Room, así nombre, duración, ejercicios y el consejo del primer ejercicio son datos reales.
// Foco del día y última sesión son de demostración (el mock no tiene historial).
@Composable
fun PreWorkoutRoute(
    onClose: () -> Unit,
    onStartWorkout: (workoutId: String) -> Unit,
    viewModel: WorkoutDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    PreWorkoutScreen(
        uiState = uiState,
        onClose = onClose,
        onRetry = viewModel::retry,
        onStartWorkout = onStartWorkout
    )
}

@Composable
fun PreWorkoutScreen(
    uiState: WorkoutDetailUiState,
    onClose: () -> Unit,
    onRetry: () -> Unit,
    onStartWorkout: (workoutId: String) -> Unit
) {
    // El micrófono no se usa de verdad
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val demoMessage = stringResource(R.string.demo_only_message)
    // Fondo verde oscuro en toda la pantalla, también detrás de la barra de estado
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.inverseSurface)
    ) {
        when (uiState) {
            WorkoutDetailUiState.Loading ->
                CircularProgressIndicator(color = MaterialTheme.colorScheme.secondary, modifier = Modifier.align(Alignment.Center))

            WorkoutDetailUiState.NotFound -> DarkMessage(
                message = stringResource(R.string.workout_detail_not_found),
                onClose = onClose,
                onRetry = null
            )

            is WorkoutDetailUiState.Unavailable -> DarkMessage(
                message = stringResource(R.string.workout_detail_unavailable),
                onClose = onClose,
                onRetry = onRetry
            )

            is WorkoutDetailUiState.Content -> PreWorkoutContent(
                workout = uiState.workout,
                onClose = onClose,
                onMicClick = { scope.launch { snackbarHostState.showSnackbar(demoMessage) } },
                onStartWorkout = { onStartWorkout(uiState.workout.id) }
            )
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
        )
    }
}

@Composable
private fun PreWorkoutContent(
    workout: Workout,
    onClose: () -> Unit,
    onMicClick: () -> Unit,
    onStartWorkout: () -> Unit
) {
    val firstExercise = workout.exercises.firstOrNull()

    // verticalScroll: en pantallas chicas el contenido se desplaza en vez de cortarse (UI03)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(R.string.pre_workout_get_ready),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onClose) {
                Icon(Icons.Filled.Close, contentDescription = stringResource(R.string.action_close), tint = MaterialTheme.colorScheme.inverseOnSurface)
            }
        }

        Text(
            text = workout.name,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.inverseOnSurface
        )
        Text(
            text = stringResource(R.string.pre_workout_meta, workout.durationMinutes, workout.exercises.size),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary
        )
        Spacer(Modifier.height(20.dp))

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            DarkInfoCard(
                label = stringResource(R.string.pre_workout_focus_label),
                title = stringResource(R.string.pre_workout_focus_title),
                body = stringResource(R.string.pre_workout_focus_body)
            )
            if (firstExercise != null) {
                DarkInfoCard(
                    label = stringResource(R.string.pre_workout_last_session_label),
                    title = stringResource(R.string.pre_workout_last_session_title, firstExercise.name),
                    body = stringResource(R.string.pre_workout_last_session_sets)
                )
            }
            // El consejo viene de la API (campo tip del primer ejercicio); si no hay, uno de ejemplo
            DarkInfoCard(
                label = stringResource(R.string.pre_workout_tip_label),
                body = firstExercise?.tip?.takeIf { it.isNotBlank() }
                    ?: stringResource(R.string.pre_workout_tip_fallback),
                bodyColor = MaterialTheme.colorScheme.secondary
            )
            NeedAnythingRow(onMicClick)
        }

        Spacer(Modifier.height(24.dp))
        // Botón menta de Figma (distinto del PrimaryButton lima, por eso se arma acá)
        Button(
            onClick = onStartWorkout,
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary, contentColor = MaterialTheme.colorScheme.onSecondary),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(stringResource(R.string.workout_detail_start), style = MaterialTheme.typography.labelLarge)
        }
    }
}

// Tarjeta translúcida sobre el fondo oscuro: etiqueta lima, título y texto
@Composable
private fun DarkInfoCard(
    label: String,
    body: String,
    title: String? = null,
    bodyColor: Color = White60
) {
    Surface(
        color = White12,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, White12),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primaryContainer)
            if (title != null) {
                Spacer(Modifier.height(6.dp))
                Text(text = title, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.inverseOnSurface)
            }
            Spacer(Modifier.height(6.dp))
            Text(text = body, style = MaterialTheme.typography.bodySmall, color = bodyColor)
        }
    }
}

@Composable
private fun NeedAnythingRow(onMicClick: () -> Unit) {
    Surface(
        color = White12,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Orbe de VORA simplificado (cuando exista VoraOrb en ui/components, se reemplaza)
            Box(
                Modifier
                    .size(32.dp)
                    .background(MaterialTheme.colorScheme.secondary, CircleShape)
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.pre_workout_need_anything),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = stringResource(R.string.pre_workout_need_anything_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = White60
                )
            }
            IconButton(
                onClick = onMicClick,
                modifier = Modifier.background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
            ) {
                Icon(
                    Icons.Outlined.Mic,
                    contentDescription = stringResource(R.string.pre_workout_talk_to_vora),
                    tint = MaterialTheme.colorScheme.onSecondary
                )
            }
        }
    }
}

// internal: lo reusa también Active Workout (mismo paquete), para no duplicar el mensaje de error
@Composable
internal fun DarkMessage(message: String, onClose: () -> Unit, onRetry: (() -> Unit)?) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = message, color = MaterialTheme.colorScheme.inverseOnSurface, textAlign = TextAlign.Center)
        Spacer(Modifier.height(12.dp))
        if (onRetry != null) {
            Button(onClick = onRetry) { Text(stringResource(R.string.action_retry)) }
        }
        IconButton(onClick = onClose) {
            Icon(Icons.Filled.Close, contentDescription = stringResource(R.string.action_close), tint = MaterialTheme.colorScheme.inverseOnSurface)
        }
    }
}

@Preview(showBackground = true, heightDp = 850)
@Composable
private fun PreWorkoutPreview() {
    VoraCoachTheme {
        PreWorkoutScreen(
            uiState = WorkoutDetailUiState.Content(
                workout = Workout(
                    id = "w01",
                    name = "Fuerza inicial",
                    level = WorkoutLevel.BEGINNER,
                    durationMinutes = 25,
                    description = "",
                    exercises = listOf(
                        Exercise("e01", "Sentadilla", "Piernas", 3, 10, 60, "", "Usá una carga que puedas controlar.")
                    )
                ),
                isRefreshing = false,
                refreshError = null
            ),
            onClose = {}, onRetry = {}, onStartWorkout = {}
        )
    }
}
