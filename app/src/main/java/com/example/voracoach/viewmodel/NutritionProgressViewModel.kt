package com.example.voracoach.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.voracoach.data.repository.NutritionRepository
import com.example.voracoach.model.Macro
import com.example.voracoach.model.Meal
import com.example.voracoach.model.MealDates
import com.example.voracoach.model.MealIngredient
import com.example.voracoach.model.MealType
import com.example.voracoach.model.NutritionGoals
import com.example.voracoach.model.NutritionProgressData
import com.example.voracoach.model.ProgressPeriod
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt

data class NutritionProgressUiState(
    val period: ProgressPeriod = ProgressPeriod.WEEK,
    val macro: Macro = Macro.PROTEIN,
    val data: NutritionProgressData = EMPTY_DATA,
    // Objetivo diario del macro elegido (kcal para calorías, gramos para el resto)
    val target: Int = NutritionGoals.PROTEIN,
    // Si se cumplió el objetivo cada día de la semana actual, de lunes a domingo
    val dailyHits: List<Boolean> = List(7) { false },
)

private val EMPTY_DATA = NutritionProgressData(
    avgCalories = 0, avgProteinGrams = 0, daysOnTarget = 0, totalDays = 7,
    frequentLunch = "", topProteinSource = "", avgBreakfastKcal = 0, avgDinnerKcal = 0,
)

// Cantidad de días que abarca cada período
private fun ProgressPeriod.days(): Int = when (this) {
    ProgressPeriod.WEEK -> 7
    ProgressPeriod.MONTH -> 30
    ProgressPeriod.THREE_MONTHS -> 90
}

private fun Macro.target(): Int = when (this) {
    Macro.CALORIES -> NutritionGoals.CALORIES
    Macro.PROTEIN -> NutritionGoals.PROTEIN
    Macro.CARBS -> NutritionGoals.CARBS
    Macro.FAT -> NutritionGoals.FAT
}

private fun Meal.amount(macro: Macro): Float = when (macro) {
    Macro.CALORIES -> calories.toFloat()
    Macro.PROTEIN -> protein
    Macro.CARBS -> carbs
    Macro.FAT -> fat
}

/**
 * Criterio de "día en objetivo" para cada macro:
 * - Calorías: entre el 85 % y el 110 % del objetivo (ni muy poco ni de más).
 * - Proteína: al menos el 90 % del objetivo (la idea es llegar).
 * - Carbohidratos y grasas: no pasarse del 110 % del objetivo.
 * Un día sin comidas registradas nunca cuenta como cumplido.
 */
private fun isOnTarget(macro: Macro, total: Float): Boolean {
    if (total <= 0f) return false
    val target = macro.target()
    return when (macro) {
        Macro.CALORIES -> total in target * 0.85f..target * 1.10f
        Macro.PROTEIN -> total >= target * 0.90f
        Macro.CARBS, Macro.FAT -> total <= target * 1.10f
    }
}

// Progress / Nutrition: todo se calcula con las comidas guardadas en Room.
// Al cambiar el período o el macro, o al registrar una comida nueva, la pantalla se actualiza sola.
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class NutritionProgressViewModel @Inject constructor(
    private val repository: NutritionRepository,
) : ViewModel() {

    private val period = MutableStateFlow(ProgressPeriod.WEEK)
    private val macro = MutableStateFlow(Macro.PROTEIN)

    // Comidas e ingredientes del período elegido. flatMapLatest: al cambiar de período se deja
    // de escuchar la consulta anterior y se escucha la del nuevo rango de fechas.
    private val periodMeals = period.flatMapLatest { p ->
        repository.observeMealsBetween(MealDates.daysAgo(p.days() - 1), MealDates.today())
    }
    private val periodIngredients = period.flatMapLatest { p ->
        repository.observeIngredientsBetween(MealDates.daysAgo(p.days() - 1), MealDates.today())
    }

    // Comidas de la semana actual (lunes a domingo) para la fila de días cumplidos
    private val weekMeals = MealDates.currentWeek().let { week ->
        repository.observeMealsBetween(week.first(), week.last())
    }

    val uiState: StateFlow<NutritionProgressUiState> =
        combine(period, macro, periodMeals, periodIngredients, weekMeals) { p, m, meals, ingredients, week ->
            NutritionProgressUiState(
                period = p,
                macro = m,
                data = buildData(p, meals, ingredients),
                target = m.target(),
                dailyHits = MealDates.currentWeek().map { date ->
                    isOnTarget(m, week.filter { it.date == date }.sumOf { it.amount(m).toDouble() }.toFloat())
                },
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = NutritionProgressUiState(),
        )

    init {
        // Si se entra a Progress antes que a Nutrition, también se cargan los datos de ejemplo
        viewModelScope.launch { repository.seedIfEmpty(MealDates.today()) }
    }

    fun onPeriodSelected(newPeriod: ProgressPeriod) {
        period.value = newPeriod
    }

    fun onMacroSelected(newMacro: Macro) {
        macro.value = newMacro
    }

    private fun buildData(
        period: ProgressPeriod,
        meals: List<Meal>,
        ingredients: List<MealIngredient>,
    ): NutritionProgressData {
        // Totales por día: los promedios se sacan sobre los días con al menos una comida
        val byDay = meals.groupBy { it.date }
        val loggedDays = byDay.size.coerceAtLeast(1)
        val dailyCalories = byDay.values.map { day -> day.sumOf { it.calories } }
        val dailyProtein = byDay.values.map { day -> day.sumOf { it.protein.toDouble() } }

        // Almuerzo más repetido y fuente de proteína (el ingrediente que más proteína aportó)
        val frequentLunch = meals.filter { it.type == MealType.LUNCH }
            .groupingBy { it.name }.eachCount()
            .maxByOrNull { it.value }?.key.orEmpty()
        val topProteinSource = ingredients
            .groupBy { it.name }
            .mapValues { (_, items) -> items.sumOf { it.protein.toDouble() } }
            .maxByOrNull { it.value }?.key.orEmpty()

        return NutritionProgressData(
            avgCalories = dailyCalories.sum() / loggedDays,
            avgProteinGrams = (dailyProtein.sum() / loggedDays).roundToInt(),
            daysOnTarget = dailyCalories.count { isOnTarget(Macro.CALORIES, it.toFloat()) },
            totalDays = period.days(),
            frequentLunch = frequentLunch,
            topProteinSource = topProteinSource,
            avgBreakfastKcal = meals.averageCaloriesOf(MealType.BREAKFAST),
            avgDinnerKcal = meals.averageCaloriesOf(MealType.DINNER),
        )
    }

    private fun List<Meal>.averageCaloriesOf(type: MealType): Int {
        val ofType = filter { it.type == type }
        return if (ofType.isEmpty()) 0 else ofType.sumOf { it.calories } / ofType.size
    }
}
