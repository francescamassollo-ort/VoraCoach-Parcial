package com.example.voracoach.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.voracoach.data.repository.NutritionRepository
import com.example.voracoach.model.Meal
import com.example.voracoach.model.MealIngredient
import com.example.voracoach.model.MealType
import com.example.voracoach.model.SavedMeal
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
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

// Comida armada en Review Meal (FoodConfirmation), lista para guardar o editar
data class MealDraft(
    val name: String,
    val type: MealType,
    val calories: Int,
    val protein: Float,
    val carbs: Float,
    val fat: Float,
    val ingredients: List<MealIngredient>,
)

// Estado del detalle de una comida: la comida, sus ingredientes y cuánta proteína falta
// para el objetivo del día (para el aviso de VORA)
data class MealDetailsUiState(
    val isLoading: Boolean = true,
    val meal: Meal? = null,
    val ingredients: List<MealIngredient> = emptyList(),
    val calorieGoal: Int = CALORIE_GOAL,
    val proteinRemaining: Int = 0,
)

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

    // Texto del buscador de comidas guardadas. Cada vez que cambia, flatMapLatest cancela
    // la búsqueda anterior y escucha la nueva consulta de Room.
    private val _savedMealsQuery = MutableStateFlow("")
    val savedMealsQuery: StateFlow<String> = _savedMealsQuery.asStateFlow()

    val savedMeals: StateFlow<List<SavedMeal>> = _savedMealsQuery
        .flatMapLatest { query -> repository.searchSavedMeals(query) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    init {
        // Carga las comidas de ejemplo (del día y guardadas) solo si las tablas están vacías
        viewModelScope.launch {
            repository.seedIfEmpty(_currentDate.value)
            repository.seedSavedMealsIfEmpty()
        }
    }

    // Registra una comida nueva (con sus ingredientes) en el día actual, con la hora de ahora
    fun addMeal(draft: MealDraft) {
        val meal = Meal(
            name = draft.name,
            date = _currentDate.value,
            time = currentTime(),
            type = draft.type,
            calories = draft.calories,
            protein = draft.protein,
            carbs = draft.carbs,
            fat = draft.fat,
        )
        viewModelScope.launch { repository.insertMeal(meal, draft.ingredients) }
    }

    // Edita una comida existente: mantiene su fecha y hora, cambia todo lo demás
    fun updateMeal(mealId: Int, draft: MealDraft) {
        viewModelScope.launch {
            val original = repository.getMeal(mealId) ?: return@launch
            val updated = original.copy(
                name = draft.name,
                type = draft.type,
                calories = draft.calories,
                protein = draft.protein,
                carbs = draft.carbs,
                fat = draft.fat,
            )
            repository.updateMeal(updated, draft.ingredients)
        }
    }

    // "Duplicate Meal": copia la comida y sus ingredientes al día de hoy, con la hora actual
    fun duplicateMeal(mealId: Int) {
        viewModelScope.launch { repository.duplicateMeal(mealId, getTodayDate(), currentTime()) }
    }

    // Detalle de una comida: combina la comida, sus ingredientes y los totales del día.
    // Si se edita la comida o se agrega otra, el detalle se actualiza solo.
    fun observeMealDetails(mealId: Int): Flow<MealDetailsUiState> =
        combine(
            repository.observeMeal(mealId),
            repository.observeIngredients(mealId),
            uiState,
        ) { meal, ingredients, day ->
            MealDetailsUiState(
                isLoading = false,
                meal = meal,
                ingredients = ingredients,
                calorieGoal = day.calorieGoal,
                proteinRemaining = (day.proteinGoal - day.totalProtein).coerceAtLeast(0),
            )
        }

    // Para "Edit Meal": la comida y sus ingredientes, leídos una vez
    suspend fun getMealForEdit(mealId: Int): Pair<Meal, List<MealIngredient>>? {
        val meal = repository.getMeal(mealId) ?: return null
        return meal to repository.getIngredients(mealId)
    }

    fun removeMeal(meal: Meal) {
        viewModelScope.launch {
            repository.deleteMeal(meal)
        }
    }

    fun onSavedMealsQueryChange(query: String) {
        _savedMealsQuery.value = query
    }

    // "Save without logging": guarda la comida como plantilla, sin sumarla al día
    fun saveWithoutLogging(draft: MealDraft) {
        val saved = SavedMeal(
            name = draft.name,
            calories = draft.calories,
            protein = draft.protein,
            carbs = draft.carbs,
            fat = draft.fat,
        )
        viewModelScope.launch { repository.saveMeal(saved) }
    }

    // Registra una comida guardada en el día actual, con el horario que corresponda
    fun logSavedMeal(saved: SavedMeal, type: MealType = MealType.forCurrentTime()) {
        addMeal(
            MealDraft(saved.name, type, saved.calories, saved.protein, saved.carbs, saved.fat, ingredients = emptyList())
        )
    }

    fun deleteSavedMeal(saved: SavedMeal) {
        viewModelScope.launch { repository.deleteSavedMeal(saved) }
    }

    fun setDate(date: String) {
        _currentDate.value = date
    }

    private fun getTodayDate(): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

    private fun currentTime(): String =
        SimpleDateFormat("HH:mm", Locale.US).format(Date())
}
