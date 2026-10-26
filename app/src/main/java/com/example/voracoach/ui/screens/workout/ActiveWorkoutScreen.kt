package com.example.voracoach.ui.screens.workout

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.voracoach.R
import com.example.voracoach.model.Exercise
import com.example.voracoach.model.Workout
import com.example.voracoach.model.WorkoutLevel
import com.example.voracoach.ui.theme.DarkGreen
import com.example.voracoach.ui.theme.LimeGreen
import com.example.voracoach.ui.theme.Mint
import com.example.voracoach.ui.theme.VoraCoachTheme
import com.example.voracoach.ui.theme.White12
import com.example.voracoach.ui.theme.White60
import com.example.voracoach.viewmodel.WorkoutDetailUiState
import com.example.voracoach.viewmodel.WorkoutDetailViewModel
import kotlinx.coroutines.delay

// Peso de demostración con el que arranca cada sesión y cuánto cambia con - / +
private const val DEMO_START_WEIGHT_KG = 45.0
private const val WEIGHT_STEP_KG = 2.5
private const val EXTRA_REST_SECONDS = 30

// Pantalla "Workout / 05 — Active Workout" (demostración navegable).
// Ejercicios, series, repeticiones, descanso y consejo son los reales del entrenamiento (Room).
@Composable
fun ActiveWorkoutRoute(
    onClose: () -> Unit,
    onFinishWorkout: (workoutId: String) -> Unit,
    onExerciseInfoClick: (workoutId: String, exerciseId: String) -> Unit,
    viewModel: WorkoutDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    ActiveWorkoutScreen(
        uiState = uiState,
        onClose = onClose,
        onRetry = viewModel::retry,
        onFinishWorkout = onFinishWorkout,
        onExerciseInfoClick = onExerciseInfoClick
    )
}

@Composable
fun ActiveWorkoutScreen(
    uiState: WorkoutDetailUiState,
    onClose: () -> Unit,
    onRetry: () -> Unit,
    onFinishWorkout: (workoutId: String) -> Unit,
    onExerciseInfoClick: (workoutId: String, exerciseId: String) -> Unit = { _, _ -> }
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkGreen)
    ) {
        when (uiState) {
            WorkoutDetailUiState.Loading ->
                CircularProgressIndicator(color = Mint, modifier = Modifier.align(Alignment.Center))

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

            is WorkoutDetailUiState.Content -> ActiveWorkoutContent(
                workout = uiState.workout,
                onFinishWorkout = { onFinishWorkout(uiState.workout.id) },
                onExerciseInfoClick = { exerciseId -> onExerciseInfoClick(uiState.workout.id, exerciseId) }
            )
        }
    }
}

@Composable
private fun ActiveWorkoutContent(
    workout: Workout,
    onFinishWorkout: () -> Unit,
    onExerciseInfoClick: (exerciseId: String) -> Unit
) {
    // Progreso de la sesión. rememberSaveable lo conserva al rotar la pantalla (RF07):
    // con remember se volvería al primer ejercicio cada vez que se gira el celular.
    var exerciseIndex by rememberSaveable { mutableIntStateOf(0) }
    var completedSets by rememberSaveable { mutableIntStateOf(0) }
    var weightKg by rememberSaveable { mutableDoubleStateOf(DEMO_START_WEIGHT_KG) }
    var restSecondsLeft by rememberSaveable { mutableIntStateOf(0) }

    // Cronómetro de descanso: mientras quede tiempo, descuenta un segundo por vez.
    // LaunchedEffect se reinicia cuando cambia la clave (empieza o termina un descanso),
    // y se cancela solo si se sale de la pantalla.
    val isResting = restSecondsLeft > 0
    LaunchedEffect(isResting) {
        while (restSecondsLeft > 0) {
            delay(1_000)
            restSecondsLeft--
        }
    }

    val exercises = workout.exercises
    if (exercises.isEmpty()) {
        DarkMessage(message = workout.name, onClose = onFinishWorkout, onRetry = null)
        return
    }
    val exercise = exercises[exerciseIndex.coerceIn(exercises.indices)]

    val onCompleteSet = {
        when {
            // Quedan series de este ejercicio: se marca la serie y empieza el descanso
            completedSets + 1 < exercise.sets -> {
                completedSets++
                restSecondsLeft = exercise.restSeconds
            }
            // Era la última serie: pasa al siguiente ejercicio, también con descanso
            exerciseIndex + 1 < exercises.size -> {
                exerciseIndex++
                completedSets = 0
                restSecondsLeft = exercise.restSeconds
            }
            // Era la última serie del último ejercicio: entrenamiento terminado
            else -> onFinishWorkout()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Header(
            exerciseNumber = exerciseIndex + 1,
            exerciseTotal = exercises.size,
            exerciseName = exercise.name,
            onFinishWorkout = onFinishWorkout,
            onExerciseInfoClick = { onExerciseInfoClick(exercise.id) }
        )
        VolumeCard(exercise)
        WeightCard(
            reps = exercise.reps,
            weightKg = weightKg,
            onDecrease = { weightKg = (weightKg - WEIGHT_STEP_KG).coerceAtLeast(0.0) },
            onIncrease = { weightKg += WEIGHT_STEP_KG }
        )
        SetsRow(totalSets = exercise.sets, completedSets = completedSets)
        RestRow(
            secondsLeft = restSecondsLeft,
            onAddTime = { restSecondsLeft += EXTRA_REST_SECONDS },
            onSkip = { restSecondsLeft = 0 }
        )
        CoachCard(tip = exercise.tip)

        Button(
            onClick = onCompleteSet,
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(containerColor = LimeGreen, contentColor = DarkGreen),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(stringResource(R.string.active_complete_set), style = MaterialTheme.typography.titleSmall)
        }
    }
}

@Composable
private fun Header(
    exerciseNumber: Int,
    exerciseTotal: Int,
    exerciseName: String,
    onFinishWorkout: () -> Unit,
    onExerciseInfoClick: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.active_exercise_counter, exerciseNumber, exerciseTotal),
                style = MaterialTheme.typography.labelMedium,
                color = Mint
            )
            // Tocar el nombre (o el ícono de info) abre la guía del ejercicio
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable(onClick = onExerciseInfoClick)
            ) {
                Text(text = exerciseName, style = MaterialTheme.typography.titleLarge, color = Color.White)
                Spacer(Modifier.width(6.dp))
                Icon(
                    Icons.Outlined.Info,
                    contentDescription = stringResource(R.string.guide_open),
                    tint = White60,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        Surface(onClick = onFinishWorkout, color = White12, shape = CircleShape) {
            Text(
                text = stringResource(R.string.active_finish_workout),
                style = MaterialTheme.typography.labelLarge,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
            )
        }
    }
}

@Composable
private fun VolumeCard(exercise: Exercise) {
    DarkCard {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(stringResource(R.string.active_volume_label), style = MaterialTheme.typography.labelMedium, color = Mint)
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                BigStat(value = exercise.reps.toString(), label = stringResource(R.string.active_reps_target))
                VerticalDivider(color = White12, modifier = Modifier.height(48.dp).padding(horizontal = 24.dp))
                BigStat(
                    value = stringResource(R.string.active_sets_by_reps, exercise.sets, exercise.reps),
                    label = stringResource(R.string.active_set_matrix)
                )
            }
        }
    }
}

@Composable
private fun BigStat(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontSize = 34.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = White60)
    }
}

@Composable
private fun WeightCard(reps: Int, weightKg: Double, onDecrease: () -> Unit, onIncrease: () -> Unit) {
    DarkCard {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(stringResource(R.string.active_last_session_label), style = MaterialTheme.typography.labelSmall, color = White60)
                Text(
                    text = stringResource(R.string.active_last_session_value, DEMO_START_WEIGHT_KG.formatKg(), reps),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White
                )
            }
            RoundIconButton(onClick = onDecrease) {
                Icon(Icons.Filled.Remove, contentDescription = stringResource(R.string.active_weight_decrease), tint = Color.White)
            }
            Text(
                text = stringResource(R.string.active_weight, weightKg.formatKg()),
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.widthIn(min = 80.dp)
            )
            RoundIconButton(onClick = onIncrease) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.active_weight_increase), tint = Color.White)
            }
        }
    }
}

// Una pastilla por serie: las hechas con el número en menta, la actual con borde lima.
// horizontalScroll por si un ejercicio tiene muchas series y no entran en el ancho.
@Composable
private fun SetsRow(totalSets: Int, completedSets: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        for (set in 1..totalSets) {
            val isDone = set <= completedSets
            val isCurrent = set == completedSets + 1
            Surface(
                color = if (isCurrent) White12 else Color.Transparent,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, if (isCurrent) LimeGreen else White12),
                modifier = Modifier.widthIn(min = 96.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(26.dp)
                            .background(
                                color = when {
                                    isDone -> Mint
                                    isCurrent -> LimeGreen
                                    else -> White12
                                },
                                shape = CircleShape
                            )
                    ) {
                        Text(
                            text = set.toString(),
                            style = MaterialTheme.typography.labelMedium,
                            color = if (isDone || isCurrent) DarkGreen else White60
                        )
                    }
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = stringResource(R.string.active_set, set),
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (isDone || isCurrent) Color.White else White60
                    )
                }
            }
        }
    }
}

@Composable
private fun RestRow(secondsLeft: Int, onAddTime: () -> Unit, onSkip: () -> Unit) {
    DarkCard {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (secondsLeft > 0) stringResource(R.string.active_resting, secondsLeft.formatMinutesSeconds())
                else stringResource(R.string.active_ready_next_set),
                style = MaterialTheme.typography.titleSmall,
                color = Mint,
                modifier = Modifier.weight(1f)
            )
            // Durante el descanso se puede sumar tiempo o saltearlo
            if (secondsLeft > 0) {
                OutlinedButton(
                    onClick = onAddTime,
                    border = BorderStroke(1.dp, Mint),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Mint)
                ) { Text(stringResource(R.string.active_add_30s)) }
                Spacer(Modifier.width(8.dp))
                Button(
                    onClick = onSkip,
                    colors = ButtonDefaults.buttonColors(containerColor = Mint, contentColor = DarkGreen)
                ) { Text(stringResource(R.string.active_skip)) }
            }
        }
    }
}

@Composable
private fun CoachCard(tip: String) {
    DarkCard {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Orbe simplificado hasta que exista VoraOrb en ui/components
                Box(
                    Modifier
                        .size(22.dp)
                        .background(Mint, CircleShape)
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    text = stringResource(R.string.active_coach_label),
                    style = MaterialTheme.typography.labelLarge,
                    color = Mint,
                    modifier = Modifier.weight(1f)
                )
                Text(stringResource(R.string.active_voice_on), style = MaterialTheme.typography.bodySmall, color = White60)
            }
            Spacer(Modifier.height(10.dp))
            // El consejo es el tip real del ejercicio; si la API no trae, uno de ejemplo
            Text(
                text = "\"${tip.ifBlank { stringResource(R.string.active_coach_fallback) }}\"",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White
            )
        }
    }
}

@Composable
private fun DarkCard(content: @Composable () -> Unit) {
    Surface(
        color = White12,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, White12),
        modifier = Modifier.fillMaxWidth()
    ) { content() }
}

@Composable
private fun RoundIconButton(onClick: () -> Unit, content: @Composable () -> Unit) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(44.dp)
            .background(White12, CircleShape)
    ) { content() }
}

// 45.0 -> "45" y 47.5 -> "47.5": sin decimales cuando el peso es entero
private fun Double.formatKg(): String =
    if (this % 1.0 == 0.0) toInt().toString() else toString()

// 78 -> "01:18"
private fun Int.formatMinutesSeconds(): String = "%02d:%02d".format(this / 60, this % 60)

@Preview(showBackground = true, heightDp = 900)
@Composable
private fun ActiveWorkoutPreview() {
    VoraCoachTheme {
        ActiveWorkoutScreen(
            uiState = WorkoutDetailUiState.Content(
                workout = Workout(
                    id = "w01",
                    name = "Fuerza inicial",
                    level = WorkoutLevel.BEGINNER,
                    durationMinutes = 25,
                    description = "",
                    exercises = listOf(
                        Exercise("e01", "Sentadilla", "Piernas", 3, 10, 60, "", "Usá una carga que puedas controlar."),
                        Exercise("e02", "Remo con mancuerna", "Espalda", 3, 10, 60, "", "")
                    )
                ),
                isRefreshing = false,
                refreshError = null
            ),
            onClose = {}, onRetry = {}, onFinishWorkout = {}
        )
    }
}
