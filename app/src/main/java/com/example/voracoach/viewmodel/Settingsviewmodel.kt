package com.example.voracoach.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

// Datos de la cuenta que muestra Settings. Sin Firebase se usa el perfil de demostración
// (igual que Home y Profile).
data class SettingsUiState(
    val userName: String,
    val email: String
)

private val DEMO_SETTINGS = SettingsUiState(userName = "Alex Morgan", email = "alex@morgan.com")

@HiltViewModel
class SettingsViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(DEMO_SETTINGS)
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()
}