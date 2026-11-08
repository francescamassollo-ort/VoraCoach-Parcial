package com.example.voracoach.ui.screens.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.ShowChart
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.GpsFixed
import androidx.compose.material.icons.outlined.Restaurant
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
import com.example.voracoach.model.FitnessGoal
import com.example.voracoach.model.ProgressPeriod
import com.example.voracoach.model.ProgressSummary
import com.example.voracoach.ui.components.AppCard
import com.example.voracoach.ui.components.AppCardStyle
import com.example.voracoach.ui.components.AppListRow
import com.example.voracoach.ui.components.StatusBadge
import com.example.voracoach.ui.components.StatusBadgeStyle
import com.example.voracoach.ui.components.VoraOrb
import com.example.voracoach.ui.screens.profile.goalsSummary
import com.example.voracoach.ui.theme.VoraCoachTheme
import com.example.voracoach.viewmodel.ProgressDashboardUiState
import com.example.voracoach.viewmodel.ProgressViewModel

// Pantalla "Progress / 02 — Dashboard": la pestaña Progress
@Composable
fun ProgressDashboardRoute(
    onWorkoutClick: () -> Unit,
    onNutritionClick: () -> Unit,
    onActivityClick: () -> Unit,
    onWeeklyReviewClick: () -> Unit,
    viewModel: ProgressViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    ProgressDashboardScreen(
        uiState = uiState,
        onPeriodSelected = viewModel::onPeriodSelected,
        onWorkoutClick = onWorkoutClick,
        onNutritionClick = onNutritionClick,
        onActivityClick = onActivityClick,
        onWeeklyReviewClick = onWeeklyReviewClick
    )
}

@Composable
fun ProgressDashboardScreen(
    uiState: ProgressDashboardUiState,
    onPeriodSelected: (ProgressPeriod) -> Unit,
    onWorkoutClick: () -> Unit,
    onNutritionClick: () -> Unit,
    onActivityClick: () -> Unit,
    onWeeklyReviewClick: () -> Unit
) {
    val summary = uiState.summary
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column {
            Text(stringResource(R.string.progress_title), style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.progress_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        PeriodSelector(selected = uiState.period, onSelect = onPeriodSelected)
        OverviewCard(uiState.period, summary)
        InsightCard(summary, onWeeklyReviewClick)

        AppListRow(
            icon = Icons.Outlined.FitnessCenter,
            title = stringResource(R.string.progress_workout),
            subtitle = stringResource(R.string.progress_workout_sub, summary.workouts, summary.volumeKg),
            onClick = onWorkoutClick,
            trailing = { ViewLabel() }
        )
        AppListRow(
            icon = Icons.Outlined.Restaurant,
            title = stringResource(R.string.progress_nutrition),
            subtitle = stringResource(R.string.progress_nutrition_sub, summary.avgProteinGrams, summary.proteinTargetPercent),
            onClick = onNutritionClick,
            trailing = { ViewLabel() }
        )
        AppListRow(
            icon = Icons.AutoMirrored.Outlined.ShowChart,
            title = stringResource(R.string.progress_activity),
            subtitle = stringResource(R.string.progress_activity_sub, summary.avgSteps, summary.avgDistanceKm),
            onClick = onActivityClick,
            trailing = { ViewLabel() }
        )

        ActiveGoalCard(uiState.goals, summary.goalCompletionPercent)
    }
}

@Composable
private fun OverviewCard(period: ProgressPeriod, summary: ProgressSummary) {
    val title = when (period) {
        ProgressPeriod.WEEK -> R.string.progress_overview_week
        ProgressPeriod.MONTH -> R.string.progress_overview_month
        ProgressPeriod.THREE_MONTHS -> R.string.progress_overview_three_months
    }
    AppCard(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(title),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatTile(
                label = stringResource(R.string.progress_workouts_completed),
                value = stringResource(R.string.progress_workouts_value, summary.workouts),
                delta = stringResource(R.string.progress_delta_count, summary.workoutsDelta),
                modifier = Modifier.weight(1f)
            )
            StatTile(
                label = stringResource(R.string.progress_avg_daily_steps),
                value = stringResource(R.string.progress_number, summary.avgSteps),
                delta = stringResource(R.string.progress_delta_percent, summary.stepsDeltaPercent),
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatTile(
                label = stringResource(R.string.progress_daily_avg_protein),
                value = stringResource(R.string.progress_grams, summary.avgProteinGrams),
                delta = stringResource(R.string.progress_delta_percent, summary.proteinDeltaPercent),
                modifier = Modifier.weight(1f)
            )
            StatTile(
                label = stringResource(R.string.progress_active_streak),
                value = stringResource(R.string.progress_streak_value, summary.streakDays),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

// Tarjeta oscura "VORA Insight": su enlace abre la revisión semanal (Progress / 06)
@Composable
private fun InsightCard(summary: ProgressSummary, onWeeklyReviewClick: () -> Unit) {
    AppCard(modifier = Modifier.fillMaxWidth(), style = AppCardStyle.Dark) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            VoraOrb(diameter = 36.dp)
            Spacer(Modifier.width(12.dp))
            Text(
                text = stringResource(R.string.progress_vora_insight),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.weight(1f)
            )
            StatusBadge(text = stringResource(R.string.progress_consistent), style = StatusBadgeStyle.OnDark)
        }
        Spacer(Modifier.height(14.dp))
        Text(
            text = stringResource(R.string.progress_insight_text, summary.workouts, summary.volumeIncreasePercent),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.inverseOnSurface
        )
        Spacer(Modifier.height(14.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable(onClick = onWeeklyReviewClick).padding(vertical = 4.dp)
        ) {
            Text(
                text = stringResource(R.string.progress_see_weekly_review),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primaryContainer
            )
            Spacer(Modifier.width(6.dp))
            Icon(
                Icons.AutoMirrored.Outlined.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun ActiveGoalCard(goals: List<FitnessGoal>, completionPercent: Int) {
    val shape = RoundedCornerShape(16.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.secondaryContainer, shape)
            .border(1.dp, MaterialTheme.colorScheme.secondary, shape)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Outlined.GpsFixed, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.progress_active_goal),
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = stringResource(R.string.progress_goal_complete, completionPercent),
                style = MaterialTheme.typography.labelLarge
            )
        }
        Spacer(Modifier.height(10.dp))
        Text(
            text = goalsSummary(goals),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.progress_goal_hint),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ViewLabel() {
    Text(
        text = stringResource(R.string.progress_view),
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary
    )
}

@Preview(showBackground = true, heightDp = 1200)
@Composable
private fun ProgressDashboardPreview() {
    VoraCoachTheme {
        ProgressDashboardScreen(
            uiState = ProgressDashboardUiState(
                period = ProgressPeriod.WEEK,
                summary = ProgressSummary(4, 1, 7_842, 6, 104, 9, 4, 8, 2_340, 74, 5.6, 68),
                goals = listOf(FitnessGoal.BUILD_STRENGTH, FitnessGoal.IMPROVE_FITNESS)
            ),
            onPeriodSelected = {},
            onWorkoutClick = {},
            onNutritionClick = {},
            onActivityClick = {},
            onWeeklyReviewClick = {}
        )
    }
}
