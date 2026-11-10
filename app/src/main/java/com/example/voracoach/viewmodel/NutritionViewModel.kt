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
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

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
