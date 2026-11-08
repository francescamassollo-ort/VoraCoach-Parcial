package com.example.voracoach.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.voracoach.data.repository.ProfileRepository
import com.example.voracoach.model.FitnessGoal
import com.example.voracoach.model.ProgressPeriod
import com.example.voracoach.model.ProgressSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class ProgressDashboardUiState(
    val period: ProgressPeriod,
    val summary: ProgressSummary,
    // Objetivos del perfil: si se cambian en Profile / Goals, "Active goal" se actualiza solo
    val goals: List<FitnessGoal>
)

// Métricas de demostración por período: la app no registra actividad real todavía
private val DEMO_SUMMARIES = mapOf(
    ProgressPeriod.WEEK to ProgressSummary(
        workouts = 4, workoutsDelta = 1, avgSteps = 7_842, stepsDeltaPercent = 6,
        avgProteinGrams = 104, proteinDeltaPercent = 9, streakDays = 4, volumeIncreasePercent = 8,
        volumeKg = 2_340, proteinTargetPercent = 74, avgDistanceKm = 5.6, goalCompletionPercent = 68
    ),
    ProgressPeriod.MONTH to ProgressSummary(
        workouts = 15, workoutsDelta = 3, avgSteps = 7_510, stepsDeltaPercent = 2,
        avgProteinGrams = 98, proteinDeltaPercent = 4, streakDays = 4, volumeIncreasePercent = 6,
        volumeKg = 9_800, proteinTargetPercent = 71, avgDistanceKm = 5.3, goalCompletionPercent = 68
    ),
    ProgressPeriod.THREE_MONTHS to ProgressSummary(
        workouts = 44, workoutsDelta = 8, avgSteps = 7_120, stepsDeltaPercent = 11,
        avgProteinGrams = 92, proteinDeltaPercent = 15, streakDays = 4, volumeIncreasePercent = 14,
        volumeKg = 27_600, proteinTargetPercent = 68, avgDistanceKm = 5.0, goalCompletionPercent = 68
    )
)

@HiltViewModel
class ProgressViewModel @Inject constructor(
    profileRepository: ProfileRepository
) : ViewModel() {

    // El período elegido vive acá y no en la pantalla: así sobrevive a la rotación
    private val period = MutableStateFlow(ProgressPeriod.WEEK)

    val uiState: StateFlow<ProgressDashboardUiState> =
        combine(period, profileRepository.preferences) { period, preferences ->
            buildState(period, preferences.goals.sortedBy { it.ordinal })
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = buildState(period.value, profileRepository.preferences.value.goals.sortedBy { it.ordinal })
        )

    fun onPeriodSelected(newPeriod: ProgressPeriod) {
        period.value = newPeriod
    }

    private fun buildState(period: ProgressPeriod, goals: List<FitnessGoal>) = ProgressDashboardUiState(
        period = period,
        summary = DEMO_SUMMARIES.getValue(period),
        goals = goals
    )
}
