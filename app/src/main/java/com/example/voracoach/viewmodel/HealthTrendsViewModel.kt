package com.example.voracoach.viewmodel

import androidx.lifecycle.ViewModel
import com.example.voracoach.model.HabitSignal
import com.example.voracoach.model.HealthTrendsData
import com.example.voracoach.model.Trend
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

// Actividad de demostración: la app no lee pasos ni sueño reales del teléfono
private val DEMO_HEALTH_TRENDS = HealthTrendsData(
    avgSteps = 7_842,
    avgDistanceKm = 5.6,
    activeKcal = 312,
    stepsGoal = 8_000,
    dailySteps = listOf(6_100, 7_600, 8_300, 7_300, 9_400, 7_600, 7_600),
    signals = listOf(
        HabitSignal.SLEEP_QUALITY to Trend.IMPROVING,
        HabitSignal.DAILY_MOVEMENT to Trend.CONSISTENT,
        HabitSignal.WORKOUT_FREQUENCY to Trend.IMPROVING,
        HabitSignal.PROTEIN_INTAKE to Trend.IMPROVING
    )
)

@HiltViewModel
class HealthTrendsViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(DEMO_HEALTH_TRENDS)
    val uiState: StateFlow<HealthTrendsData> = _uiState.asStateFlow()
}
