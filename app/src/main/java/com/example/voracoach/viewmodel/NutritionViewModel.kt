package com.example.voracoach.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.voracoach.data.repository.NutritionRepository
import com.example.voracoach.model.Meal
import com.example.voracoach.model.MealType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

// Todo lo que muestran Dashboard y TodayMeals: las comidas del día y los totales ya calculados.
// Las pantallas no suman nada, solo dibujan este estado.
data class NutritionUiState(
    val meals: List<Meal> = emptyList(),
    val totalCalories: Int = 0,
    val totalProtein: Int = 0,
    val totalCarbs: Int = 0,
    val totalFat: Int = 0,
    val calorieGoal: Int = CALORIE_GOAL,
    val proteinGoal: Int = PROTEIN_GOAL,
    val carbsGoal: Int = CARBS_GOAL,
    val fatGoal: Int = FAT_GOAL,
) {
    // Porcentaje del objetivo de calorías, limitado a 0..100 para la barra de progreso
    val caloriePercent: Int
        get() = if (calorieGoal > 0) (totalCalories * 100 / calorieGoal).coerceIn(0, 100) else 0
}

// Objetivos diarios de demostración (los mismos que mostraba la pantalla)
private const val CALORIE_GOAL = 2_100
private const val PROTEIN_GOAL = 140
private const val CARBS_GOAL = 220
private const val FAT_GOAL = 65

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class NutritionViewModel @Inject constructor(
    private val repository: NutritionRepository,
) : ViewModel() {

    private val _currentDate = MutableStateFlow(getTodayDate())
    val currentDate: StateFlow<String> = _currentDate.asStateFlow()

    val meals: StateFlow<List<Meal>> = _currentDate
        .flatMapLatest { date -> repository.getMealsByDate(date) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    // Estado completo para la UI: cada vez que Room avisa un cambio en las comidas,
    // se recalculan los totales acá y la pantalla se redibuja sola.
    val uiState: StateFlow<NutritionUiState> = meals
        .map { meals ->
            NutritionUiState(
                meals = meals,
                totalCalories = meals.sumOf { it.calories },
                totalProtein = meals.sumOf { it.protein.toDouble() }.toInt(),
                totalCarbs = meals.sumOf { it.carbs.toDouble() }.toInt(),
                totalFat = meals.sumOf { it.fat.toDouble() }.toInt(),
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = NutritionUiState()
        )

    init {
        // Carga las comidas de ejemplo solo si la base está vacía
        viewModelScope.launch { repository.seedIfEmpty(_currentDate.value) }
    }

    fun addMeal(
        name: String,
        type: MealType,
        calories: Int,
        protein: Float,
        carbs: Float,
        fat: Float
    ) {
        val meal = Meal(
            id = 0,
            name = name,
            date = _currentDate.value,
            type = type,
            calories = calories,
            protein = protein,
            carbs = carbs,
            fat = fat
        )

        viewModelScope.launch {
            repository.insertMeal(meal)
        }
    }

    fun removeMeal(meal: Meal) {
        viewModelScope.launch {
            repository.deleteMeal(meal)
        }
    }

    fun setDate(date: String) {
        _currentDate.value = date
    }

    private fun getTodayDate(): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
}
