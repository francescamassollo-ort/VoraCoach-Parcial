package com.example.voracoach.data.repository

import com.example.voracoach.model.Gender
import com.example.voracoach.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

private val DEMO_USER = UserProfile(
    firstName = "Alex",
    lastName = "Morgan",
    birthDate = "14 March 1995",
    heightCm = "178",
    weightKg = "76",
    gender = Gender.PREFER_NOT_TO_SAY
)

// Guarda el perfil en memoria (sin cuentas reales). Es singleton: Edit Profile lo modifica y
// el Dashboard de Profile lo observa, así el nombre se actualiza solo al guardar.
@Singleton
class ProfileRepository @Inject constructor() {

    private val _profile = MutableStateFlow(DEMO_USER)
    val profile: StateFlow<UserProfile> = _profile.asStateFlow()

    fun update(profile: UserProfile) {
        _profile.value = profile
    }
}