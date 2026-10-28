package com.example.voracoach.data.repository

import com.example.voracoach.model.CoachingStyles
import com.example.voracoach.model.DietType
import com.example.voracoach.model.FavoriteFood
import com.example.voracoach.model.FitnessGoal
import com.example.voracoach.model.Gender
import com.example.voracoach.model.GoalsPreferences
import com.example.voracoach.model.NutritionPreferences
import com.example.voracoach.model.NutritionPriority
import com.example.voracoach.model.TrainingFrequency
import com.example.voracoach.model.UserProfile
import com.example.voracoach.model.WorkoutType
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

private val DEMO_PREFERENCES = GoalsPreferences(
    goals = setOf(FitnessGoal.BUILD_STRENGTH, FitnessGoal.IMPROVE_FITNESS),
    frequency = TrainingFrequency.FOUR,
    workoutType = WorkoutType.STRENGTH,
    coachingStyles = CoachingStyles.BALANCED
)

private val DEMO_NUTRITION = NutritionPreferences(
    diet = DietType.EVERYTHING,
    favoriteFoods = FavoriteFood.entries.toSet(),
    avoidedFoods = listOf("Peanuts", "Shellfish", "Mushrooms"),
    priorities = NutritionPriority.entries.toSet()
)

// Guarda el perfil y las preferencias en memoria (sin cuentas reales). Es singleton: las pantallas
// de edición lo modifican y el Dashboard de Profile lo observa, así se actualiza solo al guardar.
@Singleton
class ProfileRepository @Inject constructor() {

    private val _profile = MutableStateFlow(DEMO_USER)
    val profile: StateFlow<UserProfile> = _profile.asStateFlow()

    private val _preferences = MutableStateFlow(DEMO_PREFERENCES)
    val preferences: StateFlow<GoalsPreferences> = _preferences.asStateFlow()

    private val _nutrition = MutableStateFlow(DEMO_NUTRITION)
    val nutrition: StateFlow<NutritionPreferences> = _nutrition.asStateFlow()

    fun update(profile: UserProfile) {
        _profile.value = profile
    }

    fun updatePreferences(preferences: GoalsPreferences) {
        _preferences.value = preferences
    }

    fun updateNutrition(nutrition: NutritionPreferences) {
        _nutrition.value = nutrition
    }
}