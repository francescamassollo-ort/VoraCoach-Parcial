package com.example.voracoach.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.voracoach.R
import com.example.voracoach.model.CoachingStyle
import com.example.voracoach.model.FitnessGoal
import com.example.voracoach.model.TrainingFrequency
import com.example.voracoach.model.WorkoutType
import com.example.voracoach.ui.components.AppChip
import com.example.voracoach.ui.components.AppChipStyle
import com.example.voracoach.ui.components.AppOptionCard
import com.example.voracoach.ui.components.AppSectionLabel
import com.example.voracoach.ui.components.ScreenHeader
import com.example.voracoach.ui.components.SecondaryButton
import com.example.voracoach.ui.theme.VoraCoachTheme
import com.example.voracoach.viewmodel.GoalsUiState
import com.example.voracoach.viewmodel.GoalsViewModel

// Pantalla "Profile / 03 — Goals & Preferences". Sub-pantalla: no muestra la barra inferior.
@Composable
fun GoalsRoute(
    onBack: () -> Unit,
    viewModel: GoalsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    GoalsScreen(
        uiState = uiState,
        onBack = onBack,
        onGoalToggle = viewModel::onGoalToggle,
        onFrequencySelected = viewModel::onFrequencySelected,
        onWorkoutTypeSelected = viewModel::onWorkoutTypeSelected,
        onCoachingStyleSelected = viewModel::onCoachingStyleSelected,
        // Guarda y vuelve al Dashboard, que ya muestra los valores nuevos
        onSave = {
            viewModel.onSave()
            onBack()
        }
    )
}

@OptIn(ExperimentalLayoutApi::class) // FlowRow es experimental en versiones viejas de Compose
@Composable
fun GoalsScreen(
    uiState: GoalsUiState,
    onBack: () -> Unit,
    onGoalToggle: (FitnessGoal) -> Unit,
    onFrequencySelected: (TrainingFrequency) -> Unit,
    onWorkoutTypeSelected: (WorkoutType) -> Unit,
    onCoachingStyleSelected: (CoachingStyle) -> Unit,
    onSave: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        // ScreenHeader ya trae su margen horizontal: no se le suma otro
        ScreenHeader(title = stringResource(R.string.goals_title), onBackClick = onBack)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = stringResource(R.string.goals_intro),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Objetivos: selección múltiple
            Section(stringResource(R.string.goals_section_primary)) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    FitnessGoal.entries.forEach { goal ->
                        AppOptionCard(
                            title = stringResource(goal.labelRes()),
                            subtitle = goal.descriptionRes()?.let { stringResource(it) },
                            selected = goal in uiState.goals,
                            onClick = { onGoalToggle(goal) }
                        )
                    }
                }
            }

            // Frecuencia: cuatro chips del mismo ancho
            Section(stringResource(R.string.goals_section_frequency)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TrainingFrequency.entries.forEach { option ->
                        AppChip(
                            text = stringResource(option.labelRes()),
                            modifier = Modifier.weight(1f),
                            selected = option == uiState.frequency,
                            onClick = { onFrequencySelected(option) }
                        )
                    }
                }
            }

            Section(stringResource(R.string.goals_section_workout)) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    WorkoutType.entries.forEach { type ->
                        AppChip(
                            text = stringResource(type.labelRes()),
                            style = AppChipStyle.Mint,
                            selected = type == uiState.workoutType,
                            onClick = { onWorkoutTypeSelected(type) }
                        )
                    }
                }
            }

            Section(stringResource(R.string.goals_section_coaching)) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    CoachingStyle.entries.forEach { style ->
                        AppOptionCard(
                            title = stringResource(style.labelRes()),
                            subtitle = stringResource(style.descriptionRes()),
                            selected = style == uiState.coachingStyle,
                            onClick = { onCoachingStyleSelected(style) }
                        )
                    }
                }
            }

            // Dentro del scroll, justo después del contenido, como en Figma. Los 20 dp de más
            // se suman a los 24 dp que ya separan las secciones.
            SecondaryButton(
                text = stringResource(R.string.goals_save),
                onClick = onSave,
                enabled = uiState.canSave,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
                    .navigationBarsPadding()
            )
        }
    }
}

@Composable
private fun Section(title: String, content: @Composable () -> Unit) {
    Column {
        AppSectionLabel(title)
        Spacer(Modifier.height(10.dp))
        content()
    }
}

@Preview(showBackground = true, heightDp = 1100)
@Composable
private fun GoalsPreview() {
    VoraCoachTheme {
        GoalsScreen(
            uiState = GoalsUiState(
                goals = setOf(FitnessGoal.BUILD_STRENGTH, FitnessGoal.IMPROVE_FITNESS),
                frequency = TrainingFrequency.FOUR,
                workoutType = WorkoutType.STRENGTH,
                coachingStyle = CoachingStyle.BALANCED
            ),
            onBack = {}, onGoalToggle = {}, onFrequencySelected = {},
            onWorkoutTypeSelected = {}, onCoachingStyleSelected = {}, onSave = {}
        )
    }
}