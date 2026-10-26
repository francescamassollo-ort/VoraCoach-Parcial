package com.example.voracoach.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.voracoach.R
import com.example.voracoach.model.WorkoutLevel
import com.example.voracoach.model.WorkoutSummary
import com.example.voracoach.ui.components.AppCard
import com.example.voracoach.ui.components.AppCardStyle
import com.example.voracoach.ui.components.AppChip
import com.example.voracoach.ui.components.AppChipStyle
import com.example.voracoach.ui.components.CircleIconButton
import com.example.voracoach.ui.components.InsightBanner
import com.example.voracoach.ui.components.PrimaryButton
import com.example.voracoach.ui.components.SecondaryButton
import com.example.voracoach.ui.components.SectionHeader
import com.example.voracoach.ui.components.StatusBadge
import com.example.voracoach.ui.components.StatusBadgeStyle
import com.example.voracoach.ui.components.VoraOrb
import com.example.voracoach.ui.screens.workout.label
import com.example.voracoach.ui.theme.VoraCoachTheme
import com.example.voracoach.ui.theme.White60
import com.example.voracoach.viewmodel.DailySummary
import com.example.voracoach.viewmodel.HomeUiState
import com.example.voracoach.viewmodel.HomeViewModel
import com.example.voracoach.viewmodel.PartOfDay
import java.util.Locale

// Pantalla "Home / 01 — Dashboard". Perfil y métricas de demostración; el entrenamiento
// recomendado es real (el primero del catálogo guardado en Room).
@Composable
fun HomeRoute(
    onNotificationsClick: () -> Unit,
    onStartVoiceSession: () -> Unit,
    onStartMyWorkout: () -> Unit,
    onLogBreakfast: () -> Unit,
    onWorkoutClick: (workoutId: String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    HomeScreen(
        uiState = uiState,
        onNotificationsClick = onNotificationsClick,
        onStartVoiceSession = onStartVoiceSession,
        onStartMyWorkout = onStartMyWorkout,
        onLogBreakfast = onLogBreakfast,
        onWorkoutClick = onWorkoutClick
    )
}

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onNotificationsClick: () -> Unit,
    onStartVoiceSession: () -> Unit,
    onStartMyWorkout: () -> Unit,
    onLogBreakfast: () -> Unit,
    onWorkoutClick: (workoutId: String) -> Unit
) {
    // verticalScroll, Home es más alta que la pantalla, se desplaza (UI03)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        GreetingRow(uiState.userName, uiState.partOfDay, uiState.hasUnreadNotifications, onNotificationsClick)
        CoachCard(onStartVoiceSession, onStartMyWorkout, onLogBreakfast)
        InsightBanner(message = stringResource(R.string.home_on_track))
        SectionHeader(title = stringResource(R.string.home_overview))
        OverviewRow(uiState.summary)
        StatsCard(uiState.summary)
        // Solo si ya hay entrenamientos guardados (se descargan al abrir la pestaña Workout)
        uiState.recommendedWorkout?.let { workout ->
            RecommendedWorkoutCard(workout, onStart = { onWorkoutClick(workout.id) })
        }
    }
}

@Composable
private fun GreetingRow(userName: String, partOfDay: PartOfDay, hasUnread: Boolean, onNotificationsClick: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        // no hay foto de perfil en el perfil de demostración
        val initials = userName.split(" ").mapNotNull { it.firstOrNull()?.uppercase() }.take(2).joinToString("")
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(48.dp)
                .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape)
        ) {
            Text(initials, style = MaterialTheme.typography.titleSmall)
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(
                text = stringResource(
                    when (partOfDay) {
                        PartOfDay.MORNING -> R.string.home_greeting_morning
                        PartOfDay.AFTERNOON -> R.string.home_greeting_afternoon
                        PartOfDay.EVENING -> R.string.home_greeting_evening
                    }
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(userName, style = MaterialTheme.typography.titleLarge)
        }
        CircleIconButton(
            icon = Icons.Outlined.Notifications,
            contentDescription = stringResource(R.string.home_open_notifications),
            onClick = onNotificationsClick,
            showBadge = hasUnread
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CoachCard(onStartVoiceSession: () -> Unit, onStartMyWorkout: () -> Unit, onLogBreakfast: () -> Unit) {
    // Variante oscura: el texto adentro sale blanco sin indicar color
    AppCard(modifier = Modifier.fillMaxWidth(), style = AppCardStyle.Dark, contentPadding = PaddingValues(20.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            VoraOrb(diameter = 32.dp)
            Spacer(Modifier.width(10.dp))
            Text(
                text = stringResource(R.string.home_coach_name),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.weight(1f)
            )
            StatusBadge(text = stringResource(R.string.home_ai_online), fontSize = 11.sp)
        }
        Spacer(Modifier.height(14.dp))
        Text(stringResource(R.string.home_coach_question), style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(6.dp))
        Text(
            text = stringResource(R.string.home_coach_hint),
            style = MaterialTheme.typography.bodySmall,
            color = White60
        )
        Spacer(Modifier.height(16.dp))
        PrimaryButton(
            text = stringResource(R.string.home_start_voice),
            onClick = onStartVoiceSession,
            leadingIcon = Icons.Outlined.Mic,
            alignStart = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(10.dp))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            AppChip(text = stringResource(R.string.home_start_my_workout), style = AppChipStyle.OnDark, onClick = onStartMyWorkout)
            AppChip(text = stringResource(R.string.home_log_breakfast), style = AppChipStyle.OnDark, onClick = onLogBreakfast)
        }
    }
}

// Anillo de objetivo diario + tarjeta de nutrición, lado a lado y con la misma altura
@Composable
private fun OverviewRow(summary: DailySummary) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AppCard(modifier = Modifier.fillMaxHeight()) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        progress = { summary.dailyGoalPercent / 100f },
                        modifier = Modifier.size(72.dp),
                        strokeWidth = 6.dp,
                        trackColor = MaterialTheme.colorScheme.outline,
                        strokeCap = StrokeCap.Round
                    )
                    Text(
                        text = stringResource(R.string.home_percent, summary.dailyGoalPercent),
                        style = MaterialTheme.typography.titleSmall
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.home_daily_goal),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        AppCard(modifier = Modifier
            .weight(1f)
            .fillMaxHeight()) {
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = stringResource(R.string.home_nutrition),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
                Text(summary.caloriesConsumed.withThousands(), style = MaterialTheme.typography.titleSmall)
                Text(
                    text = " " + stringResource(R.string.home_calories_goal, summary.caloriesGoal.withThousands()),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.height(10.dp))
            MacroBar(
                label = stringResource(R.string.home_protein),
                grams = summary.proteinGrams,
                goal = summary.proteinGoalGrams,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(8.dp))
            MacroBar(
                label = stringResource(R.string.home_carbs),
                grams = summary.carbsGrams,
                goal = summary.carbsGoalGrams,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@Composable
private fun MacroBar(label: String, grams: Int, goal: Int, color: Color) {
    Column {
        Row {
            Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
            Text(stringResource(R.string.home_grams_progress, grams, goal), style = MaterialTheme.typography.bodySmall)
        }
        Spacer(Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { if (goal > 0) grams.toFloat() / goal else 0f },
            color = color,
            trackColor = MaterialTheme.colorScheme.outline,
            strokeCap = StrokeCap.Round,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// Pasos, distancia y calorías activas, separados por líneas verticales
@Composable
private fun StatsCard(summary: DailySummary) {
    AppCard(modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.height(IntrinsicSize.Min)) {
            Stat(stringResource(R.string.home_steps), summary.steps.withThousands(), Modifier.weight(1f))
            VerticalDivider(color = MaterialTheme.colorScheme.outline, modifier = Modifier.padding(horizontal = 12.dp))
            Stat(
                stringResource(R.string.home_distance),
                stringResource(R.string.home_distance_value, String.format(Locale.US, "%.1f", summary.distanceKm)),
                Modifier.weight(1f)
            )
            VerticalDivider(color = MaterialTheme.colorScheme.outline, modifier = Modifier.padding(horizontal = 12.dp))
            Stat(stringResource(R.string.home_active_kcal), stringResource(R.string.home_kcal_value, summary.activeKcal), Modifier.weight(1f))
        }
    }
}

@Composable
private fun Stat(label: String, value: String, modifier: Modifier) {
    Column(modifier) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
private fun RecommendedWorkoutCard(workout: WorkoutSummary, onStart: () -> Unit) {
    AppCard(modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(20.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(R.string.home_recommended),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f)
            )
            StatusBadge(text = stringResource(R.string.home_today), style = StatusBadgeStyle.Plain)
        }
        Spacer(Modifier.height(10.dp))
        Text(workout.name, style = MaterialTheme.typography.titleLarge)
        Text(
            text = stringResource(R.string.home_recommended_meta, workout.durationMinutes, workout.exerciseCount, workout.level.label()),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(14.dp))
        Row {
            Spacer(Modifier.weight(1f))
            SecondaryButton(text = stringResource(R.string.home_start_workout), onClick = onStart)
        }
    }
}

// 1420 -> "1,420", como en Figma
private fun Int.withThousands(): String = String.format(Locale.US, "%,d", this)

@Preview(showBackground = true, heightDp = 1200)
@Composable
private fun HomePreview() {
    VoraCoachTheme {
        HomeScreen(
            uiState = HomeUiState(
                userName = "Alex Morgan",
                partOfDay = PartOfDay.MORNING,
                summary = DailySummary(72, 1420, 2100, 104, 140, 152, 220, 7842, 5.6, 312),
                recommendedWorkout = WorkoutSummary("w01", "Fuerza inicial", WorkoutLevel.BEGINNER, 25, 3),
                hasUnreadNotifications = true
            ),
            onNotificationsClick = {}, onStartVoiceSession = {}, onStartMyWorkout = {},
            onLogBreakfast = {}, onWorkoutClick = {}
        )
    }
}
