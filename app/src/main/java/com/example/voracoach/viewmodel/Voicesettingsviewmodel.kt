package com.example.voracoach.viewmodel

import androidx.lifecycle.ViewModel
import com.example.voracoach.data.repository.VoiceSettingsRepository
import com.example.voracoach.model.VoiceSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class VoiceSettingsViewModel @Inject constructor(
    private val repository: VoiceSettingsRepository
) : ViewModel() {

    val settings: StateFlow<VoiceSettings> = repository.settings

    fun onSettingsChange(settings: VoiceSettings) = repository.update(settings)
}