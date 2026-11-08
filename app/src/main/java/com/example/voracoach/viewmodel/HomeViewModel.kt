package com.example.voracoach.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.voracoach.data.repository.NotificationsRepository
import com.example.voracoach.data.repository.ProfileRepository
import com.example.voracoach.data.repository.WorkoutRepository
import com.example.voracoach.model.UserProfile
import com.example.voracoach.model.WorkoutSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
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
    val recommendedWorkout: WorkoutSummary?,
    // Decide si la campana muestra el punto de aviso
    val hasUnreadNotifications: Boolean
)

// Datos del día de demostración, en un solo lugar para no repartirlos por la pantalla
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
    workoutRepository: WorkoutRepository,
    notificationsRepository: NotificationsRepository,
    profileRepository: ProfileRepository
) : ViewModel() {

    // Home no consulta la API: observa lo que ya hay en Room. Cuando se descarga el catálogo
    // (desde la pestaña Workout), la tarjeta de entrenamiento recomendado aparece sola.
    // combine junta las tres fuentes: si cambia cualquiera, se recalcula el estado.
    // El nombre sale del perfil: si se edita en Profile / Edit Profile, el saludo cambia solo.
    val uiState: StateFlow<HomeUiState> =
        combine(
            workoutRepository.observeCatalog(),
            notificationsRepository.allRead,
            profileRepository.profile
        ) { workouts, allRead, profile ->
            buildState(workouts.firstOrNull(), hasUnread = !allRead, userName = profile.fullName())
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = buildState(
                recommended = null,
                hasUnread = !notificationsRepository.allRead.value,
                userName = profileRepository.profile.value.fullName()
            )
        )

    private fun UserProfile.fullName() = "$firstName $lastName".trim()

    private fun buildState(recommended: WorkoutSummary?, hasUnread: Boolean, userName: String) = HomeUiState(
        userName = userName,
        partOfDay = currentPartOfDay(),
        summary = DEMO_SUMMARY,
        recommendedWorkout = recommended,
        hasUnreadNotifications = hasUnread
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
