package com.example.voracoach.viewmodel

import androidx.lifecycle.ViewModel
import com.example.voracoach.model.Macro
import com.example.voracoach.model.NutritionProgressData
import com.example.voracoach.model.ProgressPeriod
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class NutritionProgressUiState(
    val period: ProgressPeriod = ProgressPeriod.WEEK,
    val macro: Macro = Macro.PROTEIN,
    val data: NutritionProgressData = DEMO_NUTRITION_PROGRESS.getValue(ProgressPeriod.WEEK),
    // Objetivo diario del macro elegido (kcal para calorías, gramos para el resto)
    val target: Int = DEMO_TARGETS.getValue(Macro.PROTEIN),
    // Si se cumplió el objetivo cada día de la última semana, de lunes a domingo
    val dailyHits: List<Boolean> = DEMO_DAILY_HITS.getValue(Macro.PROTEIN)
)

private val DEMO_TARGETS = mapOf(
    Macro.CALORIES to 2_100,
    Macro.PROTEIN to 140,
    Macro.CARBS to 230,
    Macro.FAT to 70
)

private val DEMO_DAILY_HITS = mapOf(
    Macro.CALORIES to listOf(true, true, false, true, true, true, false),
    Macro.PROTEIN to listOf(true, true, false, true, true, false, true),
    Macro.CARBS to listOf(true, false, true, true, false, true, true),
    Macro.FAT to listOf(true, true, true, false, true, true, true)
)

private val DEMO_NUTRITION_PROGRESS = mapOf(
    ProgressPeriod.WEEK to NutritionProgressData(
        avgCalories = 1_980, avgProteinGrams = 132, daysOnTarget = 5, totalDays = 7,
        frequentLunch = "Chicken & Rice Bowl", topProteinSource = "Whey & Chicken Breast",
        avgBreakfastKcal = 420, avgDinnerKcal = 610
    ),
    ProgressPeriod.MONTH to NutritionProgressData(
        avgCalories = 1_940, avgProteinGrams = 126, daysOnTarget = 21, totalDays = 30,
        frequentLunch = "Chicken & Rice Bowl", topProteinSource = "Chicken Breast",
        avgBreakfastKcal = 410, avgDinnerKcal = 630
    ),
    ProgressPeriod.THREE_MONTHS to NutritionProgressData(
        avgCalories = 1_910, avgProteinGrams = 118, daysOnTarget = 58, totalDays = 90,
        frequentLunch = "Tuna Salad", topProteinSource = "Chicken Breast",
        avgBreakfastKcal = 400, avgDinnerKcal = 650
    )
)

@HiltViewModel
class NutritionProgressViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(NutritionProgressUiState())
    val uiState: StateFlow<NutritionProgressUiState> = _uiState.asStateFlow()

    fun onPeriodSelected(period: ProgressPeriod) {
        _uiState.update { it.copy(period = period, data = DEMO_NUTRITION_PROGRESS.getValue(period)) }
    }

    fun onMacroSelected(macro: Macro) {
        _uiState.update {
            it.copy(macro = macro, target = DEMO_TARGETS.getValue(macro), dailyHits = DEMO_DAILY_HITS.getValue(macro))
        }
    }
}
