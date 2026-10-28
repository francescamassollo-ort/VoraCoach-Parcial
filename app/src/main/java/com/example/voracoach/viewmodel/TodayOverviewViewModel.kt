package com.example.voracoach.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.voracoach.data.repository.ProfileRepository
import com.example.voracoach.data.repository.WorkoutRepository
import com.example.voracoach.model.FitnessGoal
import com.example.voracoach.model.TodayProgress
import com.example.voracoach.model.WorkoutSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class TodayOverviewUiState(
    val today: TodayProgress,
    // Real: el primer entrenamiento del catálogo guardado en Room (el mismo que recomienda Home)
    val recommendedWorkout: WorkoutSummary?,
    // Del perfil: objetivos y días de entrenamiento por semana
    val goals: List<FitnessGoal>,
    val weeklyTargetDays: Int
)

// Números del día de demostración: los mismos que muestra Home, así las dos pantallas coinciden
private val DEMO_TODAY = TodayProgress(
    completionPercent = 72,
    workoutPercent = 0,
    caloriesConsumed = 1_420,
    caloriesGoal = 2_100,
    steps = 7_842,
    stepsGoal = 9_000,
    waterLitres = 1.6,
    waterGoalLitres = 2.5,
    workoutsThisWeek = 4
)

@HiltViewModel
class TodayOverviewViewModel @Inject constructor(
    workoutRepository: WorkoutRepository,
    profileRepository: ProfileRepository
) : ViewModel() {

    val uiState: StateFlow<TodayOverviewUiState> =
        combine(workoutRepository.observeCatalog(), profileRepository.preferences) { workouts, preferences ->
            TodayOverviewUiState(
                today = DEMO_TODAY,
                recommendedWorkout = workouts.firstOrNull(),
                goals = preferences.goals.sortedBy { it.ordinal },
                weeklyTargetDays = preferences.frequency.days
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = TodayOverviewUiState(
                today = DEMO_TODAY,
                recommendedWorkout = null,
                goals = profileRepository.preferences.value.goals.sortedBy { it.ordinal },
                weeklyTargetDays = profileRepository.preferences.value.frequency.days
            )
        )
}
