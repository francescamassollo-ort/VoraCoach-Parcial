package com.example.voracoach.data.repository

import com.example.voracoach.data.local.MealDao
import com.example.voracoach.data.local.entity.toEntity
import com.example.voracoach.data.local.entity.toModel
import com.example.voracoach.model.Meal
import com.example.voracoach.model.MealType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NutritionRepository @Inject constructor(
    private val mealDao: MealDao,
) {

    fun getMealsByDate(date: String): Flow<List<Meal>> =
        mealDao.getMealsByDate(date).map { entities ->
            entities.map { it.toModel() }
        }

    suspend fun insertMeal(meal: Meal) {
        mealDao.insertMeal(meal.toEntity())
    }

    suspend fun deleteMeal(meal: Meal) {
        mealDao.deleteMeal(meal.toEntity())
    }

    // La primera vez que se abre la app la tabla está vacía: se cargan comidas de ejemplo
    // para la fecha dada, así el Dashboard no arranca en cero (igual que el catálogo de Workout).
    suspend fun seedIfEmpty(date: String) {
        if (mealDao.countMeals() > 0) return
        demoMeals(date).forEach { mealDao.insertMeal(it.toEntity()) }
    }

    private fun demoMeals(date: String) = listOf(
        Meal(name = "Oatmeal with banana", date = date, type = MealType.BREAKFAST,
            calories = 380, protein = 14f, carbs = 62f, fat = 8f),
        Meal(name = "Grilled chicken salad", date = date, type = MealType.LUNCH,
            calories = 520, protein = 42f, carbs = 30f, fat = 22f),
        Meal(name = "Greek yogurt with nuts", date = date, type = MealType.AFTERNOON_SNACK,
            calories = 260, protein = 18f, carbs = 16f, fat = 14f),
    )
}
