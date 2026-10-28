package com.example.voracoach.ui.screens.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.outlined.Bed
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.MonitorHeart
import androidx.compose.material.icons.outlined.NearMe
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.voracoach.R
import com.example.voracoach.model.HabitSignal
import com.example.voracoach.model.HealthTrendsData
import com.example.voracoach.model.Trend
import com.example.voracoach.ui.components.AppCard
import com.example.voracoach.ui.components.AppSectionLabel
import com.example.voracoach.ui.components.InsightBanner
import com.example.voracoach.ui.components.VoraOrb
import com.example.voracoach.ui.theme.VoraCoachTheme
import com.example.voracoach.viewmodel.HealthTrendsViewModel

private fun HabitSignal.labelRes(): Int = when (this) {
    HabitSignal.SLEEP_QUALITY -> R.string.progress_signal_sleep
    HabitSignal.DAILY_MOVEMENT -> R.string.progress_signal_movement
    HabitSignal.WORKOUT_FREQUENCY -> R.string.progress_signal_workouts
    HabitSignal.PROTEIN_INTAKE -> R.string.progress_signal_protein
}

private fun HabitSignal.icon(): ImageVector = when (this) {
    HabitSignal.SLEEP_QUALITY -> Icons.Outlined.Bed
    HabitSignal.DAILY_MOVEMENT -> Icons.Outlined.NearMe
    HabitSignal.WORKOUT_FREQUENCY -> Icons.Outlined.FitnessCenter
    HabitSignal.PROTEIN_INTAKE -> Icons.Outlined.Restaurant
}

// Pantalla "Progress / 05 — Health trends". Se abre desde la fila Activity del Dashboard.
@Composable
fun HealthTrendsRoute(
    onBack: () -> Unit,
    viewModel: HealthTrendsViewModel = hiltViewModel()
) {
    val data by viewModel.uiState.collectAsStateWithLifecycle()
    DemoSnackbarBox { showDemo ->
        HealthTrendsScreen(data = data, onBack = onBack, onMoreClick = showDemo)
    }
}

@Composable
fun HealthTrendsScreen(data: HealthTrendsData, onBack: () -> Unit, onMoreClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        ProgressHeader(stringResource(R.string.screen_progress_health), onBack, onMoreClick)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.progress_health_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            StatsStrip(
                listOf(
                    stringResource(R.string.progress_avg_steps) to stringResource(R.string.progress_number, data.avgSteps),
                    stringResource(R.string.progress_avg_distance) to stringResource(R.string.progress_km, data.avgDistanceKm),
                    stringResource(R.string.progress_active_kcal) to stringResource(R.string.progress_kcal, data.activeKcal)
                )
            )

            AppCard(modifier = Modifier.fillMaxWidth()) {
                Row {
                    Text(
                        text = stringResource(R.string.progress_weekly_steps),
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = stringResource(R.string.progress_steps_goal, data.stepsGoal),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                Spacer(Modifier.height(16.dp))
                // Los días que superaron el objetivo van en verde oscuro; el resto, en menta
                SimpleBarChart(
                    values = data.dailySteps,
                    labels = stringArrayResource(R.array.progress_weekdays_short).toList(),
                    barColor = { index ->
                        if (data.dailySteps[index] >= data.stepsGoal) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.secondary
                    }
                )
            }

            InsightBanner(
                message = stringResource(R.string.progress_health_insight),
                leading = { VoraOrb(diameter = 32.dp) }
            )

            AppSectionLabel(stringResource(R.string.progress_habit_signals))
            data.signals.forEach { (signal, trend) -> SignalRow(signal, trend) }
        }
    }
}

@Composable
private fun SignalRow(signal: HabitSignal, trend: Trend) {
    AppCard(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(signal.icon(), contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(14.dp))
            Text(stringResource(signal.labelRes()), style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f))
            val improving = trend == Trend.IMPROVING
            val color = if (improving) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant
            Text(
                text = stringResource(if (improving) R.string.progress_trend_improving else R.string.progress_trend_consistent),
                style = MaterialTheme.typography.labelLarge,
                color = color
            )
            Spacer(Modifier.width(6.dp))
            Icon(
                imageVector = if (improving) Icons.AutoMirrored.Outlined.TrendingUp else Icons.Outlined.MonitorHeart,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Preview(showBackground = true, heightDp = 1000)
@Composable
private fun HealthTrendsPreview() {
    VoraCoachTheme {
        HealthTrendsScreen(
            data = HealthTrendsData(
                avgSteps = 7_842, avgDistanceKm = 5.6, activeKcal = 312, stepsGoal = 8_000,
                dailySteps = listOf(6_100, 7_600, 8_300, 7_300, 9_400, 7_600, 7_600),
                signals = listOf(HabitSignal.SLEEP_QUALITY to Trend.IMPROVING, HabitSignal.DAILY_MOVEMENT to Trend.CONSISTENT)
            ),
            onBack = {},
            onMoreClick = {}
        )
    }
}
