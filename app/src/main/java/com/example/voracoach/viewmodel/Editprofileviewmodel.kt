package com.example.voracoach.viewmodel


import androidx.lifecycle.ViewModel
import com.example.voracoach.data.repository.ProfileRepository
import com.example.voracoach.model.Gender
import com.example.voracoach.model.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class EditProfileUiState(
    val firstName: String,
    val lastName: String,
    val birthDate: String,
    val heightCm: String,
    val weightKg: String,
    val gender: Gender
) {
    // "Save changes" se habilita solo si hay nombre y apellido
    val canSave: Boolean get() = firstName.isNotBlank() && lastName.isNotBlank()
}

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    // El formulario se edita acá y recién se escribe en el repositorio al guardar:
    // si el usuario sale con la flecha atrás, no se pierde el perfil original.
    private val _uiState = MutableStateFlow(profileRepository.profile.value.toUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    fun onFirstNameChange(value: String) = _uiState.update { it.copy(firstName = value) }

    fun onLastNameChange(value: String) = _uiState.update { it.copy(lastName = value) }

    fun onBirthDateChange(value: String) = _uiState.update { it.copy(birthDate = value) }

    // Altura y peso: solo dígitos, hasta 3
    fun onHeightChange(value: String) = _uiState.update { it.copy(heightCm = value.onlyDigits()) }

    fun onWeightChange(value: String) = _uiState.update { it.copy(weightKg = value.onlyDigits()) }

    fun onGenderSelected(gender: Gender) = _uiState.update { it.copy(gender = gender) }

    fun onSave() {
        val form = _uiState.value
        if (!form.canSave) return
        profileRepository.update(
            UserProfile(
                firstName = form.firstName.trim(),
                lastName = form.lastName.trim(),
                birthDate = form.birthDate.trim(),
                heightCm = form.heightCm,
                weightKg = form.weightKg,
                gender = form.gender
            )
        )
    }

    private fun String.onlyDigits() = filter(Char::isDigit).take(3)

    private fun UserProfile.toUiState() = EditProfileUiState(
        firstName = firstName,
        lastName = lastName,
        birthDate = birthDate,
        heightCm = heightCm,
        weightKg = weightKg,
        gender = gender
    )
}