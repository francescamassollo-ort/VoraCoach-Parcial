package com.example.voracoach.viewmodel

import androidx.lifecycle.ViewModel
import com.example.voracoach.data.repository.ProfileRepository
import com.example.voracoach.model.DietType
import com.example.voracoach.model.FavoriteFood
import com.example.voracoach.model.NutritionPreferences
import com.example.voracoach.model.NutritionPriority
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class NutritionPreferencesUiState(
    val diet: DietType,
    val favoriteFoods: Set<FavoriteFood>,
    val avoidedFoods: List<String>,
    // Texto que se está escribiendo en "Add an ingredient..."
    val newIngredient: String,
    val priorities: Set<NutritionPriority>,
    // "Meal preferences" es un resumen de demostración: en Figma no se edita
    val mealsPerDay: String,
    val preferredBreakfast: String,
    val preferredDinner: String
)

private const val DEMO_MEALS_PER_DAY = "3 meals + 1 snack"
private const val DEMO_BREAKFAST = "Quick & high protein"
private const val DEMO_DINNER = "Balanced"

@HiltViewModel
class NutritionPreferencesViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    // Igual que las otras pantallas de edición: se edita acá y se escribe en el repositorio al guardar.
    private val _uiState = MutableStateFlow(profileRepository.nutrition.value.toUiState())
    val uiState: StateFlow<NutritionPreferencesUiState> = _uiState.asStateFlow()

    fun onDietSelected(diet: DietType) = _uiState.update { it.copy(diet = diet) }

    // Selección múltiple: tocar uno elegido lo quita
    fun onFoodToggle(food: FavoriteFood) = _uiState.update {
        it.copy(favoriteFoods = if (food in it.favoriteFoods) it.favoriteFoods - food else it.favoriteFoods + food)
    }

    fun onPriorityToggle(priority: NutritionPriority) = _uiState.update {
        it.copy(priorities = if (priority in it.priorities) it.priorities - priority else it.priorities + priority)
    }

    fun onIngredientChange(value: String) = _uiState.update { it.copy(newIngredient = value) }

    // Se llama con la tecla "Done" del teclado. Ignora vacíos y repetidos.
    fun onAddIngredient() = _uiState.update { state ->
        val name = state.newIngredient.trim().replaceFirstChar { it.uppercase() }
        val alreadyAdded = state.avoidedFoods.any { it.equals(name, ignoreCase = true) }
        if (name.isEmpty() || alreadyAdded) state.copy(newIngredient = "")
        else state.copy(avoidedFoods = state.avoidedFoods + name, newIngredient = "")
    }

    fun onRemoveIngredient(name: String) = _uiState.update { it.copy(avoidedFoods = it.avoidedFoods - name) }

    fun onSave() {
        val form = _uiState.value
        profileRepository.updateNutrition(
            NutritionPreferences(form.diet, form.favoriteFoods, form.avoidedFoods, form.priorities)
        )
    }

    private fun NutritionPreferences.toUiState() = NutritionPreferencesUiState(
        diet = diet,
        favoriteFoods = favoriteFoods,
        avoidedFoods = avoidedFoods,
        newIngredient = "",
        priorities = priorities,
        mealsPerDay = DEMO_MEALS_PER_DAY,
        preferredBreakfast = DEMO_BREAKFAST,
        preferredDinner = DEMO_DINNER
    )
}