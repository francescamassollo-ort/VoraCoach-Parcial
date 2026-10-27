package com.example.voracoach.ui.screens.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.voracoach.model.Exercise
import com.example.voracoach.ui.components.AppCard
import com.example.voracoach.ui.components.InsightBanner
import com.example.voracoach.ui.components.ScreenHeader
import com.example.voracoach.ui.components.SecondaryButton
import com.example.voracoach.ui.components.VoraOrb
import com.example.voracoach.ui.theme.VoraCoachTheme
import com.example.voracoach.viewmodel.WorkoutDetailUiState
import com.example.voracoach.viewmodel.WorkoutDetailViewModel
import kotlinx.coroutines.launch

// Pantalla "Workout / 06 — Exercise Details" (guía del ejercicio).
// Reutiliza WorkoutDetailViewModel (lee el entrenamiento de la ruta) y busca el ejercicio por id (NAV04).
@Composable
fun ExerciseGuideRoute(
    exerciseId: String,
    onBack: () -> Unit,
    viewModel: WorkoutDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val exercise = (uiState as? WorkoutDetailUiState.Content)
        ?.workout?.exercises?.firstOrNull { it.id == exerciseId }
    ExerciseGuideScreen(
        exercise = exercise,
        isLoading = uiState is WorkoutDetailUiState.Loading,
        onBack = onBack
    )
}

@Composable
fun ExerciseGuideScreen(
    exercise: Exercise?,
    isLoading: Boolean,
    onBack: () -> Unit
) {
    // Ayuda (?) y "Ask Vora" no tienen servicio real
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val demoMessage = stringResource(R.string.demo_only_message)
    val showDemo: () -> Unit = { scope.launch { snackbarHostState.showSnackbar(demoMessage) } }

    Scaffold(
        topBar = {
            ScreenHeader(
                title = stringResource(R.string.guide_header),
                onBackClick = onBack,
                modifier = Modifier.statusBarsPadding(),
                actions = {
                    IconButton(onClick = showDemo) {
                        Icon(Icons.AutoMirrored.Outlined.HelpOutline, contentDescription = stringResource(R.string.guide_help))
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                exercise != null -> GuideContent(exercise, onAskVora = showDemo, onBack = onBack)
                isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                else -> Text(
                    text = stringResource(R.string.workout_detail_unavailable),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(24.dp)
                )
            }
        }
    }
}

@Composable
private fun GuideContent(exercise: Exercise, onAskVora: () -> Unit, onBack: () -> Unit) {
    // La API trae las instrucciones en un solo texto: se separan por oración para numerarlas
    val steps = exercise.instructions
        .split(".")
        .map { it.trim() }
        .filter { it.isNotEmpty() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column {
            Text(text = exercise.name, style = MaterialTheme.typography.headlineMedium)
            if (exercise.muscleGroup.isNotBlank()) {
                Text(
                    text = stringResource(R.string.guide_target, exercise.muscleGroup),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // La API no trae imagen del ejercicio: se muestra un recuadro con ícono en su lugar
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(20.dp))
        ) {
            Icon(
                Icons.Outlined.FitnessCenter,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(56.dp)
            )
        }

        if (steps.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(stringResource(R.string.guide_steps_title), style = MaterialTheme.typography.titleSmall)
                steps.forEachIndexed { index, step ->
                    Text(
                        text = stringResource(R.string.guide_step, index + 1, step),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }


        if (exercise.tip.isNotBlank()) {
            AppCard(modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.guide_form_cues), style = MaterialTheme.typography.labelMedium)
                Spacer(Modifier.height(6.dp))
                Text(
                    text = stringResource(R.string.guide_bullet, exercise.tip),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        InsightBanner(
            message = stringResource(R.string.guide_ask_vora, exercise.name),
            leading = { VoraOrb(diameter = 24.dp) },
            modifier = Modifier.clickable(onClick = onAskVora)
        )

        SecondaryButton(
            text = stringResource(R.string.guide_back_to_workout),
            onClick = onBack,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        )
    }
}

@Preview(showBackground = true, heightDp = 850)
@Composable
private fun ExerciseGuidePreview() {
    VoraCoachTheme {
        ExerciseGuideScreen(
            exercise = Exercise(
                id = "e01",
                name = "Sentadilla",
                muscleGroup = "Piernas",
                sets = 3,
                reps = 10,
                restSeconds = 60,
                instructions = "Separá los pies. Flexioná las rodillas con control.",
                tip = "Usá una carga que puedas controlar."
            ),
            isLoading = false,
            onBack = {}
        )
    }
}
