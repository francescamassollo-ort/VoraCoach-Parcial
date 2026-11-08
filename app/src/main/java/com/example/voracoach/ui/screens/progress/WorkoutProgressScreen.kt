package com.example.voracoach.ui.screens.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.voracoach.R
import com.example.voracoach.model.ExerciseProgress
import com.example.voracoach.model.ProgressPeriod
import com.example.voracoach.ui.components.AppCard
import com.example.voracoach.ui.components.AppSectionLabel
import com.example.voracoach.ui.components.InsightBanner
import com.example.voracoach.ui.components.VoraOrb
import com.example.voracoach.ui.theme.VoraCoachTheme
import com.example.voracoach.viewmodel.WorkoutProgressUiState
import com.example.voracoach.viewmodel.WorkoutProgressViewModel

// Pantalla "Progress / 03 — Workout progress"
@Composable
fun WorkoutProgressRoute(
    onBack: () -> Unit,
    viewModel: WorkoutProgressViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    DemoSnackbarBox { showDemo ->
        WorkoutProgressScreen(
            uiState = uiState,
            onBack = onBack,
            onMoreClick = showDemo,
            onPeriodSelected = viewModel::onPeriodSelected
        )
    }
}

@Composable
fun WorkoutProgressScreen(
    uiState: WorkoutProgressUiState,
    onBack: () -> Unit,
    onMoreClick: () -> Unit,
    onPeriodSelected: (ProgressPeriod) -> Unit
) {
    val data = uiState.data
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        ProgressHeader(stringResource(R.string.screen_progress_workout), onBack, onMoreClick)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            PeriodSelector(selected = uiState.period, onSelect = onPeriodSelected)

            StatsStrip(
                listOf(
                    stringResource(R.string.progress_sessions) to data.sessions.toString(),
                    stringResource(R.string.progress_volume) to stringResource(R.string.progress_kg, data.volumeKg),
                    stringResource(R.string.progress_avg_time) to stringResource(R.string.progress_minutes, data.avgMinutes)
                )
            )

            AppCard(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.Top) {
                    Column(Modifier.weight(1f)) {
                        Text(stringResource(R.string.progress_training_volume), style = MaterialTheme.typography.titleSmall)
                        Text(
                            text = stringResource(R.string.progress_training_volume_sub),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = stringResource(R.string.progress_vs_last_period, data.volumeChangePercent),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(Modifier.height(16.dp))
                SimpleBarChart(
                    values = data.volumeBars,
                    labels = periodBarLabels(uiState.period, data.volumeBars.size)
                )
            }

            PersonalBestCard(data.personalBest)

            InsightBanner(
                message = stringResource(R.string.progress_workout_insight),
                leading = { VoraOrb(diameter = 32.dp) }
            )

            AppSectionLabel(stringResource(R.string.progress_exercise_progress))
            data.exercises.forEach { ExerciseRow(it) }
        }
    }
}

@Composable
private fun PersonalBestCard(best: ExerciseProgress) {
    val shape = RoundedCornerShape(16.dp)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, shape)
            .border(1.dp, MaterialTheme.colorScheme.secondary, shape)
            .padding(16.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(44.dp)
                .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape)
        ) {
            Icon(Icons.Outlined.EmojiEvents, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        }
        Spacer(Modifier.width(14.dp))
        Column {
            Text(
                text = stringResource(R.string.progress_personal_best),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.SemiBold
            )
            Text(best.name, style = MaterialTheme.typography.titleMedium)
            Text(
                text = stringResource(R.string.progress_personal_best_detail, best.weightKg, best.reps, best.gainKg),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ExerciseRow(exercise: ExerciseProgress) {
    AppCard(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(exercise.name, style = MaterialTheme.typography.titleSmall)
                Text(
                    text = stringResource(R.string.progress_weight_reps, exercise.weightKg, exercise.reps),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            // Mejora en menta; si el peso se mantuvo, "Stable" en gris
            val improved = exercise.gainKg > 0
            Text(
                text = if (improved) stringResource(R.string.progress_gain_kg, exercise.gainKg)
                else stringResource(R.string.progress_stable),
                style = MaterialTheme.typography.labelMedium,
                color = if (improved) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .background(
                        if (improved) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.background,
                        RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            )
        }
    }
}

@Preview(showBackground = true, heightDp = 1100)
@Composable
private fun WorkoutProgressPreview() {
    VoraCoachTheme {
        WorkoutProgressScreen(uiState = WorkoutProgressUiState(), onBack = {}, onMoreClick = {}, onPeriodSelected = {})
    }
}
