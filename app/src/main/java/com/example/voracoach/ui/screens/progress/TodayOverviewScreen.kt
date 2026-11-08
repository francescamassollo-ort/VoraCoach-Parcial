package com.example.voracoach.ui.screens.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.voracoach.R
import com.example.voracoach.model.FitnessGoal
import com.example.voracoach.model.TodayProgress
import com.example.voracoach.model.WorkoutLevel
import com.example.voracoach.model.WorkoutSummary
import com.example.voracoach.ui.components.AppCard
import com.example.voracoach.ui.components.AppCardStyle
import com.example.voracoach.ui.components.CircleIconButton
import com.example.voracoach.ui.components.PrimaryButton
import com.example.voracoach.ui.screens.profile.goalsSummary
import com.example.voracoach.ui.screens.workout.label
import com.example.voracoach.ui.theme.VoraCoachTheme
import com.example.voracoach.viewmodel.TodayOverviewUiState
import com.example.voracoach.viewmodel.TodayOverviewViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

// Pantalla "Progress / 01 — Today's Overview". Se abre desde la sección "Today's Overview" de Home.
@Composable
fun TodayOverviewRoute(
    onBack: () -> Unit,
    onWorkoutClick: (workoutId: String) -> Unit,
    viewModel: TodayOverviewViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    TodayOverviewScreen(uiState = uiState, onBack = onBack, onWorkoutClick = onWorkoutClick)
}

@Composable
fun TodayOverviewScreen(
    uiState: TodayOverviewUiState,
    onBack: () -> Unit,
    onWorkoutClick: (workoutId: String) -> Unit
) {
    // Fecha de hoy en inglés, como el resto de la interfaz: "TUESDAY, 8 SEPTEMBER"
    val date = remember { SimpleDateFormat("EEEE, d MMMM", Locale.ENGLISH).format(Date()).uppercase(Locale.ENGLISH) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CircleIconButton(
            icon = Icons.AutoMirrored.Outlined.ArrowBack,
            contentDescription = stringResource(R.string.progress_back),
            onClick = onBack
        )
        Column {
            Text(date, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(stringResource(R.string.screen_progress_today), style = MaterialTheme.typography.headlineMedium)
        }

        CompletionCard(uiState.today)

        AppCard(modifier = Modifier.fillMaxWidth(), style = AppCardStyle.Dark) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                Spacer(Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.progress_vora_advice),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            Spacer(Modifier.height(10.dp))
            Text(
                text = stringResource(R.string.progress_vora_advice_text),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.inverseOnSurface
            )
        }

        Text(stringResource(R.string.progress_category_breakdowns), style = MaterialTheme.typography.titleMedium)

        // Solo si ya hay entrenamientos guardados en Room (se descargan al abrir la pestaña Workout)
        uiState.recommendedWorkout?.let { workout -> WorkoutCategoryCard(workout, onWorkoutClick) }

        GoalRow(uiState)
    }
}

@Composable
private fun CompletionCard(today: TodayProgress) {
    val nutritionPercent = percent(today.caloriesConsumed.toDouble(), today.caloriesGoal.toDouble())
    val activityPercent = percent(today.steps.toDouble(), today.stepsGoal.toDouble())
    val waterPercent = percent(today.waterLitres, today.waterGoalLitres)

    AppCard(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.progress_daily_completion),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = stringResource(R.string.progress_percent, today.completionPercent),
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.SemiBold
                )
            }
            CircularProgressIndicator(
                progress = { today.completionPercent / 100f },
                modifier = Modifier.size(64.dp),
                strokeWidth = 6.dp,
                trackColor = MaterialTheme.colorScheme.outline,
                strokeCap = StrokeCap.Round
            )
        }
        HorizontalDivider(Modifier.padding(vertical = 14.dp), color = MaterialTheme.colorScheme.outline)
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            CompletionRow(
                color = MaterialTheme.colorScheme.error,
                label = stringResource(R.string.progress_workout_progress),
                detail = stringResource(R.string.progress_not_started),
                percent = today.workoutPercent
            )
            CompletionRow(
                color = MaterialTheme.colorScheme.secondary,
                label = stringResource(R.string.progress_nutrition_target),
                detail = stringResource(R.string.progress_kcal_of, today.caloriesConsumed, today.caloriesGoal),
                percent = nutritionPercent
            )
            CompletionRow(
                color = MaterialTheme.colorScheme.primary,
                label = stringResource(R.string.progress_activity_target),
                detail = stringResource(R.string.progress_steps, today.steps),
                percent = activityPercent
            )
            CompletionRow(
                color = MaterialTheme.colorScheme.primaryContainer,
                label = stringResource(R.string.progress_hydration),
                detail = stringResource(R.string.progress_litres_of, today.waterLitres, today.waterGoalLitres),
                percent = waterPercent
            )
        }
    }
}

private fun percent(value: Double, goal: Double): Int =
    if (goal <= 0) 0 else (value / goal * 100).roundToInt().coerceAtMost(100)

@Composable
private fun CompletionRow(color: Color, label: String, detail: String, percent: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(10.dp).background(color, CircleShape))
        Spacer(Modifier.width(10.dp))
        Text(label, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
        Text(detail, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.width(6.dp))
        Text(
            text = stringResource(R.string.progress_percent, percent),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun WorkoutCategoryCard(workout: WorkoutSummary, onWorkoutClick: (String) -> Unit) {
    AppCard(modifier = Modifier.fillMaxWidth()) {
        Row {
            Text(
                text = stringResource(R.string.progress_workout_label),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = stringResource(R.string.progress_not_started_badge),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.error
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(workout.name, style = MaterialTheme.typography.titleLarge)
        Text(
            text = stringResource(R.string.home_recommended_meta, workout.durationMinutes, workout.exerciseCount, workout.level.label()),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(14.dp))
        // Abre el detalle real del entrenamiento (Workout / 03)
        PrimaryButton(
            text = stringResource(R.string.progress_start_workout),
            onClick = { onWorkoutClick(workout.id) },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// Objetivo actual y meta semanal, tomados del perfil (Profile / Goals)
@Composable
private fun GoalRow(uiState: TodayOverviewUiState) {
    AppCard(modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.height(IntrinsicSize.Min)) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.progress_current_goal),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(4.dp))
                Text(goalsSummary(uiState.goals.take(1)), style = MaterialTheme.typography.titleMedium)
            }
            VerticalDivider(Modifier.padding(horizontal = 12.dp), color = MaterialTheme.colorScheme.outline)
            Column(Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.progress_weekly_target),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.progress_workouts_of, uiState.today.workoutsThisWeek, uiState.weeklyTargetDays),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 1100)
@Composable
private fun TodayOverviewPreview() {
    VoraCoachTheme {
        TodayOverviewScreen(
            uiState = TodayOverviewUiState(
                today = TodayProgress(72, 0, 1_420, 2_100, 7_842, 9_000, 1.6, 2.5, 4),
                recommendedWorkout = WorkoutSummary("w01", "Upper Body Strength", WorkoutLevel.INTERMEDIATE, 42, 5),
                goals = listOf(FitnessGoal.BUILD_STRENGTH),
                weeklyTargetDays = 5
            ),
            onBack = {},
            onWorkoutClick = {}
        )
    }
}
