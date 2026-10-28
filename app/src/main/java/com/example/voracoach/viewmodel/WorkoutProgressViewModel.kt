package com.example.voracoach.viewmodel

import androidx.lifecycle.ViewModel
import com.example.voracoach.model.ExerciseProgress
import com.example.voracoach.model.ProgressPeriod
import com.example.voracoach.model.WorkoutProgressData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class WorkoutProgressUiState(
    val period: ProgressPeriod = ProgressPeriod.WEEK,
    val data: WorkoutProgressData = DEMO_WORKOUT_PROGRESS.getValue(ProgressPeriod.WEEK)
)

private val DEMO_EXERCISES = listOf(
    ExerciseProgress("Bench Press", weightKg = 40.0, reps = 10, gainKg = 2.5),
    ExerciseProgress("Cable Row", weightKg = 45.0, reps = 12, gainKg = 5.0),
    ExerciseProgress("Shoulder Press", weightKg = 20.0, reps = 10, gainKg = 0.0),
    ExerciseProgress("Lateral Raise", weightKg = 8.0, reps = 12, gainKg = 1.0)
)

private val DEMO_PERSONAL_BEST = ExerciseProgress("Seated Cable Row", weightKg = 45.0, reps = 12, gainKg = 5.0)

// Métricas de demostración por período. Las barras de la semana van de lunes a viernes
// (el miércoles fue descanso); las del mes son semanas y las de 3 meses, meses.
private val DEMO_WORKOUT_PROGRESS = mapOf(
    ProgressPeriod.WEEK to WorkoutProgressData(
        sessions = 4, volumeKg = 8_420, avgMinutes = 41, volumeChangePercent = 8,
        volumeBars = listOf(1_950, 2_100, 0, 2_200, 2_170),
        personalBest = DEMO_PERSONAL_BEST, exercises = DEMO_EXERCISES
    ),
    ProgressPeriod.MONTH to WorkoutProgressData(
        sessions = 15, volumeKg = 31_200, avgMinutes = 40, volumeChangePercent = 6,
        volumeBars = listOf(7_100, 7_600, 8_080, 8_420),
        personalBest = DEMO_PERSONAL_BEST, exercises = DEMO_EXERCISES
    ),
    ProgressPeriod.THREE_MONTHS to WorkoutProgressData(
        sessions = 44, volumeKg = 88_500, avgMinutes = 39, volumeChangePercent = 14,
        volumeBars = listOf(27_300, 30_000, 31_200),
        personalBest = DEMO_PERSONAL_BEST, exercises = DEMO_EXERCISES
    )
)

@HiltViewModel
class WorkoutProgressViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(WorkoutProgressUiState())
    val uiState: StateFlow<WorkoutProgressUiState> = _uiState.asStateFlow()

    fun onPeriodSelected(period: ProgressPeriod) {
        _uiState.update { it.copy(period = period, data = DEMO_WORKOUT_PROGRESS.getValue(period)) }
    }
}
