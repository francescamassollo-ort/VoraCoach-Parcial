package com.example.voracoach.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.voracoach.data.repository.WorkoutRepository
import com.example.voracoach.model.WorkoutSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.util.Calendar
import javax.inject.Inject

// Momento del día para el saludo ("Good morning"...). La pantalla elige el texto;
// el ViewModel solo decide cuál corresponde según la hora.
enum class PartOfDay { MORNING, AFTERNOON, EVENING }

// Resumen del día. Son datos de demostración, la app no mide pasos ni calorías reales
data class DailySummary(
    val dailyGoalPercent: Int,
    val caloriesConsumed: Int,
    val caloriesGoal: Int,
    val proteinGrams: Int,
    val proteinGoalGrams: Int,
    val carbsGrams: Int,
    val carbsGoalGrams: Int,
    val steps: Int,
    val distanceKm: Double,
    val activeKcal: Int
)

data class HomeUiState(
    val userName: String,
    val partOfDay: PartOfDay,
    val summary: DailySummary,
    // Real: el primer entrenamiento del catálogo guardado en Room. Null si todavía no se descargó.
    val recommendedWorkout: WorkoutSummary?
)

// Datos del perfil y del día de demostración, en un solo lugar para no repartirlos por la pantalla.
// Sin Firebase se usa un perfil de demostración (lo permite el enunciado).
private const val DEMO_USER_NAME = "Alex Morgan"

private val DEMO_SUMMARY = DailySummary(
    dailyGoalPercent = 72,
    caloriesConsumed = 1420,
    caloriesGoal = 2100,
    proteinGrams = 104,
    proteinGoalGrams = 140,
    carbsGrams = 152,
    carbsGoalGrams = 220,
    steps = 7842,
    distanceKm = 5.6,
    activeKcal = 312
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    repository: WorkoutRepository
) : ViewModel() {

    // Home no consulta la API: observa lo que ya hay en Room. Cuando se descarga el catálogo
    // (desde la pestaña Workout), la tarjeta de entrenamiento recomendado aparece sola.
    val uiState: StateFlow<HomeUiState> =
        repository.observeCatalog()
            .map { workouts -> buildState(recommended = workouts.firstOrNull()) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = buildState(recommended = null)
            )

    private fun buildState(recommended: WorkoutSummary?) = HomeUiState(
        userName = DEMO_USER_NAME,
        partOfDay = currentPartOfDay(),
        summary = DEMO_SUMMARY,
        recommendedWorkout = recommended
    )

    private fun currentPartOfDay(): PartOfDay {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 5..11 -> PartOfDay.MORNING
            in 12..18 -> PartOfDay.AFTERNOON
            else -> PartOfDay.EVENING
        }
    }
}
