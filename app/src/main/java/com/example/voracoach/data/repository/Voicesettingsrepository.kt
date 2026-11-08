package com.example.voracoach.data.repository

import com.example.voracoach.model.VoiceSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

// Preferencias de voz e IA compartidas entre pantallas (Voice & AI Settings las cambia y
// Coach las podrá leer). @Singleton: una sola instancia para toda la app.
// Vive en memoria, igual que NotificationsRepository: al cerrar la app vuelven a los valores de Figma.
@Singleton
class VoiceSettingsRepository @Inject constructor() {

    private val _settings = MutableStateFlow(VoiceSettings())
    val settings: StateFlow<VoiceSettings> = _settings.asStateFlow()

    fun update(settings: VoiceSettings) {
        _settings.value = settings
    }
}