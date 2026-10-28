package com.example.voracoach.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.voracoach.data.repository.ProfileRepository
import com.example.voracoach.model.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

// Datos del perfil y su resumen ("My snapshot"). Son de demostración, como en Home:
// la app no tiene cuentas reales.
data class ProfileUiState(
    val userName: String,
    val tagline: String,
    val goal: String,
    val experience: String,
    val trainingDaysPerWeek: Int,
    val preferredWorkout: String,
    // Decide si la fila "Connected Data" muestra el badge "Synced"
    val dataSynced: Boolean
)

private val DEMO_PROFILE = ProfileUiState(
    userName = "",
    tagline = "Building strength & improving fitness",
    goal = "Build strength & improve fitness",
    experience = "Intermediate",
    trainingDaysPerWeek = 4,
    preferredWorkout = "Strength training",
    dataSynced = true
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    profileRepository: ProfileRepository
) : ViewModel() {

    // El nombre sale del repositorio: si Edit Profile lo cambia, el Dashboard se recalcula.
    val uiState: StateFlow<ProfileUiState> = profileRepository.profile
        .map { buildState(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = buildState(profileRepository.profile.value)
        )

    private fun buildState(profile: UserProfile) =
        DEMO_PROFILE.copy(userName = "${profile.firstName} ${profile.lastName}")
}