package com.example.voracoach.viewmodel

import androidx.lifecycle.ViewModel
import com.example.voracoach.data.repository.ProfileRepository
import com.example.voracoach.model.CoachingStyle
import com.example.voracoach.model.FitnessGoal
import com.example.voracoach.model.GoalsPreferences
import com.example.voracoach.model.TrainingFrequency
import com.example.voracoach.model.WorkoutType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class GoalsUiState(
    val goals: Set<FitnessGoal>,
    val frequency: TrainingFrequency,
    val workoutType: WorkoutType,
    val coachingStyle: CoachingStyle
) {
    // Tiene que haber al menos un objetivo elegido para guardar
    val canSave: Boolean get() = goals.isNotEmpty()
}

@HiltViewModel
class GoalsViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    // Igual que Edit Profile: se edita acá y recién se escribe en el repositorio al guardar.
    private val _uiState = MutableStateFlow(profileRepository.preferences.value.toUiState())
    val uiState: StateFlow<GoalsUiState> = _uiState.asStateFlow()

    // Los objetivos son de selección múltiple: tocar uno elegido lo quita
    fun onGoalToggle(goal: FitnessGoal) = _uiState.update {
        it.copy(goals = if (goal in it.goals) it.goals - goal else it.goals + goal)
    }

    fun onFrequencySelected(frequency: TrainingFrequency) = _uiState.update { it.copy(frequency = frequency) }

    fun onWorkoutTypeSelected(type: WorkoutType) = _uiState.update { it.copy(workoutType = type) }

    fun onCoachingStyleSelected(style: CoachingStyle) = _uiState.update { it.copy(coachingStyle = style) }

    fun onSave() {
        val form = _uiState.value
        if (!form.canSave) return
        profileRepository.updatePreferences(
            GoalsPreferences(form.goals, form.frequency, form.workoutType, form.coachingStyle)
        )
    }

    private fun GoalsPreferences.toUiState() = GoalsUiState(goals, frequency, workoutType, coachingStyle)
}