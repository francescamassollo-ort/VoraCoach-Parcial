package com.example.voracoach.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.voracoach.data.repository.ProfileRepository
import com.example.voracoach.model.FitnessGoal
import com.example.voracoach.model.GoalsPreferences
import com.example.voracoach.model.UserProfile
import com.example.voracoach.model.WorkoutType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

// Datos del perfil y su resumen ("My snapshot"). Nombre, objetivos, frecuencia y tipo de workout
// salen del repositorio; el resto es de demostración, como en Home.
// Los objetivos y el workout viajan como enums: la pantalla elige el texto (strings.xml).
data class ProfileUiState(
    val userName: String,
    val tagline: String,
    val goals: List<FitnessGoal>,
    val experience: String,
    val trainingDaysPerWeek: Int,
    val preferredWorkout: WorkoutType,
    // Decide si la fila "Connected Data" muestra el badge "Synced"
    val dataSynced: Boolean
)

private const val DEMO_TAGLINE = "Building strength & improving fitness"
private const val DEMO_EXPERIENCE = "Intermediate"

@HiltViewModel
class ProfileViewModel @Inject constructor(
    profileRepository: ProfileRepository
) : ViewModel() {

    // combine: si Edit Profile o Goals guardan cambios, el Dashboard se recalcula solo.
    val uiState: StateFlow<ProfileUiState> =
        combine(profileRepository.profile, profileRepository.preferences) { profile, preferences ->
            buildState(profile, preferences)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = buildState(profileRepository.profile.value, profileRepository.preferences.value)
        )

    private fun buildState(profile: UserProfile, preferences: GoalsPreferences) = ProfileUiState(
        userName = "${profile.firstName} ${profile.lastName}",
        tagline = DEMO_TAGLINE,
        goals = preferences.goals.sortedBy { it.ordinal },
        experience = DEMO_EXPERIENCE,
        trainingDaysPerWeek = preferences.frequency.days,
        preferredWorkout = preferences.workoutType,
        dataSynced = true
    )
}